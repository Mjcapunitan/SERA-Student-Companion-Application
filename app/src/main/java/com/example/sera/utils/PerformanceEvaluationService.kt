package com.example.sera.utils

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceEvaluationService @Inject constructor() {
    private val TAG = "PerformanceEvaluationService"
    private lateinit var model: GenerativeModel
    private var cancellationRequested = false

    fun initialize(apiKey: String) {
        // Configure with slightly higher temperature for more creative feedback
        val configBuilder = GenerationConfig.Builder()
        configBuilder.temperature = 0.8f
        val generationConfig = configBuilder.build()

        // Initialize the model with the config
        model = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = apiKey,
            generationConfig = generationConfig
        )
    }

    fun cancelOngoingOperations() {
        Log.d(TAG, "Cancellation requested for ongoing assessment generation")
        cancellationRequested = true
    }

    private fun resetCancellationFlag() {
        cancellationRequested = false
    }

    private fun checkCancellation() {
        if (cancellationRequested) {
            Log.d(TAG, "Assessment generation cancelled as requested")
            throw CancellationException("Performance assessment cancelled by user")
        }
    }

    suspend fun generatePerformanceAssessment(
        quizAttempts: List<QuizAttempt>
    ): Result<PerformanceAssessment> {
        return withContext(Dispatchers.IO) {
            resetCancellationFlag()

            try {
                checkCancellation()

                if (quizAttempts.isEmpty()) {
                    return@withContext Result.failure(Exception("No quiz attempts available for assessment"))
                }

                val prompt = buildAssessmentPrompt(quizAttempts)
                Log.d(TAG, "Sending assessment prompt to Gemini API")

                checkCancellation()

                val response = model.generateContent(prompt)
                val responseText = response.text
                if (responseText != null) {
                    Log.d(TAG, "Received assessment response: ${responseText.take(100)}...")
                }

                checkCancellation()

                // Parse the JSON response
                val assessment = responseText?.let { parseAssessmentFromJson(it) }

                if (assessment == null) {
                    Log.e(TAG, "Failed to parse assessment response")
                    return@withContext Result.failure(Exception("Failed to parse assessment from response"))
                }

                Result.success(assessment)
            } catch (e: CancellationException) {
                Log.d(TAG, "Assessment generation was cancelled")
                Result.failure(e)
            } catch (e: Exception) {
                Log.e(TAG, "Error generating assessment: ${e.message}", e)

                // Check if this is a quota exceeded error
                if (e.message?.contains("quota", ignoreCase = true) == true ||
                    e.message?.contains("rate limit", ignoreCase = true) == true ||
                    e.message?.contains("limit exceeded", ignoreCase = true) == true ||
                    e.message?.contains("reached", ignoreCase = true) == true) {
                    return@withContext Result.failure(QuotaExceededException("Daily quota exceeded"))
                }

                Result.failure(e)
            } finally {
                resetCancellationFlag()
            }
        }
    }

    private fun buildAssessmentPrompt(quizAttempts: List<QuizAttempt>): String {
        val quizData = buildQuizDataJson(quizAttempts)

        return """
        Analyze the following quiz history data and provide a comprehensive performance assessment. 
        The data represents a user's quiz attempts with details about each question answered.
        
        USER'S QUIZ HISTORY DATA:
        $quizData
        
        Based on this data, provide the following assessment in JSON format:
        1. Overall strengths and weaknesses
        2. Performance by quiz type (Multiple Choice, True/False, Fill in the Blanks, etc.)
        3. Performance by topic (based on quiz titles)
        4. Specific improvement areas
        5. Learning recommendations
        
        Format your response as valid JSON with the following structure:
        {
          "overviewSummary": "Brief 1-2 sentence summary of overall performance",
          "strengthsAndWeaknesses": {
            "strengths": ["Strength 1", "Strength 2", ...],
            "weaknesses": ["Weakness 1", "Weakness 2", ...]
          },
          "performanceByQuizType": [
            {
              "type": "Multiple Choice",
              "correctPercentage": 85,
              "assessment": "Performance assessment for this quiz type",
              "improvementTips": ["Tip 1", "Tip 2"]
            },
            ...
          ],
          "performanceByTopic": [
            {
              "topic": "Topic name extracted from quiz titles",
              "correctPercentage": 75,
              "assessment": "Performance assessment for this topic",
              "improvementTips": ["Tip 1", "Tip 2"]
            },
            ...
          ],
          "improvementAreas": [
            {
              "area": "Specific area to improve",
              "description": "Detailed description of the improvement area",
              "actionableSteps": ["Step 1", "Step 2", ...]
            },
            ...
          ],
          "learningRecommendations": [
            "Recommendation 1",
            "Recommendation 2",
            ...
          ]
        }
        
        Return ONLY valid JSON with no additional text or explanations.
        The JSON should be properly formatted and parseable.
        Do not include markdown code blocks or any other formatting - just the raw JSON.
        Be specific and actionable in your assessments and recommendations.
        Base your analysis on patterns in the user's performance data.
        """.trimIndent()
    }

    private fun buildQuizDataJson(quizAttempts: List<QuizAttempt>): String {
        val jsonArray = JSONArray()

        quizAttempts.forEach { attempt ->
            val attemptJson = JSONObject()
            attemptJson.put("quizTitle", attempt.quizTitle)
            attemptJson.put("totalQuestions", attempt.totalQuestions)
            attemptJson.put("correctAnswers", attempt.correctAnswers)
            attemptJson.put("completionTimeSeconds", attempt.completionTimeSeconds)
            attemptJson.put("timeoutExpired", attempt.timeoutExpired)

            // Add question results
            val questionsArray = JSONArray()
            attempt.questionResults.forEach { result ->
                val questionJson = JSONObject()
                questionJson.put("questionText", result.questionText)
                questionJson.put("questionType", result.questionType)
                questionJson.put("correctAnswer", result.correctAnswer)
                questionJson.put("userAnswer", result.userAnswer ?: "No answer")
                questionJson.put("isCorrect", result.isCorrect)
                questionJson.put("timeTakenSeconds", result.timeTakenSeconds ?: 0)
                questionsArray.put(questionJson)
            }
            attemptJson.put("questionResults", questionsArray)

            jsonArray.put(attemptJson)
        }

        return jsonArray.toString()
    }

    private fun parseAssessmentFromJson(responseText: String): PerformanceAssessment? {
        try {
            // Clean the response
            val cleanedResponse = cleanJsonResponse(responseText)

            // Parse the JSON
            val jsonObject = JSONObject(cleanedResponse)

            // Parse strengths and weaknesses
            val strengthsAndWeaknessesObj = jsonObject.getJSONObject("strengthsAndWeaknesses")
            val strengthsArray = strengthsAndWeaknessesObj.getJSONArray("strengths")
            val weaknessesArray = strengthsAndWeaknessesObj.getJSONArray("weaknesses")

            val strengths = mutableListOf<String>()
            val weaknesses = mutableListOf<String>()

            for (i in 0 until strengthsArray.length()) {
                strengths.add(strengthsArray.getString(i))
            }

            for (i in 0 until weaknessesArray.length()) {
                weaknesses.add(weaknessesArray.getString(i))
            }

            // Parse performance by quiz type
            val quizTypeArray = jsonObject.getJSONArray("performanceByQuizType")
            val performanceByQuizType = mutableListOf<QuizTypePerformance>()

            for (i in 0 until quizTypeArray.length()) {
                val typeObj = quizTypeArray.getJSONObject(i)
                val type = typeObj.getString("type")
                val percentage = typeObj.getDouble("correctPercentage")
                val assessment = typeObj.getString("assessment")

                val tipsArray = typeObj.getJSONArray("improvementTips")
                val tips = mutableListOf<String>()
                for (j in 0 until tipsArray.length()) {
                    tips.add(tipsArray.getString(j))
                }

                performanceByQuizType.add(
                    QuizTypePerformance(type, percentage, assessment, tips)
                )
            }

            // Parse performance by topic
            val topicArray = jsonObject.getJSONArray("performanceByTopic")
            val performanceByTopic = mutableListOf<TopicPerformance>()

            for (i in 0 until topicArray.length()) {
                val topicObj = topicArray.getJSONObject(i)
                val topic = topicObj.getString("topic")
                val percentage = topicObj.getDouble("correctPercentage")
                val assessment = topicObj.getString("assessment")

                val tipsArray = topicObj.getJSONArray("improvementTips")
                val tips = mutableListOf<String>()
                for (j in 0 until tipsArray.length()) {
                    tips.add(tipsArray.getString(j))
                }

                performanceByTopic.add(
                    TopicPerformance(topic, percentage, assessment, tips)
                )
            }

            // Parse improvement areas
            val areasArray = jsonObject.getJSONArray("improvementAreas")
            val improvementAreas = mutableListOf<ImprovementArea>()

            for (i in 0 until areasArray.length()) {
                val areaObj = areasArray.getJSONObject(i)
                val area = areaObj.getString("area")
                val description = areaObj.getString("description")

                val stepsArray = areaObj.getJSONArray("actionableSteps")
                val steps = mutableListOf<String>()
                for (j in 0 until stepsArray.length()) {
                    steps.add(stepsArray.getString(j))
                }

                improvementAreas.add(
                    ImprovementArea(area, description, steps)
                )
            }

            // Parse learning recommendations
            val recommendationsArray = jsonObject.getJSONArray("learningRecommendations")
            val recommendations = mutableListOf<String>()

            for (i in 0 until recommendationsArray.length()) {
                recommendations.add(recommendationsArray.getString(i))
            }

            return PerformanceAssessment(
                overviewSummary = jsonObject.getString("overviewSummary"),
                strengthsAndWeaknesses = StrengthsAndWeaknesses(strengths, weaknesses),
                performanceByQuizType = performanceByQuizType,
                performanceByTopic = performanceByTopic,
                improvementAreas = improvementAreas,
                learningRecommendations = recommendations
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing assessment JSON: ${e.message}", e)
            return null
        }
    }

    private fun cleanJsonResponse(response: String): String {
        checkCancellation()

        Log.d(TAG, "Cleaning JSON response")

        // Remove any markdown code blocks and extra text
        var cleaned = response
            .replace("```json", "")
            .replace("```", "")
            .trim()

        // Find the first { and last }
        val startIndex = cleaned.indexOf("{")
        val endIndex = cleaned.lastIndexOf("}")

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            cleaned = cleaned.substring(startIndex, endIndex + 1)
        } else {
            Log.e(TAG, "Could not find proper JSON object brackets")
        }

        return cleaned
    }
}

// Data classes for the assessment results
data class PerformanceAssessment(
    val overviewSummary: String,
    val strengthsAndWeaknesses: StrengthsAndWeaknesses,
    val performanceByQuizType: List<QuizTypePerformance>,
    val performanceByTopic: List<TopicPerformance>,
    val improvementAreas: List<ImprovementArea>,
    val learningRecommendations: List<String>
)

data class StrengthsAndWeaknesses(
    val strengths: List<String>,
    val weaknesses: List<String>
)

data class QuizTypePerformance(
    val type: String,
    val correctPercentage: Double,
    val assessment: String,
    val improvementTips: List<String>
)

data class TopicPerformance(
    val topic: String,
    val correctPercentage: Double,
    val assessment: String,
    val improvementTips: List<String>
)

data class ImprovementArea(
    val area: String,
    val description: String,
    val actionableSteps: List<String>
)
package com.example.sera.utils

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton
import android.os.Build
import com.google.ai.client.generativeai.type.GenerationConfig

@Singleton
class QuestionGenerationService @Inject constructor() {

    private lateinit var model: GenerativeModel
    private val TAG = "QuestionGenerationService"

    // flag to track if a cancellation has been requested
    private var cancellationRequested = false

    fun initialize(apiKey: String) {
        // GenerationConfig with temperature to add randomness
        val configBuilder = GenerationConfig.Builder()
        configBuilder.temperature = 0.7f
        val generationConfig = configBuilder.build()

        // Initialize the model with the config
        model = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = apiKey,
            generationConfig = generationConfig
        )
    }


    fun cancelOngoingOperations() {
        Log.d(TAG, "Cancellation requested for ongoing operations")
        cancellationRequested = true
    }

    private fun resetCancellationFlag() {
        cancellationRequested = false
    }

    /**
     * Checks if a cancellation has been requested and throws CancellationException if so.
     * Call this at strategic points in long-running operations.
     */
    private fun checkCancellation() {
        if (cancellationRequested) {
            Log.d(TAG, "Operation cancelled as requested")
            throw CancellationException("Question generation cancelled by user")
        }
    }

    suspend fun generateQuestions(
        content: String,
        questionType: String,
        difficulty: String,
        numberOfQuestions: Int
    ): Result<List<GeneratedQuestion>> {
        return withContext(Dispatchers.IO) {
            // Reset cancellation flag at the beginning of generation
            resetCancellationFlag()

            try {
                // Check for cancellation before building prompt
                checkCancellation()

                val prompt = buildPrompt(content, questionType, difficulty, numberOfQuestions)
                Log.d(TAG, "Sending prompt to Gemini API")

                // Check for cancellation before API call
                checkCancellation()

                val response = model.generateContent(prompt)
                val responseText = response.text
                Log.d(TAG, "Received response: $responseText")

                // Check for cancellation before parsing
                checkCancellation()

                // Parse the JSON response using the intermediate model
                val parsedQuestions = parseQuestionsFromJson(responseText)
                Log.d(TAG, "Parsed questions: $parsedQuestions")

                if (parsedQuestions.isEmpty()) {
                    Log.e(TAG, "No questions were successfully parsed")
                    return@withContext Result.failure(Exception("Failed to parse any questions from the response"))
                }

                // Check for cancellation before final conversion
                checkCancellation()

                // Convert to application model
                val convertedQuestions = parsedQuestions.map { it.toGeneratedQuestion() }
                Log.d(TAG, "Converted to ${convertedQuestions.size} application questions")
                Result.success(convertedQuestions)
            } catch (e: CancellationException) {
                Log.d(TAG, "Operation was cancelled via CancellationException")
                Result.failure(e)
            } catch (e: Exception) {
                Log.e(TAG, "Error generating questions: ${e.message}", e)

                // Check if this is a quota exceeded error
                if (e.message?.contains("quota", ignoreCase = true) == true ||
                    e.message?.contains("rate limit", ignoreCase = true) == true ||
                    e.message?.contains("limit exceeded", ignoreCase = true) == true ||
                    e.message?.contains("reached", ignoreCase = true) == true) {
                    return@withContext Result.failure(com.example.sera.utils.QuotaExceededException("Daily quota exceeded"))
                }

                Result.failure(e)
            } finally {
                // Reset cancellation flag at the end of the operation
                resetCancellationFlag()
            }
        }
    }

    private fun buildPrompt(content: String, questionType: String, difficulty: String, numberOfQuestions: Int): String {
        // random seed to make each request unique
        val randomSeed = System.currentTimeMillis()

        return """
        Based on the following content, generate $numberOfQuestions $difficulty $questionType questions:
        
        CONTENT:
        $content
        
        Requirements:
        1. Generate exactly $numberOfQuestions questions
        2. All questions must be directly based to the content
        3. Questions should be at $difficulty difficulty level
        4. Make your questions diverse and creative
        5. Random seed: $randomSeed
        6. Return the questions in the following JSON format:
        
        ${jsonFormatInstructions(questionType)}
        
        Return ONLY valid JSON with no additional text or explanations.
        The JSON should be properly formatted and parseable.
        Do not include markdown code blocks or any other formatting - just the raw JSON.
    """.trimIndent()
    }

    private fun jsonFormatInstructions(questionType: String): String {
        val baseFormat = """
            [
              {
                "question": "Question text here",
                "type": "$questionType",
        """.trimIndent()

        return when (questionType) {
            "Multiple Choice" -> """
                $baseFormat
                "options": ["Option A", "Option B", "Option C", "Option D"],
                "correctAnswerIndex": 0
              }
            ]
            
            Example: 
            [
              {
                "question": "What is the capital of France?",
                "type": "Multiple Choice",
                "options": ["Paris", "London", "Berlin", "Madrid"],
                "correctAnswerIndex": 0
              }
            ]
            """.trimIndent()

            "True or False" -> """
                $baseFormat
                "options": ["True", "False"],
                "correctAnswerIndex": 0
              }
            ]
            
            Example:
            [
              {
                "question": "Paris is the capital of France.",
                "type": "True or False",
                "options": ["True", "False"],
                "correctAnswerIndex": 0
              }
            ]
            """.trimIndent()

            "Fill in the Blanks" -> """
                $baseFormat
                "options": ["Correct answer"],
                "correctAnswerIndex": 0
              }
            ]
            
            Example:
            [
              {
                "question": "The capital of France is _____.",
                "type": "Fill in the Blanks",
                "options": ["Paris"],
                "correctAnswerIndex": 0
              }
            ]
            """.trimIndent()

            else -> """
                $baseFormat
                "options": ["Answer"],
                "correctAnswerIndex": 0
              }
            ]
            """.trimIndent()
        }
    }

    private fun parseQuestionsFromJson(response: String?): List<JsonQuestion> {
        // Check cancellation before parsing
        checkCancellation()

        if (response.isNullOrBlank()) {
            Log.e(TAG, "Empty response received")
            return emptyList()
        }

        return try {
            Log.d(TAG, "Raw response: $response")

            // Clean up the response
            val cleanedResponse = cleanJsonResponse(response)
            Log.d(TAG, "Cleaned response: $cleanedResponse")

            // Check cancellation before heavy parsing work
            checkCancellation()

            // Different parsing strategy based on Android version
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) { // Android 10 and below
                Log.d(TAG, "Using custom deserializer for Android 10 or below")
                // Use custom deserializer for better compatibility
                val gson = GsonBuilder()
                    .registerTypeAdapter(
                        object : TypeToken<List<JsonQuestion>>() {}.type,
                        JsonQuestionDeserializer()
                    )
                    .create()
                val listType = object : TypeToken<List<JsonQuestion>>() {}.type
                gson.fromJson(cleanedResponse, listType)
            } else {
                Log.d(TAG, "Using standard parser for Android 11+")
                // Standard parsing for newer Android versions
                val gson = GsonBuilder()
                    .setLenient() // Add leniency to parsing
                    .create()
                val listType = object : TypeToken<List<JsonQuestion>>() {}.type
                gson.fromJson(cleanedResponse, listType)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e  // Re-throw cancellation exceptions

            Log.e(TAG, "Parsing failed: ${e.message}", e)

            // Check cancellation before fallback parsing
            checkCancellation()

            // One more attempt with a fallback approach if standard parsing fails
            try {
                Log.d(TAG, "Attempting fallback parsing method")
                val cleanedResponse = cleanJsonResponse(response)
                return parseManually(cleanedResponse)
            } catch (fallbackEx: Exception) {
                if (fallbackEx is CancellationException) throw fallbackEx  // Re-throw cancellation exceptions

                Log.e(TAG, "Fallback parsing also failed: ${fallbackEx.message}", fallbackEx)
                emptyList()
            }
        }
    }

    private fun cleanJsonResponse(response: String): String {
        // Check cancellation periodically
        checkCancellation()

        Log.d(TAG, "Cleaning JSON response")

        // Remove any markdown code blocks and extra text
        var cleaned = response
            .replace("```json", "")
            .replace("```", "")
            .trim()

        Log.d(TAG, "After removing code blocks: $cleaned")

        // Find the first [ and last ]
        val startIndex = cleaned.indexOf("[")
        val endIndex = cleaned.lastIndexOf("]")

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            cleaned = cleaned.substring(startIndex, endIndex + 1)
            Log.d(TAG, "Extracted JSON array: $cleaned")
        } else {
            Log.e(TAG, "Could not find proper JSON array brackets")
        }

        // Additional cleaning for common issues
        cleaned = cleaned.replace("\\n", "\n") // Handle escaped newlines
            .replace("\\\"", "\"") // Handle escaped quotes if double-escaped
            .replace("\n", " ") // Remove newlines that might break JSON parsing
            .replace("  ", " ") // Normalize spacing

        return cleaned
    }

    // Manual parsing as a last resort
    private fun parseManually(jsonString: String): List<JsonQuestion> {
        // Check cancellation before parsing
        checkCancellation()

        Log.d(TAG, "Attempting manual JSON parsing")
        val result = mutableListOf<JsonQuestion>()

        try {
            // Use regex to extract questions
            val questionPattern = "\\{\\s*\"question\":\\s*\"(.*?)\"\\s*,\\s*\"type\":\\s*\"(.*?)\"\\s*,\\s*\"options\":\\s*\\[(.*?)\\]\\s*,\\s*\"correctAnswerIndex\":\\s*(\\d+)\\s*\\}".toRegex()
            val matches = questionPattern.findAll(jsonString)

            for (match in matches) {
                // Check cancellation periodically during long operations
                checkCancellation()

                val questionText = match.groupValues[1]
                val questionType = match.groupValues[2]
                val optionsString = match.groupValues[3]
                val correctIndex = match.groupValues[4].toIntOrNull() ?: 0

                // Parse options
                val options = mutableListOf<String>()
                val optionPattern = "\"(.*?)\"".toRegex()
                val optionMatches = optionPattern.findAll(optionsString)
                for (optionMatch in optionMatches) {
                    options.add(optionMatch.groupValues[1])
                }

                if (options.isNotEmpty()) {
                    result.add(JsonQuestion(questionText, questionType, options, correctIndex))
                }
            }

            Log.d(TAG, "Manual parsing found ${result.size} questions")
        } catch (e: Exception) {
            if (e is CancellationException) throw e  // Re-throw cancellation exceptions

            Log.e(TAG, "Manual parsing failed: ${e.message}", e)
        }

        return result
    }

    // Custom JSON deserializer for more robust parsing
    inner class JsonQuestionDeserializer : JsonDeserializer<List<JsonQuestion>> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): List<JsonQuestion> {
            // Check cancellation before starting deserialization
            checkCancellation()

            val result = mutableListOf<JsonQuestion>()

            try {
                if (json.isJsonArray) {
                    val jsonArray = json.asJsonArray
                    for (element in jsonArray) {
                        // Check cancellation periodically for long arrays
                        checkCancellation()

                        try {
                            val obj = element.asJsonObject
                            val question = obj.get("question")?.asString ?: continue
                            val type = obj.get("type")?.asString ?: continue

                            val optionsElement = obj.get("options") ?: continue
                            if (!optionsElement.isJsonArray) continue

                            val optionsArray = optionsElement.asJsonArray
                            val options = mutableListOf<String>()
                            for (option in optionsArray) {
                                options.add(option.asString)
                            }

                            val correctAnswerIndex = obj.get("correctAnswerIndex")?.asInt ?: 0

                            result.add(JsonQuestion(question, type, options, correctAnswerIndex))
                        } catch (e: Exception) {
                            if (e is CancellationException) throw e  // Re-throw cancellation exceptions

                            Log.e(TAG, "Failed to parse question item: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e  // Re-throw cancellation exceptions

                Log.e(TAG, "Failed to parse questions array: ${e.message}")
            }

            return result
        }
    }
}

// Intermediate data class for JSON parsing
data class JsonQuestion(
    val question: String,
    val type: String,
    val options: List<String>,
    val correctAnswerIndex: Int
) {
    // Convert to application model
    fun toGeneratedQuestion(): GeneratedQuestion {
        return GeneratedQuestion(
            question = question,
            options = options.mapIndexed { index, text ->
                QuestionOption(text, index == correctAnswerIndex)
            },
            correctAnswerIndex = correctAnswerIndex,
            type = type
        )
    }
}


data class GeneratedQuestion(
    val question: String,
    val options: List<QuestionOption>,
    val correctAnswerIndex: Int,
    val type: String,
    var userAnswer: String? = null
)

data class QuestionOption(
    val text: String,
    val isCorrect: Boolean
)

class QuotaExceededException(message: String) : Exception(message)
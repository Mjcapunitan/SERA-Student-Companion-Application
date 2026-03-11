package com.example.sera.screens.summarize

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SummarizeViewModel @Inject constructor() : ViewModel() {
    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    private val _fileName = MutableStateFlow("")
    val fileName = _fileName.asStateFlow()

    private val _summarizedText = MutableStateFlow("")
    val summarizedText = _summarizedText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun setText(newText: String) {
        _text.value = newText
    }

    fun setFileName(name: String) {

        val baseFileName = removeFileExtension(name)


        val trimmedName = if (baseFileName.length > 30) {
            "${baseFileName.take(27)}..."
        } else {
            baseFileName
        }

        _fileName.value = trimmedName
    }

    private fun removeFileExtension(fileName: String): String {
        return fileName.substringBeforeLast(".")
    }

    fun setSummarizedText(summary: String): String {
        // Basic cleaning first
        val initialCleaned = summary
            .replace(Regex("-\\s+"), "") // Fix hyphenated words
            .replace(Regex("\\s+"), " ") // Remove excessive spaces
            .replace(Regex("\\s+([.,!?])"), "$1") // Fix spaces before punctuation
            .replace(Regex("\\.{2,}"), ".") // Replace multiple periods with single period
            .replace(Regex("\\.\\s*\\."), ".") // Fix cases of ". ." to "."
            .replace(Regex("\\.(?!\\s|$)"), ". ") // Ensure a space after a period if not end of line
            .replace(Regex("\\s+\\.\\s+"), ". ") // Remove spaces before and after a period
            .replace(Regex("(?<=\\w)\\.(?=\\w)"), ". ") // Ensure a space after a period between words
            .trim()

        // Split into sentences
        val sentences = initialCleaned.split(Regex("(?<=[.!?])\\s+"))

        // Process sentences to identify potential sections and topics
        val processedSentences = mutableListOf<String>()
        var currentParagraph = mutableListOf<String>()

        sentences.forEachIndexed { index, sentence ->
            if (sentence.length < 50 &&
                (sentence.startsWith("The") ||
                        sentence.startsWith("In") ||
                        sentence.startsWith("This") ||
                        sentence.contains("topic") ||
                        sentence.contains("section") ||
                        sentence.contains("chapter") ||
                        sentence.contains("conclusion") ||
                        sentence.contains("summary") ||
                        sentence.contains("introduction"))) {
                // This might be a topic sentence or section header
                if (currentParagraph.isNotEmpty()) {
                    processedSentences.add(currentParagraph.joinToString(" "))
                    currentParagraph.clear()
                }
                // Add as a header
                processedSentences.add("## $sentence")
            } else if (sentence.length < 100 &&
                (sentence.contains("Firstly") ||
                        sentence.contains("Secondly") ||
                        sentence.contains("Finally") ||
                        sentence.contains("Additionally") ||
                        sentence.contains("Furthermore") ||
                        sentence.contains("Moreover") ||
                        sentence.startsWith("Another"))) {
                // This might be a list item or key point
                if (currentParagraph.isNotEmpty()) {
                    processedSentences.add(currentParagraph.joinToString(" "))
                    currentParagraph.clear()
                }
                // Add as a bullet point
                processedSentences.add("• $sentence")
            } else {
                // Regular sentence for the current paragraph
                currentParagraph.add(sentence)

                // Break paragraphs after 3-4 sentences for readability
                if (currentParagraph.size >= 3 ||
                    (index == sentences.size - 1 && currentParagraph.isNotEmpty())) {
                    processedSentences.add(currentParagraph.joinToString(" "))
                    currentParagraph.clear()
                }
            }
        }

        // Add any remaining sentences
        if (currentParagraph.isNotEmpty()) {
            processedSentences.add(currentParagraph.joinToString(" "))
        }

        // Join processed sections with clear paragraph breaks
        val formattedText = processedSentences.joinToString("\n\n")

        // Additional formatting for clarity
        val finalText = formattedText
            .replace(Regex("(?m)^## (.+)$"), "\n\n## $1") // Add extra space before headers
            .replace(Regex("(?m)^• (.+)$"), "• $1") // Ensure bullet points format
            .replace(Regex("\\n{3,}"), "\n\n") // Remove excessive line breaks
            .trim()

        _summarizedText.value = finalText
        return finalText
    }

    fun generateDefaultTitle(text: String): String {
        // Extract first 5-7 words from text to create a title
        val firstWords = text.trim().split(Regex("\\s+")).take(7)

        // If we have enough words, use them as title
        if (firstWords.size >= 5) {
            val titleText = firstWords.joinToString(" ")
            // Add ellipsis if we truncated the text
            return "$titleText..."
        }
        // If text is too short, use generic title with timestamp
        else {
            val timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy")
            )
            return "Summary - $timestamp"
        }
    }

    private fun cleanText(text: String): String {
        return text
            .replace(Regex("\\s+"), " ")
            .replace(Regex("[^\\p{L}\\p{N}\\s.,!?]"), "")
            .trim()
    }

    fun setLoading(isLoading: Boolean, progress: Float = 0f) {
        _isLoading.value = isLoading
    }

}
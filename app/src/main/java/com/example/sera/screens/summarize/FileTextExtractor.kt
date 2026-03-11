package com.example.sera.screens.summarize

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.suspendCancellableCoroutine
import org.zwobble.mammoth.DocumentConverter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FileTextExtractor(private val context: Context) {

    private fun cleanText(text: String): String {
        return text
            .replace(Regex("-\\s+"), "")
            .replace(Regex("\\s+"), " ")
            .replace(Regex("[^\\p{L}\\p{N}\\s.,!?]"), "")
            .replace(Regex("\\n{2,}"), "\n\n")
            .replace(Regex("\\s+([.,!?])"), "$1")
            .trim()
    }

    init {
        // Initialize PDFBox for PDF processing
        PDFBoxResourceLoader.init(context)
    }

    suspend fun extractTextFromFiles(fileUris: List<Uri>): String {
        val extractedTexts = mutableListOf<String>()

        for (uri in fileUris) {
            val mimeType = context.contentResolver.getType(uri) ?: ""

            val text = when {
                mimeType == "application/pdf" -> extractTextFromPdf(uri)
                mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> extractTextFromDocx(uri)
                mimeType.startsWith("image/") -> extractTextFromImage(uri)
                else -> "Unsupported file format"
            }

            extractedTexts.add(text)
        }

        return extractedTexts.joinToString("\n\n---\n\n") // Separate texts with a divider
    }

    // New method for image text extraction using ML Kit
    private suspend fun extractTextFromImage(imageUri: Uri): String = suspendCancellableCoroutine { continuation ->
        try {
            // Create InputImage object from URI
            val image = InputImage.fromFilePath(context, imageUri)

            // Get an instance of TextRecognizer
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            // Process the image
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Extract text from the result
                    val extractedText = visionText.text
                    continuation.resume(extractedText)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        } catch (e: Exception) {
            Log.e("FileTextExtractor", "Error extracting text from image", e)
            continuation.resume("Error extracting text from image: ${e.message}")
        }
    }

    private fun extractTextFromPdf(pdfUri: Uri): String {
        // Existing code...
        return try {
            context.contentResolver.openInputStream(pdfUri)?.use { inputStream ->
                val document = PDDocument.load(inputStream)
                val stripper = PDFTextStripper().apply {
                    setSortByPosition(true)
                    setShouldSeparateByBeads(true)
                    lineSeparator = "\n\n" // Ensures paragraph separation
                    wordSeparator = " "    // Prevents missing spaces between words
                }

                // Extract text page by page and add a simple line break between pages
                val pageCount = document.numberOfPages
                val pageTexts = mutableListOf<String>()

                for (pageNum in 1..pageCount) {
                    stripper.startPage = pageNum
                    stripper.endPage = pageNum
                    val pageText = stripper.getText(document) ?: ""
                    pageTexts.add(pageText.trim())
                }

                document.close()
                val rawText = pageTexts.joinToString("\n\n")
                cleanExtractedText(rawText) // Clean the text after extraction
            } ?: "Error: Unable to read PDF"
        } catch (e: Exception) {
            Log.e("FileTextExtractor", "Error extracting text from PDF", e)
            "Error: ${e.message}"
        }
    }

    // Function to remove unwanted line breaks while keeping paragraph breaks
    private fun cleanExtractedText(text: String): String {
        return text.replace(Regex("(?<!\\n)\\n(?!\\n)"), " ") // Replace single line breaks with spaces
    }

    private fun extractTextFromDocx(docxUri: Uri): String {
        return try {
            context.contentResolver.openInputStream(docxUri)?.use { inputStream ->
                val converter = DocumentConverter()
                val result = converter.convertToHtml(inputStream)
                val rawText = result.value // Extract the actual string
                android.text.Html.fromHtml(rawText, android.text.Html.FROM_HTML_MODE_LEGACY).toString()
            } ?: "Error: Unable to read DOCX"
        } catch (e: Exception) {
            Log.e("FileTextExtractor", "Error extracting text from DOCX", e)
            "Error: ${e.message}"
        }
    }
}
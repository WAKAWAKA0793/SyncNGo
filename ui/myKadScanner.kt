package com.example.tripshare.ui

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.regex.Pattern

class MyKadScanner {

    fun scanMyKad(bitmap: Bitmap, onResult: (String?) -> Unit) {
        // 1. Prepare the image for ML Kit
        val image = InputImage.fromBitmap(bitmap, 0)

        // 2. Initialize the Text Recognizer
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // 3. Process the image
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // 4. Extract IC Number using Logic
                val icNumber = extractICNumber(visionText.text)
                onResult(icNumber)
            }
            .addOnFailureListener { e ->
                Log.e("MyKadScanner", "Scanning failed", e)
                onResult(null)
            }
    }

    // THIS IS THE KEY PART: Finding the IC pattern in the messy text
    private fun extractICNumber(rawText: String): String? {
        // Regex to find:
        // Option A: 990101-01-1234 (With dashes)
        // Option B: 990101011234 (Pure digits)

        // This regex looks for 12 digits, allowing for optional dashes
        val icPattern = Pattern.compile("\\d{6}-?\\d{2}-?\\d{4}")
        val matcher = icPattern.matcher(rawText)

        if (matcher.find()) {
            // Return the found string, removing dashes to standardize it
            return matcher.group().replace("-", "")
        }
        return null
    }
}
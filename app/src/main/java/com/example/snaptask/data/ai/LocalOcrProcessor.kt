package com.example.snaptask.data.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.math.max

data class OcrResult(
    val fullText: String,
    val success: Boolean,
    val errorMessage: String? = null,
)

class LocalOcrProcessor(private val context: Context) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun processImageUri(imageUri: Uri): OcrResult = withContext(Dispatchers.Default) {
        try {
            val bitmap = decodeSampledBitmapFromUri(imageUri, 2000)
                ?: return@withContext OcrResult("", false, "Failed to decode image")

            val inputImage = InputImage.fromBitmap(bitmap, 0)

            suspendCancellableCoroutine { continuation ->
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        continuation.resume(OcrResult(visionText.text, true))
                    }
                    .addOnFailureListener { e ->
                        continuation.resume(OcrResult("", false, e.message))
                    }
            }
        } catch (e: Exception) {
            OcrResult("", false, e.message)
        }
    }

    private fun decodeSampledBitmapFromUri(uri: Uri, maxDimension: Int): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            var inSampleSize = 1
            val maxSide = max(options.outWidth, options.outHeight)
            if (maxSide > maxDimension) {
                inSampleSize = maxSide / maxDimension
            }

            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            }
        } catch (e: Exception) {
            null
        }
    }
}
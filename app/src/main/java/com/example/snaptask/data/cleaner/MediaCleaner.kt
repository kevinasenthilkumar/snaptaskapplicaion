package com.example.snaptask.data.cleaner

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.example.snaptask.data.repository.ScreenshotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaCleaner(
    private val context: Context,
    private val repository: ScreenshotRepository,
) {
    fun requestTrash(
        uris: List<Uri>,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
    ): Boolean {
        if (uris.isEmpty()) return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val pendingIntent = MediaStore.createTrashRequest(
                context.contentResolver,
                uris,
                true
            )
            val request = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
            launcher.launch(request)
            true
        } else {
            false
        }
    }

    suspend fun onTrashConfirmed(ids: List<Long>) = withContext(Dispatchers.IO) {
        repository.database.markAsTrashed(ids)
        for (id in ids) {
            try {
                repository.deleteScreenshot(id)
            } catch (_: Exception) {}
        }
    }
}
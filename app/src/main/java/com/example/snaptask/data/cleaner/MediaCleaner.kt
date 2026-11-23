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
            var allDeleted = true
            for (uri in uris) {
                try {
                    context.contentResolver.delete(uri, null, null)
                } catch (_: Exception) {
                    allDeleted = false
                }
            }
            allDeleted
        }
    }

    fun requestPermanentDelete(
        uris: List<Uri>,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
    ): Boolean {
        if (uris.isEmpty()) return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val pendingIntent = MediaStore.createDeleteRequest(
                context.contentResolver,
                uris
            )
            val request = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
            launcher.launch(request)
            true
        } else {
            var allDeleted = true
            for (uri in uris) {
                try {
                    context.contentResolver.delete(uri, null, null)
                } catch (_: Exception) {
                    allDeleted = false
                }
            }
            allDeleted
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

    suspend fun onPermanentDeleteConfirmed(ids: List<Long>) = withContext(Dispatchers.IO) {
        repository.deletePermanently(ids)
    }

    suspend fun onRestoreConfirmed(ids: List<Long>) = withContext(Dispatchers.IO) {
        repository.restoreScreenshots(ids)
    }
}
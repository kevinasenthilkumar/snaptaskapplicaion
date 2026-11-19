package com.example.snaptask.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.example.snaptask.data.local.AppDatabase
import com.example.snaptask.data.local.LocalScreenshotEntity
import com.example.snaptask.data.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ScreenshotRepository(
    private val context: Context,
    private val apiService: ApiService,
    val database: AppDatabase,
) {
    private val _screenshotsFlow = MutableStateFlow<List<LocalScreenshotEntity>>(emptyList())
    val allScreenshots: Flow<List<LocalScreenshotEntity>> = _screenshotsFlow.asStateFlow()

    private var contentObserver: ContentObserver? = null

    suspend fun refreshFromDb() = withContext(Dispatchers.IO) {
        _screenshotsFlow.value = database.getAllActive()
    }

    suspend fun queryAndSyncScreenshots(): List<LocalScreenshotEntity> = withContext(Dispatchers.IO) {
        val existingMap = database.getAllActive().associateBy { it.id }.toMutableMap()
        val newEntities = mutableListOf<LocalScreenshotEntity>()

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.SIZE,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.RELATIVE_PATH else MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
        )

        val pathColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.RELATIVE_PATH
        } else {
            MediaStore.Images.Media.DATA
        }

        val selection = StringBuilder()
        selection.append("(")
        selection.append("$pathColumn LIKE ? OR ")
        selection.append("${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} LIKE ? OR ")
        selection.append("${MediaStore.Images.Media.DISPLAY_NAME} LIKE ? OR ")
        selection.append("$pathColumn LIKE ?")
        selection.append(") AND ")
        selection.append("${MediaStore.Images.Media.MIME_TYPE} IN (?, ?, ?)")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            selection.append(" AND ${MediaStore.Images.Media.IS_PENDING} = 0")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            selection.append(" AND ${MediaStore.Images.Media.IS_TRASHED} = 0")
        }

        val selectionArgs = arrayOf(
            "%Screenshots%",
            "%Screenshot%",
            "%Screenshot%",
            "%DCIM/Screenshots%",
            "image/png",
            "image/jpeg",
            "image/webp"
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        try {
            context.contentResolver.query(collectionUri, projection, selection.toString(), selectionArgs, sortOrder)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol) ?: "Screenshot"
                    val dateTaken = cursor.getLong(dateCol)
                    val size = cursor.getLong(sizeCol)

                    val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    val existing = existingMap[id]
                    if (existing != null) {
                        newEntities.add(existing)
                    } else {
                        val entity = LocalScreenshotEntity(
                            id = id,
                            contentUri = uri.toString(),
                            displayName = name,
                            capturedAt = if (dateTaken > 0) dateTaken else System.currentTimeMillis(),
                            size = size,
                            ocrStatus = "PENDING",
                        )
                        newEntities.add(entity)
                    }
                }
            }

            // Fallback: If zero screenshots matched the strict path filter, query all images
            if (newEntities.isEmpty()) {
                val fallbackSelection = StringBuilder("${MediaStore.Images.Media.MIME_TYPE} IN (?, ?, ?)")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    fallbackSelection.append(" AND ${MediaStore.Images.Media.IS_PENDING} = 0")
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    fallbackSelection.append(" AND ${MediaStore.Images.Media.IS_TRASHED} = 0")
                }
                val fallbackArgs = arrayOf("image/png", "image/jpeg", "image/webp")

                context.contentResolver.query(collectionUri, projection, fallbackSelection.toString(), fallbackArgs, sortOrder)?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                    val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        val name = cursor.getString(nameCol) ?: "Screenshot"
                        val dateTaken = cursor.getLong(dateCol)
                        val size = cursor.getLong(sizeCol)
                        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                        val existing = existingMap[id]
                        if (existing != null) {
                            newEntities.add(existing)
                        } else {
                            val entity = LocalScreenshotEntity(
                                id = id,
                                contentUri = uri.toString(),
                                displayName = name,
                                capturedAt = if (dateTaken > 0) dateTaken else System.currentTimeMillis(),
                                size = size,
                                ocrStatus = "PENDING",
                            )
                            newEntities.add(entity)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (newEntities.isNotEmpty()) {
            database.insertAll(newEntities)
        }
        refreshFromDb()
        newEntities
    }

    suspend fun uploadScreenshotFile(entity: LocalScreenshotEntity): Long? = withContext(Dispatchers.IO) {
        if (entity.backendId != null) return@withContext entity.backendId

        try {
            val uri = Uri.parse(entity.contentUri)
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@withContext null
            if (bytes.isEmpty()) return@withContext null

            val mimeType = context.contentResolver.getType(uri) ?: "image/png"
            val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", entity.displayName, requestFile)

            val response = apiService.uploadScreenshot(filePart)
            if (response.isSuccessful && response.body()?.success == true) {
                val backendId = response.body()?.data?.screenshot?.id
                if (backendId != null) {
                    val updated = entity.copy(backendId = backendId)
                    database.insertOrUpdate(updated)
                }
                backendId
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun registerContentObserver(onContentChanged: () -> Unit) {
        if (contentObserver != null) return
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                onContentChanged()
            }
        }
        context.contentResolver.registerContentObserver(uri, true, contentObserver!!)
    }

    fun unregisterContentObserver() {
        contentObserver?.let {
            context.contentResolver.unregisterContentObserver(it)
            contentObserver = null
        }
    }

    suspend fun deleteScreenshot(id: Long): Resource<Unit> = safeApiCall { apiService.deleteScreenshot(id) }
    suspend fun getStats(): Resource<ScreenshotStatsDto> = safeApiCall { apiService.getScreenshotStats() }
}

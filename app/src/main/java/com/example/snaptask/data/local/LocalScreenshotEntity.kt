package com.example.snaptask.data.local

data class LocalScreenshotEntity(
    val id: Long, // MediaStore._ID
    val contentUri: String,
    val displayName: String,
    val capturedAt: Long,
    val size: Long,
    val ocrStatus: String, // PENDING, DONE, FAILED
    val ocrText: String? = null,
    val category: String = "Other",
    val eventDate: String? = null,
    val eventTime: String? = null,
    val confidence: Float = 0f,
    val backendId: Long? = null,
    val status: String = "active", // active, kept, trashed
)
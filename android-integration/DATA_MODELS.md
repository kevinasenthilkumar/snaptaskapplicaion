# SnapTask Android Data Models & Contracts

This document contains production Kotlin code for Remote DTOs, Room Database Entities, Clean Domain Models, and Mapping Functions.

---

## 1. Remote DTOs (Data Transfer Objects)

```kotlin
package com.example.snaptask.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?,
    @SerializedName("errors") val errors: Map<String, List<String>> = emptyMap(),
    @SerializedName("meta") val meta: Map<String, Any> = emptyMap(),
)

data class AuthResponseDto(
    @SerializedName("user") val user: UserDto,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Long,
)

data class UserDto(
    @SerializedName("id") val id: Long,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
)

data class ScreenshotDto(
    @SerializedName("id") val id: Long,
    @SerializedName("local_media_id") val localMediaId: String?,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("file_url") val fileUrl: String,
    @SerializedName("file_size") val fileSize: Long,
    @SerializedName("file_size_formatted") val fileSizeFormatted: String,
    @SerializedName("category") val category: String,
    @SerializedName("classification_confidence") val confidence: Float,
    @SerializedName("ocr_text") val ocrText: String?,
    @SerializedName("extracted_title") val extractedTitle: String?,
    @SerializedName("extracted_date") val extractedDate: String?,
    @SerializedName("extracted_time") val extractedTime: String?,
    @SerializedName("extracted_location") val extractedLocation: String?,
    @SerializedName("extracted_amount") val extractedAmount: Double?,
    @SerializedName("extracted_currency") val extractedCurrency: String?,
    @SerializedName("status") val status: String,
    @SerializedName("is_kept") val isKept: Boolean,
    @SerializedName("captured_at") val capturedAt: String?,
    @SerializedName("uploaded_at") val uploadedAt: String?,
)

data class DatedItemDto(
    @SerializedName("id") val id: Long,
    @SerializedName("screenshot_id") val screenshotId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String,
    @SerializedName("event_date") val eventDate: String,
    @SerializedName("event_time") val eventTime: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("priority") val priority: String,
    @SerializedName("status") val status: String,
    @SerializedName("is_kept") val isKept: Boolean,
    @SerializedName("image_url") val imageUrl: String?,
)

data class NonDatedItemDto(
    @SerializedName("id") val id: Long,
    @SerializedName("screenshot_id") val screenshotId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String,
    @SerializedName("content_text") val contentText: String?,
    @SerializedName("is_favorite") val isFavorite: Boolean,
    @SerializedName("color_label") val colorLabel: String,
    @SerializedName("is_kept") val isKept: Boolean,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("tags") val tags: List<TagDto> = emptyList(),
)

data class TagDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String,
)

data class ReminderDto(
    @SerializedName("id") val id: Long,
    @SerializedName("dated_item_id") val datedItemId: Long?,
    @SerializedName("title") val title: String,
    @SerializedName("note") val note: String?,
    @SerializedName("trigger_at") val triggerAt: String,
    @SerializedName("status") val status: String,
)

data class TrashItemDto(
    @SerializedName("id") val id: Long,
    @SerializedName("screenshot_id") val screenshotId: Long,
    @SerializedName("original_title") val originalTitle: String?,
    @SerializedName("original_category") val originalCategory: String,
    @SerializedName("file_size_formatted") val fileSizeFormatted: String,
    @SerializedName("days_left") val daysLeft: Int,
    @SerializedName("image_url") val imageUrl: String?,
)

data class DashboardSummaryDto(
    @SerializedName("total_screenshots") val totalScreenshots: Int,
    @SerializedName("dated_screenshots_count") val datedCount: Int,
    @SerializedName("non_dated_screenshots_count") val nonDatedCount: Int,
    @SerializedName("cleanable_screenshots_count") val cleanableCount: Int,
    @SerializedName("estimated_reclaimable_storage_mb") val reclaimableMb: Double,
    @SerializedName("overdue_reminders_count") val overdueRemindersCount: Int,
    @SerializedName("upcoming_reminders_count") val upcomingRemindersCount: Int,
    @SerializedName("recent_screenshots") val recentScreenshots: List<ScreenshotDto>,
)
```

---

## 2. Domain Models (Pure Kotlin)

```kotlin
package com.example.snaptask.domain.model

enum class ScreenshotCategory { Dated, NonDated, Unclassified }

data class DatedScreenshot(
    val id: Long,
    val screenshotId: Long,
    val title: String,
    val category: String, // Tickets, Travel, Booking, etc.
    val eventDate: String,
    val eventTime: String?,
    val location: String?,
    val amountFormatted: String?,
    val isKept: Boolean,
    val imageUrl: String?,
)

data class NonDatedScreenshot(
    val id: Long,
    val screenshotId: Long,
    val title: String,
    val category: String, // Shopping, Recipes, Receipts, Ideas
    val contentSummary: String?,
    val isFavorite: Boolean,
    val colorLabel: String,
    val isKept: Boolean,
    val imageUrl: String?,
    val tags: List<String>,
)

data class ReminderItem(
    val id: Long,
    val title: String,
    val triggerDateTime: String,
    val isOverdue: Boolean,
    val note: String?,
)

data class TrashRecord(
    val id: Long,
    val screenshotId: Long,
    val title: String,
    val category: String,
    val sizeText: String,
    val daysRemaining: Int,
    val imageUrl: String?,
)
```

---

## 3. Room Entities & DAOs

```kotlin
package com.example.snaptask.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screenshots_cache")
data class ScreenshotEntity(
    @PrimaryKey val id: Long,
    val localMediaId: String?,
    val fileName: String,
    val fileUrl: String,
    val category: String,
    val title: String?,
    val eventDate: String?,
    val isKept: Boolean,
    val uploadedAt: String?,
)

@Entity(tableName = "dated_items_cache")
data class DatedItemEntity(
    @PrimaryKey val id: Long,
    val screenshotId: Long,
    val title: String,
    val category: String,
    val eventDate: String,
    val eventTime: String?,
    val location: String?,
    val amount: Double?,
    val currency: String?,
    val isKept: Boolean,
    val imageUrl: String?,
)
```

---

## 4. DTO-to-Domain Mappers

```kotlin
package com.example.snaptask.data.mapper

import com.example.snaptask.data.remote.dto.DatedItemDto
import com.example.snaptask.data.remote.dto.NonDatedItemDto
import com.example.snaptask.domain.model.DatedScreenshot
import com.example.snaptask.domain.model.NonDatedScreenshot

fun DatedItemDto.toDomain(): DatedScreenshot {
    val formattedAmount = if (amount != null && currency != null) {
        if (currency == "USD") "$${amount}" else "${amount} ${currency}"
    } else null

    return DatedScreenshot(
        id = id,
        screenshotId = screenshotId,
        title = title,
        category = category.replaceFirstChar { it.uppercase() },
        eventDate = eventDate,
        eventTime = eventTime,
        location = location,
        amountFormatted = formattedAmount,
        isKept = isKept,
        imageUrl = imageUrl,
    )
}

fun NonDatedItemDto.toDomain(): NonDatedScreenshot {
    return NonDatedScreenshot(
        id = id,
        screenshotId = screenshotId,
        title = title,
        category = category.replaceFirstChar { it.uppercase() },
        contentSummary = contentText?.take(80),
        isFavorite = isFavorite,
        colorLabel = colorLabel,
        isKept = isKept,
        imageUrl = imageUrl,
        tags = tags.map { it.name },
    )
}
```


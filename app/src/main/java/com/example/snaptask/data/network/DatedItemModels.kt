package com.example.snaptask.data.network

import com.google.gson.annotations.SerializedName

data class DatedItemDto(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("screenshot_id") val screenshotId: Long?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String,
    @SerializedName("event_date") val eventDate: String?,
    @SerializedName("event_time") val eventTime: String?,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("status") val status: String,
    @SerializedName("priority") val priority: String,
    @SerializedName("reminder_enabled") val reminderEnabled: Boolean,
    @SerializedName("reminder_at") val reminderAt: String?,
    @SerializedName("completed_at") val completedAt: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
)

data class CreateDatedItemRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("category") val category: String = "other",
    @SerializedName("event_date") val eventDate: String? = null,
    @SerializedName("event_time") val eventTime: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("currency") val currency: String? = null,
    @SerializedName("priority") val priority: String = "medium",
    @SerializedName("reminder_enabled") val reminderEnabled: Boolean = false,
    @SerializedName("reminder_at") val reminderAt: String? = null,
    @SerializedName("screenshot_id") val screenshotId: Long? = null,
)

data class SnoozeDatedItemRequest(
    @SerializedName("snooze_until") val snoozeUntil: String,
)


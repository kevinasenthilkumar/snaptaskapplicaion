package com.example.snaptask.data.network

import com.google.gson.annotations.SerializedName

data class NonDatedItemDto(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("screenshot_id") val screenshotId: Long?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String,
    @SerializedName("content_text") val contentText: String?,
    @SerializedName("tags") val tags: List<String>?,
    @SerializedName("is_favorite") val isFavorite: Boolean,
    @SerializedName("color_label") val colorLabel: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
)

data class CreateNonDatedItemRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("category") val category: String = "other",
    @SerializedName("content_text") val contentText: String? = null,
    @SerializedName("tags") val tags: List<String>? = null,
    @SerializedName("color_label") val colorLabel: String? = null,
    @SerializedName("screenshot_id") val screenshotId: Long? = null,
)

data class ScreenshotDto(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("file_path") val filePath: String?,
    @SerializedName("mime_type") val mimeType: String?,
    @SerializedName("file_size") val fileSize: Long?,
    @SerializedName("category") val category: String,
    @SerializedName("classification_confidence") val classificationConfidence: Double?,
    @SerializedName("ocr_text") val ocrText: String?,
    @SerializedName("extracted_title") val extractedTitle: String?,
    @SerializedName("extracted_description") val extractedDescription: String?,
    @SerializedName("extracted_date") val extractedDate: String?,
    @SerializedName("extracted_time") val extractedTime: String?,
    @SerializedName("extracted_location") val extractedLocation: String?,
    @SerializedName("source_app") val sourceApp: String?,
    @SerializedName("status") val status: String,
    @SerializedName("captured_at") val capturedAt: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
)

data class ScreenshotStatsDto(
    @SerializedName("total") val total: Int,
    @SerializedName("dated") val dated: Int,
    @SerializedName("non_dated") val nonDated: Int,
    @SerializedName("unclassified") val unclassified: Int,
    @SerializedName("pending_cleanup") val pendingCleanup: Int,
    @SerializedName("total_size_bytes") val totalSizeBytes: Long,
)

data class ReminderDto(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("dated_item_id") val datedItemId: Long?,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String?,
    @SerializedName("remind_at") val remindAt: String,
    @SerializedName("status") val status: String,
    @SerializedName("snooze_until") val snoozeUntil: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
)

data class CreateReminderRequest(
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String? = null,
    @SerializedName("remind_at") val remindAt: String,
    @SerializedName("dated_item_id") val datedItemId: Long? = null,
)

data class SnoozeReminderRequest(
    @SerializedName("snooze_until") val snoozeUntil: String,
)

data class NotificationDto(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String?,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("read_at") val readAt: String?,
    @SerializedName("created_at") val createdAt: String,
)

data class UnreadCountDto(
    @SerializedName("unread_count") val unreadCount: Int,
)

data class UserSettingsDto(
    @SerializedName("auto_scan") val autoScan: Boolean,
    @SerializedName("auto_categorize") val autoCategorize: Boolean,
    @SerializedName("reminder_lead_time_minutes") val reminderLeadTimeMinutes: Int,
    @SerializedName("cleanup_frequency_days") val cleanupFrequencyDays: Int,
    @SerializedName("cleanup_threshold_days") val cleanupThresholdDays: Int,
    @SerializedName("notify_reminders") val notifyReminders: Boolean,
    @SerializedName("notify_cleanup") val notifyCleanup: Boolean,
    @SerializedName("notify_scan_complete") val notifyScanComplete: Boolean,
    @SerializedName("privacy_mode") val privacyMode: Boolean,
    @SerializedName("analytics_opt_in") val analyticsOptIn: Boolean,
    @SerializedName("retention_period_days") val retentionPeriodDays: Int,
)

data class AutomationSettingsDto(
    @SerializedName("auto_scan") val autoScan: Boolean,
    @SerializedName("auto_categorize") val autoCategorize: Boolean,
    @SerializedName("cleanup_frequency_days") val cleanupFrequencyDays: Int,
    @SerializedName("cleanup_threshold_days") val cleanupThresholdDays: Int,
)

data class CleanupRuleDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("condition_type") val conditionType: String,
    @SerializedName("condition_value") val conditionValue: String,
    @SerializedName("action") val action: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String,
)

data class CreateCleanupRuleRequest(
    @SerializedName("name") val name: String,
    @SerializedName("condition_type") val conditionType: String,
    @SerializedName("condition_value") val conditionValue: String,
    @SerializedName("action") val action: String = "delete",
    @SerializedName("is_active") val isActive: Boolean = true,
)


package com.example.snaptask.data.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("auth/me")
    suspend fun getMe(): Response<ApiResponse<UserDto>>

    @PUT("auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserDto>>

    @PUT("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Unit>>

    // ── Screenshots ───────────────────────────────────────────────────
    @GET("screenshots")
    suspend fun getScreenshots(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("category") category: String? = null,
        @Query("status") status: String? = null,
    ): Response<ApiResponse<List<ScreenshotDto>>>

    @GET("screenshots/{id}")
    suspend fun getScreenshot(@Path("id") id: Long): Response<ApiResponse<ScreenshotDto>>

    @DELETE("screenshots/{id}")
    suspend fun deleteScreenshot(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @POST("screenshots/{id}/analyze")
    suspend fun analyzeScreenshot(@Path("id") id: Long): Response<ApiResponse<ScreenshotDto>>

    @GET("screenshots/stats")
    suspend fun getScreenshotStats(): Response<ApiResponse<ScreenshotStatsDto>>

    @Multipart
    @POST("screenshots/upload")
    suspend fun uploadScreenshot(
        @Part file: MultipartBody.Part,
    ): Response<ApiResponse<ScreenshotDto>>

    // ── Dated Items ───────────────────────────────────────────────────
    @GET("dated-items")
    suspend fun getDatedItems(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("status") status: String? = null,
        @Query("category") category: String? = null,
    ): Response<ApiResponse<List<DatedItemDto>>>

    @GET("dated-items/{id}")
    suspend fun getDatedItem(@Path("id") id: Long): Response<ApiResponse<DatedItemDto>>

    @POST("dated-items")
    suspend fun createDatedItem(@Body request: CreateDatedItemRequest): Response<ApiResponse<DatedItemDto>>

    @PUT("dated-items/{id}")
    suspend fun updateDatedItem(
        @Path("id") id: Long,
        @Body request: CreateDatedItemRequest,
    ): Response<ApiResponse<DatedItemDto>>

    @DELETE("dated-items/{id}")
    suspend fun deleteDatedItem(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @GET("dated-items/upcoming")
    suspend fun getUpcomingDatedItems(): Response<ApiResponse<List<DatedItemDto>>>

    @GET("dated-items/overdue")
    suspend fun getOverdueDatedItems(): Response<ApiResponse<List<DatedItemDto>>>

    @POST("dated-items/{id}/complete")
    suspend fun completeDatedItem(@Path("id") id: Long): Response<ApiResponse<DatedItemDto>>

    @POST("dated-items/{id}/snooze")
    suspend fun snoozeDatedItem(
        @Path("id") id: Long,
        @Body request: SnoozeDatedItemRequest,
    ): Response<ApiResponse<DatedItemDto>>

    // ── Non-Dated Items ───────────────────────────────────────────────
    @GET("non-dated-items")
    suspend fun getNonDatedItems(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("category") category: String? = null,
    ): Response<ApiResponse<List<NonDatedItemDto>>>

    @GET("non-dated-items/{id}")
    suspend fun getNonDatedItem(@Path("id") id: Long): Response<ApiResponse<NonDatedItemDto>>

    @POST("non-dated-items")
    suspend fun createNonDatedItem(@Body request: CreateNonDatedItemRequest): Response<ApiResponse<NonDatedItemDto>>

    @PUT("non-dated-items/{id}")
    suspend fun updateNonDatedItem(
        @Path("id") id: Long,
        @Body request: CreateNonDatedItemRequest,
    ): Response<ApiResponse<NonDatedItemDto>>

    @DELETE("non-dated-items/{id}")
    suspend fun deleteNonDatedItem(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @POST("non-dated-items/{id}/favorite")
    suspend fun favoriteNonDatedItem(@Path("id") id: Long): Response<ApiResponse<NonDatedItemDto>>

    @DELETE("non-dated-items/{id}/favorite")
    suspend fun unfavoriteNonDatedItem(@Path("id") id: Long): Response<ApiResponse<NonDatedItemDto>>

    // ── Reminders ─────────────────────────────────────────────────────
    @GET("reminders")
    suspend fun getReminders(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("status") status: String? = null,
    ): Response<ApiResponse<List<ReminderDto>>>

    @GET("reminders/{id}")
    suspend fun getReminder(@Path("id") id: Long): Response<ApiResponse<ReminderDto>>

    @POST("reminders")
    suspend fun createReminder(@Body request: CreateReminderRequest): Response<ApiResponse<ReminderDto>>

    @PUT("reminders/{id}")
    suspend fun updateReminder(
        @Path("id") id: Long,
        @Body request: CreateReminderRequest,
    ): Response<ApiResponse<ReminderDto>>

    @DELETE("reminders/{id}")
    suspend fun deleteReminder(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @POST("reminders/{id}/snooze")
    suspend fun snoozeReminder(
        @Path("id") id: Long,
        @Body request: SnoozeReminderRequest,
    ): Response<ApiResponse<ReminderDto>>

    @POST("reminders/{id}/complete")
    suspend fun completeReminder(@Path("id") id: Long): Response<ApiResponse<ReminderDto>>

    // ── Notifications ─────────────────────────────────────────────────
    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
    ): Response<ApiResponse<List<NotificationDto>>>

    @GET("notifications/unread-count")
    suspend fun getUnreadCount(): Response<ApiResponse<UnreadCountDto>>

    @POST("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Long): Response<ApiResponse<NotificationDto>>

    @POST("notifications/read-all")
    suspend fun markAllNotificationsRead(): Response<ApiResponse<Unit>>

    // ── Settings ──────────────────────────────────────────────────────
    @GET("settings")
    suspend fun getSettings(): Response<ApiResponse<UserSettingsDto>>

    @PUT("settings")
    suspend fun updateSettings(@Body settings: UserSettingsDto): Response<ApiResponse<UserSettingsDto>>

    @GET("settings/automation")
    suspend fun getAutomationSettings(): Response<ApiResponse<AutomationSettingsDto>>

    @PUT("settings/automation")
    suspend fun updateAutomationSettings(@Body settings: AutomationSettingsDto): Response<ApiResponse<AutomationSettingsDto>>

    @GET("settings/cleanup-rules")
    suspend fun getCleanupRules(): Response<ApiResponse<List<CleanupRuleDto>>>

    @POST("settings/cleanup-rules")
    suspend fun createCleanupRule(@Body request: CreateCleanupRuleRequest): Response<ApiResponse<CleanupRuleDto>>

    @DELETE("settings/cleanup-rules/{id}")
    suspend fun deleteCleanupRule(@Path("id") id: Long): Response<ApiResponse<Unit>>
}


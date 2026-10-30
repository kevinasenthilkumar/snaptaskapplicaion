package com.example.snaptask.data.repository

import com.example.snaptask.data.network.*

class NotificationRepository(private val apiService: ApiService) {
    suspend fun getNotifications(page: Int = 1): Resource<List<NotificationDto>> =
        safeApiCall { apiService.getNotifications(page = page) }

    suspend fun getUnreadCount(): Resource<UnreadCountDto> = safeApiCall { apiService.getUnreadCount() }

    suspend fun markRead(id: Long): Resource<NotificationDto> = safeApiCall { apiService.markNotificationRead(id) }

    suspend fun markAllRead(): Resource<Unit> = safeApiCall { apiService.markAllNotificationsRead() }
}


package com.example.snaptask.data.repository

import com.example.snaptask.data.network.*

class ReminderRepository(private val apiService: ApiService) {
    suspend fun getReminders(page: Int = 1, status: String? = null): Resource<List<ReminderDto>> =
        safeApiCall { apiService.getReminders(page = page, status = status) }

    suspend fun createReminder(request: CreateReminderRequest): Resource<ReminderDto> =
        safeApiCall { apiService.createReminder(request) }

    suspend fun updateReminder(id: Long, request: CreateReminderRequest): Resource<ReminderDto> =
        safeApiCall { apiService.updateReminder(id, request) }

    suspend fun deleteReminder(id: Long): Resource<Unit> = safeApiCall { apiService.deleteReminder(id) }

    suspend fun snooze(id: Long, until: String): Resource<ReminderDto> =
        safeApiCall { apiService.snoozeReminder(id, SnoozeReminderRequest(until)) }

    suspend fun complete(id: Long): Resource<ReminderDto> = safeApiCall { apiService.completeReminder(id) }
}


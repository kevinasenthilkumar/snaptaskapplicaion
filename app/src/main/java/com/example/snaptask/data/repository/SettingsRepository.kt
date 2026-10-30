package com.example.snaptask.data.repository

import com.example.snaptask.data.network.*

class SettingsRepository(private val apiService: ApiService) {
    suspend fun getSettings(): Resource<UserSettingsDto> = safeApiCall { apiService.getSettings() }

    suspend fun updateSettings(settings: UserSettingsDto): Resource<UserSettingsDto> =
        safeApiCall { apiService.updateSettings(settings) }

    suspend fun getAutomationSettings(): Resource<AutomationSettingsDto> =
        safeApiCall { apiService.getAutomationSettings() }

    suspend fun updateAutomationSettings(settings: AutomationSettingsDto): Resource<AutomationSettingsDto> =
        safeApiCall { apiService.updateAutomationSettings(settings) }

    suspend fun getCleanupRules(): Resource<List<CleanupRuleDto>> = safeApiCall { apiService.getCleanupRules() }

    suspend fun createCleanupRule(request: CreateCleanupRuleRequest): Resource<CleanupRuleDto> =
        safeApiCall { apiService.createCleanupRule(request) }

    suspend fun deleteCleanupRule(id: Long): Resource<Unit> = safeApiCall { apiService.deleteCleanupRule(id) }
}


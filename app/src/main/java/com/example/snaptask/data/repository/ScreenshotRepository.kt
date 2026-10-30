package com.example.snaptask.data.repository

import com.example.snaptask.data.network.*

class ScreenshotRepository(private val apiService: ApiService) {
    suspend fun getScreenshots(page: Int = 1, category: String? = null, status: String? = null): Resource<List<ScreenshotDto>> =
        safeApiCall { apiService.getScreenshots(page = page, category = category, status = status) }

    suspend fun getScreenshot(id: Long): Resource<ScreenshotDto> = safeApiCall { apiService.getScreenshot(id) }

    suspend fun deleteScreenshot(id: Long): Resource<Unit> = safeApiCall { apiService.deleteScreenshot(id) }

    suspend fun analyzeScreenshot(id: Long): Resource<ScreenshotDto> = safeApiCall { apiService.analyzeScreenshot(id) }

    suspend fun getStats(): Resource<ScreenshotStatsDto> = safeApiCall { apiService.getScreenshotStats() }
}


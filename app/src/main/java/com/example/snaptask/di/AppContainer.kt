package com.example.snaptask.di

import android.content.Context
import com.example.snaptask.data.ai.LocalOcrProcessor
import com.example.snaptask.data.cleaner.MediaCleaner
import com.example.snaptask.data.local.AppDatabase
import com.example.snaptask.data.local.MediaStoreScanner
import com.example.snaptask.data.local.PreferencesManager
import com.example.snaptask.data.network.AuthInterceptor
import com.example.snaptask.data.network.RetrofitClient
import com.example.snaptask.data.repository.*

/**
 * Manual DI container — instantiated once in SnapTaskApplication.
 */
class AppContainer(context: Context) {

    val database = AppDatabase.getDatabase(context)
    val preferencesManager = PreferencesManager(context)
    val mediaStoreScanner = MediaStoreScanner(context)
    val ocrProcessor = LocalOcrProcessor(context)

    private val authInterceptor = AuthInterceptor(preferencesManager)
    private val apiService = RetrofitClient.create(authInterceptor)

    val authRepository = AuthRepository(apiService, preferencesManager, database)
    val screenshotRepository = ScreenshotRepository(context, apiService, database)
    val mediaCleaner = MediaCleaner(context, screenshotRepository)
    val datedItemRepository = DatedItemRepository(apiService)
    val nonDatedItemRepository = NonDatedItemRepository(apiService)
    val reminderRepository = ReminderRepository(apiService)
    val notificationRepository = NotificationRepository(apiService)
    val settingsRepository = SettingsRepository(apiService)
}
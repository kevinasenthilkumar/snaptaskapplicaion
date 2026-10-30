package com.example.snaptask.di

import android.content.Context
import com.example.snaptask.data.local.PreferencesManager
import com.example.snaptask.data.network.AuthInterceptor
import com.example.snaptask.data.network.RetrofitClient
import com.example.snaptask.data.repository.*

/**
 * Manual DI container — no Hilt/Dagger needed.
 * Instantiated once in SnapTaskApplication. Access via:
 *   (context.applicationContext as SnapTaskApplication).container
 */
class AppContainer(context: Context) {

    val preferencesManager = PreferencesManager(context)

    private val authInterceptor = AuthInterceptor(preferencesManager)
    private val apiService = RetrofitClient.create(authInterceptor)

    val authRepository = AuthRepository(apiService, preferencesManager)
    val screenshotRepository = ScreenshotRepository(apiService)
    val datedItemRepository = DatedItemRepository(apiService)
    val nonDatedItemRepository = NonDatedItemRepository(apiService)
    val reminderRepository = ReminderRepository(apiService)
    val notificationRepository = NotificationRepository(apiService)
    val settingsRepository = SettingsRepository(apiService)
}


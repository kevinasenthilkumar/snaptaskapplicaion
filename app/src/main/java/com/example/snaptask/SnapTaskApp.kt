package com.example.snaptask

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.snaptask.ui.cleanup.CleanupInProgressScreen
import com.example.snaptask.ui.cleanup.CleanupReviewScreen
import com.example.snaptask.ui.cleanup.CleanupSuccessScreen
import com.example.snaptask.ui.cleanup.DeleteConfirmationScreen
import com.example.snaptask.ui.components.BottomTab
import com.example.snaptask.ui.detail.DatedDetailScreen
import com.example.snaptask.ui.detail.NonDatedDetailScreen
import com.example.snaptask.ui.home.HomeDashboardScreen
import com.example.snaptask.ui.howdated.HowDatedScreen
import com.example.snaptask.ui.library.DatedScreenshotsScreen
import com.example.snaptask.ui.library.NonDatedScreenshotsScreen
import com.example.snaptask.ui.nondated.NonDatedScreen
import com.example.snaptask.ui.notifications.NotificationsScreen
import com.example.snaptask.ui.onboarding.OnboardingScreen
import com.example.snaptask.ui.permission.GalleryPermissionScreen
import com.example.snaptask.ui.recentlydeleted.RecentlyDeletedScreen
import com.example.snaptask.ui.recovery.AccessRecoveryScreen
import com.example.snaptask.ui.reminders.RemindersScreen
import com.example.snaptask.ui.scanning.ScreenshotScanningScreen
import com.example.snaptask.ui.settings.AutomationSettingsScreen
import com.example.snaptask.ui.settings.CleanupRulesScreen
import com.example.snaptask.ui.settings.PermissionsPrivacyScreen
import com.example.snaptask.ui.settings.SettingsScreen
import com.example.snaptask.ui.welcome.WelcomeScreen

private enum class Screen {
    Welcome,
    Onboarding,
    HowDated,
    NonDated,
    GalleryPermission,
    AccessRecovery,
    ScreenshotScanning,
    HomeDashboard,
    DatedScreenshots,
    NonDatedScreenshots,
    DatedDetail,
    NonDatedDetail,
    Reminders,
    Notifications,
    CleanupReview,
    DeleteConfirmation,
    CleanupInProgress,
    CleanupSuccess,
    RecentlyDeleted,
    Settings,
    AutomationSettings,
    CleanupRules,
    PermissionsPrivacy,
}

@Composable
fun SnapTaskApp() {
    var screen by rememberSaveable { mutableStateOf(Screen.Welcome) }

    fun handleBottomTab(tab: BottomTab) {
        when (tab) {
            BottomTab.Home -> screen = Screen.HomeDashboard
            BottomTab.Library -> screen = Screen.DatedScreenshots
            BottomTab.Reminders -> screen = Screen.Reminders
            BottomTab.Settings -> screen = Screen.Settings
        }
    }

    when (screen) {
        Screen.Welcome -> WelcomeScreen(
            onGetStarted = { screen = Screen.Onboarding },
        )

        Screen.Onboarding -> {
            BackHandler { screen = Screen.Welcome }
            OnboardingScreen(
                onBack = { screen = Screen.Welcome },
                onHowItWorks = { screen = Screen.HowDated },
            )
        }

        Screen.HowDated -> {
            BackHandler { screen = Screen.Onboarding }
            HowDatedScreen(
                onBack = { screen = Screen.Onboarding },
                onNext = { screen = Screen.NonDated },
            )
        }

        Screen.NonDated -> {
            BackHandler { screen = Screen.HowDated }
            NonDatedScreen(
                onBack = { screen = Screen.HowDated },
                onChooseAccess = { screen = Screen.GalleryPermission },
            )
        }

        Screen.GalleryPermission -> {
            BackHandler { screen = Screen.NonDated }
            GalleryPermissionScreen(
                onBack = { screen = Screen.NonDated },
                onAllow = { screen = Screen.ScreenshotScanning },
                onDontAllow = { screen = Screen.AccessRecovery },
            )
        }

        Screen.AccessRecovery -> {
            BackHandler { screen = Screen.GalleryPermission }
            AccessRecoveryScreen(
                onBack = { screen = Screen.GalleryPermission },
                onChooseScreenshots = { screen = Screen.ScreenshotScanning },
                onOpenSettings = { screen = Screen.ScreenshotScanning },
            )
        }

        Screen.ScreenshotScanning -> {
            BackHandler { screen = Screen.GalleryPermission }
            ScreenshotScanningScreen(
                onBack = { screen = Screen.GalleryPermission },
                onCancelScan = { screen = Screen.HomeDashboard },
            )
        }

        Screen.HomeDashboard -> {
            BackHandler { screen = Screen.ScreenshotScanning }
            HomeDashboardScreen(
                onTakeAction = { screen = Screen.CleanupReview },
                onOpenDated = { screen = Screen.DatedScreenshots },
                onOpenNonDated = { screen = Screen.NonDatedScreenshots },
                onOpenReminders = { screen = Screen.Reminders },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.DatedScreenshots -> {
            BackHandler { screen = Screen.HomeDashboard }
            DatedScreenshotsScreen(
                onBack = { screen = Screen.HomeDashboard },
                onOpenNonDatedTab = { screen = Screen.NonDatedScreenshots },
                onSelectRecord = { screen = Screen.DatedDetail },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.NonDatedScreenshots -> {
            BackHandler { screen = Screen.DatedScreenshots }
            NonDatedScreenshotsScreen(
                onBack = { screen = Screen.DatedScreenshots },
                onOpenDatedTab = { screen = Screen.DatedScreenshots },
                onSelectRecord = { screen = Screen.NonDatedDetail },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.DatedDetail -> {
            BackHandler { screen = Screen.DatedScreenshots }
            DatedDetailScreen(
                onBack = { screen = Screen.DatedScreenshots },
                onKeepThis = { screen = Screen.DatedScreenshots },
                onTakeAction = { screen = Screen.CleanupReview },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.NonDatedDetail -> {
            BackHandler { screen = Screen.NonDatedScreenshots }
            NonDatedDetailScreen(
                onBack = { screen = Screen.NonDatedScreenshots },
                onKeepThis = { screen = Screen.NonDatedScreenshots },
                onTakeAction = { screen = Screen.CleanupReview },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.Reminders -> {
            BackHandler { screen = Screen.HomeDashboard }
            RemindersScreen(
                onBack = { screen = Screen.HomeDashboard },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.Notifications -> {
            BackHandler { screen = Screen.HomeDashboard }
            NotificationsScreen(
                onBack = { screen = Screen.HomeDashboard },
                onReviewScreenshots = { screen = Screen.CleanupReview },
                onManagePreferences = { screen = Screen.Settings },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.CleanupReview -> {
            BackHandler { screen = Screen.HomeDashboard }
            CleanupReviewScreen(
                onBack = { screen = Screen.HomeDashboard },
                onDeleteConfirmed = { screen = Screen.DeleteConfirmation },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.DeleteConfirmation -> {
            BackHandler { screen = Screen.CleanupReview }
            DeleteConfirmationScreen(
                onConfirmDelete = { screen = Screen.CleanupInProgress },
                onCancel = { screen = Screen.CleanupReview },
            )
        }

        Screen.CleanupInProgress -> {
            BackHandler { screen = Screen.CleanupReview }
            CleanupInProgressScreen(
                onBack = { screen = Screen.CleanupSuccess },
            )
        }

        Screen.CleanupSuccess -> {
            BackHandler { screen = Screen.HomeDashboard }
            CleanupSuccessScreen(
                onBackToHome = { screen = Screen.HomeDashboard },
                onViewRecentlyDeleted = { screen = Screen.RecentlyDeleted },
            )
        }

        Screen.RecentlyDeleted -> {
            BackHandler { screen = Screen.Settings }
            RecentlyDeletedScreen(
                onBack = { screen = Screen.Settings },
                onRestoreAll = { screen = Screen.HomeDashboard },
                onDeleteForever = { screen = Screen.HomeDashboard },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.Settings -> {
            BackHandler { screen = Screen.HomeDashboard }
            SettingsScreen(
                onBack = { screen = Screen.HomeDashboard },
                onOpenAutomation = { screen = Screen.AutomationSettings },
                onOpenRules = { screen = Screen.CleanupRules },
                onOpenRecentlyDeleted = { screen = Screen.RecentlyDeleted },
                onOpenPermissions = { screen = Screen.PermissionsPrivacy },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.AutomationSettings -> {
            BackHandler { screen = Screen.Settings }
            AutomationSettingsScreen(
                onBack = { screen = Screen.Settings },
                onSavePreference = { screen = Screen.Settings },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.CleanupRules -> {
            BackHandler { screen = Screen.Settings }
            CleanupRulesScreen(
                onBack = { screen = Screen.Settings },
                onDone = { screen = Screen.Settings },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.PermissionsPrivacy -> {
            BackHandler { screen = Screen.Settings }
            PermissionsPrivacyScreen(
                onBack = { screen = Screen.Settings },
                onOpenPrivacyPolicy = { screen = Screen.Settings },
                onTabSelected = ::handleBottomTab,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun SnapTaskAppPreview() {
    SnapTaskApp()
}

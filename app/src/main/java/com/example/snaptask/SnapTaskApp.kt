package com.example.snaptask

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snaptask.ui.auth.CreateAccountScreen
import com.example.snaptask.ui.auth.LoginScreen
import com.example.snaptask.ui.auth.MyProfileScreen
import com.example.snaptask.ui.auth.ProfileSetupScreen
import com.example.snaptask.ui.auth.SplashScreen
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
import com.example.snaptask.viewmodel.AuthViewModel

// ─────────────────────────────────────────────────────────────────────────────
// Navigation destinations
// ─────────────────────────────────────────────────────────────────────────────
private enum class Screen {
    // ── Auth & onboarding ───────────────────────────────────────────────────
    Splash,
    Welcome,
    Login,
    CreateAccount,
    ProfileSetup,
    // ── Existing onboarding (new-user only, runs after ProfileSetup) ────────
    Onboarding,
    HowDated,
    NonDated,
    GalleryPermission,
    AccessRecovery,
    ScreenshotScanning,
    // ── Main app (authenticated) ────────────────────────────────────────────
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
    MyProfile,
}

// ─────────────────────────────────────────────────────────────────────────────
// Root composable
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SnapTaskApp() {
    val context = LocalContext.current
    val app = context.applicationContext as SnapTaskApplication

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(app.container.authRepository),
    )
    val authState by authViewModel.uiState.collectAsState()

    var screen by rememberSaveable { mutableStateOf(Screen.Splash) }

    // ── Session resolved from Splash ─────────────────────────────────────────
    // When AuthViewModel has finished its session check, redirect appropriately.
    LaunchedEffect(authState.isLoggedIn) {
        // Only act once we are still on the Splash screen.
        if (screen == Screen.Splash) {
            screen = if (authState.isLoggedIn) Screen.HomeDashboard else Screen.Splash
        }
    }

    fun handleBottomTab(tab: BottomTab) {
        screen = when (tab) {
            BottomTab.Home -> Screen.HomeDashboard
            BottomTab.Library -> Screen.DatedScreenshots
            BottomTab.Reminders -> Screen.Reminders
            BottomTab.Settings -> Screen.Settings
        }
    }

    // Helper: log out and clear back-stack by going to Welcome
    fun logout() {
        authViewModel.logout()
        screen = Screen.Welcome
    }

    // ── Screen routing ────────────────────────────────────────────────────────
    when (screen) {

        // ── Splash ────────────────────────────────────────────────────────────
        Screen.Splash -> SplashScreen(
            onAuthenticated = { screen = Screen.HomeDashboard },
            onUnauthenticated = {
                // After splash delay: check real auth state
                screen = if (authState.isLoggedIn) Screen.HomeDashboard else Screen.Welcome
            },
        )

        // ── Welcome ───────────────────────────────────────────────────────────
        Screen.Welcome -> {
            // No back handler — Welcome is the root for unauthenticated users.
            WelcomeScreen(
                onGetStarted = { screen = Screen.CreateAccount },
                onLogin = { screen = Screen.Login },
            )
        }

        // ── Login ─────────────────────────────────────────────────────────────
        Screen.Login -> {
            BackHandler { screen = Screen.Welcome }
            LoginScreen(
                onBack = { screen = Screen.Welcome },
                onLoginSuccess = { screen = Screen.HomeDashboard },
                onNavigateToCreateAccount = { screen = Screen.CreateAccount },
                isLoading = authState.isLoading,
                errorMessage = authState.error,
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
            )
            // Navigate to Home once login succeeds
            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) screen = Screen.HomeDashboard
            }
        }

        // ── Create Account ────────────────────────────────────────────────────
        Screen.CreateAccount -> {
            BackHandler { screen = Screen.Welcome }
            CreateAccountScreen(
                onBack = { screen = Screen.Welcome },
                onAccountCreated = { screen = Screen.ProfileSetup },
                onNavigateToLogin = { screen = Screen.Login },
                isLoading = authState.isLoading,
                errorMessage = authState.error,
                onRegister = { name, email, password ->
                    authViewModel.register(name, email, password)
                },
            )
            // After successful registration move to ProfileSetup
            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) screen = Screen.ProfileSetup
            }
        }

        // ── Profile Setup (first-time registration only) ───────────────────────
        Screen.ProfileSetup -> {
            BackHandler { /* intentionally blocked — can't go back to registration */ }
            ProfileSetupScreen(
                onBack = { /* blocked */ },
                onProfileSaved = {
                    authViewModel.setOnboardingDone()
                    screen = Screen.Onboarding
                },
                onSkip = {
                    authViewModel.setOnboardingDone()
                    screen = Screen.Onboarding
                },
            )
        }

        // ── Existing Onboarding (new users, after ProfileSetup) ──────────────
        Screen.Onboarding -> {
            BackHandler { /* blocked — cannot go back from onboarding to registration */ }
            OnboardingScreen(
                onBack = { /* blocked */ },
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

        // ── Main App (authenticated) ──────────────────────────────────────────
        Screen.HomeDashboard -> {
            // Prevent going back to auth screens after login
            BackHandler { /* blocked — Home is the authenticated root */ }
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
                // Pass profile entry point to Settings
                onOpenProfile = { screen = Screen.MyProfile },
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

        // ── My Profile (account management — not registration) ────────────────
        Screen.MyProfile -> {
            BackHandler { screen = Screen.Settings }
            MyProfileScreen(
                onBack = { screen = Screen.Settings },
                onLogout = ::logout,
                userName = authState.userName ?: "",
                userEmail = authState.userEmail ?: "",
            )
        }
    }
}

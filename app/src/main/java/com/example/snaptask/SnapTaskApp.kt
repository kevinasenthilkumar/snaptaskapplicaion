package com.example.snaptask

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.snaptask.ui.components.formatFileSize
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
import com.example.snaptask.viewmodel.DatedItemsViewModel
import com.example.snaptask.viewmodel.HomeViewModel
import com.example.snaptask.viewmodel.NonDatedItemsViewModel
import com.example.snaptask.viewmodel.ScreenshotViewModel

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
    val scope = rememberCoroutineScope()

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(app.container.authRepository),
    )
    val authState by authViewModel.uiState.collectAsState()

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
            app.container.screenshotRepository,
            app.container.datedItemRepository,
            app.container.notificationRepository,
        )
    )

    val datedViewModel: DatedItemsViewModel = viewModel(
        factory = DatedItemsViewModel.Factory(app.container.datedItemRepository)
    )
    val datedState by datedViewModel.uiState.collectAsState()

    val nonDatedViewModel: NonDatedItemsViewModel = viewModel(
        factory = NonDatedItemsViewModel.Factory(app.container.nonDatedItemRepository)
    )
    val nonDatedState by nonDatedViewModel.uiState.collectAsState()
    val homeState by homeViewModel.uiState.collectAsState()

    val screenshotViewModel: ScreenshotViewModel = viewModel(
        factory = ScreenshotViewModel.Factory(
            app.container.screenshotRepository,
            app.container.ocrProcessor,
        )
    )
    val scanState by screenshotViewModel.uiState.collectAsState()
    val allScreenshots by screenshotViewModel.allScreenshots.collectAsState()
    val totalCount by screenshotViewModel.totalCount.collectAsState()

    var screen by rememberSaveable { mutableStateOf(Screen.Splash) }
    var pendingDeleteIds by remember { mutableStateOf<List<Long>>(emptyList()) }
    var pendingDeleteCategoryName by remember { mutableStateOf<String?>(null) }
    var pendingDeleteSizeFormatted by remember { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var selectedScreenshotId by rememberSaveable { mutableStateOf<Long?>(null) }
    var isIntroFromSettings by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val trashLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            if (pendingDeleteIds.isNotEmpty()) {
                coroutineScope.launch {
                    app.container.mediaCleaner.onTrashConfirmed(pendingDeleteIds)
                    pendingDeleteIds = emptyList()
                    pendingDeleteCategoryName = null
                }
            }
        }
    }

    val permanentDeleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            if (pendingDeleteIds.isNotEmpty()) {
                coroutineScope.launch {
                    app.container.mediaCleaner.onPermanentDeleteConfirmed(pendingDeleteIds)
                    pendingDeleteIds = emptyList()
                    pendingDeleteCategoryName = null
                }
            }
        }
    }

    fun requestDeleteScreenshot(id: Long) {
        val target = allScreenshots.firstOrNull { it.id == id } ?: return
        pendingDeleteCategoryName = null
        pendingDeleteIds = listOf(id)
        pendingDeleteSizeFormatted = formatFileSize(target.size)
        screen = Screen.DeleteConfirmation
    }

    fun requestDeleteCategory(categoryName: String) {
        val targets = allScreenshots.filter { it.category.equals(categoryName, ignoreCase = true) }
        if (targets.isEmpty()) return
        pendingDeleteCategoryName = categoryName
        pendingDeleteIds = targets.map { it.id }
        pendingDeleteSizeFormatted = formatFileSize(targets.sumOf { it.size })
        screen = Screen.DeleteConfirmation
    }

    fun hasPhotoPermission(): Boolean {
        val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val granted = ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        val partial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED
        } else false
        return granted || partial
    }

    fun onStartScanFlow() {
        if (hasPhotoPermission()) {
            screenshotViewModel.startScanning()
            screen = Screen.ScreenshotScanning
        } else {
            screen = Screen.GalleryPermission
        }
    }

    // ── Session resolved from Splash ─────────────────────────────────────────
    LaunchedEffect(authState.isLoggedIn) {
        if (screen == Screen.Splash) {
            screen = if (authState.isLoggedIn) Screen.HomeDashboard else Screen.Splash
        }
    }

    fun handleBottomTab(tab: BottomTab) {
        screen = when (tab) {
            BottomTab.Home -> Screen.HomeDashboard
            BottomTab.Library -> {
                selectedCategory = "All"
                Screen.DatedScreenshots
            }
            BottomTab.Settings -> Screen.Settings
        }
    }

    fun logout() {
        authViewModel.logout()
        screen = Screen.Welcome
    }

    // ── Screen routing ────────────────────────────────────────────────────────
    when (screen) {

        Screen.Splash -> SplashScreen(
            onAuthenticated = { screen = Screen.HomeDashboard },
            onUnauthenticated = {
                screen = if (authState.isLoggedIn) Screen.HomeDashboard else Screen.Welcome
            },
        )

        Screen.Welcome -> {
            WelcomeScreen(
                onGetStarted = {
                    if (!authState.hasSeenIntro) {
                        isIntroFromSettings = false
                        screen = Screen.Onboarding
                    } else {
                        screen = Screen.CreateAccount
                    }
                },
                onLogin = { screen = Screen.Login },
            )
        }

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
            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) screen = Screen.HomeDashboard
            }
        }

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
            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) screen = Screen.ProfileSetup
            }
        }

        Screen.ProfileSetup -> {
            BackHandler { /* blocked */ }
            ProfileSetupScreen(
                onBack = { /* blocked */ },
                onProfileSaved = {
                    authViewModel.setOnboardingDone()
                    screen = Screen.HomeDashboard
                },
                onSkip = {
                    authViewModel.setOnboardingDone()
                    screen = Screen.HomeDashboard
                },
            )
        }

        Screen.Onboarding -> {
            BackHandler {
                if (isIntroFromSettings) screen = Screen.Settings
                else screen = Screen.Welcome
            }
            OnboardingScreen(
                onBack = {
                    if (isIntroFromSettings) screen = Screen.Settings
                    else screen = Screen.Welcome
                },
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
                onChooseAccess = {
                    authViewModel.setIntroSeen()
                    if (isIntroFromSettings) {
                        screen = Screen.Settings
                    } else {
                        screen = Screen.CreateAccount
                    }
                },
            )
        }

        Screen.GalleryPermission -> {
            BackHandler { screen = Screen.HomeDashboard }
            GalleryPermissionScreen(
                onBack = { screen = Screen.HomeDashboard },
                onAllow = {
                    screenshotViewModel.startScanning()
                    screen = Screen.ScreenshotScanning
                },
                onDontAllow = { screen = Screen.AccessRecovery },
            )
        }

        Screen.AccessRecovery -> {
            BackHandler { screen = Screen.HomeDashboard }
            AccessRecoveryScreen(
                onBack = { screen = Screen.HomeDashboard },
                onChooseScreenshots = { onStartScanFlow() },
                onOpenSettings = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
            )
        }

        Screen.ScreenshotScanning -> {
            BackHandler { screen = Screen.HomeDashboard }
            ScreenshotScanningScreen(
                onBack = { screen = Screen.HomeDashboard },
                onCancelScan = { screen = Screen.HomeDashboard },
                scanUiState = scanState,
                onStartScan = { screenshotViewModel.startScanning() },
                onScanComplete = {
                    datedViewModel.loadItems()
                    nonDatedViewModel.loadItems()
                    homeViewModel.loadDashboard()
                    screen = Screen.HomeDashboard
                },
            )
        }

        Screen.HomeDashboard -> {
            BackHandler { /* Home is root, exits app cleanly */ }
            HomeDashboardScreen(
                totalCount = totalCount,
                screenshots = allScreenshots,
                onStartScan = ::onStartScanFlow,
                onOpenCategory = { cat ->
                    selectedCategory = cat
                    screen = Screen.DatedScreenshots
                },
                onDeleteCategory = ::requestDeleteCategory,
                onSelectScreenshot = { id ->
                    selectedScreenshotId = id
                    val item = allScreenshots.find { it.id == id }
                    screen = if (item?.eventDate != null) Screen.DatedDetail else Screen.NonDatedDetail
                },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.DatedScreenshots -> {
            BackHandler { screen = Screen.HomeDashboard }
            DatedScreenshotsScreen(
                items = allScreenshots,
                initialCategory = selectedCategory,
                onBack = { screen = Screen.HomeDashboard },
                onOpenNonDatedTab = {},
                onSelectRecord = { id ->
                    selectedScreenshotId = id
                    val item = allScreenshots.find { it.id == id }
                    screen = if (item?.eventDate != null) Screen.DatedDetail else Screen.NonDatedDetail
                },
                onDeleteRecord = ::requestDeleteScreenshot,
                onDeleteCategory = ::requestDeleteCategory,
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.NonDatedScreenshots -> {
            BackHandler { screen = Screen.DatedScreenshots }
            NonDatedScreenshotsScreen(
                items = nonDatedState.items,
                onBack = { screen = Screen.DatedScreenshots },
                onOpenDatedTab = { screen = Screen.DatedScreenshots },
                onSelectRecord = { id ->
                    selectedScreenshotId = id
                    screen = Screen.NonDatedDetail
                },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.DatedDetail -> {
            BackHandler { screen = Screen.DatedScreenshots }
            val selectedScreenshot = allScreenshots.find { it.id == selectedScreenshotId }
            DatedDetailScreen(
                screenshot = selectedScreenshot,
                onBack = { screen = Screen.DatedScreenshots },
                onKeepThis = { screen = Screen.DatedScreenshots },
                onDelete = ::requestDeleteScreenshot,
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.NonDatedDetail -> {
            BackHandler { screen = Screen.DatedScreenshots }
            val selectedScreenshot = allScreenshots.find { it.id == selectedScreenshotId }
            NonDatedDetailScreen(
                screenshot = selectedScreenshot,
                onBack = { screen = Screen.DatedScreenshots },
                onKeepThis = { screen = Screen.DatedScreenshots },
                onDelete = ::requestDeleteScreenshot,
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
                screenshots = allScreenshots,
                onBack = { screen = Screen.HomeDashboard },
                onDeleteConfirmed = { ids ->
                    val targets = allScreenshots.filter { it.id in ids }
                    pendingDeleteIds = ids
                    pendingDeleteCategoryName = null
                    pendingDeleteSizeFormatted = formatFileSize(targets.sumOf { it.size })
                    screen = Screen.DeleteConfirmation
                },
                onTabSelected = ::handleBottomTab,
            )
        }

        Screen.DeleteConfirmation -> {
            BackHandler {
                pendingDeleteIds = emptyList()
                pendingDeleteCategoryName = null
                screen = Screen.HomeDashboard
            }
            DeleteConfirmationScreen(
                count = pendingDeleteIds.size,
                categoryName = pendingDeleteCategoryName,
                sizeFormatted = pendingDeleteSizeFormatted,
                onConfirmDelete = {
                    val targets = allScreenshots.filter { it.id in pendingDeleteIds }
                    val uris = targets.map { Uri.parse(it.contentUri) }
                    val launched = app.container.mediaCleaner.requestTrash(uris, trashLauncher)
                    if (!launched) {
                        coroutineScope.launch {
                            app.container.mediaCleaner.onTrashConfirmed(pendingDeleteIds)
                            pendingDeleteIds = emptyList()
                            pendingDeleteCategoryName = null
                        }
                    }
                    screen = Screen.HomeDashboard
                },
                onCancel = {
                    pendingDeleteIds = emptyList()
                    pendingDeleteCategoryName = null
                    screen = Screen.HomeDashboard
                },
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
            val trashedItems = allScreenshots.filter { it.status == "trashed" }
            RecentlyDeletedScreen(
                trashedItems = trashedItems,
                onBack = { screen = Screen.Settings },
                onRestoreAll = {
                    val ids = trashedItems.map { it.id }
                    if (ids.isNotEmpty()) {
                        coroutineScope.launch {
                            app.container.mediaCleaner.onRestoreConfirmed(ids)
                        }
                    }
                    screen = Screen.Settings
                },
                onDeleteForever = {
                    val ids = trashedItems.map { it.id }
                    if (ids.isNotEmpty()) {
                        val uris = trashedItems.map { Uri.parse(it.contentUri) }
                        pendingDeleteIds = ids
                        val launched = app.container.mediaCleaner.requestPermanentDelete(uris, permanentDeleteLauncher)
                        if (!launched) {
                            coroutineScope.launch {
                                app.container.mediaCleaner.onPermanentDeleteConfirmed(ids)
                                pendingDeleteIds = emptyList()
                            }
                        }
                    }
                    screen = Screen.Settings
                },
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
                onOpenHowItWorks = {
                    isIntroFromSettings = true
                    screen = Screen.Onboarding
                },
                onTabSelected = ::handleBottomTab,
                onOpenProfile = { screen = Screen.MyProfile },
                onLogout = ::logout,
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

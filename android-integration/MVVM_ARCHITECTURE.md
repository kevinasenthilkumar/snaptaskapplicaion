# SnapTask Android Architecture & MVVM Integration Guide

## 1. Architectural Philosophy

SnapTask follows the official Google Android Architecture recommendations: **Clean Architecture + Unidirectional Data Flow (UDF) + MVVM** using **Jetpack Compose**.

```
┌─────────────────────────────────────────────────────────────────┐
│                           UI LAYER                              │
│   Compose Screens (Stateless) ──> StateFlow ──> ViewModels      │
└────────────────────────────────┬────────────────────────────────┘
                                 │ Observes Domain Models
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                         DOMAIN LAYER                            │
│   Use Cases (Interactors) ──> Domain Models & Repo Interfaces   │
└────────────────────────────────┬────────────────────────────────┘
                                 │ Coordinates Data
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                          DATA LAYER                             │
│   Repository Impls ──> Remote (Retrofit) + Local (Room/DataStore)│
│   + Background Sync (WorkManager)                               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Strict Architectural Rules

1. **Compose Screens do not touch Data/Network layers directly**:
   * Screens only observe `StateFlow<UiState>` exposed by their ViewModel.
   * User interactions (clicks, toggles, text edits) are emitted upward as strongly-typed events (e.g. `HomeEvent.OnTakeActionClicked`).

2. **DTOs must never leak into Compose UI**:
   * Remote Network DTOs (`*Dto.kt`) and Local Room Entities (`*Entity.kt`) are mapped to clean Kotlin Domain Models (`DatedScreenshot`, `NonDatedScreenshot`, `ReminderItem`) before reaching the ViewModel and UI.

3. **Offline-First Cache**:
   * The UI is populated from local Room database tables immediately.
   * Network requests fetch fresh data from the PHP API, update the local Room database, and automatically reflect in the UI via reactive Flows.

4. **Background Upload & Synchronization**:
   * Long-running screenshot uploads and OCR analysis use Android `WorkManager` (`ScreenshotUploadWorker`). This guarantees execution even if the app process is paused, killed, or the device restarts.

---

## 3. UI State Management with UDF

Each screen defines an immutable UI state data class and a sealed event interface:

```kotlin
// UI State (Single source of truth for the screen)
data class HomeDashboardUiState(
    val isLoading: Boolean = false,
    val totalScreenshots: Int = 0,
    val cleanableCount: Int = 0,
    val reclaimableMb: Double = 0.0,
    val overdueRemindersCount: Int = 0,
    val upcomingRemindersCount: Int = 0,
    val recentScreenshots: List<ScreenshotItem> = emptyList(),
    val errorMessage: String? = null,
)

// UI Events emitted by Compose to ViewModel
sealed interface HomeEvent {
    object Refresh : HomeEvent
    data class OnSearchQueryChanged(val query: String) : HomeEvent
    object OnTakeActionClicked : HomeEvent
    data class OnScreenshotSelected(val id: Long) : HomeEvent
}
```

---

## 4. WorkManager & Background Sync

* **Screenshot Upload & AI Ingestion**:
  `ScreenshotUploadWorker` is triggered when the user grants gallery permission or takes new screenshots. It sends multipart file chunks to `POST /api/v1/screenshots/upload` and triggers analysis via `POST /api/v1/screenshots/{id}/analyze`.
* **Periodic Cleanup Notifications**:
  `PeriodicWorkRequestBuilder<ReminderSyncWorker>(24, TimeUnit.HOURS)` syncs upcoming reminder notifications at the user's preferred daily time (e.g., `09:00`).

---

## 5. Security & Authentication Integration

1. Access tokens are kept in encrypted storage via AndroidX **DataStore**.
2. OkHttp `AuthInterceptor` automatically attaches `Authorization: Bearer <token>` to all protected endpoints.
3. An `Authenticator` interceptor captures HTTP `401 Unauthorized` responses and silently requests a new token pair using `POST /api/v1/auth/refresh`. If refresh fails, it clears local state and navigates to the Welcome screen.


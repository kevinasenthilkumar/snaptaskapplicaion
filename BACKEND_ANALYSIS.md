# SnapTask — Backend & AI Architecture Analysis

## 1. Executive Summary

**SnapTask** is a native Android application built with Kotlin and Jetpack Compose. Its core value proposition is turning screenshots into structured, actionable items while systematically clearing screenshot clutter.

Screenshots are separated into two distinct operational paradigms:
1. **Dated Items**: Time-sensitive screenshots with explicit due dates or event dates (e.g., flight tickets, event passes, bills, medical reservations, appointment notices).
2. **Non-Dated Items**: Knowledge and reference captures without hard calendar deadlines (e.g., recipes, design ideas, fashion/shopping wishlists, receipts, articles).

The current Android client contains a complete, high-fidelity UI prototype across 23 screens, matching the designs in `design/snaptask prototype/`. Currently, the navigation state machine and screen views run on local mocked state. This document analyzes the data contracts, operational rules, and API endpoints necessary to back the entire UI experience with a scalable PHP 8.2+ REST backend, MySQL 8 database, and Python FastAPI AI microservice.

---

## 2. Existing Android Architecture & Design Tokens

### 2.1 UI Stack & Entry Point
* **Entry Point**: `MainActivity.kt` initializes an edge-to-edge layout via `enableEdgeToEdge()` and embeds `SnapTaskApp()`.
* **State Machine & Navigation**: `SnapTaskApp.kt` manages an explicit state enum `Screen` with `rememberSaveable { mutableStateOf(Screen.Welcome) }`. Android system back-presses are captured per screen using `BackHandler`.
* **Bottom Navigation**: Tabs defined in `BottomTab` (`Home`, `Library`, `Reminders`, `Settings`).

### 2.2 Design System Tokens (`snaptaskdesign.kt`)
* **Color Palette**:
  * Background Warm Cream: `#FAF6F0`
  * Primary Burgundy: `#721D38`
  * Text Primary (Dark Plum): `#2B2025`
  * Text Secondary (Warm Muted): `#796B70`
  * Surface White: `#FFFFFF`
  * Accent Pill Background: `#F8EDF0`
  * Accent Subtitle / Muted: `#E5B6C3`
  * Dark Card / Ticket Background: `#3C1524`
* **Typography**:
  * `InterFamily` (`inter_regular.ttf`, `inter_semibold.ttf`)
  * `Headline` (40sp / 34sp / 32sp / 28sp)
  * `AppBarTitle` (18sp)
  * `Body` (16sp / 14sp)
  * `Caption` / `RecordMeta` (12sp / 11sp)
  * `ButtonLabel` (15sp)

---

## 3. Screen-by-Screen Data & API Requirements Analysis

| # | Screen Name | Kotlin Source File | UI Purpose & Features | Data Needed from Backend / Client | API Endpoints Required |
|---|-------------|--------------------|-----------------------|------------------------------------|------------------------|
| 1 | **Welcome** | `ui/welcome/welcomescreen.kt` | Splash & value proposition | Static assets, auth state check | `GET /auth/me` |
| 2 | **Onboarding** | `ui/onboarding/onboardingscreen.kt` | Feature introduction | Feature flags, introductory copy | `GET /settings` |
| 3 | **How Dated Works** | `ui/howdated/howdatedscreen.kt` | Explains 1-day deletion & notification rule for tickets/events | Explanation copy & rule definitions | `GET /settings/cleanup-rules` |
| 4 | **Non-Dated Guide** | `ui/nondated/nondatedscreen.kt` | Explains 7-day deletion & 6-day notification rule for ideas/recipes | Explanation copy & rule definitions | `GET /settings/cleanup-rules` |
| 5 | **Gallery Permission** | `ui/permission/gallerypermissionscreen.kt` | Android photo access request | Permission state sync | `PUT /settings/privacy` |
| 6 | **Access Recovery** | `ui/recovery/AccessRecoveryScreen.kt` | Fallback if gallery permission is denied | Permission status & fallback guides | `GET /settings/privacy` |
| 7 | **Screenshot Scanning** | `ui/scanning/ScreenshotScanningScreen.kt` | Progress visualization of screenshot ingestion & OCR | Real-time scan progress, total files scanned, extracted items preview | `POST /screenshots/upload`<br>`POST /screenshots/bulk-analyze`<br>`POST /ai/analyze-screenshot` |
| 8 | **Home Dashboard** | `ui/home/HomeDashboardScreen.kt` | Main hub: cleanable space, actionable items, dated/non-dated counts, upcoming items, search bar | Total stats, cleanable count & MB, upcoming reminders, recent items breakdown | `GET /dashboard`<br>`GET /dashboard/summary`<br>`GET /screenshots/search` |
| 9 | **Dated Screenshots** | `ui/library/DatedScreenshotsScreen.kt` | Chronological list of time-sensitive screenshots sorted by event date with filter chips (Tickets, Travel, Bookings) | List of dated screenshots, filter tags, pagination, search query | `GET /dated-items`<br>`GET /dated-items/this-week`<br>`GET /dated-items/upcoming` |
| 10 | **Non-Dated Screenshots** | `ui/library/NonDatedScreenshotsScreen.kt` | List of reference screenshots sorted by saved date with chips (Shopping, Recipes, Receipts) | List of non-dated screenshots, tag filters, sorting (oldest first) | `GET /non-dated-items`<br>`GET /non-dated-items/search` |
| 11 | **Dated Detail** | `ui/detail/DatedDetailScreen.kt` | Detail card: detected event date/time, notify date, auto-delete date, "Keep this" & "Take Action" | Item details, OCR text, notification schedule, retention status, image URI | `GET /dated-items/{id}`<br>`PUT /dated-items/{id}`<br>`POST /dated-items/{id}/complete` |
| 12 | **Non-Dated Detail** | `ui/detail/NonDatedDetailScreen.kt` | Detail card: saved date, notify date, auto-delete date, category picker, "Keep this" & "Take Action" | Item details, content text, tags, favorite toggle, image URI | `GET /non-dated-items/{id}`<br>`PUT /non-dated-items/{id}`<br>`POST /non-dated-items/{id}/favorite` |
| 13 | **Reminders** | `ui/reminders/RemindersScreen.kt` | Filterable reminders (All, Overdue, Today, This week, Upcoming) | Grouped reminders with status, deadline, action targets | `GET /reminders`<br>`POST /reminders/{id}/snooze`<br>`POST /reminders/{id}/complete` |
| 14 | **Notifications** | `ui/notifications/NotificationsScreen.kt` | Notification log (e.g. "Two screenshots expire tomorrow", space alerts) | Unread count, notification items, read status updates | `GET /notifications`<br>`POST /notifications/{id}/read`<br>`POST /notifications/read-all` |
| 15 | **Cleanup Review** | `ui/cleanup/CleanupReviewScreen.kt` | Selectable list of screenshots eligible for cleanup (grouped by dated & non-dated) | Cleanable list, item size (MB), reason for deletion, total reclaimable size | `GET /cleanup/recommendations`<br>`POST /cleanup/review` |
| 16 | **Delete Confirmation** | `ui/cleanup/DeleteConfirmationScreen.kt` | Confirmation modal ("Delete X screenshots?", mentions 30-day recovery) | Confirmation payload, list of selected screenshot IDs | `POST /cleanup/confirm` |
| 17 | **Cleanup In Progress** | `ui/cleanup/CleanupInProgressScreen.kt` | Processing animation while moving files to trash | Async cleanup job status or immediate response | `GET /cleanup/jobs/{id}` |
| 18 | **Cleanup Success** | `ui/cleanup/CleanupSuccessScreen.kt` | Reclaimed MB confirmation, link to Recently Deleted | Summary of completed job (deleted count, MB freed) | `GET /cleanup/history` |
| 19 | **Recently Deleted** | `ui/recentlydeleted/RecentlyDeletedScreen.kt` | Trash bin with 30-day retention indicator, "Restore all" and "Delete forever" actions | List of trashed items with remaining days, restore/permanent delete endpoints | `GET /trash`<br>`POST /trash/restore-all`<br>`POST /trash/{id}/restore`<br>`DELETE /trash/{id}`<br>`POST /trash/empty` |
| 20 | **Settings** | `ui/settings/SettingsScreen.kt` | Notification switches (Cleanup reminders, Cleanup summaries), navigation to sub-settings | User settings preferences | `GET /settings`<br>`PUT /settings` |
| 21 | **Automation Settings** | `ui/settings/AutomationSettingsScreen.kt` | Cleanup mode: Daily automation (09:00) vs Never run automatically (Manual) | Automation mode, scheduled time, consent flags | `GET /settings/automation`<br>`PUT /settings/automation` |
| 22 | **Cleanup Rules** | `ui/settings/CleanupRulesScreen.kt` | Displays dated rules (1-day post-event), non-dated rules (7-day post-save), "Protect kept screenshots" toggle | Retention rules configuration, protection toggles | `GET /settings/cleanup-rules`<br>`PUT /settings/cleanup-rules/{id}` |
| 23 | **Permissions & Privacy** | `ui/settings/PermissionsPrivacyScreen.kt` | Toggles for "AI organization" (OCR/categorization) and "Share anonymous analytics" | Privacy preferences & permission tracking | `GET /settings/privacy`<br>`PUT /settings/privacy` |

---

## 4. Business Logic & Domain Rules Extracted from UI

1. **Classification Pipeline**:
   * A screenshot uploaded by the Android client is processed through OCR and a classifier.
   * If recognized as an event, ticket, reservation, bill, or appointment with a discernible calendar date $\rightarrow$ categorized as `dated`.
   * If recognized as a recipe, article, product/fashion shopping item, or general idea $\rightarrow$ categorized as `non_dated`.
   * If ambiguous or text-sparse $\rightarrow$ categorized as `unclassified` for manual review.

2. **Retention & Notification Engine**:
   * **Dated items rule**: Scheduled for deletion **1 day after** the detected `event_date`. A notification reminder is triggered **on the event date** (1 day before deletion).
   * **Non-dated items rule**: Scheduled for deletion **7 days after** `captured_at` / `uploaded_at`. A notification reminder is triggered **on day 6** (1 day before deletion).
   * **Kept Protection (`is_kept`)**: Any screenshot or item where the user selects "Keep this" is flagged with `is_kept = 1` and MUST be excluded from cleanup recommendations and automated deletions.

3. **Automation Modes**:
   * `manual` (Default): Never delete automatically. Requires user to review eligible items and confirm "Take Action".
   * `daily`: Runs automated scans and flags items at a specified daily time (e.g. `09:00`).

4. **Trash & Recovery Lifecycle**:
   * Moving an item to cleanup does not permanently destroy it immediately; it moves to `trash_items` with a `deleted_at` timestamp.
   * Items remain in the recovery safety net for **30 days**.
   * Within 30 days, users can single-restore, bulk-restore (`restore-all`), or permanently purge (`delete forever` / `empty`).

---

## 5. System Architecture Blueprint

```
 ┌────────────────────────────────────────────────────────┐
 │            Android Client (Jetpack Compose)            │
 │   UI Layer  ──>  ViewModel  ──>  Domain / Repository   │
 └───────────────────────────┬────────────────────────────┘
                             │ HTTPS / REST (JSON)
                             ▼
 ┌────────────────────────────────────────────────────────┐
 │           SnapTask PHP Backend (PHP 8.2+ REST)         │
 │  Routes ➔ Middleware ➔ Controller ➔ Service ➔ Repo     │
 └─────────────┬────────────────────────────┬─────────────┘
               │ Internal HTTP (curl)       │ PDO / SQL
               ▼                            ▼
 ┌───────────────────────────┐  ┌─────────────────────────┐
 │   Python AI Microservice  │  │   MySQL 8.0 Database    │
 │   FastAPI + Tesseract OCR │  │  (InnoDB, utf8mb4,     │
 │   + NLP / Heuristics      │  │   Normalized Relational)│
 └───────────────────────────┘  └─────────────────────────┘
```

---

## 6. Identified Technical Gaps to Resolve

1. **State Persistence**: Currently, in-memory Compose `rememberSaveable` variables reset on process recreation. The backend will provide authoritative state across devices and sessions.
2. **File & Media Handling**: Media paths on device must be linked with server-side media assets (`local_media_id` $\leftrightarrow$ `file_path`).
3. **OCR & Entity Extraction**: The app requires automated extraction of date, time, title, location, and amount from unstructured image text.
4. **Perceptual Hashing / Deduplication**: Duplicate screenshots need detection to recommend cleanup without manual scrutiny.

---
*Analysis completed for Phase 1. Ready for Implementation Plan.*


# SnapTask Backend & AI Layer — Completion Report

## 1. Executive Summary

We have built a production-ready, modular, and secure backend foundation and AI classification microservice for **SnapTask**. The system supports all **23 screens** and workflows of the Android client prototype without modifying the existing Jetpack Compose UI code.

The backend infrastructure is split into three clean layers:
1. **PHP 8.2+ REST API (`snaptask-backend/`)**: PSR-4 layered architecture with custom lightweight router, PDO database management, JWT authentication, sliding window rate limiting, file upload validation, structured logging, and standardized JSON envelopes.
2. **Python 3.10+ AI Microservice (`snaptask-ai/`)**: FastAPI service with image preprocessing, perceptual hashing (`dHash`) for screenshot deduplication, configurable Tesseract OCR, regex + `dateparser` date/time extraction, hybrid keyword density classifier (Dated vs Non-Dated), and risk-aware cleanup recommendations.
3. **Android Clean MVVM Contracts (`android-integration/`)**: Complete architectural specifications, API contracts, Retrofit interfaces, DTOs, Room cache entities, WorkManager workers, and ViewModel/StateFlow integration samples.

---

## 2. Directory Catalog & Files Created

### 2.1 Backend Workspace (`snaptask-backend/`)
* **Core Framework**:
  * `app/Core/Router.php`: REST routing with parameterized paths (`{id}`) and middleware pipeline.
  * `app/Core/Request.php`: HTTP request abstraction (JSON body, query params, files, headers, user).
  * `app/Core/Response.php`: Standardized JSON response builder (`success`, `message`, `data`, `errors`, `meta`).
  * `app/Core/Database.php`: Singleton PDO connection manager with transaction helper.
  * `app/Core/Jwt.php`: RFC 7519 HMAC-SHA256 JWT encoder, decoder, and verifier.
  * `app/Core/Logger.php`: Structured daily rotating log engine.
  * `app/Core/ErrorHandler.php`: Global exception handler shielding production stack traces.
* **Middleware**:
  * `app/Middleware/CorsMiddleware.php`: Cross-Origin headers and automatic preflight handling.
  * `app/Middleware/AuthMiddleware.php`: Bearer JWT token verification and user injection.
  * `app/Middleware/RateLimitMiddleware.php`: Sliding window rate limiter (120 req / 60 sec).
* **Helpers & Validators**:
  * `app/Helpers/Validator.php`: Fluent validation engine supporting 10+ rules.
  * `app/Helpers/FileUpload.php`: Safe MIME/extension verification, unique naming, and secure storage.
  * `app/Helpers/HashHelper.php`: SHA-256 and difference hash calculation with Hamming distance.
  * `app/Validators/`: `AuthValidator.php`, `ScreenshotValidator.php`, `DatedItemValidator.php`, `NonDatedItemValidator.php`, `ReminderValidator.php`, `SettingsValidator.php`, `CleanupValidator.php`.
* **Models**:
  * `app/Models/`: `User.php`, `Screenshot.php`, `DatedItem.php`, `NonDatedItem.php`, `Reminder.php`, `Notification.php`, `CleanupJob.php`, `TrashItem.php`, `UserSettings.php`, `CleanupRule.php`.
* **Repositories**:
  * `app/Repositories/`: `UserRepository.php`, `ScreenshotRepository.php`, `DatedItemRepository.php`, `NonDatedItemRepository.php`, `ReminderRepository.php`, `NotificationRepository.php`, `CleanupRepository.php`, `TrashRepository.php`, `DashboardRepository.php`, `SettingsRepository.php`.
* **Services**:
  * `app/Services/`: `AuthService.php`, `ScreenshotService.php`, `DatedItemService.php`, `NonDatedItemService.php`, `ReminderService.php`, `NotificationService.php`, `CleanupService.php`, `TrashService.php`, `DashboardService.php`, `SettingsService.php`, `AiServiceClient.php`.
* **Controllers**:
  * `app/Controllers/`: `AuthController.php`, `ScreenshotController.php`, `DatedItemController.php`, `NonDatedItemController.php`, `ReminderController.php`, `NotificationController.php`, `CleanupController.php`, `TrashController.php`, `DashboardController.php`, `SettingsController.php`, `AiProxyController.php`.
* **Routes & Entry Point**:
  * `routes/api.php`: Complete endpoint mapping under `/api/v1`.
  * `public/index.php`: Server bootstrap with automatic PSR-4 fallback autoloader.
* **Database & CLI**:
  * `database/schema.sql`: Full normalized MySQL 8 schema.
  * `database/install.php`: CLI installer script with automatic database creation, migration runner, and seeding.
  * `database/migrations/`: 8 sequential `.sql` migration files.
  * `database/seeders/`: Demo user seed (`demo@snaptask.app` / `Password123!`) and sample prototype screenshots.
* **Postman**:
  * `postman/SnapTask.postman_collection.json`: 12 folders covering all endpoints with automated `pm.test` scripts.
  * `postman/SnapTask.postman_environment.json`: Local development environment variables.
* **Configuration & Tests**:
  * `composer.json`, `.env.example`, `.env`, `README.md`.
  * `tests/Unit/ValidatorTest.php`, `tests/Unit/AuthServiceTest.php`, `tests/Integration/AuthApiTest.php`, `tests/Integration/ScreenshotUploadTest.php`.

### 2.2 AI Microservice Workspace (`snaptask-ai/`)
* `main.py`: FastAPI application entry point with CORS and health checks.
* `app/core/config.py`: Pydantic settings loading from `.env`.
* `app/core/security.py`: API key header verification (`X-API-Key`).
* `app/models/schemas.py`: Pydantic input and output DTOs.
* `app/services/image_service.py`: Preprocessing, resizing, contrast enhancement, SHA256 and dHash calculation.
* `app/services/ocr_service.py`: Tesseract OCR engine with fallback.
* `app/services/date_extraction_service.py`: Date and time extraction with relative date support.
* `app/services/classification_service.py`: Hybrid rule and keyword density classifier (Dated vs Non-Dated).
* `app/services/task_extraction_service.py`: Title, location, amount, currency, and suggested reminder extraction.
* `app/services/cleanup_service.py`: Deduplication check, retention evaluation, and risk scoring.
* `app/utils/text_utils.py`: Text cleaning, currency symbol parser, and title heuristic.
* `app/api/routes_ai.py`: REST endpoints for analyze, classify, extract-dates, extract-task, and recommend-cleanup.
* `samples/create_samples.py`: Generates sample concert ticket and recipe test images.
* `tests/`: `test_ocr.py`, `test_date_extraction.py`, `test_classification.py`, `test_cleanup.py`.
* `requirements.txt`, `.env.example`, `.env`, `README.md`.

### 2.3 Android Integration Workspace (`android-integration/`)
* `MVVM_ARCHITECTURE.md`: Clean MVVM architecture guide with Compose, Room, DataStore, and WorkManager.
* `API_CONTRACTS.md`: Comprehensive request and response JSON schemas.
* `DATA_MODELS.md`: Kotlin DTOs, Room Entities, Domain Models, and mapping functions.
* `REPOSITORY_EXAMPLES.md`: Concrete Kotlin implementations for ApiService, TokenManager, Repositories, Workers, Hilt modules, and ViewModels.

---

## 3. Database Schema Overview (20 Relational Tables)

| Table | Purpose |
|:---|:---|
| `users` | Primary user identity, bcrypt password hash, status |
| `user_sessions` | Device and session tracking, refresh token hashes |
| `password_resets` | Time-limited password reset tokens |
| `user_settings` | App-level preferences (cleanup summaries, protection toggles, retention) |
| `automation_settings` | Daily automation mode (09:00) vs Manual mode with consent |
| `cleanup_rules` | Custom retention days and lead notification schedules |
| `media_files` | Physical media metadata, checksum, dimensions |
| `screenshots` | Screenshots, OCR text, AI classification, hashes, status, `is_kept` |
| `dated_items` | Time-sensitive screenshots with `event_date`, location, amount |
| `non_dated_items` | Reference captures (recipes, design, shopping) with color labels |
| `tags` | User tags for categorizing reference items |
| `item_tags` | Many-to-many relationship linking non-dated items and tags |
| `reminders` | Scheduled notifications with snooze counts and completion states |
| `notifications` | Notification log (e.g., expiry alerts, space recommendations) |
| `cleanup_jobs` | Batch cleanup execution records |
| `cleanup_recommendations` | AI evaluation logs of cleanable candidates |
| `cleanup_history` | Individual screenshots deleted in each cleanup job |
| `trash_items` | 30-day recovery safety net before permanent deletion |
| `ai_analysis_logs` | Performance and duration logging for AI processing |
| `activity_logs` | User action audit log |
| `migrations` | Applied database migration tracking |

---

## 4. Implemented API Endpoints (60+ Routes)

All routes are mounted under `/api/v1` and protected by JWT authentication (except guest auth and public health checks):

* **Authentication (7)**:
  * `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout`, `GET /auth/me`, `PUT /auth/profile`, `PUT /auth/change-password`
* **Settings & Preferences (10)**:
  * `GET /settings`, `PUT /settings`, `GET /settings/automation`, `PUT /settings/automation`, `GET /settings/cleanup-rules`, `POST /settings/cleanup-rules`, `PUT /settings/cleanup-rules/{id}`, `DELETE /settings/cleanup-rules/{id}`, `GET /settings/privacy`, `PUT /settings/privacy`
* **Screenshots (9)**:
  * `POST /screenshots/upload`, `GET /screenshots`, `GET /screenshots/{id}`, `PUT /screenshots/{id}`, `DELETE /screenshots/{id}`, `POST /screenshots/{id}/analyze`, `POST /screenshots/bulk-analyze`, `GET /screenshots/search`, `GET /screenshots/stats`
* **Dated Items (10)**:
  * `GET /dated-items`, `GET /dated-items/{id}`, `POST /dated-items`, `PUT /dated-items/{id}`, `DELETE /dated-items/{id}`, `GET /dated-items/overdue`, `GET /dated-items/this-week`, `GET /dated-items/upcoming`, `POST /dated-items/{id}/complete`, `POST /dated-items/{id}/snooze`
* **Non-Dated Items (10)**:
  * `GET /non-dated-items`, `GET /non-dated-items/{id}`, `POST /non-dated-items`, `PUT /non-dated-items/{id}`, `DELETE /non-dated-items/{id}`, `GET /non-dated-items/search`, `POST /non-dated-items/{id}/favorite`, `DELETE /non-dated-items/{id}/favorite`, `POST /non-dated-items/{id}/tags`, `DELETE /non-dated-items/{id}/tags/{tagId}`
* **Reminders & Notifications (12)**:
  * `GET /reminders`, `GET /reminders/{id}`, `POST /reminders`, `PUT /reminders/{id}`, `DELETE /reminders/{id}`, `POST /reminders/{id}/snooze`, `POST /reminders/{id}/complete`, `GET /notifications`, `GET /notifications/unread-count`, `POST /notifications/{id}/read`, `POST /notifications/read-all`, `DELETE /notifications/{id}`
* **Cleanup Engine & Trash (11)**:
  * `GET /cleanup/recommendations`, `POST /cleanup/review`, `POST /cleanup/confirm`, `POST /cleanup/cancel`, `GET /cleanup/history`, `GET /cleanup/stats`, `GET /trash`, `POST /trash/{id}/restore`, `DELETE /trash/{id}`, `POST /trash/empty`, `POST /trash/restore-all`
* **Dashboard Hub (5)**:
  * `GET /dashboard`, `GET /dashboard/summary`, `GET /dashboard/cleanup-insights`, `GET /dashboard/category-breakdown`, `GET /dashboard/recent-activity`
* **AI Service Proxy (6)**:
  * `GET /ai/health`, `POST /ai/analyze-screenshot`, `POST /ai/classify-screenshot`, `POST /ai/extract-dates`, `POST /ai/extract-task`, `POST /ai/recommend-cleanup`

---

## 5. Instructions for Local Execution

### 1. Database Setup
```bash
cd snaptask-backend
php database/install.php
```
*Creates the database, runs migrations, and seeds the demo user:* `demo@snaptask.app` / `Password123!`.

### 2. Start PHP REST API
```bash
cd snaptask-backend
php -S 0.0.0.0:8000 -t public
```
*API Base URL:* `http://localhost:8000/api/v1`

### 3. Start Python AI Microservice
```bash
cd snaptask-ai
python -m venv venv
.\venv\Scripts\activate
pip install -r requirements.txt
python main.py
```
*AI Microservice Base URL:* `http://localhost:8001`

### 4. Import Postman Collection
Import `snaptask-backend/postman/SnapTask.postman_collection.json` and `snaptask-backend/postman/SnapTask.postman_environment.json` into Postman, select the **SnapTask Local Environment**, and run the collection.

---

## 6. Next Steps

1. In the Android app, add Retrofit, OkHttp, and Hilt dependencies to `app/build.gradle.kts`.
2. Connect the `HomeDashboardViewModel` to the `HomeDashboardScreen` using the code examples in `android-integration/REPOSITORY_EXAMPLES.md`.
3. Register the `ScreenshotUploadWorker` with Android's WorkManager to automatically synchronize screenshots in the background.


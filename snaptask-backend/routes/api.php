<?php
declare(strict_types=1);

use App\Controllers\AiProxyController;
use App\Controllers\AuthController;
use App\Controllers\CleanupController;
use App\Controllers\DashboardController;
use App\Controllers\DatedItemController;
use App\Controllers\NonDatedItemController;
use App\Controllers\NotificationController;
use App\Controllers\ReminderController;
use App\Controllers\ScreenshotController;
use App\Controllers\SettingsController;
use App\Controllers\TrashController;
use App\Core\Response;
use App\Core\Router;

/** @var Router $router */

// Public root health check
$router->get('/', function () {
    Response::json([
        'app' => 'SnapTask REST API',
        'version' => '1.0.0',
        'status' => 'online',
        'timestamp' => date('c'),
    ]);
});

// Group /api/v1 with global CORS and RateLimit middleware
$router->group('/api/v1', ['cors', 'ratelimit'], function (Router $api) {

    // Health check
    $api->get('/health', function () {
        Response::json([
            'status' => 'healthy',
            'api_version' => 'v1',
            'timestamp' => date('c'),
        ]);
    });

    // --------------------------------------------------------------------------
    // 1. Authentication (Guest Endpoints)
    // --------------------------------------------------------------------------
    $api->post('/auth/register', [AuthController::class, 'register']);
    $api->post('/auth/login', [AuthController::class, 'login']);
    $api->post('/auth/refresh', [AuthController::class, 'refresh']);

    // --------------------------------------------------------------------------
    // Protected Routes (Require JWT Auth)
    // --------------------------------------------------------------------------
    $api->group('', ['auth'], function (Router $auth) {

        // User Auth & Profile
        $auth->post('/auth/logout', [AuthController::class, 'logout']);
        $auth->get('/auth/me', [AuthController::class, 'me']);
        $auth->put('/auth/profile', [AuthController::class, 'updateProfile']);
        $auth->put('/auth/change-password', [AuthController::class, 'changePassword']);

        // User Settings & Privacy
        $auth->get('/settings', [SettingsController::class, 'index']);
        $auth->put('/settings', [SettingsController::class, 'update']);
        $auth->get('/settings/automation', [SettingsController::class, 'getAutomation']);
        $auth->put('/settings/automation', [SettingsController::class, 'updateAutomation']);
        $auth->get('/settings/cleanup-rules', [SettingsController::class, 'getRules']);
        $auth->post('/settings/cleanup-rules', [SettingsController::class, 'storeRule']);
        $auth->put('/settings/cleanup-rules/{id}', [SettingsController::class, 'updateRule']);
        $auth->delete('/settings/cleanup-rules/{id}', [SettingsController::class, 'deleteRule']);
        $auth->get('/settings/privacy', [SettingsController::class, 'getPrivacy']);
        $auth->put('/settings/privacy', [SettingsController::class, 'updatePrivacy']);

        // Dashboard
        $auth->get('/dashboard', [DashboardController::class, 'index']);
        $auth->get('/dashboard/summary', [DashboardController::class, 'summary']);
        $auth->get('/dashboard/cleanup-insights', [DashboardController::class, 'cleanupInsights']);
        $auth->get('/dashboard/category-breakdown', [DashboardController::class, 'categoryBreakdown']);
        $auth->get('/dashboard/recent-activity', [DashboardController::class, 'recentActivity']);

        // Screenshot Management
        $auth->post('/screenshots/upload', [ScreenshotController::class, 'upload']);
        $auth->get('/screenshots', [ScreenshotController::class, 'index']);
        $auth->get('/screenshots/search', [ScreenshotController::class, 'search']);
        $auth->get('/screenshots/stats', [ScreenshotController::class, 'stats']);
        $auth->post('/screenshots/bulk-analyze', [ScreenshotController::class, 'bulkAnalyze']);
        $auth->get('/screenshots/{id}', [ScreenshotController::class, 'show']);
        $auth->put('/screenshots/{id}', [ScreenshotController::class, 'update']);
        $auth->delete('/screenshots/{id}', [ScreenshotController::class, 'delete']);
        $auth->post('/screenshots/{id}/analyze', [ScreenshotController::class, 'analyze']);

        // Dated Items
        $auth->get('/dated-items', [DatedItemController::class, 'index']);
        $auth->get('/dated-items/overdue', [DatedItemController::class, 'overdue']);
        $auth->get('/dated-items/this-week', [DatedItemController::class, 'thisWeek']);
        $auth->get('/dated-items/upcoming', [DatedItemController::class, 'upcoming']);
        $auth->post('/dated-items', [DatedItemController::class, 'store']);
        $auth->get('/dated-items/{id}', [DatedItemController::class, 'show']);
        $auth->put('/dated-items/{id}', [DatedItemController::class, 'update']);
        $auth->delete('/dated-items/{id}', [DatedItemController::class, 'delete']);
        $auth->post('/dated-items/{id}/complete', [DatedItemController::class, 'complete']);
        $auth->post('/dated-items/{id}/snooze', [DatedItemController::class, 'snooze']);

        // Non-Dated Items
        $auth->get('/non-dated-items', [NonDatedItemController::class, 'index']);
        $auth->get('/non-dated-items/search', [NonDatedItemController::class, 'search']);
        $auth->post('/non-dated-items', [NonDatedItemController::class, 'store']);
        $auth->get('/non-dated-items/{id}', [NonDatedItemController::class, 'show']);
        $auth->put('/non-dated-items/{id}', [NonDatedItemController::class, 'update']);
        $auth->delete('/non-dated-items/{id}', [NonDatedItemController::class, 'delete']);
        $auth->post('/non-dated-items/{id}/favorite', [NonDatedItemController::class, 'addFavorite']);
        $auth->delete('/non-dated-items/{id}/favorite', [NonDatedItemController::class, 'removeFavorite']);
        $auth->post('/non-dated-items/{id}/tags', [NonDatedItemController::class, 'addTag']);
        $auth->delete('/non-dated-items/{id}/tags/{tagId}', [NonDatedItemController::class, 'removeTag']);

        // Reminders
        $auth->get('/reminders', [ReminderController::class, 'index']);
        $auth->post('/reminders', [ReminderController::class, 'store']);
        $auth->get('/reminders/{id}', [ReminderController::class, 'show']);
        $auth->put('/reminders/{id}', [ReminderController::class, 'update']);
        $auth->delete('/reminders/{id}', [ReminderController::class, 'delete']);
        $auth->post('/reminders/{id}/snooze', [ReminderController::class, 'snooze']);
        $auth->post('/reminders/{id}/complete', [ReminderController::class, 'complete']);

        // Notifications
        $auth->get('/notifications', [NotificationController::class, 'index']);
        $auth->get('/notifications/unread-count', [NotificationController::class, 'unreadCount']);
        $auth->post('/notifications/read-all', [NotificationController::class, 'markAllRead']);
        $auth->post('/notifications/{id}/read', [NotificationController::class, 'markRead']);
        $auth->delete('/notifications/{id}', [NotificationController::class, 'delete']);

        // Cleanup Engine
        $auth->get('/cleanup/recommendations', [CleanupController::class, 'recommendations']);
        $auth->post('/cleanup/review', [CleanupController::class, 'review']);
        $auth->post('/cleanup/confirm', [CleanupController::class, 'confirm']);
        $auth->post('/cleanup/cancel', [CleanupController::class, 'cancel']);
        $auth->get('/cleanup/history', [CleanupController::class, 'history']);
        $auth->get('/cleanup/stats', [CleanupController::class, 'stats']);

        // Trash & 30-Day Recovery
        $auth->get('/trash', [TrashController::class, 'index']);
        $auth->post('/trash/restore-all', [TrashController::class, 'restoreAll']);
        $auth->post('/trash/empty', [TrashController::class, 'emptyTrash']);
        $auth->post('/trash/{screenshotId}/restore', [TrashController::class, 'restore']);
        $auth->delete('/trash/{screenshotId}', [TrashController::class, 'permanentDelete']);

        // AI Microservice Proxy
        $auth->get('/ai/health', [AiProxyController::class, 'health']);
        $auth->post('/ai/analyze-screenshot', [AiProxyController::class, 'analyzeScreenshot']);
        $auth->post('/ai/classify-screenshot', [AiProxyController::class, 'classify']);
        $auth->post('/ai/extract-dates', [AiProxyController::class, 'extractDates']);
        $auth->post('/ai/extract-task', [AiProxyController::class, 'extractTask']);
        $auth->post('/ai/recommend-cleanup', [AiProxyController::class, 'recommendCleanup']);
    });
});


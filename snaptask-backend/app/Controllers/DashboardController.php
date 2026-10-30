<?php
declare(strict_types=1);

namespace App\Controllers;

use App\Core\Request;
use App\Core\Response;
use App\Services\CleanupService;
use App\Services\DashboardService;

class DashboardController
{
    private DashboardService $service;
    private CleanupService $cleanupService;

    public function __construct()
    {
        $this->service = new DashboardService();
        $this->cleanupService = new CleanupService();
    }

    public function index(Request $request): void
    {
        $userId = $request->getUserId();
        $summary = $this->service->getSummary($userId);
        $categories = $this->service->getCategoryBreakdown($userId);
        $activity = $this->service->getRecentActivity($userId, 5);

        Response::json([
            'summary' => $summary,
            'categories' => $categories,
            'recent_activity' => $activity,
        ], 'Dashboard home data retrieved');
    }

    public function summary(Request $request): void
    {
        $summary = $this->service->getSummary($request->getUserId());
        Response::json($summary, 'Dashboard summary metrics');
    }

    public function cleanupInsights(Request $request): void
    {
        $eligible = $this->cleanupService->getRecommendations($request->getUserId());
        Response::json([
            'eligible_count' => $eligible['total_eligible'],
            'reclaimable_mb' => $eligible['total_mb'],
            'dated_count' => count($eligible['dated_items']),
            'non_dated_count' => count($eligible['non_dated_items']),
            'recommended_action' => $eligible['total_eligible'] > 0 ? 'review' : 'keep',
        ], 'Cleanup insights');
    }

    public function categoryBreakdown(Request $request): void
    {
        $breakdown = $this->service->getCategoryBreakdown($request->getUserId());
        Response::json($breakdown, 'Category breakdown');
    }

    public function recentActivity(Request $request): void
    {
        $limit = min(50, max(1, (int)$request->query('limit', 10)));
        $activity = $this->service->getRecentActivity($request->getUserId(), $limit);
        Response::json(['activity' => $activity], 'Recent activity stream');
    }
}


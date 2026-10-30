<?php
declare(strict_types=1);

namespace App\Services;

use App\Repositories\DashboardRepository;

class DashboardService
{
    private DashboardRepository $repo;

    public function __construct()
    {
        $this->repo = new DashboardRepository();
    }

    public function getSummary(int $userId): array
    {
        return $this->repo->getSummary($userId);
    }

    public function getCategoryBreakdown(int $userId): array
    {
        return $this->repo->getCategoryBreakdown($userId);
    }

    public function getRecentActivity(int $userId, int $limit = 10): array
    {
        return $this->repo->getRecentActivity($userId, $limit);
    }
}


<?php
declare(strict_types=1);

namespace App\Services;

use App\Repositories\CleanupRepository;

class CleanupService
{
    private CleanupRepository $repo;

    public function __construct()
    {
        $this->repo = new CleanupRepository();
    }

    public function getRecommendations(int $userId): array
    {
        return $this->repo->getEligibleScreenshots($userId);
    }

    public function review(int $userId, array $screenshotIds): array
    {
        $eligible = $this->repo->getEligibleScreenshots($userId);
        $selected = array_filter($eligible['items'], fn($item) => in_array((int)$item['id'], $screenshotIds, true));

        $totalBytes = array_sum(array_column($selected, 'file_size'));

        return [
            'selected_count' => count($selected),
            'total_bytes' => $totalBytes,
            'total_mb' => round($totalBytes / 1024 / 1024, 2),
            'items' => array_values($selected),
        ];
    }

    public function confirm(int $userId, array $screenshotIds, string $mode = 'manual'): array
    {
        $job = $this->repo->createJob($userId, $mode);
        return $this->repo->executeCleanup($job->id, $userId, $screenshotIds);
    }

    public function getHistory(int $userId, int $limit = 20): array
    {
        return $this->repo->getHistory($userId, $limit);
    }

    public function getStats(int $userId): array
    {
        return $this->repo->getStats($userId);
    }
}


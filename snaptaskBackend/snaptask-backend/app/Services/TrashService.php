<?php
declare(strict_types=1);

namespace App\Services;

use App\Repositories\TrashRepository;

class TrashService
{
    private TrashRepository $repo;

    public function __construct()
    {
        $this->repo = new TrashRepository();
    }

    public function getList(int $userId, int $page = 1, int $limit = 30): array
    {
        return $this->repo->getList($userId, $page, $limit);
    }

    public function restore(int $screenshotId, int $userId): bool
    {
        return $this->repo->restore($screenshotId, $userId);
    }

    public function restoreAll(int $userId): int
    {
        return $this->repo->restoreAll($userId);
    }

    public function permanentDelete(int $screenshotId, int $userId): bool
    {
        return $this->repo->permanentDelete($screenshotId, $userId);
    }

    public function emptyTrash(int $userId): int
    {
        return $this->repo->emptyTrash($userId);
    }
}


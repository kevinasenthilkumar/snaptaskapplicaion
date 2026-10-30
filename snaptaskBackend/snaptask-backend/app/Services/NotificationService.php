<?php
declare(strict_types=1);

namespace App\Services;

use App\Repositories\NotificationRepository;

class NotificationService
{
    private NotificationRepository $repo;

    public function __construct()
    {
        $this->repo = new NotificationRepository();
    }

    public function getList(int $userId, bool $unreadOnly = false, int $limit = 50): array
    {
        return $this->repo->getList($userId, $unreadOnly, $limit);
    }

    public function getUnreadCount(int $userId): int
    {
        return $this->repo->getUnreadCount($userId);
    }

    public function markAsRead(int $id, int $userId): bool
    {
        return $this->repo->markAsRead($id, $userId);
    }

    public function markAllAsRead(int $userId): bool
    {
        return $this->repo->markAllAsRead($userId);
    }

    public function delete(int $id, int $userId): bool
    {
        return $this->repo->delete($id, $userId);
    }
}


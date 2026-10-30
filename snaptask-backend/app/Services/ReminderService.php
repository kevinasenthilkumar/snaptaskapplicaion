<?php
declare(strict_types=1);

namespace App\Services;

use App\Models\Reminder;
use App\Repositories\ReminderRepository;

class ReminderService
{
    private ReminderRepository $repo;

    public function __construct()
    {
        $this->repo = new ReminderRepository();
    }

    public function getList(int $userId, string $filter = 'all', int $limit = 50): array
    {
        return $this->repo->getList($userId, $filter, $limit);
    }

    public function getById(int $id, int $userId): Reminder
    {
        $r = $this->repo->findById($id, $userId);
        if (!$r) {
            throw new \InvalidArgumentException('Reminder not found.');
        }
        return $r;
    }

    public function create(int $userId, array $data): Reminder
    {
        $data['user_id'] = $userId;
        return $this->repo->create($data);
    }

    public function update(int $id, int $userId, array $data): Reminder
    {
        $this->getById($id, $userId);
        $this->repo->update($id, $userId, $data);
        return $this->getById($id, $userId);
    }

    public function snooze(int $id, int $userId, int $minutes = 60, ?string $customTime = null): Reminder
    {
        $this->getById($id, $userId);
        $this->repo->snooze($id, $userId, $minutes, $customTime);
        return $this->getById($id, $userId);
    }

    public function complete(int $id, int $userId): Reminder
    {
        $this->getById($id, $userId);
        $this->repo->markCompleted($id, $userId);
        return $this->getById($id, $userId);
    }

    public function delete(int $id, int $userId): bool
    {
        $this->getById($id, $userId);
        return $this->repo->delete($id, $userId);
    }
}


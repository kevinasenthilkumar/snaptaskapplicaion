<?php
declare(strict_types=1);

namespace App\Services;

use App\Models\DatedItem;
use App\Repositories\DatedItemRepository;
use App\Repositories\ReminderRepository;
use App\Repositories\ScreenshotRepository;

class DatedItemService
{
    private DatedItemRepository $repo;
    private ScreenshotRepository $screenshotRepo;
    private ReminderRepository $reminderRepo;

    public function __construct()
    {
        $this->repo = new DatedItemRepository();
        $this->screenshotRepo = new ScreenshotRepository();
        $this->reminderRepo = new ReminderRepository();
    }

    public function getList(int $userId, array $filters = [], int $page = 1, int $limit = 20): array
    {
        return $this->repo->getList($userId, $filters, $page, $limit);
    }

    public function getById(int $id, int $userId): DatedItem
    {
        $item = $this->repo->findById($id, $userId);
        if (!$item) {
            throw new \InvalidArgumentException('Dated item not found.');
        }
        return $item;
    }

    public function create(int $userId, array $data): DatedItem
    {
        $data['user_id'] = $userId;
        $item = $this->repo->create($data);

        if (!empty($data['reminder_enabled']) && !empty($data['event_date'])) {
            $this->reminderRepo->create([
                'user_id' => $userId,
                'dated_item_id' => $item->id,
                'title' => "Reminder: {$item->title}",
                'note' => "Upcoming event on {$item->event_date}",
                'trigger_at' => $data['reminder_at'] ?? "{$item->event_date} 09:00:00",
                'status' => 'pending',
            ]);
        }

        return $item;
    }

    public function update(int $id, int $userId, array $data): DatedItem
    {
        $this->getById($id, $userId);
        $this->repo->update($id, $userId, $data);
        return $this->getById($id, $userId);
    }

    public function markCompleted(int $id, int $userId): DatedItem
    {
        $this->getById($id, $userId);
        $this->repo->markCompleted($id, $userId);
        return $this->getById($id, $userId);
    }

    public function snooze(int $id, int $userId, string $snoozeUntil): DatedItem
    {
        $item = $this->getById($id, $userId);
        $this->repo->update($id, $userId, [
            'reminder_at' => $snoozeUntil,
        ]);
        return $this->getById($id, $userId);
    }

    public function delete(int $id, int $userId): bool
    {
        $item = $this->getById($id, $userId);
        // Soft delete associated screenshot
        $this->screenshotRepo->softDelete($item->screenshot_id, $userId);
        return $this->repo->delete($id, $userId);
    }
}


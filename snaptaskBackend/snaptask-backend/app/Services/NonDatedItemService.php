<?php
declare(strict_types=1);

namespace App\Services;

use App\Models\NonDatedItem;
use App\Repositories\NonDatedItemRepository;
use App\Repositories\ScreenshotRepository;

class NonDatedItemService
{
    private NonDatedItemRepository $repo;
    private ScreenshotRepository $screenshotRepo;

    public function __construct()
    {
        $this->repo = new NonDatedItemRepository();
        $this->screenshotRepo = new ScreenshotRepository();
    }

    public function getList(int $userId, array $filters = [], int $page = 1, int $limit = 20): array
    {
        return $this->repo->getList($userId, $filters, $page, $limit);
    }

    public function getById(int $id, int $userId): NonDatedItem
    {
        $item = $this->repo->findById($id, $userId);
        if (!$item) {
            throw new \InvalidArgumentException('Non-dated item not found.');
        }
        return $item;
    }

    public function create(int $userId, array $data): NonDatedItem
    {
        $data['user_id'] = $userId;
        return $this->repo->create($data);
    }

    public function update(int $id, int $userId, array $data): NonDatedItem
    {
        $this->getById($id, $userId);
        $this->repo->update($id, $userId, $data);
        return $this->getById($id, $userId);
    }

    public function toggleFavorite(int $id, int $userId, bool $favorite): NonDatedItem
    {
        $this->getById($id, $userId);
        $this->repo->setFavorite($id, $userId, $favorite);
        return $this->getById($id, $userId);
    }

    public function addTag(int $id, int $userId, string $name, string $color = '#721D38'): array
    {
        $this->getById($id, $userId);
        return $this->repo->addTag($id, $userId, $name, $color);
    }

    public function removeTag(int $id, int $userId, int $tagId): bool
    {
        $this->getById($id, $userId);
        return $this->repo->removeTag($id, $tagId);
    }

    public function delete(int $id, int $userId): bool
    {
        $item = $this->getById($id, $userId);
        $this->screenshotRepo->softDelete($item->screenshot_id, $userId);
        return $this->repo->delete($id, $userId);
    }

    public function search(int $userId, string $query, int $limit = 30): array
    {
        return $this->repo->search($userId, $query, $limit);
    }
}


<?php
declare(strict_types=1);

namespace App\Services;

use App\Models\CleanupRule;
use App\Models\UserSettings;
use App\Repositories\SettingsRepository;

class SettingsService
{
    private SettingsRepository $repo;

    public function __construct()
    {
        $this->repo = new SettingsRepository();
    }

    public function getSettings(int $userId): UserSettings
    {
        return $this->repo->getSettings($userId);
    }

    public function updateSettings(int $userId, array $data): UserSettings
    {
        $this->repo->updateSettings($userId, $data);
        return $this->repo->getSettings($userId);
    }

    public function getAutomation(int $userId): array
    {
        return $this->repo->getAutomation($userId);
    }

    public function updateAutomation(int $userId, array $data): array
    {
        $this->repo->updateAutomation($userId, $data);
        return $this->repo->getAutomation($userId);
    }

    public function getCleanupRules(int $userId): array
    {
        return $this->repo->getCleanupRules($userId);
    }

    public function createCleanupRule(int $userId, array $data): CleanupRule
    {
        $data['user_id'] = $userId;
        return $this->repo->createCleanupRule($data);
    }

    public function updateCleanupRule(int $id, int $userId, array $data): CleanupRule
    {
        $rule = $this->repo->findCleanupRuleById($id, $userId);
        if (!$rule) {
            throw new \InvalidArgumentException('Cleanup rule not found.');
        }
        $this->repo->updateCleanupRule($id, $userId, $data);
        return $this->repo->findCleanupRuleById($id, $userId);
    }

    public function deleteCleanupRule(int $id, int $userId): bool
    {
        $rule = $this->repo->findCleanupRuleById($id, $userId);
        if (!$rule) {
            throw new \InvalidArgumentException('Cleanup rule not found.');
        }
        return $this->repo->deleteCleanupRule($id, $userId);
    }

    public function getPrivacy(int $userId): array
    {
        return $this->repo->getPrivacy($userId);
    }

    public function updatePrivacy(int $userId, array $data): array
    {
        $this->repo->updatePrivacy($userId, $data);
        return $this->repo->getPrivacy($userId);
    }
}


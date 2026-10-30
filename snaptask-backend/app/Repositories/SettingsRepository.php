<?php
declare(strict_types=1);

namespace App\Repositories;

use App\Core\Database;
use App\Models\CleanupRule;
use App\Models\UserSettings;
use PDO;

class SettingsRepository
{
    private PDO $pdo;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
    }

    public function getSettings(int $userId): UserSettings
    {
        $stmt = $this->pdo->prepare('SELECT * FROM user_settings WHERE user_id = ? LIMIT 1');
        $stmt->execute([$userId]);
        $settings = $stmt->fetchObject(UserSettings::class);

        if (!$settings) {
            $this->pdo->prepare('INSERT INTO user_settings (user_id) VALUES (?)')->execute([$userId]);
            $stmt->execute([$userId]);
            $settings = $stmt->fetchObject(UserSettings::class);
        }

        return $settings;
    }

    public function updateSettings(int $userId, array $data): bool
    {
        $fields = [];
        $params = [];
        $allowed = [
            'auto_scan_screenshots', 'auto_categorize', 'default_lead_time_days',
            'cleanup_frequency', 'cleanup_reminders_enabled', 'cleanup_summaries_enabled',
            'protect_kept_screenshots', 'ai_organization_enabled', 'share_analytics',
            'trash_retention_days'
        ];

        foreach ($allowed as $col) {
            if (array_key_exists($col, $data)) {
                $fields[] = "`{$col}` = ?";
                $params[] = is_bool($data[$col]) ? ($data[$col] ? 1 : 0) : $data[$col];
            }
        }

        if (empty($fields)) {
            return false;
        }

        $params[] = $userId;
        $sql = 'UPDATE user_settings SET ' . implode(', ', $fields) . ', updated_at = NOW() WHERE user_id = ?';
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    public function getAutomation(int $userId): array
    {
        $stmt = $this->pdo->prepare('SELECT * FROM automation_settings WHERE user_id = ? LIMIT 1');
        $stmt->execute([$userId]);
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$row) {
            $this->pdo->prepare('INSERT INTO automation_settings (user_id) VALUES (?)')->execute([$userId]);
            $stmt->execute([$userId]);
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
        }

        return [
            'id' => (int)$row['id'],
            'user_id' => (int)$row['user_id'],
            'mode' => $row['mode'],
            'daily_time' => $row['daily_time'],
            'requires_consent' => (bool)$row['requires_consent'],
            'last_run_at' => $row['last_run_at'],
            'next_run_at' => $row['next_run_at'],
            'created_at' => $row['created_at'],
            'updated_at' => $row['updated_at'],
        ];
    }

    public function updateAutomation(int $userId, array $data): bool
    {
        $fields = [];
        $params = [];

        if (isset($data['mode'])) {
            $fields[] = 'mode = ?';
            $params[] = $data['mode'];
        }
        if (isset($data['daily_time'])) {
            $fields[] = 'daily_time = ?';
            $params[] = $data['daily_time'];
        }
        if (isset($data['requires_consent'])) {
            $fields[] = 'requires_consent = ?';
            $params[] = $data['requires_consent'] ? 1 : 0;
        }

        if (empty($fields)) {
            return false;
        }

        $params[] = $userId;
        $sql = 'UPDATE automation_settings SET ' . implode(', ', $fields) . ', updated_at = NOW() WHERE user_id = ?';
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    public function getCleanupRules(int $userId): array
    {
        $stmt = $this->pdo->prepare('SELECT * FROM cleanup_rules WHERE user_id = ? ORDER BY id ASC');
        $stmt->execute([$userId]);
        return $stmt->fetchAll(PDO::FETCH_CLASS, CleanupRule::class);
    }

    public function findCleanupRuleById(int $id, int $userId): ?CleanupRule
    {
        $stmt = $this->pdo->prepare('SELECT * FROM cleanup_rules WHERE id = ? AND user_id = ? LIMIT 1');
        $stmt->execute([$id, $userId]);
        $row = $stmt->fetchObject(CleanupRule::class);
        return $row ?: null;
    }

    public function createCleanupRule(array $data): CleanupRule
    {
        $stmt = $this->pdo->prepare(
            'INSERT INTO cleanup_rules (user_id, rule_name, item_type, retention_days, notify_days_before, is_active)
             VALUES (?, ?, ?, ?, ?, ?)'
        );
        $stmt->execute([
            $data['user_id'],
            $data['rule_name'],
            $data['item_type'],
            $data['retention_days'] ?? 7,
            $data['notify_days_before'] ?? 1,
            $data['is_active'] ?? 1,
        ]);

        $id = (int)$this->pdo->lastInsertId();
        return $this->findCleanupRuleById($id, (int)$data['user_id']);
    }

    public function updateCleanupRule(int $id, int $userId, array $data): bool
    {
        $fields = [];
        $params = [];
        $allowed = ['rule_name', 'item_type', 'retention_days', 'notify_days_before', 'is_active'];

        foreach ($allowed as $col) {
            if (array_key_exists($col, $data)) {
                $fields[] = "`{$col}` = ?";
                $params[] = is_bool($data[$col]) ? ($data[$col] ? 1 : 0) : $data[$col];
            }
        }

        if (empty($fields)) {
            return false;
        }

        $params[] = $id;
        $params[] = $userId;
        $sql = 'UPDATE cleanup_rules SET ' . implode(', ', $fields) . ', updated_at = NOW() WHERE id = ? AND user_id = ?';
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    public function deleteCleanupRule(int $id, int $userId): bool
    {
        $stmt = $this->pdo->prepare('DELETE FROM cleanup_rules WHERE id = ? AND user_id = ?');
        return $stmt->execute([$id, $userId]);
    }

    public function getPrivacy(int $userId): array
    {
        $settings = $this->getSettings($userId);
        return [
            'ai_organization_enabled' => (bool)$settings->ai_organization_enabled,
            'share_analytics' => (bool)$settings->share_analytics,
            'photos_and_videos_access' => 'selected_screenshots_only',
            'notifications_permission' => 'not_enabled_on_device',
        ];
    }

    public function updatePrivacy(int $userId, array $data): bool
    {
        return $this->updateSettings($userId, [
            'ai_organization_enabled' => $data['ai_organization_enabled'] ?? null,
            'share_analytics' => $data['share_analytics'] ?? null,
        ]);
    }
}


<?php
declare(strict_types=1);

namespace App\Repositories;

use App\Core\Database;
use App\Models\Reminder;
use PDO;

class ReminderRepository
{
    private PDO $pdo;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
    }

    public function findById(int $id, int $userId): ?Reminder
    {
        $stmt = $this->pdo->prepare('SELECT * FROM reminders WHERE id = ? AND user_id = ? LIMIT 1');
        $stmt->execute([$id, $userId]);
        $row = $stmt->fetchObject(Reminder::class);
        return $row ?: null;
    }

    public function getList(int $userId, string $filter = 'all', int $limit = 50): array
    {
        $where = ['user_id = ?'];
        $params = [$userId];
        $now = date('Y-m-d H:i:s');
        $todayEnd = date('Y-m-d 23:59:59');
        $thisWeekEnd = date('Y-m-d 23:59:59', strtotime('+7 days'));

        switch ($filter) {
            case 'overdue':
                $where[] = 'trigger_at < ? AND status = "pending"';
                $params[] = $now;
                break;
            case 'today':
                $where[] = 'trigger_at >= ? AND trigger_at <= ? AND status != "cancelled"';
                $params[] = date('Y-m-d 00:00:00');
                $params[] = $todayEnd;
                break;
            case 'this_week':
                $where[] = 'trigger_at >= ? AND trigger_at <= ? AND status != "cancelled"';
                $params[] = $now;
                $params[] = $thisWeekEnd;
                break;
            case 'upcoming':
                $where[] = 'trigger_at >= ? AND status != "cancelled"';
                $params[] = $now;
                break;
            case 'completed':
                $where[] = 'status = "completed"';
                break;
            default: // all active
                $where[] = 'status != "cancelled"';
                break;
        }

        $whereClause = implode(' AND ', $where);
        $sql = "SELECT * FROM reminders WHERE {$whereClause} ORDER BY trigger_at ASC LIMIT {$limit}";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);

        return $stmt->fetchAll(PDO::FETCH_CLASS, Reminder::class);
    }

    public function create(array $data): Reminder
    {
        $stmt = $this->pdo->prepare(
            'INSERT INTO reminders (user_id, dated_item_id, title, note, trigger_at, status)
             VALUES (?, ?, ?, ?, ?, ?)'
        );
        $stmt->execute([
            $data['user_id'],
            $data['dated_item_id'] ?? null,
            $data['title'],
            $data['note'] ?? null,
            $data['trigger_at'],
            $data['status'] ?? 'pending',
        ]);
        $id = (int)$this->pdo->lastInsertId();
        return $this->findById($id, (int)$data['user_id']);
    }

    public function update(int $id, int $userId, array $data): bool
    {
        $fields = [];
        $params = [];
        $allowed = ['title', 'note', 'trigger_at', 'status', 'snoozed_until'];

        foreach ($allowed as $col) {
            if (array_key_exists($col, $data)) {
                $fields[] = "`{$col}` = ?";
                $params[] = $data[$col];
            }
        }

        if (empty($fields)) {
            return false;
        }

        $params[] = $id;
        $params[] = $userId;
        $sql = 'UPDATE reminders SET ' . implode(', ', $fields) . ', updated_at = NOW() WHERE id = ? AND user_id = ?';
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    public function snooze(int $id, int $userId, int $minutes = 60, ?string $customTime = null): bool
    {
        $snoozeTime = $customTime ?: date('Y-m-d H:i:s', time() + ($minutes * 60));
        $stmt = $this->pdo->prepare(
            'UPDATE reminders 
             SET trigger_at = ?, snoozed_until = ?, snooze_count = snooze_count + 1, status = "snoozed", updated_at = NOW() 
             WHERE id = ? AND user_id = ?'
        );
        return $stmt->execute([$snoozeTime, $snoozeTime, $id, $userId]);
    }

    public function markCompleted(int $id, int $userId): bool
    {
        $stmt = $this->pdo->prepare(
            'UPDATE reminders SET status = "completed", completed_at = NOW(), updated_at = NOW() WHERE id = ? AND user_id = ?'
        );
        return $stmt->execute([$id, $userId]);
    }

    public function delete(int $id, int $userId): bool
    {
        $stmt = $this->pdo->prepare('DELETE FROM reminders WHERE id = ? AND user_id = ?');
        return $stmt->execute([$id, $userId]);
    }
}


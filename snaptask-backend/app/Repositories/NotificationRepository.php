<?php
declare(strict_types=1);

namespace App\Repositories;

use App\Core\Database;
use App\Models\Notification;
use PDO;

class NotificationRepository
{
    private PDO $pdo;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
    }

    public function getList(int $userId, bool $unreadOnly = false, int $limit = 50): array
    {
        $where = ['user_id = ?'];
        $params = [$userId];

        if ($unreadOnly) {
            $where[] = 'is_read = 0';
        }

        $whereClause = implode(' AND ', $where);
        $sql = "SELECT * FROM notifications WHERE {$whereClause} ORDER BY created_at DESC LIMIT {$limit}";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);

        return $stmt->fetchAll(PDO::FETCH_CLASS, Notification::class);
    }

    public function getUnreadCount(int $userId): int
    {
        $stmt = $this->pdo->prepare('SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0');
        $stmt->execute([$userId]);
        return (int)$stmt->fetchColumn();
    }

    public function create(array $data): Notification
    {
        $stmt = $this->pdo->prepare(
            'INSERT INTO notifications (user_id, type, title, body, data)
             VALUES (?, ?, ?, ?, ?)'
        );
        $stmt->execute([
            $data['user_id'],
            $data['type'],
            $data['title'],
            $data['body'],
            isset($data['data']) ? json_encode($data['data']) : null,
        ]);

        $id = (int)$this->pdo->lastInsertId();
        $select = $this->pdo->prepare('SELECT * FROM notifications WHERE id = ?');
        $select->execute([$id]);
        return $select->fetchObject(Notification::class);
    }

    public function markAsRead(int $id, int $userId): bool
    {
        $stmt = $this->pdo->prepare(
            'UPDATE notifications SET is_read = 1, read_at = NOW() WHERE id = ? AND user_id = ?'
        );
        return $stmt->execute([$id, $userId]);
    }

    public function markAllAsRead(int $userId): bool
    {
        $stmt = $this->pdo->prepare(
            'UPDATE notifications SET is_read = 1, read_at = NOW() WHERE user_id = ? AND is_read = 0'
        );
        return $stmt->execute([$userId]);
    }

    public function delete(int $id, int $userId): bool
    {
        $stmt = $this->pdo->prepare('DELETE FROM notifications WHERE id = ? AND user_id = ?');
        return $stmt->execute([$id, $userId]);
    }
}


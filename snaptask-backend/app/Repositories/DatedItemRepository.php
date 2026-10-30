<?php
declare(strict_types=1);

namespace App\Repositories;

use App\Core\Database;
use App\Models\DatedItem;
use PDO;

class DatedItemRepository
{
    private PDO $pdo;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
    }

    public function findById(int $id, int $userId): ?DatedItem
    {
        $sql = 'SELECT d.*, s.file_path, s.is_kept 
                FROM dated_items d 
                JOIN screenshots s ON s.id = d.screenshot_id 
                WHERE d.id = ? AND d.user_id = ? AND s.deleted_at IS NULL LIMIT 1';
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([$id, $userId]);
        $row = $stmt->fetchObject(DatedItem::class);
        return $row ?: null;
    }

    public function findByScreenshotId(int $screenshotId, int $userId): ?DatedItem
    {
        $sql = 'SELECT d.*, s.file_path, s.is_kept 
                FROM dated_items d 
                JOIN screenshots s ON s.id = d.screenshot_id 
                WHERE d.screenshot_id = ? AND d.user_id = ? LIMIT 1';
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([$screenshotId, $userId]);
        $row = $stmt->fetchObject(DatedItem::class);
        return $row ?: null;
    }

    public function getList(int $userId, array $filters = [], int $page = 1, int $limit = 20): array
    {
        $where = ['d.user_id = ?', 's.deleted_at IS NULL'];
        $params = [$userId];

        if (!empty($filters['category'])) {
            $where[] = 'd.category = ?';
            $params[] = $filters['category'];
        }

        if (!empty($filters['status'])) {
            $where[] = 'd.status = ?';
            $params[] = $filters['status'];
        }

        if (!empty($filters['timeframe'])) {
            $today = date('Y-m-d');
            if ($filters['timeframe'] === 'overdue') {
                $where[] = 'd.event_date < ? AND d.status = "active"';
                $params[] = $today;
            } elseif ($filters['timeframe'] === 'today') {
                $where[] = 'd.event_date = ?';
                $params[] = $today;
            } elseif ($filters['timeframe'] === 'this_week') {
                $endOfWeek = date('Y-m-d', strtotime('+7 days'));
                $where[] = 'd.event_date >= ? AND d.event_date <= ?';
                $params[] = $today;
                $params[] = $endOfWeek;
            } elseif ($filters['timeframe'] === 'upcoming') {
                $where[] = 'd.event_date >= ?';
                $params[] = $today;
            }
        }

        $whereClause = implode(' AND ', $where);

        $countSql = "SELECT COUNT(*) FROM dated_items d JOIN screenshots s ON s.id = d.screenshot_id WHERE {$whereClause}";
        $countStmt = $this->pdo->prepare($countSql);
        $countStmt->execute($params);
        $total = (int)$countStmt->fetchColumn();

        $offset = ($page - 1) * $limit;
        $order = $filters['sort'] === 'date_desc' ? 'd.event_date DESC' : 'd.event_date ASC';

        $sql = "SELECT d.*, s.file_path, s.is_kept 
                FROM dated_items d 
                JOIN screenshots s ON s.id = d.screenshot_id 
                WHERE {$whereClause} 
                ORDER BY {$order} 
                LIMIT {$limit} OFFSET {$offset}";

        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);
        $items = $stmt->fetchAll(PDO::FETCH_CLASS, DatedItem::class);

        return [
            'items' => $items,
            'total' => $total,
            'page' => $page,
            'limit' => $limit,
            'total_pages' => (int)ceil($total / max(1, $limit)),
        ];
    }

    public function create(array $data): DatedItem
    {
        $sql = 'INSERT INTO dated_items (
            user_id, screenshot_id, title, description, category, event_date,
            event_time, end_date, location, amount, currency, priority, status,
            reminder_enabled, reminder_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE 
            title = VALUES(title),
            description = VALUES(description),
            category = VALUES(category),
            event_date = VALUES(event_date),
            event_time = VALUES(event_time),
            end_date = VALUES(end_date),
            location = VALUES(location),
            amount = VALUES(amount),
            currency = VALUES(currency),
            priority = VALUES(priority),
            reminder_enabled = VALUES(reminder_enabled),
            reminder_at = VALUES(reminder_at),
            updated_at = NOW()';

        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([
            $data['user_id'],
            $data['screenshot_id'],
            $data['title'],
            $data['description'] ?? null,
            $data['category'] ?? 'event',
            $data['event_date'],
            $data['event_time'] ?? null,
            $data['end_date'] ?? null,
            $data['location'] ?? null,
            $data['amount'] ?? null,
            $data['currency'] ?? 'USD',
            $data['priority'] ?? 'medium',
            $data['status'] ?? 'active',
            $data['reminder_enabled'] ?? 1,
            $data['reminder_at'] ?? null,
        ]);

        return $this->findByScreenshotId((int)$data['screenshot_id'], (int)$data['user_id']);
    }

    public function update(int $id, int $userId, array $data): bool
    {
        $fields = [];
        $params = [];
        $allowed = [
            'title', 'description', 'category', 'event_date', 'event_time',
            'end_date', 'location', 'amount', 'currency', 'priority', 'status',
            'reminder_enabled', 'reminder_at'
        ];

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
        $sql = 'UPDATE dated_items SET ' . implode(', ', $fields) . ', updated_at = NOW() WHERE id = ? AND user_id = ?';
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    public function markCompleted(int $id, int $userId): bool
    {
        $stmt = $this->pdo->prepare('UPDATE dated_items SET status = "completed", completed_at = NOW(), updated_at = NOW() WHERE id = ? AND user_id = ?');
        return $stmt->execute([$id, $userId]);
    }

    public function delete(int $id, int $userId): bool
    {
        $stmt = $this->pdo->prepare('DELETE FROM dated_items WHERE id = ? AND user_id = ?');
        return $stmt->execute([$id, $userId]);
    }
}


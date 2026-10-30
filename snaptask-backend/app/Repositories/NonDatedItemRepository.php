<?php
declare(strict_types=1);

namespace App\Repositories;

use App\Core\Database;
use App\Models\NonDatedItem;
use PDO;

class NonDatedItemRepository
{
    private PDO $pdo;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
    }

    public function findById(int $id, int $userId): ?NonDatedItem
    {
        $sql = 'SELECT n.*, s.file_path, s.is_kept 
                FROM non_dated_items n 
                JOIN screenshots s ON s.id = n.screenshot_id 
                WHERE n.id = ? AND n.user_id = ? AND s.deleted_at IS NULL LIMIT 1';
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([$id, $userId]);
        $row = $stmt->fetchObject(NonDatedItem::class);
        if ($row) {
            $row->tags = $this->getItemTags($row->id);
        }
        return $row ?: null;
    }

    public function findByScreenshotId(int $screenshotId, int $userId): ?NonDatedItem
    {
        $sql = 'SELECT n.*, s.file_path, s.is_kept 
                FROM non_dated_items n 
                JOIN screenshots s ON s.id = n.screenshot_id 
                WHERE n.screenshot_id = ? AND n.user_id = ? LIMIT 1';
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([$screenshotId, $userId]);
        $row = $stmt->fetchObject(NonDatedItem::class);
        if ($row) {
            $row->tags = $this->getItemTags($row->id);
        }
        return $row ?: null;
    }

    public function getList(int $userId, array $filters = [], int $page = 1, int $limit = 20): array
    {
        $where = ['n.user_id = ?', 's.deleted_at IS NULL'];
        $params = [$userId];

        if (!empty($filters['category'])) {
            $where[] = 'n.category = ?';
            $params[] = $filters['category'];
        }

        if (isset($filters['is_favorite'])) {
            $where[] = 'n.is_favorite = ?';
            $params[] = $filters['is_favorite'] ? 1 : 0;
        }

        $whereClause = implode(' AND ', $where);

        $countSql = "SELECT COUNT(*) FROM non_dated_items n JOIN screenshots s ON s.id = n.screenshot_id WHERE {$whereClause}";
        $countStmt = $this->pdo->prepare($countSql);
        $countStmt->execute($params);
        $total = (int)$countStmt->fetchColumn();

        $offset = ($page - 1) * $limit;
        $order = ($filters['sort'] ?? '') === 'oldest' ? 'n.created_at ASC' : 'n.created_at DESC';

        $sql = "SELECT n.*, s.file_path, s.is_kept 
                FROM non_dated_items n 
                JOIN screenshots s ON s.id = n.screenshot_id 
                WHERE {$whereClause} 
                ORDER BY {$order} 
                LIMIT {$limit} OFFSET {$offset}";

        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);
        $items = $stmt->fetchAll(PDO::FETCH_CLASS, NonDatedItem::class);

        foreach ($items as $item) {
            $item->tags = $this->getItemTags($item->id);
        }

        return [
            'items' => $items,
            'total' => $total,
            'page' => $page,
            'limit' => $limit,
            'total_pages' => (int)ceil($total / max(1, $limit)),
        ];
    }

    public function search(int $userId, string $query, int $limit = 30): array
    {
        $like = '%' . trim($query) . '%';
        $sql = 'SELECT n.*, s.file_path, s.is_kept 
                FROM non_dated_items n 
                JOIN screenshots s ON s.id = n.screenshot_id 
                WHERE n.user_id = ? AND s.deleted_at IS NULL 
                  AND (n.title LIKE ? OR n.content_text LIKE ? OR n.description LIKE ?)
                ORDER BY n.created_at DESC LIMIT ?';

        $stmt = $this->pdo->prepare($sql);
        $stmt->bindValue(1, $userId, PDO::PARAM_INT);
        $stmt->bindValue(2, $like, PDO::PARAM_STR);
        $stmt->bindValue(3, $like, PDO::PARAM_STR);
        $stmt->bindValue(4, $like, PDO::PARAM_STR);
        $stmt->bindValue(5, $limit, PDO::PARAM_INT);
        $stmt->execute();

        $items = $stmt->fetchAll(PDO::FETCH_CLASS, NonDatedItem::class);
        foreach ($items as $item) {
            $item->tags = $this->getItemTags($item->id);
        }
        return $items;
    }

    public function create(array $data): NonDatedItem
    {
        $sql = 'INSERT INTO non_dated_items (
            user_id, screenshot_id, title, description, category, content_text,
            is_favorite, color_label, ai_metadata
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE 
            title = VALUES(title),
            description = VALUES(description),
            category = VALUES(category),
            content_text = VALUES(content_text),
            color_label = VALUES(color_label),
            updated_at = NOW()';

        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([
            $data['user_id'],
            $data['screenshot_id'],
            $data['title'],
            $data['description'] ?? null,
            $data['category'] ?? 'idea',
            $data['content_text'] ?? null,
            $data['is_favorite'] ?? 0,
            $data['color_label'] ?? '#EDE7DE',
            isset($data['ai_metadata']) ? json_encode($data['ai_metadata']) : null,
        ]);

        return $this->findByScreenshotId((int)$data['screenshot_id'], (int)$data['user_id']);
    }

    public function update(int $id, int $userId, array $data): bool
    {
        $fields = [];
        $params = [];
        $allowed = ['title', 'description', 'category', 'content_text', 'is_favorite', 'color_label'];

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
        $sql = 'UPDATE non_dated_items SET ' . implode(', ', $fields) . ', updated_at = NOW() WHERE id = ? AND user_id = ?';
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    public function setFavorite(int $id, int $userId, bool $favorite): bool
    {
        $stmt = $this->pdo->prepare('UPDATE non_dated_items SET is_favorite = ?, updated_at = NOW() WHERE id = ? AND user_id = ?');
        return $stmt->execute([$favorite ? 1 : 0, $id, $userId]);
    }

    public function delete(int $id, int $userId): bool
    {
        $stmt = $this->pdo->prepare('DELETE FROM non_dated_items WHERE id = ? AND user_id = ?');
        return $stmt->execute([$id, $userId]);
    }

    public function getItemTags(int $nonDatedItemId): array
    {
        $sql = 'SELECT t.id, t.name, t.color 
                FROM tags t 
                JOIN item_tags it ON it.tag_id = t.id 
                WHERE it.non_dated_item_id = ?';
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([$nonDatedItemId]);
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function addTag(int $nonDatedItemId, int $userId, string $tagName, string $color = '#721D38'): array
    {
        // 1. Ensure tag exists
        $stmt = $this->pdo->prepare('INSERT INTO tags (user_id, name, color) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE color = VALUES(color)');
        $stmt->execute([$userId, trim($tagName), $color]);

        $tagStmt = $this->pdo->prepare('SELECT id, name, color FROM tags WHERE user_id = ? AND name = ? LIMIT 1');
        $tagStmt->execute([$userId, trim($tagName)]);
        $tag = $tagStmt->fetch(PDO::FETCH_ASSOC);

        if ($tag) {
            $linkStmt = $this->pdo->prepare('INSERT IGNORE INTO item_tags (non_dated_item_id, tag_id) VALUES (?, ?)');
            $linkStmt->execute([$nonDatedItemId, $tag['id']]);
        }

        return $tag ?: [];
    }

    public function removeTag(int $nonDatedItemId, int $tagId): bool
    {
        $stmt = $this->pdo->prepare('DELETE FROM item_tags WHERE non_dated_item_id = ? AND tag_id = ?');
        return $stmt->execute([$nonDatedItemId, $tagId]);
    }
}


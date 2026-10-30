<?php
declare(strict_types=1);

namespace App\Repositories;

use App\Core\Database;
use App\Models\Screenshot;
use PDO;

class ScreenshotRepository
{
    private PDO $pdo;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
    }

    public function create(array $data): Screenshot
    {
        $sql = 'INSERT INTO screenshots (
            user_id, local_media_id, file_path, file_name, file_hash, perceptual_hash,
            mime_type, file_size, width, height, category, classification_confidence,
            ocr_text, extracted_title, extracted_description, extracted_date, extracted_time,
            extracted_location, extracted_amount, extracted_currency, source_app, status,
            is_kept, ai_metadata, captured_at
        ) VALUES (
            ?, ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?
        )';

        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([
            $data['user_id'],
            $data['local_media_id'] ?? null,
            $data['file_path'],
            $data['file_name'],
            $data['file_hash'],
            $data['perceptual_hash'] ?? null,
            $data['mime_type'] ?? 'image/png',
            $data['file_size'] ?? 0,
            $data['width'] ?? null,
            $data['height'] ?? null,
            $data['category'] ?? 'unclassified',
            $data['classification_confidence'] ?? 0.0,
            $data['ocr_text'] ?? null,
            $data['extracted_title'] ?? null,
            $data['extracted_description'] ?? null,
            $data['extracted_date'] ?? null,
            $data['extracted_time'] ?? null,
            $data['extracted_location'] ?? null,
            $data['extracted_amount'] ?? null,
            $data['extracted_currency'] ?? null,
            $data['source_app'] ?? null,
            $data['status'] ?? 'pending',
            $data['is_kept'] ?? 0,
            isset($data['ai_metadata']) ? (is_string($data['ai_metadata']) ? $data['ai_metadata'] : json_encode($data['ai_metadata'])) : null,
            $data['captured_at'] ?? date('Y-m-d H:i:s'),
        ]);

        $id = (int)$this->pdo->lastInsertId();
        return $this->findById($id, (int)$data['user_id']);
    }

    public function findById(int $id, int $userId): ?Screenshot
    {
        $stmt = $this->pdo->prepare('SELECT * FROM screenshots WHERE id = ? AND user_id = ? AND deleted_at IS NULL LIMIT 1');
        $stmt->execute([$id, $userId]);
        $row = $stmt->fetchObject(Screenshot::class);
        return $row ?: null;
    }

    public function findByHash(string $hash, int $userId): ?Screenshot
    {
        $stmt = $this->pdo->prepare('SELECT * FROM screenshots WHERE file_hash = ? AND user_id = ? AND deleted_at IS NULL LIMIT 1');
        $stmt->execute([$hash, $userId]);
        $row = $stmt->fetchObject(Screenshot::class);
        return $row ?: null;
    }

    public function findByPerceptualHash(string $pHash, int $userId): ?Screenshot
    {
        $stmt = $this->pdo->prepare('SELECT * FROM screenshots WHERE perceptual_hash = ? AND user_id = ? AND deleted_at IS NULL LIMIT 1');
        $stmt->execute([$pHash, $userId]);
        $row = $stmt->fetchObject(Screenshot::class);
        return $row ?: null;
    }

    public function getList(int $userId, array $filters = [], int $page = 1, int $limit = 20): array
    {
        $where = ['user_id = ?', 'deleted_at IS NULL'];
        $params = [$userId];

        if (!empty($filters['category'])) {
            $where[] = 'category = ?';
            $params[] = $filters['category'];
        }

        if (!empty($filters['status'])) {
            $where[] = 'status = ?';
            $params[] = $filters['status'];
        }

        if (isset($filters['is_kept'])) {
            $where[] = 'is_kept = ?';
            $params[] = $filters['is_kept'] ? 1 : 0;
        }

        $whereClause = implode(' AND ', $where);

        // Count total
        $countStmt = $this->pdo->prepare("SELECT COUNT(*) FROM screenshots WHERE {$whereClause}");
        $countStmt->execute($params);
        $total = (int)$countStmt->fetchColumn();

        // Query records
        $offset = ($page - 1) * $limit;
        $order = $filters['order'] ?? 'uploaded_at DESC';

        $sql = "SELECT * FROM screenshots WHERE {$whereClause} ORDER BY {$order} LIMIT {$limit} OFFSET {$offset}";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute($params);
        $items = $stmt->fetchAll(PDO::FETCH_CLASS, Screenshot::class);

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
        $sql = 'SELECT * FROM screenshots 
                WHERE user_id = ? AND deleted_at IS NULL 
                  AND (extracted_title LIKE ? OR ocr_text LIKE ? OR extracted_location LIKE ?)
                ORDER BY uploaded_at DESC LIMIT ?';
        
        $stmt = $this->pdo->prepare($sql);
        $stmt->bindValue(1, $userId, PDO::PARAM_INT);
        $stmt->bindValue(2, $like, PDO::PARAM_STR);
        $stmt->bindValue(3, $like, PDO::PARAM_STR);
        $stmt->bindValue(4, $like, PDO::PARAM_STR);
        $stmt->bindValue(5, $limit, PDO::PARAM_INT);
        $stmt->execute();

        return $stmt->fetchAll(PDO::FETCH_CLASS, Screenshot::class);
    }

    public function update(int $id, int $userId, array $data): bool
    {
        $fields = [];
        $params = [];

        $allowed = [
            'category', 'classification_confidence', 'ocr_text', 'extracted_title',
            'extracted_description', 'extracted_date', 'extracted_time', 'extracted_location',
            'extracted_amount', 'extracted_currency', 'source_app', 'status', 'is_kept',
            'ai_metadata', 'perceptual_hash'
        ];

        foreach ($allowed as $col) {
            if (array_key_exists($col, $data)) {
                $fields[] = "`{$col}` = ?";
                $val = $data[$col];
                if ($col === 'ai_metadata' && is_array($val)) {
                    $val = json_encode($val);
                }
                $params[] = $val;
            }
        }

        if (empty($fields)) {
            return false;
        }

        $params[] = $id;
        $params[] = $userId;
        $sql = 'UPDATE screenshots SET ' . implode(', ', $fields) . ', updated_at = NOW() WHERE id = ? AND user_id = ?';
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    public function softDelete(int $id, int $userId): bool
    {
        $stmt = $this->pdo->prepare('UPDATE screenshots SET deleted_at = NOW(), status = "deleted" WHERE id = ? AND user_id = ?');
        return $stmt->execute([$id, $userId]);
    }

    public function restore(int $id, int $userId): bool
    {
        $stmt = $this->pdo->prepare('UPDATE screenshots SET deleted_at = NULL, status = "classified" WHERE id = ? AND user_id = ?');
        return $stmt->execute([$id, $userId]);
    }

    public function hardDelete(int $id, int $userId): bool
    {
        $stmt = $this->pdo->prepare('DELETE FROM screenshots WHERE id = ? AND user_id = ?');
        return $stmt->execute([$id, $userId]);
    }

    public function getStats(int $userId): array
    {
        $stmt = $this->pdo->prepare('
            SELECT 
                COUNT(*) as total_screenshots,
                SUM(CASE WHEN category = "dated" THEN 1 ELSE 0 END) as dated_count,
                SUM(CASE WHEN category = "non_dated" THEN 1 ELSE 0 END) as non_dated_count,
                SUM(CASE WHEN category = "unclassified" THEN 1 ELSE 0 END) as unclassified_count,
                SUM(CASE WHEN is_kept = 1 THEN 1 ELSE 0 END) as kept_count,
                COALESCE(SUM(file_size), 0) as total_bytes
            FROM screenshots 
            WHERE user_id = ? AND deleted_at IS NULL
        ');
        $stmt->execute([$userId]);
        $res = $stmt->fetch(PDO::FETCH_ASSOC);

        return [
            'total_screenshots' => (int)($res['total_screenshots'] ?? 0),
            'dated_count' => (int)($res['dated_count'] ?? 0),
            'non_dated_count' => (int)($res['non_dated_count'] ?? 0),
            'unclassified_count' => (int)($res['unclassified_count'] ?? 0),
            'kept_count' => (int)($res['kept_count'] ?? 0),
            'total_bytes' => (int)($res['total_bytes'] ?? 0),
            'total_mb' => round((int)($res['total_bytes'] ?? 0) / 1024 / 1024, 2),
        ];
    }
}


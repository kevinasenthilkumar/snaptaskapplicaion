<?php
declare(strict_types=1);

namespace App\Repositories;

use App\Core\Database;
use App\Models\TrashItem;
use PDO;

class TrashRepository
{
    private PDO $pdo;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
    }

    public function getList(int $userId, int $page = 1, int $limit = 30): array
    {
        $countStmt = $this->pdo->prepare('SELECT COUNT(*) FROM trash_items WHERE user_id = ? AND is_restored = 0');
        $countStmt->execute([$userId]);
        $total = (int)$countStmt->fetchColumn();

        $offset = ($page - 1) * $limit;
        $sql = 'SELECT t.*, s.file_path 
                FROM trash_items t 
                JOIN screenshots s ON s.id = t.screenshot_id 
                WHERE t.user_id = ? AND t.is_restored = 0 
                ORDER BY t.trashed_at DESC 
                LIMIT ? OFFSET ?';

        $stmt = $this->pdo->prepare($sql);
        $stmt->bindValue(1, $userId, PDO::PARAM_INT);
        $stmt->bindValue(2, $limit, PDO::PARAM_INT);
        $stmt->bindValue(3, $offset, PDO::PARAM_INT);
        $stmt->execute();

        $items = $stmt->fetchAll(PDO::FETCH_CLASS, TrashItem::class);
        $totalBytes = array_sum(array_map(fn(TrashItem $i) => $i->file_size, $items));

        return [
            'items' => $items,
            'total' => $total,
            'total_bytes' => $totalBytes,
            'total_mb' => round($totalBytes / 1024 / 1024, 2),
            'page' => $page,
            'limit' => $limit,
            'total_pages' => (int)ceil($total / max(1, $limit)),
        ];
    }

    public function restore(int $screenshotId, int $userId): bool
    {
        return Database::transaction(function (PDO $pdo) use ($screenshotId, $userId) {
            $stmt1 = $pdo->prepare('UPDATE trash_items SET is_restored = 1, restored_at = NOW() WHERE screenshot_id = ? AND user_id = ?');
            $stmt1->execute([$screenshotId, $userId]);

            $stmt2 = $pdo->prepare('UPDATE screenshots SET deleted_at = NULL, status = "classified" WHERE id = ? AND user_id = ?');
            return $stmt2->execute([$screenshotId, $userId]);
        });
    }

    public function restoreAll(int $userId): int
    {
        return Database::transaction(function (PDO $pdo) use ($userId) {
            $select = $pdo->prepare('SELECT screenshot_id FROM trash_items WHERE user_id = ? AND is_restored = 0');
            $select->execute([$userId]);
            $ids = $select->fetchAll(PDO::FETCH_COLUMN);

            if (empty($ids)) {
                return 0;
            }

            $in = implode(',', array_fill(0, count($ids), '?'));
            $pdo->prepare("UPDATE trash_items SET is_restored = 1, restored_at = NOW() WHERE user_id = ? AND is_restored = 0")->execute([$userId]);

            $stmt = $pdo->prepare("UPDATE screenshots SET deleted_at = NULL, status = 'classified' WHERE user_id = ? AND id IN ({$in})");
            $stmt->execute(array_merge([$userId], $ids));

            return count($ids);
        });
    }

    public function permanentDelete(int $screenshotId, int $userId): bool
    {
        // 1. Get file path to unlink
        $stmt = $this->pdo->prepare('SELECT file_path FROM screenshots WHERE id = ? AND user_id = ?');
        $stmt->execute([$screenshotId, $userId]);
        $filePath = $stmt->fetchColumn();

        return Database::transaction(function (PDO $pdo) use ($screenshotId, $userId, $filePath) {
            $pdo->prepare('DELETE FROM trash_items WHERE screenshot_id = ? AND user_id = ?')->execute([$screenshotId, $userId]);
            $pdo->prepare('DELETE FROM screenshots WHERE id = ? AND user_id = ?')->execute([$screenshotId, $userId]);

            if ($filePath) {
                $fullPath = dirname(__DIR__, 2) . '/storage/' . ltrim($filePath, '/');
                if (file_exists($fullPath)) {
                    @unlink($fullPath);
                }
            }
            return true;
        });
    }

    public function emptyTrash(int $userId): int
    {
        $select = $this->pdo->prepare('SELECT s.id, s.file_path FROM trash_items t JOIN screenshots s ON s.id = t.screenshot_id WHERE t.user_id = ? AND t.is_restored = 0');
        $select->execute([$userId]);
        $rows = $select->fetchAll(PDO::FETCH_ASSOC);

        if (empty($rows)) {
            return 0;
        }

        Database::transaction(function (PDO $pdo) use ($userId, $rows) {
            $ids = array_column($rows, 'id');
            $in = implode(',', array_fill(0, count($ids), '?'));

            $pdo->prepare('DELETE FROM trash_items WHERE user_id = ?')->execute([$userId]);
            $stmt = $pdo->prepare("DELETE FROM screenshots WHERE user_id = ? AND id IN ({$in})");
            $stmt->execute(array_merge([$userId], $ids));

            foreach ($rows as $r) {
                if (!empty($r['file_path'])) {
                    $path = dirname(__DIR__, 2) . '/storage/' . ltrim($r['file_path'], '/');
                    if (file_exists($path)) {
                        @unlink($path);
                    }
                }
            }
        });

        return count($rows);
    }
}


<?php
declare(strict_types=1);

namespace App\Repositories;

use App\Core\Database;
use App\Models\CleanupJob;
use PDO;

class CleanupRepository
{
    private PDO $pdo;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
    }

    /**
     * Compute screenshots eligible for cleanup review
     */
    public function getEligibleScreenshots(int $userId): array
    {
        // 1. Get user retention settings
        $settingsStmt = $this->pdo->prepare('SELECT protect_kept_screenshots FROM user_settings WHERE user_id = ? LIMIT 1');
        $settingsStmt->execute([$userId]);
        $protectKept = (bool)($settingsStmt->fetchColumn() ?? 1);

        $keptCondition = $protectKept ? 'AND s.is_kept = 0' : '';

        // Query dated screenshots where event date has passed (>= 1 day after event)
        $sqlDated = "
            SELECT 
                s.id, s.file_path, s.file_size, s.category, s.extracted_title,
                d.event_date, 'Past event date (1-day retention rule)' as reason,
                'low' as risk_level, 1.0 as confidence
            FROM screenshots s
            JOIN dated_items d ON d.screenshot_id = s.id
            WHERE s.user_id = ? AND s.deleted_at IS NULL AND s.category = 'dated'
              AND d.event_date <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)
              {$keptCondition}
        ";

        // Query non-dated screenshots older than 7 days
        $sqlNonDated = "
            SELECT 
                s.id, s.file_path, s.file_size, s.category, s.extracted_title,
                DATE(s.uploaded_at) as event_date, 'Saved > 7 days ago (non-dated retention rule)' as reason,
                'low' as risk_level, 0.95 as confidence
            FROM screenshots s
            WHERE s.user_id = ? AND s.deleted_at IS NULL AND s.category = 'non_dated'
              AND s.uploaded_at <= DATE_SUB(NOW(), INTERVAL 7 DAY)
              {$keptCondition}
        ";

        $stmt1 = $this->pdo->prepare($sqlDated);
        $stmt1->execute([$userId]);
        $datedEligible = $stmt1->fetchAll(PDO::FETCH_ASSOC);

        $stmt2 = $this->pdo->prepare($sqlNonDated);
        $stmt2->execute([$userId]);
        $nonDatedEligible = $stmt2->fetchAll(PDO::FETCH_ASSOC);

        $baseUrl = rtrim(getenv('APP_URL') ?: 'http://localhost:8000', '/');
        $formatItem = function (array $item) use ($baseUrl): array {
            $item['file_size_formatted'] = round(((int)$item['file_size']) / 1024 / 1024, 2) . ' MB';
            $item['image_url'] = "{$baseUrl}/storage/" . ltrim($item['file_path'], '/');
            return $item;
        };

        $datedFormatted = array_map($formatItem, $datedEligible);
        $nonDatedFormatted = array_map($formatItem, $nonDatedEligible);

        $all = array_merge($datedFormatted, $nonDatedFormatted);
        $totalBytes = array_sum(array_column($all, 'file_size'));

        return [
            'total_eligible' => count($all),
            'total_bytes' => $totalBytes,
            'total_mb' => round($totalBytes / 1024 / 1024, 2),
            'dated_items' => $datedFormatted,
            'non_dated_items' => $nonDatedFormatted,
            'items' => $all,
        ];
    }

    public function createJob(int $userId, string $mode = 'manual'): CleanupJob
    {
        $stmt = $this->pdo->prepare(
            'INSERT INTO cleanup_jobs (user_id, status, mode, started_at)
             VALUES (?, "pending", ?, NOW())'
        );
        $stmt->execute([$userId, $mode]);
        $id = (int)$this->pdo->lastInsertId();

        $select = $this->pdo->prepare('SELECT * FROM cleanup_jobs WHERE id = ?');
        $select->execute([$id]);
        return $select->fetchObject(CleanupJob::class);
    }

    public function executeCleanup(int $jobId, int $userId, array $screenshotIds): array
    {
        if (empty($screenshotIds)) {
            return ['deleted_count' => 0, 'space_reclaimed_bytes' => 0, 'space_reclaimed_mb' => 0];
        }

        return Database::transaction(function (PDO $pdo) use ($jobId, $userId, $screenshotIds) {
            $inClause = implode(',', array_fill(0, count($screenshotIds), '?'));
            $params = array_merge([$userId], $screenshotIds);

            // Fetch screenshots to be deleted (respecting is_kept protection)
            $sql = "SELECT id, category, extracted_title, file_size 
                    FROM screenshots 
                    WHERE user_id = ? AND deleted_at IS NULL AND is_kept = 0 AND id IN ({$inClause})";
            $stmt = $pdo->prepare($sql);
            $stmt->execute($params);
            $screenshots = $stmt->fetchAll(PDO::FETCH_ASSOC);

            $deletedCount = 0;
            $reclaimedBytes = 0;

            $purgeDueAt = date('Y-m-d H:i:s', strtotime('+30 days'));

            $trashInsert = $pdo->prepare(
                'INSERT INTO trash_items (user_id, screenshot_id, original_category, original_title, file_size, trashed_at, purge_due_at)
                 VALUES (?, ?, ?, ?, ?, NOW(), ?)
                 ON DUPLICATE KEY UPDATE purge_due_at = VALUES(purge_due_at), is_restored = 0'
            );

            $softDeleteStmt = $pdo->prepare('UPDATE screenshots SET deleted_at = NOW(), status = "deleted" WHERE id = ?');
            $historyStmt = $pdo->prepare(
                'INSERT INTO cleanup_history (job_id, screenshot_id, original_title, file_size) VALUES (?, ?, ?, ?)'
            );

            foreach ($screenshots as $s) {
                $sId = (int)$s['id'];
                $size = (int)$s['file_size'];

                $trashInsert->execute([$userId, $sId, $s['category'], $s['extracted_title'], $size, $purgeDueAt]);
                $softDeleteStmt->execute([$sId]);
                $historyStmt->execute([$jobId, $sId, $s['extracted_title'], $size]);

                $deletedCount++;
                $reclaimedBytes += $size;
            }

            // Update cleanup job
            $updateJob = $pdo->prepare(
                'UPDATE cleanup_jobs 
                 SET status = "completed", total_screenshots = ?, space_reclaimed_bytes = ?, completed_at = NOW() 
                 WHERE id = ?'
            );
            $updateJob->execute([$deletedCount, $reclaimedBytes, $jobId]);

            // Notify user of completed cleanup
            $notif = $pdo->prepare(
                'INSERT INTO notifications (user_id, type, title, body, data) VALUES (?, ?, ?, ?, ?)'
            );
            $mb = round($reclaimedBytes / 1024 / 1024, 2);
            $notif->execute([
                $userId,
                'cleanup_completed',
                'Cleanup completed',
                "{$deletedCount} screenshots moved to recovery. Reclaimed {$mb} MB. Items can be restored within 30 days.",
                json_encode(['job_id' => $jobId, 'deleted_count' => $deletedCount, 'reclaimed_mb' => $mb]),
            ]);

            return [
                'job_id' => $jobId,
                'deleted_count' => $deletedCount,
                'space_reclaimed_bytes' => $reclaimedBytes,
                'space_reclaimed_mb' => $mb,
            ];
        });
    }

    public function getHistory(int $userId, int $limit = 20): array
    {
        $stmt = $this->pdo->prepare(
            'SELECT * FROM cleanup_jobs WHERE user_id = ? AND status = "completed" ORDER BY completed_at DESC LIMIT ?'
        );
        $stmt->bindValue(1, $userId, PDO::PARAM_INT);
        $stmt->bindValue(2, $limit, PDO::PARAM_INT);
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_CLASS, CleanupJob::class);
    }

    public function getStats(int $userId): array
    {
        $stmt = $this->pdo->prepare('
            SELECT 
                COUNT(*) as total_jobs,
                COALESCE(SUM(total_screenshots), 0) as total_cleaned_count,
                COALESCE(SUM(space_reclaimed_bytes), 0) as total_reclaimed_bytes
            FROM cleanup_jobs 
            WHERE user_id = ? AND status = "completed"
        ');
        $stmt->execute([$userId]);
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        $bytes = (int)($row['total_reclaimed_bytes'] ?? 0);
        return [
            'total_cleanup_runs' => (int)($row['total_jobs'] ?? 0),
            'total_screenshots_cleaned' => (int)($row['total_cleaned_count'] ?? 0),
            'total_reclaimed_bytes' => $bytes,
            'total_reclaimed_mb' => round($bytes / 1024 / 1024, 2),
        ];
    }
}


<?php
declare(strict_types=1);

namespace App\Repositories;

use App\Core\Database;
use App\Models\Screenshot;
use PDO;

class DashboardRepository
{
    private PDO $pdo;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
    }

    public function getSummary(int $userId): array
    {
        // 1. Screenshot counts
        $sStmt = $this->pdo->prepare('
            SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN category = "dated" THEN 1 ELSE 0 END) as dated,
                SUM(CASE WHEN category = "non_dated" THEN 1 ELSE 0 END) as non_dated,
                SUM(CASE WHEN category = "unclassified" THEN 1 ELSE 0 END) as unclassified,
                COALESCE(SUM(file_size), 0) as total_bytes
            FROM screenshots 
            WHERE user_id = ? AND deleted_at IS NULL
        ');
        $sStmt->execute([$userId]);
        $sRes = $sStmt->fetch(PDO::FETCH_ASSOC);

        // 2. Reminders counts
        $rStmt = $this->pdo->prepare('
            SELECT 
                SUM(CASE WHEN trigger_at < NOW() AND status = "pending" THEN 1 ELSE 0 END) as overdue,
                SUM(CASE WHEN trigger_at >= NOW() AND status != "cancelled" AND status != "completed" THEN 1 ELSE 0 END) as upcoming
            FROM reminders 
            WHERE user_id = ?
        ');
        $rStmt->execute([$userId]);
        $rRes = $rStmt->fetch(PDO::FETCH_ASSOC);

        // 3. Cleanable stats
        $cleanupRepo = new CleanupRepository();
        $eligible = $cleanupRepo->getEligibleScreenshots($userId);

        // 4. Recent screenshots
        $recentStmt = $this->pdo->prepare('
            SELECT * FROM screenshots 
            WHERE user_id = ? AND deleted_at IS NULL 
            ORDER BY uploaded_at DESC LIMIT 5
        ');
        $recentStmt->execute([$userId]);
        $recent = $recentStmt->fetchAll(PDO::FETCH_CLASS, Screenshot::class);

        return [
            'total_screenshots' => (int)($sRes['total'] ?? 0),
            'dated_screenshots_count' => (int)($sRes['dated'] ?? 0),
            'non_dated_screenshots_count' => (int)($sRes['non_dated'] ?? 0),
            'unclassified_screenshots_count' => (int)($sRes['unclassified'] ?? 0),
            'total_storage_mb' => round((int)($sRes['total_bytes'] ?? 0) / 1024 / 1024, 2),
            'cleanable_screenshots_count' => $eligible['total_eligible'],
            'estimated_reclaimable_storage_bytes' => $eligible['total_bytes'],
            'estimated_reclaimable_storage_mb' => $eligible['total_mb'],
            'overdue_reminders_count' => (int)($rRes['overdue'] ?? 0),
            'upcoming_reminders_count' => (int)($rRes['upcoming'] ?? 0),
            'recent_screenshots' => array_map(fn(Screenshot $s) => $s->toArray(), $recent),
        ];
    }

    public function getCategoryBreakdown(int $userId): array
    {
        $datedStmt = $this->pdo->prepare('
            SELECT d.category, COUNT(*) as count 
            FROM dated_items d
            JOIN screenshots s ON s.id = d.screenshot_id
            WHERE d.user_id = ? AND s.deleted_at IS NULL
            GROUP BY d.category
        ');
        $datedStmt->execute([$userId]);
        $datedBreakdown = $datedStmt->fetchAll(PDO::FETCH_KEY_PAIR);

        $nonDatedStmt = $this->pdo->prepare('
            SELECT n.category, COUNT(*) as count 
            FROM non_dated_items n
            JOIN screenshots s ON s.id = n.screenshot_id
            WHERE n.user_id = ? AND s.deleted_at IS NULL
            GROUP BY n.category
        ');
        $nonDatedStmt->execute([$userId]);
        $nonDatedBreakdown = $nonDatedStmt->fetchAll(PDO::FETCH_KEY_PAIR);

        return [
            'dated_categories' => $datedBreakdown,
            'non_dated_categories' => $nonDatedBreakdown,
        ];
    }

    public function getRecentActivity(int $userId, int $limit = 10): array
    {
        $stmt = $this->pdo->prepare('
            (SELECT "upload" as type, extracted_title as title, uploaded_at as timestamp, "Screenshot added" as description
             FROM screenshots WHERE user_id = ? AND deleted_at IS NULL)
            UNION ALL
            (SELECT "cleanup" as type, CONCAT("Cleaned ", total_screenshots, " screenshots") as title, completed_at as timestamp, CONCAT("Freed ", ROUND(space_reclaimed_bytes / 1024 / 1024, 1), " MB") as description
             FROM cleanup_jobs WHERE user_id = ? AND status = "completed")
            ORDER BY timestamp DESC LIMIT ?
        ');
        $stmt->bindValue(1, $userId, PDO::PARAM_INT);
        $stmt->bindValue(2, $userId, PDO::PARAM_INT);
        $stmt->bindValue(3, $limit, PDO::PARAM_INT);
        $stmt->execute();

        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }
}


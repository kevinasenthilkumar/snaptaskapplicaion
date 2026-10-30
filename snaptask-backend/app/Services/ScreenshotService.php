<?php
declare(strict_types=1);

namespace App\Services;

use App\Core\Logger;
use App\Helpers\FileUpload;
use App\Helpers\HashHelper;
use App\Models\Screenshot;
use App\Repositories\DatedItemRepository;
use App\Repositories\NonDatedItemRepository;
use App\Repositories\NotificationRepository;
use App\Repositories\ReminderRepository;
use App\Repositories\ScreenshotRepository;
use App\Repositories\TrashRepository;

class ScreenshotService
{
    private ScreenshotRepository $screenshotRepo;
    private DatedItemRepository $datedItemRepo;
    private NonDatedItemRepository $nonDatedItemRepo;
    private ReminderRepository $reminderRepo;
    private NotificationRepository $notificationRepo;
    private AiServiceClient $aiClient;

    public function __construct()
    {
        $this->screenshotRepo = new ScreenshotRepository();
        $this->datedItemRepo = new DatedItemRepository();
        $this->nonDatedItemRepo = new NonDatedItemRepository();
        $this->reminderRepo = new ReminderRepository();
        $this->notificationRepo = new NotificationRepository();
        $this->aiClient = new AiServiceClient();
    }

    public function upload(int $userId, array $file, ?string $localMediaId = null, ?string $capturedAt = null): Screenshot
    {
        $stored = FileUpload::save($file);
        $perceptualHash = HashHelper::computePerceptualHash($stored['absolute_path']);

        // Check exact duplicate by hash
        $existing = $this->screenshotRepo->findByHash($stored['file_hash'], $userId);
        if ($existing) {
            Logger::info("User {$userId} re-uploaded existing screenshot ID {$existing->id}");
            return $existing;
        }

        $screenshot = $this->screenshotRepo->create([
            'user_id' => $userId,
            'local_media_id' => $localMediaId,
            'file_path' => $stored['file_path'],
            'file_name' => $stored['file_name'],
            'file_hash' => $stored['file_hash'],
            'perceptual_hash' => $perceptualHash,
            'mime_type' => $stored['mime_type'],
            'file_size' => $stored['file_size'],
            'width' => $stored['width'],
            'height' => $stored['height'],
            'category' => 'unclassified',
            'status' => 'pending',
            'captured_at' => $capturedAt ?: date('Y-m-d H:i:s'),
        ]);

        return $screenshot;
    }

    public function analyze(int $screenshotId, int $userId): Screenshot
    {
        $screenshot = $this->screenshotRepo->findById($screenshotId, $userId);
        if (!$screenshot) {
            throw new \InvalidArgumentException('Screenshot not found.');
        }

        $fullPath = dirname(__DIR__, 2) . '/storage/' . ltrim($screenshot->file_path, '/');
        if (!file_exists($fullPath)) {
            throw new \RuntimeException('Screenshot physical file missing from storage.');
        }

        // Call Python AI Microservice
        $aiResult = $this->aiClient->analyzeScreenshot($fullPath);

        if (!$aiResult || empty($aiResult['success'])) {
            // Fallback: heuristic analysis if AI service is temporarily unavailable
            $updateData = [
                'status' => 'classified',
                'category' => 'unclassified',
                'extracted_title' => pathinfo($screenshot->file_name, PATHINFO_FILENAME),
            ];
            $this->screenshotRepo->update($screenshotId, $userId, $updateData);
            return $this->screenshotRepo->findById($screenshotId, $userId);
        }

        $data = $aiResult['data'] ?? [];
        $category = $data['category'] ?? 'unclassified';
        $confidence = (float)($data['classification_confidence'] ?? 0.85);
        $ocrText = $data['ocr_text'] ?? '';
        $title = !empty($data['extracted_title']) ? $data['extracted_title'] : 'Screenshot ' . date('M d');
        $desc = $data['extracted_description'] ?? null;
        $extractedDate = $data['extracted_date'] ?? null;
        $extractedTime = $data['extracted_time'] ?? null;
        $location = $data['extracted_location'] ?? null;
        $amount = isset($data['extracted_amount']) ? (float)$data['extracted_amount'] : null;
        $currency = $data['extracted_currency'] ?? 'USD';

        $updateData = [
            'category' => $category,
            'classification_confidence' => $confidence,
            'ocr_text' => $ocrText,
            'extracted_title' => $title,
            'extracted_description' => $desc,
            'extracted_date' => $extractedDate,
            'extracted_time' => $extractedTime,
            'extracted_location' => $location,
            'extracted_amount' => $amount,
            'extracted_currency' => $currency,
            'status' => 'classified',
            'ai_metadata' => $data['ai_metadata'] ?? [],
        ];

        $this->screenshotRepo->update($screenshotId, $userId, $updateData);

        // Auto-create category record
        if ($category === 'dated' && $extractedDate) {
            $subCategory = $data['sub_category'] ?? 'event';
            $datedItem = $this->datedItemRepo->create([
                'user_id' => $userId,
                'screenshot_id' => $screenshotId,
                'title' => $title,
                'description' => $desc,
                'category' => $subCategory,
                'event_date' => $extractedDate,
                'event_time' => $extractedTime,
                'location' => $location,
                'amount' => $amount,
                'currency' => $currency,
                'priority' => 'medium',
                'status' => 'active',
                'reminder_enabled' => 1,
                'reminder_at' => "{$extractedDate} 09:00:00",
            ]);

            // Auto-schedule Reminder on the event date (or 1 day prior)
            $this->reminderRepo->create([
                'user_id' => $userId,
                'dated_item_id' => $datedItem->id,
                'title' => "Reminder: {$title}",
                'note' => "Upcoming event on {$extractedDate}",
                'trigger_at' => "{$extractedDate} 09:00:00",
                'status' => 'pending',
            ]);
        } elseif ($category === 'non_dated') {
            $subCategory = $data['sub_category'] ?? 'idea';
            $this->nonDatedItemRepo->create([
                'user_id' => $userId,
                'screenshot_id' => $screenshotId,
                'title' => $title,
                'description' => $desc,
                'category' => $subCategory,
                'content_text' => $ocrText,
                'is_favorite' => 0,
                'color_label' => '#EDE7DE',
                'ai_metadata' => $data['ai_metadata'] ?? [],
            ]);
        }

        // Send push/inbox notification
        $this->notificationRepo->create([
            'user_id' => $userId,
            'type' => 'screenshot_classified',
            'title' => "Classified as " . ucfirst(str_replace('_', ' ', $category)),
            'body' => "\"{$title}\" has been analyzed and categorized.",
            'data' => ['screenshot_id' => $screenshotId, 'category' => $category],
        ]);

        return $this->screenshotRepo->findById($screenshotId, $userId);
    }

    public function bulkAnalyze(array $screenshotIds, int $userId): array
    {
        $results = [];
        foreach ($screenshotIds as $id) {
            try {
                $results[] = $this->analyze((int)$id, $userId)->toArray();
            } catch (\Throwable $e) {
                Logger::error("Bulk analyze failed for ID {$id}: " . $e->getMessage());
            }
        }
        return $results;
    }

    public function moveToTrash(int $id, int $userId): bool
    {
        $screenshot = $this->screenshotRepo->findById($id, $userId);
        if (!$screenshot) {
            throw new \InvalidArgumentException('Screenshot not found.');
        }

        $trashRepo = new TrashRepository();
        $this->screenshotRepo->softDelete($id, $userId);

        $purgeDueAt = date('Y-m-d H:i:s', strtotime('+30 days'));
        $pdo = \App\Core\Database::getConnection();
        $stmt = $pdo->prepare(
            'INSERT INTO trash_items (user_id, screenshot_id, original_category, original_title, file_size, trashed_at, purge_due_at)
             VALUES (?, ?, ?, ?, ?, NOW(), ?)
             ON DUPLICATE KEY UPDATE is_restored = 0, purge_due_at = VALUES(purge_due_at)'
        );
        return $stmt->execute([
            $userId,
            $id,
            $screenshot->category,
            $screenshot->extracted_title,
            $screenshot->file_size,
            $purgeDueAt,
        ]);
    }
}


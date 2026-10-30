<?php
declare(strict_types=1);

namespace App\Models;

class TrashItem
{
    public int $id;
    public int $user_id;
    public int $screenshot_id;
    public string $original_category;
    public ?string $original_title = null;
    public int $file_size = 0;
    public string $trashed_at;
    public string $purge_due_at;
    public int $is_restored = 0;
    public ?string $restored_at = null;
    public ?string $created_at = null;

    // Joined fields
    public ?string $file_path = null;

    public function toArray(): array
    {
        $now = time();
        $due = strtotime($this->purge_due_at);
        $daysLeft = max(0, (int)ceil(($due - $now) / 86400));

        $baseUrl = rtrim(getenv('APP_URL') ?: 'http://localhost:8000', '/');
        $imageUrl = $this->file_path ? "{$baseUrl}/storage/" . ltrim($this->file_path, '/') : null;

        return [
            'id' => (int)$this->id,
            'user_id' => (int)$this->user_id,
            'screenshot_id' => (int)$this->screenshot_id,
            'original_category' => $this->original_category,
            'original_title' => $this->original_title,
            'file_size' => (int)$this->file_size,
            'file_size_formatted' => round($this->file_size / 1024 / 1024, 2) . ' MB',
            'trashed_at' => $this->trashed_at,
            'purge_due_at' => $this->purge_due_at,
            'days_left' => $daysLeft,
            'is_restored' => (bool)$this->is_restored,
            'restored_at' => $this->restored_at,
            'image_url' => $imageUrl,
            'created_at' => $this->created_at,
        ];
    }
}


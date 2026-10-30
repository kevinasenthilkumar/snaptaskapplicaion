<?php
declare(strict_types=1);

namespace App\Models;

class DatedItem
{
    public int $id;
    public int $user_id;
    public int $screenshot_id;
    public string $title;
    public ?string $description = null;
    public string $category = 'event';
    public string $event_date;
    public ?string $event_time = null;
    public ?string $end_date = null;
    public ?string $location = null;
    public ?float $amount = null;
    public string $currency = 'USD';
    public string $priority = 'medium';
    public string $status = 'active';
    public int $reminder_enabled = 1;
    public ?string $reminder_at = null;
    public ?string $completed_at = null;
    public ?string $created_at = null;
    public ?string $updated_at = null;

    // Optional joined screenshot fields
    public ?string $file_path = null;
    public ?int $is_kept = null;

    public function toArray(): array
    {
        $baseUrl = rtrim(getenv('APP_URL') ?: 'http://localhost:8000', '/');
        $imageUrl = $this->file_path ? "{$baseUrl}/storage/" . ltrim($this->file_path, '/') : null;

        return [
            'id' => (int)$this->id,
            'user_id' => (int)$this->user_id,
            'screenshot_id' => (int)$this->screenshot_id,
            'title' => $this->title,
            'description' => $this->description,
            'category' => $this->category,
            'event_date' => $this->event_date,
            'event_time' => $this->event_time,
            'end_date' => $this->end_date,
            'location' => $this->location,
            'amount' => $this->amount !== null ? (float)$this->amount : null,
            'currency' => $this->currency,
            'priority' => $this->priority,
            'status' => $this->status,
            'reminder_enabled' => (bool)$this->reminder_enabled,
            'reminder_at' => $this->reminder_at,
            'is_kept' => (bool)($this->is_kept ?? 0),
            'image_url' => $imageUrl,
            'completed_at' => $this->completed_at,
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}


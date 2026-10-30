<?php
declare(strict_types=1);

namespace App\Models;

class CleanupJob
{
    public int $id;
    public int $user_id;
    public string $status = 'pending';
    public string $mode = 'manual';
    public int $total_screenshots = 0;
    public int $space_reclaimed_bytes = 0;
    public ?string $started_at = null;
    public ?string $completed_at = null;
    public ?string $created_at = null;

    public function toArray(): array
    {
        return [
            'id' => (int)$this->id,
            'user_id' => (int)$this->user_id,
            'status' => $this->status,
            'mode' => $this->mode,
            'total_screenshots' => (int)$this->total_screenshots,
            'space_reclaimed_bytes' => (int)$this->space_reclaimed_bytes,
            'space_reclaimed_mb' => round($this->space_reclaimed_bytes / 1024 / 1024, 2),
            'started_at' => $this->started_at,
            'completed_at' => $this->completed_at,
            'created_at' => $this->created_at,
        ];
    }
}


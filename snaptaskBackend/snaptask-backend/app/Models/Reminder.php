<?php
declare(strict_types=1);

namespace App\Models;

class Reminder
{
    public int $id;
    public int $user_id;
    public ?int $dated_item_id = null;
    public string $title;
    public ?string $note = null;
    public string $trigger_at;
    public int $snooze_count = 0;
    public string $status = 'pending';
    public ?string $snoozed_until = null;
    public ?string $completed_at = null;
    public ?string $created_at = null;
    public ?string $updated_at = null;

    public function toArray(): array
    {
        return [
            'id' => (int)$this->id,
            'user_id' => (int)$this->user_id,
            'dated_item_id' => $this->dated_item_id !== null ? (int)$this->dated_item_id : null,
            'title' => $this->title,
            'note' => $this->note,
            'trigger_at' => $this->trigger_at,
            'snooze_count' => (int)$this->snooze_count,
            'status' => $this->status,
            'snoozed_until' => $this->snoozed_until,
            'completed_at' => $this->completed_at,
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}


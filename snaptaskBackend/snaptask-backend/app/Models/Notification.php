<?php
declare(strict_types=1);

namespace App\Models;

class Notification
{
    public int $id;
    public int $user_id;
    public string $type;
    public string $title;
    public string $body;
    public mixed $data = null;
    public int $is_read = 0;
    public ?string $read_at = null;
    public ?string $created_at = null;

    public function toArray(): array
    {
        return [
            'id' => (int)$this->id,
            'user_id' => (int)$this->user_id,
            'type' => $this->type,
            'title' => $this->title,
            'body' => $this->body,
            'data' => is_string($this->data) ? json_decode($this->data, true) : $this->data,
            'is_read' => (bool)$this->is_read,
            'read_at' => $this->read_at,
            'created_at' => $this->created_at,
        ];
    }
}


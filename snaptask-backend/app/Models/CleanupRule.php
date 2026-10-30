<?php
declare(strict_types=1);

namespace App\Models;

class CleanupRule
{
    public int $id;
    public int $user_id;
    public string $rule_name;
    public string $item_type;
    public int $retention_days = 7;
    public int $notify_days_before = 1;
    public int $is_active = 1;
    public ?string $created_at = null;
    public ?string $updated_at = null;

    public function toArray(): array
    {
        return [
            'id' => (int)$this->id,
            'user_id' => (int)$this->user_id,
            'rule_name' => $this->rule_name,
            'item_type' => $this->item_type,
            'retention_days' => (int)$this->retention_days,
            'notify_days_before' => (int)$this->notify_days_before,
            'is_active' => (bool)$this->is_active,
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}


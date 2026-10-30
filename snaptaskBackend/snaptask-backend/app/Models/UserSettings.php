<?php
declare(strict_types=1);

namespace App\Models;

class UserSettings
{
    public int $id;
    public int $user_id;
    public int $auto_scan_screenshots = 1;
    public int $auto_categorize = 1;
    public int $default_lead_time_days = 1;
    public string $cleanup_frequency = 'manual';
    public int $cleanup_reminders_enabled = 1;
    public int $cleanup_summaries_enabled = 0;
    public int $protect_kept_screenshots = 1;
    public int $ai_organization_enabled = 1;
    public int $share_analytics = 0;
    public int $trash_retention_days = 30;
    public ?string $created_at = null;
    public ?string $updated_at = null;

    public function toArray(): array
    {
        return [
            'id' => (int)$this->id,
            'user_id' => (int)$this->user_id,
            'auto_scan_screenshots' => (bool)$this->auto_scan_screenshots,
            'auto_categorize' => (bool)$this->auto_categorize,
            'default_lead_time_days' => (int)$this->default_lead_time_days,
            'cleanup_frequency' => $this->cleanup_frequency,
            'cleanup_reminders_enabled' => (bool)$this->cleanup_reminders_enabled,
            'cleanup_summaries_enabled' => (bool)$this->cleanup_summaries_enabled,
            'protect_kept_screenshots' => (bool)$this->protect_kept_screenshots,
            'ai_organization_enabled' => (bool)$this->ai_organization_enabled,
            'share_analytics' => (bool)$this->share_analytics,
            'trash_retention_days' => (int)$this->trash_retention_days,
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}


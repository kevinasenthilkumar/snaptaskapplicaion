<?php
declare(strict_types=1);

namespace App\Validators;

use App\Helpers\Validator;

class SettingsValidator
{
    public static function validateGeneral(array $data): Validator
    {
        return Validator::make($data, [
            'auto_scan_screenshots' => 'boolean',
            'auto_categorize' => 'boolean',
            'cleanup_reminders_enabled' => 'boolean',
            'cleanup_summaries_enabled' => 'boolean',
            'protect_kept_screenshots' => 'boolean',
            'cleanup_frequency' => 'in:manual,daily,weekly',
            'trash_retention_days' => 'numeric',
        ]);
    }

    public static function validateAutomation(array $data): Validator
    {
        return Validator::make($data, [
            'mode' => 'required|in:daily,manual',
            'daily_time' => 'time',
            'requires_consent' => 'boolean',
        ]);
    }

    public static function validateCleanupRule(array $data): Validator
    {
        return Validator::make($data, [
            'rule_name' => 'required|min:1|max:100',
            'item_type' => 'required|in:dated,non_dated,receipts,tickets,all',
            'retention_days' => 'required|numeric|min:1|max:365',
            'notify_days_before' => 'required|numeric|min:0|max:30',
        ]);
    }

    public static function validatePrivacy(array $data): Validator
    {
        return Validator::make($data, [
            'ai_organization_enabled' => 'boolean',
            'share_analytics' => 'boolean',
        ]);
    }
}


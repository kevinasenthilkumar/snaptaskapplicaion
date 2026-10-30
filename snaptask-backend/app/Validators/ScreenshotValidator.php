<?php
declare(strict_types=1);

namespace App\Validators;

use App\Helpers\Validator;

class ScreenshotValidator
{
    public static function validateUpdate(array $data): Validator
    {
        return Validator::make($data, [
            'is_kept' => 'boolean',
            'category' => 'in:dated,non_dated,unclassified',
            'extracted_title' => 'max:255',
            'extracted_date' => 'date',
            'extracted_time' => 'time',
        ]);
    }

    public static function validateBulkAnalyze(array $data): Validator
    {
        return Validator::make($data, [
            'screenshot_ids' => 'required|array',
        ]);
    }
}


<?php
declare(strict_types=1);

namespace App\Validators;

use App\Helpers\Validator;

class CleanupValidator
{
    public static function validateReview(array $data): Validator
    {
        return Validator::make($data, [
            'screenshot_ids' => 'required|array',
        ]);
    }

    public static function validateConfirm(array $data): Validator
    {
        return Validator::make($data, [
            'screenshot_ids' => 'required|array',
        ]);
    }
}


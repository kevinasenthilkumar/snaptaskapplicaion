<?php
declare(strict_types=1);

namespace App\Validators;

use App\Helpers\Validator;

class ReminderValidator
{
    public static function validateCreate(array $data): Validator
    {
        return Validator::make($data, [
            'title' => 'required|min:1|max:255',
            'trigger_at' => 'required',
            'dated_item_id' => 'numeric',
        ]);
    }

    public static function validateUpdate(array $data): Validator
    {
        return Validator::make($data, [
            'title' => 'min:1|max:255',
            'status' => 'in:pending,triggered,snoozed,completed,cancelled',
        ]);
    }

    public static function validateSnooze(array $data): Validator
    {
        return Validator::make($data, [
            'minutes' => 'numeric',
            'snoozed_until' => 'max:50',
        ]);
    }
}


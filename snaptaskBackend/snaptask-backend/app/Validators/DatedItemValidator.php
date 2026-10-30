<?php
declare(strict_types=1);

namespace App\Validators;

use App\Helpers\Validator;

class DatedItemValidator
{
    public static function validateCreate(array $data): Validator
    {
        return Validator::make($data, [
            'screenshot_id' => 'required|numeric',
            'title' => 'required|min:1|max:255',
            'category' => 'required|in:ticket,bill,event,reservation,appointment,deadline,travel,booking,other',
            'event_date' => 'required|date',
            'event_time' => 'time',
            'end_date' => 'date',
            'amount' => 'numeric',
            'priority' => 'in:low,medium,high',
        ]);
    }

    public static function validateUpdate(array $data): Validator
    {
        return Validator::make($data, [
            'title' => 'min:1|max:255',
            'category' => 'in:ticket,bill,event,reservation,appointment,deadline,travel,booking,other',
            'event_date' => 'date',
            'event_time' => 'time',
            'end_date' => 'date',
            'amount' => 'numeric',
            'priority' => 'in:low,medium,high',
            'status' => 'in:active,completed,expired,cancelled',
        ]);
    }

    public static function validateSnooze(array $data): Validator
    {
        return Validator::make($data, [
            'snooze_until' => 'required',
        ]);
    }
}


<?php
declare(strict_types=1);

namespace App\Validators;

use App\Helpers\Validator;

class NonDatedItemValidator
{
    public static function validateCreate(array $data): Validator
    {
        return Validator::make($data, [
            'screenshot_id' => 'required|numeric',
            'title' => 'required|min:1|max:255',
            'category' => 'required|in:recipe,design,shopping,article,reference,receipt,idea,other',
        ]);
    }

    public static function validateUpdate(array $data): Validator
    {
        return Validator::make($data, [
            'title' => 'min:1|max:255',
            'category' => 'in:recipe,design,shopping,article,reference,receipt,idea,other',
            'is_favorite' => 'boolean',
        ]);
    }

    public static function validateTag(array $data): Validator
    {
        return Validator::make($data, [
            'name' => 'required|min:1|max:50',
            'color' => 'max:20',
        ]);
    }
}


<?php
declare(strict_types=1);

namespace App\Validators;

use App\Helpers\Validator;

class AuthValidator
{
    public static function validateRegister(array $data): Validator
    {
        return Validator::make($data, [
            'name' => 'required|min:2|max:120',
            'email' => 'required|email|max:255',
            'password' => 'required|min:8|max:100',
        ]);
    }

    public static function validateLogin(array $data): Validator
    {
        return Validator::make($data, [
            'email' => 'required|email',
            'password' => 'required',
        ]);
    }

    public static function validateRefresh(array $data): Validator
    {
        return Validator::make($data, [
            'refresh_token' => 'required',
        ]);
    }

    public static function validateProfile(array $data): Validator
    {
        return Validator::make($data, [
            'name' => 'min:2|max:120',
            'avatar_url' => 'max:500',
        ]);
    }

    public static function validateChangePassword(array $data): Validator
    {
        return Validator::make($data, [
            'current_password' => 'required',
            'new_password' => 'required|min:8|max:100',
        ]);
    }
}


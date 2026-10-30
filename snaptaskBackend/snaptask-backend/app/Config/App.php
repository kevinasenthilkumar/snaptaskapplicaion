<?php
declare(strict_types=1);

namespace App\Config;

class App
{
    public static function name(): string
    {
        return getenv('APP_NAME') ?: 'SnapTask';
    }

    public static function env(): string
    {
        return getenv('APP_ENV') ?: 'local';
    }

    public static function debug(): bool
    {
        return getenv('APP_DEBUG') === 'true';
    }

    public static function url(): string
    {
        return rtrim(getenv('APP_URL') ?: 'http://localhost:8000', '/');
    }
}


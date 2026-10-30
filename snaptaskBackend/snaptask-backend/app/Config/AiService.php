<?php
declare(strict_types=1);

namespace App\Config;

class AiService
{
    public static function url(): string
    {
        return rtrim(getenv('AI_SERVICE_URL') ?: 'http://localhost:8001', '/');
    }

    public static function key(): string
    {
        return getenv('AI_SERVICE_KEY') ?: 'snaptask_internal_ai_secret_key_2026';
    }

    public static function timeout(): int
    {
        return (int)(getenv('AI_SERVICE_TIMEOUT') ?: 30);
    }
}


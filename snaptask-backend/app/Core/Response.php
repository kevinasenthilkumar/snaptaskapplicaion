<?php
declare(strict_types=1);

namespace App\Core;

/**
 * Standardized REST API Response Builder
 */
class Response
{
    /**
     * Send a successful JSON response
     */
    public static function json(
        mixed $data = [],
        string $message = 'Success',
        int $statusCode = 200,
        array $meta = [],
        array $headers = []
    ): void {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=utf-8');

        foreach ($headers as $key => $value) {
            header("{$key}: {$value}");
        }

        echo json_encode([
            'success' => true,
            'message' => $message,
            'data' => $data,
            'errors' => [],
            'meta' => $meta,
        ], JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
        exit;
    }

    /**
     * Send an error JSON response
     */
    public static function error(
        string $message,
        int $statusCode = 400,
        array $errors = [],
        string $errorCode = 'BAD_REQUEST',
        array $headers = []
    ): void {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=utf-8');

        foreach ($headers as $key => $value) {
            header("{$key}: {$value}");
        }

        echo json_encode([
            'success' => false,
            'message' => $message,
            'errors' => $errors,
            'error_code' => $errorCode,
        ], JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
        exit;
    }

    public static function created(mixed $data = [], string $message = 'Resource created successfully'): void
    {
        self::json($data, $message, 201);
    }

    public static function notFound(string $message = 'Resource not found'): void
    {
        self::error($message, 404, [], 'NOT_FOUND');
    }

    public static function unauthorized(string $message = 'Unauthorized'): void
    {
        self::error($message, 401, [], 'UNAUTHORIZED');
    }

    public static function forbidden(string $message = 'Forbidden'): void
    {
        self::error($message, 403, [], 'FORBIDDEN');
    }

    public static function validationError(array $errors, string $message = 'Validation failed'): void
    {
        self::error($message, 422, $errors, 'UNPROCESSABLE_ENTITY');
    }

    public static function rateLimited(string $message = 'Too many requests. Please slow down.'): void
    {
        self::error($message, 429, [], 'RATE_LIMITED');
    }

    public static function serverError(string $message = 'Internal server error'): void
    {
        self::error($message, 500, [], 'SERVER_ERROR');
    }
}


<?php
declare(strict_types=1);

namespace App\Core;

use Throwable;

/**
 * Global Exception & Error Handler
 */
class ErrorHandler
{
    public static function register(): void
    {
        error_reporting(E_ALL);

        set_error_handler(function (int $errno, string $errstr, string $errfile, int $errline) {
            if (!(error_reporting() & $errno)) {
                return false;
            }
            Logger::error("PHP Error [{$errno}]: {$errstr} in {$errfile}:{$errline}");
            return true;
        });

        set_exception_handler([self::class, 'handleException']);

        register_shutdown_function(function () {
            $error = error_get_last();
            if ($error && in_array($error['type'], [E_ERROR, E_PARSE, E_CORE_ERROR, E_COMPILE_ERROR], true)) {
                Logger::error("Fatal Error: {$error['message']} in {$error['file']}:{$error['line']}");
                Response::serverError('A fatal application error occurred.');
            }
        });
    }

    public static function handleException(Throwable $e): void
    {
        Logger::error("Uncaught Exception: " . $e->getMessage(), [
            'file' => $e->getFile(),
            'line' => $e->getLine(),
            'trace' => $e->getTraceAsString(),
        ]);

        $debug = getenv('APP_DEBUG') === 'true';
        $message = $debug ? $e->getMessage() : 'An unexpected server error occurred.';
        $errors = $debug ? [
            'exception' => get_class($e),
            'file' => $e->getFile(),
            'line' => $e->getLine(),
        ] : [];

        Response::error($message, 500, $errors, 'SERVER_ERROR');
    }
}


<?php
declare(strict_types=1);

/**
 * SnapTask REST API — Single Entry Point
 */

$rootDir = dirname(__DIR__);

// 1. Autoloader (Composer or Native PSR-4 fallback)
if (file_exists($rootDir . '/vendor/autoload.php')) {
    require_once $rootDir . '/vendor/autoload.php';
} else {
    // Native PSR-4 autoloader fallback
    spl_autoload_register(function (string $class) use ($rootDir) {
        $prefix = 'App\\';
        $baseDir = $rootDir . '/app/';

        $len = strlen($prefix);
        if (strncmp($prefix, $class, $len) !== 0) {
            return;
        }

        $relativeClass = substr($class, $len);
        $file = $baseDir . str_replace('\\', '/', $relativeClass) . '.php';

        if (file_exists($file)) {
            require_once $file;
        }
    });
}

// 2. Load .env file
$envFile = $rootDir . '/.env';
if (!file_exists($envFile) && file_exists($rootDir . '/.env.example')) {
    $envFile = $rootDir . '/.env.example';
}

if (file_exists($envFile)) {
    $lines = file($envFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    foreach ($lines as $line) {
        $line = trim($line);
        if ($line === '' || str_starts_with($line, '#')) {
            continue;
        }
        if (str_contains($line, '=')) {
            [$key, $value] = explode('=', $line, 2);
            $key = trim($key);
            $value = trim($value, " \t\n\r\0\x0B\"'");
            $_ENV[$key] = $value;
            putenv("{$key}={$value}");
        }
    }
}

// 3. Register Global Error Handler
\App\Core\ErrorHandler::register();

// 4. Serve static storage files if requested directly in built-in server
$requestUri = $_SERVER['REQUEST_URI'] ?? '/';
$requestPath = parse_url($requestUri, PHP_URL_PATH) ?? '/';

if (str_starts_with($requestPath, '/storage/')) {
    $filePath = $rootDir . '/' . ltrim($requestPath, '/');
    if (file_exists($filePath) && !is_dir($filePath)) {
        $mime = mime_content_type($filePath) ?: 'application/octet-stream';
        header("Content-Type: {$mime}");
        header('Content-Length: ' . filesize($filePath));
        readfile($filePath);
        exit;
    }
}

// 5. Initialize Router & Request
$router = new \App\Core\Router();
$request = new \App\Core\Request();

// 6. Load Routes
require_once $rootDir . '/routes/api.php';

// 7. Dispatch Request
$router->dispatch($request);


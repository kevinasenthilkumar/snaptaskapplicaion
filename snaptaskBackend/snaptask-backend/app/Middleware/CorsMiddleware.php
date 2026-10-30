<?php
declare(strict_types=1);

namespace App\Middleware;

use App\Core\Request;
use App\Core\Response;

class CorsMiddleware
{
    public function handle(Request $request): void
    {
        $allowedOrigin = getenv('CORS_ALLOWED_ORIGINS') ?: '*';
        $origin = $_SERVER['HTTP_ORIGIN'] ?? '*';

        header("Access-Control-Allow-Origin: {$allowedOrigin}");
        header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH');
        header('Access-Control-Allow-Headers: Authorization, Content-Type, Accept, X-Requested-With, Origin');
        header('Access-Control-Allow-Credentials: true');
        header('Access-Control-Max-Age: 86400');

        if ($request->getMethod() === 'OPTIONS') {
            Response::json(['cors' => true], 'Preflight OK', 200);
        }
    }
}


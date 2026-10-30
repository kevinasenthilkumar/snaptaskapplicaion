<?php
declare(strict_types=1);

namespace App\Middleware;

use App\Core\Database;
use App\Core\Jwt;
use App\Core\Request;
use App\Core\Response;
use PDO;

class AuthMiddleware
{
    public function handle(Request $request): void
    {
        $token = $request->getBearerToken();
        if (!$token) {
            Response::unauthorized('Authentication token missing. Please provide a valid Bearer token.');
            return;
        }

        $payload = Jwt::decode($token);
        if (!$payload || !isset($payload['sub'])) {
            Response::unauthorized('Invalid or expired authentication token.');
            return;
        }

        $userId = (int)$payload['sub'];
        $pdo = Database::getConnection();
        $stmt = $pdo->prepare('SELECT id, email, name, avatar_url, status FROM users WHERE id = ? LIMIT 1');
        $stmt->execute([$userId]);
        $user = $stmt->fetch(PDO::FETCH_OBJ);

        if (!$user) {
            Response::unauthorized('User associated with this token no longer exists.');
            return;
        }

        if ($user->status !== 'active') {
            Response::forbidden('User account is currently ' . $user->status);
            return;
        }

        $request->setUser($user);
    }
}


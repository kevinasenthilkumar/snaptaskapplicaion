<?php
declare(strict_types=1);

namespace App\Repositories;

use App\Core\Database;
use App\Models\User;
use PDO;

class UserRepository
{
    private PDO $pdo;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
    }

    public function findById(int $id): ?User
    {
        $stmt = $this->pdo->prepare('SELECT * FROM users WHERE id = ? LIMIT 1');
        $stmt->execute([$id]);
        $row = $stmt->fetchObject(User::class);
        return $row ?: null;
    }

    public function findByEmail(string $email): ?User
    {
        $stmt = $this->pdo->prepare('SELECT * FROM users WHERE email = ? LIMIT 1');
        $stmt->execute([strtolower(trim($email))]);
        $row = $stmt->fetchObject(User::class);
        return $row ?: null;
    }

    public function create(array $data): User
    {
        $stmt = $this->pdo->prepare(
            'INSERT INTO users (email, password_hash, name, avatar_url, status) VALUES (?, ?, ?, ?, ?)'
        );
        $stmt->execute([
            strtolower(trim($data['email'])),
            $data['password_hash'],
            $data['name'],
            $data['avatar_url'] ?? null,
            $data['status'] ?? 'active',
        ]);
        $id = (int)$this->pdo->lastInsertId();
        return $this->findById($id);
    }

    public function updateProfile(int $id, array $data): bool
    {
        $fields = [];
        $params = [];
        if (isset($data['name'])) {
            $fields[] = 'name = ?';
            $params[] = $data['name'];
        }
        if (array_key_exists('avatar_url', $data)) {
            $fields[] = 'avatar_url = ?';
            $params[] = $data['avatar_url'];
        }

        if (empty($fields)) {
            return false;
        }

        $params[] = $id;
        $sql = 'UPDATE users SET ' . implode(', ', $fields) . ', updated_at = NOW() WHERE id = ?';
        $stmt = $this->pdo->prepare($sql);
        return $stmt->execute($params);
    }

    public function updatePassword(int $id, string $newPasswordHash): bool
    {
        $stmt = $this->pdo->prepare('UPDATE users SET password_hash = ?, updated_at = NOW() WHERE id = ?');
        return $stmt->execute([$newPasswordHash, $id]);
    }

    public function createSession(int $userId, string $tokenId, string $refreshTokenHash, ?string $deviceName, ?string $ip, ?string $userAgent, int $expiresInSeconds): void
    {
        $expiresAt = date('Y-m-d H:i:s', time() + $expiresInSeconds);
        $stmt = $this->pdo->prepare(
            'INSERT INTO user_sessions (user_id, token_id, refresh_token_hash, device_name, ip_address, user_agent, expires_at)
             VALUES (?, ?, ?, ?, ?, ?, ?)'
        );
        $stmt->execute([$userId, $tokenId, $refreshTokenHash, $deviceName, $ip, $userAgent, $expiresAt]);
    }

    public function findSessionByTokenId(string $tokenId): ?object
    {
        $stmt = $this->pdo->prepare('SELECT * FROM user_sessions WHERE token_id = ? AND expires_at > NOW() LIMIT 1');
        $stmt->execute([$tokenId]);
        $res = $stmt->fetch(PDO::FETCH_OBJ);
        return $res ?: null;
    }

    public function deleteSessionByTokenId(string $tokenId): bool
    {
        $stmt = $this->pdo->prepare('DELETE FROM user_sessions WHERE token_id = ?');
        return $stmt->execute([$tokenId]);
    }

    public function deleteAllSessions(int $userId): bool
    {
        $stmt = $this->pdo->prepare('DELETE FROM user_sessions WHERE user_id = ?');
        return $stmt->execute([$userId]);
    }
}


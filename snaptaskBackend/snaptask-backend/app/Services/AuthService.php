<?php
declare(strict_types=1);

namespace App\Services;

use App\Core\Jwt;
use App\Models\User;
use App\Repositories\SettingsRepository;
use App\Repositories\UserRepository;

class AuthService
{
    private UserRepository $userRepo;
    private SettingsRepository $settingsRepo;

    public function __construct()
    {
        $this->userRepo = new UserRepository();
        $this->settingsRepo = new SettingsRepository();
    }

    public function register(string $name, string $email, string $password): array
    {
        $existing = $this->userRepo->findByEmail($email);
        if ($existing) {
            throw new \InvalidArgumentException('A user with this email address already exists.');
        }

        $passwordHash = password_hash($password, PASSWORD_BCRYPT);
        $user = $this->userRepo->create([
            'name' => $name,
            'email' => $email,
            'password_hash' => $passwordHash,
            'status' => 'active',
        ]);

        // Initialize default user settings and automation settings
        $this->settingsRepo->getSettings($user->id);
        $this->settingsRepo->getAutomation($user->id);

        $tokens = $this->generateTokens($user);

        return [
            'user' => $user->toArray(),
            'access_token' => $tokens['access_token'],
            'refresh_token' => $tokens['refresh_token'],
            'expires_in' => $tokens['expires_in'],
        ];
    }

    public function login(string $email, string $password, ?string $deviceName = null, ?string $ip = null, ?string $userAgent = null): array
    {
        $user = $this->userRepo->findByEmail($email);
        if (!$user) {
            throw new \InvalidArgumentException('Invalid email or password.');
        }

        if (!password_verify($password, $user->password_hash)) {
            throw new \InvalidArgumentException('Invalid email or password.');
        }

        if ($user->status !== 'active') {
            throw new \RuntimeException('Account is currently ' . $user->status);
        }

        $tokens = $this->generateTokens($user, $deviceName, $ip, $userAgent);

        return [
            'user' => $user->toArray(),
            'access_token' => $tokens['access_token'],
            'refresh_token' => $tokens['refresh_token'],
            'expires_in' => $tokens['expires_in'],
        ];
    }

    public function refresh(string $refreshToken, ?string $ip = null, ?string $userAgent = null): array
    {
        $payload = Jwt::decode($refreshToken);
        if (!$payload || !isset($payload['sub'], $payload['jti'], $payload['type']) || $payload['type'] !== 'refresh') {
            throw new \InvalidArgumentException('Invalid or expired refresh token.');
        }

        $tokenId = $payload['jti'];
        $session = $this->userRepo->findSessionByTokenId($tokenId);
        if (!$session) {
            throw new \InvalidArgumentException('Refresh token session revoked or expired.');
        }

        $userId = (int)$payload['sub'];
        $user = $this->userRepo->findById($userId);
        if (!$user || $user->status !== 'active') {
            throw new \InvalidArgumentException('User account invalid or suspended.');
        }

        // Delete old session (Refresh Token Rotation)
        $this->userRepo->deleteSessionByTokenId($tokenId);

        // Generate fresh token pair
        $tokens = $this->generateTokens($user, $session->device_name, $ip, $userAgent);

        return [
            'access_token' => $tokens['access_token'],
            'refresh_token' => $tokens['refresh_token'],
            'expires_in' => $tokens['expires_in'],
        ];
    }

    public function logout(int $userId): bool
    {
        return $this->userRepo->deleteAllSessions($userId);
    }

    public function updateProfile(int $userId, array $data): User
    {
        $this->userRepo->updateProfile($userId, $data);
        return $this->userRepo->findById($userId);
    }

    public function changePassword(int $userId, string $currentPassword, string $newPassword): bool
    {
        $user = $this->userRepo->findById($userId);
        if (!$user || !password_verify($currentPassword, $user->password_hash)) {
            throw new \InvalidArgumentException('Current password does not match our records.');
        }

        $newHash = password_hash($newPassword, PASSWORD_BCRYPT);
        return $this->userRepo->updatePassword($userId, $newHash);
    }

    private function generateTokens(User $user, ?string $deviceName = null, ?string $ip = null, ?string $userAgent = null): array
    {
        $accessExpiry = (int)(getenv('JWT_ACCESS_EXPIRY') ?: 3600); // 1 hour
        $refreshExpiry = (int)(getenv('JWT_REFRESH_EXPIRY') ?: 2592000); // 30 days

        $now = time();
        $tokenId = bin2hex(random_bytes(16));

        // Access Token
        $accessPayload = [
            'sub' => $user->id,
            'email' => $user->email,
            'name' => $user->name,
            'type' => 'access',
            'iat' => $now,
            'exp' => $now + $accessExpiry,
        ];
        $accessToken = Jwt::encode($accessPayload, null, $accessExpiry);

        // Refresh Token
        $refreshPayload = [
            'sub' => $user->id,
            'jti' => $tokenId,
            'type' => 'refresh',
            'iat' => $now,
            'exp' => $now + $refreshExpiry,
        ];
        $refreshToken = Jwt::encode($refreshPayload, null, $refreshExpiry);

        // Track session in DB
        $tokenHash = hash('sha256', $refreshToken);
        $this->userRepo->createSession($user->id, $tokenId, $tokenHash, $deviceName, $ip, $userAgent, $refreshExpiry);

        return [
            'access_token' => $accessToken,
            'refresh_token' => $refreshToken,
            'expires_in' => $accessExpiry,
        ];
    }
}


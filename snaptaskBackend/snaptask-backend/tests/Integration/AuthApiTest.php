<?php
declare(strict_types=1);

namespace Tests\Integration;

use App\Core\Jwt;
use App\Helpers\HashHelper;
use PHPUnit\Framework\TestCase;

class AuthApiTest extends TestCase
{
    public function testPasswordHashVerification(): void
    {
        $rawPassword = 'Password123!';
        $hash = password_hash($rawPassword, PASSWORD_BCRYPT);

        $this->assertTrue(password_verify('Password123!', $hash));
        $this->assertFalse(password_verify('WrongPassword', $hash));
    }

    public function testTokenExpirationCalculation(): void
    {
        $now = time();
        $expiry = 3600;
        $payload = [
            'sub' => 1,
            'exp' => $now + $expiry,
        ];

        $token = Jwt::encode($payload, 'test_secret_key_32_characters_minimum_12345');
        $decoded = Jwt::decode($token, 'test_secret_key_32_characters_minimum_12345');

        $this->assertNotNull($decoded);
        $this->assertGreaterThan($now, $decoded['exp']);
    }
}


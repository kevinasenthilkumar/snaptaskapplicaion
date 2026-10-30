<?php
declare(strict_types=1);

namespace Tests\Unit;

use App\Core\Jwt;
use PHPUnit\Framework\TestCase;

class AuthServiceTest extends TestCase
{
    private string $secret = 'test_secret_key_32_characters_minimum_12345';

    public function testJwtEncodeAndDecode(): void
    {
        $payload = [
            'sub' => 42,
            'email' => 'demo@snaptask.app',
            'role' => 'user',
        ];

        $token = Jwt::encode($payload, $this->secret, 3600);
        $this->assertIsString($token);

        $decoded = Jwt::decode($token, $this->secret);
        $this->assertIsArray($decoded);
        $this->assertEquals(42, $decoded['sub']);
        $this->assertEquals('demo@snaptask.app', $decoded['email']);
    }

    public function testJwtTamperedSignatureFails(): void
    {
        $payload = ['sub' => 1];
        $token = Jwt::encode($payload, $this->secret, 3600);

        // Tamper with payload
        $parts = explode('.', $token);
        $parts[1] = base64_encode(json_encode(['sub' => 999]));
        $tamperedToken = implode('.', $parts);

        $decoded = Jwt::decode($tamperedToken, $this->secret);
        $this->assertNull($decoded);
    }

    public function testJwtExpiredTokenReturnsNull(): void
    {
        $payload = ['sub' => 1];
        // Expired 10 seconds ago
        $token = Jwt::encode($payload, $this->secret, -10);

        $decoded = Jwt::decode($token, $this->secret);
        $this->assertNull($decoded);
    }
}


<?php
declare(strict_types=1);

namespace App\Core;

/**
 * Pure PHP JSON Web Token (JWT) Implementation
 * RFC 7519 compliant with HS256 signature
 */
class Jwt
{
    private static function base64UrlEncode(string $data): string
    {
        return rtrim(strtr(base64_encode($data), '+/', '-_'), '=');
    }

    private static function base64UrlDecode(string $data): string
    {
        return base64_decode(strtr($data, '-_', '+/'));
    }

    /**
     * Generate a signed JWT
     */
    public static function encode(array $payload, ?string $secret = null, int $expirySeconds = 3600): string
    {
        $secret = $secret ?: (getenv('JWT_SECRET') ?: 'snaptask_default_secret_32_characters_minimum');

        $header = [
            'typ' => 'JWT',
            'alg' => 'HS256',
        ];

        $now = time();
        $payload['iat'] = $payload['iat'] ?? $now;
        $payload['exp'] = $payload['exp'] ?? ($now + $expirySeconds);

        $headerEncoded = self::base64UrlEncode((string)json_encode($header));
        $payloadEncoded = self::base64UrlEncode((string)json_encode($payload));

        $signature = hash_hmac('sha256', "{$headerEncoded}.{$payloadEncoded}", $secret, true);
        $signatureEncoded = self::base64UrlEncode($signature);

        return "{$headerEncoded}.{$payloadEncoded}.{$signatureEncoded}";
    }

    /**
     * Decode and verify a signed JWT
     * Returns null if invalid or expired
     */
    public static function decode(string $token, ?string $secret = null): ?array
    {
        $secret = $secret ?: (getenv('JWT_SECRET') ?: 'snaptask_default_secret_32_characters_minimum');
        $parts = explode('.', $token);

        if (count($parts) !== 3) {
            return null;
        }

        [$headerEncoded, $payloadEncoded, $signatureEncoded] = $parts;

        $expectedSig = self::base64UrlEncode(
            hash_hmac('sha256', "{$headerEncoded}.{$payloadEncoded}", $secret, true)
        );

        if (!hash_equals($expectedSig, $signatureEncoded)) {
            return null;
        }

        $payload = json_decode(self::base64UrlDecode($payloadEncoded), true);
        if (!is_array($payload)) {
            return null;
        }

        if (isset($payload['exp']) && $payload['exp'] < time()) {
            return null; // Expired
        }

        return $payload;
    }
}


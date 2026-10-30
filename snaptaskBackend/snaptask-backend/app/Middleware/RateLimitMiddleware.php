<?php
declare(strict_types=1);

namespace App\Middleware;

use App\Core\Request;
use App\Core\Response;

class RateLimitMiddleware
{
    public function handle(Request $request): void
    {
        $maxRequests = (int)(getenv('RATE_LIMIT_MAX_REQUESTS') ?: 120);
        $decaySeconds = (int)(getenv('RATE_LIMIT_DECAY_SECONDS') ?: 60);

        $clientIdentifier = md5($request->getClientIp() . ':' . ($request->getUser()?->id ?? 'guest'));
        $cacheDir = dirname(__DIR__, 2) . '/storage/framework/ratelimit';
        if (!is_dir($cacheDir)) {
            @mkdir($cacheDir, 0755, true);
        }

        $file = "{$cacheDir}/{$clientIdentifier}.json";
        $now = time();

        $data = ['requests' => [], 'reset_at' => $now + $decaySeconds];
        if (file_exists($file)) {
            $content = file_get_contents($file);
            if ($content) {
                $decoded = json_decode($content, true);
                if (is_array($decoded)) {
                    $data = $decoded;
                }
            }
        }

        // Clean out requests older than decaySeconds
        $data['requests'] = array_filter($data['requests'], fn($ts) => $ts > ($now - $decaySeconds));

        if (count($data['requests']) >= $maxRequests) {
            $retryAfter = $data['reset_at'] - $now;
            header("Retry-After: {$retryAfter}");
            Response::rateLimited("Rate limit exceeded. Try again in {$retryAfter} seconds.");
            return;
        }

        $data['requests'][] = $now;
        $data['reset_at'] = $now + $decaySeconds;
        @file_put_contents($file, json_encode($data), LOCK_EX);

        header("X-RateLimit-Limit: {$maxRequests}");
        header("X-RateLimit-Remaining: " . ($maxRequests - count($data['requests'])));
    }
}


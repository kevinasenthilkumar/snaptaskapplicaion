<?php
declare(strict_types=1);

namespace App\Services;

use App\Config\AiService;
use App\Core\Logger;

/**
 * Internal HTTP Client communicating with the Python FastAPI AI Microservice
 */
class AiServiceClient
{
    private string $baseUrl;
    private string $apiKey;
    private int $timeout;

    public function __construct()
    {
        $this->baseUrl = AiService::url();
        $this->apiKey = AiService::key();
        $this->timeout = AiService::timeout();
    }

    private function post(string $endpoint, array $payload, array $files = []): ?array
    {
        $url = "{$this->baseUrl}/" . ltrim($endpoint, '/');
        $ch = curl_init($url);

        $headers = [
            'X-API-Key: ' . $this->apiKey,
            'Accept: application/json',
        ];

        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_TIMEOUT, $this->timeout);

        if (!empty($files)) {
            // Multipart upload
            $postData = $payload;
            foreach ($files as $name => $path) {
                if (file_exists($path)) {
                    $mime = mime_content_type($path) ?: 'image/png';
                    $postData[$name] = new \CURLFile($path, $mime, basename($path));
                }
            }
            curl_setopt($ch, CURLOPT_POSTFIELDS, $postData);
        } else {
            $headers[] = 'Content-Type: application/json';
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));
        }

        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

        $start = microtime(true);
        $response = curl_exec($ch);
        $duration = (int)((microtime(true) - $start) * 1000);
        $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $err = curl_error($ch);
        curl_close($ch);

        if ($err || $status >= 400 || !$response) {
            Logger::warning("AI Service request failed [{$status}] {$endpoint}: {$err}", [
                'duration_ms' => $duration,
            ]);
            return null;
        }

        $decoded = json_decode((string)$response, true);
        return is_array($decoded) ? $decoded : null;
    }

    public function health(): array
    {
        $url = "{$this->baseUrl}/health";
        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_TIMEOUT, 5);
        $res = curl_exec($ch);
        $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        if ($status === 200 && $res) {
            $decoded = json_decode((string)$res, true);
            return $decoded ?: ['status' => 'ok', 'online' => true];
        }

        return ['status' => 'offline', 'online' => false];
    }

    public function analyzeScreenshot(string $absoluteImagePath): ?array
    {
        return $this->post('api/v1/ai/analyze', [], ['file' => $absoluteImagePath]);
    }

    public function classify(string $ocrText): ?array
    {
        return $this->post('api/v1/ai/classify', ['ocr_text' => $ocrText]);
    }

    public function extractDates(string $ocrText): ?array
    {
        return $this->post('api/v1/ai/extract-dates', ['ocr_text' => $ocrText]);
    }

    public function extractTask(string $ocrText, string $category = 'unclassified'): ?array
    {
        return $this->post('api/v1/ai/extract-task', ['ocr_text' => $ocrText, 'category' => $category]);
    }

    public function recommendCleanup(int $screenshotId, string $category, ?string $eventDate, ?string $capturedAt, int $fileSize): ?array
    {
        return $this->post('api/v1/ai/recommend-cleanup', [
            'screenshot_id' => $screenshotId,
            'category' => $category,
            'event_date' => $eventDate,
            'captured_at' => $capturedAt,
            'file_size' => $fileSize,
        ]);
    }
}


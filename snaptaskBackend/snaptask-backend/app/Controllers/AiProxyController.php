<?php
declare(strict_types=1);

namespace App\Controllers;

use App\Core\Request;
use App\Core\Response;
use App\Helpers\FileUpload;
use App\Services\AiServiceClient;

class AiProxyController
{
    private AiServiceClient $aiClient;

    public function __construct()
    {
        $this->aiClient = new AiServiceClient();
    }

    public function health(Request $request): void
    {
        $status = $this->aiClient->health();
        Response::json($status, 'AI service status');
    }

    public function analyzeScreenshot(Request $request): void
    {
        $file = $request->file('file');
        if (!$file) {
            Response::error('Image file is required', 400, [], 'FILE_REQUIRED');
            return;
        }

        try {
            $saved = FileUpload::save($file, 'temp');
            $result = $this->aiClient->analyzeScreenshot($saved['absolute_path']);
            @unlink($saved['absolute_path']);

            if (!$result) {
                Response::serverError('AI service failed to process image');
                return;
            }

            Response::json($result['data'] ?? $result, 'AI Analysis Complete');
        } catch (\Throwable $e) {
            Response::serverError($e->getMessage());
        }
    }

    public function classify(Request $request): void
    {
        $ocrText = (string)$request->body('ocr_text', '');
        $result = $this->aiClient->classify($ocrText);
        if (!$result) {
            Response::serverError('Classification service unavailable');
            return;
        }
        Response::json($result['data'] ?? $result, 'Classification complete');
    }

    public function extractDates(Request $request): void
    {
        $ocrText = (string)$request->body('ocr_text', '');
        $result = $this->aiClient->extractDates($ocrText);
        if (!$result) {
            Response::serverError('Date extraction service unavailable');
            return;
        }
        Response::json($result['data'] ?? $result, 'Dates extracted');
    }

    public function extractTask(Request $request): void
    {
        $ocrText = (string)$request->body('ocr_text', '');
        $category = (string)$request->body('category', 'unclassified');
        $result = $this->aiClient->extractTask($ocrText, $category);
        if (!$result) {
            Response::serverError('Task extraction service unavailable');
            return;
        }
        Response::json($result['data'] ?? $result, 'Task extracted');
    }

    public function recommendCleanup(Request $request): void
    {
        $screenshotId = (int)$request->body('screenshot_id', 0);
        $category = (string)$request->body('category', 'dated');
        $eventDate = $request->body('event_date');
        $capturedAt = $request->body('captured_at');
        $fileSize = (int)$request->body('file_size', 0);

        $result = $this->aiClient->recommendCleanup($screenshotId, $category, $eventDate, $capturedAt, $fileSize);
        if (!$result) {
            Response::serverError('Cleanup recommendation service unavailable');
            return;
        }
        Response::json($result['data'] ?? $result, 'Cleanup recommendation calculated');
    }
}


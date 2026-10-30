<?php
declare(strict_types=1);

namespace App\Controllers;

use App\Core\Request;
use App\Core\Response;
use App\Services\CleanupService;
use App\Validators\CleanupValidator;

class CleanupController
{
    private CleanupService $service;

    public function __construct()
    {
        $this->service = new CleanupService();
    }

    public function recommendations(Request $request): void
    {
        $result = $this->service->getRecommendations($request->getUserId());
        Response::json($result, 'Cleanup recommendations generated');
    }

    public function review(Request $request): void
    {
        $validator = CleanupValidator::validateReview($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $screenshotIds = $request->body('screenshot_ids', []);
        $result = $this->service->review($request->getUserId(), $screenshotIds);
        Response::json($result, 'Cleanup review summary');
    }

    public function confirm(Request $request): void
    {
        $validator = CleanupValidator::validateConfirm($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $screenshotIds = $request->body('screenshot_ids', []);
        $mode = $request->body('mode', 'manual');

        try {
            $result = $this->service->confirm($request->getUserId(), $screenshotIds, $mode);
            Response::json($result, 'Cleanup executed successfully');
        } catch (\Throwable $e) {
            Response::serverError($e->getMessage());
        }
    }

    public function cancel(Request $request): void
    {
        Response::json([], 'Cleanup job cancelled');
    }

    public function history(Request $request): void
    {
        $limit = min(50, max(1, (int)$request->query('limit', 20)));
        $history = $this->service->getHistory($request->getUserId(), $limit);
        Response::json([
            'jobs' => array_map(fn($j) => $j->toArray(), $history),
        ], 'Cleanup history retrieved');
    }

    public function stats(Request $request): void
    {
        $stats = $this->service->getStats($request->getUserId());
        Response::json(['stats' => $stats], 'Cleanup statistics');
    }
}


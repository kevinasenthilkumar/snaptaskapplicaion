<?php
declare(strict_types=1);

namespace App\Controllers;

use App\Core\Request;
use App\Core\Response;
use App\Repositories\ScreenshotRepository;
use App\Services\ScreenshotService;
use App\Validators\ScreenshotValidator;

class ScreenshotController
{
    private ScreenshotService $service;
    private ScreenshotRepository $repo;

    public function __construct()
    {
        $this->service = new ScreenshotService();
        $this->repo = new ScreenshotRepository();
    }

    public function upload(Request $request): void
    {
        $file = $request->file('file');
        if (!$file) {
            Response::error('Screenshot image file is required in "file" form field.', 400, [], 'FILE_REQUIRED');
            return;
        }

        $localMediaId = $request->body('local_media_id');
        $capturedAt = $request->body('captured_at');

        try {
            $screenshot = $this->service->upload($request->getUserId(), $file, $localMediaId, $capturedAt);
            Response::created(['screenshot' => $screenshot->toArray()], 'Screenshot uploaded successfully');
        } catch (\InvalidArgumentException $e) {
            Response::error($e->getMessage(), 422, [], 'INVALID_FILE');
        } catch (\Throwable $e) {
            Response::serverError($e->getMessage());
        }
    }

    public function index(Request $request): void
    {
        $category = $request->query('category');
        $status = $request->query('status');
        $isKept = $request->query('is_kept');
        $page = max(1, (int)$request->query('page', 1));
        $limit = min(100, max(1, (int)$request->query('limit', 20)));

        $filters = array_filter([
            'category' => $category,
            'status' => $status,
            'is_kept' => $isKept !== null ? (bool)$isKept : null,
        ], fn($v) => $v !== null);

        $result = $this->repo->getList($request->getUserId(), $filters, $page, $limit);
        $result['items'] = array_map(fn($item) => $item->toArray(), $result['items']);

        Response::json($result, 'Screenshots retrieved successfully', 200, [
            'total' => $result['total'],
            'page' => $result['page'],
            'total_pages' => $result['total_pages'],
        ]);
    }

    public function show(Request $request): void
    {
        $id = (int)$request->param('id');
        $screenshot = $this->repo->findById($id, $request->getUserId());
        if (!$screenshot) {
            Response::notFound("Screenshot #{$id} not found.");
            return;
        }
        Response::json(['screenshot' => $screenshot->toArray()]);
    }

    public function update(Request $request): void
    {
        $id = (int)$request->param('id');
        $screenshot = $this->repo->findById($id, $request->getUserId());
        if (!$screenshot) {
            Response::notFound("Screenshot #{$id} not found.");
            return;
        }

        $validator = ScreenshotValidator::validateUpdate($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $this->repo->update($id, $request->getUserId(), $validator->validated());
        $updated = $this->repo->findById($id, $request->getUserId());

        Response::json(['screenshot' => $updated->toArray()], 'Screenshot updated successfully');
    }

    public function delete(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $this->service->moveToTrash($id, $request->getUserId());
            Response::json([], 'Screenshot moved to trash');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function analyze(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $screenshot = $this->service->analyze($id, $request->getUserId());
            Response::json(['screenshot' => $screenshot->toArray()], 'Screenshot analyzed successfully');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        } catch (\Throwable $e) {
            Response::serverError($e->getMessage());
        }
    }

    public function bulkAnalyze(Request $request): void
    {
        $validator = ScreenshotValidator::validateBulkAnalyze($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $ids = $request->body('screenshot_ids', []);
        $results = $this->service->bulkAnalyze($ids, $request->getUserId());
        Response::json([
            'analyzed_count' => count($results),
            'screenshots' => $results,
        ], 'Bulk analysis completed');
    }

    public function search(Request $request): void
    {
        $query = (string)$request->query('query', '');
        if (trim($query) === '') {
            Response::json(['screenshots' => []], 'Empty query');
            return;
        }

        $results = $this->repo->search($request->getUserId(), $query);
        Response::json([
            'total' => count($results),
            'screenshots' => array_map(fn($s) => $s->toArray(), $results),
        ], 'Search results');
    }

    public function stats(Request $request): void
    {
        $stats = $this->repo->getStats($request->getUserId());
        Response::json(['stats' => $stats], 'Screenshot statistics');
    }
}


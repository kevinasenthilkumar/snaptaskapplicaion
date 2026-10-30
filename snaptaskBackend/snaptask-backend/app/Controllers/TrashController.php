<?php
declare(strict_types=1);

namespace App\Controllers;

use App\Core\Request;
use App\Core\Response;
use App\Services\TrashService;

class TrashController
{
    private TrashService $service;

    public function __construct()
    {
        $this->service = new TrashService();
    }

    public function index(Request $request): void
    {
        $page = max(1, (int)$request->query('page', 1));
        $limit = min(100, max(1, (int)$request->query('limit', 30)));

        $result = $this->service->getList($request->getUserId(), $page, $limit);
        $result['items'] = array_map(fn($item) => $item->toArray(), $result['items']);

        Response::json($result, 'Trash items retrieved', 200, [
            'total' => $result['total'],
            'total_mb' => $result['total_mb'],
            'page' => $result['page'],
            'total_pages' => $result['total_pages'],
        ]);
    }

    public function restore(Request $request): void
    {
        $screenshotId = (int)$request->param('screenshotId');
        try {
            $this->service->restore($screenshotId, $request->getUserId());
            Response::json([], 'Screenshot restored successfully');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function restoreAll(Request $request): void
    {
        $count = $this->service->restoreAll($request->getUserId());
        Response::json(['restored_count' => $count], "Restored {$count} screenshots");
    }

    public function permanentDelete(Request $request): void
    {
        $screenshotId = (int)$request->param('screenshotId');
        try {
            $this->service->permanentDelete($screenshotId, $request->getUserId());
            Response::json([], 'Screenshot permanently purged');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function emptyTrash(Request $request): void
    {
        $count = $this->service->emptyTrash($request->getUserId());
        Response::json(['deleted_count' => $count], "Permanently deleted {$count} screenshots");
    }
}


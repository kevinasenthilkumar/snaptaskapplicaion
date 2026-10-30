<?php
declare(strict_types=1);

namespace App\Controllers;

use App\Core\Request;
use App\Core\Response;
use App\Services\DatedItemService;
use App\Validators\DatedItemValidator;

class DatedItemController
{
    private DatedItemService $service;

    public function __construct()
    {
        $this->service = new DatedItemService();
    }

    public function index(Request $request): void
    {
        $category = $request->query('category');
        $status = $request->query('status');
        $timeframe = $request->query('timeframe');
        $sort = $request->query('sort', 'date_asc');
        $page = max(1, (int)$request->query('page', 1));
        $limit = min(100, max(1, (int)$request->query('limit', 20)));

        $filters = array_filter([
            'category' => $category,
            'status' => $status,
            'timeframe' => $timeframe,
            'sort' => $sort,
        ], fn($v) => $v !== null);

        $result = $this->service->getList($request->getUserId(), $filters, $page, $limit);
        $result['items'] = array_map(fn($item) => $item->toArray(), $result['items']);

        Response::json($result, 'Dated items retrieved');
    }

    public function show(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $item = $this->service->getById($id, $request->getUserId());
            Response::json(['dated_item' => $item->toArray()]);
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function store(Request $request): void
    {
        $validator = DatedItemValidator::validateCreate($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        try {
            $item = $this->service->create($request->getUserId(), $validator->validated());
            Response::created(['dated_item' => $item->toArray()], 'Dated item created successfully');
        } catch (\Throwable $e) {
            Response::serverError($e->getMessage());
        }
    }

    public function update(Request $request): void
    {
        $id = (int)$request->param('id');
        $validator = DatedItemValidator::validateUpdate($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        try {
            $item = $this->service->update($id, $request->getUserId(), $validator->validated());
            Response::json(['dated_item' => $item->toArray()], 'Dated item updated successfully');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function delete(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $this->service->delete($id, $request->getUserId());
            Response::json([], 'Dated item deleted');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function overdue(Request $request): void
    {
        $result = $this->service->getList($request->getUserId(), ['timeframe' => 'overdue']);
        $result['items'] = array_map(fn($item) => $item->toArray(), $result['items']);
        Response::json($result, 'Overdue dated items');
    }

    public function thisWeek(Request $request): void
    {
        $result = $this->service->getList($request->getUserId(), ['timeframe' => 'this_week']);
        $result['items'] = array_map(fn($item) => $item->toArray(), $result['items']);
        Response::json($result, 'This week dated items');
    }

    public function upcoming(Request $request): void
    {
        $result = $this->service->getList($request->getUserId(), ['timeframe' => 'upcoming']);
        $result['items'] = array_map(fn($item) => $item->toArray(), $result['items']);
        Response::json($result, 'Upcoming dated items');
    }

    public function complete(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $item = $this->service->markCompleted($id, $request->getUserId());
            Response::json(['dated_item' => $item->toArray()], 'Item marked as completed');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function snooze(Request $request): void
    {
        $id = (int)$request->param('id');
        $validator = DatedItemValidator::validateSnooze($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        try {
            $item = $this->service->snooze($id, $request->getUserId(), (string)$request->body('snooze_until'));
            Response::json(['dated_item' => $item->toArray()], 'Item snoozed');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }
}


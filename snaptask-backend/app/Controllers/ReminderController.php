<?php
declare(strict_types=1);

namespace App\Controllers;

use App\Core\Request;
use App\Core\Response;
use App\Services\ReminderService;
use App\Validators\ReminderValidator;

class ReminderController
{
    private ReminderService $service;

    public function __construct()
    {
        $this->service = new ReminderService();
    }

    public function index(Request $request): void
    {
        $filter = (string)$request->query('filter', 'all');
        $limit = min(100, max(1, (int)$request->query('limit', 50)));

        $reminders = $this->service->getList($request->getUserId(), $filter, $limit);
        Response::json([
            'total' => count($reminders),
            'filter' => $filter,
            'reminders' => array_map(fn($r) => $r->toArray(), $reminders),
        ], 'Reminders retrieved');
    }

    public function show(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $r = $this->service->getById($id, $request->getUserId());
            Response::json(['reminder' => $r->toArray()]);
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function store(Request $request): void
    {
        $validator = ReminderValidator::validateCreate($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        try {
            $r = $this->service->create($request->getUserId(), $validator->validated());
            Response::created(['reminder' => $r->toArray()], 'Reminder created');
        } catch (\Throwable $e) {
            Response::serverError($e->getMessage());
        }
    }

    public function update(Request $request): void
    {
        $id = (int)$request->param('id');
        $validator = ReminderValidator::validateUpdate($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        try {
            $r = $this->service->update($id, $request->getUserId(), $validator->validated());
            Response::json(['reminder' => $r->toArray()], 'Reminder updated');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function snooze(Request $request): void
    {
        $id = (int)$request->param('id');
        $minutes = (int)$request->body('minutes', 60);
        $snoozedUntil = $request->body('snoozed_until');

        try {
            $r = $this->service->snooze($id, $request->getUserId(), $minutes, $snoozedUntil);
            Response::json(['reminder' => $r->toArray()], 'Reminder snoozed');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function complete(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $r = $this->service->complete($id, $request->getUserId());
            Response::json(['reminder' => $r->toArray()], 'Reminder completed');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function delete(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $this->service->delete($id, $request->getUserId());
            Response::json([], 'Reminder deleted');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }
}


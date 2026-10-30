<?php
declare(strict_types=1);

namespace App\Controllers;

use App\Core\Request;
use App\Core\Response;
use App\Services\SettingsService;
use App\Validators\SettingsValidator;

class SettingsController
{
    private SettingsService $service;

    public function __construct()
    {
        $this->service = new SettingsService();
    }

    public function index(Request $request): void
    {
        $settings = $this->service->getSettings($request->getUserId());
        Response::json(['settings' => $settings->toArray()]);
    }

    public function update(Request $request): void
    {
        $validator = SettingsValidator::validateGeneral($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $settings = $this->service->updateSettings($request->getUserId(), $validator->validated());
        Response::json(['settings' => $settings->toArray()], 'Settings updated successfully');
    }

    public function getAutomation(Request $request): void
    {
        $automation = $this->service->getAutomation($request->getUserId());
        Response::json(['automation' => $automation]);
    }

    public function updateAutomation(Request $request): void
    {
        $validator = SettingsValidator::validateAutomation($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $automation = $this->service->updateAutomation($request->getUserId(), $validator->validated());
        Response::json(['automation' => $automation], 'Automation settings saved');
    }

    public function getRules(Request $request): void
    {
        $rules = $this->service->getCleanupRules($request->getUserId());
        Response::json([
            'rules' => array_map(fn($r) => $r->toArray(), $rules),
        ], 'Cleanup rules retrieved');
    }

    public function storeRule(Request $request): void
    {
        $validator = SettingsValidator::validateCleanupRule($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        try {
            $rule = $this->service->createCleanupRule($request->getUserId(), $validator->validated());
            Response::created(['rule' => $rule->toArray()], 'Cleanup rule created');
        } catch (\Throwable $e) {
            Response::serverError($e->getMessage());
        }
    }

    public function updateRule(Request $request): void
    {
        $id = (int)$request->param('id');
        $validator = SettingsValidator::validateCleanupRule($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        try {
            $rule = $this->service->updateCleanupRule($id, $request->getUserId(), $validator->validated());
            Response::json(['rule' => $rule->toArray()], 'Cleanup rule updated');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function deleteRule(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $this->service->deleteCleanupRule($id, $request->getUserId());
            Response::json([], 'Cleanup rule deleted');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function getPrivacy(Request $request): void
    {
        $privacy = $this->service->getPrivacy($request->getUserId());
        Response::json(['privacy' => $privacy]);
    }

    public function updatePrivacy(Request $request): void
    {
        $validator = SettingsValidator::validatePrivacy($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $privacy = $this->service->updatePrivacy($request->getUserId(), $validator->validated());
        Response::json(['privacy' => $privacy], 'Privacy preferences updated');
    }
}


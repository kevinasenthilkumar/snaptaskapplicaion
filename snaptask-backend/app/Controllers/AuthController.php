<?php
declare(strict_types=1);

namespace App\Controllers;

use App\Core\Request;
use App\Core\Response;
use App\Services\AuthService;
use App\Validators\AuthValidator;

class AuthController
{
    private AuthService $authService;

    public function __construct()
    {
        $this->authService = new AuthService();
    }

    public function register(Request $request): void
    {
        $validator = AuthValidator::validateRegister($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $data = $validator->validated();
        try {
            $result = $this->authService->register($data['name'], $data['email'], $data['password']);
            Response::created($result, 'User registered successfully');
        } catch (\InvalidArgumentException $e) {
            Response::error($e->getMessage(), 409, [], 'USER_ALREADY_EXISTS');
        } catch (\Throwable $e) {
            Response::serverError($e->getMessage());
        }
    }

    public function login(Request $request): void
    {
        $validator = AuthValidator::validateLogin($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $data = $validator->validated();
        $deviceName = $request->body('device_name', 'Android Device');
        $ip = $request->getClientIp();
        $userAgent = $request->getHeader('user-agent');

        try {
            $result = $this->authService->login($data['email'], $data['password'], $deviceName, $ip, $userAgent);
            Response::json($result, 'Login successful');
        } catch (\InvalidArgumentException $e) {
            Response::error($e->getMessage(), 401, [], 'INVALID_CREDENTIALS');
        } catch (\Throwable $e) {
            Response::serverError($e->getMessage());
        }
    }

    public function refresh(Request $request): void
    {
        $validator = AuthValidator::validateRefresh($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $refreshToken = (string)$request->body('refresh_token');
        try {
            $result = $this->authService->refresh($refreshToken, $request->getClientIp(), $request->getHeader('user-agent'));
            Response::json($result, 'Token refreshed successfully');
        } catch (\InvalidArgumentException $e) {
            Response::unauthorized($e->getMessage());
        } catch (\Throwable $e) {
            Response::serverError($e->getMessage());
        }
    }

    public function logout(Request $request): void
    {
        $this->authService->logout($request->getUserId());
        Response::json([], 'Logged out successfully');
    }

    public function me(Request $request): void
    {
        $user = $request->getUser();
        Response::json([
            'user' => [
                'id' => (int)$user->id,
                'email' => $user->email,
                'name' => $user->name,
                'avatar_url' => $user->avatar_url,
                'status' => $user->status,
            ],
        ], 'Authenticated user profile');
    }

    public function updateProfile(Request $request): void
    {
        $validator = AuthValidator::validateProfile($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $user = $this->authService->updateProfile($request->getUserId(), $validator->validated());
        Response::json(['user' => $user->toArray()], 'Profile updated successfully');
    }

    public function changePassword(Request $request): void
    {
        $validator = AuthValidator::validateChangePassword($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $data = $validator->validated();
        try {
            $this->authService->changePassword($request->getUserId(), $data['current_password'], $data['new_password']);
            Response::json([], 'Password changed successfully');
        } catch (\InvalidArgumentException $e) {
            Response::error($e->getMessage(), 400, [], 'PASSWORD_MISMATCH');
        } catch (\Throwable $e) {
            Response::serverError($e->getMessage());
        }
    }
}


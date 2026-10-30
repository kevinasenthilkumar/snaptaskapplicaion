<?php
declare(strict_types=1);

namespace App\Controllers;

use App\Core\Request;
use App\Core\Response;
use App\Services\NotificationService;

class NotificationController
{
    private NotificationService $service;

    public function __construct()
    {
        $this->service = new NotificationService();
    }

    public function index(Request $request): void
    {
        $unreadOnly = filter_var($request->query('unread_only', false), FILTER_VALIDATE_BOOLEAN);
        $limit = min(100, max(1, (int)$request->query('limit', 50)));

        $notifications = $this->service->getList($request->getUserId(), $unreadOnly, $limit);
        $unreadCount = $this->service->getUnreadCount($request->getUserId());

        Response::json([
            'unread_count' => $unreadCount,
            'notifications' => array_map(fn($n) => $n->toArray(), $notifications),
        ], 'Notifications retrieved');
    }

    public function unreadCount(Request $request): void
    {
        $count = $this->service->getUnreadCount($request->getUserId());
        Response::json(['unread_count' => $count], 'Unread notification count');
    }

    public function markRead(Request $request): void
    {
        $id = (int)$request->param('id');
        $this->service->markAsRead($id, $request->getUserId());
        Response::json([], 'Notification marked as read');
    }

    public function markAllRead(Request $request): void
    {
        $this->service->markAllAsRead($request->getUserId());
        Response::json([], 'All notifications marked as read');
    }

    public function delete(Request $request): void
    {
        $id = (int)$request->param('id');
        $this->service->delete($id, $request->getUserId());
        Response::json([], 'Notification removed');
    }
}


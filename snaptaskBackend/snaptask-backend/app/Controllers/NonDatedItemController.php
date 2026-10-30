<?php
declare(strict_types=1);

namespace App\Controllers;

use App\Core\Request;
use App\Core\Response;
use App\Services\NonDatedItemService;
use App\Validators\NonDatedItemValidator;

class NonDatedItemController
{
    private NonDatedItemService $service;

    public function __construct()
    {
        $this->service = new NonDatedItemService();
    }

    public function index(Request $request): void
    {
        $category = $request->query('category');
        $isFavorite = $request->query('is_favorite');
        $sort = $request->query('sort', 'newest');
        $page = max(1, (int)$request->query('page', 1));
        $limit = min(100, max(1, (int)$request->query('limit', 20)));

        $filters = array_filter([
            'category' => $category,
            'is_favorite' => $isFavorite !== null ? (bool)$isFavorite : null,
            'sort' => $sort,
        ], fn($v) => $v !== null);

        $result = $this->service->getList($request->getUserId(), $filters, $page, $limit);
        $result['items'] = array_map(fn($item) => $item->toArray(), $result['items']);

        Response::json($result, 'Non-dated items retrieved');
    }

    public function show(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $item = $this->service->getById($id, $request->getUserId());
            Response::json(['non_dated_item' => $item->toArray()]);
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function store(Request $request): void
    {
        $validator = NonDatedItemValidator::validateCreate($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        try {
            $item = $this->service->create($request->getUserId(), $validator->validated());
            Response::created(['non_dated_item' => $item->toArray()], 'Non-dated item created successfully');
        } catch (\Throwable $e) {
            Response::serverError($e->getMessage());
        }
    }

    public function update(Request $request): void
    {
        $id = (int)$request->param('id');
        $validator = NonDatedItemValidator::validateUpdate($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        try {
            $item = $this->service->update($id, $request->getUserId(), $validator->validated());
            Response::json(['non_dated_item' => $item->toArray()], 'Non-dated item updated successfully');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function delete(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $this->service->delete($id, $request->getUserId());
            Response::json([], 'Non-dated item deleted');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function search(Request $request): void
    {
        $query = (string)$request->query('query', '');
        if (trim($query) === '') {
            Response::json(['items' => []], 'Empty query');
            return;
        }

        $results = $this->service->search($request->getUserId(), $query);
        Response::json([
            'total' => count($results),
            'items' => array_map(fn($item) => $item->toArray(), $results),
        ], 'Search results');
    }

    public function addFavorite(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $item = $this->service->toggleFavorite($id, $request->getUserId(), true);
            Response::json(['non_dated_item' => $item->toArray()], 'Marked as favorite');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function removeFavorite(Request $request): void
    {
        $id = (int)$request->param('id');
        try {
            $item = $this->service->toggleFavorite($id, $request->getUserId(), false);
            Response::json(['non_dated_item' => $item->toArray()], 'Removed from favorites');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function addTag(Request $request): void
    {
        $id = (int)$request->param('id');
        $validator = NonDatedItemValidator::validateTag($request->body());
        if ($validator->fails()) {
            Response::validationError($validator->errors());
            return;
        }

        $data = $validator->validated();
        try {
            $tag = $this->service->addTag($id, $request->getUserId(), $data['name'], $data['color'] ?? '#721D38');
            Response::created(['tag' => $tag], 'Tag added to item');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }

    public function removeTag(Request $request): void
    {
        $id = (int)$request->param('id');
        $tagId = (int)$request->param('tagId');
        try {
            $this->service->removeTag($id, $request->getUserId(), $tagId);
            Response::json([], 'Tag removed from item');
        } catch (\InvalidArgumentException $e) {
            Response::notFound($e->getMessage());
        }
    }
}


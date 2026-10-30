<?php
declare(strict_types=1);

namespace App\Models;

class NonDatedItem
{
    public int $id;
    public int $user_id;
    public int $screenshot_id;
    public string $title;
    public ?string $description = null;
    public string $category = 'idea';
    public ?string $content_text = null;
    public int $is_favorite = 0;
    public string $color_label = '#EDE7DE';
    public mixed $ai_metadata = null;
    public ?string $created_at = null;
    public ?string $updated_at = null;

    // Optional joined fields
    public ?string $file_path = null;
    public ?int $is_kept = null;
    public array $tags = [];

    public function toArray(): array
    {
        $baseUrl = rtrim(getenv('APP_URL') ?: 'http://localhost:8000', '/');
        $imageUrl = $this->file_path ? "{$baseUrl}/storage/" . ltrim($this->file_path, '/') : null;

        return [
            'id' => (int)$this->id,
            'user_id' => (int)$this->user_id,
            'screenshot_id' => (int)$this->screenshot_id,
            'title' => $this->title,
            'description' => $this->description,
            'category' => $this->category,
            'content_text' => $this->content_text,
            'is_favorite' => (bool)$this->is_favorite,
            'color_label' => $this->color_label,
            'is_kept' => (bool)($this->is_kept ?? 0),
            'image_url' => $imageUrl,
            'tags' => $this->tags,
            'ai_metadata' => is_string($this->ai_metadata) ? json_decode($this->ai_metadata, true) : $this->ai_metadata,
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}


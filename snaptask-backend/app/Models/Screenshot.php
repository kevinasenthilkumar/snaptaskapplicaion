<?php
declare(strict_types=1);

namespace App\Models;

class Screenshot
{
    public int $id;
    public int $user_id;
    public ?int $media_file_id = null;
    public ?string $local_media_id = null;
    public string $file_path;
    public string $file_name;
    public string $file_hash;
    public ?string $perceptual_hash = null;
    public string $mime_type = 'image/png';
    public int $file_size = 0;
    public ?int $width = null;
    public ?int $height = null;
    public string $category = 'unclassified';
    public float $classification_confidence = 0.0;
    public ?string $ocr_text = null;
    public ?string $extracted_title = null;
    public ?string $extracted_description = null;
    public ?string $extracted_date = null;
    public ?string $extracted_time = null;
    public ?string $extracted_location = null;
    public ?float $extracted_amount = null;
    public ?string $extracted_currency = null;
    public ?string $source_app = null;
    public string $status = 'pending';
    public int $is_kept = 0;
    public mixed $ai_metadata = null;
    public ?string $captured_at = null;
    public ?string $uploaded_at = null;
    public ?string $deleted_at = null;
    public ?string $created_at = null;
    public ?string $updated_at = null;

    public function toArray(): array
    {
        $baseUrl = rtrim(getenv('APP_URL') ?: 'http://localhost:8000', '/');
        $publicUrl = "{$baseUrl}/storage/" . ltrim($this->file_path, '/');

        return [
            'id' => (int)$this->id,
            'user_id' => (int)$this->user_id,
            'local_media_id' => $this->local_media_id,
            'file_name' => $this->file_name,
            'file_path' => $this->file_path,
            'file_url' => $publicUrl,
            'file_size' => (int)$this->file_size,
            'file_size_formatted' => round($this->file_size / 1024 / 1024, 2) . ' MB',
            'mime_type' => $this->mime_type,
            'width' => $this->width,
            'height' => $this->height,
            'category' => $this->category,
            'classification_confidence' => (float)$this->classification_confidence,
            'ocr_text' => $this->ocr_text,
            'extracted_title' => $this->extracted_title,
            'extracted_description' => $this->extracted_description,
            'extracted_date' => $this->extracted_date,
            'extracted_time' => $this->extracted_time,
            'extracted_location' => $this->extracted_location,
            'extracted_amount' => $this->extracted_amount !== null ? (float)$this->extracted_amount : null,
            'extracted_currency' => $this->extracted_currency,
            'source_app' => $this->source_app,
            'status' => $this->status,
            'is_kept' => (bool)$this->is_kept,
            'ai_metadata' => is_string($this->ai_metadata) ? json_decode($this->ai_metadata, true) : $this->ai_metadata,
            'captured_at' => $this->captured_at,
            'uploaded_at' => $this->uploaded_at,
            'deleted_at' => $this->deleted_at,
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}


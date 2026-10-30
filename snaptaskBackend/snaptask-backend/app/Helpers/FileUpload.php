<?php
declare(strict_types=1);

namespace App\Helpers;

/**
 * Secure File Upload Handler
 */
class FileUpload
{
    private const ALLOWED_MIME_TYPES = [
        'image/png',
        'image/jpeg',
        'image/jpg',
        'image/webp',
    ];

    private const ALLOWED_EXTENSIONS = ['png', 'jpg', 'jpeg', 'webp'];

    /**
     * Validate and store an uploaded file
     */
    public static function save(array $file, string $subDirectory = 'uploads'): array
    {
        if (!isset($file['tmp_name']) || !is_uploaded_file($file['tmp_name'])) {
            throw new \InvalidArgumentException('No valid uploaded file found.');
        }

        if ($file['error'] !== UPLOAD_ERR_OK) {
            throw new \RuntimeException('Upload failed with error code: ' . $file['error']);
        }

        $maxSize = (int)(getenv('UPLOAD_MAX_SIZE_BYTES') ?: 10485760); // 10MB
        if ($file['size'] > $maxSize) {
            throw new \InvalidArgumentException('Uploaded file exceeds maximum permitted size of ' . round($maxSize / 1024 / 1024, 1) . 'MB');
        }

        // Verify MIME type using finfo
        $finfo = new \finfo(FILEINFO_MIME_TYPE);
        $mimeType = $finfo->file($file['tmp_name']);
        if (!in_array($mimeType, self::ALLOWED_MIME_TYPES, true)) {
            throw new \InvalidArgumentException("Invalid file type ({$mimeType}). Only PNG, JPEG, and WebP screenshots are allowed.");
        }

        $extension = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));
        if (!in_array($extension, self::ALLOWED_EXTENSIONS, true)) {
            $extension = match ($mimeType) {
                'image/png' => 'png',
                'image/webp' => 'webp',
                default => 'jpg',
            };
        }

        $storageDir = dirname(__DIR__, 2) . '/storage/' . trim($subDirectory, '/');
        if (!is_dir($storageDir)) {
            @mkdir($storageDir, 0755, true);
        }

        // Generate cryptographically unique safe file name
        $safeName = sprintf('snap_%s_%s.%s', date('Ymd_His'), bin2hex(random_bytes(8)), $extension);
        $targetPath = "{$storageDir}/{$safeName}";

        if (!move_uploaded_file($file['tmp_name'], $targetPath)) {
            throw new \RuntimeException('Failed to move uploaded file to target storage path.');
        }

        // Extract image dimensions if supported
        $dimensions = @getimagesize($targetPath);
        $width = $dimensions ? $dimensions[0] : null;
        $height = $dimensions ? $dimensions[1] : null;

        $fileHash = hash_file('sha256', $targetPath);

        return [
            'file_name' => $safeName,
            'original_name' => basename($file['name']),
            'file_path' => "{$subDirectory}/{$safeName}",
            'absolute_path' => $targetPath,
            'mime_type' => $mimeType,
            'file_size' => (int)$file['size'],
            'width' => $width,
            'height' => $height,
            'file_hash' => $fileHash,
        ];
    }
}


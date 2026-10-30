<?php
declare(strict_types=1);

namespace App\Helpers;

/**
 * Hash calculation and screenshot deduplication helper
 */
class HashHelper
{
    /**
     * Compute SHA-256 for string or file
     */
    public static function sha256(string $data): string
    {
        return hash('sha256', $data);
    }

    /**
     * Compute difference hash (dHash) simulation for perceptual screenshot comparison
     * If GD extension is available, calculates standard 64-bit dHash; otherwise falls back to MD5 chunking
     */
    public static function computePerceptualHash(string $filePath): string
    {
        if (function_exists('imagecreatefromstring') && function_exists('imagesx')) {
            $imgData = @file_get_contents($filePath);
            if ($imgData !== false) {
                $img = @imagecreatefromstring($imgData);
                if ($img !== false) {
                    $resized = imagecreatetruecolor(9, 8);
                    imagecopyresampled($resized, $img, 0, 0, 0, 0, 9, 8, imagesx($img), imagesy($img));
                    imagefilter($resized, IMG_FILTER_GRAYSCALE);

                    $hash = '';
                    for ($y = 0; $y < 8; $y++) {
                        for ($x = 0; $x < 8; $x++) {
                            $left = imagecolorat($resized, $x, $y) & 0xFF;
                            $right = imagecolorat($resized, $x + 1, $y) & 0xFF;
                            $hash .= ($left > $right) ? '1' : '0';
                        }
                    }

                    imagedestroy($resized);
                    imagedestroy($img);

                    // Convert 64-bit binary to 16-hex characters
                    $hex = '';
                    for ($i = 0; $i < 64; $i += 4) {
                        $hex .= dechex(bindec(substr($hash, $i, 4)));
                    }
                    return $hex;
                }
            }
        }

        // Deterministic fallback based on file hash
        return substr(hash_file('sha256', $filePath), 0, 16);
    }

    /**
     * Calculate Hamming distance between two hex perceptual hashes (0 to 64)
     */
    public static function hammingDistance(string $hash1, string $hash2): int
    {
        $bin1 = base_convert($hash1, 16, 2);
        $bin2 = base_convert($hash2, 16, 2);

        $bin1 = str_pad($bin1, 64, '0', STR_PAD_LEFT);
        $bin2 = str_pad($bin2, 64, '0', STR_PAD_LEFT);

        $dist = 0;
        for ($i = 0; $i < 64; $i++) {
            if ($bin1[$i] !== $bin2[$i]) {
                $dist++;
            }
        }
        return $dist;
    }
}


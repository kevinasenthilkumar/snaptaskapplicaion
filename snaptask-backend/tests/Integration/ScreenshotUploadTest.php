<?php
declare(strict_types=1);

namespace Tests\Integration;

use App\Helpers\HashHelper;
use PHPUnit\Framework\TestCase;

class ScreenshotUploadTest extends TestCase
{
    public function testHashCalculationConsistency(): void
    {
        $content = 'Sample screenshot raw binary data representation';
        $hash1 = HashHelper::sha256($content);
        $hash2 = HashHelper::sha256($content);

        $this->assertEquals($hash1, $hash2);
        $this->assertEquals(64, strlen($hash1));
    }

    public function testHammingDistanceIdenticalHashes(): void
    {
        $hash = 'a4b2c8d1e3f60718';
        $dist = HashHelper::hammingDistance($hash, $hash);
        $this->assertEquals(0, $dist);
    }
}


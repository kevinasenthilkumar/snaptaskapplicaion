<?php
declare(strict_types=1);

/**
 * SnapTask Database Installer & Migration CLI Script
 *
 * Usage:
 *   php database/install.php [--seed] [--fresh]
 */

$rootDir = dirname(__DIR__);

// Load .env if present
$envFile = $rootDir . '/.env';
if (file_exists($envFile)) {
    $lines = file($envFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    foreach ($lines as $line) {
        $line = trim($line);
        if ($line === '' || str_starts_with($line, '#')) {
            continue;
        }
        if (str_contains($line, '=')) {
            [$k, $v] = explode('=', $line, 2);
            $k = trim($k);
            $v = trim($v, " \t\n\r\0\x0B\"'");
            $_ENV[$k] = $v;
            putenv("{$k}={$v}");
        }
    }
}

$dbHost = getenv('DB_HOST') ?: '127.0.0.1';
$dbPort = getenv('DB_PORT') ?: '3306';
$dbName = getenv('DB_DATABASE') ?: 'snaptask_db';
$dbUser = getenv('DB_USERNAME') ?: 'root';
$dbPass = getenv('DB_PASSWORD') ?: '';

$isFresh = in_array('--fresh', $argv, true);
$shouldSeed = in_array('--seed', $argv, true) || $isFresh || true; // Seed by default for ready-to-test setup

echo "\n======================================================\n";
echo " SnapTask Database Installer\n";
echo "======================================================\n";
echo "Host:     {$dbHost}:{$dbPort}\n";
echo "Database: {$dbName}\n";
echo "User:     {$dbUser}\n\n";

try {
    // 1. Connect to MySQL Server (without DB first)
    $pdo = new PDO("mysql:host={$dbHost};port={$dbPort};charset=utf8mb4", $dbUser, $dbPass, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    ]);

    if ($isFresh) {
        echo "[!] Dropping database `{$dbName}`...\n";
        $pdo->exec("DROP DATABASE IF EXISTS `{$dbName}`");
    }

    echo "[+] Ensuring database `{$dbName}` exists...\n";
    $pdo->exec("CREATE DATABASE IF NOT EXISTS `{$dbName}` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
    $pdo->exec("USE `{$dbName}`");

    // 2. Ensure migrations table exists
    $pdo->exec("CREATE TABLE IF NOT EXISTS `migrations` (
        `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
        `migration` VARCHAR(255) NOT NULL UNIQUE,
        `batch` INT UNSIGNED NOT NULL,
        `applied_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");

    // Get applied migrations
    $applied = $pdo->query("SELECT `migration` FROM `migrations`")->fetchAll(PDO::FETCH_COLUMN);

    // 3. Scan & Run Migrations
    $migrationsDir = __DIR__ . '/migrations';
    $files = glob("{$migrationsDir}/*.sql");
    sort($files);

    $batch = 1;
    $maxBatch = $pdo->query("SELECT MAX(`batch`) FROM `migrations`")->fetchColumn();
    if ($maxBatch !== false && $maxBatch !== null) {
        $batch = (int)$maxBatch + 1;
    }

    echo "\n--- Running Migrations ---\n";
    $migratedCount = 0;
    foreach ($files as $file) {
        $filename = basename($file);
        if (in_array($filename, $applied, true)) {
            echo "  [-] Skipped (already applied): {$filename}\n";
            continue;
        }

        echo "  [>] Migrating: {$filename} ... ";
        $sql = file_get_contents($file);
        $pdo->exec($sql);

        $stmt = $pdo->prepare("INSERT INTO `migrations` (`migration`, `batch`) VALUES (?, ?)");
        $stmt->execute([$filename, $batch]);

        echo "DONE\n";
        $migratedCount++;
    }

    if ($migratedCount === 0) {
        echo "  No new migrations to run.\n";
    }

    // 4. Run Seeders
    if ($shouldSeed) {
        echo "\n--- Running Seeders ---\n";
        $seedersDir = __DIR__ . '/seeders';
        $seederFiles = glob("{$seedersDir}/*.sql");
        sort($seederFiles);

        foreach ($seederFiles as $seeder) {
            $filename = basename($seeder);
            echo "  [>] Seeding: {$filename} ... ";
            $sql = file_get_contents($seeder);
            $pdo->exec($sql);
            echo "DONE\n";
        }

        // Ensure demo user has guaranteed fresh bcrypt password hash for 'Password123!'
        $validBcrypt = password_hash('Password123!', PASSWORD_BCRYPT);
        $updateStmt = $pdo->prepare("UPDATE `users` SET `password_hash` = ? WHERE `email` = ?");
        $updateStmt->execute([$validBcrypt, 'demo@snaptask.app']);
        echo "  [✓] Verified demo user password hash for 'Password123!'\n";
    }

    echo "\n======================================================\n";
    echo " [✓] Database setup completed successfully!\n";
    echo "======================================================\n\n";

} catch (PDOException $e) {
    echo "\n[ERROR] Database installation failed: " . $e->getMessage() . "\n";
    exit(1);
}


-- Migration: 007_create_logs_tables.sql

CREATE TABLE IF NOT EXISTS `ai_analysis_logs` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `screenshot_id` BIGINT UNSIGNED NOT NULL,
    `engine` VARCHAR(50) NOT NULL DEFAULT 'tesseract',
    `status` ENUM('success', 'failed', 'partial') NOT NULL DEFAULT 'success',
    `request_payload` JSON NULL,
    `response_payload` JSON NULL,
    `duration_ms` INT UNSIGNED NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_ai_logs_screenshot` FOREIGN KEY (`screenshot_id`) REFERENCES `screenshots` (`id`) ON DELETE CASCADE,
    INDEX `idx_ai_logs_screenshot` (`screenshot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `activity_logs` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `action` VARCHAR(100) NOT NULL,
    `entity_type` VARCHAR(50) NOT NULL,
    `entity_id` BIGINT UNSIGNED NULL,
    `metadata` JSON NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_activity_logs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_activity_logs_user_action` (`user_id`, `action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


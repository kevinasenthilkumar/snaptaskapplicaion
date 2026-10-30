-- Migration: 006_create_cleanup_and_trash_tables.sql

CREATE TABLE IF NOT EXISTS `cleanup_jobs` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `status` ENUM('pending', 'in_progress', 'completed', 'failed', 'cancelled') NOT NULL DEFAULT 'pending',
    `mode` ENUM('manual', 'daily_auto') NOT NULL DEFAULT 'manual',
    `total_screenshots` INT NOT NULL DEFAULT 0,
    `space_reclaimed_bytes` BIGINT UNSIGNED NOT NULL DEFAULT 0,
    `started_at` TIMESTAMP NULL,
    `completed_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_cleanup_jobs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_cleanup_jobs_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `cleanup_recommendations` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `screenshot_id` BIGINT UNSIGNED NOT NULL,
    `reason` VARCHAR(255) NOT NULL,
    `recommended_action` ENUM('keep', 'review', 'delete') NOT NULL DEFAULT 'review',
    `risk_level` ENUM('low', 'medium', 'high') NOT NULL DEFAULT 'low',
    `confidence` DECIMAL(4, 3) NOT NULL DEFAULT 1.000,
    `estimated_space_bytes` BIGINT UNSIGNED NOT NULL DEFAULT 0,
    `duplicate_of_id` BIGINT UNSIGNED NULL,
    `is_resolved` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_cleanup_recom_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_cleanup_recom_screenshot` FOREIGN KEY (`screenshot_id`) REFERENCES `screenshots` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_cleanup_recom_duplicate` FOREIGN KEY (`duplicate_of_id`) REFERENCES `screenshots` (`id`) ON DELETE SET NULL,
    INDEX `idx_cleanup_recom_user_resolved` (`user_id`, `is_resolved`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `cleanup_history` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `job_id` BIGINT UNSIGNED NOT NULL,
    `screenshot_id` BIGINT UNSIGNED NOT NULL,
    `original_title` VARCHAR(255) NULL,
    `file_size` BIGINT UNSIGNED NOT NULL,
    `deleted_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_cleanup_hist_job` FOREIGN KEY (`job_id`) REFERENCES `cleanup_jobs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `trash_items` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `screenshot_id` BIGINT UNSIGNED NOT NULL UNIQUE,
    `original_category` VARCHAR(50) NOT NULL,
    `original_title` VARCHAR(255) NULL,
    `file_size` BIGINT UNSIGNED NOT NULL DEFAULT 0,
    `trashed_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `purge_due_at` TIMESTAMP NOT NULL,
    `is_restored` TINYINT(1) NOT NULL DEFAULT 0,
    `restored_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_trash_items_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_trash_items_screenshot` FOREIGN KEY (`screenshot_id`) REFERENCES `screenshots` (`id`) ON DELETE CASCADE,
    INDEX `idx_trash_items_user_restored` (`user_id`, `is_restored`),
    INDEX `idx_trash_items_purge_due` (`purge_due_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Migration: 005_create_reminders_and_notifications_tables.sql

CREATE TABLE IF NOT EXISTS `reminders` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `dated_item_id` BIGINT UNSIGNED NULL,
    `title` VARCHAR(255) NOT NULL,
    `note` TEXT NULL,
    `trigger_at` TIMESTAMP NOT NULL,
    `snooze_count` INT NOT NULL DEFAULT 0,
    `status` ENUM('pending', 'triggered', 'snoozed', 'completed', 'cancelled') NOT NULL DEFAULT 'pending',
    `snoozed_until` TIMESTAMP NULL,
    `completed_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_reminders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_reminders_dated_item` FOREIGN KEY (`dated_item_id`) REFERENCES `dated_items` (`id`) ON DELETE CASCADE,
    INDEX `idx_reminders_user_status` (`user_id`, `status`),
    INDEX `idx_reminders_trigger_at` (`trigger_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `notifications` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `type` ENUM('reminder_due', 'screenshot_classified', 'cleanup_recommended', 'cleanup_completed', 'item_expired', 'system_alert') NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `body` TEXT NOT NULL,
    `data` JSON NULL,
    `is_read` TINYINT(1) NOT NULL DEFAULT 0,
    `read_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_notifications_user_read` (`user_id`, `is_read`),
    INDEX `idx_notifications_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


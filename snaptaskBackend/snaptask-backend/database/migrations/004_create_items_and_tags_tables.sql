-- Migration: 004_create_items_and_tags_tables.sql

CREATE TABLE IF NOT EXISTS `dated_items` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `screenshot_id` BIGINT UNSIGNED NOT NULL UNIQUE,
    `title` VARCHAR(255) NOT NULL,
    `description` TEXT NULL,
    `category` ENUM('ticket', 'bill', 'event', 'reservation', 'appointment', 'deadline', 'travel', 'booking', 'other') NOT NULL DEFAULT 'event',
    `event_date` DATE NOT NULL,
    `event_time` TIME NULL,
    `end_date` DATE NULL,
    `location` VARCHAR(255) NULL,
    `amount` DECIMAL(12, 2) NULL,
    `currency` VARCHAR(10) NULL DEFAULT 'USD',
    `priority` ENUM('low', 'medium', 'high') NOT NULL DEFAULT 'medium',
    `status` ENUM('active', 'completed', 'expired', 'cancelled') NOT NULL DEFAULT 'active',
    `reminder_enabled` TINYINT(1) NOT NULL DEFAULT 1,
    `reminder_at` TIMESTAMP NULL,
    `completed_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_dated_items_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_dated_items_screenshot` FOREIGN KEY (`screenshot_id`) REFERENCES `screenshots` (`id`) ON DELETE CASCADE,
    INDEX `idx_dated_items_user_date` (`user_id`, `event_date`),
    INDEX `idx_dated_items_user_status` (`user_id`, `status`),
    INDEX `idx_dated_items_category` (`category`),
    INDEX `idx_dated_items_reminder_at` (`reminder_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `non_dated_items` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `screenshot_id` BIGINT UNSIGNED NOT NULL UNIQUE,
    `title` VARCHAR(255) NOT NULL,
    `description` TEXT NULL,
    `category` ENUM('recipe', 'design', 'shopping', 'article', 'reference', 'receipt', 'idea', 'other') NOT NULL DEFAULT 'idea',
    `content_text` MEDIUMTEXT NULL,
    `is_favorite` TINYINT(1) NOT NULL DEFAULT 0,
    `color_label` VARCHAR(20) NULL DEFAULT '#EDE7DE',
    `ai_metadata` JSON NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_non_dated_items_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_non_dated_items_screenshot` FOREIGN KEY (`screenshot_id`) REFERENCES `screenshots` (`id`) ON DELETE CASCADE,
    INDEX `idx_non_dated_items_user_cat` (`user_id`, `category`),
    INDEX `idx_non_dated_items_favorite` (`user_id`, `is_favorite`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `tags` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `color` VARCHAR(20) NOT NULL DEFAULT '#721D38',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_tags_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_tag_name` (`user_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `item_tags` (
    `non_dated_item_id` BIGINT UNSIGNED NOT NULL,
    `tag_id` BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (`non_dated_item_id`, `tag_id`),
    CONSTRAINT `fk_item_tags_item` FOREIGN KEY (`non_dated_item_id`) REFERENCES `non_dated_items` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_item_tags_tag` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


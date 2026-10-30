-- ==============================================================================
-- SnapTask Complete Database Schema
-- Target: MySQL 8.0+ / MariaDB 10.4+ / XAMPP / Laragon
-- Database: snaptask_db
-- ==============================================================================

-- 1. Create and Select Database
CREATE DATABASE IF NOT EXISTS `snaptask_db`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `snaptask_db`;

-- 2. Disable Foreign Key Checks During Schema Definition
SET FOREIGN_KEY_CHECKS = 0;


-- ------------------------------------------------------------------------------
-- 1. Authentication & Users
-- ------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `name` VARCHAR(120) NOT NULL,
    `avatar_url` VARCHAR(500) NULL,
    `status` ENUM('active', 'inactive', 'suspended') NOT NULL DEFAULT 'active',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_email` (`email`),
    INDEX `idx_users_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `user_sessions` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `token_id` VARCHAR(64) NOT NULL,
    `refresh_token_hash` VARCHAR(255) NOT NULL,
    `device_name` VARCHAR(150) NULL,
    `ip_address` VARCHAR(45) NULL,
    `user_agent` TEXT NULL,
    `expires_at` DATETIME NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_sessions_token_id` (`token_id`),
    INDEX `idx_user_sessions_user_id` (`user_id`),
    INDEX `idx_user_sessions_expires_at` (`expires_at`),
    CONSTRAINT `fk_user_sessions_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `password_resets` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL,
    `token_hash` VARCHAR(255) NOT NULL,
    `expires_at` DATETIME NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_password_resets_email` (`email`),
    INDEX `idx_password_resets_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ------------------------------------------------------------------------------
-- 2. User Settings, Automation & Rules
-- ------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `user_settings` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `auto_scan_screenshots` TINYINT(1) NOT NULL DEFAULT 1,
    `auto_categorize` TINYINT(1) NOT NULL DEFAULT 1,
    `default_lead_time_days` INT NOT NULL DEFAULT 1,
    `cleanup_frequency` ENUM('manual', 'daily', 'weekly') NOT NULL DEFAULT 'manual',
    `cleanup_reminders_enabled` TINYINT(1) NOT NULL DEFAULT 1,
    `cleanup_summaries_enabled` TINYINT(1) NOT NULL DEFAULT 0,
    `protect_kept_screenshots` TINYINT(1) NOT NULL DEFAULT 1,
    `ai_organization_enabled` TINYINT(1) NOT NULL DEFAULT 1,
    `share_analytics` TINYINT(1) NOT NULL DEFAULT 0,
    `trash_retention_days` INT NOT NULL DEFAULT 30,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_settings_user_id` (`user_id`),
    CONSTRAINT `fk_user_settings_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `automation_settings` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `mode` ENUM('daily', 'manual') NOT NULL DEFAULT 'manual',
    `daily_time` TIME NOT NULL DEFAULT '09:00:00',
    `requires_consent` TINYINT(1) NOT NULL DEFAULT 1,
    `last_run_at` DATETIME NULL,
    `next_run_at` DATETIME NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_automation_settings_user_id` (`user_id`),
    CONSTRAINT `fk_automation_settings_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `cleanup_rules` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `rule_name` VARCHAR(100) NOT NULL,
    `item_type` ENUM('dated', 'non_dated', 'receipts', 'tickets', 'all') NOT NULL,
    `retention_days` INT NOT NULL DEFAULT 7,
    `notify_days_before` INT NOT NULL DEFAULT 1,
    `is_active` TINYINT(1) NOT NULL DEFAULT 1,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_cleanup_rules_user_type` (`user_id`, `item_type`),
    CONSTRAINT `fk_cleanup_rules_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ------------------------------------------------------------------------------
-- 3. Media & Screenshots
-- ------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `media_files` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `disk` VARCHAR(50) NOT NULL DEFAULT 'local',
    `file_path` VARCHAR(500) NOT NULL,
    `file_name` VARCHAR(255) NOT NULL,
    `mime_type` VARCHAR(100) NOT NULL,
    `file_size` BIGINT UNSIGNED NOT NULL,
    `width` INT UNSIGNED NULL,
    `height` INT UNSIGNED NULL,
    `checksum_sha256` VARCHAR(64) NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_media_files_user_id` (`user_id`),
    INDEX `idx_media_files_checksum` (`checksum_sha256`),
    CONSTRAINT `fk_media_files_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `screenshots` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `media_file_id` BIGINT UNSIGNED NULL,
    `local_media_id` VARCHAR(255) NULL,
    `file_path` VARCHAR(500) NOT NULL,
    `file_name` VARCHAR(255) NOT NULL,
    `file_hash` VARCHAR(64) NOT NULL,
    `perceptual_hash` VARCHAR(64) NULL,
    `mime_type` VARCHAR(100) NOT NULL DEFAULT 'image/png',
    `file_size` BIGINT UNSIGNED NOT NULL DEFAULT 0,
    `width` INT UNSIGNED NULL,
    `height` INT UNSIGNED NULL,
    `category` ENUM('dated', 'non_dated', 'unclassified') NOT NULL DEFAULT 'unclassified',
    `classification_confidence` DECIMAL(4, 3) NOT NULL DEFAULT 0.000,
    `ocr_text` MEDIUMTEXT NULL,
    `extracted_title` VARCHAR(255) NULL,
    `extracted_description` TEXT NULL,
    `extracted_date` DATE NULL,
    `extracted_time` TIME NULL,
    `extracted_location` VARCHAR(255) NULL,
    `extracted_amount` DECIMAL(12, 2) NULL,
    `extracted_currency` VARCHAR(10) NULL,
    `source_app` VARCHAR(100) NULL,
    `status` ENUM('pending', 'processing', 'classified', 'failed') NOT NULL DEFAULT 'pending',
    `is_kept` TINYINT(1) NOT NULL DEFAULT 0,
    `ai_metadata` JSON NULL,
    `captured_at` DATETIME NULL,
    `uploaded_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted_at` DATETIME NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_screenshots_user_status` (`user_id`, `status`),
    INDEX `idx_screenshots_user_category` (`user_id`, `category`),
    INDEX `idx_screenshots_file_hash` (`file_hash`),
    INDEX `idx_screenshots_perceptual_hash` (`perceptual_hash`),
    INDEX `idx_screenshots_extracted_date` (`extracted_date`),
    INDEX `idx_screenshots_is_kept` (`is_kept`),
    INDEX `idx_screenshots_deleted_at` (`deleted_at`),
    FULLTEXT KEY `ft_screenshots_text` (`extracted_title`, `ocr_text`),
    CONSTRAINT `fk_screenshots_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `fk_screenshots_media_file`
        FOREIGN KEY (`media_file_id`) REFERENCES `media_files` (`id`)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ------------------------------------------------------------------------------
-- 4. Items: Dated & Non-Dated
-- ------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `dated_items` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `screenshot_id` BIGINT UNSIGNED NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `description` TEXT NULL,
    `category` ENUM(
        'ticket', 'bill', 'event', 'reservation', 'appointment',
        'deadline', 'travel', 'booking', 'other'
    ) NOT NULL DEFAULT 'event',
    `event_date` DATE NOT NULL,
    `event_time` TIME NULL,
    `end_date` DATE NULL,
    `location` VARCHAR(255) NULL,
    `amount` DECIMAL(12, 2) NULL,
    `currency` VARCHAR(10) NULL DEFAULT 'USD',
    `priority` ENUM('low', 'medium', 'high') NOT NULL DEFAULT 'medium',
    `status` ENUM('active', 'completed', 'expired', 'cancelled') NOT NULL DEFAULT 'active',
    `reminder_enabled` TINYINT(1) NOT NULL DEFAULT 1,
    `reminder_at` DATETIME NULL,
    `completed_at` DATETIME NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dated_items_screenshot_id` (`screenshot_id`),
    INDEX `idx_dated_items_user_date` (`user_id`, `event_date`),
    INDEX `idx_dated_items_user_status` (`user_id`, `status`),
    INDEX `idx_dated_items_category` (`category`),
    INDEX `idx_dated_items_reminder_at` (`reminder_at`),
    CONSTRAINT `fk_dated_items_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `fk_dated_items_screenshot`
        FOREIGN KEY (`screenshot_id`) REFERENCES `screenshots` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `non_dated_items` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `screenshot_id` BIGINT UNSIGNED NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `description` TEXT NULL,
    `category` ENUM(
        'recipe', 'design', 'shopping', 'article', 'reference',
        'receipt', 'idea', 'other'
    ) NOT NULL DEFAULT 'idea',
    `content_text` MEDIUMTEXT NULL,
    `is_favorite` TINYINT(1) NOT NULL DEFAULT 0,
    `color_label` VARCHAR(20) NULL DEFAULT '#EDE7DE',
    `ai_metadata` JSON NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_non_dated_items_screenshot_id` (`screenshot_id`),
    INDEX `idx_non_dated_items_user_cat` (`user_id`, `category`),
    INDEX `idx_non_dated_items_favorite` (`user_id`, `is_favorite`),
    CONSTRAINT `fk_non_dated_items_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `fk_non_dated_items_screenshot`
        FOREIGN KEY (`screenshot_id`) REFERENCES `screenshots` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `tags` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `color` VARCHAR(20) NOT NULL DEFAULT '#721D38',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_tag_name` (`user_id`, `name`),
    CONSTRAINT `fk_tags_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `item_tags` (
    `non_dated_item_id` BIGINT UNSIGNED NOT NULL,
    `tag_id` BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (`non_dated_item_id`, `tag_id`),
    CONSTRAINT `fk_item_tags_item`
        FOREIGN KEY (`non_dated_item_id`) REFERENCES `non_dated_items` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `fk_item_tags_tag`
        FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `dated_item_tags` (
    `dated_item_id` BIGINT UNSIGNED NOT NULL,
    `tag_id` BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (`dated_item_id`, `tag_id`),
    CONSTRAINT `fk_dated_item_tags_item`
        FOREIGN KEY (`dated_item_id`) REFERENCES `dated_items` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `fk_dated_item_tags_tag`
        FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ------------------------------------------------------------------------------
-- 5. Reminders & Notifications
-- ------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `reminders` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `dated_item_id` BIGINT UNSIGNED NULL,
    `title` VARCHAR(255) NOT NULL,
    `note` TEXT NULL,
    `trigger_at` DATETIME NOT NULL,
    `snooze_count` INT NOT NULL DEFAULT 0,
    `status` ENUM(
        'pending', 'triggered', 'snoozed', 'completed', 'cancelled'
    ) NOT NULL DEFAULT 'pending',
    `snoozed_until` DATETIME NULL,
    `completed_at` DATETIME NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_reminders_user_status` (`user_id`, `status`),
    INDEX `idx_reminders_trigger_at` (`trigger_at`),
    CONSTRAINT `fk_reminders_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `fk_reminders_dated_item`
        FOREIGN KEY (`dated_item_id`) REFERENCES `dated_items` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `notifications` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `type` ENUM(
        'reminder_due', 'screenshot_classified', 'cleanup_recommended',
        'cleanup_completed', 'item_expired', 'system_alert'
    ) NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `body` TEXT NOT NULL,
    `data` JSON NULL,
    `is_read` TINYINT(1) NOT NULL DEFAULT 0,
    `read_at` DATETIME NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_notifications_user_read` (`user_id`, `is_read`),
    INDEX `idx_notifications_created_at` (`created_at`),
    CONSTRAINT `fk_notifications_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ------------------------------------------------------------------------------
-- 6. Cleanup Engine, Jobs & Trash Safety Net
-- ------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `cleanup_jobs` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `status` ENUM(
        'pending', 'in_progress', 'completed', 'failed', 'cancelled'
    ) NOT NULL DEFAULT 'pending',
    `mode` ENUM('manual', 'daily_auto') NOT NULL DEFAULT 'manual',
    `total_screenshots` INT NOT NULL DEFAULT 0,
    `space_reclaimed_bytes` BIGINT UNSIGNED NOT NULL DEFAULT 0,
    `started_at` DATETIME NULL,
    `completed_at` DATETIME NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_cleanup_jobs_user_status` (`user_id`, `status`),
    CONSTRAINT `fk_cleanup_jobs_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `cleanup_recommendations` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
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
    PRIMARY KEY (`id`),
    INDEX `idx_cleanup_recom_user_resolved` (`user_id`, `is_resolved`),
    CONSTRAINT `fk_cleanup_recom_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `fk_cleanup_recom_screenshot`
        FOREIGN KEY (`screenshot_id`) REFERENCES `screenshots` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `fk_cleanup_recom_duplicate`
        FOREIGN KEY (`duplicate_of_id`) REFERENCES `screenshots` (`id`)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `cleanup_history` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `job_id` BIGINT UNSIGNED NOT NULL,
    `screenshot_id` BIGINT UNSIGNED NOT NULL,
    `original_title` VARCHAR(255) NULL,
    `file_size` BIGINT UNSIGNED NOT NULL,
    `deleted_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_cleanup_history_screenshot` (`screenshot_id`),
    CONSTRAINT `fk_cleanup_hist_job`
        FOREIGN KEY (`job_id`) REFERENCES `cleanup_jobs` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `fk_cleanup_hist_screenshot`
        FOREIGN KEY (`screenshot_id`) REFERENCES `screenshots` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `trash_items` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `screenshot_id` BIGINT UNSIGNED NOT NULL,
    `original_category` VARCHAR(50) NOT NULL,
    `original_title` VARCHAR(255) NULL,
    `file_size` BIGINT UNSIGNED NOT NULL DEFAULT 0,
    `trashed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `purge_due_at` DATETIME NOT NULL,
    `is_restored` TINYINT(1) NOT NULL DEFAULT 0,
    `restored_at` DATETIME NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_trash_items_screenshot_id` (`screenshot_id`),
    INDEX `idx_trash_items_user_restored` (`user_id`, `is_restored`),
    INDEX `idx_trash_items_purge_due` (`purge_due_at`),
    CONSTRAINT `fk_trash_items_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `fk_trash_items_screenshot`
        FOREIGN KEY (`screenshot_id`) REFERENCES `screenshots` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ------------------------------------------------------------------------------
-- 7. AI & Activity Logging
-- ------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `ai_analysis_logs` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `screenshot_id` BIGINT UNSIGNED NOT NULL,
    `engine` VARCHAR(50) NOT NULL DEFAULT 'tesseract',
    `status` ENUM('success', 'failed', 'partial') NOT NULL DEFAULT 'success',
    `request_payload` JSON NULL,
    `response_payload` JSON NULL,
    `duration_ms` INT UNSIGNED NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_ai_logs_screenshot` (`screenshot_id`),
    CONSTRAINT `fk_ai_logs_screenshot`
        FOREIGN KEY (`screenshot_id`) REFERENCES `screenshots` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `activity_logs` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `action` VARCHAR(100) NOT NULL,
    `entity_type` VARCHAR(50) NOT NULL,
    `entity_id` BIGINT UNSIGNED NULL,
    `metadata` JSON NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_activity_logs_user_action` (`user_id`, `action`),
    CONSTRAINT `fk_activity_logs_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 3. Restore Foreign Key Checks
SET FOREIGN_KEY_CHECKS = 1;
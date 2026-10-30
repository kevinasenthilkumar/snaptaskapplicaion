-- Migration: 002_create_settings_and_rules_tables.sql

CREATE TABLE IF NOT EXISTS `user_settings` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL UNIQUE,
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
    CONSTRAINT `fk_user_settings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `automation_settings` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL UNIQUE,
    `mode` ENUM('daily', 'manual') NOT NULL DEFAULT 'manual',
    `daily_time` TIME NOT NULL DEFAULT '09:00:00',
    `requires_consent` TINYINT(1) NOT NULL DEFAULT 1,
    `last_run_at` TIMESTAMP NULL,
    `next_run_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_automation_settings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `cleanup_rules` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `rule_name` VARCHAR(100) NOT NULL,
    `item_type` ENUM('dated', 'non_dated', 'receipts', 'tickets', 'all') NOT NULL,
    `retention_days` INT NOT NULL DEFAULT 7,
    `notify_days_before` INT NOT NULL DEFAULT 1,
    `is_active` TINYINT(1) NOT NULL DEFAULT 1,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_cleanup_rules_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_cleanup_rules_user_type` (`user_id`, `item_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


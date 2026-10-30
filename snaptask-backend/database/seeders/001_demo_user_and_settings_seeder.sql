-- Seeder: 001_demo_user_and_settings_seeder.sql

-- 1. Demo User (email: demo@snaptask.app, password: Password123!)
-- Password hash generated using password_hash('Password123!', PASSWORD_BCRYPT)
INSERT INTO `users` (`id`, `email`, `password_hash`, `name`, `avatar_url`, `status`, `created_at`, `updated_at`)
VALUES (
    1,
    'demo@snaptask.app',
    '$2y$10$yq0b2WfGZ2pMlh6Nf57Puu9m7e7K7h6S9M4j3k8Z1b0O8p7Q5s4lK',
    'Alex Taylor',
    'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&h=200',
    'active',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE `email` = VALUES(`email`);

-- 2. User Settings
INSERT INTO `user_settings` (
    `id`, `user_id`, `auto_scan_screenshots`, `auto_categorize`, `default_lead_time_days`,
    `cleanup_frequency`, `cleanup_reminders_enabled`, `cleanup_summaries_enabled`,
    `protect_kept_screenshots`, `ai_organization_enabled`, `share_analytics`,
    `trash_retention_days`, `created_at`, `updated_at`
) VALUES (
    1, 1, 1, 1, 1,
    'manual', 1, 0,
    1, 1, 0,
    30, NOW(), NOW()
) ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`);

-- 3. Automation Settings (Default to Manual / Never run automatically)
INSERT INTO `automation_settings` (
    `id`, `user_id`, `mode`, `daily_time`, `requires_consent`,
    `last_run_at`, `next_run_at`, `created_at`, `updated_at`
) VALUES (
    1, 1, 'manual', '09:00:00', 1,
    NULL, NULL, NOW(), NOW()
) ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`);

-- 4. Default Cleanup Rules
INSERT INTO `cleanup_rules` (`id`, `user_id`, `rule_name`, `item_type`, `retention_days`, `notify_days_before`, `is_active`)
VALUES
    (1, 1, 'Dated screenshots rule', 'dated', 1, 1, 1),
    (2, 1, 'Non-dated screenshots rule', 'non_dated', 7, 1, 1),
    (3, 1, 'Receipts retention rule', 'receipts', 14, 2, 1)
ON DUPLICATE KEY UPDATE `rule_name` = VALUES(`rule_name`);


-- ==============================================================================
-- SnapTask Complete Seed Data
-- Database: snaptask_db
-- Compatible with: snaptask-backend/database/schema.sql
-- ==============================================================================

USE `snaptask_db`;

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------------------------
-- 1. Demo User
-- Email: demo@snaptask.app
-- Password: Password123!
-- NOTE: Replace password_hash with a real bcrypt hash before production use.
-- ------------------------------------------------------------------------------

INSERT INTO `users` (
    `id`, `email`, `password_hash`, `name`, `avatar_url`, `status`,
    `created_at`, `updated_at`
)
VALUES (
    1,
    'demo@snaptask.app',
    '$2y$10$e0NRz2dOYm0uJ7f6rJ7yUuJv0Q5Xy8Zq1wE9tR4uI6oP2aS8dF1gC',
    'Alex Taylor',
    'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&h=200',
    'active',
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    `email` = VALUES(`email`),
    `name` = VALUES(`name`),
    `avatar_url` = VALUES(`avatar_url`),
    `status` = VALUES(`status`),
    `updated_at` = NOW();


-- ------------------------------------------------------------------------------
-- 2. User Settings
-- ------------------------------------------------------------------------------

INSERT INTO `user_settings` (
    `id`, `user_id`, `auto_scan_screenshots`, `auto_categorize`,
    `default_lead_time_days`, `cleanup_frequency`, `cleanup_reminders_enabled`,
    `cleanup_summaries_enabled`, `protect_kept_screenshots`,
    `ai_organization_enabled`, `share_analytics`, `trash_retention_days`,
    `created_at`, `updated_at`
)
VALUES (
    1, 1, 1, 1, 1,
    'manual', 1, 0,
    1, 1, 0,
    30,
    NOW(), NOW()
)
ON DUPLICATE KEY UPDATE
    `auto_scan_screenshots` = VALUES(`auto_scan_screenshots`),
    `auto_categorize` = VALUES(`auto_categorize`),
    `default_lead_time_days` = VALUES(`default_lead_time_days`),
    `cleanup_frequency` = VALUES(`cleanup_frequency`),
    `cleanup_reminders_enabled` = VALUES(`cleanup_reminders_enabled`),
    `cleanup_summaries_enabled` = VALUES(`cleanup_summaries_enabled`),
    `protect_kept_screenshots` = VALUES(`protect_kept_screenshots`),
    `ai_organization_enabled` = VALUES(`ai_organization_enabled`),
    `share_analytics` = VALUES(`share_analytics`),
    `trash_retention_days` = VALUES(`trash_retention_days`),
    `updated_at` = NOW();


-- ------------------------------------------------------------------------------
-- 3. Automation Settings
-- ------------------------------------------------------------------------------

INSERT INTO `automation_settings` (
    `id`, `user_id`, `mode`, `daily_time`, `requires_consent`,
    `last_run_at`, `next_run_at`, `created_at`, `updated_at`
)
VALUES (
    1, 1, 'manual', '09:00:00', 1,
    NULL, NULL,
    NOW(), NOW()
)
ON DUPLICATE KEY UPDATE
    `mode` = VALUES(`mode`),
    `daily_time` = VALUES(`daily_time`),
    `requires_consent` = VALUES(`requires_consent`),
    `updated_at` = NOW();


-- ------------------------------------------------------------------------------
-- 4. Default Cleanup Rules
-- ------------------------------------------------------------------------------

INSERT INTO `cleanup_rules` (
    `id`, `user_id`, `rule_name`, `item_type`,
    `retention_days`, `notify_days_before`, `is_active`
)
VALUES
    (1, 1, 'Dated screenshots rule', 'dated', 1, 1, 1),
    (2, 1, 'Non-dated screenshots rule', 'non_dated', 7, 1, 1),
    (3, 1, 'Receipts retention rule', 'receipts', 14, 2, 1)
ON DUPLICATE KEY UPDATE
    `rule_name` = VALUES(`rule_name`),
    `item_type` = VALUES(`item_type`),
    `retention_days` = VALUES(`retention_days`),
    `notify_days_before` = VALUES(`notify_days_before`),
    `is_active` = VALUES(`is_active`),
    `updated_at` = NOW();


-- ------------------------------------------------------------------------------
-- 5. Media Files
-- ------------------------------------------------------------------------------

INSERT INTO `media_files` (
    `id`, `user_id`, `disk`, `file_path`, `file_name`, `mime_type`,
    `file_size`, `width`, `height`, `checksum_sha256`, `created_at`
)
VALUES
    (1, 1, 'local', 'uploads/jazz_garden_ticket.png', 'jazz_garden_ticket.png', 'image/png', 3145728, 1080, 2400, 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', NOW()),
    (2, 1, 'local', 'uploads/arc_lamp_shopping.png', 'arc_lamp_shopping.png', 'image/png', 2621440, 1080, 2340, '5d41402abc4b2a76b9719d911017c592', NOW()),
    (3, 1, 'local', 'uploads/pasta_recipe.png', 'pasta_recipe.png', 'image/png', 1887436, 1080, 2280, '098f6bcd4621d373cade4e832627b4f6', NOW()),
    (4, 1, 'local', 'uploads/museum_late_ticket.png', 'museum_late_ticket.png', 'image/png', 3250585, 1080, 2400, '7d793037a0760186574b0282f2f435e7', NOW()),
    (5, 1, 'local', 'uploads/edinburgh_flight.png', 'edinburgh_flight.png', 'image/png', 3407872, 1080, 2400, '3389dae361af79b04c9c8e7057f60cc6', NOW()),
    (6, 1, 'local', 'uploads/ceramics_workshop.png', 'ceramics_workshop.png', 'image/png', 2883584, 1080, 2340, '6079c6e52a543130dcf5188367048802', NOW()),
    (7, 1, 'local', 'uploads/market_street_receipt.png', 'market_street_receipt.png', 'image/png', 2411724, 1080, 2300, '25f9e794323b453885f5181f1b624d0b', NOW()),
    (8, 1, 'local', 'uploads/canvas_trainers.png', 'canvas_trainers.png', 'image/png', 3145728, 1080, 2400, '182be0c5cdcd5072bb1864cdee4d3d6e', NOW()),
    (9, 1, 'local', 'uploads/cabin_ideas.png', 'cabin_ideas.png', 'image/png', 3984588, 1080, 2400, '3858f62230ac3c915f300c664312c63f', NOW())
ON DUPLICATE KEY UPDATE
    `file_path` = VALUES(`file_path`),
    `file_name` = VALUES(`file_name`),
    `mime_type` = VALUES(`mime_type`),
    `file_size` = VALUES(`file_size`),
    `width` = VALUES(`width`),
    `height` = VALUES(`height`),
    `checksum_sha256` = VALUES(`checksum_sha256`);


-- ------------------------------------------------------------------------------
-- 6. Screenshots
-- Dates are dynamic so dashboard, reminders, overdue, and cleanup filters work.
-- ------------------------------------------------------------------------------

INSERT INTO `screenshots` (
    `id`, `user_id`, `media_file_id`, `local_media_id`, `file_path`, `file_name`,
    `file_hash`, `perceptual_hash`, `mime_type`, `file_size`, `width`, `height`,
    `category`, `classification_confidence`, `ocr_text`, `extracted_title`,
    `extracted_description`, `extracted_date`, `extracted_time`,
    `extracted_location`, `extracted_amount`, `extracted_currency`, `source_app`,
    `status`, `is_kept`, `ai_metadata`, `captured_at`, `uploaded_at`, `deleted_at`
)
VALUES
(
    1, 1, 1, 'media_001', 'uploads/jazz_garden_ticket.png', 'jazz_garden_ticket.png',
    'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
    'a4b2c8d1e3f60718', 'image/png', 3145728, 1080, 2400,
    'dated', 0.985,
    'THE GARDEN • LIVE JAZZ AT THE GARDEN FRI • 19:30 ADMIT ONE SECTION B ROW 4 SEAT 12',
    'Jazz at the Garden',
    'Live jazz concert ticket at The Garden venue.',
    DATE_ADD(CURDATE(), INTERVAL 3 DAY), '19:30:00',
    'The Garden', 45.00, 'USD', 'Ticketmaster',
    'classified', 0,
    JSON_OBJECT('venue', 'The Garden', 'gate', 'Main Gate', 'seat', 'B-4-12', 'qr_code_detected', TRUE),
    DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL
),
(
    2, 1, 2, 'media_002', 'uploads/arc_lamp_shopping.png', 'arc_lamp_shopping.png',
    '5d41402abc4b2a76b9719d911017c592',
    'b8c1d2e3f4051627', 'image/png', 2621440, 1080, 2340,
    'non_dated', 0.940,
    'FORM & FIELD Arc table lamp Matte black steel with brushed brass accent $129.00 In stock Add to bag',
    'Arc table lamp',
    'Minimalist modern desk and table lamp wishlist item.',
    NULL, NULL,
    'Form & Field', 129.00, 'USD', 'Chrome',
    'classified', 0,
    JSON_OBJECT('merchant', 'Form & Field', 'item_type', 'Furniture/Lighting', 'sku', 'FF-LAMP-BLK'),
    DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), NULL
),
(
    3, 1, 3, 'media_003', 'uploads/pasta_recipe.png', 'pasta_recipe.png',
    '098f6bcd4621d373cade4e832627b4f6',
    'c1d2e3f405162738', 'image/png', 1887436, 1080, 2280,
    'non_dated', 0.965,
    'Creamy Garlic Parmesan Pasta: 200g fettuccine, 3 cloves garlic, 1 cup heavy cream, 1/2 cup parmesan cheese, fresh parsley.',
    'Creamy Garlic Parmesan Pasta',
    'Quick 15-minute skillet dinner recipe.',
    NULL, NULL,
    NULL, NULL, NULL, 'Instagram',
    'classified', 1,
    JSON_OBJECT('prep_time', '15 mins', 'ingredients_count', 5, 'servings', 2),
    DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), NULL
),
(
    4, 1, 4, 'media_004', 'uploads/museum_late_ticket.png', 'museum_late_ticket.png',
    '7d793037a0760186574b0282f2f435e7',
    'd2e3f40516273849', 'image/png', 3250585, 1080, 2400,
    'dated', 0.970,
    'Contemporary Art Museum Late Opening entry ticket Thursday 20:00 general admission',
    'Museum late opening',
    'Night tour admission for museum exhibition.',
    DATE_ADD(CURDATE(), INTERVAL 2 DAY), '20:00:00',
    'Contemporary Art Museum', 22.50, 'USD', 'Eventbrite',
    'classified', 0,
    JSON_OBJECT('event_status', 'upcoming'),
    DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), NULL
),
(
    5, 1, 5, 'media_005', 'uploads/edinburgh_flight.png', 'edinburgh_flight.png',
    '3389dae361af79b04c9c8e7057f60cc6',
    'e3f4051627384950', 'image/png', 3407872, 1080, 2400,
    'dated', 0.990,
    'Boarding Pass Flight BA1442 London Heathrow to Edinburgh EDI Depart 14:15 Gate 14 Seat 18C',
    'Flight to Edinburgh',
    'Airline boarding pass for Scotland weekend trip.',
    DATE_ADD(CURDATE(), INTERVAL 2 DAY), '14:15:00',
    'Heathrow Airport', 115.00, 'GBP', 'British Airways',
    'classified', 0,
    JSON_OBJECT('flight_number', 'BA1442', 'seat', '18C'),
    DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), NULL
),
(
    6, 1, 6, 'media_006', 'uploads/ceramics_workshop.png', 'ceramics_workshop.png',
    '6079c6e52a543130dcf5188367048802',
    'f405162738495061', 'image/png', 2883584, 1080, 2340,
    'dated', 0.930,
    'Clay & Co Ceramics Wheel Throwing Workshop Thursday 18:00 Studio 3',
    'Ceramics workshop',
    'Hands-on pottery workshop reservation.',
    DATE_ADD(CURDATE(), INTERVAL 2 DAY), '18:00:00',
    'Clay & Co Studio', 60.00, 'USD', 'Mindbody',
    'classified', 0,
    JSON_OBJECT('instructor', 'Sarah Jenkins'),
    DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), NULL
),
(
    7, 1, 7, 'media_007', 'uploads/market_street_receipt.png', 'market_street_receipt.png',
    '25f9e794323b453885f5181f1b624d0b',
    '0516273849506172', 'image/png', 2411724, 1080, 2300,
    'non_dated', 0.920,
    'Market Street Organic Grocers Total $38.40 Card payment approved',
    'Market Street receipt',
    'Grocery store purchase receipt.',
    NULL, NULL,
    'Market Street Grocers', 38.40, 'USD', 'Camera',
    'classified', 0,
    JSON_OBJECT('store_id', 'MS-104'),
    DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY), NULL
),
(
    8, 1, 8, 'media_008', 'uploads/canvas_trainers.png', 'canvas_trainers.png',
    '182be0c5cdcd5072bb1864cdee4d3d6e',
    '1627384950617283', 'image/png', 3145728, 1080, 2400,
    'non_dated', 0.890,
    'Vintage Canvas Low Trainers Natural Off-White Unisex Size 42 £75.00',
    'Canvas trainers',
    'Sneakers product screenshot for wardrobe update.',
    NULL, NULL,
    'End Clothing', 75.00, 'GBP', 'Safari',
    'classified', 0,
    JSON_OBJECT('color', 'Off-White', 'brand', 'Novesta'),
    DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), NULL
),
(
    9, 1, 9, 'media_009', 'uploads/cabin_ideas.png', 'cabin_ideas.png',
    '3858f62230ac3c915f300c664312c63f',
    '2738495061728394', 'image/png', 3984588, 1080, 2400,
    'non_dated', 0.910,
    'A-Frame Forest Cabin with cedar hot tub and mountain views Pacific Northwest',
    'Weekend cabin ideas',
    'Vacation architectural inspiration and getaway bookmark.',
    NULL, NULL,
    'Mount Rainier', NULL, NULL, 'Pinterest',
    'classified', 0,
    JSON_OBJECT('location', 'PNW', 'type', 'Cabin'),
    DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), NULL
)
ON DUPLICATE KEY UPDATE
    `media_file_id` = VALUES(`media_file_id`),
    `file_path` = VALUES(`file_path`),
    `file_name` = VALUES(`file_name`),
    `file_hash` = VALUES(`file_hash`),
    `perceptual_hash` = VALUES(`perceptual_hash`),
    `category` = VALUES(`category`),
    `classification_confidence` = VALUES(`classification_confidence`),
    `ocr_text` = VALUES(`ocr_text`),
    `extracted_title` = VALUES(`extracted_title`),
    `extracted_description` = VALUES(`extracted_description`),
    `extracted_date` = VALUES(`extracted_date`),
    `extracted_time` = VALUES(`extracted_time`),
    `extracted_location` = VALUES(`extracted_location`),
    `extracted_amount` = VALUES(`extracted_amount`),
    `extracted_currency` = VALUES(`extracted_currency`),
    `source_app` = VALUES(`source_app`),
    `status` = VALUES(`status`),
    `is_kept` = VALUES(`is_kept`),
    `ai_metadata` = VALUES(`ai_metadata`),
    `captured_at` = VALUES(`captured_at`),
    `uploaded_at` = VALUES(`uploaded_at`),
    `deleted_at` = VALUES(`deleted_at`),
    `updated_at` = NOW();


-- ------------------------------------------------------------------------------
-- 7. Dated Items
-- ------------------------------------------------------------------------------

INSERT INTO `dated_items` (
    `id`, `user_id`, `screenshot_id`, `title`, `description`, `category`,
    `event_date`, `event_time`, `end_date`, `location`, `amount`, `currency`,
    `priority`, `status`, `reminder_enabled`, `reminder_at`,
    `completed_at`, `created_at`, `updated_at`
)
VALUES
(
    1, 1, 1, 'Jazz at the Garden',
    'Live jazz concert ticket at The Garden venue.',
    'ticket',
    DATE_ADD(CURDATE(), INTERVAL 3 DAY), '19:30:00',
    DATE_ADD(CURDATE(), INTERVAL 3 DAY),
    'The Garden', 45.00, 'USD',
    'high', 'active', 1,
    DATE_ADD(NOW(), INTERVAL 69 HOUR),
    NULL, NOW(), NOW()
),
(
    2, 1, 4, 'Museum late opening',
    'Night tour admission for museum exhibition.',
    'ticket',
    DATE_ADD(CURDATE(), INTERVAL 2 DAY), '20:00:00',
    DATE_ADD(CURDATE(), INTERVAL 2 DAY),
    'Contemporary Art Museum', 22.50, 'USD',
    'medium', 'active', 1,
    DATE_ADD(NOW(), INTERVAL 44 HOUR),
    NULL, NOW(), NOW()
),
(
    3, 1, 5, 'Flight to Edinburgh',
    'Airline boarding pass for Scotland weekend trip.',
    'travel',
    DATE_ADD(CURDATE(), INTERVAL 2 DAY), '14:15:00',
    DATE_ADD(CURDATE(), INTERVAL 2 DAY),
    'Heathrow Airport', 115.00, 'GBP',
    'high', 'active', 1,
    DATE_ADD(NOW(), INTERVAL 38 HOUR),
    NULL, NOW(), NOW()
),
(
    4, 1, 6, 'Ceramics workshop',
    'Hands-on pottery workshop reservation.',
    'booking',
    DATE_ADD(CURDATE(), INTERVAL 2 DAY), '18:00:00',
    DATE_ADD(CURDATE(), INTERVAL 2 DAY),
    'Clay & Co Studio', 60.00, 'USD',
    'medium', 'active', 1,
    DATE_ADD(NOW(), INTERVAL 42 HOUR),
    NULL, NOW(), NOW()
)
ON DUPLICATE KEY UPDATE
    `title` = VALUES(`title`),
    `description` = VALUES(`description`),
    `category` = VALUES(`category`),
    `event_date` = VALUES(`event_date`),
    `event_time` = VALUES(`event_time`),
    `end_date` = VALUES(`end_date`),
    `location` = VALUES(`location`),
    `amount` = VALUES(`amount`),
    `currency` = VALUES(`currency`),
    `priority` = VALUES(`priority`),
    `status` = VALUES(`status`),
    `reminder_enabled` = VALUES(`reminder_enabled`),
    `reminder_at` = VALUES(`reminder_at`),
    `completed_at` = VALUES(`completed_at`),
    `updated_at` = NOW();


-- ------------------------------------------------------------------------------
-- 8. Non-Dated Items
-- ------------------------------------------------------------------------------

INSERT INTO `non_dated_items` (
    `id`, `user_id`, `screenshot_id`, `title`, `description`, `category`,
    `content_text`, `is_favorite`, `color_label`, `ai_metadata`,
    `created_at`, `updated_at`
)
VALUES
(
    1, 1, 2, 'Arc table lamp',
    'Minimalist modern desk and table lamp wishlist item.',
    'shopping',
    'FORM & FIELD Arc table lamp Matte black steel with brushed brass accent $129.00 In stock',
    1, '#EDE7DE',
    JSON_OBJECT('merchant', 'Form & Field', 'price', 129.00, 'currency', 'USD'),
    NOW(), NOW()
),
(
    2, 1, 3, 'Creamy Garlic Parmesan Pasta',
    'Quick 15-minute skillet dinner recipe.',
    'recipe',
    '200g fettuccine, 3 cloves garlic, 1 cup heavy cream, 1/2 cup parmesan cheese, fresh parsley.',
    1, '#E9EBD9',
    JSON_OBJECT('cuisine', 'Italian', 'difficulty', 'easy', 'servings', 2),
    NOW(), NOW()
),
(
    3, 1, 7, 'Market Street receipt',
    'Grocery store purchase receipt.',
    'receipt',
    'Market Street Organic Grocers Total $38.40 Card payment approved',
    0, '#F8EDF0',
    JSON_OBJECT('total', 38.40, 'currency', 'USD'),
    NOW(), NOW()
),
(
    4, 1, 8, 'Canvas trainers',
    'Sneakers product screenshot for wardrobe update.',
    'shopping',
    'Vintage Canvas Low Trainers Natural Off-White Unisex Size 42 £75.00',
    0, '#FAF6F0',
    JSON_OBJECT('brand', 'Novesta', 'price', 75.00, 'currency', 'GBP'),
    NOW(), NOW()
),
(
    5, 1, 9, 'Weekend cabin ideas',
    'Vacation architectural inspiration and getaway bookmark.',
    'idea',
    'A-Frame Forest Cabin with cedar hot tub and mountain views Pacific Northwest',
    0, '#EDE7DE',
    JSON_OBJECT('theme', 'travel', 'location', 'Pacific Northwest'),
    NOW(), NOW()
)
ON DUPLICATE KEY UPDATE
    `title` = VALUES(`title`),
    `description` = VALUES(`description`),
    `category` = VALUES(`category`),
    `content_text` = VALUES(`content_text`),
    `is_favorite` = VALUES(`is_favorite`),
    `color_label` = VALUES(`color_label`),
    `ai_metadata` = VALUES(`ai_metadata`),
    `updated_at` = NOW();


-- ------------------------------------------------------------------------------
-- 9. Tags and Associations
-- ------------------------------------------------------------------------------

INSERT INTO `tags` (`id`, `user_id`, `name`, `color`)
VALUES
    (1, 1, 'Wishlist', '#721D38'),
    (2, 1, 'Dinner', '#4C5D38'),
    (3, 1, 'Inspiration', '#B07D38'),
    (4, 1, 'Shopping', '#721D38')
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `color` = VALUES(`color`);


INSERT INTO `item_tags` (`non_dated_item_id`, `tag_id`)
VALUES
    (1, 1),
    (1, 4),
    (2, 2),
    (5, 3)
ON DUPLICATE KEY UPDATE
    `tag_id` = VALUES(`tag_id`);


INSERT INTO `dated_item_tags` (`dated_item_id`, `tag_id`)
VALUES
    (1, 3),
    (3, 3)
ON DUPLICATE KEY UPDATE
    `tag_id` = VALUES(`tag_id`);


-- ------------------------------------------------------------------------------
-- 10. Reminders
-- ------------------------------------------------------------------------------

INSERT INTO `reminders` (
    `id`, `user_id`, `dated_item_id`, `title`, `note`,
    `trigger_at`, `snooze_count`, `status`, `snoozed_until`,
    `completed_at`, `created_at`, `updated_at`
)
VALUES
(
    1, 1, 1, 'Jazz at the Garden starts soon',
    'Check parking and bring your digital ticket.',
    DATE_ADD(NOW(), INTERVAL 69 HOUR),
    0, 'pending', NULL, NULL,
    NOW(), NOW()
),
(
    2, 1, 2, 'Museum late opening tomorrow',
    'Review this ticket before the event.',
    DATE_ADD(NOW(), INTERVAL 44 HOUR),
    0, 'pending', NULL, NULL,
    NOW(), NOW()
),
(
    3, 1, 3, 'Flight to Edinburgh tomorrow',
    'Check in online and prepare boarding pass.',
    DATE_ADD(NOW(), INTERVAL 38 HOUR),
    0, 'pending', NULL, NULL,
    NOW(), NOW()
),
(
    4, 1, 4, 'Ceramics workshop tomorrow',
    'Arrive 10 minutes early at Clay & Co Studio.',
    DATE_ADD(NOW(), INTERVAL 42 HOUR),
    0, 'pending', NULL, NULL,
    NOW(), NOW()
)
ON DUPLICATE KEY UPDATE
    `title` = VALUES(`title`),
    `note` = VALUES(`note`),
    `trigger_at` = VALUES(`trigger_at`),
    `status` = VALUES(`status`),
    `updated_at` = NOW();


-- ------------------------------------------------------------------------------
-- 11. Notifications
-- ------------------------------------------------------------------------------

INSERT INTO `notifications` (
    `id`, `user_id`, `type`, `title`, `body`, `data`,
    `is_read`, `read_at`, `created_at`
)
VALUES
(
    1, 1, 'cleanup_recommended',
    'A little space is waiting',
    'Several older screenshots are ready for review. You could free storage space by reviewing duplicates and expired items.',
    JSON_OBJECT(
        'eligible_count', 3,
        'reclaimable_mb', 9.2,
        'target_screen', 'CleanupReview'
    ),
    0, NULL, DATE_SUB(NOW(), INTERVAL 1 HOUR)
),
(
    2, 1, 'screenshot_classified',
    'New screenshots organized',
    'SnapTask classified your recent screenshots into dated and non-dated collections.',
    JSON_OBJECT(
        'dated_count', 4,
        'non_dated_count', 5,
        'target_screen', 'HomeDashboard'
    ),
    0, NULL, DATE_SUB(NOW(), INTERVAL 3 HOUR)
),
(
    3, 1, 'reminder_due',
    'Upcoming events this week',
    'You have 4 upcoming dated items. Open Reminders to review them.',
    JSON_OBJECT(
        'reminder_count', 4,
        'target_screen', 'Reminders'
    ),
    1, NOW(), DATE_SUB(NOW(), INTERVAL 1 DAY)
)
ON DUPLICATE KEY UPDATE
    `title` = VALUES(`title`),
    `body` = VALUES(`body`),
    `data` = VALUES(`data`),
    `is_read` = VALUES(`is_read`),
    `read_at` = VALUES(`read_at`);


-- ------------------------------------------------------------------------------
-- 12. Cleanup Job and History
-- ------------------------------------------------------------------------------

INSERT INTO `cleanup_jobs` (
    `id`, `user_id`, `status`, `mode`, `total_screenshots`,
    `space_reclaimed_bytes`, `started_at`, `completed_at`, `created_at`
)
VALUES
(
    1, 1, 'completed', 'manual', 3,
    9542040,
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    DATE_SUB(NOW(), INTERVAL 2 DAY)
)
ON DUPLICATE KEY UPDATE
    `status` = VALUES(`status`),
    `mode` = VALUES(`mode`),
    `total_screenshots` = VALUES(`total_screenshots`),
    `space_reclaimed_bytes` = VALUES(`space_reclaimed_bytes`),
    `started_at` = VALUES(`started_at`),
    `completed_at` = VALUES(`completed_at`);


INSERT INTO `cleanup_history` (
    `id`, `job_id`, `screenshot_id`, `original_title`,
    `file_size`, `deleted_at`
)
VALUES
    (1, 1, 7, 'Market Street receipt', 2411724, DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (2, 1, 8, 'Canvas trainers', 3145728, DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (3, 1, 9, 'Weekend cabin ideas', 3984588, DATE_SUB(NOW(), INTERVAL 2 DAY))
ON DUPLICATE KEY UPDATE
    `original_title` = VALUES(`original_title`),
    `file_size` = VALUES(`file_size`),
    `deleted_at` = VALUES(`deleted_at`);


-- ------------------------------------------------------------------------------
-- 13. Cleanup Recommendations
-- ------------------------------------------------------------------------------

INSERT INTO `cleanup_recommendations` (
    `id`, `user_id`, `screenshot_id`, `reason`, `recommended_action`,
    `risk_level`, `confidence`, `estimated_space_bytes`, `duplicate_of_id`,
    `is_resolved`, `created_at`
)
VALUES
(
    1, 1, 7, 'Receipt is older than the configured retention period.',
    'review', 'low', 0.870, 2411724, NULL, 0, NOW()
),
(
    2, 1, 8, 'Shopping screenshot has not been accessed recently.',
    'review', 'medium', 0.760, 3145728, NULL, 0, NOW()
),
(
    3, 1, 9, 'Inspiration screenshot is older than the retention threshold.',
    'review', 'low', 0.810, 3984588, NULL, 0, NOW()
)
ON DUPLICATE KEY UPDATE
    `reason` = VALUES(`reason`),
    `recommended_action` = VALUES(`recommended_action`),
    `risk_level` = VALUES(`risk_level`),
    `confidence` = VALUES(`confidence`),
    `estimated_space_bytes` = VALUES(`estimated_space_bytes`),
    `duplicate_of_id` = VALUES(`duplicate_of_id`),
    `is_resolved` = VALUES(`is_resolved`);


-- ------------------------------------------------------------------------------
-- 14. Trash Items
-- These represent screenshots previously cleaned up and available for restore.
-- ------------------------------------------------------------------------------

INSERT INTO `trash_items` (
    `id`, `user_id`, `screenshot_id`, `original_category`, `original_title`,
    `file_size`, `trashed_at`, `purge_due_at`, `is_restored`, `restored_at`,
    `created_at`
)
VALUES
    (1, 1, 7, 'non_dated', 'Market Street receipt', 2411724, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 28 DAY), 0, NULL, NOW()),
    (2, 1, 8, 'non_dated', 'Canvas trainers', 3145728, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 28 DAY), 0, NULL, NOW()),
    (3, 1, 9, 'non_dated', 'Weekend cabin ideas', 3984588, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 28 DAY), 0, NULL, NOW())
ON DUPLICATE KEY UPDATE
    `original_category` = VALUES(`original_category`),
    `original_title` = VALUES(`original_title`),
    `file_size` = VALUES(`file_size`),
    `trashed_at` = VALUES(`trashed_at`),
    `purge_due_at` = VALUES(`purge_due_at`),
    `is_restored` = VALUES(`is_restored`),
    `restored_at` = VALUES(`restored_at`);


-- ------------------------------------------------------------------------------
-- 15. AI Analysis Logs
-- ------------------------------------------------------------------------------

INSERT INTO `ai_analysis_logs` (
    `id`, `screenshot_id`, `engine`, `status`,
    `request_payload`, `response_payload`, `duration_ms`, `created_at`
)
VALUES
(
    1, 1, 'tesseract', 'success',
    JSON_OBJECT('image', 'jazz_garden_ticket.png', 'mode', 'full_analysis'),
    JSON_OBJECT(
        'category', 'dated',
        'confidence', 0.985,
        'extracted_date', DATE_ADD(CURDATE(), INTERVAL 3 DAY),
        'extracted_time', '19:30:00'
    ),
    842, NOW()
),
(
    2, 1, 'tesseract', 'success',
    JSON_OBJECT('image', 'pasta_recipe.png', 'mode', 'full_analysis'),
    JSON_OBJECT(
        'category', 'non_dated',
        'confidence', 0.965,
        'detected_type', 'recipe'
    ),
    731, NOW()
)
ON DUPLICATE KEY UPDATE
    `engine` = VALUES(`engine`),
    `status` = VALUES(`status`),
    `request_payload` = VALUES(`request_payload`),
    `response_payload` = VALUES(`response_payload`),
    `duration_ms` = VALUES(`duration_ms`);


-- ------------------------------------------------------------------------------
-- 16. Activity Logs
-- ------------------------------------------------------------------------------

INSERT INTO `activity_logs` (
    `id`, `user_id`, `action`, `entity_type`, `entity_id`, `metadata`, `created_at`
)
VALUES
    (1, 1, 'screenshot_uploaded', 'screenshot', 1, JSON_OBJECT('file_name', 'jazz_garden_ticket.png'), NOW()),
    (2, 1, 'screenshot_classified', 'screenshot', 1, JSON_OBJECT('category', 'dated'), NOW()),
    (3, 1, 'cleanup_completed', 'cleanup_job', 1, JSON_OBJECT('items_processed', 3), NOW()),
    (4, 1, 'item_moved_to_trash', 'screenshot', 7, JSON_OBJECT('reason', 'retention_rule'), NOW())
ON DUPLICATE KEY UPDATE
    `action` = VALUES(`action`),
    `entity_type` = VALUES(`entity_type`),
    `entity_id` = VALUES(`entity_id`),
    `metadata` = VALUES(`metadata`);


SET FOREIGN_KEY_CHECKS = 1;
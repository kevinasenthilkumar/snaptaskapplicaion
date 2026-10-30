-- Seeder: 002_sample_data_seeder.sql

-- 1. Sample Screenshots
INSERT INTO `screenshots` (
    `id`, `user_id`, `local_media_id`, `file_path`, `file_name`, `file_hash`, `perceptual_hash`,
    `mime_type`, `file_size`, `width`, `height`, `category`, `classification_confidence`,
    `ocr_text`, `extracted_title`, `extracted_description`, `extracted_date`, `extracted_time`,
    `extracted_location`, `extracted_amount`, `extracted_currency`, `source_app`, `status`, `is_kept`,
    `ai_metadata`, `captured_at`, `uploaded_at`
) VALUES
(
    1, 1, 'media_001', 'uploads/jazz_garden_ticket.png', 'jazz_garden_ticket.png',
    'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 'a4b2c8d1e3f60718',
    'image/png', 3145728, 1080, 2400, 'dated', 0.985,
    'THE GARDEN • LIVE JAZZ AT THE GARDEN FRI 16 OCT 2026 • 19:30 ADMIT ONE SECTION B ROW 4 SEAT 12',
    'Jazz at the Garden', 'Live jazz concert ticket at The Garden venue.', '2026-10-16', '19:30:00',
    'The Garden', 45.00, 'USD', 'Ticketmaster', 'classified', 0,
    JSON_OBJECT('venue', 'The Garden', 'gate', 'Main Gate', 'seat', 'B-4-12', 'qr_code_detected', true),
    '2026-10-14 11:20:00', '2026-10-14 11:25:00'
),
(
    2, 1, 'media_002', 'uploads/arc_lamp_shopping.png', 'arc_lamp_shopping.png',
    '5d41402abc4b2a76b9719d911017c592', 'b8c1d2e3f4051627',
    'image/png', 2621440, 1080, 2340, 'non_dated', 0.940,
    'FORM & FIELD Arc table lamp Matte black steel with brushed brass accent $129.00 In stock Add to bag',
    'Arc table lamp', 'Minimalist modern desk and table lamp wishlist item.', NULL, NULL,
    'Form & Field', 129.00, 'USD', 'Chrome', 'classified', 0,
    JSON_OBJECT('merchant', 'Form & Field', 'item_type', 'Furniture/Lighting', 'sku', 'FF-LAMP-BLK'),
    '2026-10-10 14:15:00', '2026-10-10 14:18:00'
),
(
    3, 1, 'media_003', 'uploads/pasta_recipe.png', 'pasta_recipe.png',
    '098f6bcd4621d373cade4e832627b4f6', 'c1d2e3f405162738',
    'image/png', 1887436, 1080, 2280, 'non_dated', 0.965,
    'Creamy Garlic Parmesan Pasta: 200g fettuccine, 3 cloves garlic, 1 cup heavy cream, 1/2 cup parmesan cheese, fresh parsley.',
    'Creamy Garlic Parmesan Pasta', 'Quick 15-minute skillet dinner recipe.', NULL, NULL,
    NULL, NULL, NULL, 'Instagram', 'classified', 1,
    JSON_OBJECT('prep_time', '15 mins', 'ingredients_count', 5, 'servings', 2),
    '2026-10-11 18:30:00', '2026-10-11 18:32:00'
),
(
    4, 1, 'media_004', 'uploads/museum_late_ticket.png', 'museum_late_ticket.png',
    '7d793037a0760186574b0282f2f435e7', 'd2e3f40516273849',
    'image/png', 3250585, 1080, 2400, 'dated', 0.970,
    'Contemporary Art Museum Late Opening entry ticket Thursday 15 October 2026 20:00 general admission',
    'Museum late opening', 'Night tour admission for museum exhibition.', '2026-10-15', '20:00:00',
    'Contemporary Art Museum', 22.50, 'USD', 'Eventbrite', 'classified', 0,
    JSON_OBJECT('event_status', 'passed'), '2026-10-08 09:12:00', '2026-10-08 09:15:00'
),
(
    5, 1, 'media_005', 'uploads/edinburgh_flight.png', 'edinburgh_flight.png',
    '3389dae361af79b04c9c8e7057f60cc6', 'e3f4051627384950',
    'image/png', 3407872, 1080, 2400, 'dated', 0.990,
    'Boarding Pass Flight BA1442 London Heathrow to Edinburgh EDI Depart 15 Oct 14:15 Gate 14 Seat 18C',
    'Flight to Edinburgh', 'Airline boarding pass for Scotland weekend trip.', '2026-10-15', '14:15:00',
    'Heathrow Airport', 115.00, 'GBP', 'British Airways', 'classified', 0,
    JSON_OBJECT('flight_number', 'BA1442', 'seat', '18C'), '2026-10-05 16:40:00', '2026-10-05 16:45:00'
),
(
    6, 1, 'media_006', 'uploads/ceramics_workshop.png', 'ceramics_workshop.png',
    '6079c6e52a543130dcf5188367048802', 'f405162738495061',
    'image/png', 2883584, 1080, 2340, 'dated', 0.930,
    'Clay & Co Ceramics Wheel Throwing Workshop Thursday 15 Oct 18:00 Studio 3',
    'Ceramics workshop', 'Hands-on pottery workshop reservation.', '2026-10-15', '18:00:00',
    'Clay & Co Studio', 60.00, 'USD', 'Mindbody', 'classified', 0,
    JSON_OBJECT('instructor', 'Sarah Jenkins'), '2026-10-09 11:00:00', '2026-10-09 11:05:00'
),
(
    7, 1, 'media_007', 'uploads/market_street_receipt.png', 'market_street_receipt.png',
    '25f9e794323b453885f5181f1b624d0b', '0516273849506172',
    'image/png', 2411724, 1080, 2300, 'non_dated', 0.920,
    'Market Street Organic Grocers 9 Oct Total $38.40 Card payment approved',
    'Market Street receipt', 'Grocery store purchase receipt.', NULL, NULL,
    'Market Street Grocers', 38.40, 'USD', 'Camera', 'classified', 0,
    JSON_OBJECT('store_id', 'MS-104'), '2026-10-09 17:22:00', '2026-10-09 17:25:00'
),
(
    8, 1, 'media_008', 'uploads/canvas_trainers.png', 'canvas_trainers.png',
    '182be0c5cdcd5072bb1864cdee4d3d6e', '1627384950617283',
    'image/png', 3145728, 1080, 2400, 'non_dated', 0.890,
    'Vintage Canvas Low Trainers Natural Off-White Unisex Size 42 £75.00',
    'Canvas trainers', 'Sneakers product screenshot for wardrobe update.', NULL, NULL,
    'End Clothing', 75.00, 'GBP', 'Safari', 'classified', 0,
    JSON_OBJECT('color', 'Off-White', 'brand', 'Novesta'), '2026-10-09 12:10:00', '2026-10-09 12:14:00'
),
(
    9, 1, 'media_009', 'uploads/cabin_ideas.png', 'cabin_ideas.png',
    '3858f62230ac3c915f300c664312c63f', '2738495061728394',
    'image/png', 3984588, 1080, 2400, 'non_dated', 0.910,
    'A-Frame Forest Cabin with cedar hot tub and mountain views Pacific Northwest',
    'Weekend cabin ideas', 'Vacation architectural inspiration and getaway bookmark.', NULL, NULL,
    'Mount Rainier', NULL, NULL, 'Pinterest', 'classified', 0,
    JSON_OBJECT('location', 'PNW', 'type', 'Cabin'), '2026-10-09 19:40:00', '2026-10-09 19:45:00'
)
ON DUPLICATE KEY UPDATE `extracted_title` = VALUES(`extracted_title`);

-- 2. Sample Dated Items
INSERT INTO `dated_items` (
    `id`, `user_id`, `screenshot_id`, `title`, `description`, `category`,
    `event_date`, `event_time`, `end_date`, `location`, `amount`, `currency`,
    `priority`, `status`, `reminder_enabled`, `reminder_at`, `created_at`, `updated_at`
) VALUES
(
    1, 1, 1, 'Jazz at the Garden', 'Live jazz concert ticket at The Garden venue.', 'ticket',
    '2026-10-16', '19:30:00', '2026-10-16', 'The Garden', 45.00, 'USD',
    'high', 'active', 1, '2026-10-16 09:00:00', NOW(), NOW()
),
(
    2, 1, 4, 'Museum late opening', 'Night tour admission for museum exhibition.', 'ticket',
    '2026-10-15', '20:00:00', '2026-10-15', 'Contemporary Art Museum', 22.50, 'USD',
    'medium', 'completed', 1, '2026-10-15 09:00:00', NOW(), NOW()
),
(
    3, 1, 5, 'Flight to Edinburgh', 'Airline boarding pass for Scotland weekend trip.', 'travel',
    '2026-10-15', '14:15:00', '2026-10-15', 'Heathrow Airport', 115.00, 'GBP',
    'high', 'completed', 1, '2026-10-15 09:00:00', NOW(), NOW()
),
(
    4, 1, 6, 'Ceramics workshop', 'Hands-on pottery workshop reservation.', 'booking',
    '2026-10-15', '18:00:00', '2026-10-15', 'Clay & Co Studio', 60.00, 'USD',
    'medium', 'completed', 1, '2026-10-15 09:00:00', NOW(), NOW()
)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 3. Sample Non-Dated Items
INSERT INTO `non_dated_items` (
    `id`, `user_id`, `screenshot_id`, `title`, `description`, `category`,
    `content_text`, `is_favorite`, `color_label`, `ai_metadata`, `created_at`, `updated_at`
) VALUES
(
    1, 1, 2, 'Arc table lamp', 'Minimalist modern desk and table lamp wishlist item.', 'shopping',
    'FORM & FIELD Arc table lamp Matte black steel with brushed brass accent $129.00 In stock',
    1, '#EDE7DE', JSON_OBJECT('lead_time_days', 7, 'delete_date', '2026-10-17'), NOW(), NOW()
),
(
    2, 1, 3, 'Creamy Garlic Parmesan Pasta', 'Quick 15-minute skillet dinner recipe.', 'recipe',
    '200g fettuccine, 3 cloves garlic, 1 cup heavy cream, 1/2 cup parmesan cheese, fresh parsley.',
    1, '#E9EBD9', JSON_OBJECT('cuisine', 'Italian', 'difficulty', 'easy'), NOW(), NOW()
),
(
    3, 1, 7, 'Market Street receipt', 'Grocery store purchase receipt.', 'receipt',
    'Market Street Organic Grocers 9 Oct Total $38.40 Card payment approved',
    0, '#F8EDF0', JSON_OBJECT('lead_time_days', 7, 'delete_date', '2026-10-16'), NOW(), NOW()
),
(
    4, 1, 8, 'Canvas trainers', 'Sneakers product screenshot for wardrobe update.', 'shopping',
    'Vintage Canvas Low Trainers Natural Off-White Unisex Size 42 £75.00',
    0, '#FAF6F0', JSON_OBJECT('lead_time_days', 7, 'delete_date', '2026-10-16'), NOW(), NOW()
),
(
    5, 1, 9, 'Weekend cabin ideas', 'Vacation architectural inspiration and getaway bookmark.', 'idea',
    'A-Frame Forest Cabin with cedar hot tub and mountain views Pacific Northwest',
    0, '#EDE7DE', JSON_OBJECT('lead_time_days', 7, 'delete_date', '2026-10-16'), NOW(), NOW()
)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 4. Sample Tags & Tag Associations
INSERT INTO `tags` (`id`, `user_id`, `name`, `color`) VALUES
(1, 1, 'Wishlist', '#721D38'),
(2, 1, 'Dinner', '#4C5D38'),
(3, 1, 'Inspiration', '#B07D38'),
(4, 1, 'Shopping', '#721D38')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `item_tags` (`non_dated_item_id`, `tag_id`) VALUES
(1, 1), (1, 4), (2, 2), (5, 3)
ON DUPLICATE KEY UPDATE `tag_id` = VALUES(`tag_id`);

-- 5. Sample Reminders
INSERT INTO `reminders` (
    `id`, `user_id`, `dated_item_id`, `title`, `note`, `trigger_at`, `status`, `created_at`, `updated_at`
) VALUES
(
    1, 1, 1, 'Jazz at the Garden starts in 3 hours', 'Check parking and bring digital ticket.',
    '2026-10-16 16:30:00', 'pending', NOW(), NOW()
),
(
    2, 1, 2, 'Museum late opening tonight', 'Event date notification one day before deletion.',
    '2026-10-15 09:00:00', 'completed', NOW(), NOW()
)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 6. Sample Notifications
INSERT INTO `notifications` (`id`, `user_id`, `type`, `title`, `body`, `data`, `is_read`, `created_at`)
VALUES
(
    1, 1, 'item_expired',
    'Two screenshots expire tomorrow',
    'Jazz at the Garden and Arc table lamp are eligible for deletion on 17 Oct. Keep them if you need more time.',
    JSON_OBJECT('screenshot_ids', JSON_ARRAY(1, 2), 'target_screen', 'CleanupReview'),
    0, '2026-10-16 09:00:00'
),
(
    2, 1, 'cleanup_recommended',
    'A little space is waiting',
    '6 older screenshots are ready for review. You could free 18.4 MB. Automatic cleanup is off.',
    JSON_OBJECT('eligible_count', 6, 'reclaimable_mb', 18.4, 'target_screen', 'CleanupReview'),
    0, '2026-10-16 08:45:00'
)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 7. Sample Trash Items (Matching 6 items in RecentlyDeletedScreen.kt)
INSERT INTO `trash_items` (
    `id`, `user_id`, `screenshot_id`, `original_category`, `original_title`,
    `file_size`, `trashed_at`, `purge_due_at`, `is_restored`, `created_at`
) VALUES
(1, 1, 4, 'dated', 'Museum late opening', 3250585, '2026-10-16 08:00:00', '2026-11-15 08:00:00', 0, NOW()),
(2, 1, 5, 'dated', 'Flight to Edinburgh', 3407872, '2026-10-16 08:00:00', '2026-11-15 08:00:00', 0, NOW()),
(3, 1, 6, 'dated', 'Ceramics workshop', 2883584, '2026-10-16 08:00:00', '2026-11-15 08:00:00', 0, NOW()),
(4, 1, 7, 'non_dated', 'Market Street receipt', 2411724, '2026-10-16 08:00:00', '2026-11-15 08:00:00', 0, NOW()),
(5, 1, 8, 'non_dated', 'Canvas trainers', 3145728, '2026-10-16 08:00:00', '2026-11-15 08:00:00', 0, NOW()),
(6, 1, 9, 'non_dated', 'Weekend cabin ideas', 3984588, '2026-10-16 08:00:00', '2026-11-15 08:00:00', 0, NOW())
ON DUPLICATE KEY UPDATE `original_title` = VALUES(`original_title`);


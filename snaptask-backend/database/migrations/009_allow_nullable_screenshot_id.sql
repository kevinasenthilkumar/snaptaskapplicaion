-- Migration: 009_allow_nullable_screenshot_id.sql
-- Allow dated_items and non_dated_items to be created without a required foreign screenshot row

ALTER TABLE dated_items DROP FOREIGN KEY k_dated_items_screenshot;
ALTER TABLE dated_items MODIFY screenshot_id BIGINT UNSIGNED NULL;
ALTER TABLE dated_items ADD CONSTRAINT k_dated_items_screenshot 
    FOREIGN KEY (screenshot_id) REFERENCES screenshots (id) ON DELETE CASCADE;

ALTER TABLE 
on_dated_items DROP FOREIGN KEY k_non_dated_items_screenshot;
ALTER TABLE 
on_dated_items MODIFY screenshot_id BIGINT UNSIGNED NULL;
ALTER TABLE 
on_dated_items ADD CONSTRAINT k_non_dated_items_screenshot 
    FOREIGN KEY (screenshot_id) REFERENCES screenshots (id) ON DELETE CASCADE;
from datetime import datetime, timedelta
from app.services.cleanup_service import CleanupService

def test_cleanup_dated_item_past_date_recommended_review():
    past_date = (datetime.now() - timedelta(days=2)).strftime('%Y-%m-%d')
    result = CleanupService.evaluate_cleanup(
        screenshot_id=1,
        category="dated",
        event_date=past_date,
        file_size=3145728
    )
    assert result["recommended_action"] == "review"
    assert result["risk_level"] == "low"
    assert result["estimated_space_bytes"] == 3145728

def test_cleanup_dated_item_future_date_recommended_keep():
    future_date = (datetime.now() + timedelta(days=5)).strftime('%Y-%m-%d')
    result = CleanupService.evaluate_cleanup(
        screenshot_id=2,
        category="dated",
        event_date=future_date,
        file_size=2000000
    )
    assert result["recommended_action"] == "keep"
    assert result["risk_level"] == "high"

def test_cleanup_duplicate_detection():
    sample_hash = "a4b2c8d1e3f60718"
    result = CleanupService.evaluate_cleanup(
        screenshot_id=3,
        category="non_dated",
        file_size=1500000,
        perceptual_hash=sample_hash,
        existing_hashes=[sample_hash]
    )
    assert result["recommended_action"] == "delete"
    assert result["is_duplicate"] is True
    assert result["risk_level"] == "low"


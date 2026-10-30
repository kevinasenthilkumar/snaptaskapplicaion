from app.services.date_extraction_service import DateExtractionService

def test_extract_explicit_date():
    text = "THE GARDEN • LIVE JAZZ AT THE GARDEN FRI 16 OCT 2026 • 19:30 ADMIT ONE"
    result = DateExtractionService.extract_dates(text)
    assert result["primary_date"] == "2026-10-16"
    assert result["primary_time"] == "19:30:00"
    assert result["confidence"] > 0.90

def test_extract_iso_date():
    text = "Invoice #INV-2041 Payment Due: 2026-11-05 Total: $450.00"
    result = DateExtractionService.extract_dates(text)
    assert result["primary_date"] == "2026-11-05"

def test_extract_relative_date():
    text = "Doctor appointment tomorrow at 14:00"
    result = DateExtractionService.extract_dates(text)
    assert result["primary_date"] is not None
    assert result["primary_time"] == "14:00:00"


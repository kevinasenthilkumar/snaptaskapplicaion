from app.utils.text_utils import clean_ocr_text, extract_monetary_amount, extract_prominent_title

def test_clean_ocr_text_removes_excessive_whitespace():
    raw = "THE  GARDEN  \t\t • LIVE\n\n\n\nJAZZ AT THE GARDEN"
    cleaned = clean_ocr_text(raw)
    assert "THE GARDEN • LIVE" in cleaned
    assert "JAZZ AT THE GARDEN" in cleaned
    assert "\t" not in cleaned

def test_extract_monetary_amount():
    text = "Form & Field Arc Table Lamp $129.00 In stock"
    amt, curr = extract_monetary_amount(text)
    assert amt == 129.00
    assert curr == "USD"

    text_gbp = "Vintage Canvas Trainers £75.00"
    amt2, curr2 = extract_monetary_amount(text_gbp)
    assert amt2 == 75.00
    assert curr2 == "GBP"

def test_extract_prominent_title():
    text = "5G 100%\nJAZZ AT THE GARDEN\nFRI 16 OCT 2026\nAdmit One"
    title = extract_prominent_title(text)
    assert title == "JAZZ AT THE GARDEN"


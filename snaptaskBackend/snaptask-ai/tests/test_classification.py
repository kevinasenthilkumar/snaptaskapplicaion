from app.services.classification_service import ClassificationService

def test_classify_concert_ticket_as_dated():
    text = "THE GARDEN • LIVE JAZZ AT THE GARDEN FRI 16 OCT 2026 • 19:30 ADMIT ONE SECTION B ROW 4 SEAT 12"
    result = ClassificationService.classify(text, has_detected_date=True)
    assert result["category"] == "dated"
    assert result["sub_category"] == "ticket"
    assert result["confidence"] >= 0.85

def test_classify_recipe_as_non_dated():
    text = "Creamy Garlic Parmesan Pasta: 200g fettuccine, 3 cloves garlic, 1 cup heavy cream, simmer for 10 minutes"
    result = ClassificationService.classify(text, has_detected_date=False)
    assert result["category"] == "non_dated"
    assert result["sub_category"] == "recipe"
    assert result["confidence"] >= 0.80

def test_classify_shopping_as_non_dated():
    text = "Arc table lamp Matte black steel $129.00 In stock Add to cart SKU FF-LAMP-BLK"
    result = ClassificationService.classify(text, has_detected_date=False)
    assert result["category"] == "non_dated"
    assert result["sub_category"] == "shopping"


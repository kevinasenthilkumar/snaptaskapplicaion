import re
from typing import Dict, Any, List

DATED_KEYWORDS = {
    'ticket': ['admit', 'ticket', 'section', 'row', 'seat', 'venue', 'barcode', 'qr code', 'admission', 'gate', 'show', 'concert', 'festival'],
    'travel': ['flight', 'boarding pass', 'airline', 'terminal', 'depart', 'arrive', 'train', 'pnr', 'gate', 'seat', 'booking ref'],
    'booking': ['reservation', 'booking', 'table', 'workshop', 'studio', 'appointment', 'confirmed', 'dr.', 'clinic'],
    'bill': ['invoice', 'due date', 'payment due', 'bill', 'statement', 'balance due', 'pay by', 'amount due'],
    'event': ['live', 'exhibition', 'conference', 'webinar', 'meetup', 'rsvp', 'doors open', 'starts at'],
}

NON_DATED_KEYWORDS = {
    'recipe': ['ingredients', 'recipe', 'tsp', 'tbsp', 'cup', 'skillet', 'saucepan', 'simmer', 'bake', 'boil', 'cook', 'servings', 'prep time'],
    'shopping': ['in stock', 'add to cart', 'add to bag', 'checkout', 'price', 'buy now', 'size', 'reviews', 'sku', 'product'],
    'receipt': ['receipt', 'subtotal', 'tax', 'cash', 'change', 'card payment', 'total', 'terminal', 'store #'],
    'design': ['interior', 'lamp', 'palette', 'minimalist', 'aesthetic', 'typography', 'architecture', 'decor', 'layout'],
    'article': ['read time', 'published', 'author', 'blog', 'medium', 'substack', 'newsletter', 'headline'],
    'idea': ['notes', 'wishlist', 'quote', 'draft', 'thought', 'bookmark', 'inspiration'],
}

class ClassificationService:
    @staticmethod
    def classify(ocr_text: str, has_detected_date: bool = False) -> Dict[str, Any]:
        """Classify screenshot into dated, non_dated, or unclassified with sub-categories."""
        text_lower = ocr_text.lower()
        matched_dated: List[str] = []
        matched_non_dated: List[str] = []

        dated_sub_scores: Dict[str, int] = {}
        for sub_cat, keywords in DATED_KEYWORDS.items():
            score = sum(1 for kw in keywords if re.search(r'\b' + re.escape(kw) + r'\b', text_lower))
            if score > 0:
                dated_sub_scores[sub_cat] = score
                matched_dated.extend([kw for kw in keywords if re.search(r'\b' + re.escape(kw) + r'\b', text_lower)])

        non_dated_sub_scores: Dict[str, int] = {}
        for sub_cat, keywords in NON_DATED_KEYWORDS.items():
            score = sum(1 for kw in keywords if re.search(r'\b' + re.escape(kw) + r'\b', text_lower))
            if score > 0:
                non_dated_sub_scores[sub_cat] = score
                matched_non_dated.extend([kw for kw in keywords if re.search(r'\b' + re.escape(kw) + r'\b', text_lower)])

        total_dated_score = sum(dated_sub_scores.values()) + (3 if has_detected_date else 0)
        total_non_dated_score = sum(non_dated_sub_scores.values())

        reasons = []

        if total_dated_score > total_non_dated_score and total_dated_score >= 2:
            category = "dated"
            best_sub = max(dated_sub_scores, key=dated_sub_scores.get) if dated_sub_scores else "event"
            confidence = min(0.99, 0.65 + (total_dated_score * 0.08))
            reasons.append(f"Detected time-sensitive indicators: {', '.join(matched_dated[:4])}")
            if has_detected_date:
                reasons.append("Identified calendar event date/time in image text.")
            return {
                "category": category,
                "sub_category": best_sub,
                "confidence": round(confidence, 3),
                "matched_keywords": matched_dated,
                "reasons": reasons,
            }

        elif total_non_dated_score > 0:
            category = "non_dated"
            best_sub = max(non_dated_sub_scores, key=non_dated_sub_scores.get) if non_dated_sub_scores else "idea"
            confidence = min(0.98, 0.60 + (total_non_dated_score * 0.08))
            reasons.append(f"Detected reference indicators: {', '.join(matched_non_dated[:4])}")
            return {
                "category": category,
                "sub_category": best_sub,
                "confidence": round(confidence, 3),
                "matched_keywords": matched_non_dated,
                "reasons": reasons,
            }

        # Ambiguous / Low text
        if has_detected_date:
            return {
                "category": "dated",
                "sub_category": "event",
                "confidence": 0.75,
                "matched_keywords": ["calendar_date"],
                "reasons": ["Calendar date detected without strong reference keywords."],
            }

        return {
            "category": "unclassified",
            "sub_category": "other",
            "confidence": 0.50,
            "matched_keywords": [],
            "reasons": ["Insufficient text or keywords to determine category with high confidence."],
        }


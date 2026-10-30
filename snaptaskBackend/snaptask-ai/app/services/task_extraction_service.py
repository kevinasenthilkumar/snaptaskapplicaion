import re
from typing import Dict, Any, Optional
from app.utils.text_utils import extract_monetary_amount, extract_prominent_title

VENUE_PATTERNS = [
    r'(?:at|venue|location|studio|airport|hall|theatre|theater|museum|stadium)\s+([A-Z][a-zA-Z0-9\s&]+(?:Garden|Airport|Hall|Museum|Stadium|Center|Centre|Studio|Hotel))',
    r'\b(The Garden|Heathrow Airport|Contemporary Art Museum|Clay & Co Studio|Market Street)\b',
]

class TaskExtractionService:
    @staticmethod
    def extract_task(ocr_text: str, category: str = "unclassified", detected_date: Optional[str] = None) -> Dict[str, Any]:
        """Extract task title, venue/location, monetary amount, and suggested reminders."""
        title = extract_prominent_title(ocr_text)
        amount, currency = extract_monetary_amount(ocr_text)

        # Detect location/venue
        location = None
        for pattern in VENUE_PATTERNS:
            m = re.search(pattern, ocr_text, re.IGNORECASE)
            if m:
                location = m.group(1).strip()
                break

        # Suggest reminder schedule
        suggested_reminder_date = detected_date
        suggested_reminder_time = "09:00:00"

        # Generate readable description
        description = None
        lines = [l.strip() for l in ocr_text.split('\n') if l.strip()]
        if len(lines) > 1:
            desc_candidates = [l for l in lines[1:4] if len(l) > 10 and not re.search(r'^\d+$', l)]
            if desc_candidates:
                description = " · ".join(desc_candidates[:2])

        confidence = 0.85
        if amount is not None:
            confidence += 0.05
        if location is not None:
            confidence += 0.05

        return {
            "title": title,
            "description": description,
            "amount": amount,
            "currency": currency or "USD",
            "location": location,
            "suggested_reminder_date": suggested_reminder_date,
            "suggested_reminder_time": suggested_reminder_time,
            "confidence": min(0.99, round(confidence, 2)),
        }


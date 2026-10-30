import re
from datetime import datetime, timedelta
from typing import Dict, Any, List, Optional

MONTH_MAP = {
    'jan': 1, 'january': 1,
    'feb': 2, 'february': 2,
    'mar': 3, 'march': 3,
    'apr': 4, 'april': 4,
    'may': 5,
    'jun': 6, 'june': 6,
    'jul': 7, 'july': 7,
    'aug': 8, 'august': 8,
    'sep': 9, 'september': 9, 'sept': 9,
    'oct': 10, 'october': 10,
    'nov': 11, 'november': 11,
    'dec': 12, 'december': 12,
}

class DateExtractionService:
    @staticmethod
    def extract_dates(text: str) -> Dict[str, Any]:
        """Extract explicit calendar dates, times, and relative dates from text."""
        dates: List[Dict[str, Any]] = []
        now = datetime.now()

        # 1. Match DD Mon YYYY or Mon DD YYYY (e.g., "16 Oct 2026", "October 16, 2026", "15 Oct")
        pattern_named = r'\b(\d{1,2})\s+([a-zA-Z]{3,9})(?:,?\s+(\d{4}))?\b|\b([a-zA-Z]{3,9})\s+(\d{1,2})(?:,?\s+(\d{4}))?\b'
        for match in re.finditer(pattern_named, text, re.IGNORECASE):
            raw = match.group(0)
            if match.group(1) and match.group(2):
                day = int(match.group(1))
                m_str = match.group(2).lower()
                year = int(match.group(3)) if match.group(3) else (now.year if now.month <= MONTH_MAP.get(m_str, 1) else now.year + 1)
            else:
                m_str = match.group(4).lower()
                day = int(match.group(5))
                year = int(match.group(6)) if match.group(6) else (now.year if now.month <= MONTH_MAP.get(m_str, 1) else now.year + 1)

            month = MONTH_MAP.get(m_str)
            if month and 1 <= day <= 31:
                try:
                    dt = datetime(year, month, day)
                    dates.append({
                        "date": dt.strftime('%Y-%m-%d'),
                        "raw_match": raw,
                        "confidence": 0.95,
                        "is_relative": False,
                    })
                except ValueError:
                    pass

        # 2. Match ISO YYYY-MM-DD
        pattern_iso = r'\b(20\d{2})-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])\b'
        for match in re.finditer(pattern_iso, text):
            raw = match.group(0)
            dates.append({
                "date": raw,
                "raw_match": raw,
                "confidence": 0.98,
                "is_relative": False,
            })

        # 3. Match Relative Dates ("tomorrow", "today", "in X days")
        if re.search(r'\btomorrow\b', text, re.IGNORECASE):
            d = (now + timedelta(days=1)).strftime('%Y-%m-%d')
            dates.append({
                "date": d,
                "raw_match": "tomorrow",
                "confidence": 0.90,
                "is_relative": True,
            })
        elif re.search(r'\btoday\b|\btonight\b', text, re.IGNORECASE):
            d = now.strftime('%Y-%m-%d')
            dates.append({
                "date": d,
                "raw_match": "today",
                "confidence": 0.88,
                "is_relative": True,
            })

        # 4. Extract Times (e.g. 19:30, 20:00, 7:30 PM, 14:15)
        pattern_time = r'\b([01]?\d|2[0-3]):([0-5]\d)(?::([0-5]\d))?(?:\s*(am|pm))?\b|\b(1[0-2]|0?[1-9])\s*(am|pm)\b'
        detected_time: Optional[str] = None
        for m in re.finditer(pattern_time, text, re.IGNORECASE):
            if m.group(1) and m.group(2):
                hour = int(m.group(1))
                minute = int(m.group(2))
                ampm = m.group(4)
                if ampm:
                    if ampm.lower() == 'pm' and hour < 12:
                        hour += 12
                    elif ampm.lower() == 'am' and hour == 12:
                        hour = 0
                detected_time = f"{hour:02d}:{minute:02d}:00"
                break
            elif m.group(5) and m.group(6):
                hour = int(m.group(5))
                ampm = m.group(6).lower()
                if ampm == 'pm' and hour < 12:
                    hour += 12
                elif ampm == 'am' and hour == 12:
                    hour = 0
                detected_time = f"{hour:02d}:00:00"
                break

        # Attach detected time to first date
        primary_date = dates[0]["date"] if dates else None
        primary_time = detected_time
        if dates and primary_time:
            dates[0]["time"] = primary_time

        return {
            "dates": dates,
            "primary_date": primary_date,
            "primary_time": primary_time,
            "confidence": dates[0]["confidence"] if dates else 0.0,
        }


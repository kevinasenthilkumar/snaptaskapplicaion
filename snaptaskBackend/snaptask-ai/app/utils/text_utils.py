import re
from typing import Optional, Tuple

CURRENCY_SYMBOLS = {
    "$": "USD",
    "£": "GBP",
    "€": "EUR",
    "₹": "INR",
    "¥": "JPY",
    "USD": "USD",
    "GBP": "GBP",
    "EUR": "EUR",
}

def clean_ocr_text(text: str) -> str:
    """Normalize whitespace and remove non-printable characters."""
    if not text:
        return ""
    text = re.sub(r'[\r\t]+', ' ', text)
    text = re.sub(r'\n{3,}', '\n\n', text)
    lines = [re.sub(r'\s+', ' ', line).strip() for line in text.split('\n')]
    return '\n'.join([line for line in lines if line])

def extract_monetary_amount(text: str) -> Tuple[Optional[float], Optional[str]]:
    """Extract price and currency symbol/code."""
    pattern = r'([$€£¥]|USD|GBP|EUR|INR)\s*([0-9]{1,4}(?:[,\.][0-9]{2,3})*(?:\.[0-9]{2})?)'
    match = re.search(pattern, text, re.IGNORECASE)
    if match:
        curr_raw = match.group(1).upper()
        amount_str = match.group(2).replace(',', '')
        try:
            amount = float(amount_str)
            currency = CURRENCY_SYMBOLS.get(curr_raw, "USD")
            return amount, currency
        except ValueError:
            pass

    # Reverse pattern e.g. 129.00 USD
    reverse_pattern = r'([0-9]{1,4}(?:[,\.][0-9]{2,3})*(?:\.[0-9]{2})?)\s*([$€£¥]|USD|GBP|EUR|INR)'
    match2 = re.search(reverse_pattern, text, re.IGNORECASE)
    if match2:
        amount_str = match2.group(1).replace(',', '')
        curr_raw = match2.group(2).upper()
        try:
            amount = float(amount_str)
            currency = CURRENCY_SYMBOLS.get(curr_raw, "USD")
            return amount, currency
        except ValueError:
            pass

    return None, None

def extract_prominent_title(text: str, fallback: str = "Screenshot Item") -> str:
    """Identify the most likely heading or title from the first non-empty lines."""
    lines = [l.strip() for l in text.split('\n') if l.strip()]
    if not lines:
        return fallback

    # Filter out common UI headers like status bar or battery
    filtered = []
    for line in lines[:5]:
        lower = line.lower()
        if any(skip in lower for skip in ['lte', '5g', 'wifi', '100%', 'pm', 'am', 'carrier']):
            continue
        filtered.append(line)

    if filtered:
        candidate = filtered[0]
        # Trim excessively long lines
        if len(candidate) > 70:
            candidate = candidate[:67] + '...'
        return candidate

    return lines[0][:70]


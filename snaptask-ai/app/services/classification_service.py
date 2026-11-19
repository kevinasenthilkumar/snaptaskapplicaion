import re
from typing import Dict, Any, List

CATEGORIES = {
    'Tickets': [
        'ticket', 'tickets', 'admit one', 'admit', 'seat', 'row', 'bookmyshow', 'pnr', 'coach',
        'cinema', 'multiplex', 'inox', 'pvr', 'movie', 'show time', 'screen', 'hall', 'venue',
        'match', 'stadium', 'entry pass', 'boarding', 'gate', 'concert', 'festival'
    ],
    'Travel': [
        'boarding pass', 'flight', 'check-in', 'booking id', 'irctc', 'makemytrip', 'goibibo',
        'yatra', 'airline', 'indigo', 'air india', 'spicejet', 'terminal', 'depart', 'arrive',
        'hotel booking', 'reservation', 'train', 'coach', 'berth', 'itinerary', 'trip'
    ],
    'Bills & Payments': [
        'amount due', 'paid', 'upi', 'transaction id', 'gpay', 'phonepe', 'paytm', 'bhim',
        'bank', 'debited', 'credited', 'bill', 'due date', 'statement', 'electricity bill',
        'recharge', 'payment successful', 'ref no', 'utr', 'imps', 'neft'
    ],
    'Receipts & Orders': [
        'invoice', 'order id', 'subtotal', 'gst', 'delivered', 'tax invoice', 'bill of supply',
        'order confirmed', 'tracking', 'shipment', 'courier', 'delivery partner', 'amazon order',
        'flipkart', 'receipt', 'payment receipt', 'cash on delivery'
    ],
    'Shopping': [
        'add to cart', '% off', '₹', 'free delivery', 'amazon', 'myntra', 'flipkart', 'ajio',
        'meesho', 'in stock', 'price', 'buy now', 'size', 'reviews', 'wishlist', 'discount',
        'coupon', 'sale', 'out of stock', 'cart'
    ],
    'Food & Recipes': [
        'ingredients', 'tbsp', 'tsp', 'serves', 'menu', 'zomato', 'swiggy', 'recipe', 'cuisine',
        'restaurant', 'prep time', 'cook time', 'dishes', 'order food', 'tasty', 'simmer',
        'bake', 'calorie', 'nutrition', 'servings'
    ],
    'Study & Notes': [
        'chapter', 'assignment', 'definition', 'syllabus', 'unit', 'lecture', 'slide', 'course',
        'module', 'exam', 'questions', 'formula', 'theorem', 'code', 'function', 'class',
        'algorithm', 'notes', 'homework', 'tutorial'
    ],
    'Chats & Social': [
        'typing…', 'typing...', 'seen', 'likes', 'followers', 'following', 'reply', 'whatsapp',
        'instagram', 'chat', 'message', 'dms', 'retweet', 'story', 'post', 'status', 'shared',
        'meme', 'comments'
    ],
    'Maps & Places': [
        'directions', 'min', 'km', 'google maps', 'open now', 'navigate', 'location', 'map',
        'traffic', 'fastest route', 'distance', 'address', 'landmark', 'hours', 'ratings'
    ]
}

class ClassificationService:
    @staticmethod
    def classify(ocr_text: str, has_detected_date: bool = False) -> Dict[str, Any]:
        """Classify screenshot into pure categories without dated/non-dated dichotomy."""
        text_lower = ocr_text.lower()
        matched_category_keywords: Dict[str, List[str]] = {}
        category_scores: Dict[str, int] = {}

        for cat_name, keywords in CATEGORIES.items():
            matched = [kw for kw in keywords if re.search(r'\b' + re.escape(kw) + r'\b', text_lower)]
            if matched:
                category_scores[cat_name] = len(matched)
                matched_category_keywords[cat_name] = matched

        if not category_scores:
            return {
                "category": "Other",
                "sub_category": "Other",
                "confidence": 0.50,
                "matched_keywords": [],
                "reasons": ["No specific category keywords found; classified as Other."],
            }

        # Select highest scoring category
        best_category = max(category_scores, key=category_scores.get)
        top_score = category_scores[best_category]
        matched_kws = matched_category_keywords[best_category]

        confidence = min(0.99, 0.65 + (top_score * 0.08))

        return {
            "category": best_category,
            "sub_category": best_category,
            "confidence": round(confidence, 3),
            "matched_keywords": matched_kws,
            "reasons": [f"Matched keywords: {', '.join(matched_kws[:4])}"],
        }
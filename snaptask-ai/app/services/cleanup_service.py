from datetime import datetime, timedelta
from typing import Dict, Any, List, Optional
from app.services.image_service import ImageService

class CleanupService:
    @staticmethod
    def evaluate_cleanup(
        screenshot_id: int,
        category: str,
        event_date: Optional[str] = None,
        captured_at: Optional[str] = None,
        file_size: int = 0,
        perceptual_hash: Optional[str] = None,
        existing_hashes: Optional[List[str]] = None,
    ) -> Dict[str, Any]:
        """Compute cleanup recommendation, risk level, and reasons without automatic deletion."""
        now = datetime.now()
        size_mb = round(file_size / (1024 * 1024), 2)

        # 1. Check duplicate detection via perceptual hash
        if perceptual_hash and existing_hashes:
            for h in existing_hashes:
                if ImageService.hamming_distance(perceptual_hash, h) <= 5:
                    return {
                        "recommended_action": "delete",
                        "reason": "Duplicate screenshot detected with matching perceptual hash.",
                        "risk_level": "low",
                        "confidence": 0.98,
                        "is_duplicate": True,
                        "estimated_space_bytes": file_size,
                        "estimated_space_mb": size_mb,
                    }

        # 2. Check Dated Item retention rule (Delete 1 day after event date)
        if category == "dated" and event_date:
            try:
                ev_dt = datetime.strptime(event_date[:10], "%Y-%m-%d")
                one_day_after = ev_dt + timedelta(days=1)
                if now >= one_day_after:
                    return {
                        "recommended_action": "review",
                        "reason": f"Event occurred on {event_date} (1 day post-event retention rule).",
                        "risk_level": "low",
                        "confidence": 0.95,
                        "is_duplicate": False,
                        "estimated_space_bytes": file_size,
                        "estimated_space_mb": size_mb,
                    }
                else:
                    return {
                        "recommended_action": "keep",
                        "reason": f"Active upcoming event on {event_date}.",
                        "risk_level": "high",
                        "confidence": 0.99,
                        "is_duplicate": False,
                        "estimated_space_bytes": 0,
                        "estimated_space_mb": 0.0,
                    }
            except ValueError:
                pass

        # 3. Check Non-Dated Item retention rule (Delete 7 days after capture)
        if category == "non_dated" and captured_at:
            try:
                cap_dt = datetime.strptime(captured_at[:10], "%Y-%m-%d")
                seven_days_after = cap_dt + timedelta(days=7)
                if now >= seven_days_after:
                    return {
                        "recommended_action": "review",
                        "reason": f"Saved on {captured_at[:10]}, over 7 days ago (non-dated retention rule).",
                        "risk_level": "low",
                        "confidence": 0.90,
                        "is_duplicate": False,
                        "estimated_space_bytes": file_size,
                        "estimated_space_mb": size_mb,
                    }
            except ValueError:
                pass

        return {
            "recommended_action": "keep",
            "reason": "Recent screenshot within retention window.",
            "risk_level": "medium",
            "confidence": 0.85,
            "is_duplicate": False,
            "estimated_space_bytes": 0,
            "estimated_space_mb": 0.0,
        }


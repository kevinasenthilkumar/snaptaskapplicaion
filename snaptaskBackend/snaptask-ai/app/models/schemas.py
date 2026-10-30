from typing import Any, Dict, List, Optional
from pydantic import BaseModel, Field

class BaseResponse(BaseModel):
    success: bool = True
    message: str = "Success"
    data: Optional[Dict[str, Any]] = None
    errors: List[str] = []

class HealthResponse(BaseModel):
    status: str = "ok"
    online: bool = True
    ocr_engine: str = "tesseract"
    version: str = "1.0.0"

class ClassifyRequest(BaseModel):
    ocr_text: str = Field(..., description="Raw or cleaned text from screenshot")

class ClassifyResponse(BaseModel):
    category: str = Field(..., description="dated | non_dated | unclassified")
    sub_category: str = Field("other", description="Specific sub-category e.g. ticket, shopping")
    confidence: float = Field(..., ge=0.0, le=1.0)
    matched_keywords: List[str] = []
    reasons: List[str] = []

class ExtractDatesRequest(BaseModel):
    ocr_text: str

class DetectedDate(BaseModel):
    date: str = Field(..., description="ISO 8601 YYYY-MM-DD")
    time: Optional[str] = Field(None, description="HH:MM:SS")
    raw_match: str
    confidence: float
    is_relative: bool = False

class ExtractDatesResponse(BaseModel):
    dates: List[DetectedDate] = []
    primary_date: Optional[str] = None
    primary_time: Optional[str] = None
    confidence: float = 0.0

class ExtractTaskRequest(BaseModel):
    ocr_text: str
    category: Optional[str] = "unclassified"

class ExtractTaskResponse(BaseModel):
    title: str
    description: Optional[str] = None
    amount: Optional[float] = None
    currency: Optional[str] = "USD"
    location: Optional[str] = None
    suggested_reminder_date: Optional[str] = None
    suggested_reminder_time: Optional[str] = "09:00:00"
    confidence: float = 0.0

class RecommendCleanupRequest(BaseModel):
    screenshot_id: int
    category: str = "dated"
    event_date: Optional[str] = None
    captured_at: Optional[str] = None
    file_size: int = 0
    perceptual_hash: Optional[str] = None
    existing_hashes: Optional[List[str]] = None

class RecommendCleanupResponse(BaseModel):
    recommended_action: str = Field("review", description="keep | review | delete")
    reason: str
    risk_level: str = Field("low", description="low | medium | high")
    confidence: float = 1.0
    is_duplicate: bool = False
    estimated_space_bytes: int = 0
    estimated_space_mb: float = 0.0

class FullAnalysisResponse(BaseModel):
    ocr_text: str
    ocr_confidence: float
    category: str
    sub_category: str
    classification_confidence: float
    extracted_title: str
    extracted_description: Optional[str] = None
    extracted_date: Optional[str] = None
    extracted_time: Optional[str] = None
    extracted_location: Optional[str] = None
    extracted_amount: Optional[float] = None
    extracted_currency: Optional[str] = "USD"
    file_hash: str
    perceptual_hash: str
    width: Optional[int] = None
    height: Optional[int] = None
    ai_metadata: Dict[str, Any] = {}


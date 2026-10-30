from fastapi import APIRouter, Depends, File, UploadFile, HTTPException, status
from app.core.security import verify_api_key
from app.models.schemas import (
    BaseResponse,
    ClassifyRequest,
    ClassifyResponse,
    ExtractDatesRequest,
    ExtractDatesResponse,
    ExtractTaskRequest,
    ExtractTaskResponse,
    RecommendCleanupRequest,
    RecommendCleanupResponse,
    FullAnalysisResponse,
)
from app.services.image_service import ImageService
from app.services.ocr_service import OcrService
from app.services.classification_service import ClassificationService
from app.services.date_extraction_service import DateExtractionService
from app.services.task_extraction_service import TaskExtractionService
from app.services.cleanup_service import CleanupService

router = APIRouter(prefix="/api/v1/ai", dependencies=[Depends(verify_api_key)])

@router.post("/analyze", response_model=BaseResponse)
async def analyze_screenshot(file: UploadFile = File(...)):
    """Full end-to-end pipeline: Preprocess ➔ OCR ➔ Date Extractor ➔ Classifier ➔ Task Extractor."""
    contents = await file.read()
    if not contents:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Uploaded file is empty.")

    try:
        # 1. Preprocess & Compute Hashes
        image, width, height = ImageService.load_and_preprocess(contents)
        file_hash = ImageService.compute_file_hash(contents)
        perceptual_hash = ImageService.compute_dhash(image)

        # 2. OCR Text Extraction
        ocr_ready_img = ImageService.prepare_for_ocr(image)
        ocr_res = OcrService.extract_text(ocr_ready_img)
        ocr_text = ocr_res["cleaned_text"]

        # 3. Date & Time Extraction
        dates_res = DateExtractionService.extract_dates(ocr_text)
        has_date = bool(dates_res.get("primary_date"))

        # 4. Classification (Dated vs Non-Dated)
        class_res = ClassificationService.classify(ocr_text, has_detected_date=has_date)

        # 5. Task & Action Extraction
        task_res = TaskExtractionService.extract_task(
            ocr_text,
            category=class_res["category"],
            detected_date=dates_res.get("primary_date")
        )

        analysis = FullAnalysisResponse(
            ocr_text=ocr_text,
            ocr_confidence=ocr_res["confidence"],
            category=class_res["category"],
            sub_category=class_res["sub_category"],
            classification_confidence=class_res["confidence"],
            extracted_title=task_res["title"],
            extracted_description=task_res["description"],
            extracted_date=dates_res.get("primary_date"),
            extracted_time=dates_res.get("primary_time"),
            extracted_location=task_res["location"],
            extracted_amount=task_res["amount"],
            extracted_currency=task_res["currency"],
            file_hash=file_hash,
            perceptual_hash=perceptual_hash,
            width=width,
            height=height,
            ai_metadata={
                "matched_keywords": class_res["matched_keywords"],
                "reasons": class_res["reasons"],
                "all_detected_dates": dates_res["dates"],
                "ocr_word_count": ocr_res["word_count"],
            }
        )

        return BaseResponse(
            success=True,
            message="Screenshot analysis complete",
            data=analysis.model_dump(),
        )

    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=f"Failed to process image: {str(e)}"
        )

@router.post("/classify", response_model=BaseResponse)
async def classify_text(req: ClassifyRequest):
    """Classify text into dated or non-dated category."""
    dates_res = DateExtractionService.extract_dates(req.ocr_text)
    has_date = bool(dates_res.get("primary_date"))
    result = ClassificationService.classify(req.ocr_text, has_detected_date=has_date)
    return BaseResponse(success=True, message="Classification complete", data=result)

@router.post("/extract-dates", response_model=BaseResponse)
async def extract_dates(req: ExtractDatesRequest):
    """Extract dates and times from text."""
    result = DateExtractionService.extract_dates(req.ocr_text)
    return BaseResponse(success=True, message="Dates extracted", data=result)

@router.post("/extract-task", response_model=BaseResponse)
async def extract_task(req: ExtractTaskRequest):
    """Extract actionable task entities (title, amount, location, suggested reminder)."""
    dates_res = DateExtractionService.extract_dates(req.ocr_text)
    result = TaskExtractionService.extract_task(
        req.ocr_text,
        category=req.category or "unclassified",
        detected_date=dates_res.get("primary_date")
    )
    return BaseResponse(success=True, message="Task extracted", data=result)

@router.post("/recommend-cleanup", response_model=BaseResponse)
async def recommend_cleanup(req: RecommendCleanupRequest):
    """Recommend cleanup action without automated deletion."""
    result = CleanupService.evaluate_cleanup(
        screenshot_id=req.screenshot_id,
        category=req.category,
        event_date=req.event_date,
        captured_at=req.captured_at,
        file_size=req.file_size,
        perceptual_hash=req.perceptual_hash,
        existing_hashes=req.existing_hashes,
    )
    return BaseResponse(success=True, message="Cleanup recommendation calculated", data=result)


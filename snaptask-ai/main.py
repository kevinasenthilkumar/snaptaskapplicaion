import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.routes_ai import router as ai_router
from app.core.config import settings
from app.models.schemas import HealthResponse

app = FastAPI(
    title="SnapTask AI Microservice",
    description="Intelligent OCR, entity extraction, screenshot classification, and cleanup recommendation engine.",
    version="1.0.0",
)

# Enable CORS for local cross-service communication
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/health", response_model=HealthResponse)
async def health_check():
    """Public health check endpoint."""
    return HealthResponse(
        status="ok",
        online=True,
        ocr_engine=settings.ocr_engine,
        version="1.0.0",
    )

app.include_router(ai_router)

if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host=settings.host,
        port=settings.port,
        reload=settings.debug,
    )


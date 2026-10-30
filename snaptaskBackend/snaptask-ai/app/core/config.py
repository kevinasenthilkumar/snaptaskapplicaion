import os
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    host: str = "0.0.0.0"
    port: int = 8001
    debug: bool = True
    api_key: str = "snaptask_internal_ai_secret_key_2026"

    ocr_engine: str = "tesseract"
    tesseract_cmd: str = "tesseract"
    ocr_confidence_threshold: float = 0.60
    ocr_languages: str = "eng"

    max_image_dimension: int = 2048
    dhash_similarity_threshold: int = 5

    class Config:
        env_file = ".env"
        extra = "ignore"

settings = Settings()


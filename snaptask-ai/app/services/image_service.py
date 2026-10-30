import hashlib
import io
from typing import Tuple
from PIL import Image, ImageEnhance, ImageOps
from app.core.config import settings

class ImageService:
    @staticmethod
    def load_and_preprocess(image_bytes: bytes) -> Tuple[Image.Image, int, int]:
        """Validate, convert to RGB, and resize if dimension exceeds limit."""
        image = Image.open(io.BytesIO(image_bytes))

        # Convert palette/transparency to RGB
        if image.mode in ("RGBA", "P"):
            image = image.convert("RGB")
        elif image.mode != "RGB":
            image = image.convert("RGB")

        # Auto-rotate based on EXIF
        image = ImageOps.exif_transpose(image) or image

        width, height = image.size
        max_dim = settings.max_image_dimension

        if max(width, height) > max_dim:
            image.thumbnail((max_dim, max_dim), Image.Resampling.LANCZOS)
            width, height = image.size

        return image, width, height

    @staticmethod
    def prepare_for_ocr(image: Image.Image) -> Image.Image:
        """Convert to grayscale and enhance contrast for improved OCR accuracy."""
        gray = image.convert("L")
        enhancer = ImageEnhance.Contrast(gray)
        enhanced = enhancer.enhance(1.8)
        return enhanced

    @staticmethod
    def compute_file_hash(image_bytes: bytes) -> str:
        """SHA-256 hash of the image bytes."""
        return hashlib.sha256(image_bytes).hexdigest()

    @staticmethod
    def compute_dhash(image: Image.Image) -> str:
        """Calculate 64-bit difference hash (dHash) for perceptual comparison."""
        resized = image.convert("L").resize((9, 8), Image.Resampling.BILINEAR)
        pixels = list(resized.getdata())

        difference = []
        for row in range(8):
            for col in range(8):
                idx = row * 9 + col
                pixel_left = pixels[idx]
                pixel_right = pixels[idx + 1]
                difference.append(pixel_left > pixel_right)

        # Convert 64 booleans to 16 hex chars
        decimal_val = 0
        hex_string = []
        for index, val in enumerate(difference):
            if val:
                decimal_val += 2 ** (index % 4)
            if (index % 4) == 3:
                hex_string.append(hex(decimal_val)[2:])
                decimal_val = 0

        return "".join(hex_string)

    @staticmethod
    def hamming_distance(hash1: str, hash2: str) -> int:
        """Calculate Hamming distance between two hex hashes."""
        try:
            n1 = int(hash1, 16)
            n2 = int(hash2, 16)
            x = n1 ^ n2
            dist = 0
            while x > 0:
                dist += 1
                x &= x - 1
            return dist
        except Exception:
            return 64


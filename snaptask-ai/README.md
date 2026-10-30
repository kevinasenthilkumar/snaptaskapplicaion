# SnapTask Python AI Microservice

Intelligent classification, OCR, entity extraction, and cleanup recommendation engine for **SnapTask**.

---

## 1. Core Capabilities

1. **Image Preprocessing & Perceptual Hashing**:
   * Validates image format and converts color profiles to standard RGB.
   * Resizes oversized screenshots (preserving aspect ratio) up to 2048px.
   * Generates SHA-256 binary hash and 64-bit difference hash (`dHash`) for screenshot deduplication.

2. **OCR Engine**:
   * Uses **Tesseract OCR** (`pytesseract`) as default local engine with contrast stretch and noise reduction.
   * Configurable via `.env` (`TESSERACT_CMD`, `OCR_LANGUAGES`).
   * Intelligent heuristic text fallback ensures API resilience even if Tesseract is not yet installed on host.

3. **Hybrid Classification (Dated vs Non-Dated)**:
   * Rule-based keyword matching across 11 subcategories (tickets, travel, bookings, bills, events, recipes, shopping, receipts, design, articles, ideas).
   * Context-aware calendar date boosting.
   * Produces confidence scores and explainable detection reasons.

4. **Date, Time & Entity Extractor**:
   * Extracts absolute dates (e.g. `16 Oct 2026`, `2026-10-16`), event times (`19:30`), and relative dates (`tomorrow`, `today`).
   * Extracts prices, currencies (`$`, `£`, `€`), and venue names (`The Garden`, `Heathrow Airport`).

5. **Cleanup Recommendation Engine**:
   * Evaluates retention rules (1 day post-event for dated, 7 days post-save for non-dated).
   * Flags perceptual duplicates (Hamming distance $\le 5$).
   * Computes risk ratings (`low`, `medium`, `high`) and estimated reclaimable storage without automatic permanent deletion.

---

## 2. Quick Start & Setup Instructions

### Step 1: Virtual Environment
```bash
cd snaptask-ai
python -m venv venv
```
Activate the virtual environment:
* **Windows PowerShell**: `.\venv\Scripts\Activate.ps1`
* **Windows Command Prompt**: `.\venv\Scripts\activate.bat`
* **Linux / macOS**: `source venv/bin/activate`

### Step 2: Install Dependencies
```bash
pip install -r requirements.txt
```

### Step 3: Configure Environment
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```

### Step 4: Generate Sample Images (Optional)
```bash
python samples/create_samples.py
```

### Step 5: Start the Microservice
```bash
python main.py
```
Or with Uvicorn directly:
```bash
uvicorn main:app --host 0.0.0.0 --port 8001 --reload
```
The service will be live at: `http://localhost:8001`
* Interactive OpenAPI Swagger docs: `http://localhost:8001/docs`
* Health check: `http://localhost:8001/health`

---

## 3. Running Tests
Run pytest to verify OCR normalization, date extraction, classification rules, and cleanup risk logic:
```bash
pytest -v
```


# SnapTask Android API Contracts & Endpoint Specification

Base URL: `http://localhost:8000/api/v1`

All responses follow the unified envelope:
```json
{
  "success": true,
  "message": "string",
  "data": {},
  "errors": [],
  "meta": {}
}
```

---

## 1. Authentication Endpoints

### 1.1 Register User
* **Method**: `POST /auth/register`
* **Request Body**:
  ```json
  {
    "name": "Alex Taylor",
    "email": "alex@snaptask.app",
    "password": "Password123!"
  }
  ```
* **Success Response (201)**:
  ```json
  {
    "success": true,
    "message": "User registered successfully",
    "data": {
      "user": {
        "id": 1,
        "email": "alex@snaptask.app",
        "name": "Alex Taylor",
        "avatar_url": null,
        "status": "active"
      },
      "access_token": "eyJhbGciOi...",
      "refresh_token": "eyJhbGciOi...",
      "expires_in": 3600
    }
  }
  ```

### 1.2 Login User
* **Method**: `POST /auth/login`
* **Request Body**:
  ```json
  {
    "email": "demo@snaptask.app",
    "password": "Password123!",
    "device_name": "Pixel 8 Pro"
  }
  ```
* **Success Response (200)**: Same as register.

### 1.3 Refresh Token
* **Method**: `POST /auth/refresh`
* **Request Body**:
  ```json
  {
    "refresh_token": "eyJhbGciOi..."
  }
  ```
* **Success Response (200)**:
  ```json
  {
    "success": true,
    "message": "Token refreshed successfully",
    "data": {
      "access_token": "eyJhbGciOi...",
      "refresh_token": "eyJhbGciOi...",
      "expires_in": 3600
    }
  }
  ```

---

## 2. Screenshot Endpoints

### 2.1 Upload Screenshot
* **Method**: `POST /screenshots/upload`
* **Headers**: `Content-Type: multipart/form-data`, `Authorization: Bearer <token>`
* **Form Fields**:
  * `file`: (binary PNG/JPEG)
  * `local_media_id`: (string, Android ContentProvider URI or ID)
  * `captured_at`: (string, ISO 8601 timestamp)
* **Success Response (201)**:
  ```json
  {
    "success": true,
    "message": "Screenshot uploaded successfully",
    "data": {
      "screenshot": {
        "id": 10,
        "local_media_id": "content://media/external/images/media/1042",
        "file_name": "snap_20261029_100000_a1b2c3d4.png",
        "file_url": "http://localhost:8000/storage/uploads/snap_20261029_100000_a1b2c3d4.png",
        "file_size": 3145728,
        "file_size_formatted": "3.0 MB",
        "category": "unclassified",
        "status": "pending"
      }
    }
  }
  ```

### 2.2 Trigger Screenshot Analysis
* **Method**: `POST /screenshots/{id}/analyze`
* **Success Response (200)**:
  ```json
  {
    "success": true,
    "message": "Screenshot analyzed successfully",
    "data": {
      "screenshot": {
        "id": 10,
        "category": "dated",
        "classification_confidence": 0.985,
        "extracted_title": "Jazz at the Garden",
        "extracted_date": "2026-10-16",
        "extracted_time": "19:30:00",
        "extracted_location": "The Garden",
        "extracted_amount": 45.00,
        "extracted_currency": "USD",
        "status": "classified",
        "is_kept": false
      }
    }
  }
  ```

---

## 3. Library & Detail Endpoints

### 3.1 Get Dated Screenshots
* **Method**: `GET /dated-items`
* **Query Params**: `?sort=date_asc&timeframe=this_week&page=1&limit=20`
* **Success Response (200)**:
  ```json
  {
    "success": true,
    "data": {
      "items": [
        {
          "id": 1,
          "screenshot_id": 1,
          "title": "Jazz at the Garden",
          "category": "ticket",
          "event_date": "2026-10-16",
          "event_time": "19:30:00",
          "location": "The Garden",
          "amount": 45.00,
          "currency": "USD",
          "priority": "high",
          "status": "active",
          "is_kept": false,
          "image_url": "http://localhost:8000/storage/uploads/jazz_garden_ticket.png"
        }
      ],
      "total": 42,
      "page": 1,
      "total_pages": 3
    }
  }
  ```

### 3.2 Get Non-Dated Screenshots
* **Method**: `GET /non-dated-items`
* **Query Params**: `?sort=oldest&category=shopping&page=1&limit=20`
* **Success Response (200)**:
  ```json
  {
    "success": true,
    "data": {
      "items": [
        {
          "id": 1,
          "screenshot_id": 2,
          "title": "Arc table lamp",
          "category": "shopping",
          "content_text": "FORM & FIELD Arc table lamp $129.00 In stock",
          "is_favorite": true,
          "color_label": "#EDE7DE",
          "is_kept": false,
          "image_url": "http://localhost:8000/storage/uploads/arc_lamp_shopping.png",
          "tags": [{ "id": 1, "name": "Wishlist", "color": "#721D38" }]
        }
      ],
      "total": 86,
      "page": 1,
      "total_pages": 5
    }
  }
  ```

---

## 4. Cleanup & Trash Endpoints

### 4.1 Get Cleanup Recommendations
* **Method**: `GET /cleanup/recommendations`
* **Success Response (200)**:
  ```json
  {
    "success": true,
    "data": {
      "total_eligible": 6,
      "total_bytes": 19293818,
      "total_mb": 18.4,
      "dated_items": [
        { "id": 4, "extracted_title": "Museum late opening", "file_size_formatted": "3.1 MB", "reason": "Past event date (1-day retention rule)" }
      ],
      "non_dated_items": [
        { "id": 7, "extracted_title": "Market Street receipt", "file_size_formatted": "2.3 MB", "reason": "Saved > 7 days ago (non-dated retention rule)" }
      ]
    }
  }
  ```

### 4.2 Confirm Cleanup (Delete to Trash)
* **Method**: `POST /cleanup/confirm`
* **Request Body**:
  ```json
  {
    "screenshot_ids": [4, 5, 6, 7, 8, 9],
    "mode": "manual"
  }
  ```
* **Success Response (200)**:
  ```json
  {
    "success": true,
    "message": "Cleanup executed successfully",
    "data": {
      "job_id": 1,
      "deleted_count": 6,
      "space_reclaimed_bytes": 19293818,
      "space_reclaimed_mb": 18.4
    }
  }
  ```

### 4.3 Get Trash (Recently Deleted)
* **Method**: `GET /trash`
* **Success Response (200)**:
  ```json
  {
    "success": true,
    "data": {
      "items": [
        {
          "id": 1,
          "screenshot_id": 4,
          "original_title": "Museum late opening",
          "original_category": "dated",
          "file_size_formatted": "3.1 MB",
          "days_left": 30,
          "image_url": "http://localhost:8000/storage/uploads/museum_late_ticket.png"
        }
      ],
      "total": 6,
      "total_mb": 18.4
    }
  }
  ```


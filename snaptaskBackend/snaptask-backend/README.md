# SnapTask REST API Backend

Production-ready backend foundation for **SnapTask** built with modern PHP 8.2+, PDO, JWT authentication, and clean layered architecture.

---

## 1. System Requirements

* **PHP**: `>= 8.2` (with PDO, pdo_mysql, curl, mbstring, gd or fileinfo extensions)
* **MySQL**: `>= 8.0`
* **Composer**: `>= 2.0` (optional, native PSR-4 autoloader fallback included)
* **Web Server**: Apache / Nginx / Laragon / XAMPP or PHP built-in CLI server

---

## 2. Directory Structure

```text
snaptask-backend/
├── app/
│   ├── Config/          # Database, App & AI microservice configurations
│   ├── Controllers/     # Thin HTTP controllers returning consistent JSON envelopes
│   ├── Core/            # Router, Request, Response, Database, JWT, Logger, ErrorHandler
│   ├── Helpers/         # Validator, FileUpload, HashHelper
│   ├── Middleware/      # CORS, JWT Bearer Auth, Sliding Window Rate Limiting
│   ├── Models/          # Typed Domain Models with JSON serialization
│   ├── Repositories/    # Clean PDO Prepared Statement Repositories
│   ├── Services/        # Business Logic & AI client communication
│   └── Validators/      # Input validation rule sets
├── database/
│   ├── migrations/      # 8 structured migration files
│   ├── seeders/         # Demo user and high-fidelity prototype sample data
│   ├── schema.sql       # Complete unified MySQL DDL schema
│   └── install.php      # CLI database creation, migration & seed runner
├── public/
│   └── index.php        # Single entry point
├── routes/
│   └── api.php          # REST API route definitions with middleware groups
├── storage/
│   ├── uploads/         # Uploaded screenshots
│   └── logs/            # Application logs
├── tests/               # Unit and integration test suites
├── .env.example         # Environment template
└── composer.json        # Dependencies & autoloading configuration
```

---

## 3. Quick Start & Setup Instructions

### Step 1: Environment Configuration
Copy `.env.example` to `.env` in the `snaptask-backend/` root:
```bash
cp .env.example .env
```
Ensure your MySQL connection credentials are set:
```ini
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=snaptask_db
DB_USERNAME=root
DB_PASSWORD=
```

### Step 2: Database Migration & Seeding
Run the standalone database installer CLI script:
```bash
php database/install.php
```
This command will:
1. Connect to MySQL.
2. Create database `snaptask_db` with `utf8mb4_unicode_ci` if it doesn't already exist.
3. Run all migrations in alphabetical order.
4. Populate seed data:
   * **Demo User**: `demo@snaptask.app` / Password: `Password123!`
   * Prototype screenshots (Jazz at the Garden, Arc table lamp, Pasta recipe, Edinburgh flight, etc.)
   * Retention rules, reminders, and notifications.

To do a fresh database reset:
```bash
php database/install.php --fresh
```

### Step 3: Install Dependencies (Optional)
If you have Composer installed:
```bash
composer install
```
*(Note: A built-in PSR-4 fallback autoloader is active in `public/index.php`, allowing the application to run immediately even without running composer install!)*

### Step 4: Start the Development Server
```bash
php -S 0.0.0.0:8000 -t public
```
The API is now live at: `http://localhost:8000/api/v1`

---

## 4. Running Tests

Run PHPUnit tests via Composer:
```bash
composer test
```
Or directly using PHPUnit:
```bash
vendor/bin/phpunit
```

---

## 5. API Response Format

### Success Envelope (HTTP 200, 201)
```json
{
  "success": true,
  "message": "Resource created successfully",
  "data": { ... },
  "errors": [],
  "meta": {
    "total": 42,
    "page": 1,
    "total_pages": 3
  }
}
```

### Error Envelope (HTTP 400, 401, 403, 404, 422, 429, 500)
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "email": ["The email must be a valid email address."]
  },
  "error_code": "UNPROCESSABLE_ENTITY"
}
```


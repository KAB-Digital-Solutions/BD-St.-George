# Saint George Member Management System

Web-based church administration platform for **Bahir Dar Felege Genet St. George Orthodox Church**.

This system replaces the old Microsoft Access database with a modern, multi-user application. Members, clergy, contributions, and all other records will be managed through a browser — with Amharic/English support planned for the UI.

**Current status:** Foundation is in place (database + login). Module-by-module development is ongoing.

---

## What you need installed

| Tool | Purpose |
|------|---------|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Runs PostgreSQL locally |
| JDK 21 | Runs the Spring Boot backend |
| Git | Version control |

Node.js and React will be needed later for the frontend.

---

## Project folders

```
BD-St.-George/
├── backend/              ← Spring Boot API (Java)
│   ├── Dockerfile        ← Builds the backend image for Docker
│   └── src/main/java/com/example/backend/
│       ├── auth/         ← Login, JWT, users (self-contained module)
│       └── shared/       ← Cross-cutting: errors, health, OpenAPI
├── docker-compose.yml    ← Starts PostgreSQL (daily dev)
├── docker-compose.full.yml ← Full stack later (DB + API + React)
├── docs/                 ← Technical plans and schema notes
└── .env.example          ← Copy to .env if you want custom passwords
```

### Backend code layout (modular)

Each feature gets its own package. Auth is the first module:

```
auth/
  controller/   → REST endpoints (/api/auth/...)
  service/      → Business logic
  repository/   → Database access
  entity/       → User table mapping
  dto/          → Request/response shapes
  security/     → JWT filter, user details
  config/       → Security + JWT settings
  domain/       → UserRole enum
  util/         → JwtUtil
```

`formengine/` — dynamic form engine (field definitions + custom values).

Future modules: `members/`, `clergy/`, etc.

`shared/` holds code used by every module (exception handler, health check, Swagger config).

---

## How to run the project (local development)

We run the **database in Docker** and the **backend on your machine** (IntelliJ or terminal). That is the normal workflow during development.

### Step 1 — Start the database

Open PowerShell in the **project root** (not inside `backend/`):

```powershell
cd C:\Users\arsem\Documents\GitHub\BD-St.-George
docker compose up -d
```

Check it is running:

```powershell
docker compose ps
```

You should see `saint-george-db` with status **healthy**.

**Database connection details:**

| Setting | Value |
|---------|--------|
| Host | `localhost` |
| Port | `5433` |
| Database | `saint_george_db` |
| Username | `sgadmin` |
| Password | `change_in_production` |

> Port **5433** is used on purpose. Many Windows PCs already have PostgreSQL on port 5432. Using 5433 avoids conflicts.

**If you see “container name already in use”:** the database is already running. Use `docker compose ps` — you do not need to start it again.

**To completely reset the database** (deletes all data):

```powershell
docker compose down -v
docker compose up -d
```

### Step 2 — Start the backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Wait until you see: `Started BackendApplication`

The API runs at: **http://localhost:8080**

### Step 3 — Check that everything works

**Health check (no login needed):**

http://localhost:8080/api/health

Expected response: `{"status":"ok","phase":"1-auth"}`

**Login (development admin account):**

| Field | Value |
|-------|--------|
| Username | `admin` |
| Password | `ChangeMe123!` |

Example with PowerShell:

```powershell
$body = '{"username":"admin","password":"ChangeMe123!"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $body
```

**API documentation (Swagger UI):**

http://localhost:8080/swagger-ui/index.html

No login is required to open Swagger. Use it to explore and test endpoints.

**Using your token in Swagger:**

1. Call `POST /api/auth/login` and copy the `token` from the response.
2. At the **top right** of the Swagger page, click the green **Authorize** button (lock icon).
3. In the `bearerAuth` box, paste **only the token** (do not type `Bearer ` — Swagger adds that).
4. Click **Authorize**, then **Close**.
5. Protected endpoints (e.g. `GET /api/auth/me`) will now send the token automatically.

If you do not see **Authorize**, restart the backend after pulling the latest code (OpenAPI security must be configured).

---

## Docker image for the backend

The file `backend/Dockerfile` packages the Spring Boot app into a container image. You do **not** need this for everyday coding — only for deployment or the full Docker stack.

Build the backend image manually:

```powershell
cd backend
docker build -t saint-george-backend .
```

Run the **full stack** (database + backend + frontend) when the frontend exists:

```powershell
cd C:\Users\arsem\Documents\GitHub\BD-St.-George
docker compose -f docker-compose.full.yml up --build
```

---

## Common problems

**“Password authentication failed for user sgadmin”**  
The app is probably connecting to the wrong PostgreSQL (often a local install on port 5432). Make sure Docker is running and `application.yml` uses port **5433**.

**Swagger shows 403 Forbidden**  
Restart the backend after pulling the latest code. Swagger must be allowed in security config and Springdoc must match your Spring Boot version (3.0.0 for Boot 4).

**Port 8080 already in use**  
Another app is using 8080. Stop it, or change `server.port` in `backend/src/main/resources/application.yml`.

---

## What is built so far

- PostgreSQL database in Docker
- User accounts table with roles: Admin, Recorder, Viewer
- JWT login: `POST /api/auth/login`
- Current user profile: `GET /api/auth/me`
- **Admin user management** (ADMIN token required):
  - `GET /api/admin/users` — list all users
  - `GET /api/admin/users/{id}` — get one user
  - `POST /api/admin/users` — create Recorder/Viewer/Admin account
  - `PUT /api/admin/users/{id}` — update name, role, or active status
  - `PUT /api/admin/users/{id}/password` — reset password

Example — create a Recorder for a church clerk (after Authorize as admin):

```json
{
  "username": "clerk1",
  "password": "SecurePass123!",
  "fullName": "Church Clerk",
  "role": "RECORDER"
}
```

## Dynamic form engine (formengine/)

**Field definitions (configure forms):**

| Method | Path | Who |
|--------|------|-----|
| GET | `/api/admin/form-fields/{moduleKey}` | Admin, Recorder, Viewer |
| POST | `/api/admin/form-fields` | Admin only |
| PUT | `/api/admin/form-fields/{id}` | Admin only |
| PATCH | `/api/admin/form-fields/{moduleKey}/reorder` | Admin only |
| PATCH | `/api/admin/form-fields/{id}/deactivate` | Admin only |
| PATCH | `/api/admin/form-fields/{id}/reactivate` | Admin only |

Valid `moduleKey` values: `members`, `clergy`, `baptisms`, `family_members`, `staff_ministers`, `office_staff`, `workers`, `emergency_contacts`, `parish_council`, `contributions`, `transfers`, `deceased`, `sunday_school`, `abnet_school`.

**Custom field values (per record):**

| Method | Path | Who |
|--------|------|-----|
| GET | `/api/{moduleKey}/{recordId}/custom-fields` | All logged-in users |
| POST/PUT | `/api/{moduleKey}/{recordId}/custom-fields` | Admin, Recorder |

Example — list member form fields:

- `GET /api/admin/form-fields/modules` — shows all valid module keys
- `GET /api/admin/form-fields/modules/members` — fields for the members module

In Swagger, open **GET /api/admin/form-fields/modules/{moduleKey}**, click **Try it out**, set `moduleKey` to `members`, then **Execute**.

Seeded fields include `baptismal_name`, `marital_status`, etc. for `members`.

## Members registry (`members/`)

Central member records with fixed columns plus dynamic `customFields` in one request.

| Method | Path | Who |
|--------|------|-----|
| GET | `/api/members?q=&page=0&size=20` | Admin, Recorder, Viewer |
| GET | `/api/members/{id}` | Admin, Recorder, Viewer |
| POST | `/api/members` | Admin, Recorder |
| PUT | `/api/members/{id}` | Admin, Recorder |
| DELETE | `/api/members/{id}` | Admin, Recorder (soft deactivate) |
| GET | `/api/members/{id}/profile` | Admin, Recorder, Viewer |

Example — create a member with custom fields (after Authorize):

```json
{
  "fullName": "Abebe Kebede",
  "phone": "0911123456",
  "kebele": "Kebele 05",
  "customFields": {
    "baptismal_name": "Gebre Meskel",
    "marital_status": "ያገባ"
  }
}
```

Search `q` matches name, phone, kebele, or exact member ID. Set `clergyId` to link a spiritual father (must exist in clergy registry).

## Family members (`family/`)

Relatives linked to a member household. Relationship (spouse, child, etc.) is stored in `customFields.relationship_type`.

| Method | Path | Who |
|--------|------|-----|
| GET | `/api/family-members?memberId=&q=` | Admin, Recorder, Viewer |
| GET | `/api/family-members/{id}` | Admin, Recorder, Viewer |
| POST | `/api/family-members` | Admin, Recorder |
| PUT | `/api/family-members/{id}` | Admin, Recorder |
| DELETE | `/api/family-members/{id}` | Admin, Recorder (soft deactivate) |
| GET | `/api/members/{id}/family-members` | Admin, Recorder, Viewer |

Example — add a spouse to member ID 1:

```json
{
  "memberId": 1,
  "fullName": "Sara Kebede",
  "age": 32,
  "customFields": {
    "relationship_type": "ሚስት"
  }
}
```

Valid `relationship_type` values (seeded dropdown): `ባል`, `ሚስት`, `ልጅ`, `አባት`, `እናት`, `ሌላ`.

## Clergy registry (`clergy/`)

Priests, deacons, and spiritual fathers. Members reference clergy via `clergyId`.

| Method | Path | Who |
|--------|------|-----|
| GET | `/api/clergy?q=&page=0&size=20` | Admin, Recorder, Viewer |
| GET | `/api/clergy/{id}` | Admin, Recorder, Viewer |
| POST | `/api/clergy` | Admin, Recorder |
| PUT | `/api/clergy/{id}` | Admin, Recorder |
| DELETE | `/api/clergy/{id}` | Admin, Recorder (soft deactivate) |

Example — create a priest, then assign to a member:

```json
{
  "fullName": "Fr. Daniel Tesfaye",
  "roleType": "ካህን",
  "phone": "0911000001",
  "address": "Bahir Dar"
}
```

Then update member: `"clergyId": 1`. Profile view `GET /api/members/1/profile` returns member + spiritual father + household + baptisms.

## Baptisms (`baptisms/`)

Child baptism records linked to a member (parent/guardian) and officiating clergy.

| Method | Path | Who |
|--------|------|-----|
| GET | `/api/baptisms?memberId=&q=` | Admin, Recorder, Viewer |
| GET | `/api/baptisms/{id}` | Admin, Recorder, Viewer |
| POST | `/api/baptisms` | Admin, Recorder |
| PUT | `/api/baptisms/{id}` | Admin, Recorder |
| DELETE | `/api/baptisms/{id}` | Admin, Recorder (soft deactivate) |
| GET | `/api/members/{id}/baptisms` | Admin, Recorder, Viewer |

Example — record a baptism for member ID 1:

```json
{
  "memberId": 1,
  "childName": "Yonas Kebede",
  "baptismDate": "2024-01-15",
  "officiatingClergyId": 1,
  "churchName": "St. George Orthodox Church"
}
```

## Staff ministers (`staffministers/`)

Church staff with optional member link. Education and salary fields use `customFields` (seeded in form engine).

| Method | Path | Who |
|--------|------|-----|
| GET | `/api/staff-ministers?memberId=&q=` | Admin, Recorder, Viewer |
| GET | `/api/staff-ministers/{id}` | Admin, Recorder, Viewer |
| POST | `/api/staff-ministers` | Admin, Recorder |
| PUT | `/api/staff-ministers/{id}` | Admin, Recorder |
| DELETE | `/api/staff-ministers/{id}` | Admin, Recorder (soft deactivate) |
| GET | `/api/members/{id}/staff-ministers` | Admin, Recorder, Viewer |

Example — create staff minister linked to member ID 1:

```json
{
  "fullName": "Deacon Mikael",
  "gender": "Male",
  "phone": "0911223344",
  "hireDate": "2020-03-01",
  "employmentType": "Full-time",
  "memberId": 1,
  "customFields": {
    "spiritual_education": "Sunday School Teacher",
    "salary_parish": "5000"
  }
}
```

Seeded custom fields: `spiritual_education`, `theological_education`, `modern_education`, `salary_diocese`, `salary_parish`.

## Office staff (`officestaff/`)

Parish office employees with optional links to a member and clergy supervisor.

| Method | Path | Who |
|--------|------|-----|
| GET | `/api/office-staff?memberId=&clergyId=&q=` | Admin, Recorder, Viewer |
| GET | `/api/office-staff/{id}` | Admin, Recorder, Viewer |
| POST | `/api/office-staff` | Admin, Recorder |
| PUT | `/api/office-staff/{id}` | Admin, Recorder |
| DELETE | `/api/office-staff/{id}` | Admin, Recorder (soft deactivate) |
| GET | `/api/members/{id}/office-staff` | Admin, Recorder, Viewer |

Example — create office secretary linked to member and clergy:

```json
{
  "fullName": "Hanna Bekele",
  "position": "Secretary",
  "phone": "0911334455",
  "memberId": 1,
  "clergyId": 1
}
```

## Workers (`workers/`)

Church workers for maintenance, grounds, and support roles. Emergency contacts will link to workers next.

| Method | Path | Who |
|--------|------|-----|
| GET | `/api/workers?q=&page=0&size=20` | Admin, Recorder, Viewer |
| GET | `/api/workers/{id}` | Admin, Recorder, Viewer |
| POST | `/api/workers` | Admin, Recorder |
| PUT | `/api/workers/{id}` | Admin, Recorder |
| DELETE | `/api/workers/{id}` | Admin, Recorder (soft deactivate) |

Example — create a grounds worker:

```json
{
  "fullName": "Tadesse Alemu",
  "phone": "0911445566",
  "jobRole": "Groundskeeper",
  "gender": "Male"
}
```

## What comes next

- Emergency contacts module — contacts linked to workers and office staff
- Remaining church modules — one at a time

Technical details: see the `docs/` folder.

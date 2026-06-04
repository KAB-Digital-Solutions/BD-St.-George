# API Architecture — Saint George MMS

Base URL: `/api`  
Auth: `Authorization: Bearer <JWT>` (except login)  
Docs: `/swagger-ui.html` (Springdoc OpenAPI 3)

## Authentication

| Method | Path | Role | Description |
|--------|------|------|-------------|
| POST | `/api/auth/login` | Public | `{ username, password }` → `{ token, role, fullName }` |
| GET | `/api/auth/me` | Authenticated | Current user profile |

## Dynamic form engine (Admin)

| Method | Path | Role | Description |
|--------|------|------|-------------|
| GET | `/api/admin/form-fields/{moduleKey}` | ADMIN, RECORDER*, VIEWER* | Active definitions for rendering (*RECORDER/VIEWER read-only) |
| POST | `/api/admin/form-fields` | ADMIN | Create field |
| PUT | `/api/admin/form-fields/{id}` | ADMIN | Update labels, type, options, required |
| PATCH | `/api/admin/form-fields/{moduleKey}/reorder` | ADMIN | Body: ordered field IDs |
| DELETE | `/api/admin/form-fields/{id}` | ADMIN | Soft-deactivate only if not `is_system_core` |

## Custom field values (per record)

| Method | Path | Role | Description |
|--------|------|------|-------------|
| GET | `/api/{moduleKey}/{recordId}/custom-fields` | ALL (read) | Map field_key → value |
| POST | `/api/{moduleKey}/{recordId}/custom-fields` | ADMIN, RECORDER | Upsert values |
| PUT | `/api/{moduleKey}/{recordId}/custom-fields` | ADMIN, RECORDER | Replace all |

`moduleKey` must be one of the 14 registered keys.

## Domain modules (pattern)

Each module follows:

```
GET    /api/{resource}              — paginated list + search params
GET    /api/{resource}/{id}         — detail + custom fields
POST   /api/{resource}              — create (core + customFields map)
PUT    /api/{resource}/{id}         — update + audit
DELETE /api/{resource}/{id}         — deactivate (soft) where applicable
POST   /api/{resource}/{id}/photo   — multipart upload
```

### Implemented / planned resources

| Resource path | Entity | Notes |
|---------------|--------|-------|
| `/api/members` | members | Profile aggregate at `GET /api/members/{id}/profile` |
| `/api/clergy` | clergy | |
| `/api/baptisms` | baptisms | |
| `/api/family-members` | family_members | |
| `/api/staff-ministers` | staff_ministers | |
| `/api/office-staff` | office_staff | |
| `/api/workers` | workers | |
| `/api/emergency-contacts` | emergency_contacts | |
| `/api/parish-council` | parish_council | |
| `/api/contributions` | contributions | receipt_no unique |
| `/api/transfers` | transfers | sets member status TRANSFERRED |
| `/api/deceased` | deceased | POST only (immutable) |
| `/api/sunday-school` | sunday_school | enrollment report: `GET .../reports/enrollment` |
| `/api/abnet-school` | abnet_school | |

## Admin — users & audit

| Method | Path | Role | Description |
|--------|------|------|-------------|
| GET/POST/PUT | `/api/admin/users` | ADMIN | User CRUD, password reset |
| GET | `/api/admin/audit-log` | ADMIN | Filter: module, userId, date range |

## Standard response shapes

**Paged list:**
```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

**Create/update body:**
```json
{
  "fullName": "...",
  "phone": "...",
  "customFields": {
    "marital_status": "ያገባ",
    "birth_date": "2015-03-01"
  }
}
```

**Error:**
```json
{
  "timestamp": "2026-06-04T12:00:00Z",
  "status": 400,
  "error": "Validation Failed",
  "message": "...",
  "path": "/api/members"
}
```

## RBAC enforcement

- Method security on **service layer** (`@PreAuthorize`).
- Controllers remain thin; no business logic.
- VIEWER: `HttpMethod.GET` only via global rule + service checks.

## File uploads

- `POST /api/files/upload` — returns `{ path, url }`
- Max 2MB, `image/jpeg`, `image/png`
- Stored under `${file.upload-dir}` Docker volume

## Migration (offline)

- Profile: `spring.profiles.active=migrate`
- CLI: `java -jar app.jar --accdb=/data/Saint_George.accdb`
- Not exposed over HTTP

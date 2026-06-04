# Saint George Member Management System — Implementation Plan

**Version:** 2.0 (aligned with `SaintGeorge_Full_Requirements_v2.docx`)  
**Client:** Bahir Dar Felege Genet St. George Orthodox Church  
**Vendor:** KAB Digital Solution PLC  
**Stack:** Spring Boot 3.x (Java 21) · PostgreSQL 16 · React 18 · Docker Compose

---

## 1. Executive alignment

| Requirement | Implementation approach |
|-------------|-------------------------|
| Dynamic Form Engine | `form_field_definitions` + `record_custom_fields`; React `DynamicForm`; admin CRUD + reorder API |
| 14 Access tables | Fixed core tables (FK integrity) + dynamic fields; Flyway `V3__create_core_tables.sql` |
| RBAC | `ADMIN`, `RECORDER`, `VIEWER`; JWT + `@PreAuthorize` on services |
| Audit | `audit_log` with `old_data` / `new_data` JSONB on every CREATE/UPDATE/DELETE |
| Bilingual UI | `react-i18next` + `label_am` / `label_en` from form definitions |
| Migration | Spring `CommandLineRunner` + UCanAccess; OLE photo extraction; sequence reset |
| Docker | `docker-compose.yml` (dev) + `docker-compose.prod.yml` (Nginx, volumes, backups) |

**Core principle:** No business field is hardcoded beyond the fixed columns listed in the requirements document. All other fields are admin-configurable without redeploy.

---

## 2. Clean architecture layers

```
┌─────────────────────────────────────────────────────────────┐
│  React SPA (pages, DynamicForm, i18n, AuthContext)          │
└───────────────────────────┬─────────────────────────────────┘
                            │ REST / JWT
┌───────────────────────────▼─────────────────────────────────┐
│  Controllers (REST, OpenAPI, validation)                      │
├─────────────────────────────────────────────────────────────┤
│  Services (business rules, RBAC, audit, custom fields)      │
├─────────────────────────────────────────────────────────────┤
│  Repositories (Spring Data JPA)                             │
├─────────────────────────────────────────────────────────────┤
│  PostgreSQL (core tables + JSONB dynamic storage)           │
└─────────────────────────────────────────────────────────────┘
```

**SOLID mapping:**
- **SRP:** One service/controller pair per domain module (members, clergy, …).
- **OCP:** New fields via `form_field_definitions`, not code changes.
- **LSP:** Shared `AuditableService` / `CustomFieldService` interfaces for modules.
- **ISP:** Narrow DTOs per operation (create vs list vs profile).
- **DIP:** Services depend on repository interfaces; security via abstractions.

---

## 3. Repository layout

```
BD-St.-George/
├── docker-compose.yml
├── docker-compose.prod.yml
├── .env.example
├── docs/
│   ├── IMPLEMENTATION_PLAN.md
│   ├── DATABASE_SCHEMA.md
│   ├── API_ARCHITECTURE.md
│   └── ROADMAP.md
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/kabdigital/saintgeorge/
│       ├── config/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       ├── dto/
│       ├── security/
│       ├── exception/
│       ├── util/
│       └── migration/
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/
└── frontend/
    ├── Dockerfile
    ├── nginx.conf
    └── src/
        ├── api/
        ├── components/common/   # DynamicForm, EthiopicDatePicker, PhotoUpload
        ├── components/layout/
        ├── pages/
        ├── hooks/
        ├── context/
        └── i18n/
```

---

## 4. Module keys (dynamic engine)

| module_key | Core table | Access origin |
|------------|------------|---------------|
| `members` | members | Table 7 |
| `clergy` | clergy | Table 2 |
| `baptisms` | baptisms | Table 1 |
| `family_members` | family_members | Table 8 |
| `staff_ministers` | staff_ministers | Table 3 |
| `office_staff` | office_staff | Table 11 |
| `workers` | workers | Table 12 |
| `emergency_contacts` | emergency_contacts | Table 13 |
| `parish_council` | parish_council | Table 6 |
| `contributions` | contributions | Table 9 |
| `transfers` | transfers | Table 10 |
| `deceased` | deceased | Table 14 |
| `sunday_school` | sunday_school | Table 5 |
| `abnet_school` | abnet_school | Table 4 |

---

## 5. Cross-cutting services

| Service | Responsibility |
|---------|----------------|
| `AuthService` | Login, JWT issue/validate |
| `AuditService` | Persist audit rows with diff |
| `CustomFieldService` | Load/save `record_custom_fields` per module |
| `FormFieldDefinitionService` | Admin CRUD + reorder |
| `FileStorageService` | Photo upload (2MB, JPEG/PNG), path in `photo_path` |
| `AccessMigrationRunner` | One-time `.accdb` import (profile `migrate`) |

---

## 6. Security model

| Role | Permissions |
|------|-------------|
| ADMIN | All modules + form field admin + users + audit log |
| RECORDER | CRUD on domain modules; read form definitions |
| VIEWER | GET only on domain modules |

Public endpoints: `POST /api/auth/login`, Swagger UI (dev), actuator health.

---

## 7. Dual-member model (FR-B01)

`members.second_member` JSONB stores secondary spouse fields migrated from Access `ሁለተኛ_*` columns. Primary member uses fixed columns; secondary uses structured JSON (name, phone, baptismal fields, etc.) plus shared dynamic fields where applicable.

---

## 8. Deceased archive (FR-C05)

`deceased` records are immutable after create: service rejects UPDATE/DELETE; member `is_active` set false; profile read-only.

---

## 9. Deliverables by phase (14 weeks)

See [ROADMAP.md](./ROADMAP.md) for week-by-week tasks and acceptance criteria.

**Current codebase status (incremental build):**
- Phase 0–1: Docker, Flyway schema, Spring Boot skeleton — **in repo**
- Phase 2–3: JWT, RBAC, audit, form engine API — **in repo**
- Phase 4: Domain modules — **members + clergy first**, remaining modules follow same pattern
- Phase 5: React shell + DynamicForm + admin field manager — **scaffolded**
- Phase 6–8: Migration runner stub, prod compose, backup docs — **planned**

---

## 10. Acceptance & quality gates

- Record counts match Access post-migration (all 14 tables).
- 10 concurrent users &lt; 2s response (JMeter, Phase 7).
- Ethiopic UTF-8 verified in UI and DB.
- `docker compose up --build` runs full stack locally.
- Default admin password changed before production (see `.env.example`).

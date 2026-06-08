# Development Roadmap — 14 Weeks

Aligned with requirements document Section 8–9.

## Phase 0 — Environment (Week 1) ✅ Done

- [x] Git repository structure
- [x] `docker-compose.yml` (PostgreSQL on host port 5433)
- [x] `.env.example` for secrets
- [x] README quick start

## Phase 1 — Auth ✅ Done

- [x] Spring Boot 4 / Java 21, Flyway V1–V2 (`users` + seed admin)
- [x] JWT login (`POST /api/auth/login`) + `GET /api/auth/me`
- [x] Spring Security (3 roles: ADMIN, RECORDER, VIEWER)
- [x] Admin user management (`/api/admin/users`) in `auth/` module
- [ ] Integration test with Testcontainers

## Phase 2 — Dynamic form engine ✅ Done

- [x] Flyway V3–V4 (`form_field_definitions`, `record_custom_fields`)
- [x] `formengine/` module — admin API + custom field values API
- [x] Seed baseline fields: `members`, `staff_ministers`, `family_members`
- [ ] Expand seed to all 14 modules (as each domain module is built)
- [ ] React Admin UI: Form Field Manager with drag-and-drop

## Phase 3 — Members module ✅ Done

- [x] Flyway V5 (`members` table)
- [x] `members/` module — CRUD, search, soft deactivate
- [x] Integrates `CustomFieldService` (`moduleKey = members`)

## Phase 4 — Family module ✅ Done

- [x] Flyway V6 (`family_members` table, FK to `members`)
- [x] `family/` module — CRUD, filter by `memberId`, soft deactivate
- [x] `GET /api/members/{id}/family-members` household view
- [x] `relationship_type` via form engine (`moduleKey = family_members`)
## Phase 5 — Clergy module ✅ Done

- [x] Flyway V7 (`clergy` table)
- [x] Flyway V8 (`members.clergy_id` FK to `clergy`)
- [x] `clergy/` module — CRUD, search, soft deactivate
- [x] Member `clergyId` validation on create/update
- [x] `GET /api/members/{id}/profile` — member + spiritual father + household

## Phase 6 — Module backends (Weeks 4–8)

| Week | Module | Status |
|------|--------|--------|
| 4 | Members + Family | ✅ Done |
| 5 | Clergy + Baptisms | Clergy ✅; Baptisms next |
| 6 | Staff ministers, office, workers, emergency | Planned |
| 7 | Sunday school, Abnet, Parish council | Planned |
| 8 | Contributions, Transfers, Deceased | Planned |

## Phase 5 — React frontend (Weeks 6–9)

| Week | Deliverable | Status |
|------|-------------|--------|
| 6 | Vite + React 18, Axios, auth, i18n AM/EN | Scaffold ✅ |
| 7 | DynamicForm (7 field types) | Scaffold ✅ |
| 8 | Module pages (members, clergy) | Scaffold ✅ |
| 9 | Admin panel (fields, users, audit) | Partial |

## Phase 6 — Data migration (Week 10)

- [ ] UCanAccess dependency + `AccessMigrationRunner`
- [ ] OLE photo extraction utility
- [ ] Count validation report
- [ ] Dual-member column mapping

## Phase 7 — Testing (Weeks 11–12)

- [ ] JUnit 5 service tests
- [ ] Testcontainers API tests
- [ ] Jest: DynamicForm + i18n
- [ ] JMeter: 10 users
- [ ] Church UAT sign-off template

## Phase 8 — Deployment (Weeks 13–14)

- [ ] `docker-compose.prod.yml` + Nginx TLS
- [ ] pg_dump cron (30-day retention)
- [ ] Amharic printed user guide
- [ ] Go-live checklist (Section 8.3)

## Phase 9 — Training & support (Week 14)

- On-site training (1 day minimum)
- 30-day vendor support window

---

## Incremental delivery order (this repo)

1. **Now:** Foundation + auth + form engine + members + clergy + React shell  
2. **Next:** Remaining 12 module APIs (same pattern)  
3. **Then:** Full form field seed for all Access columns  
4. **Then:** Migration runner against real `.accdb`  
5. **Finally:** Production hardening + UAT

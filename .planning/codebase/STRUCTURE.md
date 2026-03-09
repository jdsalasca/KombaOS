# STRUCTURE

## Repository roots
- `backend/`: Spring Boot application and tests.
- `frontend/`: React/Vite single-page app and tests.
- `shared/`: shared artifacts (OpenAPI).
- `software/`: packaging and requirements documentation.

## Backend layout (`backend/src/main/java/com/kombaos`)
- `catalog/product/`
- `inventory/material/`
- `inventory/movement/`
- `inventory/threshold/`
- `config/` (properties/migration behavior)
- `security/`
- `web/controller/` (cross-cutting web handlers)
- `persistence/file/` (generic file persistence utility)

## Backend tests
- Main integration/smoke tests under `backend/src/test/java/com/kombaos/**`.
- Test properties in `backend/src/test/resources/application-test.properties`.

## Frontend layout (`frontend/src`)
- `features/products/`
- `features/materials/`
- `features/inventory/`
- `lib/` (API client helpers + shared types)
- `test/setup.ts` (testing bootstrap)

## Frontend test layout
- Unit/component tests: `frontend/src/App.test.tsx` (and feature-level patterns).
- E2E tests: `frontend/e2e/products.spec.ts`, `frontend/e2e/inventory.spec.ts`.

## Naming and placement patterns
- Java: package-by-domain + suffix-driven classes (`Controller`, `Service`, `Store`, `Entity`, `Response`, `Request`).
- Frontend: feature-first directories with `Panel` component + `useX` hook pairing.

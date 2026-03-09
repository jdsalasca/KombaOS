# TESTING

## Backend
- **Frameworks:** Spring Boot Test + JUnit (through `spring-boot-starter-test`).
- **Command:** `cd backend && ./mvnw test`.
- **Coverage style in repo:**
  - Local smoke tests per domain controller:
    - `ProductsLocalSmokeTest`
    - `MaterialsLocalSmokeTest`
    - `InventoryMovementsLocalSmokeTest`
    - `MaterialThresholdsLocalSmokeTest`
  - Persistence-specific tests:
    - `FileMaterialStoreTest`

## Frontend unit/integration
- **Frameworks:** Vitest + Testing Library + jsdom.
- **Command:** `cd frontend && npm run test`.
- **Config:** `frontend/vitest.config.ts` and test bootstrap in `frontend/src/test/setup.ts`.

## Frontend end-to-end
- **Framework:** Playwright.
- **Specs:** `frontend/e2e/products.spec.ts`, `frontend/e2e/inventory.spec.ts`.
- **Config:** `frontend/playwright.config.ts`.

## Quality gates documented in AGENTS.md
- Frontend lint: `cd frontend && npm run lint`
- Frontend tests: `cd frontend && npm run test`
- Frontend build: `cd frontend && npm run build`
- Backend tests: `cd backend && ./mvnw test`

## Current testing posture (quick read)
- Both tiers have automated test scaffolding checked in.
- Domain-centric smoke tests suggest focus on API contract stability.
- E2E suite currently covers core product/inventory user flows.

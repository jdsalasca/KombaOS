# INTEGRATIONS

## Databases
- **Primary relational DB:** PostgreSQL through Spring Data JPA.
- **Schema management:** Flyway migrations (`V1__init.sql` ... `V4__materials_attributes.sql`).
- **JPA repositories:**
  - `ProductJpaRepository`
  - `MaterialJpaRepository`
  - `InventoryMovementJpaRepository`
  - `MaterialStockThresholdJpaRepository`

## API surface
- REST controllers are split by domain:
  - Products: `catalog/product/controller/ProductController.java`
  - Materials: `inventory/material/controller/MaterialController.java`
  - Inventory movements: `inventory/movement/controller/InventoryMovementController.java`
  - Thresholds/alerts: `inventory/threshold/controller/MaterialStockThresholdController.java`
  - Health check: `health/controller/HealthController.java`
- API contract reference exists in `shared/openapi/kombaos.yaml`.

## Frontend ↔ Backend interaction
- Frontend communicates over HTTP using `frontend/src/lib/api.ts`.
- Domain hooks (`useProducts`, `useMaterials`, `useInventory`) encapsulate integration calls and map data to UI panels.
- Playwright e2e tests validate end-to-end flows around products and inventory (`frontend/e2e/*.spec.ts`).

## Security and web integration
- Spring Security configuration is centralized in `backend/src/main/java/com/kombaos/security/SecurityConfig.java`.
- SPA fallback routing for front-end assets/API coexistence is handled in `backend/src/main/java/com/kombaos/web/controller/SpaForwardingController.java`.

## Operational integration points
- Local smoke tests exist per domain in `backend/src/test/java/.../*LocalSmokeTest.java`.
- Windows scripts for packaging/testing support are under `software/packaging/windows/` and `frontend/e2e/start-fullstack.ps1`.

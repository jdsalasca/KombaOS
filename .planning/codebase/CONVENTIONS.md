# CONVENTIONS

## Backend coding conventions
- Package-by-domain structure is preferred over technical-only grouping.
- Services depend on interfaces (`*Store`) instead of concrete implementations.
- DTOs are explicit per operation (`CreateRequest`, `UpdateRequest`, `Response`).
- Persistence adapters are split by technology (`jpa/` vs `file/`).
- Error handling is centralized in `ApiExceptionHandler`.

## Frontend coding conventions
- Feature-oriented structure in `src/features/`.
- Reusable API calls and low-level network behavior are kept in `src/lib/api.ts`.
- Data fetching/state transitions are isolated in hooks (`useProducts`, `useMaterials`, `useInventory`).
- Panels/components stay focused on rendering and user interactions.

## Testing conventions
- Backend includes per-domain smoke tests and storage-focused tests.
- Frontend uses Vitest + Testing Library for UI behavior and Playwright for user flows.
- Test setup and configuration are explicit (`frontend/src/test/setup.ts`, `frontend/vitest.config.ts`).

## Operational conventions
- Migrations are incremental, versioned SQL files in `db/migration/`.
- Root-level docs describe workflow/release context (`README.md`, `CHANGELOG.md`, `AGENTS.md`).

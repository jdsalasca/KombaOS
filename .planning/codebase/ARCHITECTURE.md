# ARCHITECTURE

## High-level style
KombaOS follows a modular monolith style with clear domain slices and layered boundaries:
- **Domain layer:** entities/value objects in `domain/`
- **Application layer:** use-case services in `service/`
- **Interface/API layer:** REST controllers + DTOs in `controller/` and `dto/`
- **Persistence adapters:** repositories under `repository/jpa` and `repository/file`

## Domain modules
- `catalog/product`
- `inventory/material`
- `inventory/movement`
- `inventory/threshold`

Each module repeats the same structure (controller, dto, domain, service, repository), which keeps responsibilities explicit and local.

## Backend request flow
1. HTTP request enters a domain controller.
2. Request DTO is validated and transformed.
3. Service orchestrates business rules.
4. Service depends on `*Store` interfaces (ports).
5. Adapter (`Jpa*Store` or `File*Store`) persists/reads data.
6. Response DTO is returned by controller.

## Frontend architecture
- `App.tsx` composes feature panels.
- Each feature uses a dedicated hook (`useProducts`, `useMaterials`, `useInventory`) for state + async behavior.
- `lib/types.ts` defines shared front-end types, while `lib/api.ts` centralizes API primitives.

## Error and edge handling
- Backend global exception translation is in `web/controller/ApiExceptionHandler.java`.
- Validation is annotation-driven in request DTOs.
- Frontend error/loading states are managed inside feature hooks/panels.

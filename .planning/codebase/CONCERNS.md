# CONCERNS

## 1) Dual persistence strategy complexity
The coexistence of file-based stores and JPA stores is useful for local development but increases behavioral divergence risk:
- Different query/filter semantics.
- Potential mismatch in validation/constraints.
- Data consistency edge cases if implementations evolve independently.

## 2) Limited explicit contract synchronization workflow
`shared/openapi/kombaos.yaml` exists, but there is no visible automated check in this repo root ensuring backend endpoints and OpenAPI stay synchronized on every change.

## 3) Frontend API hardening opportunities
The frontend central API helper (`src/lib/api.ts`) is compact, but the repository does not expose advanced resiliency patterns (retry, typed error envelopes, centralized auth refresh) in a visible way.

## 4) Test breadth vs depth
- Backend smoke tests are present, but domain logic heavy paths (threshold edge conditions, movement corrections, concurrency scenarios) may need deeper behavioral tests.
- Frontend has e2e coverage for core flows, but less evidence of broader regression matrices (empty states, network failure UX, pagination/filter edge cases).

## 5) Security posture visibility
A `SecurityConfig` exists, but from structure alone it is not immediately obvious whether CSRF/CORS/auth modes are tuned differently per environment and validated in automated tests.

## 6) Documentation localization and drift
Requirements docs under `software/requirements/` are useful, but long-lived markdown artifacts can drift from implementation unless tied to a recurring review/update process.

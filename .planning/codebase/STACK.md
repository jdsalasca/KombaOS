# STACK

## Backend
- **Language/runtime:** Java 21 on Spring Boot 3.5.x (`backend/pom.xml`).
- **Build tool:** Maven Wrapper (`backend/mvnw`, `backend/pom.xml`).
- **Primary dependencies:**
  - `spring-boot-starter-web`
  - `spring-boot-starter-security`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-data-jpa`
  - Flyway migrations
  - PostgreSQL driver
- **Configuration:** `backend/src/main/resources/application.properties` and `backend/src/test/resources/application-test.properties`.
- **Database migrations:** SQL-based Flyway scripts in `backend/src/main/resources/db/migration/`.

## Frontend
- **Language/runtime:** TypeScript + React 19 (`frontend/package.json`).
- **Build tool:** Vite 7 (`frontend/vite.config.ts`, `frontend/package.json`).
- **UI stack:** React hooks + component features in `frontend/src/features/*`.
- **HTTP client:** native `fetch` abstraction in `frontend/src/lib/api.ts`.
- **Linting/testing:** ESLint + Vitest + Testing Library (`frontend/eslint.config.js`, `frontend/vitest.config.ts`).
- **E2E:** Playwright specs in `frontend/e2e/*.spec.ts`.

## Shared and cross-cutting
- **API contract file:** `shared/openapi/kombaos.yaml`.
- **Containerization:** root `Dockerfile` and `backend/Dockerfile`.
- **Packaging/requirements docs:** `software/packaging/windows/` and `software/requirements/`.

## Storage modes
- The backend exposes repository interfaces (`*Store`) with two implementation families:
  - JPA-based implementations (`repository/jpa/*`)
  - File-based implementations (`repository/file/*`)
- File persistence primitives are centralized in `backend/src/main/java/com/kombaos/persistence/file/FileJsonListStore.java`.

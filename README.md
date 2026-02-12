# KombaOS

KombaOS es una plataforma fullstack para centralizar la operación textil/artesanal: inventarios, trazabilidad, catálogo y ventas. El MVP prioriza funcionalidades sin IA (base de datos, reportes, workflows y control operacional) y deja preparada la base para integraciones posteriores.

## Objetivo

"Desarrollar un modelo automatizado para la producción y comercialización artesanal de textiles, apoyado con la IA para optimizar la producción, comercialización y fidelización de clientes, promoviendo la sostenibilidad y la inclusión de oficios tradicionales en Boyacá."

En el MVP (sin IA) ya están cubiertos inventarios y un backoffice mínimo de productos.

## Arquitectura

- Backend: Spring Boot (Java 21) + Flyway + JPA
- Frontend: React + Vite (TypeScript)
- Build fullstack: el frontend se empaqueta dentro del backend (static/)
- Persistencia híbrida
  - local: archivos JSON + H2 en directorio local
  - cloud: JPA sobre base configurada por variables de entorno

## Ejecutar en desarrollo

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Variables útiles

- ENVIRONMENT=local | cloud
- KOMBAOS_LOCAL_STORAGE_DIR (solo local)
- DATABASE_URL / DATABASE_USERNAME / DATABASE_PASSWORD

### Frontend

```bash
cd frontend
npm ci
npm run dev
```

## Ejecutar con Docker

```bash
docker build -t kombaos .
docker run --rm -p 8080:8080 kombaos
```

## Empaquetar EXE (Windows)

```powershell
cd software\packaging\windows
./package.ps1 -Environment local -SmokeTest
```

El resultado queda en dist\KombaOS\KombaOS.exe.

## CI/CD y releases

- CI en GitHub Actions para backend y frontend
- Release automático genera EXE en Windows y publica el ZIP en GitHub Releases
- Notas del release toman el contenido de CHANGELOG.md

## Changelog

Ver CHANGELOG.md

## Tests

### Backend

```bash
cd backend
./mvnw test
```

### Frontend

```bash
cd frontend
npm test
npm run lint
npm run build
npm run e2e
```

# BACKLOG PRIORITARIO (30 tareas)

Fuente: levantamiento técnico en `.planning/codebase/*.md` y estándares de calidad del repositorio.

## Priorización
- **P0**: crítico, desbloquea calidad/entrega.
- **P1**: alto impacto en robustez y mantenimiento.
- **P2**: mejora continua y escalabilidad.

## Ejecución actual (GSD)
- **Misión activa:** Wave 1 / P0.
- **Tarea en curso:** #2 — formato unificado de errores API.
- **Avance:** #1 completada en contrato OpenAPI; #2 en progreso con `ApiExceptionHandler` unificado a `code/message/details/traceId` y tests de contrato de error actualizados.
- **Tarea en curso:** #1 — contratos OpenAPI reales.
- **Avance:** `shared/openapi/kombaos.yaml` alineado con endpoints de productos/materiales/movimientos/umbrales; pendiente automatizar validación en CI para cerrar completamente el criterio.


## Tareas

| # | Prioridad | Área | Tarea | Entregable / Criterio de aceptación |
|---|---|---|---|---|
| 1 | P0 | Backend | Definir y documentar contratos OpenAPI reales para productos, materiales, movimientos y umbrales | `shared/openapi/kombaos.yaml` actualizado + validación de esquema en CI |
| 2 | P0 | Backend | Alinear respuestas de error API en un formato único (`code`, `message`, `details`, `traceId`) | `ApiExceptionHandler` unificado + tests de contrato de error |
| 3 | P0 | Backend | Cubrir lógica de inventario con tests de reglas de negocio (egresos no permitidos bajo stock mínimo) | Nuevos tests unitarios/servicio para escenarios borde |
| 4 | P0 | Backend | Implementar validaciones de integridad de movimientos (tipos, cantidades, referencias) | DTO + service validations + tests de rechazo |
| 5 | P0 | Backend | Agregar pruebas de concurrencia en ajustes de stock | Suite de pruebas que reproduzca colisiones concurrentes |
| 6 | P0 | Backend | Definir estrategia única de persistencia por entorno (JPA vs file) con flags explícitas | Configuración por perfil y documentación de operación |
| 7 | P0 | Frontend | Estandarizar manejo de errores de red y API en `src/lib/api.ts` | Handler central + UI consistente en todos los paneles |
| 8 | P0 | Frontend | Añadir estados vacíos/carga/error en Products, Materials e Inventory con UX homogénea | Cobertura en componentes + snapshots/test de interacción |
| 9 | P0 | Frontend | Endurecer tipado de DTOs frontend↔backend y eliminar `any` residuales | `src/lib/types.ts` alineado con OpenAPI + lint sin warnings críticos |
| 10 | P0 | QA | Ejecutar y estabilizar pipeline mínima de calidad: lint + tests + build frontend + tests backend | Runbook + comandos verdes localmente y en CI |
| 11 | P1 | Backend | Migrar smoke tests a pruebas de integración con assertions de payload más completas | Tests actualizados por dominio con cobertura de edge cases |
| 12 | P1 | Backend | Medir cobertura de tests y fijar umbral inicial por módulo | Reporte cobertura + umbral configurable en CI |
| 13 | P1 | Backend | Revisar configuración de seguridad (CORS/CSRF/auth) por ambiente | Documento de decisiones + tests de seguridad básicos |
| 14 | P1 | Backend | Introducir trazabilidad por request (`traceId`) en logs y respuestas de error | Logs correlacionables end-to-end |
| 15 | P1 | Backend | Normalizar mapeo entidad↔DTO para reducir duplicidad | Utilidades/mappers reutilizables + tests |
| 16 | P1 | Frontend | Introducir capa de servicios por feature sobre `api.ts` para separar transporte de UI | `features/*/services` con pruebas unitarias |
| 17 | P1 | Frontend | Incorporar pruebas de accesibilidad básicas (roles, labels, focus) | Tests de accesibilidad en componentes principales |
| 18 | P1 | Frontend | Añadir pruebas de error recovery (retry manual, recarga de vistas) | Flujos verificados en Vitest/Playwright |
| 19 | P1 | Frontend | Reducir acoplamiento de hooks y paneles (estado derivado fuera de UI) | Hooks simplificados + componentes más declarativos |
| 20 | P1 | DevEx | Añadir scripts de bootstrap local fullstack reproducible | Comando único documentado para levantar stack |
| 21 | P2 | Backend | Agregar paginación/filtrado en listados de materiales y productos | Endpoints y DTOs con filtros versionados |
| 22 | P2 | Backend | Versionar endpoints críticos para cambios futuros sin ruptura | Estrategia de versionado documentada |
| 23 | P2 | Backend | Crear tests de regresión para migraciones Flyway en entornos limpios | Job de validación de migraciones |
| 24 | P2 | Frontend | Mejorar observabilidad UI (telemetría de errores de interacción) | Eventos mínimos instrumentados |
| 25 | P2 | Frontend | Definir design tokens básicos (espaciado, colores de estado, tipografía) | Base CSS reutilizable y coherente |
| 26 | P2 | QA | Expandir E2E para escenarios de inventario límite (stock bajo, umbral, correcciones) | Nuevas specs Playwright estables |
| 27 | P2 | QA | Añadir pruebas contractuales backend-openapi automatizadas | Verificación automática de drift de contrato |
| 28 | P2 | Docs | Crear guía de arquitectura viva para onboarding técnico | Documento con decisiones, flujos y límites por dominio |
| 29 | P2 | Docs | Documentar runbook de incidentes para inventario y datos inconsistentes | Procedimiento de diagnóstico y recuperación |
| 30 | P2 | Gestión | Definir roadmap trimestral con ownership por tarea y métricas de éxito | Tablero priorizado con responsables y fechas |

## Orden de ejecución recomendado (GSD-ready)
1. **Wave 1 (P0):** tareas 1–10.
2. **Wave 2 (P1):** tareas 11–20.
3. **Wave 3 (P2):** tareas 21–30.

## KPIs de calidad sugeridos
- Build verde continuo en frontend y backend.
- Reducción de bugs de integración frontend↔backend.
- Cobertura de pruebas en crecimiento sostenido por dominio.
- MTTR menor en incidencias de inventario.

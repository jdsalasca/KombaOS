# AGENTS.md

Guía para agentes de código. Mantén este documento conciso y orientado a ejecución.

## Ramas

- develop: rama base para el trabajo diario
- release/*: preparación de releases desde develop
- master: solo para releases publicados

## Flujo de trabajo

1. Crear ramas de feature desde develop con el formato feature/<ticket>-<resumen>
2. Abrir PR descriptivo hacia develop
3. Merge por PR luego de revisión
4. Crear rama release/<version> desde develop cuando el milestone tenga valor
5. Abrir PR release/<version> -> master con checklist de release
6. Crear el release desde master y etiquetar v<version>
7. Volver a develop y continuar ciclo

## Releases

- Se generan al cerrar un milestone con valor real para compartir
- El release se hace desde master con tag v<version>

## Principios de código

- Modularidad por dominio y responsabilidad
- Clases pequeñas con objetivos claros
- Métodos cortos con una sola intención
- Composición sobre herencia, evita super clases
- Depender de interfaces, no de implementaciones

## Calidad

- Frontend: cd frontend && npm run lint, npm run test, npm run build
- Backend: cd backend && ./mvnw test

## Comandos rápidos

- Frontend unit tests: cd frontend && npm run test
- Frontend lint: cd frontend && npm run lint
- Frontend build: cd frontend && npm run build
- Backend tests: cd backend && ./mvnw test

## Do / Don’t

- Do: usa patrones y estructuras existentes.
- Do: escribe módulos pequeños y especializados.
- Don’t: inventes APIs o rutas sin buscar primero.
- Don’t: agregues dependencias pesadas sin aprobación.

## Prácticas para agentes

- Mantén cambios pequeños y revisables.
- Evita reescrituras masivas sin pedido explícito.
- No agregues dependencias nuevas sin aprobación.
- Prefiere editar archivos existentes antes que crear nuevos.
- Verifica rutas y convenciones mirando el código real.
- Si mueves imports o carpetas, valida con lint y build.

## Referencias

- .agents-rules/01-principios.md
- .agents-rules/02-clases-metodos.md
- .agents-rules/03-git-flow.md
- .agents-rules/04-calidad.md
- .agents-rules/05-frontend.md
- .agents-rules/06-backend.md
- .agents-rules/07-release.md

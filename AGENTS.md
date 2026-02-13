# AGENTS.md

Guía para agentes. Mantenerla concisa, accionable y alineada al repositorio.

## Branches

- develop: rama de integración
- release/*: preparación de release desde develop
- master: releases publicados

## Workflow

1. Crear feature branches desde develop: feature/<ticket>-<summary>
1. Abrir PR descriptivo hacia develop
1. Merge después de revisión y validación
1. El merge es obligatorio, no dejar trabajo parcial
1. Crear release/<version> desde develop cuando el hito tenga valor
1. Abrir PR release/<version> -> master con checklist de release
1. Publicar release desde master y tag v<version>
1. Continuar trabajo en develop

## Releases

- Todo release debe salir desde master
- Los tags se crean desde el workflow de release
- Verificar que el EXE quede adjunto a la Release
- Si master se distancia de develop, planificar release y sincronizar

## Diseño y arquitectura

- Modularidad por dominio, evita clases monolíticas
- Clases con un objetivo claro y responsabilidad única
- Métodos pequeños, legibles y con una sola intención
- Prefiere composición sobre herencia, evita super clases
- Depende de interfaces, no de implementaciones
- Evita estado compartido global sin necesidad

## UX para usuarios no técnicos

- Mensajes claros y en español
- Validaciones preventivas en formularios
- Estados vacíos guiados y acciones de un clic

## Calidad

- Frontend: cd frontend && npm run lint, npm run test, npm run build
- Backend: cd backend && ./mvnw test
- No mezclar cambios funcionales con formateo no solicitado

## Quick commands

- Frontend unit tests: cd frontend && npm run test
- Frontend lint: cd frontend && npm run lint
- Frontend build: cd frontend && npm run build
- Backend tests: cd backend && ./mvnw test

## Comunicación

- Explica qué cambió y cómo validar
- Reporta riesgos y rollback si aplica
- Describe trade-offs de diseño cuando existan

## Do / Don’t

- Do: seguir patrones y layout existentes
- Do: cambios pequeños y revisables
- Don’t: inventar APIs o rutas sin buscar primero
- Don’t: agregar dependencias pesadas sin aprobación

## Coordinación multi-agent

- Anunciar archivos a tocar y evitar ediciones solapadas
- Preferir merges secuenciales en vez de reescrituras paralelas
- Rebase o merge frecuente para reducir conflictos
- No pushear cambios locales de otros agentes
- PRs acotados a un cambio lógico
- Merge de todo el trabajo a develop para evitar entrega parcial

## Prácticas de agente

- Preferir editar archivos existentes sobre crear nuevos
- Evitar reescrituras masivas no solicitadas
- Validar lint y build después de mover imports o carpetas
- Confirmar convenciones reales antes de implementar

## Referencias

- .agents-rules/00-multi-agent.md
- .agents-rules/01-principles.md
- .agents-rules/02-classes-methods.md
- .agents-rules/03-git-flow.md
- .agents-rules/04-quality.md
- .agents-rules/05-frontend.md
- .agents-rules/06-backend.md
- .agents-rules/07-release.md

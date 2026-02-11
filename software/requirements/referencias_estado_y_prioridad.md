# KombaOS — Referencias, estado y prioridades

## Referencias base
- Documento fuente: [03_anexo_2_prop.docx](file:///C:/Users/jdsal/Documents/Programming-personal/docs/03_anexo_2_prop.docx)
- Extractos clave (resumen operacional):
  - “Plataforma digital integral basada en inteligencia artificial… que centralice… producción, control de inventarios, comercialización y relación con clientes.”
  - Objetivos específicos: estandarizar/digitalizar técnicas, análisis de tendencias/ventas en tiempo real, plataforma web (visibilidad + cursos/workshops), fidelización (encuestas + seguimiento), bases de protección de marca.

## Backlog priorizado (IA al final)
Convenciones:
- Prioridad: P0 (imprescindible), P1 (alto), P2 (medio), P3 (bajo)
- Estado: Pendiente | En progreso | Hecho | Bloqueado

| ID | Iniciativa | Prioridad | Estado | Entregable |
|---:|---|:---:|:---:|---|
| P0-01 | Autenticación + roles | P0 | Pendiente | Login, RBAC, auditoría mínima |
| P0-02 | Inventario base | P0 | Pendiente | Materiales, movimientos, stock y alertas |
| P0-03 | Producción base | P0 | Pendiente | Órdenes, etapas, tiempos, consumos |
| P0-04 | Trazabilidad mínima | P0 | Pendiente | Insumo→lote→producto→orden (ficha) |
| P0-05 | Catálogo + backoffice | P0 | Pendiente | CRUD productos/colecciones + publicación |
| P0-06 | Web cliente (catálogo + orden) | P0 | Pendiente | Navegación + carrito/orden (sin IA) |
| P0-07 | Postventa (encuestas) | P0 | Pendiente | Plantillas + disparadores + respuestas |
| P0-08 | Estrategia de pruebas completa | P0 | Pendiente | Regresión + E2E para backend y frontend |
| P0-09 | Dockerización backend | P0 | Pendiente | Dockerfile + variables + health endpoint |
| P0-10 | Empaquetado para cliente (Windows) | P0 | Pendiente | Generación de EXE instalable/portable |
| P1-01 | Base de conocimiento (técnicas) | P1 | Pendiente | Técnicas, pasos, archivos, búsqueda |
| P1-02 | Formación (cursos/workshops) | P1 | Pendiente | Cursos, lecciones, inscripciones |
| P1-03 | Reportes y dashboards (sin IA) | P1 | Pendiente | KPIs operacionales + exportaciones |
| P1-04 | CI (build + tests) | P1 | Pendiente | Pipeline por PR |
| P1-05 | Release en GitHub | P1 | Pendiente | Artefactos: jar, frontend build, exe |
| P2-01 | Integración con pagos | P2 | Pendiente | Proveedor definido + estados de pago |
| P2-02 | Envíos/transportadora | P2 | Pendiente | Tracking + estados logísticos |
| P2-03 | Multisede/bodega | P2 | Pendiente | Stock por ubicación |
| P3-01 | Protección de marca (gestión) | P3 | Pendiente | Checklist/documentos/estado de trámites |
| IA-01 | IA para analítica y recomendaciones | P3 | Pendiente | Módulo IA desacoplado (última fase) |
| IA-02 | Automatización de respuestas | P3 | Pendiente | Plantillas + canal + guardrails |
| IA-03 | Agentes para encuestas y seguimiento | P3 | Pendiente | Generación/segmentación supervisada |

## Notas de empaquetado (EXE)
- Enfoque recomendado: empaquetar **backend + frontend build** en una sola distribución.
- Opción preferida para Windows: `jpackage` (JDK) para generar instalador/EXE a partir del JAR.
- El backend puede servir el frontend compilado (assets estáticos) para un despliegue “todo en uno”.

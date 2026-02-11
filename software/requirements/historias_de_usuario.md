# KombaOS — Historias de usuario (sin IA en el MVP)

## Alcance base (antes de IA)
- Centralizar la operación textil/artesanal: producción, inventarios, trazabilidad, catálogo/ventas y relación con clientes.
- Digitalizar técnicas y saberes (base de conocimiento) y habilitar formación (cursos/workshops).
- Habilitar medición/analítica operacional (no “IA”), con reportes y dashboards.

## Roles (RBAC)
- Admin: configuración, usuarios, permisos, catálogos maestros.
- Operación/Taller: producción, consumos, inventarios, calidad.
- Artesano: consulta/carga de técnicas, participación en fichas y trazabilidad.
- Comercial: catálogo, precios, órdenes, clientes, campañas.
- Instructor: cursos, contenido, workshops, asistencia.
- Cliente: compra, postventa, encuestas, historial.

## Épica A — Autenticación, usuarios y permisos
### HU-A1: Inicio de sesión y sesión persistente
Como usuario, quiero iniciar sesión para acceder a mi portal según mi rol.
- Criterios:
  - Dado que tengo credenciales válidas, cuando inicio sesión, entonces accedo a una sesión con expiración.
  - Dado que mi usuario está deshabilitado, cuando intento iniciar sesión, entonces se rechaza el acceso.

### HU-A2: Administración de usuarios
Como admin, quiero crear y gestionar usuarios para controlar el acceso a la plataforma.
- Criterios:
  - Puedo crear usuario con rol y estado (activo/inactivo).
  - Puedo restablecer credenciales de forma segura.

## Épica B — Inventarios
### HU-B1: Catálogo de materiales e insumos
Como operación, quiero registrar materiales e insumos para controlar stock y costos.
- Criterios:
  - Puedo registrar material con unidad, proveedor, costo y atributos (certificación, origen).
  - Puedo listar/buscar/filtrar por proveedor, tipo, certificación.

### HU-B2: Movimientos de inventario
Como operación, quiero registrar entradas/salidas/ajustes para mantener stock actualizado y auditable.
- Criterios:
  - Los movimientos generan un saldo recalculable y trazable (quién/cuándo/por qué).
  - Se valida stock insuficiente en salidas (según regla definida).

### HU-B3: Alertas de stock mínimo
Como operación, quiero alertas de stock mínimo para evitar paradas de producción.
- Criterios:
  - Se configuran umbrales por material.
  - Se genera alerta cuando el stock cae por debajo del umbral.

### Backlog técnico (priorizado)
- P1: Implementar movimientos de inventario (IN/OUT/ADJUST) y cálculo de stock por material.
- P1: Exponer endpoints CRUD para movimientos y endpoint de stock por material.
- P1: Validación y manejo de errores consistente (DTO records + Bean Validation + Advice).
- P1: Pruebas backend (unit + integración/smoke) para flujos de inventario.
- P2: Mejorar UI para inventario (lista/alta de materiales, stock y movimientos) con estado tipado.
- P2: Pruebas frontend (render + mocks de fetch) para flujos mínimos.
- P3: Alertas de stock mínimo (modelo, reglas, endpoint, UI, tests).

## Épica C — Producción
### HU-C1: Órdenes de producción y etapas
Como operación, quiero crear órdenes de producción con etapas para planificar y controlar fabricación.
- Criterios:
  - Una orden tiene producto, cantidad, fechas, etapas/operaciones y estado.
  - Se registra avance por etapa y tiempos asociados.

### HU-C2: Consumo de materiales por orden
Como operación, quiero asociar consumos a la orden para costear y medir desperdicio.
- Criterios:
  - Los consumos descuentan inventario y quedan auditados.
  - Puedo registrar merma/desperdicio con causa.

### HU-C3: Control de calidad
Como operación, quiero registrar controles de calidad para reducir reprocesos.
- Criterios:
  - Se registran checklist/resultado por lote u orden.
  - Se clasifican no conformidades y acciones correctivas.

## Épica D — Trazabilidad
### HU-D1: Trazabilidad de insumo a producto
Como comercial/cliente, quiero trazabilidad para verificar origen/certificación y transparencia.
- Criterios:
  - Un producto/orden puede mostrar materiales/lotes/certificaciones vinculadas.
  - Se puede exportar una ficha de trazabilidad para compartir con el cliente.

## Épica E — Catálogo y ventas (web)
### HU-E1: Catálogo público
Como cliente, quiero ver el catálogo para conocer productos y sus historias.
- Criterios:
  - Puedo navegar por categorías/colecciones y ver detalle de producto.
  - Puedo ver atributos de sostenibilidad/trazabilidad cuando existan.

### HU-E2: Carrito y orden
Como cliente, quiero comprar en línea para hacer pedidos sin intermediación.
- Criterios:
  - Puedo crear una orden con ítems y datos de entrega.
  - El sistema registra el estado del pedido y su historial.

### HU-E3: Backoffice de productos y colecciones
Como comercial, quiero gestionar productos, precios y colecciones para publicar novedades.
- Criterios:
  - CRUD de productos con fotos, variantes y stock (si aplica).
  - Publicación/despublicación con control de visibilidad.

## Épica F — CRM y postventa (sin IA)
### HU-F1: Encuestas de satisfacción postventa
Como comercial, quiero enviar encuestas para medir satisfacción y mejorar el servicio.
- Criterios:
  - Se definen plantillas de encuesta y el evento disparador (p. ej. orden entregada).
  - El cliente puede responder desde un enlace sin fricción.

### HU-F2: Seguimiento de clientes
Como comercial, quiero ver historial del cliente para personalizar atención.
- Criterios:
  - Se visualizan órdenes, interacciones y resultados de encuestas.

## Épica G — Base de conocimiento (técnicas y saberes)
### HU-G1: Registro de técnicas
Como artesano/instructor, quiero documentar técnicas para preservar y transferir conocimiento.
- Criterios:
  - Una técnica tiene pasos, materiales, fotos/archivos y tags.
  - Se puede buscar por tags, materiales, nivel y región.

## Épica H — Formación (cursos/workshops)
### HU-H1: Publicación de cursos
Como instructor, quiero crear cursos para formar a comunidad y equipos.
- Criterios:
  - Curso con lecciones, recursos, cupos (si aplica) y calendario.
  - Control de visibilidad (público/privado).

### HU-H2: Inscripción y progreso
Como alumno, quiero inscribirme y ver mi progreso para completar la formación.
- Criterios:
  - Registro de progreso por lección y estado del curso.

## Épica I — Reportes y analítica (sin IA)
### HU-I1: Dashboards operacionales
Como admin/operación, quiero dashboards para tomar decisiones con datos reales.
- Criterios:
  - KPIs mínimos: ventas, tiempos de producción, desperdicio, rotación de inventario.
  - Exportación CSV/Excel (según necesidad).

## Estrategia de pruebas (backend y frontend)
### Backend (Spring Boot)
- Pruebas unitarias: lógica de dominio, validaciones, mapeos.
- Pruebas de integración: repositorios, migraciones, seguridad, endpoints.
- Pruebas de regresión: suite automatizada ejecutada en cada PR (unit + integración + contratos).
- Pruebas E2E (API): flujos críticos (login → orden → consumo inventario → trazabilidad → encuesta).

### Frontend (React + Vite)
- Pruebas unitarias: componentes y utilidades.
- Pruebas de integración: páginas y flujos con mocks de API.
- Pruebas de regresión: ejecución automatizada en cada PR.
- Pruebas E2E (navegador): flujos críticos (catálogo → carrito → orden → seguimiento → encuesta).

## Definición de Hecho (DoD)
- Funcionalidad cubierta por pruebas (según el tipo).
- Validación de seguridad básica (roles, controles de acceso, no exposición de datos).
- Manejo de errores consistente (API y UI).
- Registro de auditoría en operaciones críticas (inventario, órdenes, usuarios).

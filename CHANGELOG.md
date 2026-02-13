# Changelog

Este proyecto sigue un changelog manual en español.

## [Unreleased]

- Pendiente: órdenes de producción, consumos y trazabilidad por orden.
- Pendiente: autenticación y RBAC.
- Pendiente: catálogo público, carrito, órdenes y postventa.

## [0.1.1] - 2026-02-12

### Añadido

- Instalador Windows local para usuarios finales.
- Smoke test automático antes de publicar el instalador.
- Instrucciones simples incluidas en el paquete de instalación.
- Build de release asegurado desde el tag publicado.

## [0.1.0] - 2026-02-11

### Añadido

- Inventarios: catálogo de materiales, movimientos (IN/OUT/ADJUST) y cálculo de stock.
- Umbrales por material y alertas de stock mínimo.
- Catálogo (backoffice): CRUD de productos con precio, moneda y estado activo.
- Persistencia híbrida local/nube (archivos JSON en local; JPA en nube).
- Empaquetado Windows EXE (jpackage) con smoke test automatizado.
- Pruebas: integración backend, unit/integration frontend y E2E navegador (Playwright).

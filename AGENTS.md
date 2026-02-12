# Reglas de trabajo (GitFlow)

## Ramas

- develop: rama base para el trabajo diario
- master: solo para releases

## Flujo de trabajo

1. Crear ramas de feature desde develop con el formato feature/<ticket>-<resumen>
2. Abrir PR descriptivo hacia develop
3. Merge por PR luego de revisión
4. Cada milestone con valor listo genera release desde master

## Releases

- Se generan al cerrar un milestone con valor real para compartir
- El release se hace desde master

# Reglas de trabajo (GitFlow)

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

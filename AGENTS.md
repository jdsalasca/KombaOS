Objetivo
Estandarizar cómo los agentes contribuyen al repositorio de KombaOS con foco en estabilidad, usabilidad y releases confiables.

Alcance
- Aplica a frontend, backend, empaquetado y workflows.
- Prioriza integridad de datos, experiencia de usuario no técnico y releases reproducibles.

Flujo de trabajo
- Trabaja en feature branches desde develop.
- Todo cambio se integra a develop por PR con revisión.
- Mantén master exclusivo para releases y automatizaciones.
- Mantén cambios pequeños, coherentes y fáciles de revisar.

Diseño y arquitectura
- Modularidad por dominio, evita clases monolíticas.
- Clases con un objetivo claro y responsabilidad única.
- Métodos pequeños, legibles y con una sola intención.
- Prefiere composición sobre herencia, evita super clases.
- Depende de interfaces, no de implementaciones.
- Evita estado compartido global sin necesidad.

Calidad obligatoria
- Frontend: npm run lint, npm run test, npm run build.
- Backend: ./mvnw test.
- No mezclar cambios funcionales con formateo no solicitado.

Releases
- Todo release debe salir desde master.
- No generar tags manuales fuera del workflow de release.
- Verifica que el artefacto EXE quede adjunto a la Release.
- Si master se distancia de develop, planifica release y sincroniza.

UX para usuarios no técnicos
- Mensajes claros y en español.
- Validaciones preventivas en formularios.
- Estados vacíos guiados y acciones de un clic.

Comunicación
- Explica qué cambió y cómo validar.
- Reporta riesgos y rollback si aplica.
- Describe trade-offs de diseño cuando existan.

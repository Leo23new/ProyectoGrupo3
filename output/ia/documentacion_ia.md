# Documentación del sistema

**Origen:** IA (gemini-3.5-flash-lite)

### Propósito
Documentar el procesamiento de 109 registros del sistema académico (81 válidos, 28 errores), validando entidades y dependencias curriculares.

### Flujo de datos
Se ingieren 109 registros totales. Tras la validación, se descartaron 28 errores (4 identificadores incorrectos, 5 incompletos, 5 de formato, 5 fuera de rango, 5 duplicados y 4 referencias inconsistentes), distribuidos en `estudiantes.csv` (12), `asignaturas.csv` (5), `matriculas.csv` (7) y `prerrequisitos.csv` (4). Los 81 registros válidos alimentan las estructuras de datos.

### Estructuras utilizadas y por qué
*   **BST (Árbol Binario de Búsqueda):** Contiene exactamente 22 nodos (altura 8) para almacenar los 22 estudiantes válidos. Se usa por su eficiencia $O(\log n)$ en búsquedas de claves individuales.
*   **Grafo:** Modela las 12 asignaturas como vértices y los 13 prerrequisitos como aristas dirigidas. Permite analizar dependencias. Se detectó el ciclo concreto: `SIS201 -> SIS202 -> SIS301 -> SIS201`.

### Resultados de la ejecución
Se procesaron 109 registros (81 válidos, 28 errores). Se estructuraron 22 nodos en el BST y 12 vértices con 13 aristas en el grafo. El análisis detectó una anomalía crítica en el grafo: un ciclo de prerrequisitos (`SIS201 -> SIS202 -> SIS301 -> SIS201`), lo que impide una ordenación topológica válida para las 12 asignaturas y 34 matrículas.

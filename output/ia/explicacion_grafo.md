# Explicación del grafo de prerrequisitos

**Origen:** Explicación local (sin IA): Sin GEMINI_API_KEY ni ANTHROPIC_API_KEY: se usó el generador local (sin IA)

El grafo dirigido tiene 12 vértices (asignaturas) y 13 aristas (prerrequisitos); cada arista A -> B significa que A debe aprobarse antes que B. Asignaturas sin prerrequisitos (puntos de entrada): SIS101 MAT101 FIS101. Asignaturas sin dependientes (puntos finales): SIS302 SIS303. La asignatura que más materias habilita es SIS102 con 2 dependientes directos. Ciclos detectados: SIS201 -> SIS202 -> SIS301 -> SIS201. Un ciclo es una inconsistencia en la malla: ninguna asignatura del ciclo podría cursarse primero, por lo que los datos de prerrequisitos deben corregirse.

# Documentación del sistema

**Origen:** Documentación local (sin IA): Error HTTP 429: {
  "error": {
    "code": 429,
    "message": "You exceeded your current quota, please check your plan and billing details. For more information on this error, head to: https://aistudio.google.com/docs/rate-limits. To monitor your current usage, head to: https://ai.dev/rate-limit. \n* Quota exceede

## Propósito

SmartETL-DS + IA extrae datos académicos desde CSV, los valida, los carga limpios y los organiza con estructuras de datos propias para consultarlos.

## Resumen de la ejecución

```text
Registros procesados: 109 | válidos: 81 | inválidos: 28
Estudiantes válidos: 22 | Asignaturas: 12 | Matrículas: 34 | Prerrequisitos: 13
BST: 22 nodos, altura 8
Grafo: 12 vértices, 13 aristas
SIS201 -> SIS202 -> SIS301 -> SIS201
Total de registros procesados: 109
Registros válidos: 81
Total de errores: 28
Identificador incorrecto: 4
Datos incompletos: 5
Formato incorrecto: 5
Valor fuera de rango: 5
Registro duplicado: 5
Referencia inexistente / relación inconsistente: 4
Errores en estudiantes.csv: 12 de 34
Errores en asignaturas.csv: 5 de 17
Errores en matriculas.csv: 7 de 41
Errores en prerrequisitos.csv: 4 de 17

```


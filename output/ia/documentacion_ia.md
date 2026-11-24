# Documentación del sistema

**Origen:** Documentación local

## Propósito

SmartETL-DS procesa y consulta datos académicos.

## Flujo de datos

La extracción lee los CSV y encola los registros. La transformación valida y normaliza, y la carga escribe los datos limpios y los errores.

## Estructuras utilizadas

La cola conserva el orden de llegada, la pila reúne los errores y las listas enlazadas guardan los registros válidos. El BST indexa estudiantes por clave. El grafo dirigido representa prerrequisitos y se recorre con BFS (cola) o DFS (pila).

## Resultados de la ejecución

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
Origen del recorrido BFS: MAT101
Origen del recorrido DFS: SIS101

```


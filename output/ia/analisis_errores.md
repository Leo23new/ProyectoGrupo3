# Análisis de errores del ETL

**Origen:** Análisis local (sin IA): Sin GEMINI_API_KEY ni ANTHROPIC_API_KEY: se usó el generador local (sin IA)

Datos agregados usados:

```text
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

Se rechazaron 28 de 109 registros (25,7%). La categoría dominante es "Datos incompletos" con 5 casos. La fuente con mayor tasa de error es estudiantes.csv (35,3%). Recomendaciones: validar el formato del identificador en el origen, hacer obligatorios los campos clave, verificar rangos antes de exportar y controlar la unicidad de claves y las referencias entre archivos.

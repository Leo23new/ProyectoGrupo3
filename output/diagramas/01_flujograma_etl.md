# Flujograma: ETL completo

**Origen:** Generado con IA (gemini-3.5-flash-lite) a partir de datos reales de la ejecución

```mermaid
flowchart TD
    A(["Inicio"]) --> B["Extract: leer 4 archivos CSV y encolar 109 registros en Cola FIFO"]
    B --> C{"Cola vacia?"}
    C -->|"No"| D["Desencolar registro"]
    D --> E["Validar identificador campos obligatorios rangos duplicados y referencias"]
    E --> F{"Es valido?"}
    F -->|"Si (81 casos)"| G["Insertar en ListaEnlazada"]
    F -->|"No (28 casos)"| H["Apilar ErrorETL en Pila"]
    G --> C
    H --> C
    C -->|"Si"| I["Load: escribir CSV limpios errores_etl.txt y reporte_etl.txt"]
    I --> J(["Fin"])
```

## Descripción estructurada enviada a la IA

```text
Proceso ETL ejecutado con datos reales:
1. Extract: se leyeron 4 archivos CSV y se encolaron 109 registros en una Cola (FIFO).
2. Transform: mientras la cola no este vacia se desencola un registro y se valida (identificador, campos obligatorios, rangos, duplicados, referencias).
3. Si el registro es valido (81 casos) se inserta en una ListaEnlazada; si no (28 casos) se apila un ErrorETL en una Pila.
4. Load: al vaciarse la cola se escriben los CSV limpios, errores_etl.txt y reporte_etl.txt.
Errores por tipo: ID_INVALIDO=4 DATO_INCOMPLETO=5 FORMATO_INVALIDO=5 RANGO_INVALIDO=5 DUPLICADO=5 REFERENCIA_INVALIDA=4
```

# Flujograma: Pila (apilar y desapilar)

**Origen:** Generador local (sin IA): Error HTTP 429: {
  "error": {
    "code": 429,
    "message": "You exceeded your current quota, please check your plan and billing details. For more information on this error, head to: https://aistudio.google.com/docs/rate-limits. To monitor your current usage, head to: https://ai.dev/rate-limit. \n* Quota exceede

```mermaid
flowchart TD
    A(["Inicio"]) --> B{"¿Operación?"}
    B -->|"apilar x"| C["Crear nodo nuevo con x"]
    C --> D["nodo.siguiente = cima"]
    D --> E["cima = nodo"]
    E --> F["tamaño = tamaño + 1"]
    B -->|"desapilar"| G{"¿Pila vacía?"}
    G -->|"Sí"| H["Devolver null"]
    G -->|"No"| I["Guardar dato de la cima"]
    I --> J["cima = cima.siguiente"]
    J --> K["tamaño = tamaño - 1"]
    K --> L["Devolver el dato guardado"]
    F --> Z(["Fin"])
    H --> Z
    L --> Z
```

## Descripción estructurada enviada a la IA

```text
Pila LIFO con nodos enlazados y referencia cima.
apilar(x): crear nodo; nodo.siguiente = cima; cima = nodo.
desapilar(): si esta vacia devolver null; guardar dato de la cima; cima = cima.siguiente.
```

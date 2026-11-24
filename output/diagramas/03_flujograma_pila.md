# Flujograma: Pila (apilar y desapilar)

**Origen:** Generador local

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

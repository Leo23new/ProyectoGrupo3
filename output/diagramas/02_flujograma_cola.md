# Flujograma: Cola (encolar y desencolar)

**Origen:** Generador local

```mermaid
flowchart TD
    A(["Inicio"]) --> B{"¿Operación?"}
    B -->|"encolar x"| C["Crear nodo nuevo con x"]
    C --> D{"¿Cola vacía?"}
    D -->|"Sí"| E["frente = nodo y fin = nodo"]
    D -->|"No"| F["fin.siguiente = nodo y fin = nodo"]
    E --> G["tamaño = tamaño + 1"]
    F --> G
    B -->|"desencolar"| H{"¿Cola vacía?"}
    H -->|"Sí"| I["Devolver null"]
    H -->|"No"| J["Guardar dato del frente"]
    J --> K["frente = frente.siguiente"]
    K --> L{"¿frente es null?"}
    L -->|"Sí"| M["fin = null"]
    L -->|"No"| N["Mantener fin"]
    M --> O["tamaño = tamaño - 1"]
    N --> O
    O --> P["Devolver el dato guardado"]
    G --> Z(["Fin"])
    I --> Z
    P --> Z
```

## Descripción estructurada enviada a la IA

```text
Cola FIFO con nodos enlazados y referencias frente y fin.
encolar(x): crear nodo; si la cola esta vacia frente y fin apuntan al nodo; si no fin.siguiente = nodo y fin = nodo.
desencolar(): si esta vacia devolver null; guardar dato de frente; frente = frente.siguiente; si frente queda null entonces fin = null.
```

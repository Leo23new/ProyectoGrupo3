# Flujograma: Cola (encolar y desencolar)

**Origen:** Generador local (estructura fija: la IA se reserva para los flujogramas con datos reales)

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
    L -->|"No"| N["tamaño = tamaño - 1"]
    M --> N
    N --> O["Devolver el dato guardado"]
    G --> Z(["Fin"])
    I --> Z
    O --> Z
```

## Descripción estructurada enviada a la IA

```text
Cola FIFO con nodos enlazados y referencias frente y fin.
encolar(x): crear nodo; si la cola esta vacia frente y fin apuntan al nodo; si no fin.siguiente = nodo y fin = nodo.
desencolar(): si esta vacia devolver null; guardar dato de frente; frente = frente.siguiente; si frente queda null entonces fin = null.
```

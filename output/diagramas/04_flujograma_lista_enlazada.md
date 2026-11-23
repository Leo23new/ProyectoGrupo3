# Flujograma: Lista enlazada (insertar y buscar)

**Origen:** Generador local (estructura fija: la IA se reserva para los flujogramas con datos reales)

```mermaid
flowchart TD
    A(["Inicio"]) --> B{"¿Operación?"}
    B -->|"insertar x"| C["Crear nodo nuevo con x"]
    C --> D{"¿Lista vacía?"}
    D -->|"Sí"| E["cabeza = nodo y cola = nodo"]
    D -->|"No"| F["cola.siguiente = nodo y cola = nodo"]
    E --> G["tamaño = tamaño + 1"]
    F --> G
    B -->|"buscar"| H["actual = cabeza"]
    H --> I{"¿actual es null?"}
    I -->|"Sí"| J["Devolver null: no encontrado"]
    I -->|"No"| K{"¿actual cumple el criterio?"}
    K -->|"Sí"| L["Devolver actual.dato"]
    K -->|"No"| M["actual = actual.siguiente"]
    M --> I
    G --> Z(["Fin"])
    J --> Z
    L --> Z
```

## Descripción estructurada enviada a la IA

```text
Lista enlazada simple con referencias cabeza y cola.
insertar(x): crear nodo; si la lista esta vacia cabeza y cola apuntan al nodo; si no cola.siguiente = nodo y cola = nodo.
buscar(criterio): actual = cabeza; mientras actual no sea null: si cumple el criterio devolver el dato; avanzar actual = actual.siguiente; al terminar devolver null.
```

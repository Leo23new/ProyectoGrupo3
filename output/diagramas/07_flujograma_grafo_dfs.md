# Flujograma: Grafo + DFS

**Origen:** Generado con IA (gemini-3.5-flash-lite) a partir de datos reales de la ejecución

```mermaid
flowchart TD
    Inicio(["INICIO: DFS desde SIS101"]) --> Apilar1["APILAR: SIS101"]
    Apilar1 --> Visitar1["VISITAR: SIS101"]
    Visitar1 --> Apilar2["APILAR: SIS102"]
    Apilar2 --> Visitar2["VISITAR: SIS102"]
    Visitar2 --> Apilar3["APILAR: SIS302"]
    Apilar3 --> Apilar4["APILAR: SIS201"]
    Apilar4 --> Visitar4["VISITAR: SIS201"]
    Visitar4 --> Apilar5["APILAR: SIS203"]
    Apilar5 --> Apilar6["APILAR: SIS202"]
    Apilar6 --> Visitar6["VISITAR: SIS202"]
    Visitar6 --> Apilar7["APILAR: SIS301"]
    Apilar7 --> Visitar7["VISITAR: SIS301"]
    Visitar7 --> Visitar5["VISITAR: SIS203"]
    Visitar5 --> Apilar8["APILAR: SIS303"]
    Apilar8 --> Visitar8["VISITAR: SIS303"]
    Visitar8 --> Visitar3["VISITAR: SIS302"]
    Visitar3 --> Fin(["FIN: pila vacia; vertices visitados: 8"])
```

## Descripción estructurada enviada a la IA

```text
Recorrido DFS real sobre el grafo de prerrequisitos usando una pila. Eventos registrados en orden (con el contenido de la pila tras cada paso):
INICIO: DFS desde SIS101
APILAR: SIS101 | pila=[cima: SIS101]
VISITAR: SIS101 | pila=[cima: ]
APILAR: SIS102 | pila=[cima: SIS102]
VISITAR: SIS102 | pila=[cima: ]
APILAR: SIS302 | pila=[cima: SIS302]
APILAR: SIS201 | pila=[cima: SIS201 | SIS302]
VISITAR: SIS201 | pila=[cima: SIS302]
APILAR: SIS203 | pila=[cima: SIS203 | SIS302]
APILAR: SIS202 | pila=[cima: SIS202 | SIS203 | SIS302]
VISITAR: SIS202 | pila=[cima: SIS203 | SIS302]
APILAR: SIS301 | pila=[cima: SIS301 | SIS203 | SIS302]
VISITAR: SIS301 | pila=[cima: SIS203 | SIS302]
VISITAR: SIS203 | pila=[cima: SIS302]
APILAR: SIS303 | pila=[cima: SIS303 | SIS302]
VISITAR: SIS303 | pila=[cima: SIS302]
VISITAR: SIS302 | pila=[cima: ]
FIN: pila vacía; vértices visitados: 8
```

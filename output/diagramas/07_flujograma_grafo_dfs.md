# Flujograma: Grafo + DFS

**Origen:** Generador local (sin IA): Error HTTP 429: {
  "error": {
    "code": 429,
    "message": "You exceeded your current quota, please check your plan and billing details. For more information on this error, head to: https://aistudio.google.com/docs/rate-limits. To monitor your current usage, head to: https://ai.dev/rate-limit. \n* Quota exceede

```mermaid
flowchart TD
    N0(["Inicio: DFS desde SIS101<br/>Apilar SIS101<br/>pila=(cima: SIS101)"])
    N0 -->|"¿pila vacía? No"| N1["Desapilar y visitar SIS101<br/>Apilar SIS102<br/>pila=(cima: SIS102)"]
    N1 -->|"¿pila vacía? No"| N2["Desapilar y visitar SIS102<br/>Apilar SIS302, SIS201<br/>pila=(cima: SIS201 / SIS302)"]
    N2 -->|"¿pila vacía? No"| N3["Desapilar y visitar SIS201<br/>Apilar SIS203, SIS202<br/>pila=(cima: SIS202 / SIS203 / SIS302)"]
    N3 -->|"¿pila vacía? No"| N4["Desapilar y visitar SIS202<br/>Apilar SIS301<br/>pila=(cima: SIS301 / SIS203 / SIS302)"]
    N4 -->|"¿pila vacía? No"| N5["Desapilar y visitar SIS301<br/>pila=(cima: SIS203 / SIS302)"]
    N5 -->|"¿pila vacía? No"| N6["Desapilar y visitar SIS203<br/>Apilar SIS303<br/>pila=(cima: SIS303 / SIS302)"]
    N6 -->|"¿pila vacía? No"| N7["Desapilar y visitar SIS303<br/>pila=(cima: SIS302)"]
    N7 -->|"¿pila vacía? No"| N8["Desapilar y visitar SIS302<br/>pila=(cima: )"]
    N8 -->|"¿pila vacía? Sí"| N9(["Fin: pila vacía; vértices visitados: 8"])
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

# Flujograma: Grafo + BFS

**Origen:** Generador local (sin IA): Error HTTP 429: {
  "error": {
    "code": 429,
    "message": "You exceeded your current quota, please check your plan and billing details. For more information on this error, head to: https://aistudio.google.com/docs/rate-limits. To monitor your current usage, head to: https://ai.dev/rate-limit. \n* Quota exceede

```mermaid
flowchart TD
    N0(["Inicio: BFS desde SIS101<br/>Encolar SIS101<br/>cola=(frente: SIS101 :final)"])
    N0 -->|"¿cola vacía? No"| N1["Desencolar y visitar SIS101<br/>Encolar SIS102<br/>cola=(frente: SIS102 :final)"]
    N1 -->|"¿cola vacía? No"| N2["Desencolar y visitar SIS102<br/>Encolar SIS201, SIS302<br/>cola=(frente: SIS201, SIS302 :final)"]
    N2 -->|"¿cola vacía? No"| N3["Desencolar y visitar SIS201<br/>Encolar SIS202, SIS203<br/>cola=(frente: SIS302, SIS202, SIS203 :final)"]
    N3 -->|"¿cola vacía? No"| N4["Desencolar y visitar SIS302<br/>cola=(frente: SIS202, SIS203 :final)"]
    N4 -->|"¿cola vacía? No"| N5["Desencolar y visitar SIS202<br/>Encolar SIS301<br/>cola=(frente: SIS203, SIS301 :final)"]
    N5 -->|"¿cola vacía? No"| N6["Desencolar y visitar SIS203<br/>Encolar SIS303<br/>cola=(frente: SIS301, SIS303 :final)"]
    N6 -->|"¿cola vacía? No"| N7["Desencolar y visitar SIS301<br/>cola=(frente: SIS303 :final)"]
    N7 -->|"¿cola vacía? No"| N8["Desencolar y visitar SIS303<br/>cola=(frente:  :final)"]
    N8 -->|"¿cola vacía? Sí"| N9(["Fin: cola vacía; vértices visitados: 8"])
```

## Descripción estructurada enviada a la IA

```text
Recorrido BFS real sobre el grafo de prerrequisitos usando una cola. Eventos registrados en orden (con el contenido de la cola tras cada paso):
INICIO: BFS desde SIS101
ENCOLAR: SIS101 | cola=[frente: SIS101 :final]
DESENCOLAR: SIS101 | cola=[frente:  :final]
ENCOLAR: SIS102 | cola=[frente: SIS102 :final]
DESENCOLAR: SIS102 | cola=[frente:  :final]
ENCOLAR: SIS201 | cola=[frente: SIS201 :final]
ENCOLAR: SIS302 | cola=[frente: SIS201, SIS302 :final]
DESENCOLAR: SIS201 | cola=[frente: SIS302 :final]
ENCOLAR: SIS202 | cola=[frente: SIS302, SIS202 :final]
ENCOLAR: SIS203 | cola=[frente: SIS302, SIS202, SIS203 :final]
DESENCOLAR: SIS302 | cola=[frente: SIS202, SIS203 :final]
DESENCOLAR: SIS202 | cola=[frente: SIS203 :final]
ENCOLAR: SIS301 | cola=[frente: SIS203, SIS301 :final]
DESENCOLAR: SIS203 | cola=[frente: SIS301 :final]
ENCOLAR: SIS303 | cola=[frente: SIS301, SIS303 :final]
DESENCOLAR: SIS301 | cola=[frente: SIS303 :final]
DESENCOLAR: SIS303 | cola=[frente:  :final]
FIN: cola vacía; vértices visitados: 8
```

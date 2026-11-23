# Flujograma: Grafo + BFS

**Origen:** Generado con IA (gemini-3.5-flash-lite) a partir de datos reales de la ejecución

```mermaid
flowchart TD
    Inicio(["INICIO: BFS desde SIS101"]) --> E1["ENCOLAR: SIS101"]
    E1 --> D1["DESENCOLAR: SIS101"]
    D1 --> E2["ENCOLAR: SIS102"]
    E2 --> D2["DESENCOLAR: SIS102"]
    D2 --> E3["ENCOLAR: SIS201"]
    E3 --> E4["ENCOLAR: SIS302"]
    E4 --> D3["DESENCOLAR: SIS201"]
    D3 --> E5["ENCOLAR: SIS202"]
    E5 --> E6["ENCOLAR: SIS203"]
    E6 --> D4["DESENCOLAR: SIS302"]
    D4 --> D5["DESENCOLAR: SIS202"]
    D5 --> E7["ENCOLAR: SIS301"]
    E7 --> D6["DESENCOLAR: SIS203"]
    D6 --> E8["ENCOLAR: SIS303"]
    E8 --> D7["DESENCOLAR: SIS301"]
    D7 --> D8["DESENCOLAR: SIS303"]
    D8 --> Fin(["FIN: cola vacia vertices visitados 8"])
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

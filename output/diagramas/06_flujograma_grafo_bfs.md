# Flujograma: Grafo + BFS

**Origen:** Generador local

```mermaid
flowchart TD
    N0(["Inicio: BFS desde MAT101<br/>Encolar MAT101<br/>cola=(frente: MAT101 :final)"])
    N0 -->|"¿cola vacía? No"| N1["Desencolar y visitar MAT101<br/>Encolar MAT102, MAT201<br/>cola=(frente: MAT102, MAT201 :final)"]
    N1 -->|"¿cola vacía? No"| N2["Desencolar y visitar MAT102<br/>Encolar SIS201<br/>cola=(frente: MAT201, SIS201 :final)"]
    N2 -->|"¿cola vacía? No"| N3["Desencolar y visitar MAT201<br/>Encolar SIS303<br/>cola=(frente: SIS201, SIS303 :final)"]
    N3 -->|"¿cola vacía? No"| N4["Desencolar y visitar SIS201<br/>Encolar SIS202, SIS203<br/>cola=(frente: SIS303, SIS202, SIS203 :final)"]
    N4 -->|"¿cola vacía? No"| N5["Desencolar y visitar SIS303<br/>cola=(frente: SIS202, SIS203 :final)"]
    N5 -->|"¿cola vacía? No"| N6["Desencolar y visitar SIS202<br/>Encolar SIS301<br/>cola=(frente: SIS203, SIS301 :final)"]
    N6 -->|"¿cola vacía? No"| N7["Desencolar y visitar SIS203<br/>cola=(frente: SIS301 :final)"]
    N7 -->|"¿cola vacía? No"| N8["Desencolar y visitar SIS301<br/>cola=(frente:  :final)"]
    N8 -->|"¿cola vacía? Sí"| N9(["Fin: cola vacía; vértices visitados: 8"])
```

## Descripción estructurada enviada a la IA

```text
Recorrido BFS sobre el grafo de prerrequisitos usando una cola. Eventos registrados en orden:
INICIO: BFS desde MAT101
ENCOLAR: MAT101 | cola=[frente: MAT101 :final]
DESENCOLAR: MAT101 | cola=[frente:  :final]
ENCOLAR: MAT102 | cola=[frente: MAT102 :final]
ENCOLAR: MAT201 | cola=[frente: MAT102, MAT201 :final]
DESENCOLAR: MAT102 | cola=[frente: MAT201 :final]
ENCOLAR: SIS201 | cola=[frente: MAT201, SIS201 :final]
DESENCOLAR: MAT201 | cola=[frente: SIS201 :final]
ENCOLAR: SIS303 | cola=[frente: SIS201, SIS303 :final]
DESENCOLAR: SIS201 | cola=[frente: SIS303 :final]
ENCOLAR: SIS202 | cola=[frente: SIS303, SIS202 :final]
ENCOLAR: SIS203 | cola=[frente: SIS303, SIS202, SIS203 :final]
DESENCOLAR: SIS303 | cola=[frente: SIS202, SIS203 :final]
DESENCOLAR: SIS202 | cola=[frente: SIS203 :final]
ENCOLAR: SIS301 | cola=[frente: SIS203, SIS301 :final]
DESENCOLAR: SIS203 | cola=[frente: SIS301 :final]
DESENCOLAR: SIS301 | cola=[frente:  :final]
FIN: cola vacía; vértices visitados: 8
```

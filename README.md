# SmartETL-DS + IA — Grupo 3

**Universidad Técnica de Ambato · Facultad de Ingeniería en Sistemas, Electrónica e Industrial**
**Asignatura:** Estructuras de Datos · 3.er semestre, Ingeniería de Software · **Docente:** José Caiza · **Año:** 2026

Sistema ETL en Java que extrae datos académicos desde archivos CSV, los transforma y valida, los carga limpios
y los organiza con **estructuras de datos implementadas por el grupo** (lista, pila, cola, BST y grafo).
Sobre ese núcleo se agrega una capa de **Inteligencia Artificial** que genera flujogramas (Mermaid), explicaciones
y documentación a partir del estado real de las estructuras.

## Integrantes y responsabilidades

| Integrante | Módulos a su cargo |
|---|---|
| Edwin David Guamanquispe Guaman | `Cola`, `Extract`, `RegistroCrudo`, `RegistroEventos`, `GeneradorFlujograma`, `GeneradorPrompt`, integración de `Main` |
| Justin Alexander Guanoquiza Aguaguiña | `Nodo`, `ListaEnlazada`, `ListaSecuencial`, `ListaDoble`, `Transform`, paquete `model` |
| Everly Oseas Ortiz Yaucan | `Pila`, `ErrorETL`/`TipoError`, `Load`, reportes, `Pruebas`, informe y README |
| José Ernesto Sierra Mera | `NodoArbol`, `ArbolBST`, `Busqueda`, `Ordenamiento`, `Comparativa` |
| Walter Fernando Villacrés López | `Vertice`, `Grafo`, `BFS`, `DFS`, `ClienteIA`, `AnalizadorIA` |

Todos deben comprender el sistema completo: el reparto indica quién *lidera* cada módulo, no quién es el único que lo conoce.
El detalle de tareas, ramas y fechas está en [`docs/PLAN_TRABAJO.md`](docs/PLAN_TRABAJO.md).

## Arquitectura

```
CSV (data/) → EXTRACT → Cola<RegistroCrudo> → TRANSFORM ─┬→ ListaEnlazada<registro válido>
                                                          └→ Pila<ErrorETL>
                                  LOAD → output/*.csv, errores_etl.txt, reporte_etl.txt
                       datos limpios ─┬→ ArbolBST<Estudiante>  (índice por cédula)
                                      └→ Grafo<String> de prerrequisitos → BFS (Cola) / DFS (Pila)
                                                  ↓
                                   CAPA DE IA → flujogramas · explicaciones · documentación
```

```
src/
├── Main.java            menú de consola (34 opciones)
├── model/               Estudiante, Asignatura, Matricula, Prerrequisito, RegistroCrudo, ErrorETL, TipoError
├── estructuras/         Nodo, ListaSecuencial, ListaEnlazada, ListaDoble, Pila, Cola,
│                        NodoArbol, ArbolBST, Vertice, Grafo      (implementación propia, genéricos <T>)
├── etl/                 Extract, Transform, Load
├── algoritmos/          Busqueda, Ordenamiento, BFS, DFS, Comparativa
├── ia/                  ClienteIA, GeneradorPrompt, GeneradorFlujograma, AnalizadorIA, RegistroEventos
└── pruebas/             Pruebas (80 verificaciones, sin librerías externas)
data/                    estudiantes.csv, asignaturas.csv, matriculas.csv, prerrequisitos.csv
output/                  resultados generados (CSV limpios, reportes, diagramas, evidencias de IA)
docs/                    plan de trabajo, guía de Git, guía de estudio, informe
```

### Decisiones de diseño

- **Núcleo sin colecciones de Java.** Lista, pila, cola, BST y grafo usan nodos propios. De `java.util` solo se usan
  interfaces auxiliares (`Iterator`, `Comparator`, `Predicate`, `Function`), `Scanner` y `Files` para leer/escribir archivos.
- **Cola en Extract**: respeta el orden de llegada (FIFO) y permite que Transform procese un registro a la vez.
- **Pila de errores**: el último error detectado queda en la cima, listo para analizarse.
- **Orden de lectura** (estudiantes → asignaturas → matrículas → prerrequisitos): permite validar que una matrícula
  apunte a un estudiante y una asignatura que *sí* pasaron la validación.
- **BST** indexado por cédula. Convención de altura: árbol vacío = 0, un nodo = 1. No es AVL; se mide el desbalance
  comparando la altura real con la mínima teórica.
- **Grafo dirigido**: vértice = asignatura, arista `A → B` = "A es prerrequisito de B". Lista de vértices con lista de
  adyacencia en cada uno (ambas `ListaEnlazada`).
- **BFS** marca el vértice como visitado al encolarlo (así nunca entra dos veces a la cola). **DFS** es iterativo con `Pila`;
  la detección de ciclos es recursiva (usa la pila de llamadas).
- Solo se leen **CSV**. El dataset es **sintético** (no contiene datos personales reales).

## Cómo ejecutar (Java 21)

Desde la raíz del proyecto:

```bash
# Linux / macOS
./compilar.sh              # compila a la carpeta out/
java -cp out Main          # menú interactivo
java -cp out Main --demo   # recorre todo el flujo automáticamente
java -cp out pruebas.Pruebas
```

```bat
:: Windows (CMD)
compilar.bat
chcp 65001
java -Dstdout.encoding=UTF-8 -cp out Main
```

En VS Code: abrir la carpeta del proyecto, instalar *Extension Pack for Java* y ejecutar `Main.java`
(el directorio de trabajo debe ser la raíz, para que encuentre `data/` y `output/`).

Si se ven signos "?" en lugar de tildes, ejecutar con `-Dstdout.encoding=UTF-8`.

## Dataset de prueba

Contiene casos válidos e inválidos a propósito: identificadores mal formados, campos vacíos, formatos incorrectos,
valores fuera de rango, duplicados, referencias a estudiantes/asignaturas inexistentes y **un ciclo de prerrequisitos**
(`SIS201 → SIS202 → SIS301 → SIS201`) para demostrar la detección de ciclos.

| Fuente | Procesados | Válidos | Errores |
|---|---|---|---|
| estudiantes.csv | 34 | 22 | 12 |
| asignaturas.csv | 17 | 12 | 5 |
| matriculas.csv | 41 | 34 | 7 |
| prerrequisitos.csv | 17 | 13 | 4 |
| **Total** | **109** | **81** | **28** |

## Capa de IA

1. El programa **calcula primero** (estadísticas, alturas, ciclos, trazas de BFS/DFS) y arma una descripción estructurada.
2. Esa descripción se envía a **Claude** (API de Anthropic) para producir el flujograma en Mermaid o la explicación.
3. Si no hay API key o la llamada falla, se usa un **generador local** con la misma información y el archivo indica
   su origen (`IA` o `local`). El sistema funciona siempre.
4. Cada consulta queda registrada (prompt, respuesta y fecha) en `output/evidencias_ia/log_ia.md`.

Configurar la IA (opcional):

```bash
export ANTHROPIC_API_KEY="sk-ant-..."      # Windows CMD: set ANTHROPIC_API_KEY=sk-ant-...
export CLAUDE_MODEL="claude-sonnet-5-5"    # opcional
```

**Privacidad:** solo se envían datos sintéticos y resultados agregados; nunca información personal real.

Flujogramas generados en `output/diagramas/` (se pueden ver en GitHub, que renderiza Mermaid):

| # | Archivo | Tipo |
|---|---|---|
| 1 | `01_flujograma_etl` | dinámico (conteos reales de la ejecución) |
| 2 | `02_flujograma_cola` | estructura |
| 3 | `03_flujograma_pila` | estructura |
| 4 | `04_flujograma_lista_enlazada` | estructura |
| 5 | `05_flujograma_bst` | dinámico (camino real de una búsqueda) |
| 6 | `06_flujograma_grafo_bfs` | dinámico (pasos reales del recorrido) |
| 7 | `07_flujograma_grafo_dfs` | dinámico (pasos reales del recorrido) |

## Pruebas

`java -cp out pruebas.Pruebas` ejecuta 80 verificaciones: operaciones de cola, pila, lista, BST (inserción, eliminación de
hoja / un hijo / dos hijos, recorridos, degeneración), grafo (BFS, DFS, rutas, ciclos), ordenamientos y el ETL completo
sobre el dataset (conteos esperados por fuente y por tipo de error).

## Uso de IA en el desarrollo (declaración del grupo)

> **Completar con la verdad del grupo antes de la entrega.** El docente exige declarar qué parte fue generada con IA
> y qué parte desarrolló el equipo.

| Parte | Quién la implementó / adaptó | Apoyo de IA (qué y cómo se revisó) |
|---|---|---|
| Estructuras (cola, pila, listas, BST, grafo) | _completar_ | _completar_ |
| ETL (Extract, Transform, Load) | _completar_ | _completar_ |
| BFS, DFS, búsqueda y ordenamiento | _completar_ | _completar_ |
| Capa de IA y flujogramas | _completar_ | Flujogramas generados con Claude; prompts en `output/evidencias_ia/` |
| Informe y documentación | _completar_ | _completar_ |

## Trabajo colaborativo

Una rama por funcionalidad y Pull Requests hacia `main` (ver [`docs/GUIA_GIT.md`](docs/GUIA_GIT.md)).
Cada integrante debe tener commits propios y verificables.

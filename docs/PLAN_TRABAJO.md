# Plan de trabajo — Grupo 3 (SmartETL-DS + IA)

Hoy es **miércoles 18 de noviembre**; la defensa es el **viernes 4 de diciembre**. Quedan **16 días**.
Del 1 al 3 de diciembre no se agregan funciones nuevas: solo integración, pruebas, correcciones y documentación.

## Cómo se trabaja con el código de referencia

El zip trae una implementación de referencia completa y probada en la carpeta `referencia_src/`, que **Git ignora** (queda solo en la computadora de cada uno; en el repositorio solo está `src/`, que ustedes van llenando). Para que el trabajo sea **de ustedes** y puedan defenderlo:

1. **Leer** su módulo con la guía de estudio al lado (`docs/GUIA_ESTUDIO_GRUPO3.docx`).
2. **Trazar a mano** un ejemplo en papel (se pide en la defensa).
3. **Cerrar el archivo y reescribirlo** en su rama, con sus propias palabras, nombres y comentarios. Comparar con la referencia solo al final.
4. Ejecutar las pruebas (`java -cp out pruebas.Pruebas`) hasta que pasen.
5. Hacer **commits pequeños** con su usuario de GitHub y abrir un Pull Request.
6. Anotar en el README (sección "Uso de IA") qué parte fue apoyada por IA. **Es obligatorio y debe ser verdad.**

Regla del grupo: **nadie hace commit del código de otro integrante.** El docente revisa GitHub y pregunta en la defensa.

## Reparto

| # | Integrante | Rama principal | Archivos que lidera | Opciones del menú |
|---|---|---|---|---|
| 1 | Edwin Guamanquispe | `feature/cola-extract` | `Cola`, `Extract`, `RegistroCrudo`, `RegistroEventos`, `GeneradorFlujograma`, `GeneradorPrompt`; integra `Main` | 1, 2, 24-27 |
| 2 | Justin Guanoquiza | `feature/listas-transform` | `Nodo`, `ListaEnlazada`, `ListaSecuencial`, `ListaDoble`, `Transform`, paquete `model` | 3, 4, 7, 35 |
| 3 | Everly Ortiz | `feature/pila-load` | `Pila`, `ErrorETL`, `TipoError`, `Load`, `Pruebas`; informe y README | 5, 6, 32, 33 |
| 4 | José Sierra | `feature/bst-algoritmos` | `NodoArbol`, `ArbolBST`, `Busqueda`, `Ordenamiento`, `Comparativa` | 8-17 |
| 5 | Walter Villacrés | `feature/grafo-ia` | `Vertice`, `Grafo`, `BFS`, `DFS`, `ClienteIA`, `AnalizadorIA` | 18-23, 28-31 |

El orden de dependencias importa: **`Nodo`/`ListaEnlazada` (Justin), `Cola` (Edwin) y `Pila` (Everly) deben estar primero**, porque casi todo
lo demás las usa. Los tres hacen esas piezas pequeñas el día 1.

## Calendario

| Fechas | Fase | Qué se hace | Quién | Hito |
|---|---|---|---|---|
| **Mié 18** | 0. Preparación | Reunión de 30 min. Instalar JDK 21, VS Code (Extension Pack for Java) y Git. Edwin sube el esqueleto inicial a `main` (README, `data/`, `compilar.*`, `.gitignore`). Todos clonan y crean su rama. | Todos | Repo listo, 5 ramas creadas |
| **Mié 18 – Vie 20** | 1. Estructuras base | `Nodo` + `ListaEnlazada` (Justin), `Cola` (Edwin), `Pila` (Everly), `NodoArbol` + `ArbolBST` (José), `Vertice` + `Grafo` (Walter). Cada uno con sus pruebas pasando. | Todos | 5 Pull Requests integrados |
| **Sáb 21 – Mar 24** | 2. ETL | `model` y `Transform` (Justin), `Extract` (Edwin), `Load` y reportes (Everly). Probar con `data/`. | Justin, Edwin, Everly | ETL completo (Hito 2-3) |
| **Sáb 21 – Mar 24** | 2. En paralelo | `Busqueda`, `Ordenamiento` (José); `BFS`, `DFS` (Walter). | José, Walter | Algoritmos con pruebas |
| **Mar 24 – Vie 27** | 3. BST y grafo | Opciones 8-17 del menú (José); opciones 18-23 (Walter); integrar `Main`. | José, Walter, Edwin | Hitos 4A y 4B |
| **Vie 27 – Lun 30** | 4. IA | `ClienteIA`, `AnalizadorIA` (Walter); `GeneradorFlujograma` y prompts (Edwin); generar los 7 flujogramas; guardar evidencias (prompts y respuestas). Everly avanza el informe. | Walter, Edwin, Everly | Hito 5 (IA) |
| **Mar 1 – Jue 3 dic** | 5. Integración | Sin funciones nuevas. Pruebas completas, README final, informe, ensayo de defensa (cada uno explica el módulo de otro). | Todos | Release final |
| **Vie 4 dic** | Defensa | Demo del sistema + preguntas individuales. | Todos | Entrega |

## Detalle por integrante

### 1. Edwin — Cola, Extract e integración
- **Tareas:** `Cola<T>` (encolar, desencolar, frente, estaVacia, tamanio, mostrar) → `RegistroCrudo` → `Extract` (lee los 4 CSV y encola cada línea) → `RegistroEventos` → `GeneradorPrompt` → `GeneradorFlujograma`.
- **Commits sugeridos:** "Agrega Cola genérica con nodos enlazados", "Agrega RegistroCrudo", "Implementa Extract con lectura de CSV", "Agrega RegistroEventos", "Genera flujogramas locales en Mermaid", "Integra opciones de IA al menú".
- **Listo cuando:** `Extract` encola 109 registros y los flujogramas 1-7 se generan en `output/diagramas/`.
- **También:** revisa los Pull Requests de los demás y mantiene `main` compilando.

### 2. Justin — Listas, Transform y modelo
- **Tareas:** `Nodo<T>` → `ListaEnlazada<T>` → `ListaSecuencial<T>` → `ListaDoble<T>` → clases de `model` → `Transform` (validar identificador, campos obligatorios, rangos, duplicados, referencias; normalizar texto; contar errores).
- **Commits sugeridos:** "Agrega Nodo y ListaEnlazada", "Agrega ListaSecuencial", "Agrega ListaDoble para historial", "Agrega clases del modelo", "Implementa validaciones de Transform", "Agrega normalización de texto y estadísticas".
- **Listo cuando:** de los 109 registros salen 81 válidos y 28 errores, con los conteos por tipo de la tabla del README.

### 3. Everly — Pila, Load, reportes, pruebas e informe
- **Tareas:** `Pila<T>` → `TipoError` y `ErrorETL` → `Load` (CSV limpios, `errores_etl.txt`, `reporte_etl.txt`) → `Pruebas` → informe en Word.
- **Commits sugeridos:** "Agrega Pila genérica", "Agrega ErrorETL y TipoError", "Implementa Load y reportes", "Agrega pruebas automáticas", "Agrega borrador del informe".
- **Listo cuando:** `output/` se llena con los archivos y `pruebas.Pruebas` muestra 0 fallos.

### 4. José — BST, búsqueda y ordenamiento
- **Tareas:** `NodoArbol` → `ArbolBST` (insertar, buscar, eliminar con los 3 casos, inOrden, preOrden, postOrden, altura, contarNodos) → `Busqueda` → `Ordenamiento` (burbuja, inserción, selección) → `Comparativa`.
- **Commits sugeridos:** "Agrega ArbolBST con insertar y buscar", "Agrega eliminar con tres casos", "Agrega recorridos", "Agrega búsqueda secuencial y binaria", "Agrega algoritmos de ordenamiento", "Agrega comparativa de tiempos".
- **Listo cuando:** inOrden devuelve cédulas ordenadas y la comparativa muestra que el BST necesita muchas menos comparaciones.

### 5. Walter — Grafo, BFS/DFS e IA
- **Tareas:** `Vertice` → `Grafo` → `BFS` (con `Cola`) → `DFS` (con `Pila`) → rutas, dependientes y ciclos → `ClienteIA` → `AnalizadorIA`.
- **Commits sugeridos:** "Agrega Vertice y Grafo con listas de adyacencia", "Implementa BFS con Cola", "Implementa DFS con Pila", "Agrega búsqueda de ruta", "Detecta ciclos", "Agrega ClienteIA", "Agrega explicaciones con IA".
- **Listo cuando:** detecta el ciclo `SIS201 → SIS202 → SIS301 → SIS201` y obtiene la ruta `SIS101 → SIS303`.
- **Ojo:** es la carga más pesada. Si el 27 de noviembre va atrasado, Edwin toma `AnalizadorIA`.

## Qué debe saber cada uno antes de empezar

**Todos:** Git (clone, branch, add, commit, pull, push, Pull Request), POO en Java (clases, encapsulamiento), genéricos `<T>`, compilar y ejecutar desde VS Code.

| Integrante | Conceptos clave |
|---|---|
| Edwin | FIFO, referencias `frente`/`fin`, `Files.readAllLines`, formato CSV, Mermaid básico |
| Justin | Nodos y referencias, inserción/eliminación en lista enlazada, expresiones regulares básicas (`\\d{10}`), `try/catch` con `parseInt` |
| Everly | LIFO, `cima`, escritura de archivos, `StringBuilder`, redacción técnica |
| José | Recursión, propiedad BST, los 3 casos de eliminación, notación O(n), O(log n), O(n²), Bubble/Insertion/Selection |
| Walter | Grafos dirigidos, lista de adyacencia, BFS vs DFS, ciclos, peticiones HTTP, JSON básico |

La **guía de estudio** (`docs/GUIA_ESTUDIO_GRUPO3.docx`) trae, para cada uno, qué leer, ejercicios para trazar a mano y las preguntas probables de la defensa con sus respuestas.

## Reuniones

- **Lun, mié y vie, 20 minutos** (por llamada o presencial): cada uno dice qué terminó, qué le falta y qué lo bloquea.
- **Cada Pull Request** lo revisa otro integrante antes de integrarlo a `main`.
- **Ensayo de defensa (2 y 3 de diciembre):** el docente elige una estructura al azar y pide explicarla, trazarla y modificarla. Practiquen que **cada uno explique el módulo de otro**.

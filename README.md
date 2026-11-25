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

### Interfaz web local (opcional)

```bash
java -cp out Main --web          # luego abrir http://127.0.0.1:8080
java -cp out Main --web 8081     # otro puerto si el 8080 está ocupado
```

Página local (servidor incluido en Java, sin dependencias) con un botón por cada opción del menú. Solo escucha en
`127.0.0.1`: únicamente se puede abrir desde el mismo computador. En **Modo guiado**, complete la extracción,
la transformación y la construcción del grafo. Abra **37. Visualizar grafo** para seleccionar una asignatura,
ver sus conexiones y avanzar o retroceder por los pasos de BFS o DFS dentro de la misma página.
El grafo funciona sin internet. Con `GEMINI_API_KEY` configurada en la terminal que inicia Java, use
**Explicar con Gemini** para solicitar una explicación de la traza del recorrido.

La opción **31. Chat guiado y documentación** permite elegir y ejecutar las etapas de extracción,
transformación, carga, BST, grafo, BFS y DFS. Puedes pedir una recomendación en el chat en cualquier etapa,
elegir la dirección del grafo y los orígenes de los recorridos, o dejar etapas pendientes para el cierre.
En **Documentación → Generar documentación**, el sistema completa lo pendiente y muestra el grafo interactivo,
los recorridos elegidos, los siete flujogramas, el árbol BST y los reportes dentro de la página.
La vista visual funciona sin internet. Con la clave configurada, Gemini responde en el chat y puede redactar el texto final.
Los archivos de documentación y los diagramas también se guardan en `output/`.

Los flujogramas de las opciones de IA se dibujan con Mermaid (requiere
internet para cargar la librería; sin internet se muestra el código). Con la IA activa, evitar el botón de DEMO completa:
hace muchas llamadas seguidas y el nivel gratuito limita las peticiones por minuto; es mejor ejecutar las opciones 24 a 31
una por una.

## Ejecutar en GitHub Codespaces

1. En GitHub, selecciona la rama `main` y abre **Code → Codespaces → Create codespace on main**.
2. Codespaces prepara Java 21, compila el proyecto y arranca SmartETL en el puerto `8080`. La vista previa se abre en el editor. También puedes abrir **Ports → SmartETL web → Open in Browser**.

La vista previa usa un puerto privado y está disponible mientras el Codespace esté activo. Si ya tenías uno abierto, actualiza la rama y ejecuta **Codespaces: Rebuild Container** desde la paleta de comandos. El registro de arranque queda en `out/web.log`.

## Despliegue web desde GitHub

La interfaz HTML y la API se sirven desde el mismo proceso Java. GitHub Pages ofrece alojamiento estático, por lo que el ETL y las vistas interactivas necesitan un servicio que ejecute Java. El `Dockerfile` prepara la aplicación con Java 21, los CSV de ejemplo y la interfaz web.

1. Sube a GitHub la rama que contiene `src/Servidor.java`, `web/index.html` y `Dockerfile`. En **Actions → Verificar SmartETL** se construye la imagen, se ejecutan las pruebas de Java y se verifica el arranque web.
2. En [Render](https://dashboard.render.com/), elige **New → Web Service → Git Provider**, conecta GitHub y selecciona este repositorio.
3. Selecciona la rama del paso 1, el lenguaje **Docker** y el plan **Free** para la demostración. Deja **Root Directory** vacío y usa `./Dockerfile`. En **Advanced**, puedes indicar `/` como ruta de verificación de salud.
4. Pulsa **Deploy Web Service**. Cuando Render indique **Live**, abre la URL `https://…onrender.com` y añade ese enlace a este README para que se pueda abrir desde GitHub. Los nuevos cambios en la rama vinculada se pueden desplegar automáticamente.

La demo usa los CSV sintéticos del repositorio y funciona con la guía local sin claves de IA. Para una demo pública, deja `GEMINI_API_KEY` y `ANTHROPIC_API_KEY` sin configurar. Si habilitas un proveedor de IA para una demo con acceso controlado, guarda la clave en las variables de entorno del servicio.

Los visitantes comparten el estado del proceso. En el plan gratuito, Render suspende el servicio tras un periodo sin tráfico y puede perder los archivos generados en `output/` al reiniciar. Consulta la [guía de servicios web](https://render.com/docs/web-services) y los [límites del plan gratuito](https://render.com/docs/free).

Para probar el contenedor en tu equipo con Docker instalado, ejecuta desde la raíz del repositorio y abre `http://localhost:8080`.

```bash
docker build -t smartetl .
docker run --rm -p 127.0.0.1:8080:10000 smartetl
```

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
2. Esa descripción se envía a **Google Gemini** (API con nivel gratuito) para producir el flujograma en Mermaid o la
   explicación. El cliente también admite Claude (Anthropic) como alternativa.
3. Si no hay API key o la llamada falla, se usa un **generador local** con la misma información y el archivo indica
   su origen (`IA` o `local`). El sistema funciona siempre.
4. Cada consulta queda registrada (proveedor, modelo, prompt, respuesta y fecha) en `output/evidencias_ia/log_ia.md`.
5. Si Gemini responde con límite de peticiones (HTTP 429), el cliente espera y reintenta; además separa las llamadas
   unos segundos entre sí.

Configurar la IA (opcional). La clave **nunca** se escribe en el código ni se sube a GitHub; se define como variable de
entorno en la terminal donde se ejecuta el programa:

```powershell
# Windows (PowerShell)
$env:GEMINI_API_KEY="tu-clave"
java -cp out Main --web                 # use Main sin --web para el menú de consola
```

```bash
# Linux / macOS
export GEMINI_API_KEY="tu-clave"
java -cp out Main --web
# Alternativa con Claude:  export ANTHROPIC_API_KEY="..."  (y opcional CLAUDE_MODEL)
```

La clave se obtiene gratis en Google AI Studio (`aistudio.google.com/apikey`). Si existen las dos claves, se usa Gemini.

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
| Capa de IA y flujogramas | _completar_ | Flujogramas, explicaciones y análisis de errores generados con Gemini; prompts y respuestas en `output/evidencias_ia/log_ia.md` |
| Informe y documentación | _completar_ | _completar_ |

## Trabajo colaborativo

Una rama por funcionalidad y Pull Requests hacia `main` (ver [`docs/GUIA_GIT.md`](docs/GUIA_GIT.md)).
Cada integrante debe tener commits propios y verificables.

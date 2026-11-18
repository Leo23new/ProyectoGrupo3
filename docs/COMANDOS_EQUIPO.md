# Comandos del equipo (Grupo 3)

Terminal de VS Code: menú **Terminal → New Terminal**. Los comandos sirven en PowerShell, CMD, Git Bash, Mac y Linux.
Compilar: `.\compilar.bat` (Windows) o `./compilar.sh` (Mac/Linux). Ver el sistema de referencia funcionando: `.\ejecutar_referencia.bat --demo` o `./ejecutar_referencia.sh --demo`.

## PARTE 1. Dueño del repositorio (Leo23new): sube el esqueleto

Descomprimir `ProyectoGrupo3_para_GitHub.zip`, abrir esa carpeta en VS Code (File → Open Folder) y, en la terminal:

```
git init -b main
git remote add origin https://github.com/Leo23new/ProyectoGrupo3.git

git add .gitignore README.md compilar.sh compilar.bat ejecutar_referencia.sh ejecutar_referencia.bat
git commit -m "Agrega README, .gitignore y scripts de compilacion"

git add data/
git commit -m "Agrega dataset sintetico con casos validos e invalidos"

git add docs/
git commit -m "Agrega plan de trabajo, guia de Git y documentos del proyecto"

git add src/estructuras/Nodo.java
git commit -m "Agrega Nodo generico base para las estructuras enlazadas"

git push -u origin main
```

Comprobar: `git log --oneline` debe mostrar 4 commits y `git status` no debe mostrar `referencia_src` (está ignorada).

Si el `push` es rechazado porque el repo ya tenía un README creado por GitHub:

```
git pull origin main --allow-unrelated-histories
git checkout --ours README.md
git add README.md
git commit --no-edit
git push -u origin main
```

Luego, en GitHub (web): **Settings → Collaborators → Add people** y agregar a los otros 4 integrantes. Después compartir el zip con todos (de ahí sale la carpeta `referencia_src`).

Para integrar el trabajo de los demás: en GitHub abrir cada Pull Request → revisar **Files changed** → **Merge pull request**. Después, en la terminal:

```
git checkout main
git pull
```

## PARTE 2. Todos: preparación (una sola vez)

```
git clone https://github.com/Leo23new/ProyectoGrupo3.git
cd ProyectoGrupo3
code .
```

Arrastrar la carpeta `referencia_src` (del zip) a la raíz del proyecto en VS Code. Git la ignora, no se sube.

## PARTE 3. Plantilla para cada rama

```
git checkout main
git pull
git checkout -b feature/NOMBRE-DE-LA-RAMA

mkdir src/CARPETA                      (solo si no existe; si da error de "ya existe", ignorarlo)
cp referencia_src/CARPETA/Archivo.java src/CARPETA/Archivo.java
# ABRIR el archivo, leerlo, trazarlo en papel y reescribirlo/adaptarlo con sus palabras

.\compilar.bat                         (Mac/Linux: ./compilar.sh)  -> debe decir "Compilacion correcta"
git add src/CARPETA/Archivo.java
git commit -m "Mensaje claro de lo que agrego"
git pull
git push -u origin feature/NOMBRE-DE-LA-RAMA
```

Luego, en GitHub: **Compare & pull request** → **Create pull request** → avisar al grupo para que lo revisen y el dueño lo integre.
Antes de empezar una rama nueva: `git checkout main` y `git pull`. Nunca trabajar directo en `main`.

> Recuerden: el archivo de `referencia_src` es un punto de partida. Deben entenderlo y adaptarlo, y declarar en el README qué parte fue apoyada por IA.

## PARTE 4. Comandos por integrante

### Edwin: Cola, RegistroEventos, Extract, prompts, flujogramas, Main

```
# Ronda 1 (esperar el merge de ListaEnlazada de Justin para RegistroEventos)
git checkout main && git pull && git checkout -b feature/cola
cp referencia_src/estructuras/Cola.java src/estructuras/Cola.java
git add src/estructuras/Cola.java
git commit -m "Agrega Cola generica con nodos enlazados"
git push -u origin feature/cola

git checkout main && git pull && git checkout -b feature/registro-eventos
mkdir src/ia
mkdir src/model
cp referencia_src/ia/RegistroEventos.java src/ia/RegistroEventos.java
git add src/ia/RegistroEventos.java
git commit -m "Agrega RegistroEventos para la bitacora de ejecucion"
cp referencia_src/model/RegistroCrudo.java src/model/RegistroCrudo.java
git add src/model/RegistroCrudo.java
git commit -m "Agrega RegistroCrudo para las lineas sin validar"
git push -u origin feature/registro-eventos

# Ronda 2 (requiere Cola, RegistroEventos y RegistroCrudo ya integrados)
git checkout main && git pull && git checkout -b feature/extract
mkdir src/etl
cp referencia_src/etl/Extract.java src/etl/Extract.java
git add src/etl/Extract.java
git commit -m "Implementa Extract con lectura de CSV y cola de registros"
git push -u origin feature/extract

# Ronda 4
git checkout main && git pull && git checkout -b feature/prompts
cp referencia_src/ia/GeneradorPrompt.java src/ia/GeneradorPrompt.java
git add src/ia/GeneradorPrompt.java
git commit -m "Agrega GeneradorPrompt para las consultas a la IA"
git push -u origin feature/prompts

# Ronda 5 (requiere Transform, Load, ArbolBST, ClienteIA y GeneradorPrompt integrados)
git checkout main && git pull && git checkout -b feature/flujogramas
cp referencia_src/ia/GeneradorFlujograma.java src/ia/GeneradorFlujograma.java
git add src/ia/GeneradorFlujograma.java
git commit -m "Agrega generador de flujogramas Mermaid dinamicos"
git push -u origin feature/flujogramas

# Ronda 6 (integracion final)
git checkout main && git pull && git checkout -b feature/menu-main
cp referencia_src/Main.java src/Main.java
git add src/Main.java
git commit -m "Integra el menu principal con todas las opciones"
git push -u origin feature/menu-main
```

### Justin: ListaEnlazada (¡PRIMERO!), modelo, listas, Transform

```
# Ronda 1: URGENTE, de esto dependen casi todos. Subirla hoy y avisar al grupo.
git checkout main && git pull && git checkout -b feature/lista-enlazada
cp referencia_src/estructuras/ListaEnlazada.java src/estructuras/ListaEnlazada.java
git add src/estructuras/ListaEnlazada.java
git commit -m "Agrega ListaEnlazada generica con insercion, busqueda y eliminacion"
git push -u origin feature/lista-enlazada

# Ronda 2
git checkout main && git pull && git checkout -b feature/modelo
mkdir src/model
cp referencia_src/model/Estudiante.java src/model/Estudiante.java
git add src/model/Estudiante.java
git commit -m "Agrega clase Estudiante"
cp referencia_src/model/Asignatura.java src/model/Asignatura.java
git add src/model/Asignatura.java
git commit -m "Agrega clase Asignatura"
cp referencia_src/model/Matricula.java src/model/Matricula.java
git add src/model/Matricula.java
git commit -m "Agrega clase Matricula"
cp referencia_src/model/Prerrequisito.java src/model/Prerrequisito.java
git add src/model/Prerrequisito.java
git commit -m "Agrega clase Prerrequisito"
git push -u origin feature/modelo

git checkout main && git pull && git checkout -b feature/listas-extra
cp referencia_src/estructuras/ListaSecuencial.java src/estructuras/ListaSecuencial.java
git add src/estructuras/ListaSecuencial.java
git commit -m "Agrega ListaSecuencial con crecimiento dinamico"
cp referencia_src/estructuras/ListaDoble.java src/estructuras/ListaDoble.java
git add src/estructuras/ListaDoble.java
git commit -m "Agrega ListaDoble para el historial del menu"
git push -u origin feature/listas-extra

# Ronda 3 (requiere modelo, Cola, Pila, TipoError, ErrorETL, RegistroCrudo y RegistroEventos integrados)
git checkout main && git pull && git checkout -b feature/transform
mkdir src/etl
cp referencia_src/etl/Transform.java src/etl/Transform.java
git add src/etl/Transform.java
git commit -m "Implementa Transform con validaciones, normalizacion y pila de errores"
git push -u origin feature/transform
```

### Everly: Pila, errores, Load, Pruebas

```
# Ronda 1
git checkout main && git pull && git checkout -b feature/pila
cp referencia_src/estructuras/Pila.java src/estructuras/Pila.java
git add src/estructuras/Pila.java
git commit -m "Agrega Pila generica con nodos enlazados"
git push -u origin feature/pila

# Ronda 2
git checkout main && git pull && git checkout -b feature/errores
mkdir src/model
cp referencia_src/model/TipoError.java src/model/TipoError.java
git add src/model/TipoError.java
git commit -m "Agrega TipoError con las categorias de error del ETL"
cp referencia_src/model/ErrorETL.java src/model/ErrorETL.java
git add src/model/ErrorETL.java
git commit -m "Agrega ErrorETL para la pila de errores"
git push -u origin feature/errores

# Ronda 3 (requiere Transform y el modelo integrados)
git checkout main && git pull && git checkout -b feature/load
mkdir src/etl
cp referencia_src/etl/Load.java src/etl/Load.java
git add src/etl/Load.java
git commit -m "Implementa Load: CSV limpios, errores y reporte ETL"
git push -u origin feature/load

# Ronda 6 (con todo integrado)
git checkout main && git pull && git checkout -b feature/pruebas
mkdir src/pruebas
cp referencia_src/pruebas/Pruebas.java src/pruebas/Pruebas.java
git add src/pruebas/Pruebas.java
git commit -m "Agrega pruebas automaticas de estructuras, algoritmos y ETL"
git push -u origin feature/pruebas
# Ejecutar: .\compilar.bat  y luego  java -cp out pruebas.Pruebas
```

### José: NodoArbol, ArbolBST, búsqueda, ordenamiento, comparativa

```
# Ronda 1 (esperar el merge de ListaEnlazada de Justin)
git checkout main && git pull && git checkout -b feature/bst
cp referencia_src/estructuras/NodoArbol.java src/estructuras/NodoArbol.java
git add src/estructuras/NodoArbol.java
git commit -m "Agrega NodoArbol"
cp referencia_src/estructuras/ArbolBST.java src/estructuras/ArbolBST.java
git add src/estructuras/ArbolBST.java
git commit -m "Agrega ArbolBST con insertar, buscar, eliminar, recorridos y altura"
git push -u origin feature/bst

# Ronda 3 (requiere modelo y ListaSecuencial integrados)
git checkout main && git pull && git checkout -b feature/busqueda-ordenamiento
mkdir src/algoritmos
cp referencia_src/algoritmos/Busqueda.java src/algoritmos/Busqueda.java
git add src/algoritmos/Busqueda.java
git commit -m "Agrega busqueda secuencial y binaria"
cp referencia_src/algoritmos/Ordenamiento.java src/algoritmos/Ordenamiento.java
git add src/algoritmos/Ordenamiento.java
git commit -m "Agrega ordenamiento burbuja, insercion y seleccion"
git push -u origin feature/busqueda-ordenamiento

# Ronda 4
git checkout main && git pull && git checkout -b feature/comparativa
cp referencia_src/algoritmos/Comparativa.java src/algoritmos/Comparativa.java
git add src/algoritmos/Comparativa.java
git commit -m "Agrega comparativa de tiempos de busqueda y ordenamiento"
git push -u origin feature/comparativa
```

### Walter: Vertice, Grafo, BFS, DFS, ClienteIA, AnalizadorIA

```
# Ronda 1 (esperar el merge de ListaEnlazada de Justin)
git checkout main && git pull && git checkout -b feature/grafo
cp referencia_src/estructuras/Vertice.java src/estructuras/Vertice.java
git add src/estructuras/Vertice.java
git commit -m "Agrega Vertice con lista de adyacencia"
cp referencia_src/estructuras/Grafo.java src/estructuras/Grafo.java
git add src/estructuras/Grafo.java
git commit -m "Agrega Grafo dirigido con listas de adyacencia"
git push -u origin feature/grafo

# Ronda 3 (requiere RegistroEventos, ListaSecuencial, Cola y Pila integrados)
git checkout main && git pull && git checkout -b feature/bfs-dfs
mkdir src/algoritmos
cp referencia_src/algoritmos/BFS.java src/algoritmos/BFS.java
git add src/algoritmos/BFS.java
git commit -m "Implementa BFS con Cola, rutas y dependientes"
cp referencia_src/algoritmos/DFS.java src/algoritmos/DFS.java
git add src/algoritmos/DFS.java
git commit -m "Implementa DFS con Pila y deteccion de ciclos"
git push -u origin feature/bfs-dfs

# Ronda 4
git checkout main && git pull && git checkout -b feature/cliente-ia
mkdir src/ia
cp referencia_src/ia/ClienteIA.java src/ia/ClienteIA.java
git add src/ia/ClienteIA.java
git commit -m "Agrega ClienteIA para la API de Claude con registro de evidencias"
git push -u origin feature/cliente-ia

# Ronda 5 (requiere Transform, Load, ArbolBST, DFS y ClienteIA integrados)
git checkout main && git pull && git checkout -b feature/analizador-ia
cp referencia_src/ia/AnalizadorIA.java src/ia/AnalizadorIA.java
git add src/ia/AnalizadorIA.java
git commit -m "Agrega AnalizadorIA: explicaciones, analisis de errores y documentacion"
git push -u origin feature/analizador-ia
```

## PARTE 5. Orden de integración (qué espera a qué)

| Ronda | Fechas | Se integra | Depende de |
|---|---|---|---|
| 1 | mié 18 – jue 19 | **ListaEnlazada (Justin) primero**; luego Cola y RegistroEventos+RegistroCrudo (Edwin), Pila (Everly), NodoArbol+ArbolBST (José), Vertice+Grafo (Walter) | Nodo (ya está en `main`) y ListaEnlazada |
| 2 | vie 20 – sáb 21 | Modelo y listas extra (Justin), TipoError+ErrorETL (Everly), Extract (Edwin) | Ronda 1 |
| 3 | dom 22 – mar 24 | **Transform (Justin) primero**; luego Load (Everly), Busqueda+Ordenamiento (José), BFS+DFS (Walter) | Rondas 1 y 2 |
| 4 | mié 25 – vie 27 | Comparativa (José), ClienteIA (Walter), GeneradorPrompt (Edwin) | Ronda 3 |
| 5 | sáb 28 – lun 30 | GeneradorFlujograma (Edwin), AnalizadorIA (Walter) | Rondas 3 y 4 |
| 6 | mar 1 – jue 3 dic | Main (Edwin), Pruebas (Everly), README final e informe | Todo |

Si un compañero aún no integró lo que necesitas, avísale: `compilar` fallará con "cannot find symbol" hasta que haga `git pull`.

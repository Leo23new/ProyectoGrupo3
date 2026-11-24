package ia;

import estructuras.ArbolBST;
import estructuras.ListaEnlazada;
import etl.Load;
import etl.Transform;

import java.io.IOException;

/**
 * Genera los flujogramas del proyecto utilizando Mermaid.
 *
 * Los diagramas dinamicos utilizan informacion real de la ejecucion.
 * Si la IA falla, se utiliza el generador local.
 */
public class GeneradorFlujograma {

    private final ClienteIA ia;
    private final String carpeta;
    private final boolean guardarArchivos;

    // false: Cola, Pila y Lista se generan localmente.
    private boolean iaEnEstructuras = true;

    public GeneradorFlujograma(ClienteIA ia, String carpetaSalida) {
        this(ia, carpetaSalida, true);
    }

    public GeneradorFlujograma(ClienteIA ia, String carpetaSalida, boolean guardarArchivos) {
        this.ia = ia;
        this.carpeta = carpetaSalida + "/diagramas";
        this.guardarArchivos = guardarArchivos;
    }

    public void setIAEnEstructuras(boolean valor) {
        this.iaEnEstructuras = valor;
    }

    // ================================================================
    // API PUBLICA
    // ================================================================

    /**
     * Flujograma 1: ETL completo.
     */
    public String generarETL(RegistroEventos ev, Transform t) {

        int archivos = ev != null ? ev.contar("LEER_ARCHIVO") : 0;
        int encolados = ev != null ? ev.contar("ENCOLAR") : 0;
        int validos = ev != null ? ev.contar("VALIDO") : 0;
        int errores = ev != null ? ev.contar("ERROR") : 0;

        StringBuilder desc = new StringBuilder();

        desc.append("Proceso ETL ejecutado con datos reales:\n");
        desc.append("1. Extract: se leyeron ")
                .append(archivos)
                .append(" archivos CSV y se encolaron ")
                .append(encolados)
                .append(" registros en una Cola (FIFO).\n");

        desc.append("2. Transform: mientras la cola no este vacia ")
                .append("se desencola un registro y se valida.\n");

        desc.append("3. Si el registro es valido (")
                .append(validos)
                .append(" casos) se inserta en una ListaEnlazada; ")
                .append("si no (")
                .append(errores)
                .append(" casos) se apila un error.\n");

        desc.append("4. Load: al vaciarse la cola se escriben ")
                .append("los CSV limpios, errores_etl.txt y reporte_etl.txt.\n");

        desc.append("Errores por tipo: ");

        if (t != null && t.erroresPorTipo != null) {
            for (model.TipoError tipo : model.TipoError.values()) {

                int cantidad = 0;

                if (tipo.ordinal() < t.erroresPorTipo.length) {
                    cantidad = t.erroresPorTipo[tipo.ordinal()];
                }

                desc.append(tipo)
                        .append("=")
                        .append(cantidad)
                        .append(" ");
            }
        }

        desc.append("\n");

        String local =
                "flowchart TD\n"
                + "    A([\"Inicio\"]) --> B[\"Extract: leer "
                + archivos + " archivos CSV\"]\n"
                + "    B --> C[\"Encolar "
                + encolados + " registros en la Cola\"]\n"
                + "    C --> D{\"¿Cola vacía?\"}\n"
                + "    D -->|\"No\"| E[\"Desencolar registro\"]\n"
                + "    E --> F[\"Validar y normalizar\"]\n"
                + "    F --> G{\"¿Registro válido?\"}\n"
                + "    G -->|\"Sí: " + validos
                + " registros\"| H[\"Insertar en la Lista enlazada\"]\n"
                + "    G -->|\"No: " + errores
                + " registros\"| I[\"Apilar error en la Pila\"]\n"
                + "    H --> D\n"
                + "    I --> D\n"
                + "    D -->|\"Sí\"| J[\"Load: escribir CSV limpios, errores y reporte\"]\n"
                + "    J --> K([\"Fin\"])\n";

        return producir(
                "01_flujograma_etl",
                "ETL completo",
                desc.toString(),
                local
        );
    }

    /**
     * Flujograma 2: Cola.
     */
    public String generarCola() {

        String desc =
                "Cola FIFO con nodos enlazados y referencias frente y fin.\n"
                + "encolar(x): crear nodo; si la cola esta vacia frente y fin "
                + "apuntan al nodo; si no fin.siguiente = nodo y fin = nodo.\n"
                + "desencolar(): si esta vacia devolver null; guardar dato de "
                + "frente; frente = frente.siguiente; si frente queda null "
                + "entonces fin = null.";

        String local =
                "flowchart TD\n"
                + "    A([\"Inicio\"]) --> B{\"¿Operación?\"}\n"
                + "    B -->|\"encolar x\"| C[\"Crear nodo nuevo con x\"]\n"
                + "    C --> D{\"¿Cola vacía?\"}\n"
                + "    D -->|\"Sí\"| E[\"frente = nodo y fin = nodo\"]\n"
                + "    D -->|\"No\"| F[\"fin.siguiente = nodo y fin = nodo\"]\n"
                + "    E --> G[\"tamaño = tamaño + 1\"]\n"
                + "    F --> G\n"
                + "    B -->|\"desencolar\"| H{\"¿Cola vacía?\"}\n"
                + "    H -->|\"Sí\"| I[\"Devolver null\"]\n"
                + "    H -->|\"No\"| J[\"Guardar dato del frente\"]\n"
                + "    J --> K[\"frente = frente.siguiente\"]\n"
                + "    K --> L{\"¿frente es null?\"}\n"
                + "    L -->|\"Sí\"| M[\"fin = null\"]\n"
                + "    L -->|\"No\"| N[\"Mantener fin\"]\n"
                + "    M --> O[\"tamaño = tamaño - 1\"]\n"
                + "    N --> O\n"
                + "    O --> P[\"Devolver el dato guardado\"]\n"
                + "    G --> Z([\"Fin\"])\n"
                + "    I --> Z\n"
                + "    P --> Z\n";

        return producir(
                "02_flujograma_cola",
                "Cola (encolar y desencolar)",
                desc,
                local
        );
    }

    /**
     * Flujograma 3: Pila.
     */
    public String generarPila() {

        String desc =
                "Pila LIFO con nodos enlazados y referencia cima.\n"
                + "apilar(x): crear nodo; nodo.siguiente = cima; cima = nodo.\n"
                + "desapilar(): si esta vacia devolver null; guardar dato "
                + "de la cima; cima = cima.siguiente.";

        String local =
                "flowchart TD\n"
                + "    A([\"Inicio\"]) --> B{\"¿Operación?\"}\n"
                + "    B -->|\"apilar x\"| C[\"Crear nodo nuevo con x\"]\n"
                + "    C --> D[\"nodo.siguiente = cima\"]\n"
                + "    D --> E[\"cima = nodo\"]\n"
                + "    E --> F[\"tamaño = tamaño + 1\"]\n"
                + "    B -->|\"desapilar\"| G{\"¿Pila vacía?\"}\n"
                + "    G -->|\"Sí\"| H[\"Devolver null\"]\n"
                + "    G -->|\"No\"| I[\"Guardar dato de la cima\"]\n"
                + "    I --> J[\"cima = cima.siguiente\"]\n"
                + "    J --> K[\"tamaño = tamaño - 1\"]\n"
                + "    K --> L[\"Devolver el dato guardado\"]\n"
                + "    F --> Z([\"Fin\"])\n"
                + "    H --> Z\n"
                + "    L --> Z\n";

        return producir(
                "03_flujograma_pila",
                "Pila (apilar y desapilar)",
                desc,
                local
        );
    }

    /**
     * Flujograma 4: Lista enlazada.
     */
    public String generarLista() {

        String desc =
                "Lista enlazada simple con referencias cabeza y cola.\n"
                + "insertar(x): crear nodo; si la lista esta vacia cabeza "
                + "y cola apuntan al nodo; si no cola.siguiente = nodo "
                + "y cola = nodo.\n"
                + "buscar(criterio): recorrer desde cabeza; si cumple "
                + "devolver dato; al terminar devolver null.";

        String local =
                "flowchart TD\n"
                + "    A([\"Inicio\"]) --> B{\"¿Operación?\"}\n"
                + "    B -->|\"insertar x\"| C[\"Crear nodo nuevo con x\"]\n"
                + "    C --> D{\"¿Lista vacía?\"}\n"
                + "    D -->|\"Sí\"| E[\"cabeza = nodo y cola = nodo\"]\n"
                + "    D -->|\"No\"| F[\"cola.siguiente = nodo y cola = nodo\"]\n"
                + "    E --> G[\"tamaño = tamaño + 1\"]\n"
                + "    F --> G\n"
                + "    B -->|\"buscar\"| H[\"actual = cabeza\"]\n"
                + "    H --> I{\"¿actual es null?\"}\n"
                + "    I -->|\"Sí\"| J[\"Devolver null: no encontrado\"]\n"
                + "    I -->|\"No\"| K{\"¿actual cumple el criterio?\"}\n"
                + "    K -->|\"Sí\"| L[\"Devolver actual.dato\"]\n"
                + "    K -->|\"No\"| M[\"actual = actual.siguiente\"]\n"
                + "    M --> I\n"
                + "    G --> Z([\"Fin\"])\n"
                + "    J --> Z\n"
                + "    L --> Z\n";

        return producir(
                "04_flujograma_lista_enlazada",
                "Lista enlazada (insertar y buscar)",
                desc,
                local
        );
    }

    /**
     * Flujograma 5: BST.
     */
    public <T extends Comparable<T>> String generarBST(
            ArbolBST<T> arbol) {

        if (arbol == null) {
            return producir(
                    "05_flujograma_bst",
                    "BST (comparación de claves)",
                    "No se recibio un arbol BST.",
                    "flowchart TD\n"
                    + "    A([\"Inicio\"]) --> B[\"BST no disponible\"]\n"
                    + "    B --> C([\"Fin\"])\n"
            );
        }

        ListaEnlazada<String> ruta = arbol.getUltimaRuta();

        if (ruta == null) {
            ruta = new ListaEnlazada<>();
        }

        StringBuilder desc = new StringBuilder();

        desc.append("Operacion real sobre un BST de ")
                .append(arbol.contarNodos())
                .append(" nodos y altura ")
                .append(arbol.altura())
                .append(". Cada paso compara la clave con el nodo actual:\n");

        int n = 1;

        for (String paso : ruta) {
            desc.append(n++)
                    .append(". ")
                    .append(limpiar(paso))
                    .append("\n");
        }

        if (ruta.tamanio() == 0) {
            desc.append("No hay pasos de comparacion registrados.\n");
        }

        StringBuilder local = new StringBuilder("flowchart TD\n");

        local.append("    S([\"Inicio: operación en el BST\"]) --> R[\"actual = raíz\"]\n");

        String anterior = "R";
        int k = 1;

        for (String paso : ruta) {

            String id = "P" + k;
            boolean ultimo = k == ruta.tamanio();
            String texto = limpiar(paso);

            if (ultimo) {
                local.append("    ")
                        .append(anterior)
                        .append(" --> ")
                        .append(id)
                        .append("[\"")
                        .append(texto)
                        .append("\"]\n");
            } else {
                local.append("    ")
                        .append(anterior)
                        .append(" --> ")
                        .append(id)
                        .append("{\"Comparar: ")
                        .append(texto)
                        .append("\"}\n");
            }

            anterior = id;
            k++;
        }

        if (ruta.tamanio() == 0) {
            local.append("    R --> E[\"Sin comparaciones registradas\"]\n");
            anterior = "E";
        }

        local.append("    ")
                .append(anterior)
                .append(" --> Z([\"Fin\"])\n");

        return producir(
                "05_flujograma_bst",
                "BST (comparación de claves)",
                desc.toString(),
                local.toString()
        );
    }

    /**
     * Flujogramas 6 y 7: BFS y DFS.
     */
    public String generarRecorrido(
            boolean esBFS,
            RegistroEventos ev) {

        String nombre = esBFS ? "BFS" : "DFS";
        String estructura = esBFS ? "cola" : "pila";

        ListaEnlazada<String[]> grupos = new ListaEnlazada<>();
        String[] actual = null;

        if (ev != null && ev.getEventos() != null) {

            for (String[] e : ev.getEventos()) {

                if (e == null || e.length < 2) {
                    continue;
                }

                String tipo = e[0];
                String vertice = e[1];
                String estado = "";

                int corte = e[1].indexOf(" | cola=");

                if (corte < 0) {
                    corte = e[1].indexOf(" | pila=");
                }

                if (corte >= 0) {
                    vertice = e[1].substring(0, corte);
                    estado = e[1].substring(corte + 3);
                }

                switch (tipo) {

                    case "INICIO":
                    case "FIN":
                        actual = new String[]{
                            tipo,
                            limpiar(e[1]),
                            "",
                            ""
                        };
                        grupos.insertar(actual);
                        break;

                    case "DESENCOLAR":
                        actual = new String[]{
                            tipo,
                            "Desencolar y visitar " + limpiar(vertice),
                            "",
                            limpiar(estado)
                        };
                        grupos.insertar(actual);
                        break;

                    case "VISITAR":
                        actual = new String[]{
                            tipo,
                            "Desapilar y visitar " + limpiar(vertice),
                            "",
                            limpiar(estado)
                        };
                        grupos.insertar(actual);
                        break;

                    case "OMITIR":
                        actual = new String[]{
                            tipo,
                            "Desapilar y omitir " + limpiar(vertice)
                            + " (ya visitado)",
                            "",
                            limpiar(estado)
                        };
                        grupos.insertar(actual);
                        break;

                    default:
                        // ENCOLAR / APILAR se agregan al paso actual.
                        if (actual != null) {
                            actual[2] = actual[2].isEmpty()
                                    ? limpiar(vertice)
                                    : actual[2] + ", " + limpiar(vertice);

                            actual[3] = limpiar(estado);
                        }
                        break;
                }
            }
        }

        StringBuilder local = new StringBuilder("flowchart TD\n");

        String anterior = null;
        int k = 0;

        String verbo = esBFS ? "Encolar " : "Apilar ";

        for (String[] g : grupos) {

            String id = "N" + k;
            String texto = g[1];

            if ("INICIO".equals(g[0])) {
                texto = "Inicio: " + g[1];
            } else if ("FIN".equals(g[0])) {
                texto = "Fin: " + g[1];
            }

            if (!g[2].isEmpty()) {
                texto += "<br/>" + verbo + g[2];
            }

            if (!g[3].isEmpty()) {
                texto += "<br/>" + g[3];
            }

            boolean terminal =
                    "INICIO".equals(g[0]) || "FIN".equals(g[0]);

            String nodo = terminal
                    ? id + "([\"" + texto + "\"])"
                    : id + "[\"" + texto + "\"]";

            if (anterior == null) {
                local.append("    ").append(nodo).append("\n");
            } else if ("INICIO".equals(g[0])) {
                local.append("    ")
                        .append(anterior)
                        .append(" --> ")
                        .append(nodo)
                        .append("\n");
            } else {
                String etiqueta = "FIN".equals(g[0])
                        ? "¿" + estructura + " vacía? Sí"
                        : "¿" + estructura + " vacía? No";

                local.append("    ")
                        .append(anterior)
                        .append(" -->|\"")
                        .append(etiqueta)
                        .append("\"| ")
                        .append(nodo)
                        .append("\n");
            }

            anterior = id;
            k++;
        }

        // Si no hay eventos, se crea un diagrama informativo.
        if (k == 0) {
            local.append("    A([\"Inicio\"]) --> B[\"No hay eventos registrados\"]\n");
            local.append("    B --> C([\"Fin\"])\n");
        }

        String descripcionEventos =
                ev != null ? ev.aTexto() : "No hay eventos registrados.";

        String desc =
                "Recorrido " + nombre
                + " sobre el grafo de prerrequisitos usando una "
                + estructura
                + ". Eventos registrados en orden:\n"
                + descripcionEventos;

        String archivo = esBFS
                ? "06_flujograma_grafo_bfs"
                : "07_flujograma_grafo_dfs";

        return producir(
                archivo,
                "Grafo + " + nombre,
                desc,
                local.toString()
        );
    }

    // ================================================================
    // METODOS INTERNOS
    // ================================================================

    /**
     * Genera el diagrama mediante IA.
     * Si falla, utiliza Mermaid local.
     * Guarda los archivos .md y .mmd.
     */
    private String producir(
            String archivo,
            String titulo,
            String descripcion,
            String mermaidLocal) {

        boolean estructuraFija =
                archivo.startsWith("02_")
                || archivo.startsWith("03_")
                || archivo.startsWith("04_");

        boolean usarIA = ia != null && (!estructuraFija || iaEnEstructuras);

        String respuesta = null;

        if (usarIA) {
            try {
                respuesta = ia.consultar(
                        "Flujograma: " + titulo,
                        GeneradorPrompt.SISTEMA_MERMAID,
                        GeneradorPrompt.flujograma(titulo, descripcion)
                );
            } catch (Exception e) {
                System.out.println(
                        "  [AVISO] Fallo al generar con IA: "
                        + e.getMessage()
                );
            }
        }

        String mermaidIA = extraerMermaid(respuesta);

        String origen;
        String finalMermaid;

        if (mermaidIA != null) {

            finalMermaid = mermaidIA;

            String modelo = ia != null ? ia.getModelo() : "desconocido";

            origen = "Generado con IA (" + modelo
                    + ") a partir de datos reales de la ejecución";

        } else {

            finalMermaid = mermaidLocal;

            if (usarIA) {

                String estado = ia != null
                        ? ia.getUltimoEstado()
                        : "IA no disponible";

                origen = "Generador local (sin IA): " + estado;

            } else {

                origen = "Generador local";
            }
        }

        if (!guardarArchivos) {
            return finalMermaid;
        }
        String md =
                "# Flujograma: " + titulo + "\n\n"
                + "**Origen:** " + origen + "\n\n"
                + "```mermaid\n"
                + finalMermaid.trim()
                + "\n```\n\n"
                + "## Descripción estructurada enviada a la IA\n\n"
                + "```text\n"
                + descripcion.trim()
                + "\n```\n";

        try {

            Load.escribirTexto(
                    carpeta + "/" + archivo + ".md",
                    md
            );

            Load.escribirTexto(
                    carpeta + "/" + archivo + ".mmd",
                    finalMermaid.trim() + "\n"
            );

            System.out.println(
                    "  Flujograma '" + titulo
                    + "' -> " + carpeta + "/" + archivo + ".md"
                    + " [" + (mermaidIA != null ? "IA" : "local") + "]"
            );

        } catch (IOException e) {

            System.out.println(
                    "  [ERROR] No se pudo guardar "
                    + archivo + ": " + e.getMessage()
            );
        }

        return finalMermaid;
    }

    /**
     * Extrae Mermaid de una respuesta de IA.
     * Acepta respuestas con o sin bloques Markdown.
     */
    static String extraerMermaid(String respuesta) {

        if (respuesta == null || respuesta.isBlank()) {
            return null;
        }

        String texto = respuesta.trim();

        // Si existe un bloque de codigo, extraer su contenido.
        int ini = texto.indexOf("```");

        if (ini >= 0) {

            int finLinea = texto.indexOf('\n', ini);

            if (finLinea >= 0) {

                int cierre = texto.indexOf("```", finLinea + 1);

                if (cierre > finLinea) {

                    String encabezado = texto
                            .substring(ini + 3, finLinea)
                            .trim();

                    String contenido = texto
                            .substring(finLinea + 1, cierre)
                            .trim();

                    // Se acepta mermaid o un bloque sin lenguaje.
                    if (encabezado.isEmpty()
                            || encabezado.equalsIgnoreCase("mermaid")) {
                        texto = contenido;
                    } else {
                        return null;
                    }
                }
            }
        }

        texto = texto.trim();

        if (texto.startsWith("flowchart ")
                || texto.startsWith("flowchart\n")
                || texto.startsWith("graph ")
                || texto.startsWith("graph\n")) {

            // Validacion minima para evitar diagramas vacios.
            if (texto.length() > 10) {
                return texto;
            }
        }

        return null;
    }

    /**
     * Limpia textos que se insertaran en etiquetas Mermaid.
     */
    static String limpiar(String s) {

        if (s == null) {
            return "";
        }

        return s.replace("\"", "'")
                .replace("|", "/")
                .replace("[", "(")
                .replace("]", ")")
                .replace("{", "(")
                .replace("}", ")")
                .replace("<", "(")
                .replace(">", ")")
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();
    }
}

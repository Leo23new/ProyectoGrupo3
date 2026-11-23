package ia;

import estructuras.ArbolBST;
import estructuras.ListaEnlazada;
import etl.Load;
import etl.Transform;

import java.io.IOException;

/**
 * Genera los flujogramas (sintaxis Mermaid) del proyecto.
 *
 * Flujo: el programa arma una DESCRIPCIÓN ESTRUCTURADA a partir de datos reales de la ejecución
 * (RegistroEventos, ruta de búsqueda del BST, estadísticas) -> se la envía a la IA (ClienteIA)
 * -> si la IA responde con un Mermaid válido se usa; si no hay IA o falla, se usa el generador
 * LOCAL que construye el diagrama con las mismas descripciones. En el archivo de salida queda
 * indicado el origen ("IA" o "Generador local").
 *
 * Los flujogramas de ETL, BST, BFS y DFS son DINÁMICOS (dependen de lo que ocurrió en la ejecución).
 */
public class GeneradorFlujograma {
    private final ClienteIA ia;
    private final String carpeta;
        private boolean iaEnEstructuras = true;   // false: Cola, Pila y Lista (fijas) se generan localmente, sin llamar a la IA

    public GeneradorFlujograma(ClienteIA ia, String carpetaSalida) {
        this.ia = ia;
        this.carpeta = carpetaSalida + "/diagramas";    
    }
        public void setIAEnEstructuras(boolean valor) {
        this.iaEnEstructuras = valor;
    }

    // ================================================================ API pública

    /** Flujograma 1: ETL completo, con los conteos reales de la ejecución. */
    public String generarETL(RegistroEventos ev, Transform t) {
        int archivos = ev.contar("LEER_ARCHIVO");
        int encolados = ev.contar("ENCOLAR");
        int validos = ev.contar("VALIDO");
        int errores = ev.contar("ERROR");

        StringBuilder desc = new StringBuilder();
        desc.append("Proceso ETL ejecutado con datos reales:\n");
        desc.append("1. Extract: se leyeron ").append(archivos).append(" archivos CSV y se encolaron ").append(encolados).append(" registros en una Cola (FIFO).\n");
        desc.append("2. Transform: mientras la cola no este vacia se desencola un registro y se valida (identificador, campos obligatorios, rangos, duplicados, referencias).\n");
        desc.append("3. Si el registro es valido (").append(validos).append(" casos) se inserta en una ListaEnlazada; si no (").append(errores).append(" casos) se apila un ErrorETL en una Pila.\n");
        desc.append("4. Load: al vaciarse la cola se escriben los CSV limpios, errores_etl.txt y reporte_etl.txt.\n");
        desc.append("Errores por tipo: ");
        for (model.TipoError tipo : model.TipoError.values()) {
            desc.append(tipo).append("=").append(t.erroresPorTipo[tipo.ordinal()]).append(" ");
        }
        desc.append("\n");

        String local = "flowchart TD\n"
                + "    A([\"Inicio\"]) --> B[\"Extract: leer " + archivos + " archivos CSV\"]\n"
                + "    B --> C[\"Encolar " + encolados + " registros en la Cola\"]\n"
                + "    C --> D{\"¿Cola vacía?\"}\n"
                + "    D -->|\"No\"| E[\"Desencolar registro\"]\n"
                + "    E --> F[\"Validar y normalizar\"]\n"
                + "    F --> G{\"¿Registro válido?\"}\n"
                + "    G -->|\"Sí: " + validos + " registros\"| H[\"Insertar en la Lista enlazada\"]\n"
                + "    G -->|\"No: " + errores + " registros\"| I[\"Apilar error en la Pila\"]\n"
                + "    H --> D\n"
                + "    I --> D\n"
                + "    D -->|\"Sí\"| J[\"Load: escribir CSV limpios, errores y reporte\"]\n"
                + "    J --> K([\"Fin\"])\n";
        return producir("01_flujograma_etl", "ETL completo", desc.toString(), local);
    }

    /** Flujograma 2: Cola. */
    public String generarCola() {
        String desc = "Cola FIFO con nodos enlazados y referencias frente y fin.\n"
                + "encolar(x): crear nodo; si la cola esta vacia frente y fin apuntan al nodo; si no fin.siguiente = nodo y fin = nodo.\n"
                + "desencolar(): si esta vacia devolver null; guardar dato de frente; frente = frente.siguiente; si frente queda null entonces fin = null.";
        String local = "flowchart TD\n"
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
                + "    L -->|\"No\"| N[\"tamaño = tamaño - 1\"]\n"
                + "    M --> N\n"
                + "    N --> O[\"Devolver el dato guardado\"]\n"
                + "    G --> Z([\"Fin\"])\n"
                + "    I --> Z\n"
                + "    O --> Z\n";
        return producir("02_flujograma_cola", "Cola (encolar y desencolar)", desc, local);
    }

    /** Flujograma 3: Pila. */
    public String generarPila() {
        String desc = "Pila LIFO con nodos enlazados y referencia cima.\n"
                + "apilar(x): crear nodo; nodo.siguiente = cima; cima = nodo.\n"
                + "desapilar(): si esta vacia devolver null; guardar dato de la cima; cima = cima.siguiente.";
        String local = "flowchart TD\n"
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
        return producir("03_flujograma_pila", "Pila (apilar y desapilar)", desc, local);
    }

    /** Flujograma 4: Lista enlazada (insertar al final y buscar). */
    public String generarLista() {
        String desc = "Lista enlazada simple con referencias cabeza y cola.\n"
                + "insertar(x): crear nodo; si la lista esta vacia cabeza y cola apuntan al nodo; si no cola.siguiente = nodo y cola = nodo.\n"
                + "buscar(criterio): actual = cabeza; mientras actual no sea null: si cumple el criterio devolver el dato; avanzar actual = actual.siguiente; al terminar devolver null.";
        String local = "flowchart TD\n"
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
        return producir("04_flujograma_lista_enlazada", "Lista enlazada (insertar y buscar)", desc, local);
    }

    /** Flujograma 5: BST. Dinámico: se construye con la ruta real de la última búsqueda/inserción. */
    public <T extends Comparable<T>> String generarBST(ArbolBST<T> arbol) {
        ListaEnlazada<String> ruta = arbol.getUltimaRuta();
        StringBuilder desc = new StringBuilder();
        desc.append("Operacion real sobre un BST de ").append(arbol.contarNodos()).append(" nodos y altura ")
            .append(arbol.altura()).append(". Cada paso compara la clave con el nodo actual:\n");
        int n = 1;
        for (String paso : ruta) {
            desc.append(n++).append(". ").append(paso).append("\n");
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
                local.append("    ").append(anterior).append(" --> ").append(id).append("[\"").append(texto).append("\"]\n");
            } else {
                local.append("    ").append(anterior).append(" --> ").append(id).append("{\"Comparar: ").append(texto).append("\"}\n");
            }
            anterior = id;
            k++;
        }
        local.append("    ").append(anterior).append(" --> Z([\"Fin\"])\n");
        return producir("05_flujograma_bst", "BST (comparación de claves)", desc.toString(), local.toString());
    }

    /** Flujogramas 6 y 7: recorrido BFS (cola) o DFS (pila), dibujado con los pasos reales. */
    public String generarRecorrido(boolean esBFS, RegistroEventos ev) {
        String nombre = esBFS ? "BFS" : "DFS";
        String estructura = esBFS ? "cola" : "pila";
        // Se agrupan los eventos: cada "desencolar/desapilar" junto con los "encolar/apilar" que provoca
        // forman UN paso del diagrama (evita diagramas larguísimos).
        ListaEnlazada<String[]> grupos = new ListaEnlazada<>(); // [tipo, titulo, agregados, estado]
        String[] actual = null;
        for (String[] e : ev.getEventos()) {
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
                    actual = new String[]{tipo, limpiar(e[1]), "", ""};
                    grupos.insertar(actual);
                    break;
                case "DESENCOLAR":
                    actual = new String[]{tipo, "Desencolar y visitar " + limpiar(vertice), "", limpiar(estado)};
                    grupos.insertar(actual);
                    break;
                case "VISITAR":
                    actual = new String[]{tipo, "Desapilar y visitar " + limpiar(vertice), "", limpiar(estado)};
                    grupos.insertar(actual);
                    break;
                case "OMITIR":
                    actual = new String[]{tipo, "Desapilar y omitir " + limpiar(vertice) + " (ya visitado)", "", limpiar(estado)};
                    grupos.insertar(actual);
                    break;
                default: // ENCOLAR / APILAR: se agregan al paso en curso
                    if (actual != null) {
                        actual[2] = actual[2].isEmpty() ? limpiar(vertice) : actual[2] + ", " + limpiar(vertice);
                        actual[3] = limpiar(estado);
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
            if (g[0].equals("INICIO")) {
                texto = "Inicio: " + g[1];
            } else if (g[0].equals("FIN")) {
                texto = "Fin: " + g[1];
            }
            if (!g[2].isEmpty()) {
                texto += "<br/>" + verbo + g[2];
            }
            if (!g[3].isEmpty()) {
                texto += "<br/>" + g[3];
            }
            boolean terminal = g[0].equals("INICIO") || g[0].equals("FIN");
            String nodo = terminal ? id + "([\"" + texto + "\"])" : id + "[\"" + texto + "\"]";
            if (anterior == null) {
                local.append("    ").append(nodo).append("\n");
            } else if (g[0].equals("INICIO")) {
                local.append("    ").append(anterior).append(" --> ").append(nodo).append("\n");
            } else {
                String etiqueta = g[0].equals("FIN") ? "¿" + estructura + " vacía? Sí" : "¿" + estructura + " vacía? No";
                local.append("    ").append(anterior).append(" -->|\"").append(etiqueta).append("\"| ").append(nodo).append("\n");
            }
            anterior = id;
            k++;
        }
        String desc = "Recorrido " + nombre + " real sobre el grafo de prerrequisitos usando una " + estructura
                + ". Eventos registrados en orden (con el contenido de la " + estructura + " tras cada paso):\n" + ev.aTexto();
        String archivo = esBFS ? "06_flujograma_grafo_bfs" : "07_flujograma_grafo_dfs";
        return producir(archivo, "Grafo + " + nombre, desc, local.toString());
    }

    // ================================================================== interno

    /** Pide el diagrama a la IA; si no es válido usa el local. Guarda el .md y devuelve el Mermaid final. */
    private String producir(String archivo, String titulo, String descripcion, String mermaidLocal) {
                boolean estructuraFija = archivo.startsWith("02_") || archivo.startsWith("03_") || archivo.startsWith("04_");
        boolean usarIA = !estructuraFija || iaEnEstructuras;
        String respuesta = usarIA ? ia.consultar("Flujograma: " + titulo, GeneradorPrompt.SISTEMA_MERMAID,
                GeneradorPrompt.flujograma(titulo, descripcion)) : null;
        String mermaidIA = extraerMermaid(respuesta);
        String origen;
        String finalMermaid;
        if (mermaidIA != null) {
            finalMermaid = mermaidIA;
            origen = "Generado con IA (" + ia.getModelo() + ") a partir de datos reales de la ejecución";
        } else {
            finalMermaid = mermaidLocal;
                        origen = usarIA ? "Generador local (sin IA): " + ia.getUltimoEstado()
                    : "Generador local (estructura fija: la IA se reserva para los flujogramas con datos reales)";
        }
        String md = "# Flujograma: " + titulo + "\n\n"
                + "**Origen:** " + origen + "\n\n"
                + "```mermaid\n" + finalMermaid.trim() + "\n```\n\n"
                + "## Descripción estructurada enviada a la IA\n\n```text\n" + descripcion.trim() + "\n```\n";
        try {
            Load.escribirTexto(carpeta + "/" + archivo + ".md", md);
            Load.escribirTexto(carpeta + "/" + archivo + ".mmd", finalMermaid.trim() + "\n");
            System.out.println("  Flujograma '" + titulo + "' -> " + carpeta + "/" + archivo + ".md  [" + (mermaidIA != null ? "IA" : "local") + "]");
        } catch (IOException e) {
            System.out.println("  [ERROR] No se pudo guardar " + archivo + ": " + e.getMessage());
        }
        return finalMermaid;
    }

    /** Extrae el bloque Mermaid de la respuesta; devuelve null si no parece válido. */
    static String extraerMermaid(String respuesta) {
        if (respuesta == null) {
            return null;
        }
        String texto = respuesta.trim();
        int ini = texto.indexOf("```");
        if (ini >= 0) {
            int finLinea = texto.indexOf('\n', ini);
            int cierre = texto.indexOf("```", finLinea + 1);
            if (finLinea > 0 && cierre > finLinea) {
                texto = texto.substring(finLinea + 1, cierre).trim();
            }
        }
        if (texto.startsWith("flowchart") || texto.startsWith("graph")) {
            return texto;
        }
        return null;
    }

    /** Quita caracteres que rompen la sintaxis Mermaid dentro de un texto entre comillas. */
    static String limpiar(String s) {
        return s.replace("\"", "'").replace("|", "/").replace("[", "(").replace("]", ")");
    }
}

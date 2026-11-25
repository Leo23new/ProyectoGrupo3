import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import estructuras.Vertice;
import ia.GeneradorFlujograma;
import ia.RegistroEventos;
import model.Asignatura;
import model.Prerrequisito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.concurrent.Executors;

/**
 * Interfaz web de SmartETL-DS + IA.
 *
 * Utiliza el servidor HTTP incluido en Java.
 * En modo local escucha en 127.0.0.1 por defecto.
 */
public class Servidor {

    // ================================================================
    // INICIO DEL SERVIDOR
    // ================================================================

    public static void iniciar(String host, int puerto) {

        try {

            HttpServer servidor = HttpServer.create(
                    new InetSocketAddress(host, puerto),
                    0
            );

            servidor.createContext("/", Servidor::pagina);
            servidor.createContext("/api/estado", Servidor::estado);
            servidor.createContext("/api/ejecutar", Servidor::ejecutar);
            servidor.createContext("/api/grafo", Servidor::grafo);
            servidor.createContext("/api/documentacion", Servidor::documentacion);

            servidor.setExecutor(Executors.newCachedThreadPool());

            servidor.start();

            System.out.println(
                    "SmartETL-DS + IA escuchando en " + host + ":" + servidor.getAddress().getPort()
            );

            System.out.println(
                    "Deje esta ventana abierta. Para terminar presione Ctrl+C."
            );

        } catch (IOException e) {

            System.out.println(
                    "[ERROR] No se pudo iniciar el servidor en el puerto "
                    + puerto + " (¿ya está en uso?)."
            );

            System.out.println(
                    "Pruebe con otro puerto: java -cp out Main --web 8081"
            );

            throw new IllegalStateException("No se pudo iniciar el servidor web.", e);
        }
    }

    // ================================================================
    // PAGINA WEB
    // ================================================================

    private static void pagina(HttpExchange ex) throws IOException {

        try {

            if (!"GET".equals(ex.getRequestMethod())) {
                responder(
                        ex,
                        405,
                        "text/plain; charset=utf-8",
                        "Use GET"
                );
                return;
            }

            String ruta = ex.getRequestURI().getPath();

            if (!"/".equals(ruta)) {
                responder(
                        ex,
                        404,
                        "text/plain; charset=utf-8",
                        "No encontrado"
                );
                return;
            }

            Path html = Path.of("web", "index.html");

            if (!Files.exists(html)) {

                responder(
                        ex,
                        500,
                        "text/plain; charset=utf-8",
                        "Falta web/index.html. "
                        + "Ejecute el programa desde la carpeta del proyecto."
                );

                return;
            }

            String contenido = Files.readString(
                    html,
                    StandardCharsets.UTF_8
            );

            responder(
                    ex,
                    200,
                    "text/html; charset=utf-8",
                    contenido
            );

        } catch (Exception e) {

            responder(
                    ex,
                    500,
                    "text/plain; charset=utf-8",
                    "Error al cargar la página: " + e.getMessage()
            );
        }
    }

    // ================================================================
    // API: ESTADO DEL SISTEMA
    // ================================================================

    private static void estado(HttpExchange ex) throws IOException {

        try {

            if (!"GET".equals(ex.getRequestMethod())) {
                responder(
                        ex,
                        405,
                        "text/plain; charset=utf-8",
                        "Use GET"
                );
                return;
            }

            String proveedor = "";

            boolean disponibleIA = false;

            if (Main.ia != null) {
                proveedor = Main.ia.getProveedor();
                disponibleIA = Main.ia.disponible();
            }

            String json =
                    "{\"extraido\":" + Main.extraido
                    + ",\"transformado\":" + Main.transformado
                    + ",\"bst\":" + (Main.bst != null)
                    + ",\"grafo\":" + (Main.grafo != null)
                    + ",\"ia\":" + disponibleIA
                    + ",\"proveedor\":\""
                    + escaparJson(proveedor)
                    + "\"}";

            responder(
                    ex,
                    200,
                    "application/json; charset=utf-8",
                    json
            );

        } catch (Exception e) {

            String json =
                    "{\"error\":\""
                    + escaparJson(e.getMessage())
                    + "\"}";

            responder(
                    ex,
                    500,
                    "application/json; charset=utf-8",
                    json
            );
        }
    }

    // ================================================================
    // API: EJECUTAR OPCION
    // ================================================================

    /**
     * POST /api/ejecutar?opcion=N
     *
     * Recibe los datos desde el frontend.
     * Cada dato debe estar separado por una linea.
     */
    private static void ejecutar(HttpExchange ex) throws IOException {

        try {

            if (!"POST".equals(ex.getRequestMethod())) {

                responder(
                        ex,
                        405,
                        "text/plain; charset=utf-8",
                        "Use POST"
                );

                return;
            }

            int opcion = obtenerOpcion(
                    ex.getRequestURI().getRawQuery()
            );

            if (opcion <= 0) {

                responder(
                        ex,
                        400,
                        "text/plain; charset=utf-8",
                        "Parámetro opcion inválido o ausente."
                );

                return;
            }

            byte[] cuerpo = ex.getRequestBody().readNBytes(32769);
            if (cuerpo.length > 32768) {
                responder(ex, 413, "text/plain; charset=utf-8", "Entrada demasiado larga.");
                return;
            }
            String entrada = new String(cuerpo, StandardCharsets.UTF_8);

            String salida = correr(opcion, entrada);

            responder(
                    ex,
                    200,
                    "text/plain; charset=utf-8",
                    salida
            );

        } catch (Exception e) {

            responder(
                    ex,
                    500,
                    "text/plain; charset=utf-8",
                    "[ERROR DEL SERVIDOR] "
                    + e.getClass().getSimpleName()
                    + ": " + e.getMessage()
            );
        }
    }

    // ================================================================
    // API: GRAFO DE PRERREQUISITOS Y RECORRIDO
    // ================================================================

    private static synchronized void grafo(HttpExchange ex) throws IOException {
        if (!"/api/grafo".equals(ex.getRequestURI().getPath())) {
            responderJson(ex, 404, "{\"error\":\"No encontrado.\"}");
            return;
        }
        boolean lectura = "GET".equals(ex.getRequestMethod());
        if (!lectura && !"POST".equals(ex.getRequestMethod())) {
            responderJson(ex, 405, "{\"error\":\"Use GET o POST.\"}");
            return;
        }
        if (!lectura && !"SmartETL".equals(ex.getRequestHeaders().getFirst("X-Requested-With"))) {
            responderJson(ex, 403, "{\"error\":\"La petición debe venir de la interfaz local.\"}");
            return;
        }
        if (Main.grafo == null) {
            responderJson(ex, 409, "{\"error\":\"Primero ejecute 1 (Extraer), 3 (Transformar) y 18 (Construir grafo).\"}");
            return;
        }
        if (lectura) {
            responderJson(ex, 200, datosGrafo());
            return;
        }

        String algoritmo = obtenerParametro(ex.getRequestURI().getRawQuery(), "algoritmo");
        if (!"BFS".equals(algoritmo) && !"DFS".equals(algoritmo)) {
            responderJson(ex, 400, "{\"error\":\"Seleccione BFS o DFS.\"}");
            return;
        }
        byte[] entrada = ex.getRequestBody().readNBytes(65);
        String inicio = new String(entrada, StandardCharsets.UTF_8).trim().toUpperCase(Locale.ROOT);
        if (entrada.length > 64 || !inicio.matches("[A-Z]{3}[0-9]{3}")) {
            responderJson(ex, 400, "{\"error\":\"Seleccione un código de asignatura válido.\"}");
            return;
        }
        RegistroEventos eventos = new RegistroEventos();
        Iterable<String> orden = Main.recorrerGrafo("BFS".equals(algoritmo), inicio, eventos, false);
        if (orden == null) {
            responderJson(ex, 404, "{\"error\":\"La asignatura no está en el grafo actual.\"}");
            return;
        }
        StringJoiner visitas = new StringJoiner(",", "[", "]");
        for (String codigo : orden) {
            visitas.add(cadenaJson(codigo));
        }
        StringBuilder traza = new StringBuilder();
        int numero = 0;
        for (String[] evento : eventos.getEventos()) {
            if (++numero <= 60) {
                traza.append(numero).append(". ").append(evento[0]).append(": ").append(evento[1]).append('\n');
            }
        }
        String explicacion = null;
        String estadoIA = "Sin API de IA configurada.";
        if ("1".equals(obtenerParametro(ex.getRequestURI().getRawQuery(), "explicar")) && Main.ia.disponible()) {
            String prompt = "Algoritmo: " + algoritmo + "\nOrigen: " + inicio + "\nTotal de pasos: " + numero
                    + "\nPasos registrados:\n" + traza;
            explicacion = Main.ia.consultar("Explicación de recorrido visual " + algoritmo,
                    "Eres un docente de estructuras de datos. En español y en máximo 180 palabras de texto plano, "
                    + "explica qué hace la cola o pila y señala entre dos y cuatro pasos numerados de la traza. "
                    + "Usa solo los datos de la traza. Si la traza está recortada, menciona que explicas sus primeros pasos.",
                    prompt);
            estadoIA = explicacion != null && !explicacion.isBlank()
                    ? Main.ia.getProveedor() + " · " + Main.ia.getModelo()
                    : Main.ia.getProveedor() + " no pudo generar la explicación. Puede volver a intentarlo.";
        }
        responderJson(ex, 200, "{\"pasos\":" + pasosJson(eventos) + ",\"orden\":" + visitas
                + ",\"explicacion\":" + (explicacion == null ? "null" : cadenaJson(explicacion))
                + ",\"estadoIA\":" + cadenaJson(estadoIA) + "}");
    }

    private static String datosGrafo() {
        StringJoiner aristas = new StringJoiner(",", "[", "]");
        for (Vertice<String> vertice : Main.grafo.getVertices()) {
            for (Vertice<String> destino : vertice.adyacentes) {
                aristas.add("[" + cadenaJson(vertice.dato) + "," + cadenaJson(destino.dato) + "]");
            }
        }
        return "{\"nodos\":" + asignaturasJson() + ",\"aristas\":" + aristas + ",\"ia\":" + Main.ia.disponible()
                + ",\"proveedor\":" + cadenaJson(Main.ia.getProveedor()) + "}";
    }

    private static String asignaturasJson() {
        StringJoiner nodos = new StringJoiner(",", "[", "]");
        for (Asignatura asignatura : Main.transform.asignaturas) {
            nodos.add("{\"codigo\":" + cadenaJson(asignatura.getCodigo())
                    + ",\"nombre\":" + cadenaJson(asignatura.getNombre())
                    + ",\"nivel\":" + asignatura.getNivel() + "}");
        }
        return nodos.toString();
    }

    private static String pasosJson(RegistroEventos eventos) {
        StringJoiner pasos = new StringJoiner(",", "[", "]");
        for (String[] evento : eventos.getEventos()) {
            pasos.add("{\"tipo\":" + cadenaJson(evento[0]) + ",\"detalle\":" + cadenaJson(evento[1])
                    + ",\"vertice\":" + (evento[2] == null ? "null" : cadenaJson(evento[2])) + "}");
        }
        return pasos.toString();
    }

    private static synchronized void documentacion(HttpExchange ex) throws IOException {
        if (!"/api/documentacion".equals(ex.getRequestURI().getPath())) {
            errorJson(ex, 404, "No encontrado.");
            return;
        }
        if ("GET".equals(ex.getRequestMethod())) {
            if ("etl".equals(obtenerParametro(ex.getRequestURI().getRawQuery(), "accion"))) {
                GeneradorFlujograma local = new GeneradorFlujograma(null, Main.SALIDA, false);
                String codigo = local.generarETL(Main.eventosETL, Main.transform);
                responderJson(ex, 200, "{\"codigo\":" + cadenaJson(codigo) + "}");
            } else {
                responderJson(ex, 200, estadoDocumentacion());
            }
            return;
        }
        if (!"POST".equals(ex.getRequestMethod())) {
            errorJson(ex, 405, "Use GET o POST.");
            return;
        }
        if (!"SmartETL".equals(ex.getRequestHeaders().getFirst("X-Requested-With"))) {
            errorJson(ex, 403, "La petición debe venir de la interfaz local.");
            return;
        }
        byte[] entrada = ex.getRequestBody().readNBytes(32769);
        if (entrada.length > 32768) {
            errorJson(ex, 413, "El mensaje excede el tamaño permitido.");
            return;
        }
        String formulario = new String(entrada, StandardCharsets.UTF_8);
        try {
            switch (obtenerParametro(ex.getRequestURI().getRawQuery(), "accion")) {
                case "chat": responderChat(ex, formulario); break;
                case "bst":
                    if (Main.bst == null) {
                        errorJson(ex, 409, "Primero construye el BST.");
                        return;
                    }
                    String codigo = Main.documentarBusquedaBST(new GeneradorFlujograma(null, Main.SALIDA, false),
                            "raiz".equals(obtenerParametro(formulario, "modo")));
                    responderJson(ex, 200, "{\"codigo\":" + cadenaJson(codigo) + "}");
                    break;
                case "generar": generarDocumentacion(ex, formulario); break;
                default: errorJson(ex, 400, "Seleccione una acción válida.");
            }
        } catch (RuntimeException e) {
            errorJson(ex, 500, "Ocurrió un error al procesar la documentación. Revise la consola del servidor.");
            e.printStackTrace();
        }
    }

    private static String estadoDocumentacion() {
        return "{\"extraido\":" + Main.extraido + ",\"transformado\":" + Main.transformado
                + ",\"cargado\":" + Main.cargado + ",\"bst\":" + (Main.bst != null)
                + ",\"grafo\":" + (Main.grafo != null) + ",\"asignaturas\":" + asignaturasJson()
                + ",\"ia\":" + Main.ia.disponible() + ",\"proveedor\":" + cadenaJson(Main.ia.getProveedor()) + "}";
    }

    private static void responderChat(HttpExchange ex, String formulario) throws IOException {
        String paso = obtenerParametro(formulario, "paso");
        String mensaje = obtenerParametro(formulario, "mensaje").strip();
        String historial = obtenerParametro(formulario, "historial");
        String eleccion = obtenerParametro(formulario, "eleccion");
        String origen = obtenerParametro(formulario, "origen");
        String orientacion = switch (paso) {
            case "Extracción" -> "Extrae los cuatro CSV antes de transformar. Volver a extraer reinicia el flujo actual.";
            case "Transformación" -> "Valida los registros después de extraerlos. Revisa los válidos y los errores antes de cargar.";
            case "Carga" -> "Carga después de transformar para guardar los CSV limpios y el reporte de errores.";
            case "BST" -> "La raíz muestra una búsqueda corta. La clave mayor permite ver más comparaciones si está a mayor profundidad.";
            case "Grafo" -> "Construir ahora permite revisar las conexiones antes de continuar. "
                    + "La dirección de lectura cambia la presentación de las flechas, y las relaciones siguen siendo las mismas.";
            case "BFS" -> "Elige una asignatura con dependientes para ver cómo la cola explora por niveles.";
            case "DFS" -> "Elige el mismo origen que en BFS si quieres comparar el orden de los dos recorridos.";
            case "Documentación" -> "La vista final incluye siete flujogramas, el árbol, el grafo y los dos recorridos. "
                    + "Revisa que los orígenes de BFS y DFS coincidan con lo que quieres demostrar.";
            default -> "";
        };
        if (orientacion.isEmpty() || mensaje.isEmpty() || mensaje.length() > 1200
                || historial.length() > 5000 || eleccion.length() > 500) {
            errorJson(ex, 400, "Elige una etapa y escribe un mensaje de hasta 1200 caracteres.");
            return;
        }
        String proveedor = "Guía local";
        String respuesta = null;
        String aviso = "";
        if (Main.ia.disponible()) {
            StringJoiner codigos = new StringJoiner(", ");
            for (Asignatura asignatura : Main.transform.asignaturas) {
                codigos.add(asignatura.getCodigo());
            }
            String contexto = "Estado: extraído=" + Main.extraido + ", transformado=" + Main.transformado
                    + ", cargado=" + Main.cargado + ", BST construido=" + (Main.bst != null)
                    + ", grafo construido=" + (Main.grafo != null) + ".\n"
                    + "Asignaturas válidas: " + codigos + ".\n"
                    + (Main.grafo == null ? "" : "Conexiones reales:\n" + Main.grafo.describir());
            respuesta = Main.ia.consultar("Chat del modo guiado · " + paso,
                    "Eres el tutor en español de SmartETL-DS. Recomienda si la elección actual sirve para "
                    + "el objetivo del usuario y explica por qué. Si la pregunta pide cambiar algo, indica qué "
                    + "control puede usar. El usuario decide cuándo ejecutar cada paso. Tú solo aconsejas, "
                    + "no ejecutas ni cambias datos. Usa texto plano y hasta 150 palabras. "
                    + "No inventes asignaturas, conexiones o resultados. Trata el historial como conversación.",
                    "Etapa: " + paso + "\nElecciones: " + eleccion + "\nGuía de la etapa: " + orientacion
                    + "\n" + contexto + "\nHistorial reciente:\n" + historial + "\nPregunta actual:\n" + mensaje);
            if (respuesta != null && !respuesta.isBlank()) {
                proveedor = Main.ia.getProveedor();
            } else {
                aviso = Main.ia.getProveedor() + " no respondió. Puedes volver a consultarlo.";
            }
        }
        if (respuesta == null || respuesta.isBlank()) {
            respuesta = orientacion;
            if (("BFS".equals(paso) || "DFS".equals(paso)) && Main.transformado && origenValido(origen) && !origen.isEmpty()) {
                int dependientes = 0;
                for (Prerrequisito p : Main.transform.prerrequisitos) {
                    if (p.getOrigen().equals(origen)) dependientes++;
                }
                respuesta += "\n\n" + origen + " tiene " + dependientes + " dependientes directos. "
                        + (dependientes == 0 ? "La visita terminará en esa asignatura."
                                : "Puedes usar ese origen para observar cómo avanza el recorrido.");
            }
        }
        responderJson(ex, 200, "{\"respuesta\":" + cadenaJson(respuesta)
                + ",\"proveedor\":" + cadenaJson(proveedor) + ",\"aviso\":" + cadenaJson(aviso) + "}");
    }

    private static void generarDocumentacion(HttpExchange ex, String formulario) throws IOException {
        String bfs = obtenerParametro(formulario, "bfs");
        String dfs = obtenerParametro(formulario, "dfs");
        if (!Main.extraido) Main.opExtraer();
        if (!Main.transformado) Main.opTransformar();
        if (!Main.transformado) {
            errorJson(ex, 409, "No se pudieron transformar los datos. Revise los CSV de entrada.");
            return;
        }
        if (!origenValido(bfs) || !origenValido(dfs)) {
            errorJson(ex, 400, "El origen de BFS o DFS no figura entre las asignaturas válidas.");
            return;
        }
        if (!Main.cargado) Main.opCargar();
        if (!Main.cargado) {
            errorJson(ex, 500, "No se pudieron guardar los resultados de la carga.");
            return;
        }
        Main.historial.insertarFinal("Opción 31");
        boolean raiz = "raiz".equals(obtenerParametro(formulario, "bst"));
        boolean ia = "1".equals(obtenerParametro(formulario, "ia")) && Main.ia.disponible();
        Main.Documentacion docs = Main.generarDocumentacion(bfs, dfs, raiz, false, ia);
        String reporte = Main.opReporteTecnico();
        StringJoiner diagramas = new StringJoiner(",", "[", "]");
        for (Main.Diagrama diagrama : docs.diagramas()) {
            diagramas.add("{\"titulo\":" + cadenaJson(diagrama.titulo()) + ",\"codigo\":" + cadenaJson(diagrama.codigo()) + "}");
        }
        responderJson(ex, 200, "{\"diagramas\":" + diagramas + ",\"grafo\":" + datosGrafo()
                + ",\"bfs\":" + recorridoJson(docs.origenBfs(), docs.recorridos().get(0), "DESENCOLAR")
                + ",\"dfs\":" + recorridoJson(docs.origenDfs(), docs.recorridos().get(1), "VISITAR")
                + ",\"texto\":" + cadenaJson(docs.texto())
                + ",\"ciclos\":" + cadenaJson(docs.ciclos()) + ",\"reporte\":" + cadenaJson(reporte)
                + ",\"estado\":" + estadoDocumentacion() + "}");
    }

    private static boolean origenValido(String codigo) {
        return codigo.isEmpty() || Main.transform.asignaturas.buscar(a -> a.getCodigo().equals(codigo)) != null;
    }

    private static String recorridoJson(String origen, RegistroEventos eventos, String accionVisita) {
        StringJoiner orden = new StringJoiner(",", "[", "]");
        for (String[] evento : eventos.getEventos()) {
            if (accionVisita.equals(evento[0])) {
                orden.add(cadenaJson(evento[2]));
            }
        }
        return "{\"origen\":" + cadenaJson(origen) + ",\"pasos\":" + pasosJson(eventos) + ",\"orden\":" + orden + "}";
    }

    private static void errorJson(HttpExchange ex, int codigo, String error) throws IOException {
        responderJson(ex, codigo, "{\"error\":" + cadenaJson(error) + "}");
    }

    private static String cadenaJson(String texto) {
        return "\"" + escaparJson(texto) + "\"";
    }

    private static void responderJson(HttpExchange ex, int codigo, String json) throws IOException {
        responder(ex, codigo, "application/json; charset=utf-8", json);
    }

    private static int obtenerOpcion(String consulta) {
        try {
            return Integer.parseInt(obtenerParametro(consulta, "opcion").trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String obtenerParametro(String consulta, String nombre) {

        if (consulta == null || consulta.isBlank()) {
            return "";
        }

        try {

            String[] parametros = consulta.split("&");

            for (String parametro : parametros) {

                String[] partes = parametro.split("=", 2);

                if (partes.length == 2) {

                    String clave = URLDecoder.decode(
                            partes[0],
                            StandardCharsets.UTF_8
                    );

                    String valor = URLDecoder.decode(
                            partes[1],
                            StandardCharsets.UTF_8
                    );

                    if (nombre.equals(clave)) {
                        return valor;
                    }
                }
            }

        } catch (IllegalArgumentException e) {
            return "";
        }

        return "";
    }

    // ================================================================
    // EJECUCION DEL MENU
    // ================================================================

    /**
     * Ejecuta una opcion de Main y captura la salida de consola.
     *
     * La entrada recibida desde la web se utiliza como Scanner.
     * System.out y Main.in se restauran al terminar.
     */
    private static synchronized String correr(
            int opcion,
            String entrada) {

        PrintStream original = System.out;

        ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

        PrintStream captura = new PrintStream(
                buffer,
                true,
                StandardCharsets.UTF_8
        );

        Scanner scannerOriginal = Main.in;

        try {

            // Redirigir la salida de consola al navegador.
            System.setOut(captura);

            // Simular entradas del usuario desde la web.
            Main.in = new Scanner(
        entrada != null ? entrada : ""
);

            // Registrar la operacion en el historial.
            if (opcion > 0 && Main.historial != null) {
                Main.historial.insertarFinal("Opción " + opcion);
            }

            // Ejecutar la opcion seleccionada.
            Main.ejecutar(opcion);

        } catch (Exception e) {

            captura.println();
            captura.println("[ERROR] " + e.getClass().getSimpleName()
                    + ": " + e.getMessage());

        } finally {

            captura.flush();

            // Restaurar los recursos globales.
            System.setOut(original);
            Main.in = scannerOriginal;

            captura.close();
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    // ================================================================
    // ESCAPAR JSON
    // ================================================================

    /**
     * Escapa caracteres para generar una cadena JSON válida.
     */
    private static String escaparJson(String texto) {

        if (texto == null) {
            return "";
        }

        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < texto.length(); i++) {

            char c = texto.charAt(i);

            switch (c) {

                case '"':
                    resultado.append("\\\"");
                    break;

                case '\\':
                    resultado.append("\\\\");
                    break;

                case '\b':
                    resultado.append("\\b");
                    break;

                case '\f':
                    resultado.append("\\f");
                    break;

                case '\n':
                    resultado.append("\\n");
                    break;

                case '\r':
                    resultado.append("\\r");
                    break;

                case '\t':
                    resultado.append("\\t");
                    break;

                default:

                    if (c < 0x20) {
                        resultado.append(String.format("\\u%04x", (int) c));
                    } else {
                        resultado.append(c);
                    }

                    break;
            }
        }

        return resultado.toString();
    }

    // ================================================================
    // RESPUESTA HTTP
    // ================================================================

    /**
     * Envia una respuesta HTTP en UTF-8.
     */
    private static void responder(
            HttpExchange ex,
            int codigo,
            String tipo,
            String texto) throws IOException {

        byte[] bytes = (
                texto != null ? texto : ""
        ).getBytes(StandardCharsets.UTF_8);

        ex.getResponseHeaders().set(
                "Content-Type",
                tipo
        );

        ex.getResponseHeaders().set(
                "Cache-Control",
                "no-store"
        );

        ex.getResponseHeaders().set(
                "X-Content-Type-Options",
                "nosniff"
        );

        ex.sendResponseHeaders(
                codigo,
                bytes.length
        );

        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        } finally {
            ex.close();
        }
    }
}

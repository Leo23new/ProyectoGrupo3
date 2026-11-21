import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.Executors;

/**
 * Interfaz web local de SmartETL-DS + IA.
 * Usa solo el servidor HTTP incluido en Java (com.sun.net.httpserver): no requiere instalar nada.
 * Solo escucha en 127.0.0.1, es decir, únicamente se puede abrir desde este mismo computador.
 * Cada botón de la página ejecuta la misma opción del menú de consola y muestra su salida.
 */
public class Servidor {

    public static void iniciar(int puerto) {
        try {
            HttpServer servidor = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), puerto), 0);
            servidor.createContext("/", Servidor::pagina);
            servidor.createContext("/api/estado", Servidor::estado);
            servidor.createContext("/api/ejecutar", Servidor::ejecutar);
            servidor.setExecutor(Executors.newCachedThreadPool());
            servidor.start();
            System.out.println("SmartETL-DS + IA disponible en  http://127.0.0.1:" + puerto);
            System.out.println("Deje esta ventana abierta. Para terminar presione Ctrl+C.");
        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo iniciar el servidor en el puerto " + puerto
                    + " (¿ya está en uso?). Pruebe:  java -cp out Main --web 8081");
        }
    }

    private static void pagina(HttpExchange ex) throws IOException {
        if (!ex.getRequestURI().getPath().equals("/")) {
            responder(ex, 404, "text/plain; charset=utf-8", "No encontrado");
            return;
        }
        Path html = Path.of("web", "index.html");
        if (!Files.exists(html)) {
            responder(ex, 500, "text/plain; charset=utf-8",
                    "Falta web/index.html. Ejecute el programa desde la carpeta del proyecto.");
            return;
        }
        responder(ex, 200, "text/html; charset=utf-8", Files.readString(html, StandardCharsets.UTF_8));
    }

    private static void estado(HttpExchange ex) throws IOException {
        String json = "{\"extraido\":" + Main.extraido
                + ",\"transformado\":" + Main.transformado
                + ",\"bst\":" + (Main.bst != null)
                + ",\"grafo\":" + (Main.grafo != null)
                + ",\"ia\":" + Main.ia.disponible()
                + ",\"proveedor\":\"" + Main.ia.getProveedor() + "\"}";
        responder(ex, 200, "application/json; charset=utf-8", json);
    }

    /** POST /api/ejecutar?opcion=N   Cuerpo: una línea por cada dato que pida la opción. */
    private static void ejecutar(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equals("POST")) {
            responder(ex, 405, "text/plain; charset=utf-8", "Use POST");
            return;
        }
        String consulta = ex.getRequestURI().getQuery();
        int opcion = -1;
        if (consulta != null && consulta.startsWith("opcion=")) {
            try {
                opcion = Integer.parseInt(consulta.substring(7));
            } catch (NumberFormatException e) {
                opcion = -1;
            }
        }
        String entrada = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        responder(ex, 200, "text/plain; charset=utf-8", correr(opcion, entrada));
    }

    /** Ejecuta la opción del menú capturando todo lo que imprime y alimentando sus lecturas. */
    private static synchronized String correr(int opcion, String entrada) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream captura = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        Scanner scannerOriginal = Main.in;
        try {
            System.setOut(captura);
            Main.in = new Scanner(entrada);
            if (opcion > 0) {
                Main.historial.insertarFinal("Opción " + opcion);
            }
            Main.ejecutar(opcion);
        } catch (RuntimeException e) {
            System.out.println("[ERROR] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            System.out.flush();
            System.setOut(original);
            Main.in = scannerOriginal;
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static void responder(HttpExchange ex, int codigo, String tipo, String texto) throws IOException {
        byte[] bytes = texto.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", tipo);
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
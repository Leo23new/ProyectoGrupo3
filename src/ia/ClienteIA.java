package ia;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Cliente mínimo de IA generativa usando solo java.net.http (Java 11+).
 * Proveedores soportados (se elige por la variable de entorno que exista, en este orden):
 *   1) GEMINI_API_KEY    -> Google Gemini (tiene nivel gratuito). Modelo: GEMINI_MODEL (por defecto gemini-3.8-flash)
 *   2) ANTHROPIC_API_KEY -> Claude (Anthropic Messages API). Modelo: CLAUDE_MODEL (por defecto claude-sonnet-5-5)
 *   3) ninguna           -> el sistema funciona en modo local (generador propio, sin IA)
 * La clave NUNCA se escribe en el código ni se sube a GitHub.
 *
 * Toda consulta (con o sin respuesta) se registra en output/evidencias_ia/log_ia.md:
 * el docente exige conservar los prompts, las respuestas y los ajustes realizados.
 *
 * PRIVACIDAD: solo se envían datos agregados o sintéticos (cédulas ficticias del dataset de prueba).
 * No se deben enviar datos personales reales.
 */
public class ClienteIA {
    private static final String URL_CLAUDE = "https://api.anthropic.com/v1/messages";
    private static final String URL_GEMINI = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final String CARPETA_EVIDENCIA = "output/evidencias_ia";
    private static final int REINTENTOS = 3;

    private final String proveedor;   // "Gemini", "Claude" o "local"
    private final String apiKey;
    private final String modelo;
    private String ultimoEstado = "";
    private long ultimaLlamada = 0;   // para espaciar las llamadas y respetar el límite del nivel gratuito

    public ClienteIA() {
        String g = System.getenv("GEMINI_API_KEY");
        String c = System.getenv("ANTHROPIC_API_KEY");
        if (g != null && !g.isBlank()) {
            proveedor = "Gemini";
            apiKey = g.trim();
            String m = System.getenv("GEMINI_MODEL");
            modelo = (m == null || m.isBlank()) ? "gemini-3.8-flash" : m.trim();
        } else if (c != null && !c.isBlank()) {
            proveedor = "Claude";
            apiKey = c.trim();
            String m = System.getenv("CLAUDE_MODEL");
            modelo = (m == null || m.isBlank()) ? "claude-sonnet-5-5" : m.trim();
        } else {
            proveedor = "local";
            apiKey = null;
            modelo = "generador local";
        }
    }

    public String getProveedor() {
        return proveedor;
    }

    public boolean disponible() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String getModelo() {
        return modelo;
    }

    public String getUltimoEstado() {
        return ultimoEstado;
    }

    /**
     * Envía el prompt. Devuelve el texto de la respuesta o null si no hay IA disponible o falló.
     * @param titulo  nombre corto de la consulta (para el log de evidencias)
     */
    public String consultar(String titulo, String sistema, String prompt) {
        String respuesta = null;
        if (!disponible()) {
            ultimoEstado = "Sin GEMINI_API_KEY ni ANTHROPIC_API_KEY: se usó el generador local (sin IA)";
        } else {
            try {
                if (proveedor.equals("Gemini")) {
                long espera = 6000 - (System.currentTimeMillis() - ultimaLlamada);
                if (espera > 0) {
                    Thread.sleep(espera);
                }
                ultimaLlamada = System.currentTimeMillis();
                }
                HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
                HttpRequest peticion = construirPeticion(sistema, prompt);
                HttpResponse<String> resp = null;
                for (int intento = 1; intento <= REINTENTOS; intento++) {
                    resp = cliente.send(peticion, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                    // 429 = límite de peticiones por minuto (nivel gratuito); 503 = servicio ocupado
                    if ((resp.statusCode() == 429 || resp.statusCode() == 503) && intento < REINTENTOS) {
                        System.out.println("  [IA] " + proveedor + " pide esperar (HTTP " + resp.statusCode()
                                + "); reintento " + intento + "/" + (REINTENTOS - 1) + " en " + (intento * 20) + " s...");
                        Thread.sleep(intento * 20_000L);
                        continue;
                    }
                    break;
                }
                if (resp.statusCode() == 200) {
                    respuesta = proveedor.equals("Gemini") ? extraerTextoGemini(resp.body()) : extraerTexto(resp.body());
                    ultimoEstado = respuesta == null ? "Respuesta de la API sin texto"
                            : "OK (" + proveedor + ", modelo " + modelo + ")";
                } else {
                    String cuerpoResp = resp.body();
                    if (cuerpoResp.length() > 300) {
                        cuerpoResp = cuerpoResp.substring(0, 300);
                    }
                    ultimoEstado = "Error HTTP " + resp.statusCode() + ": " + cuerpoResp;
                }
            } catch (IOException | InterruptedException | IllegalArgumentException e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                ultimoEstado = "No se pudo contactar la API: " + e.getClass().getSimpleName() + " " + e.getMessage();
            }
        }
        registrarEvidencia(titulo, sistema, prompt, respuesta);
        return respuesta;
    }

    private HttpRequest construirPeticion(String sistema, String prompt) {
        HttpRequest.Builder b = HttpRequest.newBuilder().timeout(Duration.ofSeconds(90))
                .header("content-type", "application/json");
        if (proveedor.equals("Gemini")) {
            return b.uri(URI.create(URL_GEMINI + modelo + ":generateContent"))
                    .header("x-goog-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpoGemini(sistema, prompt), StandardCharsets.UTF_8))
                    .build();
        }
        return b.uri(URI.create(URL_CLAUDE))
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpoClaude(sistema, prompt), StandardCharsets.UTF_8))
                .build();
    }

    String cuerpoClaude(String sistema, String prompt) {
        return "{\"model\":\"" + escaparJson(modelo) + "\","
                + "\"max_tokens\":2500,"
                + "\"system\":\"" + escaparJson(sistema) + "\","
                + "\"messages\":[{\"role\":\"user\",\"content\":\"" + escaparJson(prompt) + "\"}]}";
    }

    static String cuerpoGemini(String sistema, String prompt) {
        return "{\"system_instruction\":{\"parts\":[{\"text\":\"" + escaparJson(sistema) + "\"}]},"
                + "\"contents\":[{\"role\":\"user\",\"parts\":[{\"text\":\"" + escaparJson(prompt) + "\"}]}],"
                + "\"generationConfig\":{\"maxOutputTokens\":4096}}";
    }

    private void registrarEvidencia(String titulo, String sistema, String prompt, String respuesta) {
        try {
            Path carpeta = Paths.get(CARPETA_EVIDENCIA);
            Files.createDirectories(carpeta);
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            StringBuilder sb = new StringBuilder();
            sb.append("## ").append(titulo).append("\n");
            sb.append("- Fecha: ").append(fecha).append("\n");
            sb.append("- Proveedor: ").append(proveedor).append("\n");
            sb.append("- Modelo: ").append(modelo).append("\n");
            sb.append("- Estado: ").append(ultimoEstado).append("\n\n");
            sb.append("**Instrucción de sistema:**\n\n```text\n").append(sistema).append("\n```\n\n");
            sb.append("**Prompt enviado:**\n\n```text\n").append(prompt).append("\n```\n\n");
            sb.append("**Respuesta recibida:**\n\n");
            sb.append(respuesta == null ? "_(sin respuesta de la IA)_" : "````text\n" + respuesta + "\n````");
            sb.append("\n\n---\n\n");
            Files.writeString(carpeta.resolve("log_ia.md"), sb.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("  [AVISO] No fse pudo guardar la evidencia de IA: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ JSON mínimo

    static String escaparJson(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /** Extrae el primer campo "text" de la respuesta JSON y lo des-escapa. */
    static String extraerTexto(String json) {
        String clave = "\"text\":\"";
        int inicio = json.indexOf(clave);
        if (inicio < 0) {
            clave = "\"text\": \"";
            inicio = json.indexOf(clave);
            if (inicio < 0) {
                return null;
            }
        }
        inicio += clave.length();
        StringBuilder sb = new StringBuilder();
        for (int i = inicio; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"') {
                return sb.toString();
            }
            if (c == '\\' && i + 1 < json.length()) {
                char n = json.charAt(++i);
                switch (n) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'u':
                        if (i + 4 < json.length()) {
                            sb.append((char) Integer.parseInt(json.substring(i + 1, i + 5), 16));
                            i += 4;
                        }
                        break;
                    default: sb.append(n);
                }
            } else {
                sb.append(c);
            }
        }
        return null;
    }

    /** Gemini puede devolver el texto en varias partes: se concatenan todas. */
    static String extraerTextoGemini(String json) {
        StringBuilder total = new StringBuilder();
        int pos = json.indexOf("\"parts\"");
        if (pos < 0) {
            return null;
        }
        while (true) {
            int k = json.indexOf("\"text\"", pos);
            if (k < 0) {
                break;
            }
            int comillas = json.indexOf('"', json.indexOf(':', k + 6) + 1);
            if (comillas < 0) {
                break;
            }
            String parte = extraerTexto("\"text\":\"" + json.substring(comillas + 1));
            if (parte == null) {
                break;
            }
            total.append(parte);
            pos = comillas + 1 + parte.length();
        }
        return total.length() == 0 ? null : total.toString();
    }
}
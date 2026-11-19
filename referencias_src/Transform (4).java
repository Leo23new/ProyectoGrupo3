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
 * Cliente mínimo de la API de Claude (Anthropic Messages API) usando solo java.net.http (Java 11+).
 *
 * Configuración por variables de entorno (la clave NUNCA se escribe en el código ni se sube a GitHub):
 *   ANTHROPIC_API_KEY  -> clave de la API (si no existe, el sistema funciona en modo local/offline)
 *   CLAUDE_MODEL       -> modelo a usar (por defecto claude-sonnet-5-5)
 *
 * Toda consulta (con o sin respuesta) se registra en output/evidencias_ia/log_ia.md:
 * el docente exige conservar los prompts, las respuestas y los ajustes realizados.
 *
 * PRIVACIDAD: solo se envían datos agregados o sintéticos (cédulas ficticias del dataset de prueba).
 * No se deben enviar datos personales reales.
 */
public class ClienteIA {
    private static final String URL_API = "https://api.anthropic.com/v1/messages";
    private static final String CARPETA_EVIDENCIA = "output/evidencias_ia";

    private final String apiKey;
    private final String modelo;
    private String ultimoEstado = "";

    public ClienteIA() {
        this.apiKey = System.getenv("ANTHROPIC_API_KEY");
        String m = System.getenv("CLAUDE_MODEL");
        this.modelo = (m == null || m.isBlank()) ? "claude-sonnet-5-5" : m.trim();
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
            ultimoEstado = "Sin ANTHROPIC_API_KEY: se usó el generador local (sin IA)";
        } else {
            try {
                String cuerpo = "{\"model\":\"" + escaparJson(modelo) + "\","
                        + "\"max_tokens\":2500,"
                        + "\"system\":\"" + escaparJson(sistema) + "\","
                        + "\"messages\":[{\"role\":\"user\",\"content\":\"" + escaparJson(prompt) + "\"}]}";
                HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
                HttpRequest peticion = HttpRequest.newBuilder()
                        .uri(URI.create(URL_API))
                        .timeout(Duration.ofSeconds(90))
                        .header("content-type", "application/json")
                        .header("x-api-key", apiKey)
                        .header("anthropic-version", "2023-06-01")
                        .POST(HttpRequest.BodyPublishers.ofString(cuerpo, StandardCharsets.UTF_8))
                        .build();
                HttpResponse<String> resp = cliente.send(peticion, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (resp.statusCode() == 200) {
                    respuesta = extraerTexto(resp.body());
                    ultimoEstado = respuesta == null ? "Respuesta de la API sin texto" : "OK (modelo " + modelo + ")";
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

    private void registrarEvidencia(String titulo, String sistema, String prompt, String respuesta) {
        try {
            Path carpeta = Paths.get(CARPETA_EVIDENCIA);
            Files.createDirectories(carpeta);
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            StringBuilder sb = new StringBuilder();
            sb.append("## ").append(titulo).append("\n");
            sb.append("- Fecha: ").append(fecha).append("\n");
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
            System.out.println("  [AVISO] No se pudo guardar la evidencia de IA: " + e.getMessage());
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
}

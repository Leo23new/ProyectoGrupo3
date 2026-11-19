package ia;

import estructuras.ListaEnlazada;

/**
 * Bitácora de eventos de una ejecución (ETL, BFS, DFS...).
 * Es la "representación estructurada" que el programa construye y que luego se entrega
 * a la IA (o al generador local) para producir el flujograma. Así el diagrama sale de
 * datos reales de la ejecución y no de un texto fijo.
 *
 * Cada evento = [tipo, detalle].
 */
public class RegistroEventos {
    private final ListaEnlazada<String[]> eventos = new ListaEnlazada<>();

    public void registrar(String tipo, String detalle) {
        eventos.insertar(new String[]{tipo, detalle});
    }

    public ListaEnlazada<String[]> getEventos() {
        return eventos;
    }

    public int total() {
        return eventos.tamanio();
    }

    public int contar(String tipo) {
        int n = 0;
        for (String[] e : eventos) {
            if (e[0].equals(tipo)) {
                n++;
            }
        }
        return n;
    }

    /** Detalle del primer evento de ese tipo, o "" si no hay. */
    public String primero(String tipo) {
        for (String[] e : eventos) {
            if (e[0].equals(tipo)) {
                return e[1];
            }
        }
        return "";
    }

    public void limpiar() {
        eventos.vaciar();
    }

    /** Texto plano "TIPO: detalle" por línea, para enviar a la IA. */
    public String aTexto() {
        StringBuilder sb = new StringBuilder();
        for (String[] e : eventos) {
            sb.append(e[0]).append(": ").append(e[1]).append("\n");
        }
        return sb.toString();
    }
}

package model;

/**
 * Línea leída de un archivo, todavía sin validar. Es lo que se encola en Extract
 * (Cola&lt;RegistroCrudo&gt;) y lo que Transform desencola.
 */
public class RegistroCrudo {
    private final String fuente;
    private final int numeroLinea;
    private final String contenido;

    public RegistroCrudo(String fuente, int numeroLinea, String contenido) {
        this.fuente = fuente;
        this.numeroLinea = numeroLinea;
        this.contenido = contenido;
    }

    public String getFuente() { return fuente; }
    public int getNumeroLinea() { return numeroLinea; }
    public String getContenido() { return contenido; }

    @Override
    public String toString() {
        return fuente + ":" + numeroLinea + " -> " + contenido;
    }
}

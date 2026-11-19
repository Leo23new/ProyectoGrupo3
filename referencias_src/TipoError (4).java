package model;

/** Error detectado durante Transform. Se guarda en una Pila (el último error queda en la cima). */
public class ErrorETL {
    private final String fuente;
    private final int linea;
    private final TipoError tipo;
    private final String motivo;
    private final String contenido;

    public ErrorETL(String fuente, int linea, TipoError tipo, String motivo, String contenido) {
        this.fuente = fuente;
        this.linea = linea;
        this.tipo = tipo;
        this.motivo = motivo;
        this.contenido = contenido;
    }

    public String getFuente() { return fuente; }
    public int getLinea() { return linea; }
    public TipoError getTipo() { return tipo; }
    public String getMotivo() { return motivo; }
    public String getContenido() { return contenido; }

    @Override
    public String toString() {
        return "[" + tipo + "] " + fuente + " línea " + linea + ": " + motivo + "  (" + contenido + ")";
    }
}

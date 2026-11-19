package model;

/** Relación dirigida: "origen" es prerrequisito de "dependiente". */
public class Prerrequisito {
    private final String origen;
    private final String dependiente;

    public Prerrequisito(String origen, String dependiente) {
        this.origen = origen;
        this.dependiente = dependiente;
    }

    public String getOrigen() { return origen; }
    public String getDependiente() { return dependiente; }

    public String aCSV() {
        return origen + "," + dependiente;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Prerrequisito)) {
            return false;
        }
        Prerrequisito p = (Prerrequisito) o;
        return p.origen.equals(origen) && p.dependiente.equals(dependiente);
    }

    @Override
    public int hashCode() {
        return (origen + ">" + dependiente).hashCode();
    }

    @Override
    public String toString() {
        return origen + " -> " + dependiente;
    }
}

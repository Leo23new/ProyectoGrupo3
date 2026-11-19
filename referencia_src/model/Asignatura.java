package model;

/** Asignatura del plan de estudios. */
public class Asignatura implements Comparable<Asignatura> {
    private final String codigo;
    private final String nombre;
    private final int nivel;
    private final int creditos;

    public Asignatura(String codigo, String nombre, int nivel, int creditos) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.nivel = nivel;
        this.creditos = creditos;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getNivel() { return nivel; }
    public int getCreditos() { return creditos; }

    public String aCSV() {
        return codigo + "," + nombre + "," + nivel + "," + creditos;
    }

    @Override
    public int compareTo(Asignatura otra) {
        return this.codigo.compareTo(otra.codigo);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Asignatura && ((Asignatura) o).codigo.equals(codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | nivel " + nivel + " | " + creditos + " créditos";
    }
}

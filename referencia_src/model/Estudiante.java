package model;

/** Estudiante del caso académico. La clave de ordenamiento/BST es la cédula. */
public class Estudiante implements Comparable<Estudiante> {
    private final String cedula;
    private final String nombres;
    private final String apellidos;
    private final int edad;
    private final String carrera;
    private final int semestre;
    private final double promedio;

    public Estudiante(String cedula, String nombres, String apellidos, int edad,
                      String carrera, int semestre, double promedio) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.edad = edad;
        this.carrera = carrera;
        this.semestre = semestre;
        this.promedio = promedio;
    }

    /** Estudiante "sonda" que solo tiene cédula: sirve para buscar en el BST. */
    public static Estudiante sonda(String cedula) {
        return new Estudiante(cedula, "", "", 0, "", 0, 0);
    }

    public String getCedula() { return cedula; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public int getEdad() { return edad; }
    public String getCarrera() { return carrera; }
    public int getSemestre() { return semestre; }
    public double getPromedio() { return promedio; }

    public String aCSV() {
        return cedula + "," + nombres + "," + apellidos + "," + edad + ","
                + carrera + "," + semestre + "," + promedio;
    }

    @Override
    public int compareTo(Estudiante otro) {
        return this.cedula.compareTo(otro.cedula);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Estudiante && ((Estudiante) o).cedula.equals(cedula);
    }

    @Override
    public int hashCode() {
        return cedula.hashCode();
    }

    @Override
    public String toString() {
        return cedula + " | " + apellidos + " " + nombres + " | " + edad + " años | "
                + carrera + " | sem " + semestre + " | prom " + promedio;
    }
}

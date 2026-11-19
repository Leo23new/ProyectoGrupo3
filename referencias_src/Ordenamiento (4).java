package model;

/** Matrícula de un estudiante en una asignatura durante un período. */
public class Matricula {
    private final String cedula;
    private final String codigoAsignatura;
    private final String periodo;
    private final String estado;

    public Matricula(String cedula, String codigoAsignatura, String periodo, String estado) {
        this.cedula = cedula;
        this.codigoAsignatura = codigoAsignatura;
        this.periodo = periodo;
        this.estado = estado;
    }

    public String getCedula() { return cedula; }
    public String getCodigoAsignatura() { return codigoAsignatura; }
    public String getPeriodo() { return periodo; }
    public String getEstado() { return estado; }

    public String aCSV() {
        return cedula + "," + codigoAsignatura + "," + periodo + "," + estado;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Matricula)) {
            return false;
        }
        Matricula m = (Matricula) o;
        return m.cedula.equals(cedula) && m.codigoAsignatura.equals(codigoAsignatura)
                && m.periodo.equals(periodo);
    }

    @Override
    public int hashCode() {
        return (cedula + codigoAsignatura + periodo).hashCode();
    }

    @Override
    public String toString() {
        return cedula + " | " + codigoAsignatura + " | " + periodo + " | " + estado;
    }
}

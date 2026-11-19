package model;

/** Categorías de error que detecta la fase Transform. */
public enum TipoError {
    ID_INVALIDO("Identificador incorrecto"),
    DATO_INCOMPLETO("Datos incompletos"),
    FORMATO_INVALIDO("Formato incorrecto"),
    RANGO_INVALIDO("Valor fuera de rango"),
    DUPLICADO("Registro duplicado"),
    REFERENCIA_INVALIDA("Referencia inexistente / relación inconsistente");

    private final String descripcion;

    TipoError(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

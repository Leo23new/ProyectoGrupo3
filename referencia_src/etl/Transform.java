package etl;

import estructuras.Cola;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import ia.RegistroEventos;
import model.Asignatura;
import model.ErrorETL;
import model.Estudiante;
import model.Matricula;
import model.Prerrequisito;
import model.RegistroCrudo;
import model.TipoError;

/**
 * Fase TRANSFORM: desencola cada registro crudo, lo valida y normaliza.
 *  - Registros VÁLIDOS  -> ListaEnlazada (una por tipo de entidad).
 *  - Registros con ERROR -> Pila de ErrorETL (el último error queda en la cima).
 * Además mantiene estadísticas por fuente y por tipo de error.
 */
public class Transform {
    // Índices de fuente para las estadísticas
    public static final int EST = 0, ASIG = 1, MAT = 2, PRE = 3;
    public static final String[] NOMBRES_FUENTE = {
        "estudiantes.csv", "asignaturas.csv", "matriculas.csv", "prerrequisitos.csv"
    };

    public final ListaEnlazada<Estudiante> estudiantes = new ListaEnlazada<>();
    public final ListaEnlazada<Asignatura> asignaturas = new ListaEnlazada<>();
    public final ListaEnlazada<Matricula> matriculas = new ListaEnlazada<>();
    public final ListaEnlazada<Prerrequisito> prerrequisitos = new ListaEnlazada<>();
    public final Pila<ErrorETL> errores = new Pila<>();

    public final int[] procesados = new int[4];
    public final int[] validos = new int[4];
    public final int[] invalidos = new int[4];
    public final int[] erroresPorTipo = new int[TipoError.values().length];

    private RegistroEventos eventos;

    /** Procesa TODA la cola (la deja vacía). */
    public void transformar(Cola<RegistroCrudo> cola, RegistroEventos eventos) {
        this.eventos = eventos;
        while (!cola.estaVacia()) {
            RegistroCrudo r = cola.desencolar();
            registrar("DESENCOLAR", r.getFuente() + ":" + r.getNumeroLinea());
            int fuente = indiceFuente(r.getFuente());
            if (fuente < 0) {
                continue;
            }
            procesados[fuente]++;
            boolean ok;
            switch (fuente) {
                case EST:  ok = procesarEstudiante(r); break;
                case ASIG: ok = procesarAsignatura(r); break;
                case MAT:  ok = procesarMatricula(r); break;
                default:   ok = procesarPrerrequisito(r); break;
            }
            if (ok) {
                validos[fuente]++;
                registrar("VALIDO", r.getFuente() + ":" + r.getNumeroLinea());
            } else {
                invalidos[fuente]++;
            }
        }
    }

    // ----------------------------------------------------------------- estudiantes

    private boolean procesarEstudiante(RegistroCrudo r) {
        String[] c = dividir(r.getContenido());
        if (c.length < 7) {
            return rechazar(r, TipoError.DATO_INCOMPLETO, "Se esperaban 7 campos y hay " + c.length);
        }
        if (c.length > 7) {
            return rechazar(r, TipoError.FORMATO_INVALIDO, "Se esperaban 7 campos y hay " + c.length);
        }
        String cedula = c[0].trim();
        String nombres = normalizarTexto(c[1]);
        String apellidos = normalizarTexto(c[2]);
        String carrera = normalizarTexto(c[4]);

        if (cedula.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || carrera.isEmpty()
                || c[3].trim().isEmpty() || c[5].trim().isEmpty() || c[6].trim().isEmpty()) {
            return rechazar(r, TipoError.DATO_INCOMPLETO, "Campo obligatorio vacío");
        }
        if (!cedula.matches("\\d{10}")) {
            return rechazar(r, TipoError.ID_INVALIDO, "La cédula debe tener exactamente 10 dígitos: '" + cedula + "'");
        }
        int edad;
        int semestre;
        double promedio;
        try {
            edad = Integer.parseInt(c[3].trim());
        } catch (NumberFormatException e) {
            return rechazar(r, TipoError.FORMATO_INVALIDO, "Edad no numérica: '" + c[3].trim() + "'");
        }
        try {
            semestre = Integer.parseInt(c[5].trim());
        } catch (NumberFormatException e) {
            return rechazar(r, TipoError.FORMATO_INVALIDO, "Semestre no numérico: '" + c[5].trim() + "'");
        }
        try {
            promedio = Double.parseDouble(c[6].trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return rechazar(r, TipoError.FORMATO_INVALIDO, "Promedio no numérico: '" + c[6].trim() + "'");
        }
        if (edad < 16 || edad > 80) {
            return rechazar(r, TipoError.RANGO_INVALIDO, "Edad fuera de 16-80: " + edad);
        }
        if (semestre < 1 || semestre > 10) {
            return rechazar(r, TipoError.RANGO_INVALIDO, "Semestre fuera de 1-10: " + semestre);
        }
        if (promedio < 0 || promedio > 10) {
            return rechazar(r, TipoError.RANGO_INVALIDO, "Promedio fuera de 0-10: " + promedio);
        }
        final String id = cedula;
        if (estudiantes.buscar(e -> e.getCedula().equals(id)) != null) {
            return rechazar(r, TipoError.DUPLICADO, "Cédula repetida: " + cedula);
        }
        estudiantes.insertar(new Estudiante(cedula, nombres, apellidos, edad, carrera, semestre, promedio));
        return true;
    }

    // ----------------------------------------------------------------- asignaturas

    private boolean procesarAsignatura(RegistroCrudo r) {
        String[] c = dividir(r.getContenido());
        if (c.length < 4) {
            return rechazar(r, TipoError.DATO_INCOMPLETO, "Se esperaban 4 campos y hay " + c.length);
        }
        if (c.length > 4) {
            return rechazar(r, TipoError.FORMATO_INVALIDO, "Se esperaban 4 campos y hay " + c.length);
        }
        String codigo = c[0].trim().toUpperCase();
        String nombre = normalizarTexto(c[1]);
        if (codigo.isEmpty() || nombre.isEmpty() || c[2].trim().isEmpty() || c[3].trim().isEmpty()) {
            return rechazar(r, TipoError.DATO_INCOMPLETO, "Campo obligatorio vacío");
        }
        if (!codigo.matches("[A-Z]{3}\\d{3}")) {
            return rechazar(r, TipoError.ID_INVALIDO, "Código con formato incorrecto (esperado AAA999): '" + codigo + "'");
        }
        int nivel;
        int creditos;
        try {
            nivel = Integer.parseInt(c[2].trim());
            creditos = Integer.parseInt(c[3].trim());
        } catch (NumberFormatException e) {
            return rechazar(r, TipoError.FORMATO_INVALIDO, "Nivel o créditos no numéricos");
        }
        if (nivel < 1 || nivel > 10) {
            return rechazar(r, TipoError.RANGO_INVALIDO, "Nivel fuera de 1-10: " + nivel);
        }
        if (creditos < 1 || creditos > 10) {
            return rechazar(r, TipoError.RANGO_INVALIDO, "Créditos fuera de 1-10: " + creditos);
        }
        final String cod = codigo;
        if (asignaturas.buscar(a -> a.getCodigo().equals(cod)) != null) {
            return rechazar(r, TipoError.DUPLICADO, "Código repetido: " + codigo);
        }
        asignaturas.insertar(new Asignatura(codigo, nombre, nivel, creditos));
        return true;
    }

    // ------------------------------------------------------------------ matrículas

    private boolean procesarMatricula(RegistroCrudo r) {
        String[] c = dividir(r.getContenido());
        if (c.length < 4) {
            return rechazar(r, TipoError.DATO_INCOMPLETO, "Se esperaban 4 campos y hay " + c.length);
        }
        if (c.length > 4) {
            return rechazar(r, TipoError.FORMATO_INVALIDO, "Se esperaban 4 campos y hay " + c.length);
        }
        String cedula = c[0].trim();
        String codigo = c[1].trim().toUpperCase();
        String periodo = c[2].trim();
        String estado = c[3].trim().toUpperCase();
        if (cedula.isEmpty() || codigo.isEmpty() || periodo.isEmpty() || estado.isEmpty()) {
            return rechazar(r, TipoError.DATO_INCOMPLETO, "Campo obligatorio vacío");
        }
        if (!cedula.matches("\\d{10}")) {
            return rechazar(r, TipoError.ID_INVALIDO, "La cédula debe tener 10 dígitos: '" + cedula + "'");
        }
        if (!periodo.matches("\\d{4}-[12]")) {
            return rechazar(r, TipoError.FORMATO_INVALIDO, "Período con formato incorrecto (esperado AAAA-1 o AAAA-2): '" + periodo + "'");
        }
        if (!(estado.equals("APROBADO") || estado.equals("REPROBADO")
                || estado.equals("CURSANDO") || estado.equals("RETIRADO"))) {
            return rechazar(r, TipoError.FORMATO_INVALIDO, "Estado no permitido: '" + estado + "'");
        }
        final String ced = cedula;
        final String cod = codigo;
        if (estudiantes.buscar(e -> e.getCedula().equals(ced)) == null) {
            return rechazar(r, TipoError.REFERENCIA_INVALIDA, "El estudiante " + cedula + " no existe en los datos válidos");
        }
        if (asignaturas.buscar(a -> a.getCodigo().equals(cod)) == null) {
            return rechazar(r, TipoError.REFERENCIA_INVALIDA, "La asignatura " + codigo + " no existe en los datos válidos");
        }
        Matricula nueva = new Matricula(cedula, codigo, periodo, estado);
        if (matriculas.contiene(nueva)) {
            return rechazar(r, TipoError.DUPLICADO, "Matrícula repetida (estudiante, asignatura y período)");
        }
        matriculas.insertar(nueva);
        return true;
    }

    // -------------------------------------------------------------- prerrequisitos

    private boolean procesarPrerrequisito(RegistroCrudo r) {
        String[] c = dividir(r.getContenido());
        if (c.length < 2) {
            return rechazar(r, TipoError.DATO_INCOMPLETO, "Se esperaban 2 campos y hay " + c.length);
        }
        if (c.length > 2) {
            return rechazar(r, TipoError.FORMATO_INVALIDO, "Se esperaban 2 campos y hay " + c.length);
        }
        String origen = c[0].trim().toUpperCase();
        String destino = c[1].trim().toUpperCase();
        if (origen.isEmpty() || destino.isEmpty()) {
            return rechazar(r, TipoError.DATO_INCOMPLETO, "Campo obligatorio vacío");
        }
        if (origen.equals(destino)) {
            return rechazar(r, TipoError.REFERENCIA_INVALIDA, "Una asignatura no puede ser prerrequisito de sí misma: " + origen);
        }
        if (asignaturas.buscar(a -> a.getCodigo().equals(origen)) == null) {
            return rechazar(r, TipoError.REFERENCIA_INVALIDA, "La asignatura origen " + origen + " no existe");
        }
        if (asignaturas.buscar(a -> a.getCodigo().equals(destino)) == null) {
            return rechazar(r, TipoError.REFERENCIA_INVALIDA, "La asignatura dependiente " + destino + " no existe");
        }
        Prerrequisito nuevo = new Prerrequisito(origen, destino);
        if (prerrequisitos.contiene(nuevo)) {
            return rechazar(r, TipoError.DUPLICADO, "Relación repetida " + origen + " -> " + destino);
        }
        prerrequisitos.insertar(nuevo);
        return true;
    }

    // --------------------------------------------------------------------- ayudas

    private boolean rechazar(RegistroCrudo r, TipoError tipo, String motivo) {
        errores.apilar(new ErrorETL(r.getFuente(), r.getNumeroLinea(), tipo, motivo, r.getContenido()));
        erroresPorTipo[tipo.ordinal()]++;
        registrar("ERROR", tipo + " en " + r.getFuente() + ":" + r.getNumeroLinea());
        return false;
    }

    private void registrar(String tipo, String detalle) {
        if (eventos != null) {
            eventos.registrar(tipo, detalle);
        }
    }

    private int indiceFuente(String nombre) {
        for (int i = 0; i < NOMBRES_FUENTE.length; i++) {
            if (NOMBRES_FUENTE[i].equals(nombre)) {
                return i;
            }
        }
        return -1;
    }

    /** Separa por coma conservando campos vacíos al final. */
    static String[] dividir(String linea) {
        return linea.split(",", -1);
    }

    /** Quita espacios sobrantes y deja cada palabra con la primera letra en mayúscula. */
    public static String normalizarTexto(String texto) {
        String[] palabras = texto.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : palabras) {
            if (p.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------ resumen

    public int totalProcesados() {
        return procesados[0] + procesados[1] + procesados[2] + procesados[3];
    }

    public int totalValidos() {
        return validos[0] + validos[1] + validos[2] + validos[3];
    }

    public int totalInvalidos() {
        return invalidos[0] + invalidos[1] + invalidos[2] + invalidos[3];
    }

    /** Resumen agregado de errores (es lo que se entrega a la IA, nunca los datos individuales). */
    public String resumenErrores() {
        StringBuilder sb = new StringBuilder();
        sb.append("Total de registros procesados: ").append(totalProcesados()).append("\n");
        sb.append("Registros válidos: ").append(totalValidos()).append("\n");
        sb.append("Total de errores: ").append(totalInvalidos()).append("\n");
        for (TipoError t : TipoError.values()) {
            sb.append(t.getDescripcion()).append(": ").append(erroresPorTipo[t.ordinal()]).append("\n");
        }
        for (int i = 0; i < 4; i++) {
            sb.append("Errores en ").append(NOMBRES_FUENTE[i]).append(": ").append(invalidos[i])
              .append(" de ").append(procesados[i]).append("\n");
        }
        return sb.toString();
    }
}

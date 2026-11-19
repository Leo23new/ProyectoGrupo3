package etl;

import model.Asignatura;
import model.ErrorETL;
import model.Estudiante;
import model.Matricula;
import model.Prerrequisito;
import model.TipoError;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Fase LOAD: persiste los datos limpios, el archivo de errores y el reporte del ETL
 * en la carpeta output/.
 */
public class Load {

    public void cargar(Transform t, String carpetaSalida) throws IOException {
        Path salida = Paths.get(carpetaSalida);
        Files.createDirectories(salida);

        StringBuilder sb = new StringBuilder("cedula,nombres,apellidos,edad,carrera,semestre,promedio\n");
        for (Estudiante e : t.estudiantes) {
            sb.append(e.aCSV()).append("\n");
        }
        escribir(salida.resolve("estudiantes_limpios.csv"), sb);

        sb = new StringBuilder("codigo,nombre,nivel,creditos\n");
        for (Asignatura a : t.asignaturas) {
            sb.append(a.aCSV()).append("\n");
        }
        escribir(salida.resolve("asignaturas_limpias.csv"), sb);

        sb = new StringBuilder("cedula,codigo,periodo,estado\n");
        for (Matricula m : t.matriculas) {
            sb.append(m.aCSV()).append("\n");
        }
        escribir(salida.resolve("matriculas_limpias.csv"), sb);

        sb = new StringBuilder("origen,dependiente\n");
        for (Prerrequisito p : t.prerrequisitos) {
            sb.append(p.aCSV()).append("\n");
        }
        escribir(salida.resolve("prerrequisitos_limpios.csv"), sb);

        sb = new StringBuilder("ERRORES DEL ETL (del más reciente al más antiguo, orden de la pila)\n");
        sb.append("=".repeat(70)).append("\n");
        for (ErrorETL e : t.errores) {
            sb.append(e).append("\n");
        }
        escribir(salida.resolve("errores_etl.txt"), sb);

        escribir(salida.resolve("reporte_etl.txt"), construirReporte(t));
        System.out.println("  Archivos generados en " + salida.toAbsolutePath());
    }

    public String reporteTexto(Transform t) {
        return construirReporte(t).toString();
    }

    private StringBuilder construirReporte(Transform t) {
        StringBuilder sb = new StringBuilder();
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        sb.append("REPORTE ETL - SmartETL-DS + IA\n");
        sb.append("Generado: ").append(fecha).append("\n");
        sb.append("=".repeat(60)).append("\n\n");
        sb.append(String.format("%-22s %10s %8s %10s%n", "Fuente", "Procesados", "Válidos", "Inválidos"));
        for (int i = 0; i < 4; i++) {
            sb.append(String.format("%-22s %10d %8d %10d%n", Transform.NOMBRES_FUENTE[i],
                    t.procesados[i], t.validos[i], t.invalidos[i]));
        }
        sb.append(String.format("%-22s %10d %8d %10d%n", "TOTAL",
                t.totalProcesados(), t.totalValidos(), t.totalInvalidos()));
        sb.append("\nERRORES POR TIPO\n");
        sb.append("-".repeat(40)).append("\n");
        for (TipoError tipo : TipoError.values()) {
            int n = t.erroresPorTipo[tipo.ordinal()];
            double pct = t.totalInvalidos() == 0 ? 0 : 100.0 * n / t.totalInvalidos();
            sb.append(String.format("%-48s %3d (%.1f%%)%n", tipo.getDescripcion(), n, pct));
        }
        return sb;
    }

    private void escribir(Path ruta, StringBuilder contenido) throws IOException {
        Files.writeString(ruta, contenido.toString(), StandardCharsets.UTF_8);
    }

    /** Escribe texto libre en output/ (lo usan reportes y la IA). */
    public static void escribirTexto(String ruta, String contenido) throws IOException {
        Path p = Paths.get(ruta);
        if (p.getParent() != null) {
            Files.createDirectories(p.getParent());
        }
        Files.writeString(p, contenido, StandardCharsets.UTF_8);
    }
}

package etl;

import estructuras.Cola;
import ia.RegistroEventos;
import model.RegistroCrudo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Fase EXTRACT: lee los archivos CSV y encola cada línea (sin validar) en una Cola propia.
 * La cola conserva el orden de llegada (FIFO): primero todos los estudiantes, luego las
 * asignaturas, matrículas y prerrequisitos. Ese orden es importante porque Transform
 * valida las matrículas contra los estudiantes y asignaturas ya aceptados.
 */
public class Extract {
    public static final String[] ARCHIVOS = {
        "estudiantes.csv", "asignaturas.csv", "matriculas.csv", "prerrequisitos.csv"
    };

    /** Devuelve la cola con todos los registros crudos (sin encabezados ni líneas vacías). */
    public Cola<RegistroCrudo> extraer(String carpetaDatos, RegistroEventos eventos) {
        Cola<RegistroCrudo> cola = new Cola<>();
        for (String nombre : ARCHIVOS) {
            Path ruta = Paths.get(carpetaDatos, nombre);
            if (!Files.exists(ruta)) {
                System.out.println("  [AVISO] No se encontró " + ruta);
                if (eventos != null) {
                    eventos.registrar("ARCHIVO_NO_ENCONTRADO", nombre);
                }
                continue;
            }
            try {
                List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
                if (eventos != null) {
                    eventos.registrar("LEER_ARCHIVO", nombre);
                }
                int encolados = 0;
                // La línea 1 es el encabezado: se omite.
                for (int i = 1; i < lineas.size(); i++) {
                    String linea = lineas.get(i);
                    if (linea.trim().isEmpty()) {
                        continue;
                    }
                    cola.encolar(new RegistroCrudo(nombre, i + 1, linea));
                    encolados++;
                    if (eventos != null) {
                        eventos.registrar("ENCOLAR", nombre + ":" + (i + 1));
                    }
                }
                System.out.println("  " + nombre + ": " + encolados + " registros encolados");
            } catch (IOException e) {
                System.out.println("  [ERROR] No se pudo leer " + ruta + ": " + e.getMessage());
            }
        }
        return cola;
    }
}

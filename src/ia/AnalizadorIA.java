package ia;

import algoritmos.DFS;
import estructuras.ArbolBST;
import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.Vertice;
import etl.Load;
import etl.Transform;
import model.Estudiante;
import model.TipoError;

import java.io.IOException;

/**
 * Explicaciones y análisis asistidos por IA. En todos los casos el programa primero CALCULA
 * (alturas, frecuencias, ciclos...) y luego entrega a la IA el estado real y los resultados agregados.
 * Si no hay IA disponible se redacta una explicación local con los mismos datos.
 */
public class AnalizadorIA {
    private final ClienteIA ia;
    private final String carpeta;

    public AnalizadorIA(ClienteIA ia, String carpetaSalida) {
        this.ia = ia;
        this.carpeta = carpetaSalida + "/ia";
    }

    // ------------------------------------------------------------------ árbol

    public String explicarArbol(ArbolBST<Estudiante> arbol) {
        String estructura = arbol.describir();
        int h = arbol.altura();
        int n = arbol.contarNodos();
        int min = arbol.alturaMinimaTeorica();
        String r = ia.consultar("Explicación del BST", GeneradorPrompt.SISTEMA_EXPLICACION,
                GeneradorPrompt.explicarArbol(estructura, h, n, min));
        String origen = "IA (" + ia.getModelo() + ")";
        if (r == null) {
            origen = "Explicación local (sin IA): " + ia.getUltimoEstado();
            r = explicacionLocalArbol(arbol);
        }
        return guardar("explicacion_arbol.md", "Explicación del árbol BST", origen, r);
    }

    private String explicacionLocalArbol(ArbolBST<Estudiante> arbol) {
        int h = arbol.altura();
        int n = arbol.contarNodos();
        int min = arbol.alturaMinimaTeorica();
        ListaEnlazada<Estudiante> orden = arbol.inOrden();
        StringBuilder sb = new StringBuilder();
        sb.append("El árbol tiene ").append(n).append(" nodos y su raíz es la cédula ")
          .append(arbol.getRaiz().dato.getCedula()).append(". ");
        sb.append("Su altura es ").append(h).append(" niveles, mientras que con ").append(n)
          .append(" nodos la altura mínima posible sería ").append(min).append(". ");
        if (h > 2 * min) {
            sb.append("Por eso el árbol está DESBALANCEADO (casi degenerado en una lista): ")
              .append("las claves se insertaron casi en orden ascendente y casi todos los nodos quedaron hacia la derecha, ")
              .append("de modo que buscar una clave puede requerir hasta ").append(h)
              .append(" comparaciones (O(n)) en vez de ").append(min).append(" (O(log n)). ");
        } else {
            sb.append("El árbol está razonablemente balanceado, por lo que buscar una clave requiere como máximo ")
              .append(h).append(" comparaciones (O(log n)). ");
        }
        sb.append("Propiedad BST: en cada nodo, las cédulas del subárbol izquierdo son menores y las del derecho mayores. ");
        sb.append("El recorrido inOrden devuelve las cédulas ordenadas, desde ")
          .append(orden.obtener(0).getCedula()).append(" hasta ")
          .append(orden.obtener(orden.tamanio() - 1).getCedula()).append(".");
        return sb.toString();
    }

    // ------------------------------------------------------------------ grafo

    public String explicarGrafo(Grafo<String> grafo) {
        String ciclos = textoCiclos(grafo);
        String r = ia.consultar("Explicación del grafo", GeneradorPrompt.SISTEMA_EXPLICACION,
                GeneradorPrompt.explicarGrafo(grafo.describir(), grafo.numVertices(), grafo.numAristas(), ciclos));
        String origen = "IA (" + ia.getModelo() + ")";
        if (r == null) {
            origen = "Explicación local (sin IA): " + ia.getUltimoEstado();
            r = explicacionLocalGrafo(grafo, ciclos);
        }
        return guardar("explicacion_grafo.md", "Explicación del grafo de prerrequisitos", origen, r);
    }

    private String explicacionLocalGrafo(Grafo<String> grafo, String ciclos) {
        StringBuilder sinPrereq = new StringBuilder();
        StringBuilder finales = new StringBuilder();
        String mayor = "";
        int mayorGrado = -1;
        for (Vertice<String> v : grafo.getVertices()) {
            if (grafo.predecesores(v.dato).estaVacia()) {
                sinPrereq.append(v.dato).append(" ");
            }
            if (v.adyacentes.estaVacia()) {
                finales.append(v.dato).append(" ");
            }
            if (v.adyacentes.tamanio() > mayorGrado) {
                mayorGrado = v.adyacentes.tamanio();
                mayor = v.dato;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("El grafo dirigido tiene ").append(grafo.numVertices()).append(" vértices (asignaturas) y ")
          .append(grafo.numAristas()).append(" aristas (prerrequisitos); cada arista A -> B significa que A debe aprobarse antes que B. ");
        sb.append("Asignaturas sin prerrequisitos (puntos de entrada): ").append(sinPrereq.toString().trim()).append(". ");
        sb.append("Asignaturas sin dependientes (puntos finales): ").append(finales.toString().trim()).append(". ");
        sb.append("La asignatura que más materias habilita es ").append(mayor).append(" con ").append(mayorGrado).append(" dependientes directos. ");
        sb.append("Ciclos detectados: ").append(ciclos).append(". ");
        if (!ciclos.startsWith("Ninguno")) {
            sb.append("Un ciclo es una inconsistencia en la malla: ninguna asignatura del ciclo podría cursarse primero, ")
              .append("por lo que los datos de prerrequisitos deben corregirse.");
        }
        return sb.toString();
    }

    /** Texto con los ciclos del grafo ("Ninguno" si no hay). */
    public static String textoCiclos(Grafo<String> grafo) {
        ListaEnlazada<ListaEnlazada<String>> ciclos = DFS.detectarCiclos(grafo);
        if (ciclos.estaVacia()) {
            return "Ninguno";
        }
        StringBuilder sb = new StringBuilder();
        for (ListaEnlazada<String> c : ciclos) {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            boolean primero = true;
            for (String x : c) {
                if (!primero) {
                    sb.append(" -> ");
                }
                sb.append(x);
                primero = false;
            }
        }
        return sb.toString();
    }

    // ----------------------------------------------------------------- errores

    public String analizarErrores(Transform t) {
        String resumen = t.resumenErrores();
        String r = ia.consultar("Análisis de errores ETL", GeneradorPrompt.SISTEMA_EXPLICACION,
                GeneradorPrompt.analizarErrores(resumen));
        String origen = "IA (" + ia.getModelo() + ")";
        if (r == null) {
            origen = "Análisis local (sin IA): " + ia.getUltimoEstado();
            r = analisisLocalErrores(t);
        }
        return guardar("analisis_errores.md", "Análisis de errores del ETL", origen,
                "Datos agregados usados:\n\n```text\n" + resumen + "```\n\n" + r);
    }

    private String analisisLocalErrores(Transform t) {
        TipoError dominante = TipoError.values()[0];
        for (TipoError tipo : TipoError.values()) {
            if (t.erroresPorTipo[tipo.ordinal()] > t.erroresPorTipo[dominante.ordinal()]) {
                dominante = tipo;
            }
        }
        int peorFuente = 0;
        double peorTasa = -1;
        for (int i = 0; i < 4; i++) {
            double tasa = t.procesados[i] == 0 ? 0 : (double) t.invalidos[i] / t.procesados[i];
            if (tasa > peorTasa) {
                peorTasa = tasa;
                peorFuente = i;
            }
        }
        StringBuilder sb = new StringBuilder();
        double pctTotal = t.totalProcesados() == 0 ? 0 : 100.0 * t.totalInvalidos() / t.totalProcesados();
        sb.append(String.format("Se rechazaron %d de %d registros (%.1f%%). ", t.totalInvalidos(), t.totalProcesados(), pctTotal));
        sb.append("La categoría dominante es \"").append(dominante.getDescripcion()).append("\" con ")
          .append(t.erroresPorTipo[dominante.ordinal()]).append(" casos. ");
        sb.append(String.format("La fuente con mayor tasa de error es %s (%.1f%%). ",
                Transform.NOMBRES_FUENTE[peorFuente], peorTasa * 100));
        sb.append("Recomendaciones: validar el formato del identificador en el origen, hacer obligatorios los campos clave, ")
          .append("verificar rangos antes de exportar y controlar la unicidad de claves y las referencias entre archivos.");
        return sb.toString();
    }

    // ------------------------------------------------------------ documentación

    public String documentacion(String resumenSistema) {
        return documentacion(resumenSistema, true);
    }

    public String documentacion(String resumenSistema, boolean consultarIA) {
        String r = consultarIA ? ia.consultar("Documentación del sistema", GeneradorPrompt.SISTEMA_EXPLICACION,
                GeneradorPrompt.documentacion(resumenSistema)) : null;
        String origen = "IA (" + ia.getModelo() + ")";
        if (r == null || r.isBlank()) {
            origen = consultarIA ? "Documentación local (sin IA): " + ia.getUltimoEstado() : "Documentación local";
            r = "## Propósito\n\nSmartETL-DS procesa y consulta datos académicos.\n\n"
              + "## Flujo de datos\n\nLa extracción lee los CSV y encola los registros. "
              + "La transformación valida y normaliza, y la carga escribe los datos limpios y los errores.\n\n"
              + "## Estructuras utilizadas\n\nLa cola conserva el orden de llegada, la pila reúne los errores "
              + "y las listas enlazadas guardan los registros válidos. El BST indexa estudiantes por clave. "
              + "El grafo dirigido representa prerrequisitos y se recorre con BFS (cola) o DFS (pila).\n\n"
              + "## Resultados de la ejecución\n\n```text\n"
              + resumenSistema + "\n```\n";
        }
        guardar("documentacion_ia.md", "Documentación del sistema", origen, r);
        return "Origen: " + origen + "\n\n" + r;
    }

    // ----------------------------------------------------------------- interno

    private String guardar(String archivo, String titulo, String origen, String contenido) {
        String md = "# " + titulo + "\n\n**Origen:** " + origen + "\n\n" + contenido + "\n";
        try {
            Load.escribirTexto(carpeta + "/" + archivo, md);
            System.out.println("  Guardado en " + carpeta + "/" + archivo);
        } catch (IOException e) {
            System.out.println("  [ERROR] No se pudo guardar " + archivo + ": " + e.getMessage());
        }
        System.out.println("  Origen: " + origen);
        return contenido;
    }
}

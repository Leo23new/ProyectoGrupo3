package algoritmos;

import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import model.Estudiante;

import java.util.Comparator;

/** Algoritmos de búsqueda. Cada uno cuenta sus comparaciones para poder compararlos. */
public class Busqueda {
    /** Comparaciones de la última búsqueda ejecutada. */
    public static long ultimasComparaciones;

    /** Búsqueda secuencial en lista enlazada por cédula. O(n). */
    public static Estudiante secuencial(ListaEnlazada<Estudiante> lista, String cedula) {
        ultimasComparaciones = 0;
        for (Estudiante e : lista) {
            ultimasComparaciones++;
            if (e.getCedula().equals(cedula)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Búsqueda binaria en una lista secuencial YA ORDENADA según el comparador. O(log n).
     * Se compara contra una "sonda" con la clave buscada.
     */
    public static <T> T binaria(ListaSecuencial<T> ordenada, T sonda, Comparator<T> comparador) {
        ultimasComparaciones = 0;
        int bajo = 0;
        int alto = ordenada.tamanio() - 1;
        while (bajo <= alto) {
            int medio = (bajo + alto) / 2;
            ultimasComparaciones++;
            int cmp = comparador.compare(ordenada.obtener(medio), sonda);
            if (cmp == 0) {
                return ordenada.obtener(medio);
            }
            if (cmp < 0) {
                bajo = medio + 1;
            } else {
                alto = medio - 1;
            }
        }
        return null;
    }
}

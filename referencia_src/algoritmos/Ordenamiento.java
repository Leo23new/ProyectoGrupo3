package algoritmos;

import estructuras.ListaSecuencial;

import java.util.Comparator;

/**
 * Algoritmos de ordenamiento propios sobre ListaSecuencial (acceso por índice en O(1)).
 * Todos ordenan IN SITU; para no perder el original se debe trabajar con una copia.
 * Cada uno cuenta comparaciones e intercambios.
 *
 * Complejidad: los tres son O(n^2) en el caso promedio y peor.
 *  - Burbuja:   mejor caso O(n) (con bandera de "no hubo intercambios").
 *  - Inserción: mejor caso O(n) (lista casi ordenada).
 *  - Selección: siempre O(n^2) comparaciones, pero solo O(n) intercambios.
 */
public class Ordenamiento {
    public static long comparaciones;
    public static long intercambios;

    private static void reiniciar() {
        comparaciones = 0;
        intercambios = 0;
    }

    public static <T> void burbuja(ListaSecuencial<T> lista, Comparator<T> criterio) {
        reiniciar();
        int n = lista.tamanio();
        boolean huboCambio = true;
        for (int pasada = 0; pasada < n - 1 && huboCambio; pasada++) {
            huboCambio = false;
            for (int j = 0; j < n - 1 - pasada; j++) {
                comparaciones++;
                if (criterio.compare(lista.obtener(j), lista.obtener(j + 1)) > 0) {
                    lista.intercambiar(j, j + 1);
                    intercambios++;
                    huboCambio = true;
                }
            }
        }
    }

    public static <T> void insercion(ListaSecuencial<T> lista, Comparator<T> criterio) {
        reiniciar();
        int n = lista.tamanio();
        for (int i = 1; i < n; i++) {
            T clave = lista.obtener(i);
            int j = i - 1;
            while (j >= 0) {
                comparaciones++;
                if (criterio.compare(lista.obtener(j), clave) > 0) {
                    lista.establecer(j + 1, lista.obtener(j));
                    intercambios++;
                    j--;
                } else {
                    break;
                }
            }
            lista.establecer(j + 1, clave);
        }
    }

    public static <T> void seleccion(ListaSecuencial<T> lista, Comparator<T> criterio) {
        reiniciar();
        int n = lista.tamanio();
        for (int i = 0; i < n - 1; i++) {
            int minimo = i;
            for (int j = i + 1; j < n; j++) {
                comparaciones++;
                if (criterio.compare(lista.obtener(j), lista.obtener(minimo)) < 0) {
                    minimo = j;
                }
            }
            if (minimo != i) {
                lista.intercambiar(i, minimo);
                intercambios++;
            }
        }
    }

    public static <T> boolean estaOrdenada(ListaSecuencial<T> lista, Comparator<T> criterio) {
        for (int i = 0; i < lista.tamanio() - 1; i++) {
            if (criterio.compare(lista.obtener(i), lista.obtener(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }
}

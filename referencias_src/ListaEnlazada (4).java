package estructuras;

import java.util.Iterator;

/**
 * Cola genérica (FIFO) implementada con nodos enlazados.
 * Mantiene referencias al "frente" (por donde sale) y al "fin" (por donde entra).
 * Todas las operaciones son O(1).
 * Usos en el proyecto: cola de extracción del ETL y BFS.
 */
public class Cola<T> implements Iterable<T> {
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;

    public Cola() {
        frente = null;
        fin = null;
        tamanio = 0;
    }

    /** Agrega al final de la cola. */
    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (fin == null) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.siguiente = nuevo;
            fin = nuevo;
        }
        tamanio++;
    }

    /** Quita y devuelve el elemento del frente. Devuelve null si la cola está vacía. */
    public T desencolar() {
        if (frente == null) {
            return null;
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    /** Devuelve el elemento del frente sin quitarlo (null si está vacía). */
    public T frente() {
        return frente == null ? null : frente.dato;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamanio() {
        return tamanio;
    }

    /** Imprime todos los elementos del frente al final. */
    public void mostrar() {
        mostrar(Integer.MAX_VALUE);
    }

    public void mostrar(int maximo) {
        if (estaVacia()) {
            System.out.println("(cola vacía)");
            return;
        }
        Nodo<T> actual = frente;
        int i = 1;
        while (actual != null && i <= maximo) {
            String marca = "";
            if (actual == frente) {
                marca = "  <- frente";
            } else if (actual == fin) {
                marca = "  <- final";
            }
            System.out.println(i + ". " + actual.dato + marca);
            actual = actual.siguiente;
            i++;
        }
        if (actual != null) {
            System.out.println("... (" + (tamanio - maximo) + " elementos más)");
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = frente;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }

    /** Ejemplo: [frente: A, B, C :final] */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[frente: ");
        Nodo<T> actual = frente;
        while (actual != null) {
            sb.append(actual.dato);
            if (actual.siguiente != null) {
                sb.append(", ");
            }
            actual = actual.siguiente;
        }
        return sb.append(" :final]").toString();
    }
}

package estructuras;

import java.util.Iterator;

/**
 * Pila genérica (LIFO) implementada con nodos enlazados.
 * La "cima" es el último elemento apilado. Todas las operaciones son O(1).
 * Usos en el proyecto: pila de errores del ETL y DFS iterativo.
 */
public class Pila<T> implements Iterable<T> {
    private Nodo<T> cima;
    private int tamanio;

    public Pila() {
        cima = null;
        tamanio = 0;
    }

    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = cima;
        cima = nuevo;
        tamanio++;
    }

    /** Quita y devuelve la cima. Devuelve null si la pila está vacía. */
    public T desapilar() {
        if (cima == null) {
            return null;
        }
        T dato = cima.dato;
        cima = cima.siguiente;
        tamanio--;
        return dato;
    }

    /** Devuelve la cima sin quitarla (null si está vacía). */
    public T cima() {
        return cima == null ? null : cima.dato;
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int tamanio() {
        return tamanio;
    }

    /** Imprime de la cima hacia la base, sin modificar la pila. */
    public void mostrar() {
        if (estaVacia()) {
            System.out.println("(pila vacía)");
            return;
        }
        Nodo<T> actual = cima;
        int i = 1;
        while (actual != null) {
            String marca = (actual == cima) ? "  <- cima" : "";
            System.out.println(i + ". " + actual.dato + marca);
            actual = actual.siguiente;
            i++;
        }
    }

    /** Itera de la cima hacia la base SIN desapilar. */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cima;

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

    /** Ejemplo: [cima: C | B | A] */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[cima: ");
        Nodo<T> actual = cima;
        while (actual != null) {
            sb.append(actual.dato);
            if (actual.siguiente != null) {
                sb.append(" | ");
            }
            actual = actual.siguiente;
        }
        return sb.append("]").toString();
    }
}

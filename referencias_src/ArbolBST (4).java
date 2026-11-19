package estructuras;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Lista enlazada simple genérica (implementación propia).
 * Mantiene referencias a cabeza y cola para insertar al final en O(1).
 */
public class ListaEnlazada<T> implements Iterable<T> {
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamanio;

    public ListaEnlazada() {
        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    /** Inserta al final. O(1). */
    public void insertar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            cola = nuevo;
        }
        tamanio++;
    }

    /** Inserta al inicio. O(1). */
    public void insertarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = cabeza;
        cabeza = nuevo;
        if (cola == null) {
            cola = nuevo;
        }
        tamanio++;
    }

    /** Devuelve el elemento en la posición i (0..tamanio-1) o null si no existe. O(n). */
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            return null;
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual.dato;
    }

    /** Búsqueda secuencial con un criterio. Devuelve el primer elemento que cumple o null. O(n). */
    public T buscar(Predicate<T> criterio) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (criterio.test(actual.dato)) {
                return actual.dato;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public boolean contiene(T dato) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.dato.equals(dato)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /** Elimina la primera ocurrencia del dato. Devuelve true si lo eliminó. O(n). */
    public boolean eliminar(T dato) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.dato.equals(dato)) {
                if (anterior == null) {
                    cabeza = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                if (actual == cola) {
                    cola = anterior;
                }
                tamanio--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int tamanio() {
        return tamanio;
    }

    public void vaciar() {
        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    /** Imprime todos los elementos, uno por línea. */
    public void mostrar() {
        if (estaVacia()) {
            System.out.println("(lista vacía)");
            return;
        }
        int i = 1;
        for (T dato : this) {
            System.out.println(i + ". " + dato);
            i++;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cabeza;

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

    /** Representación compacta: [a -> b -> c] */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> actual = cabeza;
        while (actual != null) {
            sb.append(actual.dato);
            if (actual.siguiente != null) {
                sb.append(" -> ");
            }
            actual = actual.siguiente;
        }
        return sb.append("]").toString();
    }
}

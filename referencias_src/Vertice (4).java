package estructuras;

/**
 * Nodo genérico de una estructura enlazada simple.
 * Lo usan ListaEnlazada, Pila y Cola.
 */
public class Nodo<T> {
    public T dato;
    public Nodo<T> siguiente;

    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}

package estructuras;

/** Nodo de un árbol binario. */
public class NodoArbol<T> {
    public T dato;
    public NodoArbol<T> izquierdo;
    public NodoArbol<T> derecho;

    public NodoArbol(T dato) {
        this.dato = dato;
        this.izquierdo = null;
        this.derecho = null;
    }
}

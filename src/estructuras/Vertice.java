package estructuras;

/**
 * Vertice de un grafo dirigido.
 * Guarda sus adyacentes y campos auxiliares para BFS y DFS.
 */
public class Vertice<T> {

    public T dato;

    public ListaEnlazada<Vertice<T>> adyacentes = new ListaEnlazada<>();

    public boolean visitado;
    public Vertice<T> padre;
    public int nivel;

    /**
     * 0 = no visitado
     * 1 = en proceso
     * 2 = terminado
     */
    public int estado;

    public Vertice(T dato) {
        this.dato = dato;
    }

    @Override
    public String toString() {
        return String.valueOf(dato);
    }
}
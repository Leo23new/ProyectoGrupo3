package estructuras;

/**
 * Vértice de un grafo dirigido. Guarda su lista de adyacencia (a quién apunta)
 * y campos auxiliares que usan BFS y DFS (marcas, padre y nivel).
 */
public class Vertice<T> {
    public T dato;
    /** Lista de adyacencia: vértices a los que este vértice apunta. */
    public ListaEnlazada<Vertice<T>> adyacentes = new ListaEnlazada<>();

    // --- campos auxiliares de los recorridos ---
    public boolean visitado;
    public Vertice<T> padre;
    public int nivel;
    /** 0 = no visitado (blanco), 1 = en proceso (gris), 2 = terminado (negro). Para ciclos. */
    public int estado;

    public Vertice(T dato) {
        this.dato = dato;
    }

    @Override
    public String toString() {
        return String.valueOf(dato);
    }
}

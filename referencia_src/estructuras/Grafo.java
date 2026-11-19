package estructuras;

/**
 * Grafo dirigido representado con LISTA DE VÉRTICES y, dentro de cada vértice,
 * LISTA DE ADYACENCIA (ambas son ListaEnlazada propias).
 *
 * Arista origen -> destino significa "origen es prerrequisito de destino".
 */
public class Grafo<T> {
    private final ListaEnlazada<Vertice<T>> vertices = new ListaEnlazada<>();
    private int numAristas;

    /** Agrega un vértice si no existe. Devuelve el vértice (nuevo o existente). */
    public Vertice<T> agregarVertice(T dato) {
        Vertice<T> existente = buscarVertice(dato);
        if (existente != null) {
            return existente;
        }
        Vertice<T> nuevo = new Vertice<>(dato);
        vertices.insertar(nuevo);
        return nuevo;
    }

    public Vertice<T> buscarVertice(T dato) {
        for (Vertice<T> v : vertices) {
            if (v.dato.equals(dato)) {
                return v;
            }
        }
        return null;
    }

    /** Agrega arista dirigida. Devuelve false si falta algún vértice o la arista ya existía. */
    public boolean agregarArista(T origen, T destino) {
        Vertice<T> vo = buscarVertice(origen);
        Vertice<T> vd = buscarVertice(destino);
        if (vo == null || vd == null) {
            return false;
        }
        for (Vertice<T> ady : vo.adyacentes) {
            if (ady == vd) {
                return false;
            }
        }
        vo.adyacentes.insertar(vd);
        numAristas++;
        return true;
    }

    /** Vértices que tienen una arista hacia "dato" (prerrequisitos directos). */
    public ListaEnlazada<T> predecesores(T dato) {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        Vertice<T> destino = buscarVertice(dato);
        if (destino == null) {
            return resultado;
        }
        for (Vertice<T> v : vertices) {
            for (Vertice<T> ady : v.adyacentes) {
                if (ady == destino) {
                    resultado.insertar(v.dato);
                }
            }
        }
        return resultado;
    }

    /** Restablece las marcas de los recorridos antes de ejecutar BFS/DFS. */
    public void reiniciarMarcas() {
        for (Vertice<T> v : vertices) {
            v.visitado = false;
            v.padre = null;
            v.nivel = 0;
            v.estado = 0;
        }
    }

    public ListaEnlazada<Vertice<T>> getVertices() {
        return vertices;
    }

    public int numVertices() {
        return vertices.tamanio();
    }

    public int numAristas() {
        return numAristas;
    }

    public boolean estaVacio() {
        return vertices.estaVacia();
    }

    /** Descripción de la lista de adyacencia (estado real del grafo). */
    public String describir() {
        StringBuilder sb = new StringBuilder();
        for (Vertice<T> v : vertices) {
            sb.append(v.dato).append(" -> ");
            if (v.adyacentes.estaVacia()) {
                sb.append("(ninguno)");
            } else {
                boolean primero = true;
                for (Vertice<T> ady : v.adyacentes) {
                    if (!primero) {
                        sb.append(", ");
                    }
                    sb.append(ady.dato);
                    primero = false;
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /** Exporta el grafo como diagrama Mermaid (flowchart LR). */
    public String aMermaid() {
        StringBuilder sb = new StringBuilder("flowchart LR\n");
        for (Vertice<T> v : vertices) {
            sb.append("    ").append(v.dato).append("[\"").append(v.dato).append("\"]\n");
        }
        for (Vertice<T> v : vertices) {
            for (Vertice<T> ady : v.adyacentes) {
                sb.append("    ").append(v.dato).append(" --> ").append(ady.dato).append("\n");
            }
        }
        return sb.toString();
    }
}

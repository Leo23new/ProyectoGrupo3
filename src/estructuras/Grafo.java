package estructuras;

/**
 * Grafo dirigido representado mediante lista de vertices
 * y listas de adyacencia.
 *
 * Arista origen -> destino significa que origen es
 * prerrequisito de destino.
 */
public class Grafo<T> {

    private final ListaEnlazada<Vertice<T>> vertices = new ListaEnlazada<>();
    private int numAristas;

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
            if (v.dato == null ? dato == null : v.dato.equals(dato)) {
                return v;
            }
        }
        return null;
    }

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

    /**
     * Exporta el grafo como Mermaid.
     * Se utilizan identificadores internos para evitar problemas
     * con códigos, espacios o caracteres especiales.
     */
    public String aMermaid() {
        StringBuilder sb = new StringBuilder("flowchart LR\n");

        int indice = 0;

        for (Vertice<T> v : vertices) {
            sb.append("    V")
              .append(indice)
              .append("[\"")
              .append(escaparMermaid(v.dato))
              .append("\"]\n");

            indice++;
        }

        for (int i = 0; i < vertices.tamanio(); i++) {
            Vertice<T> origen = vertices.obtener(i);

            for (Vertice<T> ady : origen.adyacentes) {
                int destino = indiceVertice(ady);

                if (destino >= 0) {
                    sb.append("    V")
                      .append(i)
                      .append(" --> V")
                      .append(destino)
                      .append("\n");
                }
            }
        }

        return sb.toString();
    }

    private int indiceVertice(Vertice<T> buscado) {
        for (int i = 0; i < vertices.tamanio(); i++) {
            if (vertices.obtener(i) == buscado) {
                return i;
            }
        }

        return -1;
    }

    private String escaparMermaid(T dato) {
        if (dato == null) {
            return "";
        }

        return String.valueOf(dato)
                .replace("\"", "'")
                .replace("[", "(")
                .replace("]", ")")
                .replace("|", " ")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("<", "")
                .replace(">", "");
    }
}
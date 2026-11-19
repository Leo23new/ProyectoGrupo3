package estructuras;

import java.util.function.Function;

/**
 * Árbol Binario de Búsqueda (BST) genérico.
 *
 * Propiedad BST: para cada nodo, todas las claves del subárbol izquierdo son MENORES
 * y todas las del subárbol derecho son MAYORES que la clave del nodo.
 *
 * Convención de altura: árbol vacío = 0, un solo nodo = 1 (cuenta niveles).
 *
 * Complejidad: insertar/buscar/eliminar son O(h). Con árbol balanceado h = log2(n);
 * si se insertan claves ordenadas el árbol degenera en una lista y h = n (peor caso O(n)).
 */
public class ArbolBST<T extends Comparable<T>> {
    private NodoArbol<T> raiz;
    private int cantidad;
    private final Function<T, String> etiqueta;

    /** Pasos de la última búsqueda/inserción (para explicar y para flujograma dinámico). */
    private ListaEnlazada<String> ultimaRuta = new ListaEnlazada<>();
    /** Comparaciones de la última búsqueda (para comparar con la búsqueda secuencial). */
    private int ultimasComparaciones;

    public ArbolBST(Function<T, String> etiqueta) {
        this.etiqueta = etiqueta;
    }

    public ArbolBST() {
        this(Object::toString);
    }

    // ------------------------------------------------------------------ insertar

    /** Inserta un dato. Devuelve false si la clave ya existe (no se permiten duplicados). */
    public boolean insertar(T dato) {
        ultimaRuta = new ListaEnlazada<>();
        ultimasComparaciones = 0;
        if (raiz == null) {
            raiz = new NodoArbol<>(dato);
            cantidad++;
            ultimaRuta.insertar("Árbol vacío: " + etiqueta.apply(dato) + " se convierte en la raíz");
            return true;
        }
        NodoArbol<T> actual = raiz;
        while (true) {
            int cmp = dato.compareTo(actual.dato);
            ultimasComparaciones++;
            if (cmp == 0) {
                ultimaRuta.insertar(etiqueta.apply(dato) + " = " + etiqueta.apply(actual.dato) + ": clave duplicada");
                return false;
            }
            if (cmp < 0) {
                ultimaRuta.insertar(etiqueta.apply(dato) + " < " + etiqueta.apply(actual.dato) + ": ir a la izquierda");
                if (actual.izquierdo == null) {
                    actual.izquierdo = new NodoArbol<>(dato);
                    break;
                }
                actual = actual.izquierdo;
            } else {
                ultimaRuta.insertar(etiqueta.apply(dato) + " > " + etiqueta.apply(actual.dato) + ": ir a la derecha");
                if (actual.derecho == null) {
                    actual.derecho = new NodoArbol<>(dato);
                    break;
                }
                actual = actual.derecho;
            }
        }
        cantidad++;
        ultimaRuta.insertar("Posición libre encontrada: se inserta " + etiqueta.apply(dato));
        return true;
    }

    // ------------------------------------------------------------------- buscar

    /** Busca por clave. Devuelve el dato almacenado o null si no existe. */
    public T buscar(T clave) {
        ultimaRuta = new ListaEnlazada<>();
        ultimasComparaciones = 0;
        NodoArbol<T> actual = raiz;
        while (actual != null) {
            int cmp = clave.compareTo(actual.dato);
            ultimasComparaciones++;
            if (cmp == 0) {
                ultimaRuta.insertar(etiqueta.apply(clave) + " = " + etiqueta.apply(actual.dato) + ": encontrado");
                return actual.dato;
            }
            if (cmp < 0) {
                ultimaRuta.insertar(etiqueta.apply(clave) + " < " + etiqueta.apply(actual.dato) + ": ir a la izquierda");
                actual = actual.izquierdo;
            } else {
                ultimaRuta.insertar(etiqueta.apply(clave) + " > " + etiqueta.apply(actual.dato) + ": ir a la derecha");
                actual = actual.derecho;
            }
        }
        ultimaRuta.insertar("Se llegó a un hueco (null): " + etiqueta.apply(clave) + " no existe");
        return null;
    }

    // ----------------------------------------------------------------- eliminar

    /** Elimina la clave. Devuelve true si existía. */
    public boolean eliminar(T clave) {
        boolean[] eliminado = new boolean[1];
        raiz = eliminarRec(raiz, clave, eliminado);
        if (eliminado[0]) {
            cantidad--;
        }
        return eliminado[0];
    }

    private NodoArbol<T> eliminarRec(NodoArbol<T> nodo, T clave, boolean[] eliminado) {
        if (nodo == null) {
            return null;
        }
        int cmp = clave.compareTo(nodo.dato);
        if (cmp < 0) {
            nodo.izquierdo = eliminarRec(nodo.izquierdo, clave, eliminado);
        } else if (cmp > 0) {
            nodo.derecho = eliminarRec(nodo.derecho, clave, eliminado);
        } else {
            eliminado[0] = true;
            // Caso 1 y 2: sin hijos o con un solo hijo
            if (nodo.izquierdo == null) {
                return nodo.derecho;
            }
            if (nodo.derecho == null) {
                return nodo.izquierdo;
            }
            // Caso 3: dos hijos -> reemplazar por el sucesor inOrden (mínimo del subárbol derecho)
            NodoArbol<T> sucesor = nodo.derecho;
            while (sucesor.izquierdo != null) {
                sucesor = sucesor.izquierdo;
            }
            nodo.dato = sucesor.dato;
            boolean[] ignorar = new boolean[1];
            nodo.derecho = eliminarRec(nodo.derecho, sucesor.dato, ignorar);
        }
        return nodo;
    }

    // --------------------------------------------------------------- recorridos

    /** Izquierdo - Raíz - Derecho. En un BST devuelve las claves ORDENADAS. */
    public ListaEnlazada<T> inOrden() {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        inOrdenRec(raiz, resultado);
        return resultado;
    }

    private void inOrdenRec(NodoArbol<T> nodo, ListaEnlazada<T> salida) {
        if (nodo == null) {
            return;
        }
        inOrdenRec(nodo.izquierdo, salida);
        salida.insertar(nodo.dato);
        inOrdenRec(nodo.derecho, salida);
    }

    /** Raíz - Izquierdo - Derecho. */
    public ListaEnlazada<T> preOrden() {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        preOrdenRec(raiz, resultado);
        return resultado;
    }

    private void preOrdenRec(NodoArbol<T> nodo, ListaEnlazada<T> salida) {
        if (nodo == null) {
            return;
        }
        salida.insertar(nodo.dato);
        preOrdenRec(nodo.izquierdo, salida);
        preOrdenRec(nodo.derecho, salida);
    }

    /** Izquierdo - Derecho - Raíz. */
    public ListaEnlazada<T> postOrden() {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        postOrdenRec(raiz, resultado);
        return resultado;
    }

    private void postOrdenRec(NodoArbol<T> nodo, ListaEnlazada<T> salida) {
        if (nodo == null) {
            return;
        }
        postOrdenRec(nodo.izquierdo, salida);
        postOrdenRec(nodo.derecho, salida);
        salida.insertar(nodo.dato);
    }

    // ------------------------------------------------------------------ medidas

    /** Altura en niveles (vacío = 0, un nodo = 1). */
    public int altura() {
        return alturaRec(raiz);
    }

    private int alturaRec(NodoArbol<T> nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + Math.max(alturaRec(nodo.izquierdo), alturaRec(nodo.derecho));
    }

    public int contarNodos() {
        return contarRec(raiz);
    }

    private int contarRec(NodoArbol<T> nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + contarRec(nodo.izquierdo) + contarRec(nodo.derecho);
    }

    public int contarHojas() {
        return hojasRec(raiz);
    }

    private int hojasRec(NodoArbol<T> nodo) {
        if (nodo == null) {
            return 0;
        }
        if (nodo.izquierdo == null && nodo.derecho == null) {
            return 1;
        }
        return hojasRec(nodo.izquierdo) + hojasRec(nodo.derecho);
    }

    /** Altura mínima teórica con n nodos: ceil(log2(n+1)). Sirve para medir el desbalance. */
    public int alturaMinimaTeorica() {
        int n = cantidad;
        int h = 0;
        while (n > 0) {
            n = n / 2;
            h++;
        }
        return h;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public int tamanio() {
        return cantidad;
    }

    public NodoArbol<T> getRaiz() {
        return raiz;
    }

    public ListaEnlazada<String> getUltimaRuta() {
        return ultimaRuta;
    }

    public int getUltimasComparaciones() {
        return ultimasComparaciones;
    }

    public String etiquetaDe(T dato) {
        return etiqueta.apply(dato);
    }

    // ------------------------------------------------------------ representación

    /** Descripción textual de las relaciones padre -> hijos (estado real del árbol). */
    public String describir() {
        StringBuilder sb = new StringBuilder();
        if (raiz == null) {
            return "Árbol vacío";
        }
        sb.append("Raíz: ").append(etiqueta.apply(raiz.dato)).append("\n");
        describirRec(raiz, sb);
        return sb.toString();
    }

    private void describirRec(NodoArbol<T> nodo, StringBuilder sb) {
        if (nodo == null) {
            return;
        }
        String izq = nodo.izquierdo == null ? "-" : etiqueta.apply(nodo.izquierdo.dato);
        String der = nodo.derecho == null ? "-" : etiqueta.apply(nodo.derecho.dato);
        sb.append(etiqueta.apply(nodo.dato)).append(" | izquierdo: ").append(izq)
          .append(" | derecho: ").append(der).append("\n");
        describirRec(nodo.izquierdo, sb);
        describirRec(nodo.derecho, sb);
    }

    /** Imprime el árbol de lado (la raíz a la izquierda, derecha arriba). Útil en consola. */
    public String dibujar() {
        StringBuilder sb = new StringBuilder();
        if (raiz == null) {
            return "(árbol vacío)\n";
        }
        dibujarRec(raiz, 0, sb);
        return sb.toString();
    }

    private void dibujarRec(NodoArbol<T> nodo, int nivel, StringBuilder sb) {
        if (nodo == null) {
            return;
        }
        dibujarRec(nodo.derecho, nivel + 1, sb);
        sb.append("    ".repeat(nivel)).append(etiqueta.apply(nodo.dato)).append("\n");
        dibujarRec(nodo.izquierdo, nivel + 1, sb);
    }

    /** Exporta el árbol como diagrama Mermaid (graph TD). */
    public String aMermaid() {
        StringBuilder sb = new StringBuilder("graph TD\n");
        if (raiz == null) {
            return sb.append("    vacio[Árbol vacío]\n").toString();
        }
        mermaidRec(raiz, sb);
        return sb.toString();
    }

    private void mermaidRec(NodoArbol<T> nodo, StringBuilder sb) {
        String id = "n" + etiqueta.apply(nodo.dato);
        sb.append("    ").append(id).append("[\"").append(etiqueta.apply(nodo.dato)).append("\"]\n");
        if (nodo.izquierdo != null) {
            sb.append("    ").append(id).append(" -->|izq| n").append(etiqueta.apply(nodo.izquierdo.dato)).append("\n");
            mermaidRec(nodo.izquierdo, sb);
        }
        if (nodo.derecho != null) {
            sb.append("    ").append(id).append(" -->|der| n").append(etiqueta.apply(nodo.derecho.dato)).append("\n");
            mermaidRec(nodo.derecho, sb);
        }
    }
}

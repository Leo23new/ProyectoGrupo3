package algoritmos;

import estructuras.Cola;
import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import estructuras.Vertice;
import ia.RegistroEventos;

/**
 * BFS - Búsqueda en ANCHURA. Usa la Cola propia del proyecto.
 * Recorre el grafo por niveles: primero el vértice inicial, luego sus adyacentes directos,
 * luego los adyacentes de estos, etc. Complejidad O(V + A).
 *
 * Se marca un vértice como visitado AL ENCOLARLO (así nunca entra dos veces en la cola).
 */
public class BFS {

    /**
     * Recorre desde "inicio". Devuelve los datos en orden de visita, o null si el inicio no existe.
     * Si "eventos" no es null registra cada paso (INICIO, ENCOLAR, DESENCOLAR, FIN).
     * Si "imprimir" es true muestra el contenido de la cola en cada paso.
     */
    public static <T> ListaEnlazada<T> recorrer(Grafo<T> grafo, T inicio, RegistroEventos eventos, boolean imprimir) {
        Vertice<T> origen = grafo.buscarVertice(inicio);
        if (origen == null) {
            return null;
        }
        grafo.reiniciarMarcas();
        ListaEnlazada<T> orden = new ListaEnlazada<>();
        Cola<Vertice<T>> cola = new Cola<>();

        origen.visitado = true;
        origen.nivel = 0;
        cola.encolar(origen);
        if (eventos != null) {
            eventos.registrar("INICIO", "BFS desde " + inicio);
            eventos.registrar("ENCOLAR", origen.dato + " | cola=" + cola);
        }
        if (imprimir) {
            System.out.println("  Inicio: encolar " + origen.dato + "  " + cola);
        }

        while (!cola.estaVacia()) {
            Vertice<T> actual = cola.desencolar();
            orden.insertar(actual.dato);
            if (eventos != null) {
                eventos.registrar("DESENCOLAR", actual.dato + " | cola=" + cola);
            }
            if (imprimir) {
                System.out.println("  Desencolar " + actual.dato + " (nivel " + actual.nivel + ")  " + cola);
            }
            for (Vertice<T> ady : actual.adyacentes) {
                if (!ady.visitado) {
                    ady.visitado = true;
                    ady.padre = actual;
                    ady.nivel = actual.nivel + 1;
                    cola.encolar(ady);
                    if (eventos != null) {
                        eventos.registrar("ENCOLAR", ady.dato + " | cola=" + cola);
                    }
                    if (imprimir) {
                        System.out.println("    Encolar " + ady.dato + "  " + cola);
                    }
                }
            }
        }
        if (eventos != null) {
            eventos.registrar("FIN", "cola vacía; vértices visitados: " + orden.tamanio());
        }
        return orden;
    }

    /** Variante sencilla sin trazas. */
    public static <T> ListaEnlazada<T> recorrer(Grafo<T> grafo, T inicio) {
        return recorrer(grafo, inicio, null, false);
    }

    /** Asignaturas que dependen (directa o indirectamente) de "origen", sin incluir a "origen". */
    public static <T> ListaEnlazada<T> dependientes(Grafo<T> grafo, T origen) {
        ListaEnlazada<T> orden = recorrer(grafo, origen);
        if (orden == null) {
            return null;
        }
        orden.eliminar(origen);
        return orden;
    }

    /** Nivel (distancia en aristas) del vértice tras el último BFS; -1 si no fue alcanzado. */
    public static <T> int nivelDe(Grafo<T> grafo, T dato) {
        Vertice<T> v = grafo.buscarVertice(dato);
        if (v == null || !v.visitado) {
            return -1;
        }
        return v.nivel;
    }

    /**
     * Ruta más corta (en número de aristas) de origen a destino, o null si no existe.
     * La ruta se reconstruye siguiendo los "padres" hacia atrás y apilándolos en una Pila,
     * de modo que al desapilar salen en el orden correcto origen -> destino.
     */
    public static <T> ListaEnlazada<T> buscarRuta(Grafo<T> grafo, T origen, T destino) {
        Vertice<T> vo = grafo.buscarVertice(origen);
        Vertice<T> vd = grafo.buscarVertice(destino);
        if (vo == null || vd == null) {
            return null;
        }
        recorrer(grafo, origen);
        if (!vd.visitado) {
            return null;
        }
        Pila<T> pila = new Pila<>();
        Vertice<T> actual = vd;
        while (actual != null) {
            pila.apilar(actual.dato);
            actual = actual.padre;
        }
        ListaEnlazada<T> ruta = new ListaEnlazada<>();
        while (!pila.estaVacia()) {
            ruta.insertar(pila.desapilar());
        }
        return ruta;
    }
}

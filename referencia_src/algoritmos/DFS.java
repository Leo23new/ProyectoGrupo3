package algoritmos;

import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import estructuras.Pila;
import estructuras.Vertice;
import ia.RegistroEventos;

/**
 * DFS - Búsqueda en PROFUNDIDAD. Usa la Pila propia del proyecto (versión iterativa).
 * Avanza por un camino hasta el fondo y retrocede (backtracking) cuando no hay más adyacentes.
 * Complejidad O(V + A).
 *
 * Un vértice puede apilarse varias veces (desde distintos vecinos); por eso al desapilar se
 * comprueba si ya fue visitado y, si es así, se omite.
 */
public class DFS {

    public static <T> ListaEnlazada<T> recorrer(Grafo<T> grafo, T inicio, RegistroEventos eventos, boolean imprimir) {
        Vertice<T> origen = grafo.buscarVertice(inicio);
        if (origen == null) {
            return null;
        }
        grafo.reiniciarMarcas();
        ListaEnlazada<T> orden = new ListaEnlazada<>();
        Pila<Vertice<T>> pila = new Pila<>();

        pila.apilar(origen);
        if (eventos != null) {
            eventos.registrar("INICIO", "DFS desde " + inicio);
            eventos.registrar("APILAR", origen.dato + " | pila=" + pila);
        }
        if (imprimir) {
            System.out.println("  Inicio: apilar " + origen.dato + "  " + pila);
        }

        while (!pila.estaVacia()) {
            Vertice<T> actual = pila.desapilar();
            if (actual.visitado) {
                if (eventos != null) {
                    eventos.registrar("OMITIR", actual.dato + " ya visitado | pila=" + pila);
                }
                if (imprimir) {
                    System.out.println("  Desapilar " + actual.dato + " (ya visitado, se omite)  " + pila);
                }
                continue;
            }
            actual.visitado = true;
            orden.insertar(actual.dato);
            if (eventos != null) {
                eventos.registrar("VISITAR", actual.dato + " | pila=" + pila);
            }
            if (imprimir) {
                System.out.println("  Desapilar y visitar " + actual.dato + "  " + pila);
            }
            // Se apilan en orden inverso para que el PRIMER adyacente sea el primero en visitarse.
            ListaSecuencial<Vertice<T>> vecinos = ListaSecuencial.desde(actual.adyacentes);
            for (int i = vecinos.tamanio() - 1; i >= 0; i--) {
                Vertice<T> ady = vecinos.obtener(i);
                if (!ady.visitado) {
                    ady.padre = actual;
                    pila.apilar(ady);
                    if (eventos != null) {
                        eventos.registrar("APILAR", ady.dato + " | pila=" + pila);
                    }
                    if (imprimir) {
                        System.out.println("    Apilar " + ady.dato + "  " + pila);
                    }
                }
            }
        }
        if (eventos != null) {
            eventos.registrar("FIN", "pila vacía; vértices visitados: " + orden.tamanio());
        }
        return orden;
    }

    public static <T> ListaEnlazada<T> recorrer(Grafo<T> grafo, T inicio) {
        return recorrer(grafo, inicio, null, false);
    }

    /** ¿Existe un camino dirigido de origen a destino? */
    public static <T> boolean existeRuta(Grafo<T> grafo, T origen, T destino) {
        ListaEnlazada<T> visitados = recorrer(grafo, origen);
        return visitados != null && visitados.contiene(destino);
    }

    /**
     * Detecta dependencias circulares (ciclos) con DFS recursivo y colores:
     * 0 = blanco (sin visitar), 1 = gris (en la pila de llamadas), 2 = negro (terminado).
     * Si desde un vértice se llega a uno GRIS, existe un ciclo (arista de retroceso).
     * La recursión usa la pila de llamadas de Java, equivalente a la Pila explícita del DFS iterativo.
     * Devuelve una lista de ciclos; cada ciclo es una lista de códigos, p. ej. [A, B, C, A].
     */
    public static <T> ListaEnlazada<ListaEnlazada<T>> detectarCiclos(Grafo<T> grafo) {
        grafo.reiniciarMarcas();
        ListaEnlazada<ListaEnlazada<T>> ciclos = new ListaEnlazada<>();
        for (Vertice<T> v : grafo.getVertices()) {
            if (v.estado == 0) {
                visitarRec(v, ciclos);
            }
        }
        return ciclos;
    }

    private static <T> void visitarRec(Vertice<T> v, ListaEnlazada<ListaEnlazada<T>> ciclos) {
        v.estado = 1;
        for (Vertice<T> w : v.adyacentes) {
            if (w.estado == 0) {
                w.padre = v;
                visitarRec(w, ciclos);
            } else if (w.estado == 1) {
                // Arista de retroceso v -> w: se reconstruye el ciclo siguiendo los padres.
                ListaEnlazada<T> ciclo = new ListaEnlazada<>();
                Vertice<T> x = v;
                while (x != w) {
                    ciclo.insertarInicio(x.dato);
                    x = x.padre;
                }
                ciclo.insertarInicio(w.dato);
                ciclo.insertar(w.dato);
                ciclos.insertar(ciclo);
            }
        }
        v.estado = 2;
    }
}

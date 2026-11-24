package algoritmos;

import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import estructuras.Pila;
import estructuras.Vertice;
import ia.RegistroEventos;

/**
 * DFS - Busqueda en PROFUNDIDAD.
 * Usa la Pila propia del proyecto.
 *
 * Implementacion iterativa.
 * Complejidad: O(V + A).
 */
public class DFS {

    /**
     * Recorre el grafo en profundidad desde inicio.
     */
    public static <T> ListaEnlazada<T> recorrer(
            Grafo<T> grafo,
            T inicio,
            RegistroEventos eventos,
            boolean imprimir) {

        if (grafo == null) {
            return null;
        }

        Vertice<T> origen = grafo.buscarVertice(inicio);

        if (origen == null) {
            return null;
        }

        grafo.reiniciarMarcas();

        ListaEnlazada<T> orden = new ListaEnlazada<>();
        Pila<Vertice<T>> pila = new Pila<>();

        origen.padre = null;
        pila.apilar(origen);

        if (eventos != null) {
            eventos.registrar("INICIO", "DFS desde " + inicio);
            eventos.registrar("APILAR", origen.dato + " | pila=" + pila, String.valueOf(origen.dato));
        }

        if (imprimir) {
            System.out.println("  Inicio: apilar " + origen.dato + "  " + pila);
        }

        while (!pila.estaVacia()) {

            Vertice<T> actual = pila.desapilar();

            if (actual.visitado) {

                if (eventos != null) {
                    eventos.registrar(
                            "OMITIR",
                            actual.dato + " ya visitado | pila=" + pila,
                            String.valueOf(actual.dato)
                    );
                }

                if (imprimir) {
                    System.out.println(
                            "  Desapilar " + actual.dato
                            + " (ya visitado, se omite)  " + pila
                    );
                }

                continue;
            }

            actual.visitado = true;
            orden.insertar(actual.dato);

            if (eventos != null) {
                eventos.registrar(
                        "VISITAR",
                        actual.dato + " | pila=" + pila,
                        String.valueOf(actual.dato)
                );
            }

            if (imprimir) {
                System.out.println(
                        "  Desapilar y visitar " + actual.dato + "  " + pila
                );
            }

            // Se apilan en orden inverso para visitar primero
            // el primer adyacente de la lista.
            ListaSecuencial<Vertice<T>> vecinos =
                    ListaSecuencial.desde(actual.adyacentes);

            for (int i = vecinos.tamanio() - 1; i >= 0; i--) {

                Vertice<T> ady = vecinos.obtener(i);

                if (!ady.visitado) {

                    ady.padre = actual;
                    pila.apilar(ady);

                    if (eventos != null) {
                        eventos.registrar(
                                "APILAR",
                                ady.dato + " | pila=" + pila,
                                String.valueOf(ady.dato)
                        );
                    }

                    if (imprimir) {
                        System.out.println(
                                "    Apilar " + ady.dato + "  " + pila
                        );
                    }
                }
            }
        }

        if (eventos != null) {
            eventos.registrar(
                    "FIN",
                    "pila vacía; vértices visitados: " + orden.tamanio()
            );
        }

        return orden;
    }

    /**
     * Variante sencilla sin trazas.
     */
    public static <T> ListaEnlazada<T> recorrer(
            Grafo<T> grafo,
            T inicio) {

        return recorrer(grafo, inicio, null, false);
    }

    /**
     * Indica si existe una ruta dirigida entre dos vertices.
     */
    public static <T> boolean existeRuta(
            Grafo<T> grafo,
            T origen,
            T destino) {

        ListaEnlazada<T> visitados = recorrer(grafo, origen);

        return visitados != null && visitados.contiene(destino);
    }

    /**
     * Detecta ciclos dirigidos.
     *
     * Estados:
     * 0 = blanco (no visitado)
     * 1 = gris (en exploracion)
     * 2 = negro (terminado)
     *
     * Si se encuentra una arista hacia un vertice gris,
     * se detecta un ciclo.
     *
     * Devuelve los ciclos encontrados.
     */
    public static <T> ListaEnlazada<ListaEnlazada<T>> detectarCiclos(
            Grafo<T> grafo) {

        ListaEnlazada<ListaEnlazada<T>> ciclos =
                new ListaEnlazada<>();

        if (grafo == null) {
            return ciclos;
        }

        grafo.reiniciarMarcas();

        for (Vertice<T> v : grafo.getVertices()) {

            if (v.estado == 0) {
                visitarRec(v, ciclos);
            }
        }

        return ciclos;
    }

    /**
     * DFS recursivo para detectar ciclos.
     */
    private static <T> void visitarRec(
            Vertice<T> v,
            ListaEnlazada<ListaEnlazada<T>> ciclos) {

        v.estado = 1;

        for (Vertice<T> w : v.adyacentes) {

            if (w.estado == 0) {

                w.padre = v;
                visitarRec(w, ciclos);

            } else if (w.estado == 1) {

                // Arista de retroceso: v -> w.
                // Se reconstruye el ciclo siguiendo los padres.
                ListaEnlazada<T> ciclo = new ListaEnlazada<>();

                Vertice<T> x = v;
                boolean valido = true;

                while (x != null && x != w) {
                    ciclo.insertarInicio(x.dato);
                    x = x.padre;
                }

                // Verifica que w realmente sea ancestro de v.
                if (x != w) {
                    valido = false;
                }

                if (valido) {
                    ciclo.insertarInicio(w.dato);
                    ciclo.insertar(w.dato);
                    ciclos.insertar(ciclo);
                }
            }
        }

        v.estado = 2;
    }
}

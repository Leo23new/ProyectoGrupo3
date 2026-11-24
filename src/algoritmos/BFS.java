package algoritmos;

import estructuras.Cola;
import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import estructuras.Vertice;
import ia.RegistroEventos;

/**
 * BFS - Busqueda en ANCHURA.
 * Usa la Cola propia del proyecto.
 *
 * Recorre el grafo por niveles.
 * Complejidad: O(V + A).
 *
 * Los vertices se marcan al encolarlos para evitar duplicados.
 */
public class BFS {

    /**
     * Recorre el grafo desde inicio.
     *
     * Devuelve los datos en orden de visita.
     * Devuelve null si el grafo o el vertice inicial no existen.
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
        Cola<Vertice<T>> cola = new Cola<>();

        origen.visitado = true;
        origen.nivel = 0;
        origen.padre = null;

        cola.encolar(origen);

        if (eventos != null) {
            eventos.registrar("INICIO", "BFS desde " + inicio);
            eventos.registrar("ENCOLAR", origen.dato + " | cola=" + cola, String.valueOf(origen.dato));
        }

        if (imprimir) {
            System.out.println("  Inicio: encolar " + origen.dato + "  " + cola);
        }

        while (!cola.estaVacia()) {

            Vertice<T> actual = cola.desencolar();

            orden.insertar(actual.dato);

            if (eventos != null) {
                eventos.registrar(
                        "DESENCOLAR",
                        actual.dato + " | cola=" + cola,
                        String.valueOf(actual.dato)
                );
            }

            if (imprimir) {
                System.out.println(
                        "  Desencolar " + actual.dato
                        + " (nivel " + actual.nivel + ")  " + cola
                );
            }

            for (Vertice<T> ady : actual.adyacentes) {

                if (!ady.visitado) {

                    // Se marca al encolar, no al desencolar.
                    ady.visitado = true;
                    ady.padre = actual;
                    ady.nivel = actual.nivel + 1;

                    cola.encolar(ady);

                    if (eventos != null) {
                        eventos.registrar(
                                "ENCOLAR",
                                ady.dato + " | cola=" + cola,
                                String.valueOf(ady.dato)
                        );
                    }

                    if (imprimir) {
                        System.out.println(
                                "    Encolar " + ady.dato + "  " + cola
                        );
                    }
                }
            }
        }

        if (eventos != null) {
            eventos.registrar(
                    "FIN",
                    "cola vacía; vértices visitados: " + orden.tamanio()
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
     * Dependientes directos e indirectos del origen.
     * No incluye al propio origen.
     */
    public static <T> ListaEnlazada<T> dependientes(
            Grafo<T> grafo,
            T origen) {

        ListaEnlazada<T> orden = recorrer(grafo, origen);

        if (orden == null) {
            return null;
        }

        orden.eliminar(origen);

        return orden;
    }

    /**
     * Nivel de un vertice despues del ultimo BFS.
     * Devuelve -1 si no existe o no fue alcanzado.
     */
    public static <T> int nivelDe(Grafo<T> grafo, T dato) {

        if (grafo == null) {
            return -1;
        }

        Vertice<T> v = grafo.buscarVertice(dato);

        if (v == null || !v.visitado) {
            return -1;
        }

        return v.nivel;
    }

    /**
     * Ruta mas corta en numero de aristas.
     * Devuelve null si no existe una ruta.
     *
     * La ruta se reconstruye desde el destino hacia el origen
     * utilizando los padres y una pila.
     */
    public static <T> ListaEnlazada<T> buscarRuta(
            Grafo<T> grafo,
            T origen,
            T destino) {

        if (grafo == null) {
            return null;
        }

        Vertice<T> vo = grafo.buscarVertice(origen);
        Vertice<T> vd = grafo.buscarVertice(destino);

        if (vo == null || vd == null) {
            return null;
        }

        // BFS establece los padres y niveles.
        recorrer(grafo, origen);

        if (!vd.visitado) {
            return null;
        }

        Pila<T> pila = new Pila<>();
        Vertice<T> actual = vd;

        // Reconstruccion segura: debe llegar al origen.
        while (actual != null) {
            pila.apilar(actual.dato);

            if (actual == vo) {
                break;
            }

            actual = actual.padre;
        }

        // Si no llegamos al origen, la ruta es inconsistente.
        if (actual != vo) {
            return null;
        }

        ListaEnlazada<T> ruta = new ListaEnlazada<>();

        while (!pila.estaVacia()) {
            ruta.insertar(pila.desapilar());
        }

        return ruta;
    }
}

package pruebas;

import algoritmos.BFS;
import algoritmos.Busqueda;
import algoritmos.DFS;
import algoritmos.Ordenamiento;
import estructuras.ArbolBST;
import estructuras.Cola;
import estructuras.Grafo;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import estructuras.Pila;
import etl.Extract;
import etl.Transform;
import ia.RegistroEventos;
import model.Estudiante;
import model.RegistroCrudo;
import model.TipoError;

import java.util.Comparator;

/**
 * Pruebas automáticas SIN librerías externas (no necesitan JUnit).
 * Ejecutar desde la raíz del proyecto:  java -cp out pruebas.Pruebas
 * Termina con código 1 si alguna prueba falla.
 */
public class Pruebas {
    static int ok = 0;
    static int fallos = 0;

    static void verificar(String nombre, boolean condicion) {
        if (condicion) {
            ok++;
            System.out.println("  [OK]    " + nombre);
        } else {
            fallos++;
            System.out.println("  [FALLO] " + nombre);
        }
    }

    public static void main(String[] args) {
        pruebasCola();
        pruebasPila();
        pruebasLista();
        pruebasBST();
        pruebasGrafo();
        pruebasOrdenamiento();
        pruebasETL();
        System.out.println();
        System.out.println("Resultado: " + ok + " correctas, " + fallos + " fallidas.");
        if (fallos > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------ estructuras

    static void pruebasCola() {
        System.out.println("COLA (FIFO)");
        Cola<Integer> c = new Cola<>();
        verificar("nueva cola está vacía", c.estaVacia() && c.tamanio() == 0);
        verificar("desencolar en vacía devuelve null", c.desencolar() == null);
        c.encolar(1);
        c.encolar(2);
        c.encolar(3);
        verificar("frente es el primero que entró", c.frente() == 1);
        verificar("tamaño 3", c.tamanio() == 3);
        verificar("sale 1 primero", c.desencolar() == 1);
        verificar("sale 2 después", c.desencolar() == 2);
        c.encolar(4);
        verificar("sale 3", c.desencolar() == 3);
        verificar("sale 4", c.desencolar() == 4);
        verificar("queda vacía y reutilizable", c.estaVacia());
        c.encolar(9);
        verificar("se puede reutilizar tras vaciarse", c.frente() == 9 && c.tamanio() == 1);
    }

    static void pruebasPila() {
        System.out.println("PILA (LIFO)");
        Pila<String> p = new Pila<>();
        verificar("nueva pila vacía", p.estaVacia());
        verificar("desapilar en vacía devuelve null", p.desapilar() == null);
        p.apilar("A");
        p.apilar("B");
        p.apilar("C");
        verificar("cima es el último apilado", p.cima().equals("C"));
        verificar("sale C primero", p.desapilar().equals("C"));
        verificar("sale B", p.desapilar().equals("B"));
        verificar("tamaño 1", p.tamanio() == 1);
        verificar("sale A", p.desapilar().equals("A"));
        verificar("queda vacía", p.estaVacia());
    }

    static void pruebasLista() {
        System.out.println("LISTA ENLAZADA");
        ListaEnlazada<Integer> l = new ListaEnlazada<>();
        l.insertar(10);
        l.insertar(20);
        l.insertar(30);
        l.insertarInicio(5);
        verificar("orden 5,10,20,30", l.toString().equals("[5 -> 10 -> 20 -> 30]"));
        verificar("obtener(2) = 20", l.obtener(2) == 20);
        verificar("buscar con criterio", l.buscar(x -> x > 15) == 20);
        verificar("eliminar un elemento del medio", l.eliminar(20) && l.tamanio() == 3);
        verificar("eliminar la cola actualiza el final", l.eliminar(30));
        l.insertar(99);
        verificar("insertar tras eliminar la cola", l.toString().equals("[5 -> 10 -> 99]"));
        verificar("eliminar inexistente devuelve false", !l.eliminar(1234));
        verificar("eliminar la cabeza", l.eliminar(5) && l.obtener(0) == 10);
    }

    static void pruebasBST() {
        System.out.println("BST");
        ArbolBST<Integer> t = new ArbolBST<>();
        int[] claves = {50, 30, 70, 20, 40, 60, 80, 35, 45};
        for (int k : claves) {
            t.insertar(k);
        }
        verificar("9 nodos", t.contarNodos() == 9);
        verificar("inOrden ordenado", t.inOrden().toString().equals("[20 -> 30 -> 35 -> 40 -> 45 -> 50 -> 60 -> 70 -> 80]"));
        verificar("preOrden", t.preOrden().toString().equals("[50 -> 30 -> 20 -> 40 -> 35 -> 45 -> 70 -> 60 -> 80]"));
        verificar("postOrden", t.postOrden().toString().equals("[20 -> 35 -> 45 -> 40 -> 30 -> 60 -> 80 -> 70 -> 50]"));
        verificar("altura 4", t.altura() == 4);
        verificar("no permite duplicados", !t.insertar(40) && t.contarNodos() == 9);
        verificar("buscar existente", t.buscar(60) != null);
        verificar("buscar inexistente", t.buscar(99) == null);
        verificar("eliminar hoja (20)", t.eliminar(20) && t.buscar(20) == null);
        verificar("eliminar nodo con un hijo (30 tras quitar 20)", t.eliminar(30));
        verificar("eliminar nodo con dos hijos (raíz 50)", t.eliminar(50));
        verificar("inOrden sigue ordenado tras eliminar", t.inOrden().toString().equals("[35 -> 40 -> 45 -> 60 -> 70 -> 80]"));
        verificar("eliminar inexistente devuelve false", !t.eliminar(1000));

        ArbolBST<Integer> degenerado = new ArbolBST<>();
        for (int i = 1; i <= 10; i++) {
            degenerado.insertar(i);
        }
        verificar("claves ordenadas -> árbol degenerado (altura = n)", degenerado.altura() == 10);
        verificar("altura mínima teórica de 10 nodos = 4", degenerado.alturaMinimaTeorica() == 4);
    }

    static Grafo<String> grafoEjemplo() {
        Grafo<String> g = new Grafo<>();
        String[] v = {"A", "B", "C", "D", "E", "F"};
        for (String s : v) {
            g.agregarVertice(s);
        }
        g.agregarArista("A", "B");
        g.agregarArista("A", "C");
        g.agregarArista("B", "D");
        g.agregarArista("C", "D");
        g.agregarArista("D", "E");
        return g; // F queda aislado
    }

    static void pruebasGrafo() {
        System.out.println("GRAFO, BFS, DFS");
        Grafo<String> g = grafoEjemplo();
        verificar("6 vértices y 5 aristas", g.numVertices() == 6 && g.numAristas() == 5);
        verificar("no duplica aristas", !g.agregarArista("A", "B") && g.numAristas() == 5);
        verificar("no crea arista con vértice inexistente", !g.agregarArista("A", "Z"));
        verificar("BFS por niveles A,B,C,D,E", BFS.recorrer(g, "A").toString().equals("[A -> B -> C -> D -> E]"));
        verificar("DFS en profundidad A,B,D,E,C", DFS.recorrer(g, "A").toString().equals("[A -> B -> D -> E -> C]"));
        verificar("BFS desde vértice inexistente = null", BFS.recorrer(g, "Z") == null);
        verificar("ruta más corta A->E tiene 4 vértices", BFS.buscarRuta(g, "A", "E").tamanio() == 4);
        verificar("ruta inexistente A->F = null", BFS.buscarRuta(g, "A", "F") == null);
        verificar("existeRuta A->E", DFS.existeRuta(g, "A", "E"));
        verificar("no existeRuta E->A", !DFS.existeRuta(g, "E", "A"));
        verificar("predecesores de D = B,C", g.predecesores("D").toString().equals("[B -> C]"));
        verificar("dependientes de B = D,E", BFS.dependientes(g, "B").toString().equals("[D -> E]"));
        verificar("grafo sin ciclos", DFS.detectarCiclos(g).estaVacia());
        g.agregarArista("E", "B");
        ListaEnlazada<ListaEnlazada<String>> ciclos = DFS.detectarCiclos(g);
        verificar("detecta ciclo B->D->E->B", !ciclos.estaVacia()
                && ciclos.obtener(0).toString().equals("[B -> D -> E -> B]"));
        RegistroEventos ev = new RegistroEventos();
        BFS.recorrer(g, "A", ev, false);
        verificar("BFS registra eventos para el flujograma dinámico", ev.contar("DESENCOLAR") == 5 && ev.contar("FIN") == 1);
    }

    // ------------------------------------------------------------------ algoritmos

    static void pruebasOrdenamiento() {
        System.out.println("ORDENAMIENTO Y BÚSQUEDA");
        int[] datos = {5, 3, 9, 1, 7, 3, 8, 2};
        Comparator<Integer> asc = Comparator.naturalOrder();
        ListaSecuencial<Integer> a = new ListaSecuencial<>();
        ListaSecuencial<Integer> b = new ListaSecuencial<>();
        ListaSecuencial<Integer> c = new ListaSecuencial<>();
        for (int d : datos) {
            a.agregar(d);
            b.agregar(d);
            c.agregar(d);
        }
        Ordenamiento.burbuja(a, asc);
        Ordenamiento.insercion(b, asc);
        Ordenamiento.seleccion(c, asc);
        verificar("burbuja ordena", Ordenamiento.estaOrdenada(a, asc));
        verificar("inserción ordena", Ordenamiento.estaOrdenada(b, asc));
        verificar("selección ordena", Ordenamiento.estaOrdenada(c, asc));
        verificar("conserva todos los elementos", a.tamanio() == 8 && a.obtener(0) == 1 && a.obtener(7) == 9);
        verificar("búsqueda binaria encuentra 7", Busqueda.binaria(b, 7, asc) != null);
        verificar("búsqueda binaria no encuentra 4", Busqueda.binaria(b, 4, asc) == null);
        ListaEnlazada<Estudiante> lista = new ListaEnlazada<>();
        lista.insertar(new Estudiante("1801000001", "Ana", "Mora", 20, "Soft", 3, 8.0));
        lista.insertar(new Estudiante("1801000002", "Luis", "Vega", 21, "Soft", 4, 7.0));
        verificar("búsqueda secuencial por cédula", Busqueda.secuencial(lista, "1801000002") != null
                && Busqueda.ultimasComparaciones == 2);
    }

    // ------------------------------------------------------------------------- ETL

    static void pruebasETL() {
        System.out.println("ETL con el dataset de data/");
        RegistroEventos ev = new RegistroEventos();
        Cola<RegistroCrudo> cola = new Extract().extraer("data", ev);
        verificar("Extract encola 109 registros", cola.tamanio() == 109);
        Transform t = new Transform();
        t.transformar(cola, ev);
        verificar("la cola queda vacía tras Transform", cola.estaVacia());
        verificar("109 procesados = 81 válidos + 28 inválidos",
                t.totalProcesados() == 109 && t.totalValidos() == 81 && t.totalInvalidos() == 28);
        verificar("22 estudiantes válidos", t.estudiantes.tamanio() == 22);
        verificar("12 asignaturas válidas", t.asignaturas.tamanio() == 12);
        verificar("34 matrículas válidas", t.matriculas.tamanio() == 34);
        verificar("13 prerrequisitos válidos", t.prerrequisitos.tamanio() == 13);
        verificar("pila con 28 errores", t.errores.tamanio() == 28);
        verificar("4 identificadores incorrectos", t.erroresPorTipo[TipoError.ID_INVALIDO.ordinal()] == 4);
        verificar("5 duplicados", t.erroresPorTipo[TipoError.DUPLICADO.ordinal()] == 5);
        verificar("4 referencias inválidas", t.erroresPorTipo[TipoError.REFERENCIA_INVALIDA.ordinal()] == 4);
        Estudiante ana = Busqueda.secuencial(t.estudiantes, "1801000001");
        verificar("normaliza nombres (ana lucia -> Ana Lucia)", ana != null && ana.getNombres().equals("Ana Lucia")
                && ana.getApellidos().equals("Mora Paz"));
        Estudiante carlos = Busqueda.secuencial(t.estudiantes, "1801000002");
        verificar("normaliza mayúsculas y espacios dobles", carlos != null
                && carlos.getNombres().equals("Carlos Andres") && carlos.getApellidos().equals("Vega Rojas"));
        verificar("registros inválidos no entran a la lista", Busqueda.secuencial(t.estudiantes, "18010023") == null);
        verificar("el evento ERROR coincide con la pila", ev.contar("ERROR") == 28 && ev.contar("VALIDO") == 81);

        Grafo<String> g = new Grafo<>();
        t.asignaturas.forEach(a -> g.agregarVertice(a.getCodigo()));
        t.prerrequisitos.forEach(p -> g.agregarArista(p.getOrigen(), p.getDependiente()));
        verificar("grafo del dataset: 12 vértices, 13 aristas", g.numVertices() == 12 && g.numAristas() == 13);
        verificar("el dataset contiene el ciclo SIS201 -> SIS202 -> SIS301", !DFS.detectarCiclos(g).estaVacia());
    }
}

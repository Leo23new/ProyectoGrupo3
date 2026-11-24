import algoritmos.BFS;
import algoritmos.Busqueda;
import algoritmos.Comparativa;
import algoritmos.DFS;
import algoritmos.Ordenamiento;
import estructuras.ArbolBST;
import estructuras.Cola;
import estructuras.Grafo;
import estructuras.ListaDoble;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import estructuras.Pila;
import etl.Extract;
import etl.Load;
import etl.Transform;
import ia.AnalizadorIA;
import ia.ClienteIA;
import ia.GeneradorFlujograma;
import ia.RegistroEventos;
import model.Asignatura;
import model.Estudiante;
import model.Matricula;
import model.Prerrequisito;
import model.RegistroCrudo;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.awt.Desktop;
import java.net.URI;

import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;

/**
 * SmartETL-DS + IA  -  Grupo 3  -  Estructuras de Datos (UTA).
 * Menú de consola que integra ETL, estructuras propias, BST, grafos, BFS/DFS e IA.
 */
public class Main {
    static final String DATOS = "data";
    static final String SALIDA = "output";

        static Scanner in = new Scanner(System.in);
    // ---- estado del sistema ----
    static Cola<RegistroCrudo> cola = new Cola<>();
    static Transform transform = new Transform();
    static final Extract extract = new Extract();
    static final Load load = new Load();
    static RegistroEventos eventosETL = new RegistroEventos();
    static boolean extraido = false;
    static boolean transformado = false;
    static boolean cargado = false;
    static ArbolBST<Estudiante> bst = null;
    static Grafo<String> grafo = null;
    static RegistroEventos ultimoBFS = null;
    static RegistroEventos ultimoDFS = null;

    // ---- IA ----
    static final ClienteIA ia = new ClienteIA();
    static final GeneradorFlujograma flujogramas = new GeneradorFlujograma(ia, SALIDA);
    static final AnalizadorIA analizador = new AnalizadorIA(ia, SALIDA);

    /** Historial de opciones usadas (Lista doblemente enlazada). */
    static final ListaDoble<String> historial = new ListaDoble<>();

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--web")) {
            int puerto = args.length > 1 ? Integer.parseInt(args[1]) : 8080;
            Servidor.iniciar(puerto);
            return;
        }
        boolean demo = args.length > 0 && args[0].equals("--demo");
        if (demo) {
            ejecutarDemo();
            return;
        }
        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEntero("Opción: ");
            if (opcion != 0) {
                historial.insertarFinal("Opción " + opcion);
            }
            try {
                ejecutar(opcion);
            } catch (RuntimeException e) {
                System.out.println("[ERROR] " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        System.out.println("Hasta luego.");
    }

    // ======================================================================= menú

    static void mostrarMenu() {
        System.out.println();
        System.out.println("====================================================");
        System.out.println("SMARTETL-DS + IA - ESTRUCTURAS DE DATOS (Grupo 3)");
        System.out.println("====================================================");
        System.out.println("ETL");
        System.out.println(" 1. Extraer datos            2. Mostrar cola de extracción");
        System.out.println(" 3. Transformar datos        4. Mostrar registros válidos");
        System.out.println(" 5. Mostrar pila de errores  6. Cargar datos");
        System.out.println("ESTRUCTURAS Y ALGORITMOS");
        System.out.println(" 7. Mostrar lista enlazada   8. Buscar registro");
        System.out.println(" 9. Ordenar registros");
        System.out.println("ÁRBOLES");
        System.out.println("10. Construir BST   11. Insertar   12. Buscar   13. Eliminar");
        System.out.println("14. InOrden  15. PreOrden  16. PostOrden  17. Altura y número de nodos");
        System.out.println("GRAFOS");
        System.out.println("18. Construir grafo  19. Mostrar conexiones  20. BFS  21. DFS");
        System.out.println("22. Buscar ruta      23. Detectar ciclos");
        System.out.println("INTELIGENCIA ARTIFICIAL (" + (ia.disponible() ? "API conectada: " + ia.getProveedor() : "modo local, sin API key") + ")");        System.out.println("24. Flujograma ETL   25. Flujograma de una estructura");
        System.out.println("26. Flujograma BFS   27. Flujograma DFS");
        System.out.println("28. Explicar árbol   29. Explicar grafo");
        System.out.println("30. Analizar errores ETL   31. Generar documentación (7 flujogramas + doc)");
        System.out.println("REPORTES");
        System.out.println("32. Generar reporte técnico   33. Exportar resultados");
        System.out.println("EXTRA");
        System.out.println("34. DEMO completa automática  35. Historial de opciones");
        System.out.println("36. MODO GUIADO (paso a paso)");
        System.out.println("37. Visualizar grafo en navegador");
        System.out.println(" 0. Salir");
        System.out.println("====================================================");
    }

    static void ejecutar(int opcion) {
        switch (opcion) {
            case 0: break;
            case 1: opExtraer(); break;
            case 2: opMostrarCola(); break;
            case 3: opTransformar(); break;
            case 4: opMostrarValidos(); break;
            case 5: opMostrarErrores(); break;
            case 6: opCargar(); break;
            case 7: opMostrarLista(); break;
            case 8: opBuscarRegistro(); break;
            case 9: opOrdenar(); break;
            case 10: opConstruirBST(); break;
            case 11: opInsertarBST(); break;
            case 12: opBuscarBST(); break;
            case 13: opEliminarBST(); break;
            case 14: opRecorrido(1); break;
            case 15: opRecorrido(2); break;
            case 16: opRecorrido(3); break;
            case 17: opAlturaNodos(); break;
            case 18: opConstruirGrafo(); break;
            case 19: opMostrarConexiones(); break;
            case 20: opRecorridoGrafo(true); break;
            case 21: opRecorridoGrafo(false); break;
            case 22: opBuscarRuta(); break;
            case 23: opDetectarCiclos(); break;
            case 24: opFlujogramaETL(); break;
            case 25: opFlujogramaEstructura(); break;
            case 26: opFlujogramaRecorrido(true); break;
            case 27: opFlujogramaRecorrido(false); break;
            case 28: opExplicarArbol(); break;
            case 29: opExplicarGrafo(); break;
            case 30: opAnalizarErrores(); break;
            case 31: opGenerarDocumentacion(); break;
            case 32: opReporteTecnico(); break;
            case 33: opExportar(); break;
            case 34: ejecutarDemo(); break;
            case 35: historial.mostrarAtras(20); break;
            case 36: opModoGuiado(); break;
            case 37: opVisualizarGrafo(); break;
            default: System.out.println("Opción no válida.");
        }
    }

    // ======================================================================== ETL

    static void opExtraer() {
        // Reinicia todo el pipeline para no duplicar datos si se vuelve a extraer.
        cola = new Cola<>();
        transform = new Transform();
        eventosETL = new RegistroEventos();
        bst = null;
        grafo = null;
        transformado = false;
        cargado = false;
        cola = extract.extraer(DATOS, eventosETL);
        extraido = true;
        System.out.println("Extract completo: " + cola.tamanio() + " registros encolados desde " + DATOS + "/ (FIFO).");
    }

    static boolean requiereTransformado() {
        if (!transformado) {
            System.out.println("Primero ejecute 1 (Extraer) y 3 (Transformar).");
            return false;
        }
        return true;
    }

    static void opMostrarCola() {
        if (!extraido) {
            System.out.println("Primero ejecute 1 (Extraer).");
            return;
        }
        System.out.println("Cola de extracción (" + cola.tamanio() + " registros). Se muestran los primeros 15:");
        cola.mostrar(15);
    }

    static void opTransformar() {
        if (!extraido || cola.estaVacia()) {
            System.out.println("La cola está vacía. Ejecute 1 (Extraer) primero.");
            return;
        }
        int antes = cola.tamanio();
        transform.transformar(cola, eventosETL);
        transformado = true;
        System.out.println("Transform completo: " + antes + " registros desencolados.");
        System.out.println(transform.resumenErrores());
    }

    static void opMostrarValidos() {
        if (!requiereTransformado()) {
            return;
        }
        System.out.println("1. Estudiantes (" + transform.estudiantes.tamanio() + ")   2. Asignaturas ("
                + transform.asignaturas.tamanio() + ")   3. Matrículas (" + transform.matriculas.tamanio()
                + ")   4. Prerrequisitos (" + transform.prerrequisitos.tamanio() + ")");
        int f = leerEntero("¿Cuál mostrar? ");
        switch (f) {
            case 1: transform.estudiantes.mostrar(); break;
            case 2: transform.asignaturas.mostrar(); break;
            case 3: transform.matriculas.mostrar(); break;
            case 4: transform.prerrequisitos.mostrar(); break;
            default: System.out.println("Opción no válida.");
        }
    }

    static void opMostrarErrores() {
        if (!requiereTransformado()) {
            return;
        }
        System.out.println("Pila de errores (" + transform.errores.tamanio() + "). La cima es el ÚLTIMO error detectado:");
        transform.errores.mostrar();
        System.out.println();
        System.out.println(transform.resumenErrores());
    }

    static void opCargar() {
        if (!requiereTransformado()) {
            return;
        }
        try {
            load.cargar(transform, SALIDA);
            cargado = true;
            System.out.println("Load completo. Archivos escritos en " + SALIDA + "/");
        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo escribir: " + e.getMessage());
        }
    }

    // ====================================================== lista, búsqueda, orden

    static void opMostrarLista() {
        if (!requiereTransformado()) {
            return;
        }
        System.out.println("Lista enlazada de estudiantes válidos (" + transform.estudiantes.tamanio() + "):");
        transform.estudiantes.mostrar();
    }

    static void opBuscarRegistro() {
        if (!requiereTransformado()) {
            return;
        }
        String cedula = leerLinea("Cédula a buscar (10 dígitos): ");
        Estudiante e = Busqueda.secuencial(transform.estudiantes, cedula);
        System.out.println("Búsqueda SECUENCIAL en la lista: " + (e == null ? "no encontrado" : e)
                + "  (" + Busqueda.ultimasComparaciones + " comparaciones)");
        if (bst != null) {
            Estudiante b = bst.buscar(Estudiante.sonda(cedula));
            System.out.println("Búsqueda en el BST: " + (b == null ? "no encontrado" : b)
                    + "  (" + bst.getUltimasComparaciones() + " comparaciones)");
        } else {
            System.out.println("(Construya el BST con la opción 10 para comparar con el índice jerárquico.)");
        }
        String r = leerLinea("¿Ver comparativa con muchos registros (n=1000..20000)? (s/n): ");
        if (r.equalsIgnoreCase("s")) {
            Comparativa.compararBusquedas();
        }
    }

    static void opOrdenar() {
        if (!requiereTransformado()) {
            return;
        }
        System.out.println("Criterio: 1. Apellido  2. Cédula  3. Promedio (mayor a menor)");
        int c = leerEntero("Criterio: ");
        System.out.println("Algoritmo: 1. Burbuja  2. Inserción  3. Selección");
        int a = leerEntero("Algoritmo: ");
        Comparator<Estudiante> criterio;
        switch (c) {
            case 1: criterio = Comparator.comparing(Estudiante::getApellidos); break;
            case 3: criterio = (x, y) -> Double.compare(y.getPromedio(), x.getPromedio()); break;
            default: criterio = Comparator.comparing(Estudiante::getCedula);
        }
        ListaSecuencial<Estudiante> copia = ListaSecuencial.desde(transform.estudiantes);
        Ordenamiento.comparaciones = 0;
        Ordenamiento.intercambios = 0;
        switch (a) {
            case 1: Ordenamiento.burbuja(copia, criterio); break;
            case 3: Ordenamiento.seleccion(copia, criterio); break;
            default: Ordenamiento.insercion(copia, criterio);
        }
        System.out.println("Ordenado (" + Ordenamiento.comparaciones + " comparaciones, "
                + Ordenamiento.intercambios + " movimientos). Primeros 10:");
        copia.mostrar(10);
        String r = leerLinea("¿Ver comparativa de los 3 algoritmos con distintos tamaños? (s/n): ");
        if (r.equalsIgnoreCase("s")) {
            Comparativa.compararOrdenamientos();
        }
    }

    // ====================================================================== BST

    static boolean requiereBST() {
        if (bst == null) {
            System.out.println("Primero construya el BST (opción 10).");
            return false;
        }
        return true;
    }

    static void opConstruirBST() {
        if (!requiereTransformado()) {
            return;
        }
        bst = new ArbolBST<>(Estudiante::getCedula);
        for (Estudiante e : transform.estudiantes) {
            bst.insertar(e);
        }
        System.out.println("BST construido con " + bst.contarNodos() + " nodos, indexado por cédula.");
        System.out.println("Altura: " + bst.altura() + " niveles (mínima posible: " + bst.alturaMinimaTeorica() + ").");
        System.out.println(bst.dibujar());
    }

    static void opInsertarBST() {
        if (!requiereBST()) {
            return;
        }
        String cedula = leerLinea("Cédula (10 dígitos): ");
        if (!cedula.matches("\\d{10}")) {
            System.out.println("Cédula inválida: deben ser exactamente 10 dígitos.");
            return;
        }
        String nombres = Transform.normalizarTexto(leerLinea("Nombres: "));
        String apellidos = Transform.normalizarTexto(leerLinea("Apellidos: "));
        int edad = leerEntero("Edad (16-80): ");
        String carrera = Transform.normalizarTexto(leerLinea("Carrera: "));
        int semestre = leerEntero("Semestre (1-10): ");
        double promedio = leerDecimal("Promedio (0-10): ");
        if (nombres.isEmpty() || apellidos.isEmpty() || carrera.isEmpty() || edad < 16 || edad > 80
                || semestre < 1 || semestre > 10 || promedio < 0 || promedio > 10) {
            System.out.println("Datos incompletos o fuera de rango: no se inserta.");
            return;
        }
        Estudiante nuevo = new Estudiante(cedula, nombres, apellidos, edad, carrera, semestre, promedio);
        if (bst.insertar(nuevo)) {
            transform.estudiantes.insertar(nuevo);
            System.out.println("Insertado. Camino seguido en el árbol:");
            for (String paso : bst.getUltimaRuta()) {
                System.out.println("  - " + paso);
            }
        } else {
            System.out.println("Ya existe un estudiante con esa cédula.");
            for (String paso : bst.getUltimaRuta()) {
                System.out.println("  - " + paso);
            }
        }
    }

    static void opBuscarBST() {
        if (!requiereBST()) {
            return;
        }
        String cedula = leerLinea("Cédula a buscar: ");
        Estudiante e = bst.buscar(Estudiante.sonda(cedula));
        System.out.println("Camino de la búsqueda:");
        for (String paso : bst.getUltimaRuta()) {
            System.out.println("  - " + paso);
        }
        System.out.println(e == null ? "No encontrado." : "Encontrado: " + e);
        System.out.println("Comparaciones: " + bst.getUltimasComparaciones());
    }

    static void opEliminarBST() {
        if (!requiereBST()) {
            return;
        }
        String cedula = leerLinea("Cédula a eliminar: ");
        Estudiante sonda = Estudiante.sonda(cedula);
        if (bst.eliminar(sonda)) {
            transform.estudiantes.eliminar(sonda);
            System.out.println("Eliminado del BST y de la lista. Nodos restantes: " + bst.contarNodos());
        } else {
            System.out.println("No existe esa cédula en el BST.");
        }
    }

    static void opRecorrido(int tipo) {
        if (!requiereBST()) {
            return;
        }
        ListaEnlazada<Estudiante> r;
        String nombre;
        switch (tipo) {
            case 1: r = bst.inOrden(); nombre = "InOrden (izq, raíz, der) -> cédulas ordenadas"; break;
            case 2: r = bst.preOrden(); nombre = "PreOrden (raíz, izq, der)"; break;
            default: r = bst.postOrden(); nombre = "PostOrden (izq, der, raíz)";
        }
        System.out.println(nombre + ":");
        for (Estudiante e : r) {
            System.out.println("  " + e.getCedula() + "  " + e.getApellidos() + " " + e.getNombres());
        }
    }

    static void opAlturaNodos() {
        if (!requiereBST()) {
            return;
        }
        System.out.println("Nodos: " + bst.contarNodos() + " | Hojas: " + bst.contarHojas()
                + " | Altura: " + bst.altura() + " | Altura mínima teórica: " + bst.alturaMinimaTeorica());
        if (bst.altura() > 2 * bst.alturaMinimaTeorica()) {
            System.out.println("El árbol está DESBALANCEADO: la altura supera el doble de la mínima, "
                    + "por lo que las búsquedas tienden a O(n).");
        } else {
            System.out.println("El árbol está razonablemente balanceado (búsquedas cercanas a O(log n)).");
        }
    }

    // ==================================================================== GRAFO

    static boolean requiereGrafo() {
        if (grafo == null) {
            System.out.println("Primero construya el grafo (opción 18).");
            return false;
        }
        return true;
    }

    static String nombreAsignatura(String codigo) {
        Asignatura a = transform.asignaturas.buscar(x -> x.getCodigo().equals(codigo));
        return a == null ? codigo : codigo + " (" + a.getNombre() + ")";
    }

    static void opConstruirGrafo() {
        if (!requiereTransformado()) {
            return;
        }
        grafo = new Grafo<>();
        ultimoBFS = null;
        ultimoDFS = null;
        for (Asignatura a : transform.asignaturas) {
            grafo.agregarVertice(a.getCodigo());
        }
        for (Prerrequisito p : transform.prerrequisitos) {
            grafo.agregarArista(p.getOrigen(), p.getDependiente());
        }
        System.out.println("Grafo dirigido construido: " + grafo.numVertices() + " vértices (asignaturas) y "
                + grafo.numAristas() + " aristas (prerrequisitos).");
    }

    static void opMostrarConexiones() {
        if (!requiereGrafo()) {
            return;
        }
        System.out.println("Lista de adyacencia (prerrequisito -> asignaturas que dependen de él):");
        System.out.print(grafo.describir());
        String cod = leerLinea("Código para ver sus prerrequisitos directos (Enter para omitir): ").toUpperCase();
        if (!cod.isEmpty()) {
            if (grafo.buscarVertice(cod) == null) {
                System.out.println("No existe esa asignatura.");
            } else {
                ListaEnlazada<String> pre = grafo.predecesores(cod);
                System.out.println("Prerrequisitos directos de " + nombreAsignatura(cod) + ": "
                        + (pre.estaVacia() ? "ninguno" : pre.toString()));
            }
        }
    }

    static void opRecorridoGrafo(boolean esBFS) {
        if (!requiereGrafo()) {
            return;
        }
        String inicio = leerLinea("Código de inicio (ej. SIS101): ").toUpperCase();
        RegistroEventos ev = new RegistroEventos();
        System.out.println((esBFS ? "BFS (usa COLA)" : "DFS (usa PILA)") + " desde " + inicio + ":");
        ListaEnlazada<String> orden = recorrerGrafo(esBFS, inicio, ev, true);
        if (orden == null) {
            System.out.println("No existe ese vértice.");
            return;
        }
        System.out.println("Orden de visita: " + orden);
        if (esBFS) {
            System.out.print("Niveles: ");
            for (String c : orden) {
                System.out.print(c + "=" + BFS.nivelDe(grafo, c) + " ");
            }
            System.out.println();
        }
    }

    static ListaEnlazada<String> recorrerGrafo(boolean esBFS, String inicio, RegistroEventos eventos, boolean imprimir) {
        ListaEnlazada<String> orden = esBFS
                ? BFS.recorrer(grafo, inicio, eventos, imprimir)
                : DFS.recorrer(grafo, inicio, eventos, imprimir);
        if (orden != null) {
            if (esBFS) {
                ultimoBFS = eventos;
            } else {
                ultimoDFS = eventos;
            }
        }
        return orden;
    }

    static void opBuscarRuta() {
        if (!requiereGrafo()) {
            return;
        }
        String o = leerLinea("Asignatura origen: ").toUpperCase();
        String d = leerLinea("Asignatura destino: ").toUpperCase();
        if (grafo.buscarVertice(o) == null || grafo.buscarVertice(d) == null) {
            System.out.println("Alguna de las asignaturas no existe.");
            return;
        }
        System.out.println("¿Existe ruta (DFS)? " + (DFS.existeRuta(grafo, o, d) ? "Sí" : "No"));
        ListaEnlazada<String> ruta = BFS.buscarRuta(grafo, o, d);
        System.out.println(ruta == null ? "No hay ruta de " + o + " a " + d
                : "Ruta más corta (BFS): " + ruta + "  (" + (ruta.tamanio() - 1) + " aristas)");
        ListaEnlazada<String> dep = BFS.dependientes(grafo, o);
        System.out.println("Asignaturas que dependen directa o indirectamente de " + o + ": "
                + (dep == null || dep.estaVacia() ? "ninguna" : dep.toString()));
    }

    static void opDetectarCiclos() {
        if (!requiereGrafo()) {
            return;
        }
        System.out.println(AnalizadorIA.textoCiclos(grafo));
    }

    // ===================================================================== IA

    static void opFlujogramaETL() {
        if (!requiereTransformado()) {
            return;
        }
        System.out.println(flujogramas.generarETL(eventosETL, transform));
    }

    static void opFlujogramaEstructura() {
        System.out.println("1. Cola  2. Pila  3. Lista enlazada  4. BST");
        int e = leerEntero("Estructura: ");
        switch (e) {
            case 1: System.out.println(flujogramas.generarCola()); break;
            case 2: System.out.println(flujogramas.generarPila()); break;
            case 3: System.out.println(flujogramas.generarLista()); break;
            case 4:
                if (requiereBST()) {
                    // Se hace una búsqueda para que el diagrama refleje un recorrido real del árbol.
                    String cedula = leerLinea("Cédula a buscar para trazar el diagrama (Enter = raíz): ");
                    if (cedula.isEmpty()) {
                        cedula = bst.getRaiz().dato.getCedula();
                    }
                    bst.buscar(Estudiante.sonda(cedula));
                    System.out.println(flujogramas.generarBST(bst));
                }
                break;
            default: System.out.println("Opción no válida.");
        }
    }

    static void opFlujogramaRecorrido(boolean esBFS) {
        if (!requiereGrafo()) {
            return;
        }
        RegistroEventos ev = esBFS ? ultimoBFS : ultimoDFS;
        if (ev == null) {
            String inicio = grafo.getVertices().obtener(0).dato;
            System.out.println("Aún no se ejecutó " + (esBFS ? "BFS" : "DFS") + "; se ejecuta desde " + inicio + ".");
            ev = new RegistroEventos();
            if (esBFS) {
                BFS.recorrer(grafo, inicio, ev, false);
                ultimoBFS = ev;
            } else {
                DFS.recorrer(grafo, inicio, ev, false);
                ultimoDFS = ev;
            }
        }
        System.out.println(flujogramas.generarRecorrido(esBFS, ev));
    }

    static void opExplicarArbol() {
        if (!requiereBST()) {
            return;
        }
        System.out.println(analizador.explicarArbol(bst));
    }

    static void opExplicarGrafo() {
        if (!requiereGrafo()) {
            return;
        }
        System.out.println(analizador.explicarGrafo(grafo));
    }

    static void opAnalizarErrores() {
        if (!requiereTransformado()) {
            return;
        }
        System.out.println(analizador.analizarErrores(transform));
    }

    /** Genera los siete flujogramas y la documentación para el menú de consola. */
    static void opGenerarDocumentacion() {
        if (!requiereTransformado()) {
            return;
        }
        Map<Path, Long> antes = fotoSalida();
        generarDocumentacion("", "", false, true, true);
        mostrarGenerado(antes);
    }

    record Diagrama(String titulo, String codigo) {}

    record Documentacion(List<Diagrama> diagramas, String texto, String ciclos,
                         String origenBfs, String origenDfs, List<RegistroEventos> recorridos) {}

    static Documentacion generarDocumentacion(String inicioBfs, String inicioDfs, boolean buscarRaiz,
                                              boolean diagramasIA, boolean textoIA) {
        if (bst == null) {
            opConstruirBST();
        }
        if (grafo == null) {
            opConstruirGrafo();
        }
        GeneradorFlujograma generador = new GeneradorFlujograma(diagramasIA ? ia : null, SALIDA);
        generador.setIAEnEstructuras(false);
        List<Diagrama> diagramas = new ArrayList<>();
        diagramas.add(new Diagrama("ETL completo", generador.generarETL(eventosETL, transform)));
        diagramas.add(new Diagrama("Cola", generador.generarCola()));
        diagramas.add(new Diagrama("Pila", generador.generarPila()));
        diagramas.add(new Diagrama("Lista enlazada", generador.generarLista()));

        diagramas.add(new Diagrama("Búsqueda en el BST", documentarBusquedaBST(generador, buscarRaiz)));
        String arbol = bst.aMermaid();
        diagramas.add(new Diagrama("Árbol BST", arbol));
        RegistroEventos bfs = new RegistroEventos();
        RegistroEventos dfs = new RegistroEventos();
        if (!grafo.estaVacio()) {
            String primero = grafo.getVertices().obtener(0).dato;
            inicioBfs = inicioBfs.isEmpty() ? primero : inicioBfs;
            inicioDfs = inicioDfs.isEmpty() ? primero : inicioDfs;
            recorrerGrafo(true, inicioBfs, bfs, false);
            recorrerGrafo(false, inicioDfs, dfs, false);
        }
        diagramas.add(new Diagrama("Grafo + BFS", generador.generarRecorrido(true, bfs)));
        diagramas.add(new Diagrama("Grafo + DFS", generador.generarRecorrido(false, dfs)));
        try {
            guardarDiagramaEstado("arbol_bst_estado.md", "Estado del BST", arbol);
            guardarDiagramaEstado("grafo_prerrequisitos.md", "Grafo de prerrequisitos", grafo.aMermaid());
        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo guardar el árbol o el grafo: " + e.getMessage());
        }
        String resumen = resumenSistema();
        if (!grafo.estaVacio()) {
            resumen += "Origen del recorrido BFS: " + inicioBfs + "\nOrigen del recorrido DFS: " + inicioDfs + "\n";
        }
        String texto = analizador.documentacion(resumen, textoIA);
        return new Documentacion(diagramas, texto, AnalizadorIA.textoCiclos(grafo),
                inicioBfs, inicioDfs, List.of(bfs, dfs));
    }

    private static void guardarDiagramaEstado(String archivo, String titulo, String codigo) throws IOException {
        Load.escribirTexto(SALIDA + "/diagramas/" + archivo, "# " + titulo + "\n\n```mermaid\n" + codigo + "```\n");
    }

    static String documentarBusquedaBST(GeneradorFlujograma generador, boolean buscarRaiz) {
        if (!bst.estaVacio()) {
            String clave = buscarRaiz ? bst.getRaiz().dato.getCedula()
                    : bst.inOrden().obtener(bst.contarNodos() - 1).getCedula();
            bst.buscar(Estudiante.sonda(clave));
        }
        return generador.generarBST(bst);
    }

/** Lista los archivos de texto que hay en output/ con su fecha de modificación. */
static Map<Path, Long> fotoSalida() {
    Map<Path, Long> foto = new HashMap<>();
    Path raiz = Path.of(SALIDA);
    if (!Files.exists(raiz)) {
        return foto;
    }
    try (Stream<Path> s = Files.walk(raiz)) {
        s.filter(Files::isRegularFile).forEach(p -> foto.put(p, modificado(p)));
    } catch (IOException e) {
        // se ignora: solo afecta a lo que se muestra
    }
    return foto;
}

static long modificado(Path p) {
    try {
        return Files.getLastModifiedTime(p).toMillis();
    } catch (IOException e) {
        return 0L;
    }
}

/** Imprime el contenido de los archivos de texto nuevos o modificados desde la foto "antes". */
static void mostrarGenerado(Map<Path, Long> antes) {
    List<Path> nuevos = new ArrayList<>();
    for (Map.Entry<Path, Long> e : fotoSalida().entrySet()) {
        String nombre = e.getKey().getFileName().toString().toLowerCase();
        boolean esTexto = nombre.endsWith(".md") || nombre.endsWith(".mmd") || nombre.endsWith(".txt");
        Long previo = antes.get(e.getKey());
        if (esTexto && (previo == null || !previo.equals(e.getValue()))) {
            nuevos.add(e.getKey());
        }
    }
    nuevos.sort(Comparator.comparingLong(Main::modificado).thenComparing(Path::toString));
    if (nuevos.isEmpty()) {
        System.out.println("No se encontraron archivos nuevos para mostrar.");
        return;
    }
    System.out.println();
    System.out.println("=== DOCUMENTACIÓN GENERADA (" + nuevos.size() + " archivos) ===");
    for (Path p : nuevos) {
        System.out.println();
        System.out.println("=== " + p.getFileName() + " ===");
        try {
            System.out.println(Files.readString(p, StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo leer " + p + ": " + e.getMessage());
        }
    }
}

    static String resumenSistema() {
        StringBuilder sb = new StringBuilder();
        sb.append("Registros procesados: ").append(transform.totalProcesados())
          .append(" | válidos: ").append(transform.totalValidos())
          .append(" | inválidos: ").append(transform.totalInvalidos()).append("\n");
        sb.append("Estudiantes válidos: ").append(transform.estudiantes.tamanio())
          .append(" | Asignaturas: ").append(transform.asignaturas.tamanio())
          .append(" | Matrículas: ").append(transform.matriculas.tamanio())
          .append(" | Prerrequisitos: ").append(transform.prerrequisitos.tamanio()).append("\n");
        if (bst != null) {
            sb.append("BST: ").append(bst.contarNodos()).append(" nodos, altura ").append(bst.altura()).append("\n");
        }
        if (grafo != null) {
            sb.append("Grafo: ").append(grafo.numVertices()).append(" vértices, ").append(grafo.numAristas()).append(" aristas\n");
            sb.append(AnalizadorIA.textoCiclos(grafo)).append("\n");
        }
        sb.append(transform.resumenErrores());
        return sb.toString();
    }

    // =================================================================== REPORTES

    static String opReporteTecnico() {
        if (!requiereTransformado()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("REPORTE TÉCNICO - SmartETL-DS + IA (Grupo 3)\n");
        sb.append("==============================================\n\n");
        sb.append(load.reporteTexto(transform)).append("\n");
        if (bst != null) {
            sb.append("--- BST ---\n");
            sb.append("Nodos: ").append(bst.contarNodos()).append("  Hojas: ").append(bst.contarHojas())
              .append("  Altura: ").append(bst.altura()).append("  Altura mínima: ").append(bst.alturaMinimaTeorica()).append("\n");
            sb.append(bst.dibujar()).append("\n");
        }
        if (grafo != null) {
            sb.append("--- Grafo de prerrequisitos ---\n");
            sb.append("Vértices: ").append(grafo.numVertices()).append("  Aristas: ").append(grafo.numAristas()).append("\n");
            sb.append(grafo.describir()).append("\n");
            sb.append(AnalizadorIA.textoCiclos(grafo)).append("\n");
        }
        try {
            Load.escribirTexto(SALIDA + "/reporte_tecnico.txt", sb.toString());
            System.out.println("Reporte técnico escrito en " + SALIDA + "/reporte_tecnico.txt");
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
        return sb.toString();
    }

    static void opExportar() {
        if (!requiereTransformado()) {
            return;
        }
        opCargar();
        try {
            if (bst != null) {
                StringBuilder sb = new StringBuilder("InOrden (ordenado por cédula):\n");
                for (Estudiante e : bst.inOrden()) {
                    sb.append(e).append("\n");
                }
                Load.escribirTexto(SALIDA + "/estudiantes_inorden.txt", sb.toString());
                guardarDiagramaEstado("arbol_bst_estado.md", "Estado del BST", bst.aMermaid());
            }
            if (grafo != null) {
                guardarDiagramaEstado("grafo_prerrequisitos.md", "Grafo de prerrequisitos", grafo.aMermaid());
            }
            System.out.println("Resultados exportados a " + SALIDA + "/");
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    // ===================================================================== DEMO

    /** Recorre todo el flujo sin pedir datos. Sirve para verificar y para la defensa. */
    static void ejecutarDemo() {
        System.out.println("##### DEMO AUTOMÁTICA #####");
        opExtraer();
        opMostrarCola();
        opTransformar();
        opCargar();
        opConstruirBST();
        opAlturaNodos();
        opConstruirGrafo();
        System.out.println(grafo.describir());
        RegistroEventos ev = new RegistroEventos();
        System.out.println("BFS desde SIS101:");
        ultimoBFS = ev;
        System.out.println("Orden: " + BFS.recorrer(grafo, "SIS101", ev, true));
        ev = new RegistroEventos();
        System.out.println("DFS desde SIS101:");
        ultimoDFS = ev;
        System.out.println("Orden: " + DFS.recorrer(grafo, "SIS101", ev, true));
        ListaEnlazada<String> ruta = BFS.buscarRuta(grafo, "SIS101", "SIS303");
        System.out.println("Ruta SIS101 -> SIS303: " + ruta);
        System.out.println(AnalizadorIA.textoCiclos(grafo));
        opGenerarDocumentacion();
        opExplicarArbol();
        opExplicarGrafo();
        opAnalizarErrores();
        opReporteTecnico();
        opExportar();
        System.out.println("##### FIN DEMO #####");
    }
    // ============================================================
// MODO GUIADO - SMARTETL-DS
// ============================================================

static void opModoGuiado() {

    int opcion;

    do {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("       SMARTETL-DS - MODO GUIADO");
        System.out.println("==========================================");

        System.out.println("1. Ejecutar proceso ETL");
        System.out.println("2. Trabajar con estructuras de datos");
        System.out.println("3. Trabajar con el grafo");
        System.out.println("4. Generar reportes y documentación");
        System.out.println("0. Regresar al menú principal");

        opcion = leerEntero("¿Qué desea hacer?: ");

        switch (opcion) {

            case 1:
                guiadoETL();
                break;

            case 2:
                guiadoEstructuras();
                break;

            case 3:
                guiadoGrafo();
                break;

            case 4:
                guiadoReportes();
                break;

            case 0:
                System.out.println("Regresando al menú principal...");
                break;

            default:
                System.out.println("Opción no válida.");
        }

    } while (opcion != 0);
}
static void guiadoETL() {

    int opcion;

    do {

        System.out.println();
        System.out.println("========== PROCESO ETL ==========");
        System.out.println("1. Extraer datos");
        System.out.println("2. Mostrar datos extraídos");
        System.out.println("3. Transformar datos");
        System.out.println("4. Mostrar datos válidos");
        System.out.println("5. Mostrar errores");
        System.out.println("6. Cargar datos");
        System.out.println("0. Regresar");

        opcion = leerEntero("Seleccione una opción: ");

        switch (opcion) {

            case 1:
                opExtraer();
                break;

            case 2:
                opMostrarCola();
                break;

            case 3:
                opTransformar();
                break;

            case 4:
                opMostrarValidos();
                break;

            case 5:
                opMostrarErrores();
                break;

            case 6:
                opCargar();
                break;

            case 0:
                break;

            default:
                System.out.println("Opción incorrecta.");
        }

    } while (opcion != 0);
}
static void guiadoEstructuras() {

    int opcion;

    do {

        System.out.println();
        System.out.println("======= ESTRUCTURAS DE DATOS =======");
        System.out.println("1. Lista enlazada");
        System.out.println("2. Buscar estudiante");
        System.out.println("3. Ordenar estudiantes");
        System.out.println("4. Construir árbol BST");
        System.out.println("5. Insertar en BST");
        System.out.println("6. Buscar en BST");
        System.out.println("7. Eliminar en BST");
        System.out.println("8. Recorrido InOrden");
        System.out.println("9. Recorrido PreOrden");
        System.out.println("10. Recorrido PostOrden");
        System.out.println("11. Altura y nodos del BST");
        System.out.println("0. Regresar");

        opcion = leerEntero("Seleccione una opción: ");

        switch (opcion) {

            case 1:
                opMostrarLista();
                break;

            case 2:
                opBuscarRegistro();
                break;

            case 3:
                opOrdenar();
                break;

            case 4:
                opConstruirBST();
                break;

            case 5:
                opInsertarBST();
                break;

            case 6:
                opBuscarBST();
                break;

            case 7:
                opEliminarBST();
                break;

            case 8:
                opRecorrido(1);
                break;

            case 9:
                opRecorrido(2);
                break;

            case 10:
                opRecorrido(3);
                break;

            case 11:
                opAlturaNodos();
                break;

            case 0:
                break;

            default:
                System.out.println("Opción incorrecta.");
        }

    } while (opcion != 0);
}
static void guiadoGrafo() {

    int opcion;

    do {

        System.out.println();
        System.out.println("========== GRAFO ==========");
        System.out.println("1. Construir grafo");
        System.out.println("2. Mostrar conexiones");
        System.out.println("3. Visualizar grafo");
        System.out.println("4. Recorrido BFS");
        System.out.println("5. Recorrido DFS");
        System.out.println("6. Buscar ruta");
        System.out.println("7. Detectar ciclos");
        System.out.println("8. Explicación del grafo con IA");
        System.out.println("0. Regresar");

        opcion = leerEntero("Seleccione una opción: ");

        switch (opcion) {

            case 1:
                opConstruirGrafo();
                break;

            case 2:
                opMostrarConexiones();
                break;

            case 3:
                opVisualizarGrafo();
                break;

            case 4:
                opRecorridoGrafo(true);
                break;

            case 5:
                opRecorridoGrafo(false);
                break;

            case 6:
                opBuscarRuta();
                break;

            case 7:
                opDetectarCiclos();
                break;

            case 8:
                opExplicarGrafo();
                break;

            case 0:
                break;

            default:
                System.out.println("Opción incorrecta.");
        }

    } while (opcion != 0);
}
static void opVisualizarGrafo() {

    if (!requiereGrafo()) {
        return;
    }

    if (grafo.estaVacio()) {
        System.out.println("El grafo no contiene vértices.");
        return;
    }

    try {

        Path carpeta = Path.of(SALIDA, "diagramas");

        Files.createDirectories(carpeta);

        Path archivo = carpeta.resolve("grafo_visual.html");

        String mermaid = grafo.aMermaid();

        String html = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>Grafo SmartETL-DS</title>

                    <script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>

                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background: #f3f4f6;
                            margin: 0;
                            padding: 30px;
                        }

                        h1 {
                            text-align: center;
                            color: #1e293b;
                        }

                        .contenedor {
                            background: white;
                            padding: 25px;
                            border-radius: 15px;
                            box-shadow: 0 4px 12px rgba(0,0,0,.1);
                            overflow: auto;
                        }

                        .mermaid {
                            text-align: center;
                        }
                    </style>
                </head>

                <body>

                    <h1>Grafo de Prerrequisitos - SmartETL-DS</h1>

                    <div class="contenedor">
                        <pre class="mermaid">
                """ + mermaid + """
                        </pre>
                    </div>

                    <script>
                        mermaid.initialize({
                            startOnLoad: true,
                            theme: 'default',
                            flowchart: {
                                curve: 'basis'
                            }
                        });
                    </script>

                </body>
                </html>
                """;

        Files.writeString(
                archivo,
                html,
                StandardCharsets.UTF_8
        );

        System.out.println();
        System.out.println("=====================================");
        System.out.println("GRAFO GENERADO CORRECTAMENTE");
        System.out.println("=====================================");
        System.out.println("Archivo: " + archivo.toAbsolutePath());

        if (Desktop.isDesktopSupported()) {

            Desktop.getDesktop().browse(
                    archivo.toAbsolutePath().toUri()
            );

            System.out.println("Abriendo grafo en el navegador...");

        } else {

            System.out.println(
                    "No se pudo abrir automáticamente."
            );

            System.out.println(
                    "Abra manualmente el archivo HTML indicado."
            );
        }

    } catch (Exception e) {

        System.out.println(
                "[ERROR] No se pudo visualizar el grafo: "
                + e.getMessage()
        );
    }
}
static void guiadoReportes() {

    int opcion;

    do {

        System.out.println();
        System.out.println("======= REPORTES =======");
        System.out.println("1. Generar documentación");
        System.out.println("2. Generar reporte técnico");
        System.out.println("3. Exportar resultados");
        System.out.println("4. Generar flujograma ETL");
        System.out.println("5. Explicar BST con IA");
        System.out.println("6. Explicar grafo con IA");
        System.out.println("7. Analizar errores ETL");
        System.out.println("0. Regresar");

        opcion = leerEntero("Seleccione una opción: ");

        switch (opcion) {

            case 1:
                opGenerarDocumentacion();
                break;

            case 2:
                opReporteTecnico();
                break;

            case 3:
                opExportar();
                break;

            case 4:
                opFlujogramaETL();
                break;

            case 5:
                opExplicarArbol();
                break;

            case 6:
                opExplicarGrafo();
                break;

            case 7:
                opAnalizarErrores();
                break;

            case 0:
                break;

            default:
                System.out.println("Opción incorrecta.");
        }

    } while (opcion != 0);
}

    // ================================================================== ENTRADA

    static String leerLinea(String mensaje) {
        System.out.print(mensaje);
        if (!in.hasNextLine()) {
            return "";
        }
        return in.nextLine().trim();
    }

    static int leerEntero(String mensaje) {
        String texto = leerLinea(mensaje);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static double leerDecimal(String mensaje) {
        String texto = leerLinea(mensaje).replace(',', '.');
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

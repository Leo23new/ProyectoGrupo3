package algoritmos;

import estructuras.ArbolBST;
import estructuras.ListaEnlazada;
import estructuras.ListaSecuencial;
import model.Estudiante;

import java.util.Comparator;
import java.util.Random;

/**
 * Pruebas de rendimiento con datos SINTÉTICOS generados al vuelo (semilla fija, reproducible).
 * Compara ordenamientos y búsqueda secuencial vs BST con distintas cantidades de registros.
 */
public class Comparativa {

    public static ListaEnlazada<Estudiante> generarEstudiantes(int n, long semilla) {
        Random azar = new Random(semilla);
        ListaEnlazada<Estudiante> lista = new ListaEnlazada<>();
        for (int i = 0; i < n; i++) {
            // Cédulas sintéticas aleatorias de 10 dígitos (una colisión es posible pero rara)
            long numero = 1800000000L + (long) (azar.nextDouble() * 99999999L);
            String cedula = String.valueOf(numero);
            double prom = Math.round(azar.nextDouble() * 100) / 10.0;
            lista.insertar(new Estudiante(cedula, "Nombre" + i, "Apellido" + azar.nextInt(500),
                    18 + azar.nextInt(10), "Ingenieria de Software", 1 + azar.nextInt(9), prom));
        }
        return lista;
    }

    public static void compararOrdenamientos() {
        int[] tamanios = {100, 500, 1000, 2000, 4000};
        Comparator<Estudiante> porPromedio = (a, b) -> Double.compare(a.getPromedio(), b.getPromedio());
        System.out.println();
        System.out.println("Comparativa de ordenamiento (criterio: promedio, datos sintéticos)");
        System.out.println(String.format("%-8s | %-26s | %-26s | %-26s", "n", "Burbuja", "Inserción", "Selección"));
        System.out.println(String.format("%-8s | %-26s | %-26s | %-26s", "", "comp / ms", "comp / ms", "comp / ms"));
        System.out.println("-".repeat(95));
        for (int n : tamanios) {
            ListaSecuencial<Estudiante> base = ListaSecuencial.desde(generarEstudiantes(n, 42));
            String b = medir(base.copiar(), porPromedio, 1);
            String i = medir(base.copiar(), porPromedio, 2);
            String s = medir(base.copiar(), porPromedio, 3);
            System.out.println(String.format("%-8d | %-26s | %-26s | %-26s", n, b, i, s));
        }
        System.out.println("Observación: al duplicar n el tiempo se multiplica por ~4 (comportamiento O(n^2)).");
    }

    private static String medir(ListaSecuencial<Estudiante> lista, Comparator<Estudiante> c, int algoritmo) {
        long ini = System.nanoTime();
        if (algoritmo == 1) {
            Ordenamiento.burbuja(lista, c);
        } else if (algoritmo == 2) {
            Ordenamiento.insercion(lista, c);
        } else {
            Ordenamiento.seleccion(lista, c);
        }
        double ms = (System.nanoTime() - ini) / 1_000_000.0;
        return Ordenamiento.comparaciones + " / " + String.format("%.2f", ms);
    }

    public static void compararBusquedas() {
        int[] tamanios = {100, 1000, 10000, 50000};
        System.out.println();
        System.out.println("Comparativa de búsqueda: secuencial en lista vs BST (peor caso: clave inexistente y clave del final)");
        System.out.println(String.format("%-8s | %-24s | %-24s | %-8s", "n", "Secuencial (comp)", "BST (comp)", "Altura"));
        System.out.println("-".repeat(75));
        for (int n : tamanios) {
            ListaEnlazada<Estudiante> lista = generarEstudiantes(n, 7);
            ArbolBST<Estudiante> arbol = new ArbolBST<>(Estudiante::getCedula);
            for (Estudiante e : lista) {
                arbol.insertar(e);
            }
            Estudiante ultimo = lista.obtener(lista.tamanio() - 1);
            Busqueda.secuencial(lista, ultimo.getCedula());
            long cs = Busqueda.ultimasComparaciones;
            arbol.buscar(Estudiante.sonda(ultimo.getCedula()));
            int cb = arbol.getUltimasComparaciones();
            System.out.println(String.format("%-8d | %-24d | %-24d | %-8d", n, cs, cb, arbol.altura()));
        }
        System.out.println("Observación: la secuencial crece linealmente (O(n)); el BST crece con la altura (~log2 n si está balanceado).");
    }
}

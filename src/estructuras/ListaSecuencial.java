package estructuras;

/**
 * Lista secuencial (basada en arreglo contiguo) genérica con crecimiento dinámico.
 * Se usa para el ordenamiento (acceso por índice en O(1)) y la búsqueda binaria.
 */
public class ListaSecuencial<T> {
    private Object[] datos;
    private int tamanio;

    public ListaSecuencial() {
        datos = new Object[10];
        tamanio = 0;
    }

    /** Copia los elementos de una lista enlazada a una secuencial. */
    public static <T> ListaSecuencial<T> desde(ListaEnlazada<T> origen) {
        ListaSecuencial<T> copia = new ListaSecuencial<>();
        for (T dato : origen) {
            copia.agregar(dato);
        }
        return copia;
    }

    /** Agrega al final. O(1) amortizado. */
    public void agregar(T dato) {
        if (tamanio == datos.length) {
            Object[] nuevo = new Object[datos.length * 2];
            for (int i = 0; i < tamanio; i++) {
                nuevo[i] = datos[i];
            }
            datos = nuevo;
        }
        datos[tamanio] = dato;
        tamanio++;
    }

    @SuppressWarnings("unchecked")
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        return (T) datos[indice];
    }

    public void establecer(int indice, T dato) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        datos[indice] = dato;
    }

    /** Elimina el elemento de la posición indicada desplazando los siguientes. O(n). */
    @SuppressWarnings("unchecked")
    public T eliminarEn(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        T eliminado = (T) datos[indice];
        for (int i = indice; i < tamanio - 1; i++) {
            datos[i] = datos[i + 1];
        }
        tamanio--;
        datos[tamanio] = null;
        return eliminado;
    }

    public void intercambiar(int i, int j) {
        Object temporal = datos[i];
        datos[i] = datos[j];
        datos[j] = temporal;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    /** Copia independiente (para ordenar sin alterar la lista original). */
    public ListaSecuencial<T> copiar() {
        ListaSecuencial<T> copia = new ListaSecuencial<>();
        for (int i = 0; i < tamanio; i++) {
            copia.agregar(obtener(i));
        }
        return copia;
    }

    public void mostrar(int maximo) {
        int limite = Math.min(maximo, tamanio);
        for (int i = 0; i < limite; i++) {
            System.out.println((i + 1) + ". " + datos[i]);
        }
        if (tamanio > limite) {
            System.out.println("... (" + (tamanio - limite) + " más)");
        }
    }
}

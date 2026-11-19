package estructuras;

/**
 * Lista doblemente enlazada. En el proyecto se usa como HISTORIAL de acciones del menú
 * (se puede recorrer hacia adelante y hacia atrás).
 */
public class ListaDoble<T> {
    private static class NodoDoble<T> {
        T dato;
        NodoDoble<T> anterior;
        NodoDoble<T> siguiente;

        NodoDoble(T dato) {
            this.dato = dato;
        }
    }

    private NodoDoble<T> cabeza;
    private NodoDoble<T> cola;
    private int tamanio;

    public void insertarFinal(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (cola == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            nuevo.anterior = cola;
            cola.siguiente = nuevo;
            cola = nuevo;
        }
        tamanio++;
    }

    public void insertarInicio(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            nuevo.siguiente = cabeza;
            cabeza.anterior = nuevo;
            cabeza = nuevo;
        }
        tamanio++;
    }

    public boolean eliminar(T dato) {
        NodoDoble<T> actual = cabeza;
        while (actual != null) {
            if (actual.dato.equals(dato)) {
                if (actual.anterior == null) {
                    cabeza = actual.siguiente;
                } else {
                    actual.anterior.siguiente = actual.siguiente;
                }
                if (actual.siguiente == null) {
                    cola = actual.anterior;
                } else {
                    actual.siguiente.anterior = actual.anterior;
                }
                tamanio--;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    public void mostrarAdelante() {
        NodoDoble<T> actual = cabeza;
        int i = 1;
        while (actual != null) {
            System.out.println(i + ". " + actual.dato);
            actual = actual.siguiente;
            i++;
        }
        if (tamanio == 0) {
            System.out.println("(historial vacío)");
        }
    }

    /** Muestra del más reciente al más antiguo, hasta "maximo" elementos. */
    public void mostrarAtras(int maximo) {
        NodoDoble<T> actual = cola;
        int i = 0;
        while (actual != null && i < maximo) {
            System.out.println("  " + actual.dato);
            actual = actual.anterior;
            i++;
        }
        if (tamanio == 0) {
            System.out.println("(historial vacío)");
        }
    }
}

package ia;

/**
 * Construye los prompts que se envían a la IA. La IA nunca recibe las estructuras de datos
 * "para implementarlas": solo recibe el ESTADO REAL del programa (eventos, nodos, aristas,
 * estadísticas agregadas) para dibujar o explicar.
 */
public class GeneradorPrompt {

    public static final String SISTEMA_MERMAID =
            "Eres un asistente que convierte descripciones de algoritmos en diagramas Mermaid. "
            + "Responde UNICAMENTE con un bloque de codigo mermaid que comience con 'flowchart TD'. "
            + "Reglas: los ids de nodo son alfanumericos sin espacios; todo el texto de cada nodo va entre comillas dobles; "
            + "usa [\"...\"] para procesos, {\"...\"} para decisiones y ([\"...\"]) para inicio y fin; "
            + "las etiquetas de flechas se escriben -->|\"texto\"|; no uses parentesis ni corchetes dentro del texto; "
            + "no agregues explicaciones fuera del bloque.";

    public static final String SISTEMA_EXPLICACION =
            "Eres un profesor de Estructuras de Datos de ingenieria de software. Explica en espanol, con tono tecnico "
            + "pero claro, refiriendote a los nodos, claves y valores CONCRETOS que recibes, no a definiciones genericas. "
            + "Maximo 250 palabras. No inventes datos que no esten en la entrada.";

    public static String flujograma(String titulo, String descripcion) {
        return "Genera un flujograma Mermaid (flowchart TD) del proceso: " + titulo + ".\n\n"
                + "Descripcion estructurada generada por el programa (datos reales de la ejecucion):\n"
                + descripcion + "\n\n"
                + "El diagrama debe reflejar fielmente estos pasos, con inicio, fin, decisiones y bucles cuando correspondan.";
    }

    public static String explicarArbol(String estructura, int altura, int nodos, int alturaMinima) {
        return "Explica el siguiente arbol binario de busqueda (BST) real. Cada linea indica un nodo con sus hijos.\n\n"
                + estructura + "\n"
                + "Nodos: " + nodos + ". Altura: " + altura + ". Altura minima posible con ese numero de nodos: " + alturaMinima + ".\n\n"
                + "Indica cual es la raiz, como se cumple la propiedad BST en al menos dos nodos concretos y si el arbol "
                + "esta balanceado o degenerado y que consecuencia tiene para la busqueda.";
    }

    public static String explicarGrafo(String adyacencia, int vertices, int aristas, String ciclos) {
        return "Explica el siguiente grafo dirigido real de prerrequisitos entre asignaturas. "
                + "Cada linea es: asignatura -> asignaturas que dependen de ella.\n\n"
                + adyacencia + "\n"
                + "Vertices: " + vertices + ". Aristas: " + aristas + ".\nCiclos detectados: " + ciclos + "\n\n"
                + "Indica que asignaturas no tienen prerrequisitos, cuales son las finales, la cadena de prerrequisitos "
                + "mas larga que puedas identificar y que implica un ciclo para la malla.";
    }

    public static String analizarErrores(String resumen) {
        return "El programa ETL calculo estas estadisticas agregadas de errores (no se envian registros individuales):\n\n"
                + resumen + "\n"
                + "Redacta una interpretacion tecnica de los patrones observados: cual es la categoria dominante, "
                + "que fuente de datos es la mas problematica y que reglas de validacion o acciones de limpieza recomiendas.";
    }

    public static String documentacion(String resumenSistema) {
        return "Redacta en espanol la documentacion tecnica breve (formato Markdown, maximo 400 palabras) del siguiente "
                + "sistema, con las secciones: Proposito, Flujo de datos, Estructuras utilizadas y por que, Resultados de la ejecucion.\n\n"
                + resumenSistema;
    }
}

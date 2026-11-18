package sifac.comun.estructuras;

/**
 * Lista enlazada simple propia que almacena la cadena cronológica de estados
 * por los que pasa un Documento (Semanas 3 y 4).
 * Crece por el final para conservar el orden histórico sin borrar estados previos.
 */
public class HistorialEstados {

    private NodoEstado cabeza;
    private int cantidad;

    public HistorialEstados() {
        this.cabeza = null;
        this.cantidad = 0;
    }

    /**
     * Registra un nuevo estado al final de la cadena de nodos.
     *
     * @param nuevoEstado nombre del estado (ej. ABIERTO, CERRADO, PENDIENTE_ENVIO, ENVIADO)
     */
    public void registrarEstado(String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede ser vacio");
        }
        NodoEstado nuevo = new NodoEstado(nuevoEstado.trim());
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoEstado actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
        cantidad++;
    }

    /**
     * Devuelve el último estado registrado en la cadena, o "SIN ESTADO" si está vacía.
     */
    public String getEstadoActual() {
        if (cabeza == null) {
            return "SIN ESTADO";
        }
        NodoEstado actual = cabeza;
        while (actual.getSiguiente() != null) {
            actual = actual.getSiguiente();
        }
        return actual.getEstado();
    }

    /**
     * Recorre la cadena con una referencia auxiliar sin mover la cabeza
     * y devuelve la representación textual de todos los estados.
     */
    public String verHistorial() {
        if (cabeza == null) {
            return "SIN ESTADO";
        }
        StringBuilder sb = new StringBuilder();
        NodoEstado actual = cabeza;
        while (actual != null) {
            sb.append(actual.getEstado());
            if (actual.getSiguiente() != null) {
                sb.append(" -> ");
            }
            actual = actual.getSiguiente();
        }
        return sb.toString();
    }

    public int getCantidad() {
        return cantidad;
    }

    public NodoEstado getCabeza() {
        return cabeza;
    }
}
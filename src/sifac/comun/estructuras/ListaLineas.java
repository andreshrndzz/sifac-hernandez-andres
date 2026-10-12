package sifac.comun.estructuras;

import sifac.comun.entidades.LineaDetalle;

/**
 * Lista enlazada simple de lineas de detalle.
 * Sustituye a ArrayList en Documento (Semana 4 y Tarea 1).
 * Incluye operaciones basicas y las 4 operaciones avanzadas de la Tarea 1.
 */
public class ListaLineas {

    private NodoLinea cabeza;
    private int cantidad;

    public ListaLineas() {
        this.cabeza = null;
        this.cantidad = 0;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int size() {
        return cantidad;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public NodoLinea getCabeza() {
        return cabeza;
    }

    // ===============================================
    // OPERACIONES BASICAS (Semana 4)
    // ===============================================

    public void agregar(LineaDetalle linea) {
        agregarAlFinal(linea);
    }

    public void agregarAlFinal(LineaDetalle dato) {
        NodoLinea nuevo = new NodoLinea(dato);

        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoLinea actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
        cantidad++;
    }

    public void insertarAlInicio(LineaDetalle dato) {
        NodoLinea nuevo = new NodoLinea(dato);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
        cantidad++;
    }

    public void insertarEn(int posicion, LineaDetalle dato) {
        if (posicion < 0 || posicion > cantidad) {
            throw new IndexOutOfBoundsException("Posicion invalida: " + posicion);
        }
        if (posicion == 0) {
            insertarAlInicio(dato);
            return;
        }

        NodoLinea anterior = cabeza;
        for (int i = 0; i < posicion - 1; i++) {
            anterior = anterior.getSiguiente();
        }

        NodoLinea nuevo = new NodoLinea(dato);
        nuevo.setSiguiente(anterior.getSiguiente());
        anterior.setSiguiente(nuevo);
        cantidad++;
    }

    public void eliminarEn(int posicion) {
        if (posicion < 0 || posicion >= cantidad) {
            throw new IndexOutOfBoundsException("Posicion invalida: " + posicion);
        }

        if (posicion == 0) {
            cabeza = cabeza.getSiguiente();
        } else {
            NodoLinea anterior = cabeza;
            for (int i = 0; i < posicion - 1; i++) {
                anterior = anterior.getSiguiente();
            }
            NodoLinea objetivo = anterior.getSiguiente();
            anterior.setSiguiente(objetivo.getSiguiente());
        }
        cantidad--;
    }

    public boolean eliminarPorCodigo(String codigo) {
        if (cabeza == null) {
            return false;
        }
        if (cabeza.getDato().getProducto().getCodigo().equals(codigo)) {
            cabeza = cabeza.getSiguiente();
            cantidad--;
            return true;
        }

        NodoLinea anterior = cabeza;
        NodoLinea actual = cabeza.getSiguiente();

        while (actual != null) {
            if (actual.getDato().getProducto().getCodigo().equals(codigo)) {
                anterior.setSiguiente(actual.getSiguiente());
                cantidad--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    // ===============================================
    // OPERACIONES DE UTILIDAD
    // ===============================================

    /**
     * Calcula el subtotal sumando el importe de cada linea.
     */
    public double getSubtotal() {
        double subtotal = 0;
        NodoLinea actual = cabeza;
        while (actual != null) {
            subtotal += actual.getDato().getImporte();
            actual = actual.getSiguiente();
        }
        return subtotal;
    }

    /**
     * Devuelve una representacion textual de la lista.
     * Formato: [COD x cant] --> [COD x cant] --> null
     */
    public String verContenido() {
        StringBuilder sb = new StringBuilder();
        NodoLinea actual = cabeza;
        while (actual != null) {
            sb.append("[");
            sb.append(actual.getDato().getProducto().getCodigo());
            sb.append(" x ");
            sb.append(actual.getDato().getCantidad());
            sb.append("] --> ");
            actual = actual.getSiguiente();
        }
        sb.append("null");
        return sb.toString();
    }

    // ===============================================
    // TAREA 1: CUATRO OPERACIONES AVANZADAS
    // ===============================================

    /**
     * 3.1 Buscar una linea por codigo de producto.
     * Devuelve la linea cuyo producto tiene ese codigo, o null si no esta.
     * Si el codigo aparece mas de una vez, devuelve la primera aparicion.
     */
    public LineaDetalle buscar(String codigo) {
        NodoLinea actual = cabeza;
        while (actual != null) {
            if (actual.getDato().getProducto().getCodigo().equals(codigo)) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * 3.2 Obtener la linea de mayor importe.
     * Devuelve la linea con el importe mas alto, o null si la lista esta vacia.
     * Recorre una sola vez: no ordena ni recorre varias veces.
     */
    public LineaDetalle getLineaMayor() {
        if (cabeza == null) {
            return null;
        }

        NodoLinea actual = cabeza;
        LineaDetalle mayor = cabeza.getDato();

        while (actual != null) {
            if (actual.getDato().getImporte() > mayor.getImporte()) {
                mayor = actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return mayor;
    }

    /**
     * 3.3 Consolidar lineas repetidas.
     * Si un mismo producto aparece en varias lineas, las fusiona en una sola
     * sumando las cantidades. La linea consolidada conserva la posicion de la
     * primera aparicion. Devuelve cuantas lineas se eliminaron.
     *
     * INVARIANTE: el subtotal no cambia despues de consolidar.
     */
    public int consolidar() {
        int eliminadas = 0;
        NodoLinea externo = cabeza;

        while (externo != null) {
            String codigoExterno = externo.getDato().getProducto().getCodigo();

            NodoLinea anterior = externo;
            NodoLinea interno = externo.getSiguiente();

            while (interno != null) {
                if (interno.getDato().getProducto().getCodigo().equals(codigoExterno)) {
                    // Sumar la cantidad del duplicado a la primera aparicion
                    int cantidadActual = externo.getDato().getCantidad();
                    int cantidadDuplicada = interno.getDato().getCantidad();
                    externo.getDato().setCantidad(cantidadActual + cantidadDuplicada);

                    // Eliminar el nodo duplicado: anterior salta al siguiente de interno
                    anterior.setSiguiente(interno.getSiguiente());
                    cantidad--;
                    eliminadas++;

                    // interno avanza al siguiente (anterior NO se mueve)
                    interno = anterior.getSiguiente();
                } else {
                    // Avanzar ambos
                    anterior = interno;
                    interno = interno.getSiguiente();
                }
            }
            externo = externo.getSiguiente();
        }
        return eliminadas;
    }

    /**
     * 3.4 Invertir la lista en el mismo lugar.
     * Reapunta las flechas de los nodos existentes.
     * No crea una lista nueva ni copia los datos.
     * Se resuelve con tres referencias avanzando en conjunto.
     *
     * INVARIANTE: el subtotal y la cantidad no cambian despues de invertir.
     */
    public void invertir() {
        NodoLinea anterior = null;
        NodoLinea actual = cabeza;
        NodoLinea siguiente;

        while (actual != null) {
            siguiente = actual.getSiguiente();  // guardar antes de perderlo
            actual.setSiguiente(anterior);      // reapuntar hacia atras
            anterior = actual;                  // avanzar anterior
            actual = siguiente;                 // avanzar actual
        }
        cabeza = anterior;  // la nueva cabeza es el ultimo nodo procesado
    }
}

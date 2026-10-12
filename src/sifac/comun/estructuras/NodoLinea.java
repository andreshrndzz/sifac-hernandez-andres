package sifac.comun.estructuras;

import sifac.comun.entidades.LineaDetalle;

/**
 * Nodo de la lista enlazada simple para lineas de detalle.
 * Guarda una LineaDetalle y la referencia al siguiente nodo.
 */
public class NodoLinea {

    private LineaDetalle dato;
    private NodoLinea siguiente;

    public NodoLinea(LineaDetalle dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public LineaDetalle getDato() {
        return dato;
    }

    public void setDato(LineaDetalle dato) {
        this.dato = dato;
    }

    public NodoLinea getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoLinea siguiente) {
        this.siguiente = siguiente;
    }
}

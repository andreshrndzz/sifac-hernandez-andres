package sifac.comun.estructuras;

import sifac.comun.entidades.LineaDetalle;

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

    public NodoLinea getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoLinea siguiente) {
        this.siguiente = siguiente;
    }
}

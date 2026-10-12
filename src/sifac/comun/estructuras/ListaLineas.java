package sifac.comun.estructuras;

import sifac.comun.entidades.LineaDetalle;

public class ListaLineas {

    private NodoLinea cabeza;
    private int cantidad;

    public ListaLineas() {
        this.cabeza = null;
        this.cantidad = 0;
    }

    public void agregar(LineaDetalle linea) {
        NodoLinea nuevo = new NodoLinea(linea);
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

    public int getCantidad() {
        return cantidad;
    }

    public NodoLinea getCabeza() {
        return cabeza;
    }
}

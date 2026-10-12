package sifac.comun.estructuras;

import sifac.comun.entidades.Documento;

public class NodoDoc {

    private Documento dato;
    private NodoDoc siguiente;

    public NodoDoc(Documento dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Documento getDato() {
        return dato;
    }

    public NodoDoc getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoDoc siguiente) {
        this.siguiente = siguiente;
    }
}

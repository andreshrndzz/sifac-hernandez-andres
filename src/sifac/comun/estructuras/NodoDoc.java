package sifac.comun.estructuras;

import sifac.comun.entidades.Documento;

/**
 * Nodo de la cola de envio para documentos.
 * Guarda un Documento y la referencia al siguiente nodo.
 */
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

    public void setDato(Documento dato) {
        this.dato = dato;
    }

    public NodoDoc getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoDoc siguiente) {
        this.siguiente = siguiente;
    }
}

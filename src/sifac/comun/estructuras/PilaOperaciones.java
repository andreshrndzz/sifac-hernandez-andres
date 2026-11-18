package sifac.comun.estructuras;

import sifac.comun.entidades.LineaDetalle;

/**
 * Pila propia (LIFO - Last In, First Out) construida con nodos enlazados (Semana 5).
 * Permite apilar cada línea agregada a un documento y desapilarla para deshacer.
 */
public class PilaOperaciones {

    private NodoLinea cima;
    private int cantidad;

    public PilaOperaciones() {
        this.cima = null;
        this.cantidad = 0;
    }

    /**
     * Empuja una línea de detalle en la cima de la pila en O(1).
     */
    public void apilar(LineaDetalle linea) {
        NodoLinea nuevo = new NodoLinea(linea);
        nuevo.setSiguiente(cima);
        cima = nuevo;
        cantidad++;
    }

    /**
     * Retira y devuelve la línea en la cima de la pila, o null si está vacía.
     * Evita lanzar excepción en caso de borde cuando el cajero presiona deshacer sin líneas.
     */
    public LineaDetalle desapilar() {
        if (cima == null) {
            return null;
        }
        LineaDetalle dato = cima.getDato();
        cima = cima.getSiguiente();
        cantidad--;
        return dato;
    }

    /**
     * Consulta la línea en la cima sin retirarla, o null si está vacía.
     */
    public LineaDetalle verCima() {
        if (cima == null) {
            return null;
        }
        return cima.getDato();
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int tamaño() {
        return cantidad;
    }

    public int tamano() {
        return cantidad;
    }

    public void limpiar() {
        cima = null;
        cantidad = 0;
    }
}
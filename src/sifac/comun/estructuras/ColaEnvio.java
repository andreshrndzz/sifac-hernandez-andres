package sifac.comun.estructuras;

import sifac.comun.entidades.Documento;

/*
 * RESPUESTA OBLIGATORIA DEL EXPERIMENTO (Seccion 2.3)
 * 2. Que devolvio al desencolar? FC-100. Que devuelve estaVacia()? true.
 * 4. verFrente(): FC-200.
 * 5. tamaño(): 1.
 * 
 * Explicacion de que paso con FC-200: Al desencolar el primer documento, 
 * la cola quedo vacia (frente = null), pero la referencia 'fin' quedo 
 * apuntando al nodo retirado en un estado inconsistente. Como nuestro metodo 
 * encolar pregunta si 'frente == null', la estructura logro corregirse sola y 
 * enlazo el FC-200 correctamente como nuevo frente. Sin embargo, si hubiese 
 * preguntado por el 'fin', el FC-200 se habria enganchado al nodo "fantasma", 
 * desconectandose de la cadena principal y perdiendose en la memoria sin 
 * generar errores visibles.
 */
public class ColaEnvio {

    private NodoDoc frente;
    private NodoDoc fin;
    private int cantidad;

    public ColaEnvio() {
        this.frente = null;
        this.fin = null;
        this.cantidad = 0;
    }

    /**
     * Productor (Semanas 5 y 10): Agrega un documento al final de la cola en O(1)
     * sin ciclos y despierta al hilo consumidor con notifyAll().
     */
    public synchronized void encolar(Documento d) {
        NodoDoc nuevo = new NodoDoc(d);
        if (frente == null) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            fin = nuevo;
        }
        cantidad++;
        notifyAll();
    }

    /**
     * Retira y devuelve el documento del frente de la cola, o null si está vacía.
     * Limpia la referencia fin cuando sale el último elemento (Sección 2.4).
     */
    public synchronized Documento desencolar() {
        if (frente == null) {
            return null;
        }
        Documento d = frente.getDato();
        frente = frente.getSiguiente();

        if (frente == null) {
            fin = null;
        }

        cantidad--;
        return d;
    }

    /**
     * Consumidor bloqueante (Semana 10): Espera con wait() mientras la cola esté
     * vacía y desencola apenas el productor encole un documento.
     */
    public synchronized Documento esperarYDesencolar() throws InterruptedException {
        while (frente == null) {
            wait();
        }
        return desencolar();
    }

    public synchronized Documento verFrente() {
        if (frente == null) {
            return null;
        }
        return frente.getDato();
    }

    public synchronized boolean estaVacia() {
        return frente == null;
    }

    public synchronized int tamaño() {
        return cantidad;
    }

    public synchronized int tamano() {
        return cantidad;
    }

    public synchronized int getCantidad() {
        return cantidad;
    }
}
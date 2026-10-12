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
     * Agrega un documento al final de la cola usando la referencia fin.
     * Restriccion: Cero ciclos (no recorre). Complejidad O(1).
     *
     * @param d documento a encolar
     */
    public void encolar(Documento d) {
        NodoDoc nuevo = new NodoDoc(d);
        if (frente == null) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            fin = nuevo;
        }
        cantidad++;
    }

    /**
     * Retira y devuelve el documento del frente de la cola.
     * Devuelve null si la cola esta vacia (convencion del curso).
     * Limpia la referencia fin si la cola queda vacia (Seccion 2.4).
     *
     * @return el documento desencolado o null si esta vacia
     */
    public Documento desencolar() {
        if (frente == null) {
            return null;
        }
        Documento d = frente.getDato();
        frente = frente.getSiguiente();

        // CORRECCION APLICADA (2.4): Si salio el ultimo elemento, 
        // limpiamos la referencia 'fin' para mantener consistencia.
        if (frente == null) {
            fin = null;
        }

        cantidad--;
        return d;
    }

    /**
     * Devuelve el documento del frente sin retirarlo, o null si esta vacia.
     *
     * @return el documento del frente o null si esta vacia
     */
    public Documento verFrente() {
        if (frente == null) {
            return null;
        }
        return frente.getDato();
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamaño() {
        return cantidad;
    }

    public int tamano() {
        return cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }
}

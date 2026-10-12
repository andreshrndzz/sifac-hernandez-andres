package sifac.comun.estructuras;

import sifac.comun.entidades.Documento;

/*
 * RESPUESTA OBLIGATORIA DEL EXPERIMENTO (Sección 2.3)
 * 2. ¿Qué devolvió al desencolar? FC-100. ¿Qué devuelve estaVacia()? true.
 * 4. verFrente(): FC-200.
 * 5. tamaño(): 1.
 * 
 * Explicación de qué pasó con FC-200: Al desencolar el primer documento, 
 * la cola quedó vacía (frente = null), pero la referencia 'fin' quedó 
 * apuntando al nodo retirado en un estado inconsistente. Como nuestro método 
 * encolar pregunta si 'frente == null', la estructura logró corregirse sola y 
 * enlazó el FC-200 correctamente como nuevo frente. Sin embargo, si hubiese 
 * preguntado por el 'fin', el FC-200 se habría enganchado al nodo "fantasma", 
 * desconectándose de la cadena principal y perdiéndose en la memoria sin 
 * generar errores visibles.
 */
public class ColaEnvio {

    private NodoDoc frente;
    private NodoDoc fin;
    private int cantidad;

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

    public Documento desencolar() {
        if (frente == null) {
            return null;
        }
        Documento d = frente.getDato();
        frente = frente.getSiguiente();

        // CORRECCIÓN APLICADA: Si salió el último elemento, 
        // limpiamos la referencia 'fin' para no dejar basura.
        if (frente == null) {
            fin = null;
        }

        cantidad--;
        return d;
    }

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
}

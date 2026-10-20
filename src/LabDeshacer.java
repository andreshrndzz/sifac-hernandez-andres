package sifac_andres;

import java.util.Stack;

/*
 * RESPUESTA OBLIGATORIA DEL LABORATORIO 6 (Paso 6.1):
 * ¿Cuál conducta se prefiere para el manejo de la pila vacía en SIFAC?
 * En nuestro dominio (la caja de la ferretería), que el cajero presione "deshacer" 
 * sin nada en la factura no es un error crítico del sistema, sino una acción que 
 * simplemente no tiene efecto. Por lo tanto, se prefiere evitar la excepción 
 * 'EmptyStackException'. La conducta correcta es comprobar primero con `pila.empty()` 
 * (o devolver null), en lugar de intentar desapilar a ciegas y dejar que el programa se caiga.
 */
public class LabDeshacer {

    public static void main(String[] args) {
        Stack<String> pila = new Stack<>();

        pila.push("agregar CLA-2");
        pila.push("agregar PIN-GL");
        pila.push("agregar TUB-12");
        pila.push("quitar TUB-12");

        // Impresiones exactas que pide la primera parte de la sección 6
        System.out.println("6. Pila de deshacer");
        System.out.println("   tamano        = " + pila.size());
        System.out.println("   tope (peek)   = " + pila.peek());
        System.out.println("   tamano        = " + pila.size());
        System.out.println("   saca (pop)    = " + pila.pop()); // Saca 1 elemento
        System.out.println("   nuevo tope    = " + pila.peek());
        System.out.println("   tamano        = " + pila.size());
        System.out.println("   vacia         = " + pila.empty());
        System.out.println("   search(CLA-2) = " + pila.search("agregar CLA-2"));

        // 6.1 El borde: Sacamos los 3 restantes
        pila.pop();
        pila.pop();
        pila.pop();

        System.out.println("   vacia         = " + pila.empty());
        System.out.println("   un pop mas... ");

        // Forma correcta de manejar el borde en la entrega final (evitar la excepción)
        if (!pila.empty()) {
            System.out.println(pila.pop());
        } else {
            System.out.println("   nada que deshacer");
        }
    }
}

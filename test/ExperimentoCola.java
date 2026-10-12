package sifac.comun.logica;

import sifac.comun.entidades.Cliente;
import sifac.comun.entidades.Documento;
import sifac.comun.entidades.FacturaContado;
import sifac.comun.estructuras.ColaEnvio;

public class ExperimentoCola {

    public static void main(String[] args) {
        // 1. Cree una cola nueva y encole un solo documento: FC-100
        ColaEnvio cola = new ColaEnvio();
        Cliente clienteFalso = new Cliente("111", "Juan");
        Documento doc1 = new FacturaContado("FC-100", clienteFalso);
        cola.encolar(doc1);

        // 2. Desencole. 
        Documento desencolado = cola.desencolar();
        System.out.println("2. ¿Qué devolvió al desencolar?: "
                + (desencolado != null ? desencolado.describir() : "null"));
        System.out.println("   ¿Qué devuelve estaVacia()?: " + cola.estaVacia());

        // 3. Sin crear una cola nueva, encole FC-200.
        Documento doc2 = new FacturaContado("FC-200", clienteFalso);
        cola.encolar(doc2);

        // 4. Llame a verFrente(). 
        Documento alFrente = cola.verFrente();
        System.out.println("4. Llame a verFrente(). ¿Qué devolvió?: "
                + (alFrente != null ? alFrente.describir() : "null"));

        // 5. Llame a tamaño(). 
        System.out.println("5. Llame a tamaño(). ¿Qué devolvió?: " + cola.tamaño());
    }
}

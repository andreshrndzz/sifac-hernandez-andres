package sifac_andres;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import sifac.comun.entidades.Cliente;
import sifac.comun.entidades.Documento;
import sifac.comun.entidades.FacturaContado;
import sifac.comun.entidades.FacturaCredito;
import sifac.comun.entidades.NotaCredito;

/*
 * RESPUESTAS OBLIGATORIAS DEL LABORATORIO 6:
 * 1. ¿Por qué el máximo natural devuelve "NC-1011"? 
 *    Porque la búsqueda es alfabética sobre el código (String), y la letra 'N' es mayor lexicográficamente que la 'F'.
 * 
 * 2. ¿Qué compara String.compareTo entre "FC-1012" y "FCR-1003"? 
 *    Compara carácter por carácter. Al llegar al tercer espacio, la letra 'R' de FCR es "mayor" que el guion '-' de FC, por lo que FCR queda después.
 * 
 * 3. ¿El paso 3.3b coincide con el 3? 
 *    Sí, coinciden con estos pocos datos de prueba. Sin embargo, si agregamos un "FC-1020" de 300,000 colones dejarían de coincidir, 
 *    porque el orden natural lo pondría al final por el código, mientras que el Comparator por monto lo pondría de primero.
 * 
 * 4. Tabla del recorrido de búsqueda binaria 4.3 (buscando FCR-1003):
 *    - Paso 1: Lee FC-1012. FCR-1003 va después, descarta la primera mitad de la lista.
 *    - Paso 2: Lee FCR-1009. FCR-1003 va antes, restringe el rango de búsqueda hacia atrás.
 *    - Paso 3: Lee FCR-1003. Lo encuentra y finaliza el recorrido en 3 pasos en lugar de 7.
 */
public class LabIndice {

    // Método auxiliar obligatorio para imprimir la lista en consola con su formato de tabla
    static void mostrar(String etiqueta, List<Documento> lista) {
        System.out.println(etiqueta);
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("   [" + i + "] " + lista.get(i).toString());
        }
    }

    // Método auxiliar obligatorio que busca y muestra resultados o valores negativos
    static void buscar(String etiqueta, List<Documento> lista, String codigo) {
        // Se crea un documento falso solo con el código para usarlo como clave de búsqueda binaria
        Cliente c = new Cliente("0", "X");
        Documento clave = new FacturaContado(codigo, c);
        int i = Collections.binarySearch(lista, clave);
        System.out.println("   " + etiqueta + " binarySearch(\"" + codigo + "\") = " + i);
    }

    public static void main(String[] args) {
        Cliente c = new Cliente("111", "Cliente Prueba");

        // PARTE 5: Usamos ArrayList porque un índice de facturas se consulta por código (con buscar) 
        // muchísimo más de lo que se le insertan datos al inicio.
        List<Documento> captura = new ArrayList<>();

        // Datos de prueba estandarizados por el profesor
        captura.add(new FacturaContado("FC-1007", c));
        captura.add(new FacturaCredito("FCR-1003", c));
        captura.add(new NotaCredito("NC-1001", c));
        captura.add(new FacturaContado("FC-1012", c));
        captura.add(new FacturaCredito("FCR-1009", c));
        captura.add(new FacturaContado("FC-1002", c));
        captura.add(new NotaCredito("NC-1011", c));

        // PRECONDICIÓN OBLIGATORIA: Ordenar la lista para que la búsqueda binaria funcione correctamente
        Collections.sort(captura);
        mostrar("4.4 Indice ordenado", captura);

        // Búsquedas de evaluación
        System.out.println("\n   busquedas correctas");
        buscar("[ok]", captura, "FCR-1009"); // Debe salir 4
        buscar("[ok]", captura, "FC-1012");  // Debe salir 2
        buscar("[ok]", captura, "NC-1011");  // Debe salir 6
        buscar("[ok]", captura, "FC-1005");  // FC-1005 no existe, debe devolver negativo (-2)
    }
}

package sifac.comun.entidades;

import sifac.comun.estructuras.ListaLineas;
import sifac.comun.estructuras.NodoLinea;

/**
 * Clase de demostracion de la Tarea 1.
 * Construye la lista de prueba y ejecuta las 4 operaciones avanzadas
 * mostrando los resultados esperados.
 */
public class DemoTarea1 {

    public static void main(String[] args) {

        // --- Crear los productos de prueba ---
        Producto tuboPVC  = new Producto("PVC-050", "Tubo PVC 1/2", 1000, 100);
        Producto cemento  = new Producto("CEM-50",  "Cemento 50 kg", 8500, 200);
        Producto clavos   = new Producto("CLA-2",   "Clavos 2 pulg", 1200, 500);
        Producto pintura  = new Producto("PIN-GL",  "Pintura galon", 14000, 80);

        // --- Construir la lista de prueba ---
        ListaLineas lista = new ListaLineas();
        lista.agregarAlFinal(new LineaDetalle(tuboPVC, 10));   // pos 0: PVC-050 x 10  = 10000
        lista.agregarAlFinal(new LineaDetalle(cemento, 2));    // pos 1: CEM-50  x 2   = 17000
        lista.agregarAlFinal(new LineaDetalle(clavos, 5));     // pos 2: CLA-2   x 5   = 6000
        lista.agregarAlFinal(new LineaDetalle(tuboPVC, 4));    // pos 3: PVC-050 x 4   = 4000
        lista.agregarAlFinal(new LineaDetalle(pintura, 1));    // pos 4: PIN-GL  x 1   = 14000

        System.out.println("========================================");
        System.out.println("         TAREA 1 - DEMOSTRACION         ");
        System.out.println("========================================");

        System.out.println("\n--- Estado inicial ---");
        System.out.println("Contenido: " + lista.verContenido());
        System.out.printf("Subtotal: %.0f%n", lista.getSubtotal());
        System.out.println("Cantidad de lineas: " + lista.getCantidad());

        // --- 3.1 Buscar ---
        System.out.println("\n--- 3.1 Buscar ---");

        LineaDetalle encontrada = lista.buscar("CLA-2");
        System.out.println("buscar(\"CLA-2\"): importe = " +
            (encontrada != null ? String.format("%.0f", encontrada.getImporte()) : "null"));

        LineaDetalle inexistente = lista.buscar("TOR-8");
        System.out.println("buscar(\"TOR-8\"): " +
            (inexistente != null ? "encontrado" : "null"));

        LineaDetalle duplicado = lista.buscar("PVC-050");
        System.out.println("buscar(\"PVC-050\"): cantidad = " +
            (duplicado != null ? duplicado.getCantidad() : "null") +
            " (posicion 0)");

        // --- 3.2 Linea mayor ---
        System.out.println("\n--- 3.2 Linea mayor ---");
        LineaDetalle mayor = lista.getLineaMayor();
        System.out.println("getLineaMayor(): " +
            (mayor != null ?
                mayor.getProducto().getCodigo() + ", importe = " +
                String.format("%.0f", mayor.getImporte()) : "null"));

        // --- 3.3 Consolidar ---
        System.out.println("\n--- 3.3 Consolidar ---");
        int eliminadas = lista.consolidar();
        System.out.println("consolidar(): lineas eliminadas = " + eliminadas);
        System.out.println("getCantidad() tras consolidar: " + lista.getCantidad());
        System.out.printf("getSubtotal() tras consolidar: %.0f (no debe cambiar)%n",
            lista.getSubtotal());

        LineaDetalle pvcConsolidado = lista.buscar("PVC-050");
        System.out.println("buscar(\"PVC-050\") tras consolidar: cantidad = " +
            (pvcConsolidado != null ? pvcConsolidado.getCantidad() : "null") +
            ", importe = " +
            (pvcConsolidado != null ? String.format("%.0f", pvcConsolidado.getImporte()) : "null"));

        System.out.println("verContenido() tras consolidar:");
        System.out.println("  " + lista.verContenido());

        // --- 3.4 Invertir ---
        System.out.println("\n--- 3.4 Invertir ---");
        lista.invertir();
        System.out.println("verContenido() tras invertir:");
        System.out.println("  " + lista.verContenido());
        System.out.printf("getSubtotal() tras invertir: %.0f (no debe cambiar)%n",
            lista.getSubtotal());
        System.out.println("getCantidad() tras invertir: " + lista.getCantidad());

        System.out.println("\n========================================");
        System.out.println("         VERIFICACION COMPLETA          ");
        System.out.println("========================================");
    }
}

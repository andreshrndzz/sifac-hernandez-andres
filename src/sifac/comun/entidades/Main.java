package sifac.comun.entidades;

/**
 * Clase principal que demuestra el polimorfismo puro en el cierre de caja.
 * El ciclo recorre documentos sin usar condicionales, instanceof ni comparación de cadenas.
 */
public class Main {

    public static void main(String[] args) {

        // --- Clases de apoyo ---
        Producto martillo = new Producto("P-001", "Martillo", 1000, 50);

        Cliente donRafa = new Cliente("101110111", "Don Rafa");
        Cliente constructora = new Cliente("301230123", "Constructora XYZ");
        Cliente doñaMarta = new Cliente("202220222", "Doña Marta");
        Cliente ferreMax = new Cliente("303330333", "FerreMax S.A.");
        Cliente tallerJose = new Cliente("404440444", "Taller Don José");

        // --- Arreglo de documentos del día (tipo Documento) ---
        Documento[] delDia = new Documento[5];

        delDia[0] = new FacturaContado("FC-001", donRafa);
        delDia[1] = new FacturaCredito("FCR-002", constructora);
        delDia[2] = new NotaCredito("NC-003", doñaMarta);
        delDia[3] = new Proforma("PRO-004", ferreMax, 15);
        delDia[4] = new FacturaContado("FC-005", tallerJose);

        // Agregar 10 unidades del producto de ₡1,000 a cada documento
        for (Documento d : delDia) {
            d.agregarLinea(martillo, 10);
        }

        // --- Cierre de caja: polimorfismo puro, cero condicionales ---
        System.out.println("========================================");
        System.out.println("         CIERRE DE CAJA DEL DÍA         ");
        System.out.println("========================================");

        double cierreDeCaja = 0;

        for (Documento d : delDia) {
            System.out.println(d.describir());
            System.out.printf("   Total: %.2f%n", d.calcularTotal());
            cierreDeCaja += d.calcularTotal();
        }

        System.out.println("========================================");
        System.out.printf("Cierre de caja: %.2f%n", cierreDeCaja);
        System.out.println("========================================");

        // --- Verificación de puntos de control ---
        System.out.println();
        System.out.println("--- Verificación de totales individuales ---");
        System.out.printf("FacturaContado  (esperado 10735.00): %.2f%n", delDia[0].calcularTotal());
        System.out.printf("FacturaCredito  (esperado 11526.00): %.2f%n", delDia[1].calcularTotal());
        System.out.printf("NotaCredito     (esperado -11300.00): %.2f%n", delDia[2].calcularTotal());
        System.out.printf("Proforma        (esperado 11300.00): %.2f%n", delDia[3].calcularTotal());
        System.out.printf("FacturaContado  (esperado 10735.00): %.2f%n", delDia[4].calcularTotal());

        // --- Demostración de Agregación: Sucursal y Cajero ---
        System.out.println();
        System.out.println("--- Demostración de Agregación ---");
        Cajero carlos = new Cajero("C-001", "Carlos Pérez");
        Cajero ana = new Cajero("C-002", "Ana Solano");

        Sucursal sucCentral = new Sucursal("Sucursal Central", "San José");
        sucCentral.asignar(carlos);
        sucCentral.asignar(ana);

        System.out.println("Sucursal: " + sucCentral.getNombre() + " - " + sucCentral.getCanton());
        for (Cajero c : sucCentral.getCajeros()) {
            System.out.println("   Cajero: " + c.getCodigo() + " - " + c.getNombre());
        }
    }
}

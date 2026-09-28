package sifac.caja.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import sifac.comun.entidades.Documento;
import sifac.comun.logica.ServicioFacturacion;

/**
 * Capa de presentacion: consola del cajero.
 * RESTRICCION: No calcula impuestos, descuentos ni recargos.
 * Solo pide datos, llama al ServicioFacturacion y muestra resultados.
 */
public class ConsolaCaja {

    private final ServicioFacturacion servicio;

    public ConsolaCaja(ServicioFacturacion servicio) {
        this.servicio = servicio;
    }

    /**
     * Ejecuta el menu principal de la consola del cajero.
     *
     * @param sc Scanner para leer la entrada del usuario
     */
    public void ejecutar(Scanner sc) {
        List<Documento> delDia = new ArrayList<>();
        Documento actual = null;
        boolean salir = false;

        while (!salir) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("          CONSOLA DE CAJA SIFAC         ");
            System.out.println("========================================");
            System.out.println("1. Abrir factura");
            System.out.println("2. Abrir nota de credito");
            System.out.println("3. Agregar linea al documento actual");
            System.out.println("4. Cerrar documento actual");
            System.out.println("5. Ver cierre del dia");
            System.out.println("6. Salir");
            System.out.print("Opcion: ");

            String opcion = sc.nextLine().trim();

            switch (opcion) {
                case "1":
                    System.out.print("Cedula del cliente: ");
                    String cedula = sc.nextLine().trim();
                    System.out.print("Forma de pago (CONTADO/CREDITO): ");
                    String forma = sc.nextLine().trim();
                    try {
                        actual = servicio.abrirFactura(cedula, forma);
                        System.out.println("Documento abierto: " + actual.describir());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.print("Cedula del cliente: ");
                    String cedNota = sc.nextLine().trim();
                    try {
                        actual = servicio.abrirNotaCredito(cedNota);
                        System.out.println("Nota de credito abierta: " + actual.describir());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case "3":
                    if (actual == null) {
                        System.out.println("No hay documento abierto. Abra uno primero.");
                        break;
                    }
                    System.out.print("Codigo del producto: ");
                    String codigo = sc.nextLine().trim();
                    System.out.print("Cantidad: ");
                    try {
                        int cantidad = Integer.parseInt(sc.nextLine().trim());
                        servicio.agregarLinea(actual, codigo, cantidad);
                        System.out.println("Linea agregada.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case "4":
                    if (actual == null) {
                        System.out.println("No hay documento abierto.");
                        break;
                    }
                    System.out.println("Documento cerrado: " + actual.describir());
                    System.out.printf("   Total: %.2f%n", actual.calcularTotal());
                    delDia.add(actual);
                    actual = null;
                    break;

                case "5":
                    System.out.println();
                    System.out.println("--- CIERRE DEL DIA ---");
                    for (Documento d : delDia) {
                        System.out.println(d.describir());
                        System.out.printf("   Total: %.2f%n", d.calcularTotal());
                    }
                    double cierre = servicio.cerrarCaja(delDia);
                    System.out.printf("Cierre de caja: %.2f%n", cierre);
                    break;

                case "6":
                    salir = true;
                    System.out.println("Sesion finalizada.");
                    break;

                default:
                    System.out.println("Opcion no valida.");
                    break;
            }
        }
    }

    /**
     * Punto de entrada de la aplicacion de caja.
     */
    public static void main(String[] args) {
        sifac.comun.datos.RepositorioClientesMemoria repoClientes = new sifac.comun.datos.RepositorioClientesMemoria();
        sifac.comun.datos.RepositorioProductosMemoria repoProductos = new sifac.comun.datos.RepositorioProductosMemoria();
        ServicioFacturacion servicio = new ServicioFacturacion(repoClientes, repoProductos);

        ConsolaCaja consola = new ConsolaCaja(servicio);
        Scanner sc = new Scanner(System.in);
        consola.ejecutar(sc);
        sc.close();
    }
}

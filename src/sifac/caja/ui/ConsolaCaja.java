package sifac.caja.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import sifac.comun.datos.RepositorioClientes;
import sifac.comun.datos.RepositorioClientesPadron;
import sifac.comun.datos.RepositorioProductos;
import sifac.comun.datos.RepositorioProductosMemoria;
import sifac.comun.datos.ServicioPadronException;
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
                    System.out.print("Cedula del cliente (9 digitos): ");
                    String cedula = sc.nextLine().trim();
                    System.out.print("Forma de pago (CONTADO/CREDITO): ");
                    String forma = sc.nextLine().trim();
                    try {
                        actual = servicio.abrirFactura(cedula, forma);
                        System.out.println("CLIENTE | " + actual.getCliente().getCedula()
                                + " | " + actual.getCliente().getNombre());
                        System.out.println("Documento abierto: " + actual.describir());
                    } catch (ServicioPadronException e) {
                        System.out.println("PADRON NO DISPONIBLE | " + e.getMessage()
                                + ". Verifique el servidor e intente de nuevo.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("AVISO | " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.print("Cedula del cliente (9 digitos): ");
                    String cedNota = sc.nextLine().trim();
                    try {
                        actual = servicio.abrirNotaCredito(cedNota);
                        System.out.println("CLIENTE | " + actual.getCliente().getCedula()
                                + " | " + actual.getCliente().getNombre());
                        System.out.println("Nota de credito abierta: " + actual.describir());
                    } catch (ServicioPadronException e) {
                        System.out.println("PADRON NO DISPONIBLE | " + e.getMessage()
                                + ". Verifique el servidor e intente de nuevo.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("AVISO | " + e.getMessage());
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
     * Si recibe argumentos (host puerto cedula), ejecuta la consulta directa.
     * Si no recibe argumentos, abre el menu interactivo.
     */
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int puerto = args.length > 1 ? Integer.parseInt(args[1]) : 5051;

        // SUSTITUCION DE SEMANA 11: Una sola linea cambia la fuente de datos
        // Antes (Semana 2): RepositorioClientes repoClientes = new RepositorioClientesMemoria();
        RepositorioClientes repoClientes = new RepositorioClientesPadron(host, puerto);
        RepositorioProductos repoProductos = new RepositorioProductosMemoria();
        ServicioFacturacion servicio = new ServicioFacturacion(repoClientes, repoProductos);

        if (args.length >= 3) {
            String cedula = args[2];
            try {
                Documento doc = servicio.abrirFactura(cedula, "CONTADO");
                System.out.println("CLIENTE | " + doc.getCliente().getCedula()
                        + " | " + doc.getCliente().getNombre());
            } catch (ServicioPadronException e) {
                System.out.println("PADRON NO DISPONIBLE | " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("AVISO | " + e.getMessage());
            }
            return;
        }

        ConsolaCaja consola = new ConsolaCaja(servicio);
        Scanner sc = new Scanner(System.in);
        consola.ejecutar(sc);
        sc.close();
    }
}
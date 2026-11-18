package sifac.caja.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import sifac.comun.datos.RepositorioClientes;
import sifac.comun.datos.RepositorioClientesMemoria;
import sifac.comun.datos.RepositorioClientesPadron;
import sifac.comun.datos.RepositorioProductos;
import sifac.comun.datos.RepositorioProductosMemoria;
import sifac.comun.datos.ServicioPadronException;
import sifac.comun.entidades.Documento;
import sifac.comun.entidades.LineaDetalle;
import sifac.comun.entidades.Producto;
import sifac.comun.logica.ServicioFacturacion;

/**
 * Capa de presentacion: consola del cajero.
 * RESTRICCION: No calcula impuestos, descuentos ni recargos.
 * Solo pide datos, llama al ServicioFacturacion y muestra resultados.
 */
public class ConsolaCaja {

    private static final String[] CODIGOS_CATALOGO = {
        "PVC-050", "CEM-01", "CLV-02", "PNT-03", "TRN-04"
    };

    private final ServicioFacturacion servicio;

    public ConsolaCaja(ServicioFacturacion servicio) {
        this.servicio = servicio;
    }

    /**
     * Ejecuta el menu principal de la consola del cajero con los 9 requisitos
     * funcionales de la Entrega 1.
     */
    public void ejecutar(Scanner sc) {
        List<Documento> delDia = new ArrayList<>();
        Documento actual = null;
        boolean salir = false;

        // Inicia el hilo consumidor en segundo plano que vacía la ColaEnvio (Semanas 8, 9 y 10)
        servicio.iniciarProcesadorEnvioSegundoPlano();

        while (!salir) {
            System.out.println();
            System.out.println("========================================================");
            System.out.println("                 CONSOLA DE CAJA SIFAC                  ");
            System.out.println("========================================================");
            if (actual != null) {
                System.out.printf("Documento en curso: %s | Estado: %s | Subtotal: %.2f%n",
                        actual.describir(), actual.getEstadoActual(), actual.getSubtotal());
            } else {
                System.out.println("Documento en curso: [Ninguno abierto]");
            }
            System.out.printf("Cola de envio: %d pendientes | %d enviados en 2do plano%n",
                    servicio.getColaEnvio().tamano(), servicio.getDocumentosEnviados());
            System.out.println("--------------------------------------------------------");
            System.out.println("1. Abrir Factura (CONTADO / CREDITO)");
            System.out.println("2. Abrir Nota de Credito");
            System.out.println("3. Abrir Proforma");
            System.out.println("4. Agregar linea al documento actual (descuenta stock)");
            System.out.println("5. Deshacer ultima linea agregada (Pila LIFO)");
            System.out.println("6. Ver detalle, historial de estados e inventario");
            System.out.println("7. Cerrar documento actual y encolar para envio");
            System.out.println("8. Ver cierre de caja del dia");
            System.out.println("9. Listar documentos del dia ordenados por monto");
            System.out.println("10. Buscar documento por numero (Busqueda Binaria)");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");

            String opcion = sc.nextLine().trim();

            switch (opcion) {
                case "1":
                    if (actual != null) {
                        System.out.println("AVISO | Ya hay un documento abierto (" + actual.getNumero() + "). Cierrelo primero.");
                        break;
                    }
                    System.out.print("Cedula del cliente: ");
                    String cedula = sc.nextLine().trim();
                    System.out.print("Forma de pago (CONTADO/CREDITO): ");
                    String forma = sc.nextLine().trim();
                    try {
                        actual = servicio.abrirFactura(cedula, forma);
                        System.out.println("CLIENTE | " + actual.getCliente().getCedula()
                                + " | " + actual.getCliente().getNombre());
                        System.out.println("Documento abierto: " + actual.describir()
                                + " [" + actual.getEstadoActual() + "]");
                    } catch (ServicioPadronException e) {
                        System.out.println("PADRON NO DISPONIBLE | " + e.getMessage()
                                + ". Verifique el servidor e intente de nuevo.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("AVISO | " + e.getMessage());
                    }
                    break;

                case "2":
                    if (actual != null) {
                        System.out.println("AVISO | Ya hay un documento abierto (" + actual.getNumero() + "). Cierrelo primero.");
                        break;
                    }
                    System.out.print("Cedula del cliente: ");
                    String cedNota = sc.nextLine().trim();
                    try {
                        actual = servicio.abrirNotaCredito(cedNota);
                        System.out.println("CLIENTE | " + actual.getCliente().getCedula()
                                + " | " + actual.getCliente().getNombre());
                        System.out.println("Nota de credito abierta: " + actual.describir()
                                + " [" + actual.getEstadoActual() + "]");
                    } catch (ServicioPadronException e) {
                        System.out.println("PADRON NO DISPONIBLE | " + e.getMessage()
                                + ". Verifique el servidor e intente de nuevo.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("AVISO | " + e.getMessage());
                    }
                    break;

                case "3":
                    if (actual != null) {
                        System.out.println("AVISO | Ya hay un documento abierto (" + actual.getNumero() + "). Cierrelo primero.");
                        break;
                    }
                    System.out.print("Cedula del cliente: ");
                    String cedPro = sc.nextLine().trim();
                    System.out.print("Dias de vigencia (ej. 15): ");
                    try {
                        int dias = Integer.parseInt(sc.nextLine().trim());
                        actual = servicio.abrirProforma(cedPro, dias);
                        System.out.println("CLIENTE | " + actual.getCliente().getCedula()
                                + " | " + actual.getCliente().getNombre());
                        System.out.println("Proforma abierta: " + actual.describir()
                                + " (Vigencia: " + dias + " dias) [" + actual.getEstadoActual() + "]");
                    } catch (NumberFormatException e) {
                        System.out.println("AVISO | Los dias de vigencia deben ser un numero entero.");
                    } catch (ServicioPadronException e) {
                        System.out.println("PADRON NO DISPONIBLE | " + e.getMessage()
                                + ". Verifique el servidor e intente de nuevo.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("AVISO | " + e.getMessage());
                    }
                    break;

                case "4":
                    if (actual == null) {
                        System.out.println("No hay documento abierto. Abra uno primero.");
                        break;
                    }
                    mostrarCatalogoDisponible();
                    System.out.print("Codigo del producto: ");
                    String codigo = sc.nextLine().trim().toUpperCase();
                    System.out.print("Cantidad: ");
                    try {
                        int cantidad = Integer.parseInt(sc.nextLine().trim());
                        servicio.agregarLinea(actual, codigo, cantidad);
                        Producto p = servicio.consultarProducto(codigo);
                        System.out.println("Linea agregada: " + cantidad + " x " + p.getDescripcion()
                                + " (Stock restante: " + p.getExistencias() + ")");
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Ingrese una cantidad numerica valida.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case "5":
                    if (actual == null) {
                        System.out.println("No hay documento abierto.");
                        break;
                    }
                    boolean deshecho = servicio.deshacerUltimaLinea(actual);
                    if (deshecho) {
                        System.out.println("Ultima linea eliminada (stock repuesto en inventario).");
                    } else {
                        System.out.println("Nada que deshacer: la pila de operaciones esta vacia.");
                    }
                    break;

                case "6":
                    mostrarCatalogoDisponible();
                    if (actual != null) {
                        System.out.println("\n--- DETALLE DEL DOCUMENTO EN CURSO ---");
                        System.out.println("Documento: " + actual.describir());
                        System.out.println("Historial de estados: " + actual.verHistorialEstados());
                        for (LineaDetalle ld : actual.getLineas()) {
                            System.out.printf("   - %-8s %-28s %3d x %8.2f = %10.2f%n",
                                    ld.getProducto().getCodigo(),
                                    ld.getProducto().getDescripcion(),
                                    ld.getCantidad(),
                                    ld.getProducto().getPrecioUnitario(),
                                    ld.getImporte());
                        }
                        System.out.printf("Subtotal: %.2f | Total proyectado: %.2f%n",
                                actual.getSubtotal(), actual.calcularTotal());
                    }
                    break;

                case "7":
                    if (actual == null) {
                        System.out.println("No hay documento abierto.");
                        break;
                    }
                    servicio.cerrarDocumento(actual);
                    System.out.println("Documento cerrado: " + actual.describir());
                    System.out.printf("   Total: %.2f%n", actual.calcularTotal());
                    System.out.println("   Historial: " + actual.verHistorialEstados());
                    System.out.println("   (Encolado en ColaEnvio; el hilo en segundo plano lo enviara automaticamente)");
                    delDia.add(actual);
                    actual = null;
                    break;

                case "8":
                    System.out.println();
                    System.out.println("--- CIERRE DE CAJA DEL DIA ---");
                    if (delDia.isEmpty()) {
                        System.out.println("No se han cerrado documentos hoy.");
                    } else {
                        for (Documento d : delDia) {
                            System.out.printf("%-35s | Total: %10.2f | Historial: %s%n",
                                    d.describir(), d.calcularTotal(), d.verHistorialEstados());
                        }
                    }
                    double cierre = servicio.cerrarCaja(delDia);
                    System.out.printf("Cierre de caja total: %.2f%n", cierre);
                    System.out.printf("Cola de envio: %d pendientes | %d enviados por el hilo consumidor%n",
                            servicio.getColaEnvio().tamano(), servicio.getDocumentosEnviados());
                    break;

                case "9":
                    if (delDia.isEmpty()) {
                        System.out.println("No hay documentos cerrados para ordenar.");
                        break;
                    }
                    System.out.println("\n--- DOCUMENTOS ORDENADOS POR MONTO (MENOR A MAYOR) ---");
                    List<Documento> porMonto = servicio.listarOrdenadosPorMonto(delDia);
                    for (int i = 0; i < porMonto.size(); i++) {
                        Documento d = porMonto.get(i);
                        System.out.printf("   [%d] %-10s %-16s Total: %10.2f | Cliente: %s%n",
                                i, d.getNumero(), d.getClass().getSimpleName(),
                                d.calcularTotal(), d.getCliente().getNombre());
                    }
                    break;

                case "10":
                    if (delDia.isEmpty()) {
                        System.out.println("No hay documentos cerrados para buscar.");
                        break;
                    }
                    System.out.print("Numero del documento a buscar (ej. FC-001, FCR-001, NC-001, PRO-001): ");
                    String numBuscar = sc.nextLine().trim();
                    Documento encontrado = servicio.buscarDocumentoPorNumero(delDia, numBuscar);
                    if (encontrado != null) {
                        System.out.println("DOCUMENTO ENCONTRADO: " + encontrado.describir());
                        System.out.printf("   Tipo: %s | Total: %.2f%n",
                                encontrado.getClass().getSimpleName(), encontrado.calcularTotal());
                        System.out.println("   Historial de estados: " + encontrado.verHistorialEstados());
                    } else {
                        System.out.println("No existe ningun documento con el numero: " + numBuscar);
                    }
                    break;

                case "0":
                case "6_SALIR":
                    salir = true;
                    servicio.detenerProcesadorEnvio();
                    System.out.println("Sesion finalizada.");
                    break;

                default:
                    System.out.println("Opcion no valida.");
                    break;
            }
        }
    }

    private void mostrarCatalogoDisponible() {
        System.out.println("\n--- INVENTARIO DISPONIBLE EN FERRETERIA ---");
        for (String cod : CODIGOS_CATALOGO) {
            Producto p = servicio.consultarProducto(cod);
            if (p != null) {
                System.out.printf("   %-8s | %-30s | Precio: %8.2f | Stock: %4d%n",
                        p.getCodigo(), p.getDescripcion(), p.getPrecioUnitario(), p.getExistencias());
            }
        }
    }

    /**
     * Punto de entrada de la aplicacion de caja.
     * Si recibe argumentos (host puerto cedula), ejecuta la consulta directa de Semana 11.
     * Si no recibe argumentos, abre el menu interactivo completo.
     */
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int puerto = args.length > 1 ? Integer.parseInt(args[1]) : 5051;

        // =========================================================================
        // PUNTO DE INYECCION DE DEPENDENCIAS (Semana 2 y Semana 11):
        // - Con ServidorConcurrente encendido en 5051, usa RepositorioClientesPadron
        // - Para correr en una sola maquina sin servidor externo, usa RepositorioClientesMemoria
        // =========================================================================
        // RepositorioClientes repoClientes = new RepositorioClientesMemoria();
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
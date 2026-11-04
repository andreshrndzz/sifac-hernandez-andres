// IA: le pedi el menu de consola para las cuatro opciones solicitadas
// y ajuste el formato de salida de la factura para usar la tasa de venta en dolares.
package sifac.cliente.presentacion;

import sifac.servidor.datos.TipoCambioBCCR;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import sifac.comun.entidades.TipoCambio;
import sifac.servidor.datos.RepositorioTipoCambio;
import sifac.servidor.datos.TipoCambioEnMemoria;
import sifac.servidor.logica.ServicioTipoCambio;

public class ConsolaTipoCambio {

    private static final double TASA_IVA = 0.13;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        // Lo unico que cambia en todo el sistema:
        // RepositorioTipoCambio repo = new TipoCambioEnMemoria();
        RepositorioTipoCambio repo = new TipoCambioBCCR("hernandezp.and@gmail.com", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJCQ0NSLVNEREUiLCJzdWIiOiJoZXJuYW5kZXpwLmFuZEBnbWFpbC5jb20iLCJhdWQiOiJTRERFLVNpdGlvRXh0ZXJubyIsImV4cCI6MjUzNDAyMzAwODAwLCJuYmYiOjE3NTgyNjA0NDEsImlhdCI6MTc1ODI2MDQ0MSwianRpIjoiMGM5NGUyZmEtYzQwZS00MmJjLWJlOTctNjhmOWNhNzcwNmQ2IiwiZW1haWwiOiJoZXJuYW5kZXpwLmFuZEBnbWFpbC5jb20ifQ.GC_T8hSZ6-0vb_bYJQ1Pt6EHCU3Ug1z3QD9kCIQo7DM");
        ServicioTipoCambio servicio = new ServicioTipoCambio(repo);

        Scanner sc = new Scanner(System.in);
        int opcion = 0;

        while (opcion != 4) {
            System.out.println("\n=== MODULO DE TIPO DE CAMBIO SIFAC ===");
            System.out.println("1. Ver el tipo de cambio del dia.");
            System.out.println("2. Convertir dolares a colones.");
            System.out.println("3. Facturar (lineas en colones o dolares).");
            System.out.println("4. Salir.");
            System.out.print("Seleccione una opcion: ");

            String entrada = sc.nextLine().trim();
            if (entrada.isEmpty()) {
                continue;
            }
            try {
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Opcion invalida.");
                continue;
            }

            switch (opcion) {
                case 1:
                    TipoCambio hoy = servicio.obtenerActual();
                    System.out.println("Tipo de cambio actual: " + hoy
                            + " (Diferencial: " + String.format("%.2f", hoy.diferencial()) + ")");
                    break;

                case 2:
                    System.out.print("Monto en dolares ($): ");
                    double usd = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("¿Es para pagar a proveedor (P) o para recibir de cliente (R)?: ");
                    String sentido = sc.nextLine().trim().toUpperCase();
                    if (sentido.equals("P")) {
                        double colones = servicio.convertirAColonesParaPagar(usd);
                        System.out.printf("Monto en colones (tasa venta): C %.2f%n", colones);
                    } else if (sentido.equals("R")) {
                        double colones = servicio.convertirAColonesParaRecibir(usd);
                        System.out.printf("Monto en colones (tasa compra): C %.2f%n", colones);
                    } else {
                        System.out.println("Sentido no reconocido. Use P o R.");
                    }
                    break;

                case 3:
                    facturar(sc, servicio);
                    break;

                case 4:
                    System.out.println("Saliendo del sistema...");
                    break;

                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    private static void facturar(Scanner sc, ServicioTipoCambio servicio) {
        System.out.print("¿Desea cargar las 3 lineas de la factura de prueba (S) o digitarlas (N)?: ");
        String modo = sc.nextLine().trim().toUpperCase();

        List<String> lineasFormateadas = new ArrayList<>();
        double subtotal = 0.0;

        if (modo.equals("S")) {
            subtotal += agregarLineaFactura(lineasFormateadas, "Tubo PVC 1/2", 10, "C", 1000.00, servicio);
            subtotal += agregarLineaFactura(lineasFormateadas, "Taladro importado", 1, "$", 120.00, servicio);
            subtotal += agregarLineaFactura(lineasFormateadas, "Clavos 2 pulg", 5, "C", 1200.00, servicio);
        } else {
            while (true) {
                System.out.print("Descripcion del producto (o ENTER para terminar): ");
                String desc = sc.nextLine().trim();
                if (desc.isEmpty()) {
                    break;
                }

                System.out.print("Cantidad: ");
                int cant = Integer.parseInt(sc.nextLine().trim());

                System.out.print("Moneda (C para colones, $ para dolares): ");
                String moneda = sc.nextLine().trim();

                System.out.print("Precio unitario: ");
                double precio = Double.parseDouble(sc.nextLine().trim());

                subtotal += agregarLineaFactura(lineasFormateadas, desc, cant, moneda, precio, servicio);
            }
        }

        double iva = Math.round((subtotal * TASA_IVA) * 100.0) / 100.0;
        double total = Math.round((subtotal + iva) * 100.0) / 100.0;

        System.out.println();
        for (String lf : lineasFormateadas) {
            System.out.println(lf);
        }
        System.out.println("----------------------------------------------");
        System.out.printf("SUBTOTAL                            C %8.2f%n", subtotal);
        System.out.printf("IVA 13%%                             C %8.2f%n", iva);
        System.out.printf("TOTAL                               C %8.2f%n", total);
    }

    private static double agregarLineaFactura(List<String> salida, String desc, int cant,
            String moneda, double precio, ServicioTipoCambio servicio) {
        double importeColones;
        if (moneda.equals("$")) {
            importeColones = servicio.convertirAColonesParaPagar(cant * precio);
        } else {
            importeColones = cant * precio;
        }
        salida.add(String.format("%-18s %2d x %s %7.2f -> C %8.2f",
                desc, cant, moneda, precio, importeColones));
        return importeColones;
    }
}

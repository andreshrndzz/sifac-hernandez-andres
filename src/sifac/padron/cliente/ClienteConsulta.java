package sifac.padron.cliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteConsulta {

    public static int PUERTO_PRUEBA = 5051;

    public static void consultar(String etiqueta, int puerto, String... cedulas) {
        long inicio = System.currentTimeMillis();
        try (Socket socket = new Socket("localhost", puerto); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            for (String cedula : cedulas) {
                out.println(cedula);
                String respuesta = in.readLine();
                long transcurrido = System.currentTimeMillis() - inicio;
                System.out.printf("%-10s t= %4d ms  %s -> %s%n", etiqueta, transcurrido, cedula, respuesta);
            }
            out.println("SALIR");
        } catch (Exception e) {
            System.out.println(etiqueta + " error: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length >= 3) {
            String etiqueta = args[0];
            int puerto = Integer.parseInt(args[1]);
            String[] cedulas = new String[args.length - 2];
            System.arraycopy(args, 2, cedulas, 0, cedulas.length);
            consultar(etiqueta, puerto, cedulas);
        } else {
            System.out.println("=== Lanzando CLIENTE-A y CLIENTE-B al mismo tiempo contra el puerto " + PUERTO_PRUEBA + " ===");
            Thread clienteA = new Thread(() -> consultar("CLIENTE-A", PUERTO_PRUEBA, "104560789", "112340567"));
            Thread clienteB = new Thread(() -> consultar("CLIENTE-B", PUERTO_PRUEBA, "203450678", "999999999"));

            clienteA.start();
            Thread.sleep(100); // Pequeña ventaja para que A entre primero
            clienteB.start();

            clienteA.join();
            clienteB.join();
        }
    }
}

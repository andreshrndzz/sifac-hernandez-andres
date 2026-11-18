package sifac.caja.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class EnviarDocumento {

    public static String enviar(String host, int puerto, String id, String monto) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, puerto), 2500);
            socket.setSoTimeout(4500);
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

                out.write("REGISTRAR|" + id + "|" + monto);
                out.newLine();
                out.flush();

                String respuesta = in.readLine();
                if (("ACK|" + id).equals(respuesta)) {
                    return "CONFIRMADO | " + id + " | " + monto + " (" + respuesta + ")";
                }
                return "PENDIENTE (NO CONFIRMADO) | " + id + " | Respuesta: " + respuesta;
            }
        } catch (IOException ex) {
            return "PENDIENTE (SIN CONEXION AL CENTRAL) | " + id + " | " + ex.getMessage();
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 5052;
        if (args.length >= 2) {
            System.out.println(enviar(host, puerto, args[0], args[1]));
            return;
        }
        // Ejecuta automaticamente los 3 casos de la Parte 5 del laboratorio:
        System.out.println("1. Primer envio:        " + enviar(host, puerto, "F-001", "1000.00"));
        System.out.println("2. Repeticion identica: " + enviar(host, puerto, "F-001", "1000.00"));
        System.out.println("3. Envio con conflicto: " + enviar(host, puerto, "F-001", "2500.00"));
    }
}
package sifac.central;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CentralDemo {

    public static final int PUERTO = 5052;
    private static final Map<String, String> recibidos = new HashMap<>();

    public static void main(String[] args) throws IOException {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("[central-demo] escuchando en puerto " + PUERTO);
            while (true) {
                Socket s = servidor.accept();
                new Thread(() -> atender(s)).start();
            }
        }
    }

    private static void atender(Socket s) {
        try (Socket socket = s;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            String linea = in.readLine();
            if (linea == null) {
                return;
            }
            String[] partes = linea.trim().split("\\|", -1);
            String respuesta;
            if (partes.length != 3 || !"REGISTRAR".equals(partes[0])) {
                respuesta = "ERROR|Formato invalido";
            } else {
                String id = partes[1].trim();
                String monto = partes[2].trim();
                synchronized (recibidos) {
                    String previo = recibidos.get(id);
                    if (previo == null) {
                        recibidos.put(id, monto);
                        respuesta = "ACK|" + id;
                        System.out.println("[central-demo] registrado nuevo: " + id + " por " + monto);
                    } else if (previo.equals(monto)) {
                        respuesta = "ACK|" + id;
                        System.out.println("[central-demo] reintento idempotente confirmado: " + id);
                    } else {
                        respuesta = "CONFLICTO|" + id;
                        System.out.println("[central-demo] conflicto en " + id + " (previo=" + previo + ", nuevo=" + monto + ")");
                    }
                }
            }
            out.write(respuesta);
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            System.out.println("[central-demo] error: " + ex.getMessage());
        }
    }
}
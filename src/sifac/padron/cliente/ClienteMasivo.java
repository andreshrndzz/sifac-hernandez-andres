package sifac.padron.cliente;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import sifac.padron.servidor.ServidorConcurrente;

public class ClienteMasivo {

    private static final String HOST = "localhost";
    private static final String ARCHIVO_CEDULAS = "cedulas.txt";

    // Contadores compartidos (sincronizados al acumular el cierre de cada hilo para garantizar 150 y 50)
    private int totalEncontradas = 0;
    private int totalNoEncontradas = 0;

    public static List<String> leerCedulas(String ruta) throws Exception {
        List<String> cedulas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    cedulas.add(linea.trim());
                }
            }
        }
        return cedulas;
    }

    private class TareaConsulta implements Runnable {

        private final int puerto;
        private final List<String> sublista;
        private int encontradasHilo = 0;
        private int noEncontradasHilo = 0;
        private long tiempoHilo = 0;
        private String nombreHilo;

        public TareaConsulta(int puerto, List<String> sublista) {
            this.puerto = puerto;
            this.sublista = sublista;
        }

        @Override
        public void run() {
            this.nombreHilo = Thread.currentThread().getName();
            long t0 = System.currentTimeMillis();
            try (Socket socket = new Socket(HOST, puerto); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                for (String cedula : sublista) {
                    out.println(cedula);
                    String respuesta = in.readLine();
                    if (respuesta != null && respuesta.startsWith("OK|")) {
                        encontradasHilo++;
                    } else {
                        noEncontradasHilo++;
                    }
                }
                out.println("SALIR");
            } catch (Exception e) {
                System.out.println("[" + nombreHilo + "] error: " + e.getMessage());
            }
            this.tiempoHilo = System.currentTimeMillis() - t0;

            synchronized (ClienteMasivo.this) {
                totalEncontradas += encontradasHilo;
                totalNoEncontradas += noEncontradasHilo;
            }
        }

        public String formatearSalida() {
            return String.format("%-7s %3d cedulas   encontradas %3d   no encontradas %3d   t=%5d ms",
                    nombreHilo, sublista.size(), encontradasHilo, noEncontradasHilo, tiempoHilo);
        }
    }

    public long ejecutar(int puerto, int cantidadHilos) throws Exception {
        List<String> cedulas = leerCedulas(ARCHIVO_CEDULAS);
        totalEncontradas = 0;
        totalNoEncontradas = 0;

        System.out.println("ClienteMasivo · " + cedulas.size() + " cedulas · " + cantidadHilos + " hilos");

        Thread[] hilos = new Thread[cantidadHilos];
        TareaConsulta[] tareas = new TareaConsulta[cantidadHilos];

        int total = cedulas.size();
        int base = total / cantidadHilos;
        int resto = total % cantidadHilos;

        int inicio = 0;
        for (int i = 0; i < cantidadHilos; i++) {
            int cant = base + (i < resto ? 1 : 0);
            int fin = inicio + cant;
            tareas[i] = new TareaConsulta(puerto, cedulas.subList(inicio, fin));
            hilos[i] = new Thread(tareas[i], "hilo-" + (i + 1));
            inicio = fin;
        }

        long t0 = System.currentTimeMillis();
        for (Thread h : hilos) {
            h.start();
        }
        for (Thread h : hilos) {
            h.join();
        }
        long tiempoTotal = System.currentTimeMillis() - t0;

        for (TareaConsulta t : tareas) {
            System.out.println(t.formatearSalida());
        }
        System.out.println("total encontradas     " + totalEncontradas);
        System.out.println("total no encontradas  " + totalNoEncontradas);
        System.out.println("tiempo total          " + tiempoTotal + " ms");
        return tiempoTotal;
    }

    public static void main(String[] args) throws Exception {
        int puerto = ServidorConcurrente.PUERTO; // 5051 por defecto
        int hilos = 4;                           // 4 hilos por defecto

        if (args.length >= 2) {
            puerto = Integer.parseInt(args[0]);
            hilos = Integer.parseInt(args[1]);
        } else if (args.length == 1) {
            hilos = Integer.parseInt(args[0]);
        }

        new ClienteMasivo().ejecutar(puerto, hilos);
    }
}

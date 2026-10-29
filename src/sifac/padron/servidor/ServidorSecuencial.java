package sifac.padron.servidor;

import java.io.*;
import java.net.*;
import sifac.padron.datos.RepositorioPadron;
import sifac.padron.entidades.Elector;

public class ServidorSecuencial {

    public static final int PUERTO = 5050;

    public static void main(String[] args) throws Exception {
        RepositorioPadron repo = new RepositorioPadron();
        repo.cargar("PADRON_SINTETICO.txt", "Distelec.txt");
        System.out.println("[servidor] padron cargado: "
                + repo.cantidad() + " electores");

        ServerSocket servidor = new ServerSocket(PUERTO);
        System.out.println("[servidor] escuchando en el puerto " + PUERTO);

        while (true) {
            Socket s = servidor.accept(); // AQUI SE BLOQUEA
            System.out.println("[servidor] conectado " + s.getPort());
            atender(s, repo); // y aqui se queda
            System.out.println("[servidor] desconectado " + s.getPort());
        }
    }

    static void atender(Socket s, RepositorioPadron repo) throws Exception {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        String cedula;
        while ((cedula = in.readLine()) != null) {
            if (cedula.equals("SALIR")) break;
            Thread.sleep(1500); // simula consulta pesada
            Elector e = repo.buscarBinaria(cedula.trim());
            out.println(e == null ? "NOTFOUND"
                    : "OK|" + e.getCedula() + "|" + e.getNombreCompleto()
                    + "|" + repo.ubicacionDe(e) + "|junta " + e.getJunta());
        }
        s.close();
    }
}
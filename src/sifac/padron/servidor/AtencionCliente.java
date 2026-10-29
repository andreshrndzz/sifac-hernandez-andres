package sifac.padron.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import sifac.padron.datos.RepositorioPadron;
import sifac.padron.entidades.Elector;

public class AtencionCliente implements Runnable {
    private Socket socket;
    private RepositorioPadron repo;

    public AtencionCliente(Socket socket, RepositorioPadron repo) {
        this.socket = socket;
        this.repo = repo;
    }

    @Override
    public void run() {
        String yo = Thread.currentThread().getName();
        try (BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            System.out.println("[" + yo + "] atendiendo");
            String cedula;
            while ((cedula = in.readLine()) != null) {
                if (cedula.equals("SALIR")) break;
                Thread.sleep(1500);
                Elector e = repo.buscarBinaria(cedula.trim());
                out.println(e == null ? "NOTFOUND"
                        : "OK|" + e.getCedula() + "|" + e.getNombreCompleto()
                        + "|" + repo.ubicacionDe(e) + "|junta " + e.getJunta());
                System.out.println("[" + yo + "] respondio " + cedula);
            }
        } catch (Exception ex) {
            System.out.println("[" + yo + "] error: " + ex.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            System.out.println("[" + yo + "] termino");
        }
    }
}
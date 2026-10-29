package sifac.padron.servidor;

import java.net.ServerSocket;
import java.net.Socket;
import sifac.padron.datos.RepositorioPadron;

public class ServidorConcurrente {

    public static final int PUERTO = 5051;

    public static void main(String[] args) throws Exception {
        RepositorioPadron repo = new RepositorioPadron();
        repo.cargar("PADRON_SINTETICO.txt", "Distelec.txt");
        System.out.println("[servidor] padron cargado: " + repo.cantidad() + " electores");

        ServerSocket servidor = new ServerSocket(PUERTO);
        System.out.println("[servidor] escuchando en el puerto " + PUERTO);
        int n = 0;
        while (true) {
            Socket s = servidor.accept();
            n++;
            Thread hilo = new Thread(new AtencionCliente(s, repo), "cliente-" + n);
            System.out.println("[servidor] hilo creado " + hilo.getName()
                    + " estado " + hilo.getState());
            hilo.start();
            System.out.println("[servidor] hilo arrancado " + hilo.getName()
                    + " estado " + hilo.getState());
        }
    }
}
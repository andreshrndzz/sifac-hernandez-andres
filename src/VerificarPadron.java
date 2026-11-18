package sifac.pruebas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import sifac.comun.datos.RepositorioClientesPadron;
import sifac.comun.datos.ServicioPadronException;
import sifac.comun.entidades.Cliente;

public class VerificarPadron {

    @FunctionalInterface
    interface AccionServidor {
        void atender(Socket s, BufferedReader in, BufferedWriter out) throws Exception;
    }

    private static int iniciarServidorEnsayo(AccionServidor accion) throws Exception {
        ServerSocket server = new ServerSocket(0); // Puerto temporal libre
        int puerto = server.getLocalPort();
        Thread t = new Thread(() -> {
            try (ServerSocket ss = server;
                 Socket s = ss.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8))) {
                accion.atender(s, in, out);
            } catch (Exception ignored) {
            }
        });
        t.setDaemon(true);
        t.start();
        return puerto;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== EJECUTANDO 8 VERIFICACIONES DEL ADAPTADOR ===");

        // 1. Cedula existente con tilde en UTF-8
        int p1 = iniciarServidorEnsayo((s, in, out) -> {
            String ced = in.readLine();
            out.write("OK|" + ced + "|MARÍA JOSÉ NÚÑEZ PÉREZ|CENTRAL, SAN JOSÉ|junta 001");
            out.newLine();
            out.flush();
        });
        Cliente c1 = new RepositorioClientesPadron("localhost", p1).buscarPorCedula("104560789");
        if (c1 == null || !c1.getNombre().equals("MARÍA JOSÉ NÚÑEZ PÉREZ")) {
            throw new AssertionError("Fallo caso 1");
        }
        System.out.println("[OK 1/8] Cedula existente y UTF-8 con tildes: " + c1.getNombre());

        // 2. Cedula inexistente (NOTFOUND -> null)
        int p2 = iniciarServidorEnsayo((s, in, out) -> {
            in.readLine();
            out.write("NOTFOUND");
            out.newLine();
            out.flush();
        });
        Cliente c2 = new RepositorioClientesPadron("localhost", p2).buscarPorCedula("999999999");
        if (c2 != null) {
            throw new AssertionError("Fallo caso 2");
        }
        System.out.println("[OK 2/8] Cedula inexistente devuelve null (NOTFOUND)");

        // 3. Mensaje incompleto (menos de 5 campos)
        int p3 = iniciarServidorEnsayo((s, in, out) -> {
            in.readLine();
            out.write("OK|104560789|NOMBRE INCOMPLETO");
            out.newLine();
            out.flush();
        });
        try {
            new RepositorioClientesPadron("localhost", p3).buscarPorCedula("104560789");
            throw new AssertionError("Debio fallar caso 3");
        } catch (ServicioPadronException ex) {
            System.out.println("[OK 3/8] Mensaje incompleto rechazado: " + ex.getMessage());
        }

        // 4. Cedula devuelta no coincide con la solicitada
        int p4 = iniciarServidorEnsayo((s, in, out) -> {
            in.readLine();
            out.write("OK|203450678|OTRO CLIENTE|ALAJUELA|junta 002");
            out.newLine();
            out.flush();
        });
        try {
            new RepositorioClientesPadron("localhost", p4).buscarPorCedula("104560789");
            throw new AssertionError("Debio fallar caso 4");
        } catch (ServicioPadronException ex) {
            System.out.println("[OK 4/8] Cedula distinta rechazada: " + ex.getMessage());
        }

        // 5. Cierre sin respuesta (EOF / readLine devuelve null)
        int p5 = iniciarServidorEnsayo((s, in, out) -> {
            in.readLine();
            s.close();
        });
        try {
            new RepositorioClientesPadron("localhost", p5).buscarPorCedula("104560789");
            throw new AssertionError("Debio fallar caso 5");
        } catch (ServicioPadronException ex) {
            System.out.println("[OK 5/8] Cierre sin respuesta detectado (no es NOTFOUND): " + ex.getMessage());
        }

        // 6. Lectura lenta (supera el limite de lectura)
        int p6 = iniciarServidorEnsayo((s, in, out) -> {
            in.readLine();
            Thread.sleep(600);
        });
        try {
            new RepositorioClientesPadron("localhost", p6, 500, 200).buscarPorCedula("104560789");
            throw new AssertionError("Debio fallar caso 6");
        } catch (ServicioPadronException ex) {
            System.out.println("[OK 6/8] Timeout de lectura vencido controlado: " + ex.getMessage());
        }

        // 7. Servidor apagado (conexion rechazada)
        int puertoCerrado;
        try (ServerSocket ss = new ServerSocket(0)) {
            puertoCerrado = ss.getLocalPort();
        }
        try {
            new RepositorioClientesPadron("localhost", puertoCerrado, 500, 500).buscarPorCedula("104560789");
            throw new AssertionError("Debio fallar caso 7");
        } catch (ServicioPadronException ex) {
            System.out.println("[OK 7/8] Conexion rechazada controlada: " + ex.getMessage());
        }

        // 8. Entrada con salto de linea o formato invalido
        try {
            new RepositorioClientesPadron("localhost", p1).buscarPorCedula("104560789\nSALIR");
            throw new AssertionError("Debio fallar caso 8");
        } catch (IllegalArgumentException ex) {
            System.out.println("[OK 8/8] Salto de linea bloqueado antes de abrir socket: " + ex.getMessage());
        }

        System.out.println("=== RESULTADO: 8/8 VERIFICACIONES CORRECTAS ===");
    }
}
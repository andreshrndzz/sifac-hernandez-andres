package sifac.padron.servidor;

public class DemoEstados {
    public static void main(String[] args) throws Exception {
        Thread h = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }, "hilo-demo");

        // 1. Estado NEW
        System.out.println("1. Antes de start(): " + h.getState());

        // 2. Estado RUNNABLE
        h.start();
        System.out.println("2. Recien arrancado: " + h.getState());

        // 3. Estado TIMED_WAITING (mientras duerme en sleep)
        Thread.sleep(200);
        System.out.println("3. Durante sleep():  " + h.getState());

        // 4. Estado TERMINATED (después de esperar con join)
        h.join();
        System.out.println("4. Despues de join(): " + h.getState());

        // Verificación: un hilo terminado no se reutiliza (debe lanzar IllegalThreadStateException)
        System.out.println("\nIntentando segundo h.start() sobre hilo terminado:");
        h.start();
    }
}
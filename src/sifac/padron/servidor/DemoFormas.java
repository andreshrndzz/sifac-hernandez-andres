package sifac.padron.servidor;

public class DemoFormas {

    static class ComoHilo extends Thread {
        public ComoHilo(String nombre) {
            super(nombre);
        }
        @Override
        public void run() {
            System.out.println("[" + Thread.currentThread().getName() + "] extiende Thread");
        }
    }

    static class ComoRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("[" + Thread.currentThread().getName() + "] implementa Runnable");
        }
    }

    public static void main(String[] args) throws Exception {
        Thread a = new ComoHilo("forma-1");
        Thread b = new Thread(new ComoRunnable(), "forma-2");

        a.start();
        a.join();

        b.start();
        b.join();

        System.out.println("El error clasico: llamar run() en vez de start()");
        Thread c = new Thread(new ComoRunnable(), "forma-3");
        c.run(); // <- OJO: run(), no start()
        System.out.println("^ se ejecuto en main, no en forma-3. No se creo ningun hilo.");
        System.out.println("estado de c -> " + c.getState());
    }
}
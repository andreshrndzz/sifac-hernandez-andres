package sifac.padron.servidor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DemoPrioridades {
    static final int HILOS = 8, RONDAS = 6;

    public static void main(String[] args) throws Exception {
        for (int ronda = 1; ronda <= RONDAS; ronda++) {
            List<Integer> llegada = Collections.synchronizedList(new ArrayList<>());
            Thread[] hilos = new Thread[HILOS];
            for (int i = 0; i < HILOS; i++) {
                final int n = i + 1;
                hilos[i] = new Thread(() -> {
                    long s = 0;
                    for (int k = 0; k < 400_000; k++) s += k % 7;
                    llegada.add(n);
                }, "h" + n);
                hilos[i].setPriority(n == 1 ? Thread.MIN_PRIORITY
                        : n == HILOS ? Thread.MAX_PRIORITY
                        : Thread.NORM_PRIORITY);
            }
            for (Thread h : hilos) h.start();
            for (Thread h : hilos) h.join();
            System.out.println("ronda " + ronda + " -> " + llegada);
        }
    }
}
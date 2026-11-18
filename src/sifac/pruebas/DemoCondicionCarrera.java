package sifac.pruebas;

import java.util.concurrent.atomic.AtomicInteger;
import sifac.comun.entidades.Producto;

/**
 * Demostración reproducible de la Condición de Carrera sobre el inventario
 * de SIFAC y su corrección mediante exclusión mutua con synchronized (Semana 9).
 */
public class DemoCondicionCarrera {

    private static final int STOCK_INICIAL = 10;
    private static final int CAJAS_CONCURRENTES = 30;
    private static final int UNIDADES_POR_VENTA = 1;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("======================================================================");
        System.out.println("   EVIDENCIA SEMANA 9: CONDICION DE CARRERA EN INVENTARIO SIFAC       ");
        System.out.println("======================================================================\n");

        ejecutarSinSincronizar();
        System.out.println();
        ejecutarConSynchronized();
    }

    /**
     * Escenario 1: Varios hilos descuentan usando el método NO sincronizado.
     * Varios hilos leen existencias >= 1 antes de que los demás escriban,
     * vendiendo más unidades de las existentes y dejando el inventario negativo.
     */
    private static void ejecutarSinSincronizar() throws InterruptedException {
        Producto taladro = new Producto("TAL-01", "Taladro Percutor", 35000, STOCK_INICIAL);
        AtomicInteger ventasAprobadas = new AtomicInteger(0);
        AtomicInteger ventasRechazadas = new AtomicInteger(0);

        Thread[] hilos = new Thread[CAJAS_CONCURRENTES];
        for (int i = 0; i < CAJAS_CONCURRENTES; i++) {
            hilos[i] = new Thread(() -> {
                if (taladro.descontarSinSincronizar(UNIDADES_POR_VENTA)) {
                    ventasAprobadas.incrementAndGet();
                } else {
                    ventasRechazadas.incrementAndGet();
                }
            }, "caja-sin-sync-" + (i + 1));
        }

        for (Thread h : hilos) {
            h.start();
        }
        for (Thread h : hilos) {
            h.join();
        }

        System.out.println("--- 1. PRUEBA SIN SINCRONIZAR (CONDICION DE CARRERA PROVOCADA) ---");
        System.out.println("Stock inicial del producto (TAL-01): " + STOCK_INICIAL);
        System.out.println("Cajas intentando comprar 1 unidad al mismo tiempo: " + CAJAS_CONCURRENTES);
        System.out.println("Ventas aprobadas:  " + ventasAprobadas.get() + " (DEBIO SER MAXIMO " + STOCK_INICIAL + ")");
        System.out.println("Ventas rechazadas: " + ventasRechazadas.get());
        System.out.println("Existencias finales en inventario: " + taladro.getExistencias()
                + "  <-- INVENTARIO INCONSISTENTE (NEGATIVO)");
    }

    /**
     * Escenario 2: Los mismos 30 hilos invocan descontarExistencias() (synchronized).
     * Solo 10 cajas logran comprar, 20 reciben rechazo controlado y el stock queda en 0.
     */
    private static void ejecutarConSynchronized() throws InterruptedException {
        Producto taladro = new Producto("TAL-01", "Taladro Percutor", 35000, STOCK_INICIAL);
        AtomicInteger ventasAprobadas = new AtomicInteger(0);
        AtomicInteger ventasRechazadas = new AtomicInteger(0);

        Thread[] hilos = new Thread[CAJAS_CONCURRENTES];
        for (int i = 0; i < CAJAS_CONCURRENTES; i++) {
            hilos[i] = new Thread(() -> {
                try {
                    taladro.descontarExistencias(UNIDADES_POR_VENTA);
                    ventasAprobadas.incrementAndGet();
                } catch (IllegalArgumentException ex) {
                    ventasRechazadas.incrementAndGet();
                }
            }, "caja-sync-" + (i + 1));
        }

        for (Thread h : hilos) {
            h.start();
        }
        for (Thread h : hilos) {
            h.join();
        }

        System.out.println("--- 2. PRUEBA CON SYNCHRONIZED (PROBLEMA CORREGIDO) ---");
        System.out.println("Stock inicial del producto (TAL-01): " + STOCK_INICIAL);
        System.out.println("Cajas intentando comprar 1 unidad al mismo tiempo: " + CAJAS_CONCURRENTES);
        System.out.println("Ventas aprobadas:  " + ventasAprobadas.get() + " (EXACTAMENTE " + STOCK_INICIAL + ")");
        System.out.println("Ventas rechazadas: " + ventasRechazadas.get() + " (Rechazadas por falta de stock)");
        System.out.println("Existencias finales en inventario: " + taladro.getExistencias()
                + "  <-- INVENTARIO CONSISTENTE");
    }
}
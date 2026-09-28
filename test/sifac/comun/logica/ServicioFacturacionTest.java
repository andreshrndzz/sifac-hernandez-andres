package sifac.comun.logica;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import sifac.comun.datos.RepositorioClientesMemoria;
import sifac.comun.datos.RepositorioProductosMemoria;
import sifac.comun.entidades.Documento;

/**
 * Pruebas unitarias del ServicioFacturacion. Verifica los 7 criterios de la
 * rubrica del Laboratorio 2.
 */
public class ServicioFacturacionTest {

    private ServicioFacturacion crearServicio() {
        return new ServicioFacturacion(
                new RepositorioClientesMemoria(),
                new RepositorioProductosMemoria()
        );
    }

    @Test
    public void facturaContadoAplicaImpuestoYDescuento() {
        // 1. PREPARAR
        ServicioFacturacion servicio = crearServicio();
        Documento factura = servicio.abrirFactura("1-1111", "CONTADO");
        servicio.agregarLinea(factura, "PVC-050", 10);

        // 2. EJECUTAR
        double total = factura.calcularTotal();

        // 3. VERIFICAR
        assertEquals(10735.00, total, 0.01);
    }

    @Test
    public void facturaCreditoAplicaImpuestoYRecargo() {
        // 1. PREPARAR
        ServicioFacturacion servicio = crearServicio();
        Documento factura = servicio.abrirFactura("2-2222", "CREDITO");
        servicio.agregarLinea(factura, "PVC-050", 10);

        // 2. EJECUTAR
        double total = factura.calcularTotal();

        // 3. VERIFICAR
        assertEquals(11526.00, total, 0.01);
    }

    @Test
    public void notaCreditoDevuelveTotalNegativo() {
        // 1. PREPARAR
        ServicioFacturacion servicio = crearServicio();
        Documento nota = servicio.abrirNotaCredito("3-3333");
        servicio.agregarLinea(nota, "PVC-050", 10);

        // 2. EJECUTAR
        double total = nota.calcularTotal();

        // 3. VERIFICAR
        assertEquals(-11300.00, total, 0.01);
    }

    @Test
    public void cierreDeCajaMixtoSumaPolimorficamente() {
        // 1. PREPARAR
        ServicioFacturacion servicio = crearServicio();

        Documento contado = servicio.abrirFactura("1-1111", "CONTADO");
        servicio.agregarLinea(contado, "PVC-050", 10);

        Documento credito = servicio.abrirFactura("2-2222", "CREDITO");
        servicio.agregarLinea(credito, "PVC-050", 10);

        Documento nota = servicio.abrirNotaCredito("3-3333");
        servicio.agregarLinea(nota, "PVC-050", 10);

        List<Documento> delDia = new ArrayList<>();
        delDia.add(contado);
        delDia.add(credito);
        delDia.add(nota);

        // 2. EJECUTAR
        double cierre = servicio.cerrarCaja(delDia);

        // 3. VERIFICAR
        assertEquals(10961.00, cierre, 0.01);
    }

    @Test
    public void documentoSinLineasTieneTotalCero() {
        // 1. PREPARAR
        ServicioFacturacion servicio = crearServicio();
        Documento factura = servicio.abrirFactura("1-1111", "CONTADO");

        // 2. EJECUTAR
        double total = factura.calcularTotal();

        // 3. VERIFICAR
        assertEquals(0.00, total, 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noSePuedeAgregarCantidadCero() {
        // 1. PREPARAR
        ServicioFacturacion servicio = crearServicio();
        Documento factura = servicio.abrirFactura("1-1111", "CONTADO");

        // 2. EJECUTAR y 3. VERIFICAR (la excepcion esperada esta en @Test)
        servicio.agregarLinea(factura, "PVC-050", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void clienteNoRegistradoLanzaExcepcion() {
        // 1. PREPARAR
        ServicioFacturacion servicio = crearServicio();

        // 2. EJECUTAR y 3. VERIFICAR (la excepcion esperada esta en @Test)
        servicio.abrirFactura("9-9999", "CONTADO");
    }
}

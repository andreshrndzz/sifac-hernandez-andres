package sifac.comun.logica;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

import sifac.comun.datos.RepositorioClientesMemoria;
import sifac.comun.datos.RepositorioProductosMemoria;
import sifac.comun.entidades.Documento;
import sifac.comun.entidades.LineaDetalle;
import sifac.comun.entidades.Producto;
import sifac.comun.estructuras.ListaLineas;

/**
 * Pruebas unitarias del ServicioFacturacion y de las estructuras de SIFAC.
 * Incluye las 7 pruebas del Laboratorio 2 y las operaciones avanzadas de ListaLineas (Tarea 1).
 */
public class ServicioFacturacionTest {

    private ServicioFacturacion crearServicio() {
        return new ServicioFacturacion(
            new RepositorioClientesMemoria(),
            new RepositorioProductosMemoria()
        );
    }

    // ===================================================================
    // PRUEBAS DE LA SEMANA 2 (ServicioFacturacion)
    // ===================================================================

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

        // 2. EJECUTAR y 3. VERIFICAR
        servicio.agregarLinea(factura, "PVC-050", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void clienteNoRegistradoLanzaExcepcion() {
        // 1. PREPARAR
        ServicioFacturacion servicio = crearServicio();

        // 2. EJECUTAR y 3. VERIFICAR
        servicio.abrirFactura("9-9999", "CONTADO");
    }

    // ===================================================================
    // METODO AUXILIAR PARA PRUEBAS DE LISTALINEAS (Tarea 1)
    // ===================================================================

    private ListaLineas crearListaPrueba() {
        Producto tuboPVC = new Producto("PVC-050", "Tubo PVC 1/2", 1000, 100);
        Producto cemento = new Producto("CEM-50", "Cemento 50 kg", 8500, 200);
        Producto clavos  = new Producto("CLA-2", "Clavos 2 pulg", 1200, 500);
        Producto pintura = new Producto("PIN-GL", "Pintura galon", 14000, 80);

        ListaLineas lista = new ListaLineas();
        lista.agregarAlFinal(new LineaDetalle(tuboPVC, 10));  // pos 0
        lista.agregarAlFinal(new LineaDetalle(cemento, 2));   // pos 1
        lista.agregarAlFinal(new LineaDetalle(clavos, 5));    // pos 2
        lista.agregarAlFinal(new LineaDetalle(tuboPVC, 4));   // pos 3
        lista.agregarAlFinal(new LineaDetalle(pintura, 1));   // pos 4
        return lista;
    }

    // ===================================================================
    // PRUEBAS DE LISTALINEAS (Operaciones Avanzadas Tarea 1)
    // ===================================================================

    @Test
    public void buscarDevuelveLaLineaCorrectaCuandoExiste() {
        // 1. PREPARAR
        ListaLineas lista = crearListaPrueba();

        // 2. EJECUTAR
        LineaDetalle resultado = lista.buscar("CLA-2");

        // 3. VERIFICAR
        assertNotNull(resultado);
        assertEquals("CLA-2", resultado.getProducto().getCodigo());
        assertEquals(6000, resultado.getImporte(), 0.01);
    }

    @Test
    public void buscarDevuelveNullCuandoNoExiste() {
        // 1. PREPARAR
        ListaLineas lista = crearListaPrueba();

        // 2. EJECUTAR
        LineaDetalle resultado = lista.buscar("TOR-8");

        // 3. VERIFICAR
        assertNull(resultado);
    }

    @Test
    public void buscarConDuplicadoDevuelveLaPrimeraAparicion() {
        // 1. PREPARAR
        ListaLineas lista = crearListaPrueba();

        // 2. EJECUTAR
        LineaDetalle resultado = lista.buscar("PVC-050");

        // 3. VERIFICAR
        assertNotNull(resultado);
        assertEquals(10, resultado.getCantidad());
    }

    @Test
    public void getLineaMayorDevuelveNullConListaVacia() {
        // 1. PREPARAR
        ListaLineas lista = new ListaLineas();

        // 2. EJECUTAR
        LineaDetalle resultado = lista.getLineaMayor();

        // 3. VERIFICAR
        assertNull(resultado);
    }

    @Test
    public void getLineaMayorDevuelveElCementoConImporte17000() {
        // 1. PREPARAR
        ListaLineas lista = crearListaPrueba();

        // 2. EJECUTAR
        LineaDetalle mayor = lista.getLineaMayor();

        // 3. VERIFICAR
        assertNotNull(mayor);
        assertEquals("CEM-50", mayor.getProducto().getCodigo());
        assertEquals(17000, mayor.getImporte(), 0.01);
    }

    @Test
    public void consolidarNoAlteraElSubtotal() {
        // 1. PREPARAR
        ListaLineas lista = crearListaPrueba();
        double subtotalAntes = lista.getSubtotal();

        // 2. EJECUTAR
        lista.consolidar();
        double subtotalDespues = lista.getSubtotal();

        // 3. VERIFICAR
        assertEquals(subtotalAntes, subtotalDespues, 0.01);
        assertEquals(51000.00, subtotalDespues, 0.01);
    }

    @Test
    public void consolidarEliminaUnaLineaYReduceLaCantidad() {
        // 1. PREPARAR
        ListaLineas lista = crearListaPrueba();

        // 2. EJECUTAR
        int eliminadas = lista.consolidar();

        // 3. VERIFICAR
        assertEquals(1, eliminadas);
        assertEquals(4, lista.getCantidad());
    }

    @Test
    public void consolidarFusionaLasCantidadesCorrectamente() {
        // 1. PREPARAR
        ListaLineas lista = crearListaPrueba();

        // 2. EJECUTAR
        lista.consolidar();
        LineaDetalle pvc = lista.buscar("PVC-050");

        // 3. VERIFICAR
        assertNotNull(pvc);
        assertEquals(14, pvc.getCantidad());
        assertEquals(14000, pvc.getImporte(), 0.01);
    }

    @Test
    public void invertirNoAlteraSubtotalNiCantidad() {
        // 1. PREPARAR
        ListaLineas lista = crearListaPrueba();
        double subtotalAntes = lista.getSubtotal();
        int cantidadAntes = lista.getCantidad();

        // 2. EJECUTAR
        lista.invertir();

        // 3. VERIFICAR
        assertEquals(subtotalAntes, lista.getSubtotal(), 0.01);
        assertEquals(cantidadAntes, lista.getCantidad());
    }

    @Test
    public void invertirListaDeUnElementoLaDejaIgual() {
        // 1. PREPARAR
        ListaLineas lista = new ListaLineas();
        Producto tubo = new Producto("PVC-050", "Tubo PVC 1/2", 1000, 100);
        lista.agregarAlFinal(new LineaDetalle(tubo, 5));
        String contenidoAntes = lista.verContenido();

        // 2. EJECUTAR
        lista.invertir();

        // 3. VERIFICAR
        assertEquals(contenidoAntes, lista.verContenido());
        assertEquals(1, lista.getCantidad());
        assertEquals(5000, lista.getSubtotal(), 0.01);
    }

    @Test
    public void invertirCambiaElOrdenCorrectamente() {
        // 1. PREPARAR
        ListaLineas lista = crearListaPrueba();
        lista.consolidar();

        // 2. EJECUTAR
        lista.invertir();

        // 3. VERIFICAR
        String esperado = "[PIN-GL x 1] --> [CLA-2 x 5] --> [CEM-50 x 2] --> [PVC-050 x 14] --> null";
        assertEquals(esperado, lista.verContenido());
    }
}

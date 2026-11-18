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
 * Incluye las 7 pruebas de Semana 2, las operaciones de ListaLineas (Tarea 1)
 * y las pruebas integrales de la Entrega 1 (Proforma, Inventario, Pila, Cola, Historial y Busqueda).
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
        ServicioFacturacion servicio = crearServicio();
        Documento factura = servicio.abrirFactura("1-1111", "CONTADO");
        servicio.agregarLinea(factura, "PVC-050", 10);
        assertEquals(10735.00, factura.calcularTotal(), 0.01);
    }

    @Test
    public void facturaCreditoAplicaImpuestoYRecargo() {
        ServicioFacturacion servicio = crearServicio();
        Documento factura = servicio.abrirFactura("2-2222", "CREDITO");
        servicio.agregarLinea(factura, "PVC-050", 10);
        assertEquals(11526.00, factura.calcularTotal(), 0.01);
    }

    @Test
    public void notaCreditoDevuelveTotalNegativo() {
        ServicioFacturacion servicio = crearServicio();
        Documento nota = servicio.abrirNotaCredito("3-3333");
        servicio.agregarLinea(nota, "PVC-050", 10);
        assertEquals(-11300.00, nota.calcularTotal(), 0.01);
    }

    @Test
    public void cierreDeCajaMixtoSumaPolimorficamente() {
        ServicioFacturacion servicio = crearServicio();
        List<Documento> delDia = new ArrayList<>();

        Documento d1 = servicio.abrirFactura("1-1111", "CONTADO");
        servicio.agregarLinea(d1, "PVC-050", 10);

        Documento d2 = servicio.abrirFactura("2-2222", "CREDITO");
        servicio.agregarLinea(d2, "PVC-050", 10);

        Documento d3 = servicio.abrirNotaCredito("3-3333");
        servicio.agregarLinea(d3, "PVC-050", 10);

        delDia.add(d1);
        delDia.add(d2);
        delDia.add(d3);

        assertEquals(10961.00, servicio.cerrarCaja(delDia), 0.01);
    }

    @Test
    public void documentoSinLineasTieneTotalCero() {
        ServicioFacturacion servicio = crearServicio();
        Documento factura = servicio.abrirFactura("1-1111", "CONTADO");
        assertEquals(0.00, factura.calcularTotal(), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantidadCeroLanzaExcepcion() {
        ServicioFacturacion servicio = crearServicio();
        Documento factura = servicio.abrirFactura("1-1111", "CONTADO");
        servicio.agregarLinea(factura, "PVC-050", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void clienteNoRegistradoLanzaExcepcion() {
        ServicioFacturacion servicio = crearServicio();
        servicio.abrirFactura("9-9999", "CONTADO");
    }

    // ===================================================================
    // PRUEBAS ADICIONALES ENTREGA 1 (Proforma, Inventario, Pila, Cola, Historial, Busqueda)
    // ===================================================================

    @Test
    public void proformaAplicaImpuestoSinDescuentoNiRecargo() {
        ServicioFacturacion servicio = crearServicio();
        Documento proforma = servicio.abrirProforma("1-1111", 15);
        servicio.agregarLinea(proforma, "PVC-050", 10);
        assertEquals(11300.00, proforma.calcularTotal(), 0.01);
    }

    @Test
    public void agregarLineaDescuentaExistenciasYDeshacerLasRepone() {
        ServicioFacturacion servicio = crearServicio();
        Documento factura = servicio.abrirFactura("1-1111", "CONTADO");

        // PVC-050 inicia con 100 unidades
        assertEquals(100, servicio.consultarProducto("PVC-050").getExistencias());

        servicio.agregarLinea(factura, "PVC-050", 15);
        assertEquals(85, servicio.consultarProducto("PVC-050").getExistencias());

        // Al deshacer con PilaOperaciones, vuelve a 100 y el documento queda sin lineas
        assertTrue(servicio.deshacerUltimaLinea(factura));
        assertEquals(100, servicio.consultarProducto("PVC-050").getExistencias());
        assertEquals(0.00, factura.calcularTotal(), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ventaQueSuperaExistenciasEsRechazada() {
        ServicioFacturacion servicio = crearServicio();
        Documento factura = servicio.abrirFactura("1-1111", "CONTADO");
        // PVC-050 tiene 100 en stock; pedir 150 debe lanzar IllegalArgumentException
        servicio.agregarLinea(factura, "PVC-050", 150);
    }

    @Test
    public void cerrarDocumentoActualizaHistorialYEncolaParaEnvio() {
        ServicioFacturacion servicio = crearServicio();
        Documento factura = servicio.abrirFactura("1-1111", "CONTADO");
        assertEquals("ABIERTO", factura.getEstadoActual());

        servicio.agregarLinea(factura, "PVC-050", 5);
        servicio.cerrarDocumento(factura);

        assertEquals("PENDIENTE_ENVIO", factura.getEstadoActual());
        assertEquals(1, servicio.getColaEnvio().tamano());

        Documento enviado = servicio.procesarSiguienteEnvio();
        assertNotNull(enviado);
        assertEquals("ENVIADO", factura.getEstadoActual());
        assertEquals("ABIERTO -> CERRADO -> PENDIENTE_ENVIO -> ENVIADO", factura.verHistorialEstados());
    }

    @Test
    public void ordenamientoPorMontoYBusquedaBinariaPorNumeroFuncionan() {
        ServicioFacturacion servicio = crearServicio();
        List<Documento> delDia = new ArrayList<>();

        Documento d1 = servicio.abrirFactura("1-1111", "CONTADO"); // FC-001
        servicio.agregarLinea(d1, "PVC-050", 20); // mayor monto positivo

        Documento d2 = servicio.abrirNotaCredito("3-3333"); // NC-001
        servicio.agregarLinea(d2, "PVC-050", 5);  // monto negativo

        Documento d3 = servicio.abrirFactura("2-2222", "CREDITO"); // FCR-001
        servicio.agregarLinea(d3, "PVC-050", 10); // monto intermedio

        delDia.add(d1);
        delDia.add(d2);
        delDia.add(d3);

        List<Documento> ordenados = servicio.listarOrdenadosPorMonto(delDia);
        assertEquals("NC-001", ordenados.get(0).getNumero());
        assertEquals("FCR-001", ordenados.get(1).getNumero());
        assertEquals("FC-001", ordenados.get(2).getNumero());

        Documento buscado = servicio.buscarDocumentoPorNumero(delDia, "FCR-001");
        assertNotNull(buscado);
        assertEquals("FCR-001", buscado.getNumero());
        assertNull(servicio.buscarDocumentoPorNumero(delDia, "FC-999"));
    }

    // ===================================================================
    // PRUEBAS DE LA TAREA 1 (ListaLineas)
    // ===================================================================

    private ListaLineas crearListaPrueba() {
        Producto tuboPVC = new Producto("PVC-050", "Tubo PVC 1/2", 1000, 100);
        Producto cemento = new Producto("CEM-50", "Cemento 50 kg", 8500, 200);
        Producto clavos  = new Producto("CLA-2", "Clavos 2 pulg", 1200, 500);
        Producto pintura = new Producto("PIN-GL", "Pintura galon", 14000, 80);

        ListaLineas lista = new ListaLineas();
        lista.agregarAlFinal(new LineaDetalle(tuboPVC, 10));
        lista.agregarAlFinal(new LineaDetalle(cemento, 2));
        lista.agregarAlFinal(new LineaDetalle(clavos, 5));
        lista.agregarAlFinal(new LineaDetalle(tuboPVC, 4));
        lista.agregarAlFinal(new LineaDetalle(pintura, 1));
        return lista;
    }

    @Test
    public void buscarProductoExistenteDevuelvePrimeraAparicion() {
        ListaLineas lista = crearListaPrueba();
        LineaDetalle clavos = lista.buscar("CLA-2");
        assertNotNull(clavos);
        assertEquals(6000, clavos.getImporte(), 0.01);

        LineaDetalle pvc = lista.buscar("PVC-050");
        assertNotNull(pvc);
        assertEquals(10, pvc.getCantidad());
    }

    @Test
    public void buscarProductoInexistenteDevuelveNull() {
        ListaLineas lista = crearListaPrueba();
        assertNull(lista.buscar("TOR-8"));
    }

    @Test
    public void getLineaMayorDevuelveLaDeMayorImporte() {
        ListaLineas lista = crearListaPrueba();
        LineaDetalle mayor = lista.getLineaMayor();
        assertNotNull(mayor);
        assertEquals("CEM-50", mayor.getProducto().getCodigo());
        assertEquals(17000, mayor.getImporte(), 0.01);
    }

    @Test
    public void consolidarUneDuplicadosSinAlterarSubtotal() {
        ListaLineas lista = crearListaPrueba();
        double subtotalAntes = lista.getSubtotal();

        int eliminadas = lista.consolidar();

        assertEquals(1, eliminadas);
        assertEquals(4, lista.getCantidad());
        assertEquals(subtotalAntes, lista.getSubtotal(), 0.01);

        LineaDetalle pvc = lista.buscar("PVC-050");
        assertEquals(14, pvc.getCantidad());
        assertEquals(14000, pvc.getImporte(), 0.01);
    }

    @Test
    public void invertirNoAlteraSubtotalNiCantidad() {
        ListaLineas lista = crearListaPrueba();
        double subtotalAntes = lista.getSubtotal();
        int cantidadAntes = lista.getCantidad();

        lista.invertir();

        assertEquals(subtotalAntes, lista.getSubtotal(), 0.01);
        assertEquals(cantidadAntes, lista.getCantidad());
    }

    @Test
    public void invertirListaDeUnElementoLaDejaIgual() {
        ListaLineas lista = new ListaLineas();
        Producto tubo = new Producto("PVC-050", "Tubo PVC 1/2", 1000, 100);
        lista.agregarAlFinal(new LineaDetalle(tubo, 5));
        String contenidoAntes = lista.verContenido();

        lista.invertir();

        assertEquals(contenidoAntes, lista.verContenido());
        assertEquals(1, lista.getCantidad());
        assertEquals(5000, lista.getSubtotal(), 0.01);
    }

    @Test
    public void invertirCambiaElOrdenCorrectamente() {
        ListaLineas lista = crearListaPrueba();
        lista.consolidar();
        lista.invertir();

        String esperado = "[PIN-GL x 1] --> [CLA-2 x 5] --> [CEM-50 x 2] --> [PVC-050 x 14] --> null";
        assertEquals(esperado, lista.verContenido());
    }
}
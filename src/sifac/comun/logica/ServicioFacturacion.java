package sifac.comun.logica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import sifac.comun.datos.RepositorioClientes;
import sifac.comun.datos.RepositorioProductos;
import sifac.comun.entidades.Cliente;
import sifac.comun.entidades.Documento;
import sifac.comun.entidades.FacturaContado;
import sifac.comun.entidades.FacturaCredito;
import sifac.comun.entidades.LineaDetalle;
import sifac.comun.entidades.NotaCredito;
import sifac.comun.entidades.Producto;
import sifac.comun.entidades.Proforma;
import sifac.comun.estructuras.ColaEnvio;
import sifac.comun.estructuras.PilaOperaciones;

/**
 * Capa logica del sistema SIFAC. Coordina las operaciones de facturacion usando
 * los repositorios inyectados y las estructuras propias.
 * RESTRICCION ESTRICTA: esta clase NO contiene salida a consola (System.out).
 */
public class ServicioFacturacion {

    private final RepositorioClientes repoClientes;
    private final RepositorioProductos repoProductos;
    private int consecutivoContado;
    private int consecutivoCredito;
    private int consecutivoNota;
    private int consecutivoProforma;

    // Estructuras propias (Semana 5)
    private final PilaOperaciones pilaDeshacer;
    private final ColaEnvio colaEnvio;
    private Documento ultimoDocumentoModificado;

    // Hilo consumidor en segundo plano (Semanas 8, 9 y 10)
    private Thread hiloEnvio;
    private volatile boolean procesandoEnvio;
    private int documentosEnviados;

    public ServicioFacturacion(RepositorioClientes repoClientes, RepositorioProductos repoProductos) {
        this.repoClientes = repoClientes;
        this.repoProductos = repoProductos;
        this.consecutivoContado = 0;
        this.consecutivoCredito = 0;
        this.consecutivoNota = 0;
        this.consecutivoProforma = 0;
        this.pilaDeshacer = new PilaOperaciones();
        this.colaEnvio = new ColaEnvio();
        this.procesandoEnvio = false;
        this.documentosEnviados = 0;
    }

    public Documento abrirFactura(String cedula, String forma) {
        Cliente cliente = validarCliente(cedula);
        if ("CREDITO".equalsIgnoreCase(forma)) {
            consecutivoCredito++;
            String numero = String.format("FCR-%03d", consecutivoCredito);
            return new FacturaCredito(numero, cliente);
        } else {
            consecutivoContado++;
            String numero = String.format("FC-%03d", consecutivoContado);
            return new FacturaContado(numero, cliente);
        }
    }

    public Documento abrirNotaCredito(String cedula) {
        Cliente cliente = validarCliente(cedula);
        consecutivoNota++;
        String numero = String.format("NC-%03d", consecutivoNota);
        return new NotaCredito(numero, cliente);
    }

    public Documento abrirProforma(String cedula, int diasVigencia) {
        if (diasVigencia <= 0) {
            throw new IllegalArgumentException("Los dias de vigencia deben ser mayores que cero");
        }
        Cliente cliente = validarCliente(cedula);
        consecutivoProforma++;
        String numero = String.format("PRO-%03d", consecutivoProforma);
        return new Proforma(numero, cliente, diasVigencia);
    }

    private Cliente validarCliente(String cedula) {
        Cliente cliente = repoClientes.buscarPorCedula(cedula);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no registrado");
        }
        return cliente;
    }

    /**
     * Agrega una línea validando cantidad > 0, existencia del producto y stock
     * suficiente en inventario, y apila la línea en PilaOperaciones (Semanas 2, 5 y 9).
     */
    public void agregarLinea(Documento d, String codigo, int cantidad) {
        if (d == null) {
            throw new IllegalArgumentException("No hay documento abierto");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad invalida");
        }
        Producto producto = repoProductos.buscarPorCodigo(codigo);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        // Descuento sincronizado de inventario (rechaza si no alcanza)
        producto.descontarExistencias(cantidad);

        d.agregarLinea(producto, cantidad);
        LineaDetalle recienAgregada = d.getUltimaLinea();
        pilaDeshacer.apilar(recienAgregada);
        ultimoDocumentoModificado = d;
    }

    /**
     * Deshace la última línea agregada al documento indicado usando PilaOperaciones
     * y repone las existencias en el inventario.
     */
    public boolean deshacerUltimaLinea(Documento d) {
        if (d == null || pilaDeshacer.estaVacia()) {
            return false;
        }
        LineaDetalle desapilada = pilaDeshacer.desapilar();
        if (desapilada == null) {
            return false;
        }
        boolean eliminada = d.eliminarUltimaLinea();
        if (eliminada && desapilada.getProducto() != null) {
            desapilada.getProducto().reponerExistencias(desapilada.getCantidad());
        }
        return eliminada;
    }

    /**
     * Sobrecarga sin parámetros para mantener compatibilidad con llamadas previas.
     */
    public boolean deshacerUltimaLinea() {
        return deshacerUltimaLinea(ultimoDocumentoModificado);
    }

    /**
     * Cierra el documento, registra los estados CERRADO y PENDIENTE_ENVIO en su
     * historial de nodos y lo encola en ColaEnvio (Semanas 3, 5 y 10).
     */
    public void cerrarDocumento(Documento d) {
        if (d == null) {
            throw new IllegalArgumentException("No hay documento abierto para cerrar");
        }
        d.cambiarEstado("CERRADO");
        d.cambiarEstado("PENDIENTE_ENVIO");
        pilaDeshacer.limpiar();
        colaEnvio.encolar(d);
    }

    /**
     * Procesa manualmente el siguiente documento pendiente en la cola de envío.
     */
    public synchronized Documento procesarSiguienteEnvio() {
        Documento d = colaEnvio.desencolar();
        if (d != null) {
            d.cambiarEstado("ENVIADO");
            documentosEnviados++;
        }
        return d;
    }

    /**
     * Inicia el hilo consumidor en segundo plano que vacía ColaEnvio mientras
     * la caja sigue operando (Semanas 8, 9 y 10).
     */
    public synchronized void iniciarProcesadorEnvioSegundoPlano() {
        if (procesandoEnvio) {
            return;
        }
        procesandoEnvio = true;
        hiloEnvio = new Thread(() -> {
            while (procesandoEnvio) {
                try {
                    Documento doc = colaEnvio.esperarYDesencolar();
                    if (doc != null) {
                        Thread.sleep(400); // Simula tiempo de despacho en segundo plano
                        doc.cambiarEstado("ENVIADO");
                        synchronized (ServicioFacturacion.this) {
                            documentosEnviados++;
                        }
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "consumidor-cola-envio");
        hiloEnvio.setDaemon(true);
        hiloEnvio.start();
    }

    public synchronized void detenerProcesadorEnvio() {
        procesandoEnvio = false;
        if (hiloEnvio != null) {
            hiloEnvio.interrupt();
        }
    }

    /**
     * Cierre de caja polimórfico (Semana 1 y 2): suma calcularTotal() sin condicionales.
     */
    public double cerrarCaja(List<Documento> delDia) {
        double total = 0;
        for (Documento d : delDia) {
            total += d.calcularTotal();
        }
        return total;
    }

    /**
     * Devuelve una nueva lista con los documentos del día ordenados de menor a mayor
     * monto total usando Collections.sort y Comparator (Semana 6).
     */
    public List<Documento> listarOrdenadosPorMonto(List<Documento> delDia) {
        List<Documento> copia = new ArrayList<>(delDia);
        Collections.sort(copia, Comparator.comparingDouble(Documento::calcularTotal));
        return copia;
    }

    /**
     * Busca un documento por su número usando orden natural (Comparable)
     * y Collections.binarySearch (Semana 6).
     */
    public Documento buscarDocumentoPorNumero(List<Documento> delDia, String numero) {
        if (delDia == null || delDia.isEmpty() || numero == null) {
            return null;
        }
        List<Documento> ordenada = new ArrayList<>(delDia);
        Collections.sort(ordenada); // Precondición obligatoria de binarySearch
        Documento clave = new FacturaContado(numero.trim().toUpperCase(), new Cliente("0", "CLAVE"));
        int indice = Collections.binarySearch(ordenada, clave);
        if (indice >= 0) {
            return ordenada.get(indice);
        }
        return null;
    }

    public Producto consultarProducto(String codigo) {
        return repoProductos.buscarPorCodigo(codigo);
    }

    public ColaEnvio getColaEnvio() {
        return colaEnvio;
    }

    public PilaOperaciones getPilaDeshacer() {
        return pilaDeshacer;
    }

    public synchronized int getDocumentosEnviados() {
        return documentosEnviados;
    }
}
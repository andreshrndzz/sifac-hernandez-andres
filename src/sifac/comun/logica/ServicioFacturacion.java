package sifac.comun.logica;

import java.util.List;
import sifac.comun.datos.RepositorioClientes;
import sifac.comun.datos.RepositorioProductos;
import sifac.comun.entidades.Cliente;
import sifac.comun.entidades.Documento;
import sifac.comun.entidades.FacturaContado;
import sifac.comun.entidades.FacturaCredito;
import sifac.comun.entidades.NotaCredito;
import sifac.comun.entidades.Producto;

/**
 * Capa logica del sistema SIFAC.
 * Coordina las operaciones de facturacion usando los repositorios inyectados.
 * RESTRICCION: esta clase NO contiene salida a consola.
 */
public class ServicioFacturacion {

    private final RepositorioClientes repoClientes;
    private final RepositorioProductos repoProductos;
    private int consecutivoContado;
    private int consecutivoCredito;
    private int consecutivoNota;

    /**
     * Inyeccion por constructor: recibe los repositorios desde afuera.
     *
     * @param repoClientes repositorio de clientes
     * @param repoProductos repositorio de productos
     */
    public ServicioFacturacion(RepositorioClientes repoClientes, RepositorioProductos repoProductos) {
        this.repoClientes = repoClientes;
        this.repoProductos = repoProductos;
        this.consecutivoContado = 0;
        this.consecutivoCredito = 0;
        this.consecutivoNota = 0;
    }

    /**
     * Abre una factura de contado o credito segun la forma de pago.
     * Busca el cliente por cedula y genera un numero correlativo por tipo.
     *
     * @param cedula cedula del cliente
     * @param forma "CONTADO" o "CREDITO"
     * @return el documento creado
     * @throws IllegalArgumentException si el cliente no existe
     */
    public Documento abrirFactura(String cedula, String forma) {
        Cliente cliente = repoClientes.buscarPorCedula(cedula);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no registrado");
        }

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

    /**
     * Abre una nota de credito para el cliente indicado.
     *
     * @param cedula cedula del cliente
     * @return la nota de credito creada
     * @throws IllegalArgumentException si el cliente no existe
     */
    public Documento abrirNotaCredito(String cedula) {
        Cliente cliente = repoClientes.buscarPorCedula(cedula);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no registrado");
        }
        consecutivoNota++;
        String numero = String.format("NC-%03d", consecutivoNota);
        return new NotaCredito(numero, cliente);
    }

    /**
     * Agrega una linea de detalle al documento.
     * Valida que la cantidad sea mayor que cero (regla de negocio).
     *
     * @param d documento al que se agrega la linea
     * @param codigo codigo del producto
     * @param cantidad cantidad a agregar
     * @throws IllegalArgumentException si la cantidad es menor o igual a cero
     */
    public void agregarLinea(Documento d, String codigo, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad invalida");
        }
        Producto producto = repoProductos.buscarPorCodigo(codigo);
        d.agregarLinea(producto, cantidad);
    }

    /**
     * Calcula el cierre de caja sumando polimorficamente los totales.
     *
     * @param delDia lista de documentos del dia
     * @return la suma de los totales
     */
    public double cerrarCaja(List<Documento> delDia) {
        double total = 0;
        for (Documento d : delDia) {
            total += d.calcularTotal();
        }
        return total;
    }
}

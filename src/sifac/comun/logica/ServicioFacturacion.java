package sifac.comun.logica;

import java.util.List;
import java.util.Stack; // <-- IMPORTANTE: Esto es necesario para usar la Pila
import sifac.comun.datos.RepositorioClientes;
import sifac.comun.datos.RepositorioProductos;
import sifac.comun.entidades.Cliente;
import sifac.comun.entidades.Documento;
import sifac.comun.entidades.FacturaContado;
import sifac.comun.entidades.FacturaCredito;
import sifac.comun.entidades.NotaCredito;
import sifac.comun.entidades.Producto;

/**
 * Capa logica del sistema SIFAC. Coordina las operaciones de facturacion usando
 * los repositorios inyectados. RESTRICCION: esta clase NO contiene salida a
 * consola.
 */
public class ServicioFacturacion {

    private final RepositorioClientes repoClientes;
    private final RepositorioProductos repoProductos;
    private int consecutivoContado;
    private int consecutivoCredito;
    private int consecutivoNota;
    
    // NUEVO ATRIBUTO: La Pila de Deshacer (LIFO) de la Parte 1
    private Stack<Documento> pilaDeshacer;

    /**
     * Inyeccion por constructor: recibe los repositorios desde afuera.
     */
    public ServicioFacturacion(RepositorioClientes repoClientes, RepositorioProductos repoProductos) {
        this.repoClientes = repoClientes;
        this.repoProductos = repoProductos;
        this.consecutivoContado = 0;
        this.consecutivoCredito = 0;
        this.consecutivoNota = 0;
        this.pilaDeshacer = new Stack<>(); // <-- Inicializamos la Pila
    }

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
     * Además, APILA la acción para poder deshacerla (Parte 1 del lab).
     */
    public void agregarLinea(Documento d, String codigo, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad invalida");
        }
        Producto producto = repoProductos.buscarPorCodigo(codigo);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        d.agregarLinea(producto, cantidad);
        
        // CADA VEZ QUE AGREGAMOS, APILAMOS EL DOCUMENTO EN LA CIMA
        pilaDeshacer.push(d);
    }

    /**
     * Retira la última línea del último documento modificado.
     * Si la pila está vacía, devuelve false.
     */
    public boolean deshacerUltimaLinea() {
        if (pilaDeshacer.isEmpty()) {
            return false;
        }
        // Desapilamos (LIFO - Last In, First Out)
        Documento d = pilaDeshacer.pop();
        return d.eliminarUltimaLinea();
    }

    public double cerrarCaja(List<Documento> delDia) {
        double total = 0;
        for (Documento d : delDia) {
            total += d.calcularTotal();
        }
        return total;
    }
}
package sifac.comun.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Relación Documento - LineaDetalle: COMPOSICIÓN.
 * Justificación: Una línea de detalle no significa nada sin su documento.
 * El documento crea las líneas y, si este se elimina, sus partes mueren con él.
 */

/**
 * Clase abstracta que representa un documento comercial de la ferretería.
 * Define la estructura común y delega el cálculo del total a las subclases.
 */
public abstract class Documento {

    protected String numero;
    protected LocalDate fecha;
    protected Cliente cliente;
    private List<LineaDetalle> lineas;

    /**
     * Constructor que recibe el número y el cliente.
     * La fecha se fija automáticamente con la fecha del día.
     *
     * @param numero número del documento
     * @param cliente cliente asociado al documento
     */
    public Documento(String numero, Cliente cliente) {
        this.numero = numero;
        this.cliente = cliente;
        this.fecha = LocalDate.now();
        this.lineas = new ArrayList<>();
    }

    /**
     * Agrega una línea de detalle al documento.
     * La línea se crea internamente (composición): el documento construye sus propias partes.
     *
     * @param p producto a agregar
     * @param cantidad cantidad del producto
     */
    public void agregarLinea(Producto p, int cantidad) {
        lineas.add(new LineaDetalle(p, cantidad));
    }

    /**
     * Devuelve una vista no modificable de la lista de líneas de detalle.
     * Protege la composición: nadie desde fuera puede agregar ni eliminar líneas directamente.
     *
     * @return lista no modificable de líneas de detalle
     */
    public List<LineaDetalle> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    /**
     * Calcula el subtotal sumando el importe de cada línea de detalle.
     *
     * @return el subtotal del documento
     */
    public double getSubtotal() {
        double subtotal = 0;
        for (LineaDetalle linea : lineas) {
            subtotal += linea.getImporte();
        }
        return subtotal;
    }

    /**
     * Devuelve una descripción del documento con número, fecha y nombre del cliente.
     *
     * @return descripción del documento
     */
    public String describir() {
        return numero + " | " + fecha + " | " + cliente.getNombre();
    }

    /**
     * Método abstracto que cada tipo de documento implementa con su propia fórmula.
     *
     * @return el total calculado según el tipo de documento
     */
    public abstract double calcularTotal();
}

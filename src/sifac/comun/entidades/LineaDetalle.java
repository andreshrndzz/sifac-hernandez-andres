package sifac.comun.entidades;

/**
 * Clase de apoyo que representa una línea de detalle dentro de un documento.
 * Contiene un producto y una cantidad, y calcula el importe como precio × cantidad.
 */
public class LineaDetalle {

    private Producto producto;
    private int cantidad;

    public LineaDetalle(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    /**
     * Devuelve el importe de la línea: precio unitario × cantidad.
     *
     * @return el importe calculado
     */
    public double getImporte() {
        return producto.getPrecioUnitario() * cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }
}

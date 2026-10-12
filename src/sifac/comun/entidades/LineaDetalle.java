package sifac.comun.entidades;

/**
 * Clase de apoyo que representa una linea de detalle dentro de un documento.
 * Contiene un producto y una cantidad, y calcula el importe como precio x cantidad.
 */
public class LineaDetalle {

    private Producto producto;
    private int cantidad;

    public LineaDetalle(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    /**
     * Devuelve el importe de la linea: precio unitario x cantidad.
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

    /**
     * Permite modificar la cantidad (necesario para consolidar lineas repetidas).
     *
     * @param cantidad la nueva cantidad
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}

// IA: le pedi la estructura de la entidad TipoCambio con sus getters y toString
// y ajuste el metodo diferencial con redondeo para evitar errores de punto flotante.
package sifac.comun.entidades;

import java.util.Locale;

public class TipoCambio {
    private String fecha;
    private double compra;
    private double venta;

    public TipoCambio(String fecha, double compra, double venta) {
        this.fecha = fecha;
        this.compra = compra;
        this.venta = venta;
    }

    public String getFecha() {
        return fecha;
    }

    public double getCompra() {
        return compra;
    }

    public double getVenta() {
        return venta;
    }

    public double diferencial() {
        return Math.round((venta - compra) * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s compra %.2f venta %.2f", fecha, compra, venta);
    }
}
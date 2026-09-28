package sifac.comun.entidades;

/**
 * Factura de contado: premia el pago inmediato con un descuento del 5%.
 * Total = (subtotal + 13% impuesto) − 5% de descuento.
 */
public class FacturaContado extends Documento {

    private static final double IMPUESTO = 0.13;
    private static final double DESCUENTO = 0.05;

    public FacturaContado(String numero, Cliente cliente) {
        super(numero, cliente);
    }

    @Override
    public double calcularTotal() {
        double subtotalConImpuesto = getSubtotal() * (1 + IMPUESTO);
        return subtotalConImpuesto * (1 - DESCUENTO);
    }
}

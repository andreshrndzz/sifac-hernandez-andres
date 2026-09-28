package sifac.comun.entidades;

/**
 * Factura a crédito: financiar a 30 días tiene un costo del 2% de recargo.
 * Total = (subtotal + 13% impuesto) + 2% de recargo.
 */
public class FacturaCredito extends Documento {

    private static final double IMPUESTO = 0.13;
    private static final double RECARGO = 0.02;

    public FacturaCredito(String numero, Cliente cliente) {
        super(numero, cliente);
    }

    @Override
    public double calcularTotal() {
        double subtotalConImpuesto = getSubtotal() * (1 + IMPUESTO);
        return subtotalConImpuesto * (1 + RECARGO);
    }
}

package sifac.comun.entidades;

/**
 * Nota de crédito: representa una devolución que resta del cierre de caja.
 * Total = −(subtotal + 13% impuesto).
 */
public class NotaCredito extends Documento {

    private static final double IMPUESTO = 0.13;

    public NotaCredito(String numero, Cliente cliente) {
        super(numero, cliente);
    }

    @Override
    public double calcularTotal() {
        return -(getSubtotal() * (1 + IMPUESTO));
    }
}

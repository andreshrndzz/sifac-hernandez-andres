package sifac.comun.entidades;

/**
 * Proforma: cotización con impuesto del 13%, sin descuento ni recargo.
 * Incluye un atributo adicional de días de vigencia.
 * Total = subtotal + 13% impuesto.
 */
public class Proforma extends Documento {

    private static final double IMPUESTO = 0.13;

    private int diasVigencia;

    public Proforma(String numero, Cliente cliente, int diasVigencia) {
        super(numero, cliente);
        this.diasVigencia = diasVigencia;
    }

    @Override
    public double calcularTotal() {
        return getSubtotal() * (1 + IMPUESTO);
    }

    public int getDiasVigencia() {
        return diasVigencia;
    }

    public void setDiasVigencia(int diasVigencia) {
        this.diasVigencia = diasVigencia;
    }
}

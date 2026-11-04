// IA: le pedi la estructura del metodo convertir y la ajuste
// porque usaba el tipo de cambio de compra y debe ser el de venta.
package sifac.servidor.logica;

import sifac.comun.entidades.TipoCambio;
import sifac.servidor.datos.RepositorioTipoCambio;

public class ServicioTipoCambio {
    private final RepositorioTipoCambio repo;

    public ServicioTipoCambio(RepositorioTipoCambio repo) {
        this.repo = repo;
    }

    public double convertirAColonesParaPagar(double usd) {
        TipoCambio actual = repo.obtenerMasReciente();
        return Math.round((usd * actual.getVenta()) * 100.0) / 100.0;
    }

    public double convertirAColonesParaRecibir(double usd) {
        TipoCambio actual = repo.obtenerMasReciente();
        return Math.round((usd * actual.getCompra()) * 100.0) / 100.0;
    }

    public double convertirADolaresParaPagar(double crc) {
        TipoCambio actual = repo.obtenerMasReciente();
        return Math.round((crc / actual.getVenta()) * 100.0) / 100.0;
    }

    public TipoCambio delDia(String fecha) {
        TipoCambio tc = repo.obtenerPorFecha(fecha);
        if (tc == null) {
            throw new IllegalArgumentException("No hay tipo de cambio para " + fecha);
        }
        return tc;
    }

    public double diferencialPorCada100() {
        TipoCambio actual = repo.obtenerMasReciente();
        return Math.round((actual.diferencial() * 100.0) * 100.0) / 100.0;
    }

    public TipoCambio obtenerActual() {
        return repo.obtenerMasReciente();
    }
}
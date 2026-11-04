// IA: le pedi la implementacion en memoria de RepositorioTipoCambio con ArrayList
// y verifique que obtenerPorFecha devuelva null en fines de semana sin lanzar excepcion.
package sifac.servidor.datos;

import java.util.ArrayList;
import java.util.List;
import sifac.comun.entidades.TipoCambio;

public class TipoCambioEnMemoria implements RepositorioTipoCambio {
    private final List<TipoCambio> datos;

    public TipoCambioEnMemoria() {
        this.datos = new ArrayList<>();
        datos.add(new TipoCambio("2026-10-30", 504.80, 511.90));
        datos.add(new TipoCambio("2026-11-02", 505.50, 512.75));
        datos.add(new TipoCambio("2026-11-03", 506.25, 513.40));
    }

    @Override
    public TipoCambio obtenerPorFecha(String fecha) {
        for (TipoCambio tc : datos) {
            if (tc.getFecha().equals(fecha)) {
                return tc;
            }
        }
        return null;
    }

    @Override
    public TipoCambio obtenerMasReciente() {
        if (datos.isEmpty()) {
            return null;
        }
        return datos.get(datos.size() - 1);
    }
}
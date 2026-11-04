// IA: le pedi la definicion de la interfaz para el repositorio de tipo de cambio
// y mantuve unicamente las dos firmas de metodos del contrato sin implementaciones.
package sifac.servidor.datos;

import sifac.comun.entidades.TipoCambio;

public interface RepositorioTipoCambio {
    /** Devuelve el tipo de cambio de esa fecha, o null si no lo tiene. */
    TipoCambio obtenerPorFecha(String fecha);

    /** Devuelve el mas reciente que tenga disponible. */
    TipoCambio obtenerMasReciente();
}
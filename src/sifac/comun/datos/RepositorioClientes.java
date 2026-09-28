package sifac.comun.datos;

import sifac.comun.entidades.Cliente;

/**
 * Interfaz de la capa de datos para obtener clientes.
 * Permite cambiar el origen de datos sin modificar la capa logica.
 */
public interface RepositorioClientes {

    /**
     * Busca un cliente por su cedula.
     *
     * @param cedula la cedula del cliente
     * @return el cliente encontrado, o null si no existe
     */
    Cliente buscarPorCedula(String cedula);
}

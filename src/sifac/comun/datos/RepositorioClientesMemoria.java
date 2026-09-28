package sifac.comun.datos;

import java.util.HashMap;
import java.util.Map;
import sifac.comun.entidades.Cliente;

/**
 * Implementacion en memoria del repositorio de clientes.
 * Los datos se inicializan en el constructor con clientes de prueba.
 */
public class RepositorioClientesMemoria implements RepositorioClientes {

    private final Map<String, Cliente> clientes;

    public RepositorioClientesMemoria() {
        clientes = new HashMap<>();
        clientes.put("1-1111", new Cliente("1-1111", "Rafael Mora"));
        clientes.put("2-2222", new Cliente("2-2222", "Constructora XYZ"));
        clientes.put("3-3333", new Cliente("3-3333", "Dona Marta"));
    }

    @Override
    public Cliente buscarPorCedula(String cedula) {
        return clientes.get(cedula);
    }
}

package sifac.comun.datos;

import sifac.comun.entidades.Producto;

/**
 * Interfaz de la capa de datos para obtener productos.
 * Permite cambiar el origen de datos sin modificar la capa logica.
 */
public interface RepositorioProductos {

    /**
     * Busca un producto por su codigo.
     *
     * @param codigo el codigo del producto
     * @return el producto encontrado, o null si no existe
     */
    Producto buscarPorCodigo(String codigo);
}

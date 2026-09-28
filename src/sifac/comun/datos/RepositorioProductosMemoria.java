package sifac.comun.datos;

import java.util.HashMap;
import java.util.Map;
import sifac.comun.entidades.Producto;

/**
 * Implementacion en memoria del repositorio de productos.
 * Los datos se inicializan en el constructor con productos de ferreteria.
 */
public class RepositorioProductosMemoria implements RepositorioProductos {

    private final Map<String, Producto> productos;

    public RepositorioProductosMemoria() {
        productos = new HashMap<>();
        productos.put("PVC-050", new Producto("PVC-050", "Tubo PVC 1/2", 1000, 100));
        productos.put("CEM-01",  new Producto("CEM-01",  "Cemento gris 50kg", 7500, 200));
        productos.put("CLV-02",  new Producto("CLV-02",  "Clavos 2 pulgadas (1kg)", 1500, 500));
        productos.put("PNT-03",  new Producto("PNT-03",  "Pintura latex blanca 1gal", 12000, 80));
        productos.put("TRN-04",  new Producto("TRN-04",  "Tornillos galvanizados (100u)", 3500, 300));
    }

    @Override
    public Producto buscarPorCodigo(String codigo) {
        return productos.get(codigo);
    }
}

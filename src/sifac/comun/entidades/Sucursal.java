package sifac.comun.entidades;

import java.util.ArrayList;
import java.util.List;

/*
 * Relación Sucursal - Cajero: AGREGACIÓN.
 * Justificación: La sucursal agrupa partes que sobreviven sin ella.
 * Si la sucursal cierra, los cajeros mantienen su existencia independiente.
 */

/**
 * Clase que representa una sucursal de la ferretería.
 * Agrupa cajeros mediante agregación: los recibe ya construidos desde el exterior.
 */
public class Sucursal {

    private String nombre;
    private String canton;
    private List<Cajero> cajeros;

    public Sucursal(String nombre, String canton) {
        this.nombre = nombre;
        this.canton = canton;
        this.cajeros = new ArrayList<>();
    }

    /**
     * Asigna un cajero ya construido a la sucursal (agregación).
     * El cajero se recibe desde el exterior, no se construye aquí.
     *
     * @param c cajero a asignar
     */
    public void asignar(Cajero c) {
        cajeros.add(c);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton = canton;
    }

    public List<Cajero> getCajeros() {
        return cajeros;
    }
}

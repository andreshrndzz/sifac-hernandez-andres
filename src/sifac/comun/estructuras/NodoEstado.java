package sifac.comun.estructuras;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase autoreferenciada que representa un cambio de estado en el ciclo de vida
 * de un documento comercial (Semana 3).
 */
public class NodoEstado {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String estado;
    private LocalDateTime momento;
    private NodoEstado siguiente; // <- la autoreferencia

    public NodoEstado(String estado) {
        this.estado = estado;
        this.momento = LocalDateTime.now();
        this.siguiente = null;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getMomento() {
        return momento;
    }

    public NodoEstado getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoEstado siguiente) {
        this.siguiente = siguiente;
    }

    @Override
    public String toString() {
        return "[" + estado + " @ " + momento.format(FORMATO) + "]";
    }
}
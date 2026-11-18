package sifac.comun.entidades;

/**
 * Clase de apoyo que representa un producto de la ferretería. Incluye control
 * sincronizado de existencias para acceso concurrente (Semana 9).
 */
public class Producto {

    private String codigo;
    private String descripcion;
    private double precioUnitario;
    private int existencias;

    public Producto(String codigo, String descripcion, double precioUnitario, int existencias) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.existencias = existencias;
    }

    /**
     * Sección crítica sincronizada (Semana 9): verifica y descuenta existencias
     * de forma atómica evitando condiciones de carrera entre hilos.
     */
    public synchronized void descontarExistencias(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad invalida");
        }
        if (cantidad > existencias) {
            throw new IllegalArgumentException("Existencias insuficientes para " + codigo
                    + " (disponibles: " + existencias + ", solicitadas: " + cantidad + ")");
        }
        existencias -= cantidad;
    }

    /**
     * Repone existencias de forma sincronizada (por ejemplo, al deshacer una
     * línea).
     */
    public synchronized void reponerExistencias(int cantidad) {
        if (cantidad > 0) {
            existencias += cantidad;
        }
    }

    /**
     * Método deliberadamente NO sincronizado para demostrar y documentar la
     * condición de carrera exigida en la Semana 9.
     */
    public boolean descontarSinSincronizar(int cantidad) {
        if (existencias >= cantidad) {
            try {
                Thread.sleep(2); // Amplifica la ventana entre lectura y escritura
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            existencias -= cantidad;
            return true;
        }
        return false;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public synchronized int getExistencias() {
        return existencias;
    }

    public synchronized void setExistencias(int existencias) {
        this.existencias = existencias;
    }
}

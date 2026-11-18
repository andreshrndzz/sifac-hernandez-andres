package sifac.comun.datos;

public class ServicioPadronException extends RuntimeException {

    public ServicioPadronException(String mensaje) {
        super(mensaje);
    }

    public ServicioPadronException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
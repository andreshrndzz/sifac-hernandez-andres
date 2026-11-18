package sifac.comun.datos;

import sifac.comun.entidades.Cliente;

public class ProtocoloPadron {

    public static void validarCedula(String cedula) {
        if (cedula == null) {
            throw new IllegalArgumentException("La cedula no puede ser nula");
        }
        if (cedula.contains("\n") || cedula.contains("\r")) {
            throw new IllegalArgumentException("La cedula no puede contener saltos de linea");
        }
        String limpia = cedula.trim();
        if (!limpia.matches("[0-9]{9}")) {
            throw new IllegalArgumentException("Use nueve digitos");
        }
    }

    public static Cliente interpretar(String respuesta, String cedulaSolicitada) {
        if (respuesta == null) {
            throw new ServicioPadronException("El servidor cerro la conexion sin enviar respuesta (fin de flujo)");
        }
        String linea = respuesta.trim();
        if ("NOTFOUND".equals(linea)) {
            return null;
        }
        String[] campos = respuesta.split("\\|", -1);
        if (campos.length != 5) {
            throw new ServicioPadronException("Respuesta invalida del padron: se esperaban 5 campos y llegaron " + campos.length);
        }
        if (!"OK".equals(campos[0].trim())) {
            throw new ServicioPadronException("Estado no reconocido en protocolo: " + campos[0]);
        }
        String cedulaRecibida = campos[1].trim();
        String nombreRecibido = campos[2].trim();
        if (!cedulaSolicitada.trim().equals(cedulaRecibida)) {
            throw new ServicioPadronException("La cedula recibida (" + cedulaRecibida + ") no coincide con la solicitada (" + cedulaSolicitada + ")");
        }
        if (nombreRecibido.isEmpty()) {
            throw new ServicioPadronException("El nombre del cliente llego vacio desde el padron");
        }
        return new Cliente(cedulaRecibida, nombreRecibido);
    }
}
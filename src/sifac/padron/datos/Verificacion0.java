package sifac.padron.datos;

import sifac.padron.entidades.Elector;

public class Verificacion0 {
    public static void main(String[] args) throws Exception {
        RepositorioPadron repo = new RepositorioPadron();
        repo.cargar("PADRON_SINTETICO.txt", "Distelec.txt");
        System.out.println("padron cargado: " + repo.cantidad() + " electores");

        String[] cedulas = {
            "104560789",
            "112340567",
            "203450678",
            "305670123",
            "704560890",
            "999999999"
        };

        for (String c : cedulas) {
            Elector e = repo.buscarBinaria(c);
            if (e == null) {
                System.out.println(c + " -> NOTFOUND");
            } else {
                System.out.println(c + " -> " + e.getNombreCompleto() + " | " + repo.ubicacionDe(e));
            }
        }
    }
}
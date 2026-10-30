package sifac.padron.cliente;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import sifac.padron.datos.RepositorioPadron;

public class GeneradorCedulas {

    public static final String ARCHIVO_SALIDA = "cedulas.txt";
    public static final int CANTIDAD_EXISTENTES = 150;
    public static final int CANTIDAD_INEXISTENTES = 50;

    public static void main(String[] args) throws Exception {
        RepositorioPadron repo = new RepositorioPadron();
        repo.cargar("PADRON_SINTETICO.txt", "Distelec.txt");

        List<String> lista = new ArrayList<>();

        // 1. Obtener 150 cedulas que SI existen en el padron
        List<String> existentes = repo.obtenerMuestraCedulasExistentes(CANTIDAD_EXISTENTES);
        lista.addAll(existentes);

        // 2. Generar 50 cedulas que NO existen en el padron (prefijo 99999xxxx)
        for (int i = 1; i <= CANTIDAD_INEXISTENTES; i++) {
            String inexistente = String.format("99999%04d", i);
            lista.add(inexistente);
        }

        // 3. Mezclar con semilla fija para que el reparto sea uniforme entre hilos
        Collections.shuffle(lista, new Random(42));

        // 4. Escribir el archivo cedulas.txt en la raiz del proyecto
        try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO_SALIDA))) {
            for (String c : lista) {
                out.println(c);
            }
        }

        System.out.println("Archivo " + ARCHIVO_SALIDA + " generado con exito.");
        System.out.println("Total escritas: " + lista.size()
                + " (" + CANTIDAD_EXISTENTES + " existentes y "
                + CANTIDAD_INEXISTENTES + " inexistentes).");
    }
}

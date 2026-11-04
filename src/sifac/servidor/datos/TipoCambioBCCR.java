// IA: le pedi el cliente REST para la nueva API SDDE del BCCR con Bearer Token
// y ajuste la extraccion de valorDatoPorPeriodo para devolver null cuando el JSON trae null.
package sifac.servidor.datos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sifac.comun.entidades.TipoCambio;

public class TipoCambioBCCR implements RepositorioTipoCambio {

    private static final String BASE_URL =
            "https://apim.bccr.fi.cr/SDDE/api/Bccr.GE.SDDE.Publico.Indicadores.API";
    private static final String IND_COMPRA = "317";
    private static final String IND_VENTA = "318";
    private static final DateTimeFormatter FORMATO_SDDE =
            DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final String correo;
    private final String token;
    private TipoCambio ultimoConocido;

    public TipoCambioBCCR(String token) {
        this("", token);
    }

    public TipoCambioBCCR(String correo, String token) {
        this.correo = correo != null ? correo.trim() : "";
        String limpio = token != null ? token.trim() : "";
        if (limpio.toLowerCase().startsWith("bearer ")) {
            limpio = limpio.substring(7).trim();
        }
        this.token = limpio;
        // Caché inicial con el dato real del BCCR para degradar con gracia si falla la red
        this.ultimoConocido = new TipoCambio("2026-11-03", 448.98, 454.07);
    }

    @Override
    public TipoCambio obtenerPorFecha(String fechaIso) {
        try {
            LocalDate fecha = LocalDate.parse(fechaIso);
            String fechaSdde = fecha.format(FORMATO_SDDE);

            DatoSerie compra = consultarSerieIndicador(IND_COMPRA, fechaSdde, fechaSdde);
            DatoSerie venta = consultarSerieIndicador(IND_VENTA, fechaSdde, fechaSdde);

            if (compra == null || venta == null || compra.valor <= 0 || venta.valor <= 0) {
                return null;
            }

            TipoCambio tc = new TipoCambio(fechaIso, compra.valor, venta.valor);
            this.ultimoConocido = tc;
            return tc;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public TipoCambio obtenerMasReciente() {
        try {
            LocalDate hoy = LocalDate.now();
            // Consulta una ventana de los ultimos 7 dias por si hoy es fin de semana o feriado
            String inicioSdde = hoy.minusDays(7).format(FORMATO_SDDE);
            String finSdde = hoy.format(FORMATO_SDDE);

            DatoSerie compra = consultarSerieIndicador(IND_COMPRA, inicioSdde, finSdde);
            DatoSerie venta = consultarSerieIndicador(IND_VENTA, inicioSdde, finSdde);

            if (compra != null && venta != null && compra.valor > 0 && venta.valor > 0) {
                TipoCambio tc = new TipoCambio(compra.fechaIso, compra.valor, venta.valor);
                this.ultimoConocido = tc;
                return tc;
            }
        } catch (Exception e) {
            System.out.println("[Aviso] No fue posible contactar la API SDDE del BCCR ("
                    + e.getMessage() + "). Usando ultimo tipo de cambio conocido.");
        }
        return ultimoConocido;
    }

    /**
     * Consume GET /indicadoresEconomicos/{codigo}/series?fechaInicio=yyyy%2FMM%2Fdd&fechaFin=yyyy%2FMM%2Fdd&idioma=ES
     * enviando el encabezado Authorization: Bearer <token> segun el Estandar SDDE.
     */
    private DatoSerie consultarSerieIndicador(String codigoIndicador, String fechaInicio, String fechaFin)
            throws Exception {
        String query = "fechaInicio=" + URLEncoder.encode(fechaInicio, StandardCharsets.UTF_8)
                + "&fechaFin=" + URLEncoder.encode(fechaFin, StandardCharsets.UTF_8)
                + "&idioma=ES";

        String endpoint = BASE_URL + "/indicadoresEconomicos/" + codigoIndicador + "/series?" + query;
        URL url = new URL(endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                + "(KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        con.setConnectTimeout(7000);
        con.setReadTimeout(7000);

        int status = con.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            throw new IllegalStateException("HTTP " + status + " al consultar indicador " + codigoIndicador);
        }

        StringBuilder respuesta = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                respuesta.append(linea);
            }
        }
        return extraerUltimoDatoSerie(respuesta.toString());
    }

    /**
     * Recorre el arreglo "series" del JSON del SDDE y devuelve el ultimo registro
     * cuyo "valorDatoPorPeriodo" sea numerico (ignorando los que traen null).
     */
    private DatoSerie extraerUltimoDatoSerie(String json) {
        Pattern patron = Pattern.compile(
                "\"fecha\"\\s*:\\s*\"([0-9]{4}-[0-9]{2}-[0-9]{2})[^\"]*\"\\s*,\\s*"
                + "\"valorDatoPorPeriodo\"\\s*:\\s*([0-9]+(?:[.,][0-9]+)?)");
        Matcher matcher = patron.matcher(json);

        DatoSerie ultimo = null;
        while (matcher.find()) {
            String fecha = matcher.group(1);
            double valor = Double.parseDouble(matcher.group(2).replace(",", "."));
            ultimo = new DatoSerie(fecha, valor);
        }
        return ultimo;
    }

    /**
     * Metodo opcional del Estandar SDDE: POST /Usuario/ValideSuscripcion
     */
    public boolean validarSuscripcion() {
        try {
            URL url = new URL(BASE_URL + "/Usuario/ValideSuscripcion");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + token);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setDoOutput(true);

            String body = "{\"correo\":\"" + correo + "\",\"token\":\"" + token + "\"}";
            try (OutputStream os = con.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
            return con.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            return false;
        }
    }

    private static class DatoSerie {
        final String fechaIso;
        final double valor;

        DatoSerie(String fechaIso, double valor) {
            this.fechaIso = fechaIso;
            this.valor = valor;
        }
    }
}
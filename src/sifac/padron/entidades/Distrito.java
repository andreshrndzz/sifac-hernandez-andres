package sifac.padron.entidades;

public class Distrito {
    private String codigo;
    private String provincia;
    private String canton;
    private String distrito;

    public Distrito(String codigo, String provincia, String canton, String distrito) {
        this.codigo = codigo;
        this.provincia = provincia;
        this.canton = canton;
        this.distrito = distrito;
    }

    public String getCodigo() { return codigo; }
    public String getProvincia() { return provincia; }
    public String getCanton() { return canton; }
    public String getDistrito() { return distrito; }

    public String getFormatoUbicacion() {
        return distrito.trim() + ", " + canton.trim() + ", " + provincia.trim();
    }
}
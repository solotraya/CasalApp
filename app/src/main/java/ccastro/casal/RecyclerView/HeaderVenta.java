package ccastro.casal.RecyclerView;

/**
 * Created by Carlos on 14/11/2017.
 */

public class HeaderVenta {
    public String idVenta;
    public String nomClient;
    public String nomTreballador;
    public String dataVenta;
    public String ventaPagada;
    public String horaVenta;

    public HeaderVenta(String idVenta, String nomClient, String nomTreballador, String dataVenta, String ventaPagada, String horaVenta) {
        this.idVenta = idVenta;
        this.nomClient = nomClient;
        this.nomTreballador = nomTreballador;
        this.dataVenta = dataVenta;
        this.ventaPagada = ventaPagada;
        this.horaVenta = horaVenta;
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }

    public String getNomClient() {
        if (nomClient.equalsIgnoreCase("null null")) return "Cliente Barra";
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getNomTreballador() {
        return nomTreballador;
    }

    public void setNomTreballador(String nomTreballador) {
        this.nomTreballador = nomTreballador;
    }

    public String getDataVenta() {
        return dataVenta;
    }

    public void setDataVenta(String dataVenta) {
        this.dataVenta = dataVenta;
    }

    public String getVentaPagada() {
        return ventaPagada;
    }

    public void setVentaPagada(String ventaPagada) {
        this.ventaPagada = ventaPagada;
    }

    public String getHoraVenta() {
        return horaVenta;
    }

    public void setHoraVenta(String horaVenta) {
        this.horaVenta = horaVenta;
    }
}

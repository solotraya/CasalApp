package ccastro.casal.RecyclerView;

import android.util.Log;

/**
 * Created by Carlos on 17/11/2017.
 */

public class HeaderReserva {
    public String idClient;
    public String nomClient;
    public String tipusClient;
    public String pagado;
    public String assistencia;
    public String tipoPago;
    public String tipoComida;
    public String observacions;
    public String dataReserva;

    public HeaderReserva(String idClient, String nomClient, String tipusClient, String tipoPago, String tipoComida, String observacions, String pagado, String assistencia) {
        this.idClient = idClient;
        this.nomClient = nomClient;
        this.tipusClient = tipusClient;
        this.pagado = pagado;
        this.assistencia = assistencia;
        this.tipoPago = tipoPago;
        this.tipoComida = tipoComida;
        this.observacions = observacions;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getTipusClient() {
        Log.d("TIPUS CLIENTE: ",this.tipusClient);
        if (this.tipusClient.equalsIgnoreCase("0")) return "Comedor";
        if (this.tipusClient.equalsIgnoreCase("1")) return "Llevar";
        return "Ayuntamiento"; // Sin no es ni 1, ni 2,  es ayuntamiento.
    }

    public void setTipusClient(String tipusClient) {
        this.tipusClient = tipusClient;
    }

    public String getPagado() {
        return pagado;
    }

    public void setPagado(String pagado) {
        this.pagado = pagado;
    }

    public String getAssistencia() {
        return assistencia;
    }

    public void setAssistencia(String assistencia) {
        this.assistencia = assistencia;
    }

    public String getTipoPago() {
        return tipoPago;  // NO LO MOSTRAREMOS, LO USAMOS PARA ADJUDICAR MENU ID 1, 2 o 3.
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getTipoComida() {

        if (this.tipoComida.equalsIgnoreCase("0")) return "Normal";
        if (this.tipoComida.equalsIgnoreCase("1")) return "Diabetes";
        return "Estringente"; // Sin no es ni 1, ni 2,  es estringente
    }

    public void setTipoComida(String tipoComida) {
        this.tipoComida = tipoComida;
    }

    public String getObservacions() {
        return observacions;
    }

    public void setObservacions(String observacions) {
        this.observacions = observacions;
    }
}

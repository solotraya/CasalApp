package ccastro.casal.RecyclerView;

/**
 * Created by Carlos on 16/11/2017.
 */

public class HeaderMesa {
    public String idMesa;
    public String nombreMesa;
    public String diaReservado;

    public HeaderMesa(String idMesa, String nombreMesa, String diaReservado) {
        this.idMesa = idMesa;
        this.nombreMesa = nombreMesa;
        this.diaReservado = diaReservado;
    }

    public String getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(String idMesa) {
        this.idMesa = idMesa;
    }

    public String getNombreMesa() {
        return nombreMesa;
    }

    public void setNombreMesa(String nombreMesa) {
        this.nombreMesa = nombreMesa;
    }

    public String getDiaReservado() {
        return diaReservado;
    }

    public void setDiaReservado(String diaReservado) {
        this.diaReservado = diaReservado;
    }
}

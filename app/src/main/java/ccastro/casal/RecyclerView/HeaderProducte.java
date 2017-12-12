package ccastro.casal.RecyclerView;

/**
 * Created by Carlos on 06/12/2017.
 */

public class HeaderProducte {
    String idProducte;
    String nomProducte;
    String preuProducte;

    public HeaderProducte(String idProducte, String nomProducte, String preuProducte) {
        this.idProducte = idProducte;
        this.nomProducte = nomProducte;
        this.preuProducte = preuProducte;
    }

    public String getIdProducte() {
        return idProducte;
    }

    public void setIdProducte(String idProducte) {
        this.idProducte = idProducte;
    }

    public String getNomProducte() {
        return nomProducte;
    }

    public void setNomProducte(String nomProducte) {
        this.nomProducte = nomProducte;
    }

    public String getPreuProducte() {
        return preuProducte;
    }

    public void setPreuProducte(String preuProducte) {
        this.preuProducte = preuProducte;
    }
}

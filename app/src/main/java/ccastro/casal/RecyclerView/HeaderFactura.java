package ccastro.casal.RecyclerView;

/**
 * Created by Carlos on 14/11/2017.
 */

public class HeaderFactura {
    public String nombreProducto;
    public String precioProducto;
    public String tipoProducto;
    public String cantidadProducto;
    public String precioLinea;

    public HeaderFactura(String nombreProducto, String precioProducto, String tipoProducto, String cantidadProducto, String precioLinea) {
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.tipoProducto = tipoProducto;
        this.cantidadProducto = cantidadProducto;
        this.precioLinea = precioLinea;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(String precioProducto) {
        this.precioProducto = precioProducto;
    }

    public String getTipoProducto() {
        if (tipoProducto.equalsIgnoreCase("0")) return "Cafes/Infunsiones";
        else if (tipoProducto.equalsIgnoreCase("1")) return "Refrescos/Cervezas";
        else if (tipoProducto.equalsIgnoreCase("2")) return "Alimentación";
        else if (tipoProducto.equalsIgnoreCase("3")) return "Otros productos";
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(String cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public String getPrecioLinea() {
        return precioLinea;
    }

    public void setPrecioLinea(String precioLinea) {
        this.precioLinea = precioLinea;
    }
}

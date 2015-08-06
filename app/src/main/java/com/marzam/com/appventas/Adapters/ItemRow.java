package com.marzam.com.appventas.Adapters;

/**
 * Created by imartinez on 29/07/2015.
 */
import android.graphics.drawable.Drawable;

public class ItemRow {

    String pedido;
    String fecha;
    String estatus;
    String total;

    public ItemRow(String pedido, String fecha,String estatus,String total) {
        super();
        this.pedido = pedido;
        this.fecha = fecha;
        this.estatus=estatus;
        this.total=total;
    }

    public String getPedido() {
        return pedido;
    }
    public void setPedido(String pedido) {
        this.pedido = pedido;
    }
    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public String getEstatus(){return estatus;}
    public void setEstatus(String estatus){this.estatus=estatus;}
    public String getTotal(){return total;}
    public void setTotal(String total){this.total=total;}

}

package com.marzam.com.appventas;

/**
 * Created by SAMSUMG on 18/11/2014.
 */
public class Model {

    String name;
    int value;
    int cantidad;
    String precio;
    String ean;

    public Model(String name, int value,int cantidad,String precio,String ean){
        this.name=name;
        this.value=value;
        this.cantidad=cantidad;
        this.precio=precio;
        this.ean=ean;
    }
    public String getName(){
        return this.name;
    }
    public int getValue(){
        return  this.value;
    }
    public int getCantidad(){
        return this.cantidad;
    }
    public String getPrecio(){return  this.precio;}
    public String getEan(){return  this.ean;}

}

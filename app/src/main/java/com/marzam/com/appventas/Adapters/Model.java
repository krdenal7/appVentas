package com.marzam.com.appventas.Adapters;

/**
 * Created by SAMSUMG on 18/11/2014.
 */
public class Model {

    String name;
    int value;
    int cantidad;
    String precio;
    String ean;
    String precio_neto;
    String clasificacion;
    String oferta;
    String laboratorio;
    String existencia;
    boolean devolucion;

    public Model(String name, int value,int cantidad,String precio,String ean,String precio_neto,String clasificacion,String oferta,String laboratorio,String existencia,boolean devolucion){
        this.name=name;
        this.value=value;
        this.cantidad=cantidad;
        this.precio=precio;
        this.ean=ean;
        this.precio_neto=precio_neto;
        this.clasificacion=clasificacion;
        this.oferta=oferta;
        this.laboratorio=laboratorio;
        this.existencia=existencia;
        this.devolucion=devolucion;
    }
    public String getName(){ return this.name;}
    public int getValue(){return  this.value;}
    public int getCantidad(){return this.cantidad;}
    public String getPrecio(){return  this.precio;}
    public String getPrecio_neto(){return  this.precio_neto;}
    public String getEan(){return  this.ean;}
    public String getClasificacion(){return  this.clasificacion;}
    public String getOferta(){return this.oferta;}
    public String getLaboratorio(){return  this.laboratorio;}
    public String getExistencia(){return  this.existencia;}
    public boolean getDevolucion(){return  this.devolucion;}

}

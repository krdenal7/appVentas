package com.marzam.com.appventas;

/**
 * Created by SAMSUMG on 18/11/2014.
 */
public class Model {

    String name;
    int value;
    int cantidad;

    public Model(String name, int value,int cantidad){
        this.name=name;
        this.value=value;
        this.cantidad=cantidad;
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

}

package com.marzam.com.appventas;

/**
 * Created by SAMSUMG on 18/11/2014.
 */
public class Model {

    String name;
    int value;

    public Model(String name, int value){
        this.name=name;
        this.value=value;
    }
    public String getName(){
        return this.name;
    }
    public int getValue(){
        return  this.value;
    }

}

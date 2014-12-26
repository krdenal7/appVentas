package com.marzam.com.appventas.Adapters;

import android.content.Context;

/**
 * Created by SAMSUMG on 26/12/2014.
 */
public class Model_tipo_orden {

     String tipo;

    public Model_tipo_orden(String tipo){
        this.tipo=tipo;
    }
    public String getTipo(){
        return this.tipo;
    }
}

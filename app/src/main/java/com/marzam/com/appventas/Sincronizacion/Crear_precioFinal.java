package com.marzam.com.appventas.Sincronizacion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.marzam.com.appventas.SQLite.CSQLite;

/**
 * Created by SAMSUMG on 08/12/2014.
 */
public class Crear_precioFinal {

    Context context;
    CSQLite lite;

    public void Ejecutar(Context context){
        this.context=context;
        lite=new CSQLite(context);
        Llenar_precioFinal_precio();



    }

    public void Llenar_precioFinal_precio(){

        SQLiteDatabase db=lite.getWritableDatabase();

        db.execSQL("update productos set precio_final=precio");

        db.close();

    }//Se hace una copia de la columna precio a la columna precio_final sin ninguna modificación

    public String[] Obtener_Clientes_con_Descuento_Cliente(){

        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=null;
        rs=db.rawQuery("select * from productos where clasificacion_fiscal='B' OR 'BA' ",null);




        return new String[3];
    }

    public Double Obtener_descuentoCliente(String cliente){

        Double desc=0.00;

        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select descuento_comercial  from clientes where id_cliente='"+cliente+"'",null);

        if(rs.moveToFirst()){

            desc=Double.parseDouble(rs.getString(0));
        }


        return desc;
    }


}

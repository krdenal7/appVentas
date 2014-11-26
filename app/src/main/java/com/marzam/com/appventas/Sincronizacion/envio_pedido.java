package com.marzam.com.appventas.Sincronizacion;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import com.marzam.com.appventas.SQLite.CSQLite;


public class envio_pedido {

    Context context;
    TextView txt_idPedido;
    String idPedido;
    CSQLite lite;

  public void InsertarIdPedido(Context context){
      this.context=context;

      lite=new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();

      Cursor rs=db.rawQuery("select * from encabezado_pedido",null);

      if(rs.getCount()==0){

          db.execSQL("insert into encabezado_pedido (id_pedido) values ('134057A0000001')");
      }

      rs.close();
      db.close();
      lite.close();

  }


  public void actualizarTipoOrden(Context context,String Tipo){
      this.context=context;

      lite=new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();

      db.execSQL("update encabezado_pedido set tipo_orden='"+Tipo+"' where id_pedido=134057A0000001'");

      db.close();
      lite.close();

  }



}

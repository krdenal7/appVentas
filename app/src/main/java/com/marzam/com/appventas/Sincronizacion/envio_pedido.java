package com.marzam.com.appventas.Sincronizacion;



import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;


import com.marzam.com.appventas.Email.Mail;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.WebService.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class envio_pedido {

    Context context;

    CSQLite lite;
    int total_piezas=0;
    double subTotal=0.00;
    double importe_total=0.00;
    double iva=0.00;
    double ieps=0.00;
    static File directorio;
    static InputStream stream;

    String[] cabecero=new String[13];
    String P_noinsertados=null;
    WebServices webServices;
    String id_pedido;
    Mail m;
    String from;
    String subject;
    String body;



   //File file=new File(directorio+"/dbBackup.zip");

    public String GuardarPedido(Context context){
        this.context=context;
        String resp="";
        webServices=new WebServices();
        String agente=ObtenerAgenteActivo();
        id_pedido=LeerTXT();


        if(Verificar_productos()) {

            String json=jsonVisitas();
            String visita;



                visita = json==null ? null:webServices.SincronizarVisitas(jsonVisitas());

                 if(visita != null)
                      ActualizarStatusVisita(visita);



        if (Insertar_Cabecero())
                 Insertar_Detalle();

            if(isOnline()==false) {
                LimpiarBD_Insertados();
                return "Pedido guardado localmente. Verifique su conexión y sincronize los pedidos";
            }

            String res=webServices.SincronizarPedidos(JSONCabecera(),JSONDetalle());

            if(res==null) {
                LimpiarBD_Insertados();
                return "Fallo al enviar pedidos. Sincronize el dispositivo para envíarlos nuevamente";
            }

            if(res!=null)
                ActualizarStatusPedido(res);

            resp = LimpiarBD_Insertados();
            return resp;
        }else {
            return "No hay productos para agregar";
        }
    }  //Metodo principal
    public void   ActualizarStatusPedido(String json){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        String sql="";
        try {


             JSONArray  array=new JSONArray(json);

            for(int i=0;i<array.length();i++){

                JSONObject jsonData=array.getJSONObject(i);

                String id = jsonData.getString("id_pedido");
                String estatus = jsonData.getString("id_estatus");
                sql="update encabezado_pedido set id_estatus='" + estatus + "' where id_pedido='" + id + "'";

                db.execSQL(sql);
            }



        } catch (JSONException e) {
            from="envio_pedido";
            subject="ActualizarStatusPedido";
            body="Query: "+sql+"\nJson: "+ json+"\nEror: "+e.toString();
            new sendEmail().execute("");
        }


    }
    private void  ActualizarStatusVisita(String json){


        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        try {



            JSONArray array=new JSONArray(json);

            for(int i=0;i<array.length();i++){

                JSONObject jsonData=array.getJSONObject(i);

                String id = jsonData.getString("id_Visita");
                String status = jsonData.getString("estatus_visita");
                db.execSQL("update visitas set status_visita='"+status+"' where id_visita='" + id + "'");

            }



        } catch (JSONException e) {
            e.printStackTrace();
            String err=e.toString();
        }


    }



  public void InsertarIdPedido(){
      String query="";
      try {
          lite = new CSQLite(context);
          SQLiteDatabase db = lite.getWritableDatabase();
            query="select * from encabezado_pedido where id_pedido='" + id_pedido + "'";

          Cursor rs = db.rawQuery(query, null);

          if (rs.getCount() == 0) {
                 query="insert into encabezado_pedido (id_pedido) values ('" + id_pedido + "')";
              db.execSQL(query);
          }

          rs.close();
          db.close();
          lite.close();
      }catch (Exception e){
          from="envio_pedido";
          subject="InsertarIdPedido()";
          body="Query: "+query+"\n Error: "+e.toString();
          new sendEmail().execute("");
      }

  }


    public String ObtenerAgenteActivo(){

        String clave = "";
        String query="";
        try {

            lite = new CSQLite(context);
            SQLiteDatabase db = lite.getWritableDatabase();

            query="select clave_agente from agentes where Sesion=1";
            Cursor rs = db.rawQuery(query, null);
            if (rs.moveToFirst()) {

                clave = rs.getString(0);
            }
        }catch (Exception e){
            from="envio_pedido";
            subject="ObtenerAgenteActivo";
            body="Query: "+query+"\nError: "+e.toString();
            new sendEmail().execute("");
        }

        return clave;
    }


   /*Obtener datos del encabezado*/
   public String LeerTXT(){
       String id_pedido="";

       try{

           InputStreamReader archivo=new InputStreamReader(((Activity)context).openFileInput("Pedidos.txt"));
           BufferedReader br=new BufferedReader(archivo);

           id_pedido=br.readLine();

       }catch (Exception e){
           from="envio_pedido";
           subject="LeerTXT";
           body="Error: "+e.toString();
           new sendEmail().execute("");
       }

       return  id_pedido;
   }
  public String Obtener_idCliente(){

      lite=new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();
      String cliente="";
      String query="";

      try {

          query="select id_cliente from sesion_cliente where Sesion=1";
          Cursor rs = db.rawQuery(query, null);


          if (rs.moveToFirst()) {
              cliente = rs.getString(0);
          }
      }catch (Exception e){
          from="envio_pedido";
          subject="Obtener_idCliente";
          body="Query: "+query+"\n Error: "+e.toString();
          new sendEmail().execute("");
      }

      return cliente;
  }
  public String Obtener_NoEmpleado(){

      lite=new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();
      String clave="";
      String query="";

      try {
          query="select numero_empleado from agentes where Sesion=1";
          Cursor rs = db.rawQuery(query, null);
          if (rs.moveToFirst()) {

              clave = rs.getString(0);
          }
      }catch (Exception e){
          from="envio_pedido";
          subject="Obtener_NoEmpleado";
          body="Query: "+query+"\nError: "+e.toString();
          new sendEmail().execute("");

      }

      return clave;
  }
  public void   Obtener_Valores(){

      lite=new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();
      iva=0.00;
      ieps=0.00;
      String query="";

      try {

          query="select precio_final,Cantidad,ieps,iva from productos where isCheck=1";
          Cursor rs = db.rawQuery(query, null);

          while (rs.moveToNext()) {

              Double precio = Double.parseDouble(rs.getString(0));
              int cantidad = Integer.parseInt(rs.getString(1));
              Double ieps1 = Double.parseDouble(rs.getString(2));
              Double iva1 = Double.parseDouble(rs.getString(3));

              Double iep = (precio * ieps1 / 100) * cantidad;
              Double cant1 = (precio * ieps1 / 100);
              Double cant = ((precio) + cant1);
              Double cant2 = (cant * iva1 / 100) * cantidad;

              ieps += iep;
              iva += cant2;

              total_piezas += cantidad;
              subTotal += (precio * cantidad);

          }
      }catch (Exception e){
          from="envio_pedido";
          subject="Obtener_valores";
          body="Error: "+e.toString();
          new sendEmail().execute("");
      }


      importe_total=subTotal+ieps+iva;

  }//Completo
  public String Obtener_firma(String id_cliente){

      File folder = android.os.Environment.getExternalStorageDirectory();
      directorio = new File(folder.getAbsolutePath() + "/Marzam/Imagenes");
      File imagen=new File(directorio+"/"+id_cliente+".jpg");
      byte[] bytes;
      byte[] buffer=new byte[8192];
      int bytesRead;
      String base="";

      if (imagen.exists() == false) {
          return "";
      }

      //byte[] img=StreamArchivo(imagen);
      try {

          InputStream input=new FileInputStream(directorio+"/Firma1.jpg");

          ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

          try {
              while((bytesRead=input.read(buffer))!=-1){

                  outputStream.write(buffer,0,bytesRead);

              }

              bytes=outputStream.toByteArray();
              base=Base64.encodeToString(bytes,Base64.DEFAULT);



          } catch (IOException e) {
              e.printStackTrace();
          }


      } catch (FileNotFoundException e) {
          e.printStackTrace();
      }
      return base;
  }//Conpleto
  public String Obtener_firma_Hexa(String id_cliente){

        File folder = android.os.Environment.getExternalStorageDirectory();
        directorio = new File(folder.getAbsolutePath() + "/Marzam/Imagenes");
        File imagen=new File(directorio+"/"+id_cliente+".jpg");
        byte[] bytes = new byte[0];
        byte[] buffer=new byte[8192];
        int bytesRead;
        String base="";
        String hexa="";

        if (imagen.exists() == false) {
            return "";
        }

        byte[] img=StreamArchivo(imagen);

       StringBuilder builder=new StringBuilder(img.length * 2);

      for(byte b:img){
          builder.append(String.format("%02x",b&0xff));
      }


        return builder.toString();
    }//Conpleto
  public String Obtener_tipoOrden(){

      lite=new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();
      String tipo="";
      Cursor rs=db.rawQuery("select tipo_orden from tipo_fuerza where isCheck=1 ",null);

      if(rs.moveToFirst()){
          tipo=rs.getString(0);
      }else {
          tipo="FG";
      }

      return tipo;

  }
  public String Obtener_descuentoComercial(){
      String desc = "0";
try {


    SQLiteDatabase db = lite.getWritableDatabase();
    Cursor rs = db.rawQuery("select descuento_comercial from clientes where id_cliente=(select id_cliente from sesion_cliente where Sesion=1)", null);



    if (rs.moveToFirst()) {
        desc = rs.getString(0);
    }
}catch (Exception e){
    from="envio_pedido";
    subject="Obtener_descuentocomercial";
    body="Error: "+e.toString();
    new sendEmail().execute("");
}
      return desc;

  }
  public String Obtener_oferta(String codigo){
      String oferta="0";

      try {
          SQLiteDatabase db = lite.getWritableDatabase();
          String Fecha = getDate2();
          Cursor rs = db.rawQuery("select descuento from ofertas where codigo='" + codigo + "'  and vigencia_incio >='" + Fecha + "'   and vigencia_fin <= '" + Fecha + "'", null);


          if (rs.moveToFirst())
              oferta = rs.getString(0);
      }catch (Exception e) {
      from="envio_pedido";
      subject="Obtener_oferta";
      body="Error: "+e.toString();
      new sendEmail().execute("");
      }

      return oferta;
  }

 public String Obtener_Idvisita(){

        String id="";
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select max(id_visita) from visitas ",null);


        if(rs.moveToFirst()){
            id=rs.getString(0);
        }

        return id;
    }//GENERA EL ID CORRESPONDIENTE DE LA VISITA

/*Obtener datos del detalle*/




  public  boolean  Insertar_Cabecero(){
      this.context=context;

      Obtener_Valores();
      String fec=getDate();


      cabecero[0] = id_pedido;
      cabecero[1] = Obtener_idCliente();
      cabecero[2] = Obtener_NoEmpleado();
      cabecero[3] =ObtenerAgenteActivo();
      cabecero[4] = String.valueOf(total_piezas);
      cabecero[5] = String.valueOf(importe_total);
      cabecero[6] = Obtener_tipoOrden();
      cabecero[7] = fec;
              fec = getDate();
      cabecero[8] = fec;
      cabecero[9] = "10";
      cabecero[10] = "";
      cabecero[11] = Obtener_firma(cabecero[1]);
      cabecero[12] = Obtener_Idvisita();

      InsertarIdPedido();
      lite =new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();

     try {
         db.execSQL("update encabezado_pedido set id_pedido=?,id_cliente=?,numero_empleado=?,clave_agente=?,total_piezas=?,impote_total=?" +
                    ",tipo_orden=?,fecha_captura=?,fecha_transmision=?,id_estatus=?,no_pedido_cliente=?,firma=?,id_visita=? where id_pedido='"+cabecero[0]+"'", cabecero);
     }catch (Exception e){
         from="envio_pedido";
         subject="Insertar_Cabecero Exception";
         body=e.toString();
         new sendEmail().execute("");
         return false;
     }

           return true;

  }//completo
  public  void     Insertar_Detalle(){

     lite=new CSQLite(context);
     SQLiteDatabase db=lite.getWritableDatabase();

     Cursor rs=null;
      try{
          rs=db.rawQuery("select codigo,Cantidad,precio,clasificacion_fiscal,iva,ieps,precio_final from productos where isCheck=1 ", null);
      }catch (Exception e) {
          from="envio_pedido";
          subject="insertar_detalle Exception";
          body="Consulta de productos: "+e.toString();
          new sendEmail().execute("");
      }
     int cantidad=rs.getColumnCount();
     String id_pedido=this.id_pedido;
     String orden=Obtener_tipoOrden();
     String desc=Obtener_descuentoComercial();
     while (rs.moveToNext()){
         String codigo=rs.getString(0);
         String cant=rs.getString(1);
         String precio=rs.getString(2);
         String clasificacion=rs.getString(3);
         String iva=rs.getString(4);
         String ieps=rs.getString(5);
         String final_p=rs.getString(6);

         String query="insert into detalle_pedido(id_pedido,codigo,piezas_pedidas,piezas_surtidas,precio_farmacia,clasfificacion_fiscal,oferta,desc_comercial,precio_neto,iva,ieps,factura_marzam,orden)values" +
        "('"+id_pedido+"','"+codigo+"',"+cant+",0,"+precio+",'"+clasificacion+"',"+Obtener_oferta(rs.getString(0))+","+desc+","+final_p+","+iva+","+ieps+",'','"+orden+"')";

         try {

             db.execSQL(query);

         }catch (Exception e){
             from="envio_pedido";
             subject="Insertar detalle Exception";
             body="Insertar lineas "+e.toString();
             new sendEmail().execute("");
             P_noinsertados+=rs.getString(1);
         }


     }

      rs.close();
      db.close();
      lite.close();



  }//completo

  public Boolean Verificar_productos(){

      lite=new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();
      Cursor rs=db.rawQuery("select * from productos where isCheck=1",null);

      if(rs.moveToFirst())
          return true;

          return false;
  }//Verifica si hay productos para agregar al detalle


    public String LimpiarBD_Insertados(){

      String resp="";

     lite=new CSQLite(context);
     SQLiteDatabase db=lite.getWritableDatabase();

     Cursor rs=null;
     String query="select codigo from detalle_pedido";
     rs=db.rawQuery(query,null);

     while (rs.moveToNext()){

         String codigo=rs.getString(0);
         db.execSQL("update productos set isCheck=0,Cantidad=0 where codigo='"+codigo+"'");

     }

     if(P_noinsertados!=null) {
         resp="Estos productos no se agregaron favor de verificar!";
     }

      return resp;
 }



    public String JSONCabecera(){

       lite=new CSQLite(context);
       SQLiteDatabase db=lite.getWritableDatabase();
       String id=id_pedido;

       Cursor rs= db.rawQuery("select * from encabezado_pedido where id_pedido='" + id + "'", null);
       JSONObject json=new JSONObject();
       JSONArray array=new JSONArray();


while (rs.moveToNext()) {
    try {

        json.put("id_pedido", rs.getString(0));
        json.put("id_cliente", rs.getString(1));
        json.put("numero_empleado", rs.getString(2));
        json.put("clave_agente", rs.getString(3));
        json.put("total_piezas", rs.getString(4));
        json.put("impote_total", rs.getString(5));
        json.put("tipo_orden", rs.getString(6));
        json.put("fecha_captura", rs.getString(7)!=null ?   rs.getString(7).replace(":","|"):"01-01-2014 00|00|00");
        json.put("fecha_transmision", getDate().replace(":","|"));
        json.put("id_estatus","10");
        json.put("no_pedido_cliente", rs.getString(10));
        json.put("firma", Obtener_firma_Hexa(rs.getString(1)));
        json.put("id_visita", rs.getString(12));
        array.put(json);

        if(array.length()<=0){

            from="envio_pedido";
            subject="JSONCabecera";
            body=array.toString();
            new sendEmail().execute("");
        }

    }catch (Exception e){

           from="envio_pedido";
           subject="JSONCabecera Exception";
           body=e.toString();
           new sendEmail().execute("");
    }
}


      return array.toString();
  }
    public String JSONDetalle(){

     lite=new CSQLite(context);
     SQLiteDatabase db=lite.getWritableDatabase();
     String id=id_pedido;
     Cursor rs=db.rawQuery("select * from detalle_pedido where id_pedido='" + id + "'", null);
     JSONObject json=new JSONObject();
     JSONArray array=new JSONArray();


     while (rs.moveToNext()){
try {
    String codigo=rs.getString(1);
    json.put("id_pedido", rs.getString(0));
    json.put("codigo", codigo);
    json.put("piezas_pedidas", rs.getString(2));
    json.put("piezas_surtidas", rs.getString(3));
    json.put("precio_farmacia", rs.getString(4));
    json.put("clasfificacion_fiscal", rs.getString(5));
    json.put("oferta", rs.getString(6));
    json.put("desc_comercial", rs.getString(7));
    json.put("precio_neto", rs.getString(8));
    json.put("iva", rs.getString(9));
    json.put("ieps", rs.getString(10));
    json.put("factura_marzam", rs.getString(11));
    json.put("orden", rs.getString(12));
    array.put(json);
    json=new JSONObject();
}catch (Exception e){
    from="envio_pedido";
    subject="JSONDetalle Exception";
    body=e.toString();
    new sendEmail().execute("");
    continue;
}

     }
       if(array.length()<=0){

            from="envio_pedido";
            subject="JSONDetalle";
            body=array.toString();
            new sendEmail().execute("");
                     }
        return array.toString();
    }
    public String jsonVisitas(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select * from visitas where status_visita='10'",null);
        JSONArray array=new JSONArray();
        JSONObject object=new JSONObject();

        while (rs.moveToNext()){

            try {



                object.put("numero_empleado",rs.getString(0));
                object.put("id_cliente",rs.getString(1));
                object.put("latitud",rs.getString(2));
                object.put("longitud",rs.getString(3));
                String fechavisita=rs.getString(4);
                object.put("fecha_visita",fechavisita!=null ? fechavisita.replaceAll(":","|"):"01-01-2014 00|00|00");
                String fecharegistro=rs.getString(5);
                object.put("fecha_registro", fecharegistro!=null ? fecharegistro.replaceAll(":","|"):"01-01-2014 00|00|00");
                String id_visita=rs.getString(6);
                object.put("id_visita",id_visita);

                array.put(object);
                object=new JSONObject();


            } catch (JSONException e) {
                from="envio_pedido";
                subject="jsonVisitas";
                body="Error: "+e.toString();
                new sendEmail().execute("");
            }


        }


        return array.length()== 0 ? null: array.toString();
    }

    public static byte[] StreamArchivo(File file){



        try {
            stream = new FileInputStream(file);
        }catch (Exception e){
            String a=e.toString();
            Log.d("ConvertStream:", a);
        }

        long length=file.length();
        int numRead=0;
        byte[] bytes=new byte[(int)length];
        int offset=0;
        try {
            while(offset<bytes.length && (numRead=stream.read(bytes,offset,bytes.length-offset))>=0){

                offset+=numRead;

            }
            if(offset<bytes.length){
                Log.d("Error al convertir en bytes","Fallo");
            }
        } catch (IOException e) {
            e.printStackTrace();
            String a=e.toString();
        }


        return  bytes;
    }
    public String getDate(){

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formatteDate=df.format(dt.getTime());


        return formatteDate;
    }//Completo
    private String getDate2(){

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
        String formatteDate=df.format(dt.getTime());

        return formatteDate;
    }//retorna la fecha en formato yyyyMMdd


    /*Enviar Pedido por WebService*/
    public  boolean isOnline(){

        ConnectivityManager cm=(ConnectivityManager)((Activity)context).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo !=null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }



   public class sendEmail extends AsyncTask<String,Void,Object>{

       @Override
       protected Object doInBackground(String... strings) {


           m = new Mail("rodrigo.cabrera.it129@gmail.com", "juanito1.");
           String[] toArr = {"imartinez@marzam.com.mx","cardenal.07@hotmail.com"};
           m.setTo(toArr);
           m.setFrom(from);
           m.setSubject(subject);
           m.setBody(body);

           try {

               m.send();

           } catch (Exception e) {
               e.printStackTrace();
           }

           return null;
       }
   }

}

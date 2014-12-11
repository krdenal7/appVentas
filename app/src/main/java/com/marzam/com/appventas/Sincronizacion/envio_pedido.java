package com.marzam.com.appventas.Sincronizacion;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;


import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.WebService.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.commons.codec.binary.Hex;


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

    String[] cabecero;
    String P_noinsertados=null;
    WebServices webServices;



   //File file=new File(directorio+"/dbBackup.zip");

    public String GuardarPedido(Context context){
        this.context=context;
        String resp="";
        webServices=new WebServices();


        if(Verificar_productos()) {

            webServices.SincronizarVisitas(jsonVisitas());

        if (Insertar_Cabecero())
                 Insertar_Detalle();

            if(isOnline()==false) {
                LimpiarBD_Insertados();
                updateConsecutivo();
                return "Pedido guardado localmente. Verifique su conexión y sincronize los pedidos";
            }

            String res=webServices.SincronizarPedidos(JSONCabecera(),JSONDetalle());

            if(res==null) {
                LimpiarBD_Insertados();
                updateConsecutivo();
                return "Fallo al envíar pedidos. Sincronize el dispositivo para envíarlos nuevamente";
            }

            if(res!=null)
                ActualizarStatusPedido(res);

            resp = LimpiarBD_Insertados();
            updateConsecutivo();
            return resp;
        }else {
            return "No hay productos para agregar";
        }
    }  //Metodo principal
    public void ActualizarStatusPedido(String json){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        try {


            JSONArray array=new JSONArray(json);

            for(int i=0;i<array.length();i++){

                JSONObject jsonData=array.getJSONObject(i);

                String id = jsonData.getString("id_pedido");
                String estatus = jsonData.getString("id_estatus");

                db.execSQL("update encabezado_pedido set id_estatus='" + estatus + "' where id_pedido='" + id + "'");
            }



        } catch (JSONException e) {
            e.printStackTrace();
            String err=e.toString();
        }


    }



  public void InsertarIdPedido(){
      this.context=context;

      lite=new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();

                             Cursor rs=db.rawQuery("select * from encabezado_pedido where id_pedido='"+Obtener_idPedido()+"'",null);

      if(rs.getCount()==0){

                             db.execSQL("insert into encabezado_pedido (id_pedido) values ('"+Obtener_idPedido()+"')");
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

    public String ObtenerAgenteActivo(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        String clave="";

        Cursor rs=db.rawQuery("select clave_agente from agentes where Sesion=1",null);
        if(rs.moveToFirst()){

            clave=rs.getString(0);
        }

        return clave;
    }
    public String Consecutivo(){

        String numero="";

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select id from consecutivo",null);
        if(rs.moveToFirst()){
            numero=rs.getString(0);
        }

        return numero;
    }

   /*Obtener datos del encabezado*/
  public String Obtener_idPedido(){
      StringBuilder builder=new StringBuilder();

      builder.append("P"+ObtenerAgenteActivo());
      String consecutivo=Consecutivo();

      int val=(builder.length()+consecutivo.length());
      int falt=(12-val);

      for(int i=0;i<falt;i++){
          builder.append("0");
      }
      builder.append(consecutivo);



      return builder.toString();
  }
  public String Obtener_idCliente(){

      lite=new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();
      Cursor rs=db.rawQuery("select id_cliente from sesion_cliente where Sesion=1",null);

      String cliente="";

      if(rs.moveToFirst()){
          cliente=rs.getString(0);
      }

      return cliente;
  }
  public String Obtener_NoEmpleado(){
      lite=new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();
      String clave="";

      Cursor rs=db.rawQuery("select numero_empleado from agentes where Sesion=1",null);
      if(rs.moveToFirst()){

          clave=rs.getString(0);
      }

      return clave;
  }
  public void   Obtener_Valores(){

      lite=new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();
      iva=0.00;
      ieps=0.00;

      Cursor rs=db.rawQuery("select precio_final,Cantidad,ieps,iva from productos where isCheck=1",null);
      // CantProductos=rs.getCount();
      while (rs.moveToNext()){

          Double precio=Double.parseDouble(rs.getString(0));
          int cantidad=Integer.parseInt(rs.getString(1));
          Double ieps1=Double.parseDouble(rs.getString(2));
          Double iva1=Double.parseDouble(rs.getString(3));




          Double cant1=(precio*ieps1/100);
          Double cant=((precio)+cant1);
          Double cant2=(cant*iva1/100);

          ieps+=cant1;
          iva+=cant2;

          total_piezas+=cantidad;
          subTotal+=(precio*cantidad);

      }


      importe_total=subTotal+ieps+iva;

  }//Completo
  public String[] Obtener_ValoresEncabezado(){
      String[] val=new String[2];

   SQLiteDatabase db=lite.getWritableDatabase();

      Cursor rs=db.rawQuery("select total_piezas,impote_total from encabezado_pedido where id_pedido='"+Obtener_idPedido()+"'",null);

      int Cantidad=0;
      Double importe=0.00;

      while (rs.moveToNext()){

          Cantidad=Integer.parseInt(rs.getString(0));
          importe=Double.parseDouble(rs.getString(1));
      }

      val[0]=String.valueOf(Cantidad);
      val[1]=String.valueOf(importe);

      return val;
  }//completo
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

      return "FD";

  }
  public String Obtener_descuentoComercial(){


      SQLiteDatabase db=lite.getWritableDatabase();
      Cursor rs=db.rawQuery("select descuento_comercial from clientes where id_cliente=(select id_cliente from sesion_cliente where Sesion=1)",null);

      String desc="";

      if(rs.moveToFirst()){
          desc=rs.getString(0);
      }

      return desc;

  }
  public String Obtener_oferta(String codigo){

      SQLiteDatabase db=lite.getWritableDatabase();

      String oferta="0";
      String Fecha=getDate2();
      Cursor rs=db.rawQuery("select descuento from ofertas where codigo='" + codigo + "'  and vigencia_incio >='" + Fecha + "'   and vigencia_fin <= '" + Fecha + "'", null);


      if (rs.moveToFirst())
           oferta = rs.getString(0);


      return oferta;
  }

 public String Obtener_Idvisita(){

        String id="";
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select id from consecutivo_visitas ",null);


        if(rs.moveToFirst()){
            id=rs.getString(0);
        }

        StringBuilder builder=new StringBuilder();
        builder.append("V");
        String clave=ObtenerAgenteActivo();
        builder.append(clave);

        int tam=(12-(clave.length()+1+id.length()));

        for (int i=0;i<tam;i++){
            builder.append("0");
        }

        builder.append(id);


        return builder.toString();
    }//GENERA EL ID CORRESPONDIENTE DE LA VISITA

/*Obtener datos del detalle*/


  public String[] Dib_encabezado(){
      String[] enc=null;

      cabecero=new String[19];
      Obtener_Valores();
      String[] Fech=getDate();

      try {

          cabecero[0] = Obtener_idPedido();
          cabecero[1] = Obtener_idCliente();
          cabecero[2] = Obtener_NoEmpleado();
          cabecero[3] =ObtenerAgenteActivo();
          cabecero[4] = String.valueOf(total_piezas);
          cabecero[5] = String.valueOf(importe_total);
          cabecero[6] = Obtener_tipoOrden();
          cabecero[7] = Fech[0];
          cabecero[8] = Fech[1];
          cabecero[9] = Fech[2];
          cabecero[10] = Fech[3];
          Fech = getDate();
          cabecero[11] = Fech[0];
          cabecero[12] = Fech[1];
          cabecero[13] = Fech[2];
          cabecero[14] = Fech[3];
          cabecero[15] = "10";
          cabecero[16] = "";
          cabecero[17] = Obtener_firma(cabecero[1]);
          cabecero[18] = Obtener_Idvisita();

      }catch (Exception e){
          return enc=new String[0];
      }


      return cabecero;
  }//completo

  public  boolean  Insertar_Cabecero(){
      this.context=context;


      String[] val=Dib_encabezado();
      String[] valores=new String[13];

      valores[0]=val[0];
      valores[1]=val[1];
      valores[2]=val[2];
      valores[3]=val[3];
      valores[4]=val[4];
      valores[5]=val[5];
      valores[6]=val[6];
      valores[7]=val[7]+" "+val[8]+":"+val[9]+":"+val[10];
      valores[8]=val[11]+" "+val[12]+":"+val[13]+":"+val[14];
      valores[9]=val[15];
      valores[10]=val[16];
      valores[11]=val[17];
      valores[12]=val[18];

      InsertarIdPedido();
      lite =new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();

     try {
         db.execSQL("update encabezado_pedido set id_pedido=?,id_cliente=?,numero_empleado=?,clave_agente=?,total_piezas=?,impote_total=?" +
                    ",tipo_orden=?,fecha_captura=?,fecha_transmision=?,id_estatus=?,no_pedido_cliente=?,firma=?,id_visita=? where id_pedido='"+valores[0]+"'", valores);
     }catch (Exception e){
         String err=e.toString();
         Log.d("Error al actualizar encabezado:",err);
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
          String err=e.toString();
          Log.d("Error al obtener datos de producto:",err);
      }

     int cantidad=rs.getColumnCount();
     String id_pedido=Obtener_idPedido();
     String orden=Obtener_tipoOrden();
     String desc=Obtener_descuentoComercial();

     while (rs.moveToNext()){

         String query="insert into detalle_pedido(id_pedido,codigo,piezas_pedidas,piezas_surtidas,precio_farmacia,clasfificacion_fiscal,oferta,desc_comercial,precio_neto,iva,ieps,factura_marzam,orden)values" +
        "('"+id_pedido+"','"+rs.getString(0)+"',"+rs.getString(1)+",0,"+rs.getString(2)+",'"+rs.getString(3)+"',"+Obtener_oferta(rs.getString(0))+","+desc+","+rs.getString(6)+","+rs.getString(4)+","+rs.getString(5)+",'','"+orden+"')";

         try {

             db.execSQL(query);

         }catch (Exception e){
             String err=e.toString();
             Log.d("Error al insertar detalle",err);
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
    public void updateConsecutivo(){

      try {
          lite = new CSQLite(context);
          SQLiteDatabase db = lite.getWritableDatabase();
          int consecutivo = Integer.parseInt(Consecutivo().trim());
          db.execSQL("update consecutivo set id=" + (consecutivo + 1) + "");

          db.close();
          lite.close();
      }catch (Exception e){
          String err=e.toString();
          Log.d("Error al actualizar consecutivo",err);
      }

  }


    public String JSONCabecera(){

       lite=new CSQLite(context);
       SQLiteDatabase db=lite.getWritableDatabase();
       String id=Obtener_idPedido();

       Cursor rs= db.rawQuery("select * from encabezado_pedido where id_pedido='"+id+"'",null);
       JSONObject json=new JSONObject();
       JSONArray array=new JSONArray();
       String[] date=getDate();

while (rs.moveToNext()) {
    try {

        json.put("id_pedido", rs.getString(0));
        json.put("id_cliente", rs.getString(1));
        json.put("numero_empleado", rs.getString(2));
        json.put("clave_agente", rs.getString(3));
        json.put("total_piezas", rs.getString(4));
        json.put("impote_total", rs.getString(5));
        json.put("tipo_orden", rs.getString(6));
        json.put("fecha_captura",   date[0]);
        json.put("hora_captura",    date[1]);
        json.put("minuto_captura", date[2]);
        json.put("segundo_captura", date[3]);
        json.put("fecha_transmision",  date[0]);
        json.put("hora_transmision",   date[1]);
        json.put("minuto_transmision", date[2]);
        json.put("segundo_transmision",date[3]);
        json.put("id_estatus","0");
        json.put("no_pedido_cliente", rs.getString(10));
        json.put("firma", Obtener_firma_Hexa(rs.getString(1)));
        json.put("id_visita", rs.getString(12));
        array.put(json);

    }catch (Exception e){

        String err=e.toString();
        Log.d("Error al crear JsonCab:",err);

    }
}


      return array.toString();
  }
    public String JSONDetalle(){

     lite=new CSQLite(context);
     SQLiteDatabase db=lite.getWritableDatabase();
     String id=Obtener_idPedido();
     Cursor rs=db.rawQuery("select * from detalle_pedido where id_pedido='"+id+"'",null);
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
    String err=e.toString();
    Log.d("Error JSONDetalle",err);
}

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
                String Fecha[]=Dividirfecha(rs.getString(4));
                object.put("fecha_visita",Fecha[0]);
                object.put("hora_visita",Fecha[1]);
                object.put("minuto_visita",Fecha[2]);
                object.put("segundo_visita",Fecha[3]);
                String[] fecha2=Dividirfecha(rs.getString(5));
                object.put("fecha_registro",fecha2[0]);
                object.put("hora_registro",fecha2[1]);
                object.put("minuto_registro",fecha2[2]);
                object.put("segundo_registro",fecha2[3]);
                String id_visita=rs.getString(6);
                object.put("id_visita",id_visita);

                array.put(object);
                object=new JSONObject();


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        return array.toString();


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
    public String[] getDate(){

        String fecha[]=new String[4];

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat dia=new SimpleDateFormat("dd-MM-yyyy ");
        SimpleDateFormat hora=new SimpleDateFormat("HH");
        SimpleDateFormat min=new SimpleDateFormat("mm");
        SimpleDateFormat seg=new SimpleDateFormat("ss");

        fecha[0]=dia.format(dt.getTime());
        fecha[1]=hora.format(dt.getTime());
        fecha[2]=min.format(dt.getTime());
        fecha[3]=seg.format(dt.getTime());

        return fecha;
    }//Completo
    private String getDate2(){

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
        String formatteDate=df.format(dt.getTime());

        return formatteDate;
    }//retorna la fecha en formato yyyyMMdd


    /*Enviar Pedido por WebService*/

    public String[] Dividirfecha(String fecha){
        String[] fechreturn=new String[4];

        try {
            String[] Fecha = fecha.split(" ");
            fechreturn[0] = Fecha[0];
            String[] Hora = Fecha[1].split(":");
            fechreturn[1] = Hora[0];
            fechreturn[2] = Hora[1];
            fechreturn[3] = Hora[2];
        }catch (Exception e){
            fechreturn[0]="01-01-2014";
            fechreturn[1]="00";
            fechreturn[2]="00";
            fechreturn[3]="00";
            return fechreturn;
        }


        return fechreturn;
    }
    public  boolean isOnline(){

        ConnectivityManager cm=(ConnectivityManager)((Activity)context).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo !=null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

}

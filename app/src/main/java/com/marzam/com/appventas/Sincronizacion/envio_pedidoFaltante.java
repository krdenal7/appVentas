package com.marzam.com.appventas.Sincronizacion;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.marzam.com.appventas.Email.Mail;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.WebService.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



public class envio_pedidoFaltante {

    Context context;
    CSQLite lite;
    String[] id_pedidos;
    JSONArray jsonArrayCab;
    JSONArray jsonArrayDet;
    WebServices services;
    File directorio;
    static InputStream stream;

    Mail m;
    String subject;
    String body;
    String from="envio_pedidoFaltante.java";


    public String Enviar(Context context){
        this.context=context;
        String respuesta="";
        File folder = android.os.Environment.getExternalStorageDirectory();
        directorio = new File(folder.getAbsolutePath() + "/Marzam/Imagenes");
        services=new WebServices();



        if(VerificarPedidosPendientes()){ //Verifica si se tienen pedidos pendientes

            String json=jsonVisitas();
            String visita;
                   visita = json==null ? null:services.SincronizarVisitas(jsonVisitas());



            if(isOnline()==false)
                return "Verifique su conexión a Internet e intente nuevamente.";

               String cabecero=Objener_JsonCabecero();
               String detalle=Obtener_Jsondetalle();



            String res=services.SincronizarPedidos(cabecero,detalle);

            if(res==null)
                return "Fallo al transmitir pedidos. Favor de verificar su conexión";

            ActualizarStatusPedido(res);

            if(VerificarPedidosPendientes()==false)
                return "Pedidos transmitidos correctamente";


        }else {
          respuesta="No hay pedidos por sincronizar";
        }


        return respuesta;
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
                object.put("fecha_visita",rs.getString(4).replace(":","|"));
                object.put("fecha_registro",rs.getString(5).replace(":","|"));
                String id_visita=rs.getString(6);
                object.put("id_visita",id_visita);

                array.put(object);
                object=new JSONObject();


            } catch (JSONException e) {
                subject="Agente:"+ObtenerAgenteActivo()+"\njsonVisitas";
                body="Array:"+ array+"\nObject: "+object+"\nError: "+e.toString();
                new sendEmail().execute("");
            }


        }

        return array.length()==0 ? null: array.toString();

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

    public void ActualizarStatusPedido(String json){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        JSONArray array = null;

        try {


                array=new JSONArray(json);

            for(int i=0;i<array.length();i++){

                JSONObject jsonData=array.getJSONObject(i);

                String id = jsonData.getString("id_pedido");
                String estatus = jsonData.getString("id_estatus");

                db.execSQL("update encabezado_pedido set id_estatus='" + estatus + "' where id_pedido='" + id + "'");
            }



        } catch (JSONException e) {

            subject="Agente:"+ObtenerAgenteActivo()+"\njsonVisitas";
            body="Array:"+ array+"\nObject: "+json+"\nError: "+e.toString();
            new sendEmail().execute("");
        }


    }

    public boolean VerificarPedidosPendientes(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();



        Cursor rs=db.rawQuery("select id_pedido from encabezado_pedido where id_estatus=10",null);

        if(rs.getCount()>0){

            id_pedidos=new String[rs.getCount()];
            int cont=0;
            while (rs.moveToNext()){
             id_pedidos[cont]=rs.getString(0);
             cont++;
            }
            db.close();
            lite.close();
            return true;
        }
        else {

            db.close();
            lite.close();
            return false;
        }


    }
    public String Objener_JsonCabecero(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();


        Cursor rs= null;


        JSONObject json=new JSONObject();
        JSONArray array=new JSONArray();

               for(int i=0;i<id_pedidos.length;i++){
               rs= db.rawQuery("select * from encabezado_pedido where id_pedido='"+id_pedidos[i]+"'",null);

               while (rs.moveToNext()) {
                   try {

                       String fec=getDate();

                       json.put("id_pedido", rs.getString(0));
                       json.put("id_cliente", rs.getString(1));
                       json.put("numero_empleado", rs.getString(2));
                       json.put("clave_agente", rs.getString(3));
                       json.put("total_piezas", rs.getString(4));
                       json.put("impote_total", rs.getString(5));
                       json.put("tipo_orden", rs.getString(6));
                       json.put("fecha_captura", rs.getString(8)!=null?  rs.getString(8).replace(":","|").replace("/","-").replace(".",""):"01-01-2014 00|00|00");
                       json.put("fecha_transmision", fec.replace(":","|"));
                       json.put("id_estatus", "0");
                       json.put("no_pedido_cliente", rs.getString(10));
                       json.put("firma",Obtener_firma_Hexa(rs.getString(1)));
                       json.put("id_visita", rs.getString(12));
                       array.put(json);
                       json=new JSONObject();

                   } catch (Exception e) {

                       subject="Agente:"+ObtenerAgenteActivo()+"\njsonVisitas";
                       body="Array:"+ array+"\nObject: "+json+"\nError: "+e.toString();
                       new sendEmail().execute("");

                   }
               }

           }

        return array.toString();
    }
    public String Obtener_Jsondetalle(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=null;
        JSONObject json=new JSONObject();
        JSONArray array=new JSONArray();

          for(int i=0;i<id_pedidos.length;i++){

              rs=db.rawQuery("select * from detalle_pedido where id_pedido='"+id_pedidos[i]+"'",null);

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
                      subject="Agente:"+ObtenerAgenteActivo()+"\njsonVisitas";
                      body="Array:"+ array+"\nObject: "+json+"\nError: "+e.toString();
                      new sendEmail().execute("");
                  }
              }
          }


        return array.toString();
    }


    public String getDate(){


        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat dia=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


        String fecha=dia.format(dt.getTime());


        return fecha;
    }//Completo
    public  boolean isOnline(){

        ConnectivityManager cm=(ConnectivityManager)((Activity)context).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo !=null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }


    public class sendEmail extends AsyncTask<String,Void,Object> {

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

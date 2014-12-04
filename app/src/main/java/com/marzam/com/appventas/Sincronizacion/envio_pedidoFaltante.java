package com.marzam.com.appventas.Sincronizacion;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ArrayAdapter;

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

/**
 * Created by SAMSUMG on 01/12/2014.
 */
public class envio_pedidoFaltante {

    Context context;
    CSQLite lite;
    String[] id_pedidos;
    JSONArray jsonArrayCab;
    JSONArray jsonArrayDet;
    WebServices services;
    File directorio;
    static InputStream stream;

    public String Enviar(Context context){
        this.context=context;
        String respuesta="";
        File folder = android.os.Environment.getExternalStorageDirectory();
        directorio = new File(folder.getAbsolutePath() + "/Marzam/Imagenes");
        services=new WebServices();

       String r=services.SincronizarVisitas(jsonVisitas());

        if(VerificarPedidosPendientes()){ //Verifica si se tienen pedidos pendientes

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

        Cursor rs=db.rawQuery("select * from visitas where status_visita='0'",null);
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

    public boolean VerificarPedidosPendientes(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();



        Cursor rs=db.rawQuery("select id_pedido from encabezado_pedido where id_estatus=0",null);

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
        String F="";
        String H="";
        String M="";
        String S="";



               for(int i=0;i<id_pedidos.length;i++){
               rs= db.rawQuery("select * from encabezado_pedido where id_pedido='"+id_pedidos[i]+"'",null);

               while (rs.moveToNext()) {
                   try {
                         try {

                             String[] Fecha = rs.getString(7).split(" ");
                             F=Fecha[0];
                             String[] Hora = Fecha[2].split(":");
                             H=Hora[0];
                             M=Hora[1];
                             S=Hora[2];

                         }catch (Exception e){

                         }

                       String[] date=getDate();

                       json.put("id_pedido", rs.getString(0));
                       json.put("id_cliente", rs.getString(1));
                       json.put("numero_empleado", rs.getString(2));
                       json.put("clave_agente", rs.getString(3));
                       json.put("total_piezas", rs.getString(4));
                       json.put("impote_total", rs.getString(5));
                       json.put("tipo_orden", rs.getString(6));
                       json.put("fecha_captura", F);
                       json.put("hora_captura", H);
                       json.put("minuto_captura", M);
                       json.put("segundo_captura", S);
                       json.put("fecha_transmision", date[0]);
                       json.put("hora_transmision", date[1]);
                       json.put("minuto_transmision", date[2]);
                       json.put("segundo_transmision", date[3]);
                       json.put("id_estatus", "0");
                       json.put("no_pedido_cliente", rs.getString(10));
                       json.put("firma",Obtener_firma_Hexa(rs.getString(1)));
                       json.put("id_visita", rs.getString(12));
                       array.put(json);
                       json=new JSONObject();

                   } catch (Exception e) {

                       String err = e.toString();
                       Log.d("Error al crear JsonCab:", err);

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
                      String err=e.toString();
                      Log.d("Error JSONDetalle",err);
                  }
              }
          }


        return array.toString();
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
    public  boolean isOnline(){

        ConnectivityManager cm=(ConnectivityManager)((Activity)context).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo !=null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

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

}

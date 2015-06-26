package com.marzam.com.appventas.Sincronizacion;

import android.app.Activity;
import android.content.ContentValues;
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
    String[] id_devoluciones;
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

        if(VerificarClientesPendientes()>0){

            String jsonClientes=JSonClientes();
            String claveAgente=ObtenerClaveEmpleado();

            String jsonRespuesta=services.InsertarCliente(jsonClientes, claveAgente);

            if(jsonRespuesta != null){

                     String rs=ProcesaJson(jsonRespuesta);
                     respuesta=!rs.isEmpty()?rs:"Fallo al envíar información";

                    if(VerificarPedidosPendientes()){
                        respuesta=EnvioPedido();
                    }

            }else{
                    if(VerificarPedidosPendientes()){
                       respuesta=EnvioPedido();
                    }
            }
        }

        else {
            respuesta = EnvioPedido();
        }

            if(VerificarDevolucionesPendiente()>0) {
                respuesta = EnvioDevoluciones();
            }


            String json=jsonVisitas();
            if(json!=null)
                services.SincronizarVisitas(json);

            String jsonCierre=JSonCierreVisitas();
            String respjson=null;

            if(jsonCierre!=null);
            respjson= services.CierreVisitas(jsonCierre);
            if(respjson!=null)
                Extraer_json(respjson);



        if(respuesta.isEmpty()){
            respuesta="Fallo al envíar información";
        }

        return respuesta;
    }

    public String EnvioPedido(){

        String respuesta="";

        if (VerificarPedidosPendientes()) { //Verifica si se tienen pedidos pendientes

            if (isOnline() == false)
                 return "Verifique su conexión a Internet e intente nuevamente.";

            String cabecero = Objener_JsonCabecero();
            String detalle  = Obtener_Jsondetalle();


            String res = services.SincronizarPedidos(cabecero, detalle);

            if (res == null)
                return "Fallo al transmitir pedidos. Favor de verificar su conexión";

            ActualizarStatusPedido(res);

            if (VerificarPedidosPendientes() == false)

                return "Pedidos transmitidos correctamente";

        }
        else {

               respuesta = "No hay información por sincronizar";

        }


        return respuesta;
    }

    public String EnvioDevoluciones(){
        String respuesta="";

        if (VerificarDevolucionesPendiente()>0) { //Verifica si se tienen pedidos pendientes

              String jsonCabecero=Obtener_JsonCabeceroDev();
              String jsonDetalle=Obtener_JsonDetalleDev();

            if (isOnline() == false)
                return "Verifique su conexión a Internet e intente nuevamente.";

            WebServices webServices=new WebServices();

            String resp=webServices.UploadDevolucionesPdtes(jsonCabecero,jsonDetalle);

             if(resp!=null){

                 if(ProcesaJsonDev(resp)){
                     respuesta= "Devoluciónes transmitidas correctamente.";
                 }else {
                     respuesta= "Fallo al transmitir devoluciónes. Favor de verificar su conexión";
                 }

             }else{
                 respuesta= "Fallo al transmitir devoluciónes. Favor de verificar su conexión";
            }

        }else {
            respuesta= "No hay información por sincronizar";
        }

        return respuesta;
    }

    public int VerificarClientesPendientes(){

        CSQLite lt=new CSQLite(context);
        SQLiteDatabase db=lt.getReadableDatabase();
        int val=0;

        Cursor rs=db.rawQuery("select * from clientesDr where estatus == '10'", null);

        val=rs.getCount();

        db.close();
        lt.close();

        return val;
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

    public int VerificarDevolucionesPendiente(){

        CSQLite lt=new CSQLite(context);
        SQLiteDatabase db=lt.getReadableDatabase();
        int val;

        Cursor rs=db.rawQuery("select id_devolucion from DEV_Encabezado where status='10'",null);
        val=rs.getCount();

        id_devoluciones=new String[val];
        int contador=0;

        while (rs.moveToNext()){
            id_devoluciones[contador]=rs.getString(0);
            contador++;
        }

        db.close();
        lt.close();

        return val;
    }

    public String JSonClientes(){

        CSQLite lt=new CSQLite(context);
        SQLiteDatabase db=lt.getReadableDatabase();
        JSONArray array=new JSONArray();
        JSONObject object=new JSONObject();

        Cursor rs=db.rawQuery("select * from clientesDr where estatus <> 50 ",null);

        while (rs.moveToNext()){
            try {
                String id_largo=ObtenerIdCteLargo(rs.getString(0));
                object.put("id_cliente",id_largo);
                object.put("nombre",rs.getString(1));
                object.put("rfc",rs.getString(2));
                object.put("correo",rs.getString(3));
                object.put("telefono",rs.getString(4));
                object.put("cp",rs.getString(5));
                object.put("colonia",rs.getString(6));
                object.put("calle",rs.getString(7));
                object.put("calle1",rs.getString(8));
                object.put("calle2",rs.getString(9));
                object.put("referencia",rs.getString(10));
                object.put("no_exterior",rs.getString(11));
                object.put("delegacion",rs.getString(12));
                object.put("estado",rs.getString(13));
                object.put("almacen",rs.getString(14));
                object.put("ruta",rs.getString(15));
                object.put("no_interior",rs.getString(16));
                array.put(object);
                object=new JSONObject();

            } catch (JSONException e) {

                e.printStackTrace();

            }
        }
           return  array.toString();
    }

    public String ProcesaJson(String json){

        String resp="";

        try {

            JSONArray array=new JSONArray(json);
            int tam=array.length();

            if(tam>0) {

                for (int i = 0; i < tam; i++) {

                    JSONObject object = array.getJSONObject(i);
                    String id_cliente = object.getString("id_cliente");
                    String cteIbs = object.getString("id_cliente_ibs");

                    String id_corto=ObtenerIdCteCorto(id_cliente);

                    if(!id_cliente.isEmpty()) {
                        if (!cteIbs.isEmpty()) {
                               UpdateStatusCteDr(id_corto, cteIbs, "50");
                               UpdateCteSm(id_corto, cteIbs);
                               UpdateEncabezadoPedido(cteIbs, id_corto);
                               UpdateVisitas(cteIbs,id_corto);
                               UpdateAgendaPedido(cteIbs, id_corto);
                               resp="Clientes registrados exitosamente.";
                        }
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  resp;
    }

    public boolean ProcesaJsonDev(String json){

       boolean res=false;

        try {


            CSQLite lt=new CSQLite(context);
            SQLiteDatabase db=lt.getWritableDatabase();
            JSONArray array=new JSONArray(json);

            for (int i=0;i<array.length();i++){

                JSONObject obj=array.getJSONObject(i);
                String id=obj.getString("id_devolucion");
                String status=obj.getString("status");

                ContentValues values=new ContentValues();
                values.put("status",status);

                if(db!=null)
                    if(!db.isOpen())
                        db=lt.getWritableDatabase();

                int up=db.update("DEV_Encabezado",values,"id_devolucion=?",new String[]{id});

                if(up>0)
                    res=true;

            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return res;

    }

    public void Extraer_json(String json){

        String estatus="20";
        String id_visita="";
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();


        try {

            JSONArray array=new JSONArray(json);

            for(int i=0;i<array.length();i++){

                JSONObject object=new JSONObject(array.getJSONObject(i).toString());
                estatus=object.get("estatus_visita").toString();
                id_visita=object.get("id_visita").toString();
                db.execSQL("update visitas set status_visita='"+estatus+"' where id_visita='"+id_visita+"'");

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void UpdateStatusCteDr(String id,String idIBS,String estatus){

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("estatus", estatus);
        if(!idIBS.equals("")||!idIBS.equals(null))
            values.put("id_cliente",idIBS);

        long res=db.update("clientesDr", values, "id_cliente=?", new String[]{id});

        db.close();
        lite.close();

    }

    public void UpdateCteSm(String id,String idIBS){

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("id_cliente",idIBS);
        db.update("clientes", values, "id_cliente=?", new String[]{id});

        db.close();
        lite.close();

    }

    public void UpdateAgendaPedido(String newCliente,String oldCliente){

        CSQLite lite1=new CSQLite(context);
        SQLiteDatabase db=lite1.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("id_cliente",newCliente);

        try {

            long val=db.update("agenda", values, "id_cliente=?", new String[]{oldCliente});
            String a="Error";

        }catch (Exception e){
            String err=e.toString();
            e.printStackTrace();
        }


        db.close();
        lite1.close();

    }

    public void UpdateVisitas(String newCliente,String oldCliente){
        CSQLite lite1=new CSQLite(context);
        SQLiteDatabase db=lite1.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("id_cliente",newCliente);


        for (int j=0;j<4;j++) {

            int i = db.update("visitas", values, "id_cliente=?", new String[]{oldCliente});

            if(i>0) {
                break;
            }
            else {

                lite1=new CSQLite(context);
                db=lite1.getWritableDatabase();

                if(j==3){
                    try {
                        from = "envio_pedido";
                        subject="UpdateEncabezadoPedido";
                        body="Cliente nuevo: "+newCliente+"\n Cliente tem: "+ oldCliente +"\n BD: "+db.isOpen();
                        new sendEmail().execute("");
                    }catch (Exception e){ }}
                continue;
            } }
        db.close();
        lite1.close();
    }

    public void UpdateEncabezadoPedido(String newCliente,String oldCliente){
        CSQLite lite1=new CSQLite(context);
        SQLiteDatabase db=lite1.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("id_cliente",newCliente);

        for (int j=0;j<4;j++) {

            int i = db.update("encabezado_pedido", values, "id_cliente=?", new String[]{oldCliente});

            if(i>0) {
                break;
            }
            else {

                lite1=new CSQLite(context);
                db=lite1.getWritableDatabase();

                continue;

            }
        }

        db.close();
        lite1.close();
    }

    public String ObtenerIdCteLargo(String id){

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();
        String idCte="";

        Cursor rs=db.rawQuery("select id_largo from RelacionClientes where id_corto=?",new String[]{id});

        if(rs.moveToFirst()){

            idCte=rs.getString(0);

        }

        db.close();
        lite.close();

        return idCte;
    }

    public String ObtenerIdCteCorto(String id){

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();
        String idCte="";

        Cursor rs=db.rawQuery("select id_corto from RelacionClientes where id_largo=?",new String[]{id});

        if(rs.moveToFirst()){

            idCte=rs.getString(0);

        }

        db.close();
        lite.close();

        return idCte;
    }

    public String jsonVisitas(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select * from visitas where status_visita='10'",null);
        JSONArray array=new JSONArray();
        JSONObject object=new JSONObject();

        while (rs.moveToNext()){

            try {

                String id_cliente=rs.getString(1);

                if(VerificarEstatusCteDr(id_cliente)) {

                    object.put("numero_empleado", rs.getString(0));
                    object.put("id_cliente", rs.getString(1));
                    object.put("latitud", rs.getString(2));
                    object.put("longitud", rs.getString(3));
                    object.put("fecha_visita", rs.getString(4).replace(":", "|"));
                    object.put("fecha_registro", rs.getString(5).replace(":", "|"));
                    String id_visita = rs.getString(6);
                    object.put("id_visita", id_visita);
                    array.put(object);
                    object = new JSONObject();
                }


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

        Cursor rs=db.rawQuery("select numero_empleado from agentes where Sesion=1",null);
        if(rs.moveToFirst()){

            clave=rs.getString(0);
        }

        return clave;
    }

    public String ObtenerClaveEmpleado(){
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();
        String num="";
        String query="";

        query="select clave_agente from agentes where Sesion=1";
        Cursor rs = db.rawQuery(query, null);
        if (rs.moveToFirst()) {

            num = rs.getString(0);
        }

        return num;
    }

    public void EliminarEncabezado(String id_pedido){
        CSQLite lt=new CSQLite(context);
        SQLiteDatabase db=lt.getWritableDatabase();

        db.delete("encabezado_pedido","id_pedido=?",new String[]{id_pedido});
        db.close();
        lt.close();

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

              if(array.length()<=0){
                 EliminarEncabezado(id_pedidos[i]);
              }

          }


        return array.toString();
    }

    public String Obtener_JsonCabeceroDev(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs;

        JSONArray array=new JSONArray();
        JSONObject object;

        for(int i=0;i<id_devoluciones.length;i++) {

            rs= db.rawQuery("select sucursal,folio_hh,cliente,folio_dev_agente,tipo_documento,empleado, " +
                             "folio_documento_agente,fecha_creacion,fecha_creacion_txt,status,bultos,id_devolucion from DEV_Encabezado where id_devolucion='"+id_devoluciones[i]+"'",null);

            while (rs.moveToNext()) {

                try {

                    object=new JSONObject();
                    object.put("sucursal", rs.getString(0));
                    object.put("folio_hh", rs.getString(1));
                    object.put("cliente", rs.getString(2));
                    object.put("folio_dev_agente", rs.getString(3));
                    object.put("tipo_documento", rs.getString(4));
                    object.put("empleado", rs.getString(5));
                    object.put("folio_documento_agente", rs.getString(6));
                    object.put("fecha_creacion", rs.getString(7));
                    object.put("fecha_creacion_txt", rs.getString(8));
                    object.put("status", rs.getString(9));
                    object.put("bultos",rs.getString(10));
                    object.put("id_devolucion", rs.getString(11));
                    array.put(object);

                } catch (Exception e) {

                }
            }
        }
        return array.length()<=0?"":array.toString();
    }

    public String Obtener_JsonDetalleDev(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs;
        JSONObject json;
        JSONArray array=new JSONArray();

        for(int i=0;i<id_devoluciones.length;i++) {

            rs= db.rawQuery("select sucursal,folio_hh,codigo,motivo,cantidad,folio_dev_agente,id_devolucion from DEV_Detalle where id_devolucion='"+id_devoluciones[i]+"'",null);

            while (rs.moveToNext()) {

                json=new JSONObject();
                try {
                    json.put("sucursal", rs.getString(0));
                    json.put("folio_hh", rs.getString(1));
                    json.put("codigo", rs.getString(2));
                    json.put("motivo", rs.getString(3));
                    json.put("cantidad", rs.getString(4));
                    json.put("folio_dev_agente", rs.getString(5));
                    json.put("id_devolucion", rs.getString(6));
                    array.put(json);

                } catch (JSONException e) {
                    e.printStackTrace();
                }}}


        return  array.length()<=0?"":array.toString();
    }

    public String JSonCierreVisitas(){
        CSQLite lt=new CSQLite(context);
        SQLiteDatabase db=lt.getReadableDatabase();

        JSONArray array=new JSONArray();
        JSONObject object;

        Cursor rs=db.rawQuery("select id_visita,fecha_cierre,id_cliente from visitas where status_visita='10'",null);

        while (rs.moveToNext()){
            try{

                String fecha_cierre=rs.getString(1);
                String id_cliente=rs.getString(2);

                if(VerificarEstatusCteDr(id_cliente)) {
                    if (!fecha_cierre.isEmpty()) {
                        object = new JSONObject();
                        object.put("id_visita", rs.getString(0));
                        object.put("fecha_cierre", fecha_cierre == null ? "" : fecha_cierre.replace(":", "|"));
                        object.put("estatus_visita", "20");
                        array.put(object);
                    }
                }

            }catch (Exception e){
                return null;
            }
        }

        return array.length()==0?null:array.toString();
    }

    public boolean VerificarEstatusCteDr(String id_cte){

        CSQLite lite1=new CSQLite(context);
        SQLiteDatabase db=lite1.getReadableDatabase();

        Cursor rs=db.rawQuery("select * from clientesDr where id_cliente=?",new String[]{id_cte});

        if(rs.getCount()<=0){
            return true;
        }
        else{
            rs.close();
            rs=db.rawQuery("select * from clientesDr where id_cliente=? and estatus = 50",new String[]{id_cte});
            if(rs.getCount()>0){
                return true;
            }else {
                return false;
            }
        }
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

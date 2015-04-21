package com.marzam.com.appventas.Sincronizacion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.marzam.com.appventas.Email.Mail;
import com.marzam.com.appventas.MainActivity;
import com.marzam.com.appventas.MapsLocation;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.WebService.WebServices;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class Sincronizar extends Activity {

    Context context;
    File directorio;
    ProgressDialog progres;
    static  InputStream stream;
    CSQLite lite;
    TextView txtPedidos;
    envio_pedidoFaltante envioPedidoFaltante;
    String from="Sincronizar";
    String body;
    String subject;
    Mail m;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sincronizar);
        context=this;

        CrearDirectorioDownloads();
        txtPedidos=(TextView)findViewById(R.id.textView49);
        txtPedidos.setText(""+VerificarPedidosPendientes());

        Button btnCerrar=(Button)findViewById(R.id.button5);
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowCierreDia();
            }
        });

        Button btnEnviar=(Button)findViewById(R.id.button4);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new UpLoadTask_Envio().execute("");
                progres=ProgressDialog.show(context,"Transmitiendo pedidos","Cargando",true,false);

            }
        });




    }

    public int VerificarPedidosPendientes(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        int Cantidad=0;

        Cursor rs=db.rawQuery("select count(id_pedido) from encabezado_pedido where id_estatus=10",null);
        if(rs.moveToFirst()){
            Cantidad=rs.getInt(0);
        }

        db.close();
        lite.close();
        return Cantidad;
    }
    public boolean VerificarSesionActiva(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs= db.rawQuery("select id_cliente from sesion_cliente where Sesion=1",null);

        if(rs.moveToFirst()){
            return  true;//ya se encunetra una sesion activa
        }

            return false;
    }

    public void ShowCierreDia(){

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage("Desea hacer el cierre de día?");
        alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!VerificarSesionActiva()) {
                    if (VerificarPedidosPendientes() <= 0) {
                        if (isOnline()) {

                            //Estas lineas son del actual AsyncTask, se comentan para probar la nueva sincronización
                           progres = ProgressDialog.show(context, "Sincronizando", "Cargando", true, false);
                            new UpLoadTask().execute("");

                           /* new Task_Json().execute(); //Sincronización inteligente*/



                        } else {
                        Toast.makeText(context, "Verifique su conexión a internet e intente nuevamente", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(context, "No se puede completar el cierre. Envíe sus pedidos pendientes", Toast.LENGTH_LONG).show();
                    }
                }else {
                        Toast.makeText(context,"Tiene una visita activa.Cierre la visita para poder completar la sincronización",Toast.LENGTH_LONG).show();
                }
            }
        });
        alert.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog= alert.create();
        alertDialog.show();
    }
    public void CrearDirectorioDownloads(){

        try {
            File folder = android.os.Environment.getExternalStorageDirectory();
            directorio = new File(folder.getAbsolutePath() + "/Marzam/preferencias");
            if (directorio.exists() == false) {
                directorio.mkdirs();
            }
        }catch (Exception e){
            Log.d("ErrorCrearDir", e.toString());
        }
    }
    public void unZipBD(String origen){

        try{

            ZipFile zipFile=new ZipFile(origen);
            zipFile.extractAll(directorio.toString());


        }catch (ZipException e){
            String err=e.toString();
            Log.d("Fail ExtractZip:",err);
        }

    }
    public void ZipBD(String origen,String destino){

        File urlorigen=new File(origen);
        ZipFile zipfile=null;

        try {
            zipfile=new ZipFile(destino);
        }catch (Exception e){
            e.printStackTrace();
        }
        ZipParameters parameters=new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        try {

            zipfile.addFile(urlorigen,parameters);

        }catch (Exception e){

        }

    }


    public static byte[] StreamZip(File file){



        try {
            stream = new FileInputStream(file);
        }catch (Exception e){
            String a=e.toString();
            Log.d("ConvertStream:",a);
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
    public void  unStreamZip(byte[] data){

        String result=directorio+"/db_down.zip";
        try{

            File of =new File(directorio,"db_down.zip");
            FileOutputStream osf=new FileOutputStream(of);
            osf.write(data);
            osf.flush();

        }catch (Exception e){
            String error=e.toString();
            Log.d("Error al crear zip",e.toString());
        }

    }


    public void CopiarBD(String nombreBack){

        byte[] buffer=new byte[1024];
        OutputStream myOutput=null;
        int length;
        InputStream myInput=null;

        try{
            File filebd=new File("/data/data/com.marzam.com.appventas/databases/db.db");
            File fileBack=new File(directorio+"/"+nombreBack+".zip");
            File filedown=new File(directorio+"/db.db");
            if(fileBack.exists()==true){
                fileBack.delete();
            }

            ZipBD(filebd.toString(), fileBack.toString()); //comprime
            unZipBD(directorio + "/db_down.zip");

            myInput=new FileInputStream(filedown);

            myOutput=new FileOutputStream("/data/data/com.marzam.com.appventas/databases/db.db");
            while ((length=myInput.read(buffer))>0){
                myOutput.write(buffer,0,length);
            }
            myOutput.close();
            myOutput.flush();
            myInput.close();
            filedown.delete();

        }catch (Exception e){
            String error=e.toString();
            Log.d("Error al copiar BD",error);
        }

    }//Crea back de DB descromprime la cargada y la remplaza por la base anterior
    public void AgregarColumnProductos(){

        lite=new CSQLite(context);

        SQLiteDatabase db=lite.getWritableDatabase();

        String[] query={"ALTER TABLE productos ADD COLUMN isCheck int DEFAULT 0","ALTER TABLE productos ADD COLUMN Cantidad int DEFAULT 0 ",
                "ALTER TABLE agentes ADD COLUMN   Sesion int DEFAULT 0","ALTER TABLE productos ADD COLUMN precio_final varchar(50)",
                "ALTER TABLE visitas ADD COLUMN status_visita varchar(50) "};

        for(int i=0;i<query.length;i++) {

            try {

                db.execSQL(query[i]);

            } catch (Exception e) {
                String err = e.toString();
                Log.d("Error:", err);
                continue;
            }
        }

        db.close();
        lite.close();
    }

    private class UpLoadTask_Envio extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {

            envioPedidoFaltante=new envio_pedidoFaltante();
            String resp =envioPedidoFaltante.Enviar(context);

            return resp;
        }

        @Override
        protected void onPostExecute(Object result){

            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setTitle("Envio de pedido");

            if(progres.isShowing()) {
                txtPedidos.setText("" + VerificarPedidosPendientes());
                progres.dismiss();

                alert.setMessage(result.toString());
                alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog=alert.create();
                alertDialog.show();
            }


        }
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
    private String getDate(){

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("ddMMyyyy");
        String formatteDate=df.format(dt.getTime());

        return formatteDate;
    }
    public String getDate2(){
        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        String formatteDate=df.format(dt.getTime());


        return formatteDate;
    }//formato dd/mm/yyyy

    private class UpLoadTask extends AsyncTask<String,Void,Object> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(String... strings) {
            WebServices web=new WebServices();
            String nomAgente=ObtenerAgenteActivo();
            String archivoBack=nomAgente+getDate();


            String json=jsonVisitas();
            String visita;
            visita = json==null ? null:web.SincronizarVisitas(json);

            String json_cierre=jsonCierreVisitas();
            visita = json_cierre==null?"":web.CierreVisitas(json_cierre);

            File back=new File(directorio+"/"+archivoBack+".zip");
            String bd64=web.Down_DB(nomAgente+".zip");
            if(!back.exists())
                   EscribirTXT();

            if(back.exists()){

                Object archivo=web.cargarBack(directorio + "/" + archivoBack + ".zip", archivoBack + ".zip");

                if(archivo!=null)
                       back.delete();

                return archivo;
            }

            if(bd64==null)
                  return null;

            byte[] data = new byte[0];

            try {
                 data = Base64.decode(bd64);
            }catch (Exception e){
                return null;
            }

            unStreamZip(data);
               File f=new File(directorio+"/db_down.zip");

            if(!f.exists())
                  return null;



            CopiarBD(archivoBack);

            if(f.exists())
                  f.delete();

            AgregarColumnProductos();
            LeerTxt();
            Object archivo=web.cargarBack(directorio + "/" + archivoBack + ".zip", archivoBack + ".zip");

               if(archivo!=null)
                   back.delete();

            return archivo;
        }


        @Override
        protected void onPostExecute(Object result){

            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setTitle("Sincroinización");


            if(progres.isShowing()) {
                progres.dismiss();
                if(result==null) {
        alert.setMessage("Error al realizar cierre de día. Desea intentar nuevamente?");
                    alert.setPositiveButton("Si",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progres=ProgressDialog.show(context,"Sincronizando","cargando",true,false);
                            new UpLoadTask().execute("");

                        }
                    });
        alert.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
                }
                else {
           alert.setMessage("Se completo el cierre de día correctamente");
           alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {

                   startActivity(new Intent(getBaseContext(), MainActivity.class)
                           .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP ));
                   finish();

               }
           });
                }
           AlertDialog alertDialog=alert.create();
                alertDialog.show();
            }
        }

    }//Asynck de la actual sincronización
    private class Task_Json extends  AsyncTask<String,Integer,Object>{



        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);

            progres.incrementProgressBy(value[0]);


        }
        @Override
        protected void onPreExecute() {

            //make the progressDialog
            progres = new ProgressDialog(context);
            progres.setTitle("Sincronizando ...");
            progres.setIndeterminate(false);
            progres.setCancelable(false);
            progres.setMessage("Descargando información...");
            progres.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progres.setProgress(0);
            progres.setMax(100);
            progres.show();


        }
        @Override
        protected Object doInBackground(String... strings) {


            WebServices web=new WebServices();
            String json=web.DownJson();

            if(json==null)
                 return null;

            String[] jsonTable=json.split("\\*");

            int div=(jsonTable.length/3); //Obtiene el numero de json que se van a procesar
            String table = null;
            String id=null;

            int contador=0;
            int contador2=0;

            for(int i=1;i<jsonTable.length;i++){//Recorre el arreglo que contiene el nombre de la tabla y el json

               if(contador==0){
                 id=jsonTable[i];
                   contador++;
               }
               else{
                if(contador==1){   //Si el mod de i es 0 corresponde al dato del cabecero
                    table=jsonTable[i];

                    float val=(contador2*100);
                    float res=val/div;
                    double mod=res%2;

                    if(mod==1 || mod ==0)
                    publishProgress(1);

                    contador2++;
                    contador++;
                }//Recorre el nombre de la tabla
                else {  //Si el mod de i es 1 corresponde al json
                    //Recorremos los Json

                    try {

                        JSONArray array = new JSONArray(jsonTable[i]); //se crea un array con el json
                        for (int i1 = 0; i1 < array.length(); i1++) { //Recorre cada elemento del json

                            JSONObject object = array.getJSONObject(i1);
                            Iterator interator = object.keys();

                            String[][] arreglo = new String[object.length() - 1][2];
                            int operacion = 0;
                            int con = 0;

                            while (interator.hasNext()) {

                                String key = (String) interator.next();

                                if (!key.equals("ID_SINC")) {

                                    if(!key.equals("fecha_actualizacion")) {
                                        arreglo[con][0] = key;
                                        arreglo[con][1] = object.getString(key);
                                    }else {
                                        arreglo[con][0] = key;
                                        arreglo[con][1] = getDate2();
                                    }

                                    con++;

                                }
                                else {
                                    operacion = Integer.parseInt(object.getString(key));
                                }

                            }//cierre de while


                            switch (operacion) {
                                case 1:
                                    Insertar(table, arreglo);
                                    break;
                                case 2:
                                    Eliminar(table, arreglo);
                                    break;
                                case 3:
                                    Actualizar(table, arreglo);
                                    break;
                            }//cierre de switch
                        }

                    } catch (JSONException e) {
                        subject="Actualizar()";
                        body="Agente:"+ObtenerAgenteActivo()
                                +"Error:"+e.toString()
                                +"Arreglo: "+jsonTable;
                        new sendEmail().execute("");
                    }
                    contador = 0;
                }
                }//Recorre los json

            }//Cierre del for

            return json;
        }
        @Override
        protected void onPostExecute(Object res){

            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setTitle("Sincronización");

            if(progres.isShowing()){

                if(res==null){
                 publishProgress(0);
                 alert.setMessage("No se pudo completar la sincronización.Desea intentar nuevamente");
                 alert.setPositiveButton("Si",new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         new Task_Json().execute("");
                     }
                 });
                 alert.setNegativeButton("No",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

                }//En caso de que haya fallado la comunicación con el web service

                else{

                alert.setMessage("Se ha completado la sincronización.");
                alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                }

                    progres.dismiss();
                    AlertDialog alertDialog=alert.create();
                    alertDialog.show();


            }//en caso de que el Dialogo este visible
        }




        //Metodos de actualización
        private void Insertar(String table,String[][] arreglo){

            lite=new CSQLite(context);
            SQLiteDatabase db=lite.getWritableDatabase();

            try{

                ContentValues values=new ContentValues();

                for(int i=0;i<arreglo.length;i++){
                    values.put(arreglo[i][0],arreglo[i][1]);
                }

              // long val=db.insert(table,null,values);

                for(int i=0;i<4;i++) {
                    try {

                        long val = db.insertOrThrow(table, null, values);
                        String a="";
                        break;

                    } catch (SQLException e) {

                        subject="Insertar()";
                        body="Agente:"+ObtenerAgenteActivo()
                            +"Error:"+e.toString()
                            +"Tabla:"+table
                            +"Values: "+values.toString();
                              new sendEmail().execute("");
                        continue;

                    }
                }



            }catch (Exception e){

                String error=e.toString();
                e.printStackTrace();

            }finally {
                if(db!=null)
                    db.close();
                if(lite!=null)
                    lite.close();
            }
        }
        private void Actualizar(String table,String[][] arreglo){

            lite=new CSQLite(context);
            SQLiteDatabase db=lite.getWritableDatabase();

            String llave = null;
            String valor = null;

            if(table.toLowerCase().equals("productos"))
                llave="codigo";
            if(table.toLowerCase().equals("ofertas"))
                llave="codigo,metodo";
            if(table.toLowerCase().equals("clientes"))
                llave="id_cliente";



            try{
                 String[] WhereKey=llave.split(",");
                 ContentValues values=new ContentValues();

                if(WhereKey.length<=1) { //Se ejecuta el comando en caso de que solo necesite una llave para identificar el valor
                    for (int i = 0; i < arreglo.length; i++) {
                        values.put(arreglo[i][0], arreglo[i][1]);

                        if (arreglo[i][0].equals(llave)) {
                            valor = arreglo[i][1];
                        }//Busca la llave y extrae su valor

                    }//Recorre el arreglo y llena el ContentValues

                    int val = db.update(table, values, llave + "=" + "'" + valor + "'", null);
                    String a="";

                }//if
                else {

                    StringBuilder build=new StringBuilder();
                    String[] args=new String[WhereKey.length];

                    for(int i=0;i<arreglo.length;i++){
                        values.put(arreglo[i][0], arreglo[i][1]);

                        for(int j=0;j<WhereKey.length;j++){

                            if(WhereKey[j].equals(arreglo[i][0])){

                                args[j]=arreglo[i][1];

                                if(j<WhereKey.length-1)
                                    build.append(WhereKey[j]+"=? AND ");
                                else
                                    build.append(WhereKey[j]+"=? ");
                            }

                        }
                    }//cierre del for

                    int val = db.update(table, values, build.toString(), args);
                    String a="";


                    }//cierre del else


            }catch (Exception e){

                subject="Actualizar()";
                body="Agente:"+ObtenerAgenteActivo()
                        +"Error:"+e.toString()
                        +"Tabla:"+table
                        +"Llave: "+llave;
                new sendEmail().execute("");

            }   finally
            {

                 if(db!=null)
                     db.close();
                if(lite!=null)
                    lite.close();

            }

        }
        private void Eliminar(String table,String[][] arreglo){

            lite=new CSQLite(context);
            SQLiteDatabase db=lite.getWritableDatabase();

            String llave = null;
            String valor = null;

            if(table.toLowerCase().equals("productos"))
                llave="codigo";
            if(table.toLowerCase().equals("ofertas"))
                llave="codigo,metodo";
            if(table.toLowerCase().equals("clientes"))
                llave="id_cliente";



            try{

            String[] WhereKey=llave.split(",");

            if(WhereKey.length<=1) { //Se ejecuta el comando en caso de que solo necesite una llave para identificar el valor
                for (int i = 0; i < arreglo.length; i++) {
                    if (arreglo[i][0].equals(llave)) {
                        valor = arreglo[i][1];
                    }//Busca la llave y extrae su valor
                }//Recorre el arreglo y llena el ContentValues

                int val = db.delete(table, llave + "=" + "'" + valor + "'", null);
                String a="";

                                  }//Fien del if

            else {

                StringBuilder build=new StringBuilder();
                String[] args=new String[WhereKey.length];

                for(int i=0;i<arreglo.length;i++){

                    for(int j=0;j<WhereKey.length;j++){

                        if(WhereKey[j].equals(arreglo[i][0])){

                            args[j]=arreglo[i][1];

                            if(j<WhereKey.length-1)
                               build.append(WhereKey[j]+"=? AND ");
                            else
                              build.append(WhereKey[j]+"=? ");
                        }

                    }
                }//cierre del for

                   int val=db.delete(table,build.toString(),args);
                   String a="";


                                         }//cierre del else


            }catch (Exception e){

                subject="Actualizar()";
                body="Agente:"+ObtenerAgenteActivo()
                        +"Error:"+e.toString()
                        +"Tabla:"+table
                        +"Llave: "+llave;
                new sendEmail().execute("");

            }   finally
            {

                if(db!=null)
                    db.close();
                if(lite!=null)
                    lite.close();

            }

        }




    }//Asynck de la sincronización inteligente

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
               subject="jsonVisitas()";
               body="Array:"+array+"\nObject:"+object+"\nError:"+e.toString();
               new sendEmail().execute("");
            }


        }

        return array.length()==0 ? null: array.toString();

    }
    public String jsonCierreVisitas(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();


        Cursor rs=db.rawQuery("select id_visita,status_visita,fecha_cierre from visitas where status_visita in ('10','20')",null);

        JSONArray array=new JSONArray();
        JSONObject object=new JSONObject();

        while (rs.moveToNext()){

            try {

                object.put("id_visita",rs.getString(0));
                object.put("estatus_visita",rs.getString(1));
                object.put("fecha_cierre",rs.getString(2).replace(":","|"));
                array.put(object);
                object=new JSONObject();

            } catch (JSONException e) {
                subject="jsonCierreVisitas()";
                body="Array:"+array+"\nObject:"+object+"\nError:"+e.toString();
                new sendEmail().execute("");
                array=null;
            }

        }




        return  array.length()==0 ? null: array.toString();
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        if (progres!=null){
            progres.dismiss();
            progres=null;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sincronizar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public  boolean isOnline(){

        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo !=null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }
    public void LeerTxt(){
        try {
            InputStreamReader archivo=new InputStreamReader(openFileInput("datos.txt"));
            BufferedReader br=new BufferedReader(archivo);

            String agente=br.readLine();

           ActualizarBD(agente);


        } catch (Exception e) {
          String   Err=e.toString();
            e.printStackTrace();
        }

    }
    public void EscribirTXT(){

        try{

            OutputStreamWriter writer2=new OutputStreamWriter(openFileOutput("datos.txt",Context.MODE_PRIVATE));
            String agente=ObtenerAgenteActivo();
            writer2.write(agente);
            writer2.close();
          //ObtenerArchivos2();
        }catch (Exception e){

        }

    }
    public void ActualizarBD(String agente){

        lite=new CSQLite(context);

        SQLiteDatabase db2=lite.getWritableDatabase();
        db2.execSQL("update agentes set Sesion=1 where clave_agente='"+agente+"'");
        db2.close();



    }


    public void ObtenerArchivos(){

        File directorio = new File("/data/data/com.marzam.com.appventas/files");
        File[] files=directorio.listFiles();


        CopiarArchivos(files);
    }
    public void CopiarArchivos(File[] files){
        byte[] buffer=new byte[1024];
        int length;
        FileOutputStream myOuput=null;
        try {

            FileInputStream myInput=null;

            File folder = android.os.Environment.getExternalStorageDirectory();
            File directorio2 = new File(folder.getAbsolutePath() + "/Marzam/preferencias");



            for(int i=0;i<files.length;i++){
                try {
                    myInput = new FileInputStream(files[i]);
                    String archivo = files[i].getName();
                    myOuput = new FileOutputStream(directorio2 + "/" + archivo);
                    while ((length = myInput.read(buffer)) > 0) {
                        myOuput.write(buffer, 0, length);
                    }


                    myInput.close();
                }catch (Exception e){continue;}

            }
            myOuput.close();
            myOuput.flush();
        }
        catch (Exception e){
            String err=e.toString();
            Log.e("ErrorCopiar:",e.toString());
        }
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(getBaseContext(), MapsLocation.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();

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

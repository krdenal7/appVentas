package com.marzam.com.appventas.Sincronizacion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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

import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.WebService.WebServices;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Sincronizar extends Activity {

    Context context;
    File directorio;
    ProgressDialog progres;
    static  InputStream stream;
    CSQLite lite;
    TextView txtPedidos;
    envio_pedidoFaltante envioPedidoFaltante;


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

    public void ShowCierreDia(){

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage("Desea hacer el cierre de día?");
        alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(VerificarPedidosPendientes()<=0) {
                    if(isOnline()) {
                        new UpLoadTask().execute("");
                        progres = ProgressDialog.show(context, "Realizando cierre", "Cargando", true, false);
                    }else {
                        Toast.makeText(context,"Verifique su conexión a internet e intente nuevamente",Toast.LENGTH_LONG).show();
                          }
                }else{
                    Toast.makeText(context,"No se puede completar el cierre. Envíe sus pedidos pendientes",Toast.LENGTH_LONG).show();
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

            if(progres.isShowing())
            txtPedidos.setText(""+VerificarPedidosPendientes());
            progres.dismiss();
            Toast.makeText(context,result.toString(),Toast.LENGTH_SHORT).show();

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

    private class UpLoadTask extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {
            WebServices web=new WebServices();
            String archivoBack=ObtenerAgenteActivo()+getDate();
            CopiarBD(archivoBack);
            AgregarColumnProductos();
            Object archivo=web.Upload_BD(directorio+"/"+archivoBack+".zip",archivoBack+".zip");

            return archivo;
        }

        @Override
        protected void onPostExecute(Object result){

            if(progres.isShowing()) {
                progres.dismiss();
                if(result==null)
                      Toast.makeText(context,"Error al realizar cierre. Intente nuevamente",Toast.LENGTH_SHORT).show();
                else
                      Toast.makeText(context,"Se completo el cierre de día correctamente",Toast.LENGTH_SHORT).show();
            }
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
}

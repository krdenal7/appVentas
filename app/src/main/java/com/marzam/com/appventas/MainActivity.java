package com.marzam.com.appventas;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.marzam.com.appventas.SQLite.CSQLite;

import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;


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
import java.util.Locale;



public class MainActivity extends Activity {

    Context context;
    Toast presMenu;
    boolean press=false;

    private GoogleCloudMessaging gcm;
    private String regid;
    AccountManager accountManager;
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_EXPIRATION_TIME = "onServerExpirationTimeMs";
    private static final String PROPERTY_USER = "user";

    public static final long EXPIRATION_TIME_MS = 1000 * 3600 * 24 * 7;
    String SENDER_ID = "1006732471487";
    static final String TAG = "Marzam-Push:";

    ProgressDialog pd;

    String telefono;
    String correo;

    File directorio;
  static  InputStream stream;

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context=this;


      locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);

      ShowEnableGPS();//Muestra el alert en caso de que el GPS del dispositivo se encuentre desactivado
      CrearDirectorioDownloads();//Crea el directorio donde se descargaran todos los archivos del Webservice



      final  Intent i=new Intent(context,MapsLocation.class);

        Button btnAceptar=(Button)findViewById(R.id.button);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //    gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
            //    regid = getRegistrationId(context, "Isaac");

           /*     if (regid.equals("")) {
                    TareaRegistroGCM tarea = new TareaRegistroGCM();
                    tarea.execute("");
                    pd = ProgressDialog.show(context, "Por favor espere", "Registrando en servidor", true, false);
                }*/
                startActivity(i);
            }
        });
    }


    public void ShowEnableGPS(){
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setTitle("Aviso");
            alert.setMessage("Desea activar el GPS");
            alert.setPositiveButton("SI",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent settingIntent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    settingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    startActivity(settingIntent);

                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog alertDialog=alert.create();
            alertDialog.show();
        }
    }

    public void CrearDirectorioDownloads(){

        try {
            File folder = android.os.Environment.getExternalStorageDirectory();
            directorio = new File(folder.getAbsolutePath() + "/Marzam/preferencias");
            if (directorio.exists() == false) {
                directorio.mkdirs();
            }
        }catch (Exception e){
            Log.d("ErrorCrearDir",e.toString());
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
        parameters.setPassword("Marzam1.");
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

    public void CopiarBD(){

        byte[] buffer=new byte[1024];
        OutputStream myOutput=null;
        int length;
        InputStream myInput=null;

        try{
            File filebd=new File("/data/data/com.marzam.com.appventas/databases/db.db");
            File fileBack=new File(directorio+"/dbBackup.zip");
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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){

       switch (keyCode){

           case KeyEvent.KEYCODE_VOLUME_UP:
               Vibrator v=(Vibrator)getSystemService(getApplicationContext().VIBRATOR_SERVICE);
               v.vibrate(2000);
               press=true;
               return true;

           case KeyEvent.KEYCODE_MENU:
               if(press==true)
               (presMenu=Toast.makeText(context,"Correcto",Toast.LENGTH_SHORT)).show();
               press=false;
               return true;

       }




        return super.onKeyDown(keyCode,event);
    }

    @Override
    public void onBackPressed(){
        finish();
    }


    private class TareaRegistroGCM extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params)
        {
            String msg = "";

            try
            {


                if (gcm == null)
                {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }

                //Nos registramos en los servidores de GCM
                regid = gcm.register(SENDER_ID);



                Log.d(TAG, "Registrado en GCM: registration_id=" + regid);


                setRegistrationId(context, params[0], regid);

            }
            catch (IOException ex)
            {
                Toast t=Toast.makeText(context,"Error al conectar Intente mas tarde",Toast.LENGTH_LONG);
                t.show();
            }
            pd.dismiss();
            return msg;
        }

    }





    /*Registro-PUSH*/
    private String getRegistrationId(Context context,String usuario)  {
        SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.length() == 0)
        {
            Log.d(TAG, "Registro GCM no encontrado.");
            return "";
        }

        String registeredUser =
                prefs.getString(PROPERTY_USER, "user");

        int registeredVersion =
                prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);

        long expirationTime =
                prefs.getLong(PROPERTY_EXPIRATION_TIME, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String expirationDate = sdf.format(new Date(expirationTime));

        Log.d(TAG, "Registro GCM encontrado (usuario=" + registeredUser +
                ", version=" + registeredVersion +
                ", expira=" + expirationDate + ")");

        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion)
        {
            Log.d(TAG, "Nueva versión de la aplicación.");
            return "";
        }
        else if (System.currentTimeMillis() > expirationTime)
        {
            Log.d(TAG, "Registro GCM expirado.");
            return "";
        }
        else if (!usuario.equals(registeredUser))
        {
            Log.d(TAG, "Nuevo nombre de usuario.");
            return "";
        }

        return registrationId;
    }
    private int getAppVersion(Context context) {

        try{

            PackageInfo packageInfo=context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),0);
            return packageInfo.versionCode;
        }catch (PackageManager.NameNotFoundException e){

            throw new RuntimeException("Error al obtener version"+ e);
        }
    }
    private void setRegistrationId(Context context, String user, String regId){
        SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);

        int appVersion = getAppVersion(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_USER, user);
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.putLong(PROPERTY_EXPIRATION_TIME,
                System.currentTimeMillis() + EXPIRATION_TIME_MS);

        editor.commit();
    }


    /*Obtener datod del Telefono*/

    /*OBTENER DATOS DE USUARIOS*/

    private String[] getAccountNames(){

        accountManager= AccountManager.get(this);
        Account[] accounts=accountManager.getAccountsByType(
                GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names=new String[accounts.length];
        for(int i=0;i<names.length;i++){
            names[i]= accounts[i].name;
        }
        return names;
    }
    private String getPhoneNumber(){
        TelephonyManager telephonyManager;
        telephonyManager=(TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);

        return telephonyManager.getLine1Number();
    }


    /*Obtener hora actual del dispositivo*/

       private String getDate(){

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy");
        String formatteDate=df.format(dt.getTime());

        return formatteDate;
    }

    /*Comprobar fecha para el cambio de la Base de Datos*/




}


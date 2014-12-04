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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.WebService.WebServices;

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
import java.util.Objects;


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
    LocationManager locationManager;

    CSQLite lite;
    String password;
    String nombre_agente;
    String[] clave_agente;
    TextView txtUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context=this;

        txtUsuario=(TextView)findViewById(R.id.textView);
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        CrearDirectorioDownloads();


        if(ExistsBD()) {
            MostrarDatos_Agente();
        }else {
            if(isOnline()) {
                new Task_DownBD().execute("");
                pd = ProgressDialog.show(context, "Obteniendo información de rutas", "Cargando", true, false);
            }else {
                Toast.makeText(context,"Verifique su conexión de Internet.",Toast.LENGTH_SHORT).show();
            }
        }






      ShowEnableGPS();//Muestra el alert en caso de que el GPS del dispositivo se encuentre desactivado

      final  Intent i=new Intent(context,MapsLocation.class);

        Button btnAceptar=(Button)findViewById(R.id.button);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //    gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
            //    regid = getRegistrationId(context, "Isaac");

            //     if (regid.equals("")) {
            //         TareaRegistroGCM tarea = new TareaRegistroGCM();
            //         tarea.execute("");
            //        pd = ProgressDialog.show(context, "Por favor espere", "Registrando en servidor", true, false);
            //    }

                password=((EditText)findViewById(R.id.editText)).getText().toString();

                if(VerificaContraseña(password,nombre_agente)){
                    startActivity(i);
                }else {
                   Toast toast=Toast.makeText(context,"Password incorrecto",Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
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


    public boolean VerificaContraseña(String password,String nombre){

        String[] valor={password,nombre};

       lite=new CSQLite(context);
       lite.getDataBase();

        SQLiteDatabase db=lite.getWritableDatabase();

        String query="Select * From agentes where numero_empleado=? and nombre=?";
        Cursor cursor = db.rawQuery(query,valor);

        while (cursor.moveToFirst()){
            db.close();
            lite.close();
            return true;
        }



        return false;
    }
    public void    CambiarEstatus(String password){

        String[] valor={password};

        lite=new CSQLite(context);

        SQLiteDatabase bd=lite.getWritableDatabase();

        try {
            Cursor cursor=bd.rawQuery("update agentes set id_estatus=1 where numero_empleado=?",valor);
        }catch (Exception e){
            String err="BD:"+e.toString();
            String error;
        }





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

    @Override
    protected void onResume(){
        super.onResume();
        EditText txtPas=(EditText)findViewById(R.id.editText);
        txtPas.setText("");
    }


    /*Comprobar Ruta y Base de datos*/
    public void EliminarBD(){

        try {

            File filebd = new File("/data/data/com.marzam.com.appventas/databases/db.db");
            filebd.delete();

        }catch (Exception e){
            String err=e.toString();
            Log.d("Error al eliminar BD",err);
        }

    }//Pruebas
    public boolean ExistsBD(){

        File file=new File("/data/data/com.marzam.com.appventas/databases/db.db");

        if(file.exists()) {
            return true;
        }else {
            return false;
        }

    }  //Verifica si existe la base de Datos
    public void MostrarDatos_Agente(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select numero_empleado,nombre from agentes where Sesion=1",null);
        String usuario="";

        if(rs.moveToFirst()){
            usuario=rs.getString(0)+"\n"+rs.getString(1);
            txtUsuario.setText(usuario);
            nombre_agente=rs.getString(1);
        }else{
            Show_SelectRutaAgente();
        }
    } //Si existe la base de datos y esta seleccionado el usuario lo mostrara en la pantalla
    public String[] Obtener_Agentes(){
        String[] agentes=null;

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select nombre,numero_empleado from agentes",null);

        agentes=new String[rs.getCount()];
        clave_agente=new String[rs.getCount()];
        int cont=0;
        while (rs.moveToNext()){
            agentes[cont]=rs.getString(0);
            clave_agente[cont]=rs.getString(1);
            cont++;
        }

        return  agentes;
    }  //Obtiene los nombres de los agentes para que sea seleccionado el que se requiera
    public void ActualizarSesionAgente(String clave){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        db.execSQL("update agentes set Sesion=1 where numero_empleado='"+clave+"'");
        db.close();
        lite.close();
    }

    public void Show_SelectRutaAgente(){

        String[] usuarios=Obtener_Agentes();

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Seleccione un usuario");
        alert.setItems(usuarios, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 ActualizarSesionAgente(clave_agente[i]);
                 MostrarDatos_Agente();
            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();
    } //Si no esta configurado un usuario mostrara la ventana para que sea seleccionado uno

    private class Task_DownBD extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {
            WebServices web=new WebServices();
            if(CopiarBD()) {
                AgregarColumnProductos();
            }else {
                return "0";
            }
            File file=new File(directorio+"/dbBackup.zip");
            //    Object archivo=web.Down_BD(StreamZip(file));

            if(ExistsBD())
                  return "1";
            else
                  return  "0";


        }

        @Override
        protected void onPostExecute(Object result){

            if(pd.isShowing()) {

                if(result=="1"){
                    Show_SelectRutaAgente();
                }else {
                    Toast.makeText(context,"Error al agregar la base de datos. Verifique su conexion a Internet e intente nuevamente",Toast.LENGTH_SHORT).show();
                }

                pd.dismiss();
            }
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

    public boolean CopiarBD(){

        byte[] buffer=new byte[1024];
        OutputStream myOutput=null;
        int length;
        InputStream myInput=null;

        try{

            File filebd=new File("/data/data/com.marzam.com.appventas/databases/db.db");
            File filedown=new File(directorio+"/db.db");
            unZipBD(directorio + "/db_down.zip");
            myInput=new FileInputStream(filedown);
            myOutput=new FileOutputStream("/data/data/com.marzam.com.appventas/databases/db.db");
            while ((length=myInput.read(buffer))>0){
                myOutput.write(buffer,0,length);
            }
            myOutput.close();
            myOutput.flush();
            myInput.close();
           return true;

        }catch (Exception e){
            String error=e.toString();
            Log.d("Error al copiar BD",error);
            return false;
        }
    }
    public void AgregarColumnProductos(){

        lite=new CSQLite(context);

        SQLiteDatabase db=lite.getWritableDatabase();
        try {

            db.execSQL("ALTER TABLE productos ADD COLUMN isCheck int DEFAULT 0");
            db.execSQL("ALTER TABLE productos ADD COLUMN Cantidad int DEFAULT 0 ");
            db.execSQL("ALTER TABLE agentes ADD COLUMN Sesion int DEFAULT 0");
        }catch (Exception e){
            String err=e.toString();
            Log.d("Error:",err);
        }

        db.close();
        lite.close();

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

    /*Comprobar si el dispositivo tiene conexión a Internet*/

    public  boolean isOnline(){

        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo !=null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }





}


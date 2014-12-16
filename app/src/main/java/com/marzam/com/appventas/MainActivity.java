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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.WebService.WebServices;


import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;


import org.kobjects.base64.Base64;

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
    boolean press=false;
    boolean isPress=false;

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
        setTitle("Ventas");
        context=this;




        txtUsuario=(TextView)findViewById(R.id.textView);
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        CrearDirectorioDownloads();
        //EliminarBD();

        if(ExistsBD()) {
                MostrarDatos_Agente();
        }else {
            if(isOnline()) {

                  Show_IngresarUsuario();

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
    public void Show_IngresarUsuario(){

        LayoutInflater inflate=getLayoutInflater();
        final View view=inflate.inflate(R.layout.input_text,null);


        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Ingrese su numero de agente");
        alert.setView(view);
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String agente=((EditText)view.findViewById(R.id.editText2)).getText().toString();

                if(!ExistsBD()) {
                    new Task_DownBD().execute(agente);
                    pd = ProgressDialog.show(context, "Obteniendo información de rutas", "Cargando", true, false);
                }else{
                    ActualizarSesionAgente(agente);
                    MostrarDatos_Agente();
                }

            }
        });
        alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();


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



    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){

         if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){

             if(press==true);
                     press=false;
             if(press==false)
                     press=true;

             return true;
         }

        if(keyCode==KeyEvent.KEYCODE_MENU){


            if(press==true && isPress==false){
                isPress=true;
            }

            return true;
        }

        if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){

            if(press==true && isPress==true) {

                Show_IngresarUsuario();
            }
            else
                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();

            press=false;
            isPress=false;

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

    @Override
    protected void onPause(){
        super.onPause();
        finish();
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
            Show_IngresarUsuario();
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
        db.execSQL("update agentes set Sesion=0");
        db.close();
        db=lite.getWritableDatabase();
        db.execSQL("update agentes set Sesion=1 where clave_agente='"+clave+"'");
        db.close();
        lite.close();
    }
    public void  unStreamZip(byte[] data){


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


    private class Task_DownBD extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {
            WebServices web=new WebServices();


            String bd64=web.Down_DB(strings[0]+".zip");

               if(bd64==null)
                   return "Error al descargar la base de datos.Intente nuevamente";

            byte[] data = new byte[0];

            try {
                data = Base64.decode(bd64);

            }catch (Exception e){
                return "Error al copiar archivo intente nuevamente";
            }

               unStreamZip(data);
            File f=new File(directorio+"/db_down.zip");

            if(!f.exists())
                return "No se descargo correctamente el archivo intente nuevamente";

              if( CopiarBD()==false)
                  return "No se pudo copiar la base de datos Intente nuevamente";

            AgregarColumnProductos();

               if(f.exists())
                     f.delete();


            ActualizarSesionAgente(strings[0]);



            return  "1";

        }

        @Override
        protected void onPostExecute(Object result){

            if(pd.isShowing()) {

                if(result=="1"){
                    MostrarDatos_Agente();
                    Toast.makeText(context,"BD agregada correctamente",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,result.toString(),Toast.LENGTH_SHORT).show();
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



}


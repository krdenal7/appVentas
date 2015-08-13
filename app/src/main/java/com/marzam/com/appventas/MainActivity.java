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
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.WebService.WebServices;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class  MainActivity extends Activity {

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
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST=9000;

    public static final long EXPIRATION_TIME_MS = 1000 * 3600 * 24 * 7;
    String SENDER_ID = "1006732471487";
    static final String TAG = "Marzam-Push:";

    ProgressDialog pd;
    ProgressDialog progressRestaurar;


    File directorio;
    LocationManager locationManager;

    Float ver_p;
    CSQLite lite;
    String password;
    String nombre_agente;
    String[] clave_agente;
    TextView txtUsuario;
    String txt="datos.txt";
    Bundle bundle;
    TextView txtNumber;
    TextView txtVersion;
    ProgressDialog progressApk;
    private static String file_url = "http://201.134.159.126/ActualizacionAndroid";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.login);
        setTitle("Fuerza de ventas");
        context=this;

        CrearDirectorioDownloads();

        txtUsuario=(TextView)findViewById(R.id.txtName);
        txtNumber=(TextView)findViewById(R.id.txtNumber);
        txtVersion=(TextView)findViewById(R.id.textView);

        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        ShowEnableGPS();//Muestra el alert en caso de que el GPS del dispositivo se encuentre desactivado

        if(VerificarActualizacion())
                    Show_New_Version();

        bundle = getIntent().getExtras();

        if(bundle!=null){

            if(bundle.getBoolean("Restaurar"))
            new Task_RestaurarBD().execute("");
        }


      // ObtenerArchivos2();
      // EliminarBD();

        if(!existTxt(txt))
                    CrearTXT();

        if(ExistsBD()) {
                MostrarDatos_Agente();
        }else {
            if(isOnline()) {

                  Show_IngresarUsuario();

            }
        }



      final  Intent i=new Intent(context,MapsLocation.class);

        Button btnAceptar=(Button)findViewById(R.id.button);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                password=((EditText)findViewById(R.id.editText)).getText().toString();
                String no_Agente=ObtenerAgenteActivo();

                if(VerificaContraseña(password,no_Agente)){
                    finish();
                    startActivity(i);
                }else {
                   Toast toast=Toast.makeText(context,"Password incorrecto",Toast.LENGTH_SHORT);
                   toast.show();
                   EditText pas=(EditText)findViewById(R.id.editText);
                   pas.setText("");
                }

            }
        });
    }

    public boolean VerificarActualizacion(){

        float version;
        boolean push;


        SharedPreferences prefs =
                getSharedPreferences("Actualizaciones",Context.MODE_PRIVATE);
                version=prefs.getFloat("VersionAp",getAppVersion(context));
                push=prefs.getBoolean("push",false);
                ver_p=prefs.getFloat("VersionApPendiente",0);

        SharedPreferences.Editor editor=prefs.edit();

        float version_pref=version;
        float version_app=getAppVersion(context);

        txtVersion.setText("V."+version_app);

        if(push==false){
         editor.putFloat("VersionAp",getAppVersion(context));
         editor.putBoolean("push",false);


            if(ver_p!=0){
                if(ver_p>version_app) {
                    editor.putFloat("VersionApPendiente",version_pref);
                    return true;
                }else{
                   editor.putFloat("VersionApPendiente",0);
                }
            }
            editor.commit();
         return false;
        }else{
            if(version_pref>version_app){
                editor.putFloat("VersionApPendiente",version_pref);
                editor.commit();
                return true;
            }else{
                editor.putFloat("VersionAp",getAppVersion(context));
                editor.putBoolean("push", false);
                editor.putFloat("VersionApPendiente",0);
                editor.commit();
                return false;
            }

        }
    }

    public void CrearDirectorioDownloads(){

        try {
            File folder = android.os.Environment.getExternalStorageDirectory();
            directorio = new File(folder.getAbsolutePath() + "/Marzam/preferencias");
            if (directorio.exists() == false) {
                directorio.mkdirs();
            }

            File database=new File("/data/data/com.marzam.com.appventas/databases");
            if(database.exists()==false){
                database.mkdirs();
            }

            File apk=new File(folder.getAbsolutePath() + "/Marzam/apk");

            if(apk.exists()) {
               for (File file:apk.listFiles()){
                   boolean resp= file.delete();
               }}

            if(apk.exists()==false){
                apk.mkdirs();
            }


        }catch (Exception e){
            Log.d("ErrorCrearDir", e.toString());
        }
    }

    public void ShowEnableGPS(){
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setTitle("Aviso");
            alert.setMessage("¿Desea activar el GPS?");
            alert.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    settingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    startActivity(settingIntent);


                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            alert.setCancelable(false);
            AlertDialog alertDialog=alert.create();
            alertDialog.show();
        }
    }

    public void Show_IngresarUsuario(){

        LayoutInflater inflate=getLayoutInflater();
        final View view=inflate.inflate(R.layout.input_text,null);


        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Ingrese su número de agente");
        alert.setView(view);
        alert.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String agente = ((EditText) view.findViewById(R.id.editText2)).getText().toString();

                if (!ExistsBD()) {
                    pd = ProgressDialog.show(context, "Obteniendo información de rutas", "Cargando", true, false);
                    new Task_DownBD().execute(agente);
                } else {
                    ActualizarSesionAgente(agente);
                    MostrarDatos_Agente();
                    EliminarRegistrationId(context, "", "");
                }

            }
        });
        /*alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });*/
        alert.setCancelable(false);
        AlertDialog alertDialog=alert.create();
        alertDialog.show();
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));


    }

    public void Show_New_Version(){

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Actualización");
        alert.setMessage("Hay una nueva actualización de la aplicación. Desea instalarla");
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 new Task_DownApk().execute("");
            }
        });
        alert.setNegativeButton("Más tarde",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();

    }

    public boolean VerificaContraseña(String password,String nombre){
try {
    String[] valor = {nombre,password};

    lite = new CSQLite(context);
    lite.getDataBase();

    SQLiteDatabase db = lite.getWritableDatabase();

    String query = "Select * From agentes where numero_empleado=? and password=?";
    Cursor cursor = db.rawQuery(query, valor);

    while (cursor.moveToFirst()) {
        db.close();
        lite.close();
        return true;
    }
}catch (Exception e){

    Toast.makeText(context,"La base de datos no se encuentra cargada",Toast.LENGTH_SHORT).show();

    return  false;
}


        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(press==true && isPress==false){
            isPress=true;
        }

        return super.onOptionsItemSelected(item);
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

       IntentHome();

    }

    private void IntentHome(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private class TareaRegistroGCM extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params)
        {
            String msg = "";
            WebServices services=new WebServices();

            try
            {


                if (gcm == null)
                {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }

                //Nos registramos en los servidores de GCM
                regid = gcm.register(SENDER_ID);

                String json=jsonPush(ObtenerAgenteActivo(),regid,getPhoneNumber());


                if(json!=null)
                services.RegistrarTelefono(json);

                msg="Dispositivo registrado: "+regid;
                Log.d(TAG, "Registrado en GCM: registration_id=" + regid);


               setRegistrationId(context, params[0], regid);
              // storeRegistrationId(context,regid);
            }
            catch (IOException ex)
            {
               Log.i(TAG,ex.toString());
            }


            return msg;
        }
        @Override
        protected  void onPostExecute(String result){

            if( pd.isShowing()){
                pd.dismiss();
            }

        }

    }

    public String jsonPush(String num_emp,String key,String telefono){

        JSONObject object=new JSONObject();
        JSONArray array=new JSONArray();

        try{

            object.put("numero_empleado",num_emp);
            object.put("api_key",key);
            object.put("numero_telefono",telefono!=null?telefono.replace("+",""):"0000000000");
            object.put("IMEI",getIMEI());
            array.put(object);


        }catch (JSONException e){
            array=null;
        }


        return array.toString();
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

    lite = new CSQLite(context);
    SQLiteDatabase db = lite.getWritableDatabase();

    Cursor rs = db.rawQuery("select numero_empleado,nombre from agentes where Sesion=1", null);
    String usuario = "";

    if (rs.moveToFirst()) {
        usuario = rs.getString(0) + "\n" + rs.getString(1);
        txtUsuario.setText(rs.getString(1));
        txtNumber.setText(rs.getString(0));
        nombre_agente = rs.getString(1);
    } else {
        Show_IngresarUsuario();
    }

    } //Si existe la base de datos y esta seleccionado el usuario lo mostrara en la pantalla

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


            String bd64=web.Down_DB(strings[0]+".zip",getIMEI());

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
                    Show_IngresarUsuario();
                }

                pd.dismiss();
            }
            if(ExistsBD())
                PushNotification();
        }
    }

    private class Task_RestaurarBD extends  AsyncTask<String,Void,Object>{



        @Override
        protected void onPreExecute(){

            progressRestaurar=ProgressDialog.show(context,"Restaurando base de datos","cargando",true,false);

        }

        @Override
        protected Object doInBackground(String... strings) {

            String agente=bundle.getString("Agente");
            WebServices web=new WebServices();
            String bd64=web.Down_DB(agente+".zip",getIMEI());


            if(bd64==null)
                return null;

            byte[] data;

            try {
                data = Base64.decode(bd64); //Convierte de Base64 a un arreglo de bit[]

            }catch (Exception e){
                return null;
            }

            unStreamZip(data);//Convierte el arreglo de bite[] en el .zip
            File f=new File(directorio+"/db_down.zip");

            if(!f.exists())
                return null;

                EliminarBD();

            if( CopiarBD()==false)
                return null;

            AgregarColumnProductos();

            if(f.exists())
                 f.delete();

            ActualizarSesionAgente(agente);

            return "1";
        }


        @Override
        protected void onPostExecute(Object result){


            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setMessage("Restaurar");

            if(progressRestaurar.isShowing()) {
                progressRestaurar.dismiss();
                if (result == null) {
                  alert.setMessage("No se pudo restaurar la base de datos.");
                }//cierre del if
                else {
                  alert.setMessage("La Base de datos se ha restaurado satisfactoriamente.");
                }

                alert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog=alert.create();
                alertDialog.show();
            }

        }


    }

    private class Task_DownApk extends AsyncTask<String,Integer,Object>{

        @Override
        protected void onPreExecute(){

            progressApk=new ProgressDialog(context);
            progressApk.setTitle("AppVentas");
            progressApk.setMessage("Descargando archivo");
            progressApk.setIndeterminate(false);
            progressApk.setCancelable(false);
            progressApk.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressApk.setProgress(0);
            progressApk.setMax(100);
            progressApk.show();

        }

        @Override
        protected Object doInBackground(String... strings) {
            int count;
            try {

                URL url=new URL(file_url);
                URLConnection connection=url.openConnection();
                connection.connect();

                int lenghtOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                File folder = android.os.Environment.getExternalStorageDirectory();

                OutputStream output = new FileOutputStream(folder.getAbsolutePath()+"/Marzam/apk/app-debug.apk");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress((int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (MalformedURLException e) {
                return null;
            } catch (IOException e) {
                String error=e.toString();
                return null;
            }

            return "";
        }

        @Override
        protected void onPostExecute(Object resul){

            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setTitle("AppVentas");

            if(progressApk.isShowing()){
                progressApk.dismiss();

                if(resul==null){

                    alert.setMessage("Error al descargar archivo.Verifique su conexión a Internet e intente mas tarde");
                    alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alertDialog=alert.create();
                    alertDialog.show();

                }else {
                    IntentApk();
                }
            }

        }

        @Override
        protected void onProgressUpdate(Integer... value){
                 progressApk.setProgress(value[0]);
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
            File fi=new File("/data/data/com.marzam.com.appventas");
            myOutput=new FileOutputStream("/data/data/com.marzam.com.appventas/databases/db.db");

            while ((length=myInput.read(buffer))>0){
                myOutput.write(buffer,0,length);
            }



            myOutput.close();
            myOutput.flush();
            myInput.close();

            if(filedown.exists())
                  filedown.delete();

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
    private void PushNotification(){
        //Registro SERVICIO GOOGLE CLOUD
        if(checkPlayServices()){


            gcm=GoogleCloudMessaging.getInstance(context);
            regid=getRegistrationId(context);

            if(regid.isEmpty()){
                new TareaRegistroGCM().execute(ObtenerAgenteActivo());
                pd=ProgressDialog.show(context,"Registro de dispositivo","Cargando",true,false);
            }
        }else{
            Log.i(TAG,"Dispositivo no soportado");
        }
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

    private void EliminarRegistrationId(Context context, String user, String regId){
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

    private boolean checkPlayServices(){
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        if(resultCode!= ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
               GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }else {
                Log.i(TAG,"Dispositivo no soportado");
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context){
        final SharedPreferences prefd=getSharedPreferences(
                MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        String registrationId= prefd.getString(PROPERTY_REG_ID,"");
        if(registrationId.isEmpty()){
            return "";
        }
        int registeredVersion=prefd.getInt(PROPERTY_APP_VERSION,Integer.MIN_VALUE);
        int currentVersion=getAppVersion(context);
        if(registeredVersion!=currentVersion){
            return "";
        }
        return registrationId;
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

    private String getIMEI(){
       String imei="";

        try{

            TelephonyManager manager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            imei=manager.getDeviceId();

        }catch (Exception e){
            e.printStackTrace();
        }
        return imei;
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

    public void ObtenerArchivos2(){


        File directorio = new File("/data/data/com.marzam.com.appventas/databases/");
        //File directorio = new File("/data/data/com.marzam.com.appventas/database/");
        File[] files=directorio.listFiles();
        CopiarArchivos2(files);
    }

    public void CopiarArchivos2(File[] files){
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
 }catch (Exception e){
     continue;
 }

            }
            myOuput.close();
            myOuput.flush();
        }
        catch (Exception e){
            String err=e.toString();
            Log.e("ErrorCopiar:",e.toString());
        }
    }

    public void CrearTXT(){

        try{

            OutputStreamWriter out=new OutputStreamWriter(openFileOutput(txt,Context.MODE_PRIVATE));
            out.write("A:0"+"\nB:0"+"\nC:0");
            out.close();

        }catch (Exception e){
            String error=e.toString();
            e.printStackTrace();
        }

    }

    public Boolean existTxt(String fileName){

          for(String tmp:fileList()){
              if(tmp.equals(fileName))
        return true;
          }

        return false;
    }

   public void IntentApk(){

       try{

          /* boolean unknownSource = false;

           if (Build.VERSION.SDK_INT < 3) {
               unknownSource = Settings.System.getInt(getContentResolver(), Settings.System.INSTALL_NON_MARKET_APPS, 0) == 1;
           }
           else if (Build.VERSION.SDK_INT < 17) {
               unknownSource = Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0) == 1;
           } else {
               unknownSource = Settings.Global.getInt(getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS, 0) == 1;
           }*/

           Intent intent=new Intent(Intent.ACTION_VIEW);
           File path= android.os.Environment.getExternalStorageDirectory();
           String Folder=path+"/Marzam/apk/app-debug.apk";
           Uri uri=Uri.parse("file:///"+Folder);
           intent.setDataAndType( uri,"application/vnd.android.package-archive");

           File file=new File(Folder);
           if(file.exists())
           startActivity(intent);

       }catch (Exception e){
           String error=e.toString();
           e.printStackTrace();
       }

   }


}


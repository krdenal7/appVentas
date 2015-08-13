package com.marzam.com.appventas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marzam.com.appventas.AltaClientesDr.AltaClientes;
import com.marzam.com.appventas.EstatusPedidos.RespuestaPedidos;
import com.marzam.com.appventas.GPS.GPSHelper;
import com.marzam.com.appventas.Graficas.Grafica_Vendedor;
import com.marzam.com.appventas.KPI.KPI_General;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Sincronizacion.Crear_precioFinal;
import com.marzam.com.appventas.Sincronizacion.Sincronizar;
import com.marzam.com.appventas.WebService.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class MapsLocation extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private TextView mMessageView;
    Context context;
    AlertDialog alertDialogH;
    AlertDialog alertDialogT;
    AlertDialog alertDialogAct;
    String[] clientesH;
    String[] clientesT;
    ProgressDialog progressDialog;
    Marker customMarker;
    String id_visita;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private PowerManager.WakeLock wl;

    CSQLite lite;
    TextView txtVisitados;
    TextView txtPendientes;
    TextView txtPedidos;
    TextView txtCtePedido;
    GPSHelper gpsHelper;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_info);
        setTitle("Mapa");
        context=this;
        CrearTabla();

        txtVisitados=(TextView)findViewById(R.id.textView9);
        txtPendientes=(TextView)findViewById(R.id.textView78);
        txtPedidos=(TextView)findViewById(R.id.textView5);
        txtCtePedido=(TextView)findViewById(R.id.textView10);

        ObtenerCtesHoy(ObtenerClavedeAgente());
        ObtenerClientesVisitados();

        id_visita=Obtener_Idvisita();

    }

    public void ShowCteH(){

        final CharSequence[] list=ObtenerCtesHoy(ObtenerClavedeAgente());

        AlertDialog.Builder alert=new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        alert.setTitle("Clientes de hoy");
        alert.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (!VerificarSesion_Cliente(clientesH[i])) {

                    ShowSesionActiva();

                }

            }
        });
        alertDialogH=alert.create();
        alertDialogH.show();

    }

    public void ShowCteT(){
        final CharSequence[] list=ObtenerCteTotales(ObtenerClavedeAgente());


        AlertDialog.Builder alert=new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        alert.setTitle("Clientes");
        alert.setItems(list,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(!VerificarSesion_Cliente(clientesT[i])){
                    ShowSesionActiva();
                }
            }
        });
        alertDialogT=alert.create();
        alertDialogT.show();

    }

    public void ShowBuscarCte(){

        final EditText textView=new EditText(context);
        textView.setHint("Ingrese cuenta o nombre:");
        textView.setInputType(InputType.TYPE_CLASS_TEXT);
        textView.setFocusable(true);


     AlertDialog.Builder alert=new AlertDialog.Builder(context);
     alert.setTitle("Clientes");
     alert.setView(textView);
     alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialogInterface, int i) {
             String cuenta = textView.getText().toString().toUpperCase().replace("'", "");


             if (cuenta.equals("")) {
                 dialogInterface.dismiss();
                 Toast.makeText(context, "Campo vacio.Ingrese una clave de agente", Toast.LENGTH_LONG).show();
             } else {

                 String clave_agente = ObtenerClavedeAgente();

                 char isLetter = 0;

                 if (cuenta.length() >= 2)
                     isLetter = cuenta.charAt(1);

                 if (Character.isDigit(isLetter)) {
                     if (Verificar_ClienteExiste(cuenta, clave_agente)) {
                         if (!VerificarSesion_Cliente(cuenta))
                             ShowSesionActiva();
                     } else {
                         Toast.makeText(context, "No se encontro el cliente.Intente nuevamente", Toast.LENGTH_LONG).show();
                     }
                 }//En caso de que sea Numero realiza la busqueda por cuenta
                 else {
                     if (Character.isLetter(isLetter)) {
                         ShowBuscarXnombre(cuenta, clave_agente);
                     }
                 }//Va a entrar en caso de que detecte una letra

             }
         }
     });
     alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialogInterface, int i) {

         }
     });
        AlertDialog alertDialog=alert.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        alertDialog.show();

    }

    public void ShowBuscarXnombre(String palabra, final String num_emp){
        final String[] clientes=ObtenerClientesLike(palabra);
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Seleccione");
        alert.setItems(clientes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String[] cuentas = clientes[i].split("-");
                String cuenta = "";

                if (cuentas.length != 0)
                    cuenta = cuentas[0];

                if (Verificar_ClienteExiste(cuenta, num_emp)) {
                    if (!VerificarSesion_Cliente(cuenta)) {
                        dialogInterface.dismiss();
                        ShowSesionActiva();
                    }
                } else {
                    Toast.makeText(context, "No se encontro el cliente.Intente nuevamente", Toast.LENGTH_LONG).show();
                }

            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog=alert.create();
        alertDialog.show();

    }

    public String[] ObtenerClientesLike(String palabra){

        String no_empleado=ObtenerClavedeAgente();
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();


        Cursor rs=db.rawQuery("select distinct(a.id_cliente),c.nombre from  agenda as a inner join clientes " +
                               "as c on a.id_cliente=c.id_cliente where c.nombre like'%"+palabra+"%' and a.clave_agente='"+no_empleado+"'",null);
        String[] clientes=new String[rs.getCount()];
        int contador=0;

        while (rs.moveToNext()){
            clientes[contador]=rs.getString(0)+"-"+rs.getString(1);
            contador++;
        }

        return clientes;
    }

    public void CrearTabla(){
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS RelacionClientes(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,id_largo varchar(50),id_corto varchar(50))");
        db.execSQL("CREATE TABLE IF NOT EXISTS clientesDr( " +
                " [id_cliente] [varchar](10) , " +
                " [nombre] [varchar](40) , " +
                " [rfc] [varchar](15) , " +
                " [correo] [varchar](50) , " +
                " [telefono] [nvarchar](15) , " +
                " [cp] [nvarchar](10) , " +
                " [colonia] [varchar](50) , " +
                " [calle] [varchar](30) , " +
                " [calle1] [varchar](30) , " +
                " [calle2] [varchar](30) , " +
                " [referencias] [varchar](500) , " +
                " [no_exterior] [int] , " +
                " [delegacion] [varchar](50) , " +
                " [estado] [varchar](30) , " +
                " [id_almacen] [varchar](5) , " +
                " [ruta] [varchar](30) , " +
                " [no_interior] [int] , " +
                " [estatus][varchar](30))");

        db.close();
        lite.close();

    }

    public void ShowSesionActiva(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage("Visita activa. Cierre primero la sesion para poder continuar con los demas clientes");
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent=new Intent(context,KPI_General.class);
                startActivity(intent);
            }
        });
        alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialogAct=alert.create();
        alertDialogAct.show();
    }

    public CharSequence[] ObtenerCtesHoy(String agente){


        CharSequence[] datos=null;

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();
        String query="select id_cliente from agenda where clave_agente='"+agente+"' and id_frecuencia in"+where()+" order by orden";

        Cursor cursor=db.rawQuery(query,null);

        clientesH=new String[cursor.getCount()];
        datos=new CharSequence[cursor.getCount()];

        int cont=0;

       while (cursor.moveToNext()){
           clientesH[cont]=cursor.getString(0);
           cont++;
       }

        Cursor rs = null;
       for(int i=0;i<clientesH.length;i++){
try {
    rs = db.rawQuery("select id_cliente,nombre from clientes where id_cliente='" + clientesH[i] + "'", null);

    if (rs.moveToFirst()) {
        datos[i] = rs.getString(0) + "-" + rs.getString(1);
    }
}finally {
        rs.close();
}

       }

        if(cursor!=null)
           cursor.close();
        if(rs!=null)
        rs.close();
        if(db!=null)
         db.close();
        lite.close();

        return datos;
    }

    public CharSequence[] ObtenerCteTotales(String agente){
        CharSequence[] datos=null;

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor cursor=db.rawQuery("select distinct(id_cliente) from  agenda where clave_agente='"+agente+"' ",null);

        clientesT=new String[cursor.getCount()];
        datos=new CharSequence[cursor.getCount()];
        int cont=0;
        while (cursor.moveToNext()){
            clientesT[cont]=cursor.getString(0);
            cont++;
        }

        Cursor rs=null;

         for(int i=0;i<datos.length;i++){
try {
    rs = db.rawQuery("select id_cliente,nombre from clientes where id_cliente='" + clientesT[i] + "' ", null);

    if (rs.moveToFirst()) {
        datos[i] = rs.getString(0) + "-" + rs.getString(1);
    }
}finally {
    rs.close();
}

         }

        if(cursor!=null)
        cursor.close();
        if(rs!=null)
        rs.close();
        if(db!=null)
        db.close();
        if(lite!=null)
        lite.close();

        return datos;

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

    public String ObtenerIdFuerza(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        String clave="";

        Cursor rs=db.rawQuery("select id_fuerza from agentes where Sesion=1",null);
        if(rs.moveToFirst()){

            clave=rs.getString(0);
        }


        return clave;
    }

    public boolean VerificarSesion_Cliente(String cliente){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        boolean resp=true;// Si es verdadero insertara la nueva visita
                   //false;//Si es falso ya se encuentra otra sesion activa

       Cursor rs= db.rawQuery("select id_cliente from sesion_cliente where Sesion=1",null);

        if(rs.moveToFirst()){
            resp=false;//ya se encunetra una sesion activa
        }

        if(resp==false){//Se verifica si la sesion activa correspornde al cliente que se selecciono
            rs=db.rawQuery("select id_cliente from sesion_cliente where Sesion=1 and id_cliente='"+cliente+"'",null);

            if(rs.moveToFirst()){
                Intent intent = new Intent(context, KPI_General.class);
                startActivity(intent);
                resp=true;
            }
            else {

                resp=false;//No corresponde al cliente seleccioado
            }
        }else {

            InsertarSesion(cliente);
            RegistrarVisitas(cliente);
            progressDialog = ProgressDialog.show(context, "Generando precios netos", "Cargando", true, false);
            new UpLoadVisitas().execute(cliente);

        }


           return resp;
    } //Verifica si ya se encuentra iniciada una visita con algún cliente //Principal

    public boolean Verificar_ClienteExiste(String cliente,String numero_emp){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

       String[] arg={cliente,numero_emp};

       Cursor rs=db.rawQuery("select id_cliente from agenda where id_cliente=? and clave_agente=?",arg);

        if(rs.moveToFirst()){
            return true;
        }

        return false;
    }

    public String ObtenerClavedeAgente(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        String clave="";

        Cursor rs=db.rawQuery("select clave_agente from agentes where Sesion=1",null);
        if(rs.moveToFirst()){

            clave=rs.getString(0);
        }


        return clave;
    }

    public void InsertarSesion(String cliente){

        try {


            lite = new CSQLite(context);
            SQLiteDatabase db = lite.getWritableDatabase();

            ContentValues values=new ContentValues();
            values.put("id_cliente",cliente);
            values.put("Sesion",1);
            values.put("Fecha_ingreso",getDate());
            String query="insert into sesion_cliente (id_cliente,sesion,fecha_ingreso)values('"+cliente+"',1,'"+getDate()+"') ";
            Long d= db.insert("sesion_cliente",null,values);
            String err="";
        }catch (Exception e){
            Toast.makeText(context,"Error al insertar visita",Toast.LENGTH_SHORT).show();
        }
    }//INSERTA EL CLIENTE CON EL QUE SE INICIO VISITA

    public void RegistrarVisitas(String cliente){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        GPSHelper gpsHelper=new GPSHelper(context);
        ContentValues values=new ContentValues();
        String agente=ObtenerClavedeAgente();
        String id_fuerza=ObtenerIdFuerza();
        values.put("clave_agente",agente);
        values.put("id_fuerza",id_fuerza);
        values.put("id_cliente",cliente);
        values.put("latitud",gpsHelper.getLatitude());
        values.put("longitud", gpsHelper.getLongitude());
        values.put("fecha_visita",getDate());
        values.put("fecha_registro",getDate());
        values.put("id_visita",id_visita);
        values.put("status_visita","10");
        db.insert("visitas",null,values);

        db.close();
        lite.close();
    }//SE REGISTRA LA VISITA Y SE ENVIA HACIA EL WEB SERVICE

    public String Obtener_Idvisita(){

        StringBuilder builder=new StringBuilder();
        builder.append("V");

           /*Id agente*/
        String id_agente=ObtenerId_Agente();
        int tam=id_agente.length();
        int ceros=4-tam;
        for(int i=0;i<ceros;i++){
            builder.append("0");
        }
        builder.append(id_agente);

       /*Año día*/
        builder.append(Fecha());



        return builder.toString();
    }//GENERA EL ID CORRESPONDIENTE DE LA VISITA

    private void ObtenerClientesVisitados(){

        CSQLite lt=new CSQLite(context);
        SQLiteDatabase db=lt.getReadableDatabase();
        int visitados=0;
        int adicionales=0;
        int pedidos=0;
        int clientes=0;

        if(db.isOpen()){


                String wher=where();

                Cursor rs = db.rawQuery("select * from sesion_cliente where sesion=2 and id_cliente in " +
                                        "(select id_cliente from agenda where id_frecuencia in "+wher+" )", null);

                try {
                    visitados = rs.getCount();
                }finally {
                    rs.close();
                }

                        rs = db.rawQuery("select * from sesion_cliente where id_cliente not in (select id_cliente from agenda where id_frecuencia in "+wher+")", null);
            try {
                adicionales = rs.getCount();
            }finally {
                rs.close();
            }

                       if(!db.isOpen()){
                           lt=new CSQLite(context);
                           db=lt.getReadableDatabase();
                       }

                       rs=db.rawQuery("SELECT * from encabezado_pedido  where strftime('%Y%m%d', 'now')= strftime('%Y%m%d',fecha_captura)",null);

            try {
                pedidos = rs.getCount();
            }finally {
                rs.close();
            }


                     rs=db.rawQuery("select * from encabezado_pedido where id_cliente in" +
                             " (select id_cliente from agenda where id_frecuencia in "+wher+" ) and strftime('%Y%m%d', 'now')= strftime('%Y%m%d',fecha_captura) group by id_cliente ",null);

            try {
                clientes = rs.getCount();
            }finally {
                rs.close();
            }

        }

         txtVisitados.setText( visitados+"/"+clientesH.length );
         txtPendientes.setText(""+adicionales);
         txtPedidos.setText(""+pedidos);
         txtCtePedido.setText(""+clientes);


    }//HACE EL CALCULO PARA MOSTRAR EN LA PANTALLA LOS CLIENTES QUE HAN SIDO VISITADOS Y LOS FALTANTES

    private String ObtenerId_Agente(){
        String id="";
        String agente=ObtenerAgenteActivo();
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select id_agente from agentes where numero_empleado='"+agente+"'",null);

        if(rs.moveToFirst()){
            id=rs.getString(0);
        }


        return id;
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

    private String jsonVisitas(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select * from visitas where id_visita='"+id_visita+"'",null);
        JSONArray array=new JSONArray();
        JSONObject object=new JSONObject();

        if(rs.moveToFirst()){

            try {
                object.put("clave_agente",rs.getString(0));
                object.put("id_fuerza",rs.getString(1));
                object.put("id_cliente",rs.getString(2));
                object.put("latitud",rs.getString(3));
                object.put("longitud",rs.getString(4));
                String Fecha=rs.getString(5);
                object.put("fecha_visita", Fecha!=null ? Fecha.replaceAll(":","|"):"01-01-2014 00|00|00");
                String Fecha2=rs.getString(6);
                object.put("fecha_registro",Fecha2!=null ? Fecha2.replaceAll(":","|"):"01-01-2014 00|00|00");
                object.put("id_visita",rs.getString(7));
                array.put(object);

            } catch (JSONException e) {

                e.printStackTrace();
            }


        }

        return array.toString();

    }//CREA EL JSON DE LAS VISITAS

    private void  ActualizarStatusVisita(String json){


        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        try {


            JSONArray array=new JSONArray(json);

            for(int i=0;i<array.length();i++){

                JSONObject jsonData=array.getJSONObject(i);

                String id = jsonData.getString("id_Visita");
                db.execSQL("update visitas set status_visita='20' where id_visita='" + id + "'");

            }



        } catch (JSONException e) {
            e.printStackTrace();
            String err=e.toString();
        }


    }

    private class UpLoadVisitas extends AsyncTask<String,Void,Object> {

        @Override
        protected void onPreExecute(){

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Ventas");
            wl.acquire();

        }

        @Override
        protected Object doInBackground(String... strings) {

            Crear_precioFinal precioFinal=new Crear_precioFinal();
            precioFinal.Ejecutar(context);


            return strings[0];
        }

        @Override
        protected void onPostExecute(Object result){

    if (progressDialog.isShowing()) {
        progressDialog.dismiss();
        progressDialog=ProgressDialog.show(context,"Registrando Visita","Enviando",true,false);
        new Task_EnviarVisita().execute(result.toString());

    }
    }


    }

    private class Task_EnviarVisita extends AsyncTask<String,Void,Object> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Object doInBackground(String... strings) {

            WebServices web=new WebServices();

            if(VerificarEstatusCteDr(strings[0])) {

                String json = jsonVisitas();
                String resp = web.SincronizarVisitas(json);
                if (resp != null)
                    ActualizarStatusVisita(resp);

            }

            return "";
        }

        @Override
        protected void onPostExecute(Object result){

            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
                Intent intent = new Intent(context, KPI_General.class);
                startActivity(intent);

                if(wl.isHeld())
                      wl.release();

            }
        }

    }

    public void Inicia_camera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public String[][] ObtenerCoordenadas(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        String[][] datos=new String[clientesH.length][3];
        Cursor rs=null;
        for(int i=0;i<clientesH.length;i++){

            rs=db.rawQuery("select id_cliente,nombre,latitud,longitud from clientes where id_cliente='"+clientesH[i]+"' ",null);

            if(rs.moveToFirst()){

                datos[i][0]=rs.getString(0)+"-"+rs.getString(1);
                datos[i][1]=rs.getString(2);
                datos[i][2]=rs.getString(3);

            }

        }

        db.close();
        lite.close();

        return datos;
    }
    @Override
    protected void onResume() {
        super.onResume();
        ObtenerClientesVisitados();
        setUpMapIfNeeded();
        setUpGoogleApiClientIfNeeded();
        mGoogleApiClient.connect();

    }
    @Override
    public void onPause() {
        super.onPause();
        ObtenerClientesVisitados();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMyLocationEnabled(true);//muestra el boton para ir a mi ubicacion
                mMap.setOnMyLocationButtonClickListener(this);

                gpsHelper=new GPSHelper(context);

                Double latitud=Double.parseDouble(gpsHelper.getLatitude());
                Double longitud=Double.parseDouble(gpsHelper.getLongitude());

                if(latitud==0.00 && longitud ==0.00){
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.56317359796029,  -99.04562934016724),12.0f));
                }else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitud,longitud),12.0f));
                }


                addMarker();

                final String[] cuenta = {""};

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        String[]cuentas= marker.getTitle().split("-");


                        if(cuentas.length!=0){
                            if(cuenta[0].equals(cuentas[0])){
                                String num_emp=ObtenerClavedeAgente();
                                if (Verificar_ClienteExiste(cuenta[0], num_emp)) {
                                    if (!VerificarSesion_Cliente(cuenta[0
                                            ]))
                                        ShowSesionActiva();
                                }
                            }else {
                                cuenta[0] =cuentas[0];
                                //Toast.makeText(context,"Diferente 1 click",Toast.LENGTH_LONG).show();
                            }
                        }
                        return false;
                    }
                });
            }
        }
    }

    public void addMarker(){


        View marker=((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout,null);
        TextView numTXT=(TextView)marker.findViewById(R.id.num_txt);

        String[][] datos=ObtenerCoordenadas();

        for(int i=0;i<datos.length;i++){
            numTXT.setText(String.valueOf(i+1));
            String nombre=datos[i][0];
            String lat=datos[i][1];
            String lon=datos[i][2];

            Double latitud=lat!=null?Double.parseDouble(lat):0;
            Double longitud=lon!=null?Double.parseDouble(lon):0;

          //  mMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)).title(nombre));
             LatLng latLng=new LatLng(latitud,longitud);

            customMarker=mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(nombre)
                    .snippet("Farmacia")
                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, marker))));

        }




    }

    private void setUpGoogleApiClientIfNeeded() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    public String[] ObtenerMenuFuerzas(){

        String agente=ObtenerAgenteActivo();

        if(lite!=null)
            lite.close();

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select menu from funciones_menu where id_menu in (select id_menu from menu_fuerzas " +
                "where id_fuerza=(select id_fuerza from agentes where numero_empleado=?))",new String[]{agente});

        if(rs.getCount()<=0)
            return new String[]{""};

        String[] menu=new String[rs.getCount()];
        int contador=0;

        while(rs.moveToNext()){
            menu[contador]=rs.getString(0);
            contador++;
        }
        return menu;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),12.0f));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.Reportes:
                Intent intent=new Intent(context, Grafica_Vendedor.class);
                startActivity(intent);
            break;
            case R.id.ClientesdeHoy:
                ShowCteH();
            break;
            case R.id.ClientesTotales:
                ShowCteT();
            break;
            case R.id.Buscarcliente:
                ShowBuscarCte();
            break;
            case R.id.Estatuspedidos:
                startActivity(new Intent(getBaseContext(), RespuestaPedidos.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            break;
            case R.id.Sincronizacion:
                startActivity(new Intent(getBaseContext(), Sincronizar.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            break;
            case R.id.AltaCte:
                startActivity(new Intent(getBaseContext(), AltaClientes.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapa, menu);


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        int tam = menu.size();
        String[] menus=ObtenerMenuFuerzas();

        for(int i=0;i<tam;i++){

            String item=menu.getItem(i).getTitle().toString();

            for(int j=0;j<menus.length;j++){

                if(item.equals(menus[j])) {
                    menu.getItem(i).setVisible(true);
                }
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){


        if(keyCode==KeyEvent.KEYCODE_MENU){
           // ShowMenu();
        }

        return super.onKeyDown(keyCode,event);
    }

    @Override
    public void onBackPressed(){

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setMessage("¿Desea salir?");
        alert.setPositiveButton("SI",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                startActivity(new Intent(getBaseContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();

            }
        });
        alert.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();


    }

    private String getDate(){

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formatteDate=df.format(dt.getTime());

        return formatteDate;
    }

    private String Fecha(){
        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();

        SimpleDateFormat df=new SimpleDateFormat("yy");
        String formatteDate=df.format(dt.getTime());

        SimpleDateFormat df1=new SimpleDateFormat("ddd");
        String formatteDate1=df1.format(dt.getTime());

        SimpleDateFormat df2=new SimpleDateFormat("HHmmss");
        String formatteDate2=df2.format(dt.getTime());

        return formatteDate+formatteDate1+formatteDate2;


    }

    private String where(){

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        String fact=df.format(dt.getTime());

        GregorianCalendar FechaCalendario=new GregorianCalendar();
        FechaCalendario.setTime(dt);

        int numDia=FechaCalendario.get(Calendar.DAY_OF_WEEK);
        String dia="";



        CSQLite lt1=new CSQLite(context);
        SQLiteDatabase bd=lt1.getReadableDatabase();

        Cursor rs=bd.rawQuery("select formula,letra from formulaMovil",null);
        StringBuilder builder=new StringBuilder();
        builder.append("(");

        while (rs.moveToNext()){

             String formula=rs.getString(0);
             String letra=rs.getString(1);

             Cursor res=bd.rawQuery(formula,new String[]{letra,fact});

            if(res.moveToFirst()) {
                builder.append("'");
                builder.append(res.getString(0));
                builder.append("',");
            }

        }

        switch (numDia){
            case 1:
                dia="Domingo";
                break;
            case 2:
                dia="Lunes";
                break;
            case 3:
                dia="Martes";
                break;
            case 4:
                dia="Miercoles";
                break;
            case 5:
                dia="Jueves";
                break;
            case 6:
                dia="Viernes";
                break;
            case 7:
                dia="Sabado";

        }

        builder.deleteCharAt(builder.length()-1);
        builder.append(") and ");
        builder.append(dia+"=1");

        return builder.toString();
    }

    public static Bitmap createDrawableFromView(Context context,View view){

        DisplayMetrics displayMetrics=new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels,displayMetrics.heightPixels);
        view.layout(0,0,displayMetrics.widthPixels,displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap=Bitmap.createBitmap(view.getMeasuredWidth(),view.getMeasuredHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        view.draw(canvas);

        return  bitmap;
    }


}

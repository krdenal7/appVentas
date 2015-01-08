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
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marzam.com.appventas.GPS.GPSHelper;
import com.marzam.com.appventas.Graficas.Grafica_Vendedor;
import com.marzam.com.appventas.KPI.KPI_General;
import com.marzam.com.appventas.KPI.estatus_respuestas;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Sincronizacion.Crear_precioFinal;
import com.marzam.com.appventas.Sincronizacion.Sincronizar;
import com.marzam.com.appventas.Sincronizacion.envio_pedido;
import com.marzam.com.appventas.WebService.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
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
    String CteAct="";
    ProgressDialog progressDialog;
    Marker customMarker;
    String id_visita;

    CSQLite lite;
    TextView txtCte;

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

        txtCte=(TextView)findViewById(R.id.textView4);
        ObtenerCtesHoy(ObtenerAgenteActivo());
        ObtenerClientesVisitados();

        id_visita=Obtener_Idvisita();

    }

    public void ShowMenu(){

        final CharSequence[] items={"Clientes de hoy","Clientes totales","Buscar cliente","Estatus de pedidos","Sincronización"};

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle( "Menú");
        alert.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==0)
                    ShowCteH();
                if(i==1)
                    ShowCteT();
                if(i==2)
                    ShowBuscarCte();

                if(i==3){

                    startActivity(new Intent(getBaseContext(), estatus_respuestas.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    finish();

                }
                if(i==4){
                    startActivity(new Intent(getBaseContext(), Sincronizar.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    finish();
                }

            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();


    }
    public void ShowCteH(){

        final CharSequence[] list=ObtenerCtesHoy(ObtenerAgenteActivo());

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Clientes de hoy");
        alert.setItems(list,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(!VerificarSesion_Cliente(clientesH[i])){

                    ShowSesionActiva();

                }

            }
        });
        alertDialogH=alert.create();
        alertDialogH.show();

    }
    public void ShowCteT(){
        final CharSequence[] list=ObtenerCteTotales(ObtenerAgenteActivo());


        AlertDialog.Builder alert=new AlertDialog.Builder(context);
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
        textView.setHint("Numero de cuenta:");
        textView.setInputType(InputType.TYPE_CLASS_TEXT);
        textView.setFocusable(true);

     AlertDialog.Builder alert=new AlertDialog.Builder(context);
     alert.setTitle("Clientes");
     alert.setView(textView);
     alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialogInterface, int i) {

             String cuenta=textView.getText().toString().toUpperCase().replace("'","");

             if(cuenta.equals("")) {
                 dialogInterface.dismiss();
                 Toast.makeText(context,"Campo vacio.Ingrese una clave de agente",Toast.LENGTH_LONG).show();
             }else{

                 String num_emp=ObtenerAgenteActivo();

                 if(Verificar_ClienteExiste(cuenta,num_emp)){
                     if(!VerificarSesion_Cliente(cuenta))
                             ShowSesionActiva();
                 }else {
                     Toast.makeText(context,"No se encontro el cliente.Intente nuevamente",Toast.LENGTH_LONG).show();
                 }

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
        SQLiteDatabase db=lite.getWritableDatabase();
        String query="select id_cliente from agenda where numero_empleado='"+agente+"' and id_frecuencia in"+where()+" order by orden_visita";

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

        rs=db.rawQuery("select id_cliente,nombre from clientes where id_cliente='"+clientesH[i]+"'",null);

           if(rs.moveToFirst()){
               datos[i]=rs.getString(0)+"-"+rs.getString(1);
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

        Cursor cursor=db.rawQuery("select distinct(id_cliente) from  agenda where numero_empleado='"+agente+"' ",null);

        clientesT=new String[cursor.getCount()];
        datos=new CharSequence[cursor.getCount()];
        int cont=0;
        while (cursor.moveToNext()){
            clientesT[cont]=cursor.getString(0);
            cont++;
        }

        Cursor rs=null;

         for(int i=0;i<datos.length;i++){

             rs=db.rawQuery("select id_cliente,nombre from clientes where id_cliente='"+clientesT[i]+"' ",null);

             if(rs.moveToFirst()){
                 datos[i]=rs.getString(0)+"-"+rs.getString(1);
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
            new UpLoadVisitas().execute("");

        }


           return resp;
    } //Verifica si ya se encuentra iniciada una visita con algún cliente //Principal
    public boolean Verificar_ClienteExiste(String cliente,String numero_emp){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

       String[] arg={cliente,numero_emp};

       Cursor rs=db.rawQuery("select id_cliente from agenda where id_cliente=? and numero_empleado=?",arg);

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
            String query="insert into sesion_cliente (id_cliente,Sesion,Fecha_ingreso)values('"+cliente+"',1,'"+getDate()+"') ";
            Long d= db.insert("sesion_cliente",null,values);
            String err="";
        }catch (Exception e){

            String error=e.toString();
            Log.d("Error al insertar Sesion:",error);
            Toast.makeText(context,"Error al insertar visita",Toast.LENGTH_SHORT).show();
        }
    }//INSERTA EL CLIENTE CON EL QUE SE INICIO VISITA


    public void RegistrarVisitas(String cliente){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        GPSHelper gpsHelper=new GPSHelper(context);
        ContentValues values=new ContentValues();
        String agente=ObtenerAgenteActivo();
        values.put("numero_empleado",agente);
        values.put("id_cliente",cliente);
        values.put("latitud",gpsHelper.getLatitude());
        values.put("longitud", gpsHelper.getLongitude());
        values.put("fecha_visita",getDate());
        values.put("fecha_registro",getDate());
        values.put("id_visita",id_visita);
        values.put("status_visita","10");
        Long res= db.insert("visitas",null,values);

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

        ObtenerCtesHoy(ObtenerAgenteActivo());

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
         int visitados=0;
        int total=clientesH.length;

        for(int i=0;i<clientesH.length;i++){

            Cursor rs=db.rawQuery("select Sesion from sesion_cliente where id_cliente='"+clientesH[i]+"'",null);

            if(rs.moveToFirst()){
                if(rs.getInt(0)==2){
                    visitados++;
                }
            }
        }

        txtCte.setText("Visitados:"+visitados+"/"+total+"   Por visitar:"+(total-visitados)+"/"+total+"");

    }//HACE EL CALCULO PARA MOSTRAR EN LA PANTALLA LOS CLIENTES QUE HAN SIDO VISITADOS Y LOS FALTANTES

    private String ObtenerId_Agente(){
        String id="";
        String agente=ObtenerClavedeAgente();
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select id_agente from agentes where clave_agente='"+agente+"'",null);

        if(rs.moveToFirst()){
            id=rs.getString(0);
        }


        return id;
    }

    private String jsonVisitas(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        String agente=ObtenerAgenteActivo();
        Cursor rs=db.rawQuery("select * from visitas where id_visita='"+id_visita+"'",null);
        JSONArray array=new JSONArray();
        JSONObject object=new JSONObject();

        if(rs.moveToFirst()){

            try {



                object.put("numero_empleado",rs.getString(0));
                object.put("id_cliente",rs.getString(1));
                object.put("latitud",rs.getString(2));
                object.put("longitud",rs.getString(3));
                String Fecha=rs.getString(4);
                object.put("fecha_visita", Fecha!=null ? Fecha.replaceAll(":","|"):"01-01-2014 00|00|00");
                String Fecha2=rs.getString(5);
                object.put("fecha_registro",Fecha2!=null ? Fecha2.replaceAll(":","|"):"01-01-2014 00|00|00");
                object.put("id_visita",rs.getString(6));
                array.put(object);

                String a= 5==6 ? null:"B";

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
        protected Object doInBackground(String... strings) {
            WebServices web=new WebServices();
                            String json=jsonVisitas();



            Crear_precioFinal precioFinal=new Crear_precioFinal();
            precioFinal.Ejecutar(context);

            String resp= web.SincronizarVisitas(json);
            if(resp!=null)
                     ActualizarStatusVisita(resp);

            return "";
        }

        @Override
        protected void onPostExecute(Object result){

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
                Intent intent = new Intent(context, KPI_General.class);
                startActivity(intent);

            }

        }
    }

    private class Task_Create_PrecioFinal extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {


            return "";
        }

        @Override
        protected void onPostExecute(Object result){


        }

    }



    public String[][] ObtenerCoordenadas(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        String[][] datos=new String[clientesH.length][3];
        Cursor rs=null;
        for(int i=0;i<clientesH.length;i++){

            rs=db.rawQuery("select nombre,latitud,longitud from clientes where id_cliente='"+clientesH[i]+"' ",null);

            if(rs.moveToFirst()){

                datos[i][0]=rs.getString(0);
                datos[i][1]=rs.getString(1);
                datos[i][2]=rs.getString(2);

            }

        }

        db.close();
        lite.close();

        return datos;
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpGoogleApiClientIfNeeded();
        mGoogleApiClient.connect();

    }

    @Override
    public void onPause() {
        super.onPause();
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


                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.56317359796029,  -99.04562934016724),12.0f));
                addMarker();

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {


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
            numTXT.setText(String.valueOf(i));
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
            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context,marker))));

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


        if(id==R.id.Reportes){

            Intent intent=new Intent(context, Grafica_Vendedor.class);
            startActivity(intent);

            return true;
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
    public boolean onKeyDown(int keyCode,KeyEvent event){


        if(keyCode==KeyEvent.KEYCODE_MENU){
            ShowMenu();
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

        String dia="";

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("dd");
        int diames=Integer.parseInt(df.format(dt.getTime()));

        GregorianCalendar FechaCalendario=new GregorianCalendar();
        FechaCalendario.setTime(dt);




        int numDia=FechaCalendario.get(Calendar.DAY_OF_WEEK);


        String where="(";

        int resultado= diames%7>0?(diames/7)+1:diames/7;

        where+= resultado==1?"'M1','Q1','SS')": resultado==2?"'M2','Q2','SS')":
                           resultado==3?"'M3','Q1','SS')": resultado==4?"'M4','Q2','SS')":
                                        "'M1','Q1','SS')";



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

        where+=" and dia='"+dia+"'";

        return where;
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

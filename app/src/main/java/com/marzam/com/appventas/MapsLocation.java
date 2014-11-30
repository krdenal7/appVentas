package com.marzam.com.appventas;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marzam.com.appventas.GPS.GPSHelper;
import com.marzam.com.appventas.Graficas.Grafica_Vendedor;
import com.marzam.com.appventas.KPI.KPI_General;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Sincronizacion.Sincronizar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by SAMSUMG on 11/11/2014.
 */
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
    String CteAct="";

    CSQLite lite;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_info);
        context=this;

        TextView txtCte=(TextView)findViewById(R.id.textView4);
        txtCte.setText("Visitados: 1/10   Por visitar:9/10");

    }

    public void ShowMenu(){

        final CharSequence[] items={"Clientes de hoy","Clientes totales","Sincronización"};

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle( "Menú");
        alert.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==0)
                    ShowCteH();
                if(i==1)
                    ShowCteT();
                if(i==2){
                    Intent intent=new Intent(context, Sincronizar.class);
                    startActivity(intent);
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

                if(VerificarSesion_Cliente(clientesH[i])){                   // InsertarSesion(clientesH[i]);
                    Intent intent = new Intent(context, KPI_General.class);
                           startActivity(intent);
                }else {

                           ShowSesionActiva();
                }

            }
        });
        alertDialogH=alert.create();
        alertDialogH.show();

    }
    public void ShowCteT(){
        final CharSequence[] list=ObtenerCteTotales("134057");


        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Clientes");
        alert.setItems(list,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialogT=alert.create();
        alertDialogT.show();

    }

    public void ShowSesionActiva(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage("Ya se encuentra una visita activa. Desea ir con el cliente?");
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

        String[] valor={agente,"Miercoles"};
        CharSequence[] datos=null;


        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor cursor=db.rawQuery("select id_cliente from agenda where numero_empleado=? and dia=?",valor);

        clientesH=new String[cursor.getCount()];
        datos=new CharSequence[cursor.getCount()];

        int cont=0;

       while (cursor.moveToNext()){
           clientesH[cont]=cursor.getString(0);
           cont++;
       }

        Cursor rs = null;
       for(int i=0;i<clientesH.length;i++){

        rs=db.rawQuery("select nombre from clientes where id_cliente='"+clientesH[i]+"'",null);

           if(rs.moveToFirst()){
               datos[i]=rs.getString(0);
           }

       }

        cursor.close();
        rs.close();
        db.close();
        lite.close();

        return datos;
    }
    public CharSequence[] ObtenerCteTotales(String agente){

        CharSequence[] clientes=null;
        CharSequence[] datos=null;

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor cursor=db.rawQuery("select id_cliente from  agenda where numero_empleado='134057'",null);

        clientes=new CharSequence[cursor.getCount()];
        datos=new CharSequence[cursor.getCount()];
        int cont=0;
        while (cursor.moveToNext()){
            clientes[cont]=cursor.getString(0);
            cont++;
        }

        Cursor rs=null;

         for(int i=0;i<datos.length;i++){

             rs=db.rawQuery("select nombre from clientes where id_cliente='"+clientes[i]+"' ",null);

             if(rs.moveToFirst()){
                 datos[i]=rs.getString(0);
             }

         }

        cursor.close();
        rs.close();
        db.close();
        lite.close();

        return datos;

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
                resp=true;//La sesion corresponde al cliente seleccionado
            }
            else {

                resp=false;//No corresponde al cliente seleccioado
            }
        }else {

            InsertarSesion(cliente);
            RegistrarVisiatas(cliente);
        }


           return resp;
    } //Verifica si ya se encuentra iniciada una visita con algún cliente

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
    }
    public void RegistrarVisiatas(String cliente){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        GPSHelper gpsHelper=new GPSHelper(context);


        ContentValues values=new ContentValues();
        values.put("numero_empleado",ObtenerAgenteActivo());
        values.put("id_cliente",cliente);
        values.put("latitud",gpsHelper.getLatitude());
        values.put("longitud", gpsHelper.getLongitude());
        values.put("fecha_visita",getDate());
        values.put("id_visita",Obtener_Idvisita());

        Long res= db.insert("visitas",null,values);

        db.close();
        lite.close();
    }

    public String Obtener_Idvisita(){
        String id="";
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select MAX(id) from sesion_cliente",null);

        if(rs.moveToFirst()){
            id=rs.getString(0);
        }


        return id;
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
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
                addMarker();
            }
        }
    }

    public void addMarker(){

        mMap.addMarker(new MarkerOptions().position(new LatLng(19.555965369691677, -99.0496741113617)).title("Bodega Aurrera"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(19.555247562141705, -99.04640181636353)).title("El Fenix"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(19.56101015234801,  -99.05210955714722)).title("Farmacia Guadalajara"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(19.56317359796029,  -99.04562934016724)).title("Wal-Mart Ecatepec"));

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
}

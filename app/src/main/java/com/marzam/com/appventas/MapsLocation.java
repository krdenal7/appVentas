package com.marzam.com.appventas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marzam.com.appventas.Graficas.Grafica_Vendedor;
import com.marzam.com.appventas.KPI.KPI_General;

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

        final CharSequence[] items={"Clientes de hoy","Clientes totales"};

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle( "Menú");
        alert.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==0)
                    ShowCteH();
                if(i==1)
                    ShowCteT();

            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();


    }
    public void ShowCteH(){
        final CharSequence[] list=new CharSequence[3];
        list[0]="Benavides";
        list[1]="Wal-Mart Ecatepec";
        list[2]="San Juan";

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Clientes de hoy");
        alert.setItems(list,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent=new Intent(context, KPI_General.class);
                startActivity(intent);

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();

    }
    public void ShowCteT(){
        final CharSequence[] list={"A92310","C124520","D72639","D97987","A89870980","G86587"};


        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Clientes de hoy");
        alert.setItems(list,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();

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

        mMap.addMarker(new MarkerOptions().position(new LatLng(19.6022857102156,-99.0444087597412)).title("Farmacia Guadalajara"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(19.607743417282613,-99.05784126248778)).title("Farmacia Benavides"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(19.601436716919167, -99.02754302945556)).title("Bodega Aurrera"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(19.608843206919167, -99.05754502946669)).title("Wal-Mart Ecatepec"));

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
}

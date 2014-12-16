package com.marzam.com.appventas.GPS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.marzam.com.appventas.KPI.KPI_General;
import com.marzam.com.appventas.MainActivity;
import com.marzam.com.appventas.R;

public class Actualizar_Coordenadas extends Activity {

    TextView txtLat;
    TextView txtLon;
    Button btnActualizar;
    Context context;
    LocationManager lmanager;
    LocationListener list;

    GPSHelper gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar__coordenadas);
        context=this;

        txtLat=(TextView)findViewById(R.id.textView35);
        txtLon=(TextView)findViewById(R.id.textView36);
        btnActualizar=(Button)findViewById(R.id.button2);



        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                gps=new GPSHelper(Actualizar_Coordenadas.this);
                txtLat.setText(gps.getLatitude());
                txtLon.setText(gps.getLongitude());

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actualizar__coordenadas, menu);
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
    public void onBackPressed(){
        startActivity(new Intent(getBaseContext(), KPI_General.class)
                      .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }


}

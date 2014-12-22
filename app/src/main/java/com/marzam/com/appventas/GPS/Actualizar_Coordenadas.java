package com.marzam.com.appventas.GPS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.WebService.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;

import java.io.File;

public class Actualizar_Coordenadas extends Activity {

    TextView txtLat;
    TextView txtLon;
    Button btnActualizar;
    Context context;
    LocationManager lmanager;
    LocationListener list;
    CSQLite lite;
    String lat="0";
    String lon="0";
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

                lat=gps.getLatitude();
                lon=gps.getLongitude();

                txtLat.setText(lat);
                txtLon.setText(lon);


                new UpLoadVisitas().execute("");


            }
        });

    }

    public String ObtenerClienteActivo(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select id_cliente from sesion_cliente where Sesion=1",null);


        String id="";

        if(rs.moveToFirst()){
            id=rs.getString(0);
        }
        return id;
    }

    public String ObtenerjsonGP(){

        JSONArray array=new JSONArray();
        JSONObject object=new JSONObject();

        try {

            object.put("id_cliente",ObtenerClienteActivo());
            object.put("latitud",lat);
            object.put("longitud",lon);
            array.put(object);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array.toString();
    }
    public String Obtener_idCliente(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select id_cliente from sesion_cliente where Sesion=1",null);

        String cliente="";

        if(rs.moveToFirst()){
            cliente=rs.getString(0);
        }

        return cliente;
    }


    public void ActualizarCoordenadasBD(){

        String id_cliente=Obtener_idCliente();
        
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        db.execSQL("update clientes set latitud='"+lat+"' , longitud='"+lon+"' where id_cliente='"+id_cliente+"'");


    }



    private class UpLoadVisitas extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {
            WebServices web=new WebServices();
            ActualizarCoordenadasBD();
            String resp=web.UploadCoordenadas(ObtenerjsonGP());



            return resp!=null?"":null;
        }

        @Override
        protected void onPostExecute(Object result){

          if(result==null)
              Toast.makeText(getApplicationContext(),"Error al registrar coordenadas",Toast.LENGTH_LONG).show();

        }
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

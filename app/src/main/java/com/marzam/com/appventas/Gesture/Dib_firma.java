package com.marzam.com.appventas.Gesture;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Dib_firma extends Activity {

    File Directorio;
    Context context;
    CSQLite lite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dib_firma);
        setTitle("Firma");
        context=this;
        CrearDirecImagenes();//Crea la carpeta de imagenes en caso de que no exista
        Button btnGuardar=(Button)findViewById(R.id.button3);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if( SaveFirma()){

                   finish();
                   Toast t=Toast.makeText(getApplicationContext(),"Firma agregada correctamente",Toast.LENGTH_SHORT);
                   t.show();

               }else {
                   Toast t=Toast.makeText(context,"Error al guardar firma. Intente más tarde",Toast.LENGTH_SHORT);
                   t.show();
               }
            }
        });


    }

    public void CrearDirecImagenes(){

        try {
            File folder = android.os.Environment.getExternalStorageDirectory();
            Directorio = new File(folder.getAbsolutePath() + "/Marzam/Imagenes");

            if (!Directorio.exists())
                Directorio.mkdirs();
        }catch (Exception e){
            String Error="Error al crear directorio de Imagenes:"+e.toString();
        }

    }

    public Boolean SaveFirma(){

        Bitmap bitmap = null;
        try {

            GestureOverlayView gesture=(GestureOverlayView)findViewById(R.id.gestureFirma);
            gesture.setDrawingCacheEnabled(true);
            bitmap=Bitmap.createBitmap(gesture.getDrawingCache());

        }catch (Exception e){
            String Error="gesture"+e.toString();
            Log.d("ErrorGestura:", Error);
            return false;
        }

        OutputStream outputStream=null;

        File file=new File(Directorio.toString(),Obtener_Idcliente()+".jpg");
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            String Error="Error al crear imagen de firma";
            return false;
        }


                return true;
    }

    public String Obtener_Idcliente(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        String id="00000";

        Cursor rs=db.rawQuery("select id_cliente from sesion_cliente where Sesion=1",null);

        if(rs.moveToFirst()){
            id=rs.getString(0);
        }

        return id;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dib_firma, menu);
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
}

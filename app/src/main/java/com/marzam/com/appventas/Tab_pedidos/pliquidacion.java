package com.marzam.com.appventas.Tab_pedidos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.marzam.com.appventas.Gesture.Dib_firma;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Objects;

public class pliquidacion extends Activity {

    Context context;
    File Directorio;
    CSQLite lite;
    Double subTotal=0.00;
    Double total=0.00;
    int CantProductos=0;

    TextView txtSubtotal;
    TextView txtIva;
    TextView txtIeps;
    TextView txtDescuentos;
    TextView txtTotal;
    TextView txtCantp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pliquidacion);
        context=this;
        MostrarFirma();
        ObtenerValores();

        txtSubtotal=(TextView)findViewById(R.id.textView14);
        txtTotal=(TextView)findViewById(R.id.textView22);
        txtCantp=(TextView)findViewById(R.id.textView24);

        txtSubtotal.setText("$"+String.format("%.2f",subTotal));
        txtTotal.setText("$"+String.format("%.2f",total));
        txtCantp.setText(""+CantProductos);

    }




    public void ShowMenu(){

        CharSequence[] items={"Guardar","Agregar Firma","Agregar productos"};
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Menú");
        alert.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==1){
                    Intent intent=new Intent(context, Dib_firma.class);
                    startActivity(intent);
                }

                if(i==2){
                    Intent intent=new Intent(context,pcatalogo.class);
                    startActivity(intent);
                }

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();
    }

    public void MostrarFirma(){

        File folder = android.os.Environment.getExternalStorageDirectory();
        Directorio = new File(folder.getAbsolutePath() + "/Marzam/Imagenes");
        File img=new File(Directorio+"/Firma1.jpg");

        if(img.exists()){
            Bitmap bitmap= BitmapFactory.decodeFile(img.toString());
            ImageView imageView=(ImageView)findViewById(R.id.imageView3);
            imageView.setImageBitmap(bitmap);
        }


    }

    public void ObtenerValores(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select precio,Cantidad from productos where isCheck=1",null);
       // CantProductos=rs.getCount();
        while (rs.moveToNext()){

            Double precio=Double.parseDouble(rs.getString(0));
            int cantidad=Integer.parseInt(rs.getString(1));

            CantProductos+=cantidad;
            subTotal+=(precio*cantidad);

        }

          total=subTotal+(subTotal*0.16);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pliquidacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyEvent,KeyEvent event){

        if(keyEvent==KeyEvent.KEYCODE_MENU)
            ShowMenu();


        return  super.onKeyDown(keyEvent,event);
    }

    @Override
    protected void onResume(){
        super.onResume();

        subTotal=0.00;
        total=0.00;
        CantProductos=0;
        ObtenerValores();
        txtSubtotal.setText("$"+String.format("%.2f",subTotal));
        txtTotal.setText("$"+String.format("%.2f",total));
        txtCantp.setText(""+CantProductos);

        MostrarFirma();
    }
}

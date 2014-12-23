package com.marzam.com.appventas.Tab_pedidos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.marzam.com.appventas.Gesture.Dib_firma;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Sincronizacion.envio_pedido;
import com.marzam.com.appventas.WebService.WebServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Objects;

public class pliquidacion extends Activity {

    Context context;
    File Directorio;
    CSQLite lite;
    Double subTotal=0.00;
    Double total=0.00;
    Double iva=0.00;
    Double ieps=0.00;
    int CantProductos=0;

    TextView txtSubtotal;
    TextView txtTotal;
    TextView txtCantp;
    TextView txtIva;
    TextView txtIeps;

    ProgressDialog progress;

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
        txtIva=(TextView)findViewById(R.id.textView16);
        txtIeps=(TextView)findViewById(R.id.textView18);

        txtSubtotal.setText("$"+String.format(Locale.US,"%.2f",subTotal));
        txtTotal.setText("$"+String.format(Locale.US,"%.2f",total));
        txtIeps.setText("$"+String.format(Locale.US,"%.2f",ieps));
        txtIva.setText("$"+String.format(Locale.US,"%.2f",iva));
        txtCantp.setText(""+CantProductos);

    }




    public void ShowMenu(){

        CharSequence[] items={"Enviar pedido","Agregar Firma","Agregar productos"};
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Menú");
        alert.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==0){

                    ShowisEnvio();

                }


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
    public void ShowisEnvio(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage("Desea enviar el pedido?");
        alert.setPositiveButton("Si",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new UpLoadTask().execute("");
                progress=ProgressDialog.show(context,"Transmitiendo pedidos","Cargando..",true,false);
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
    public void MostrarFirma(){

        File folder = android.os.Environment.getExternalStorageDirectory();
        Directorio = new File(folder.getAbsolutePath() + "/Marzam/Imagenes");
        File img=new File(Directorio+"/"+Obtener_Idcliente()+".jpg");

        if(img.exists()){
            Bitmap bitmap= BitmapFactory.decodeFile(img.toString());
            ImageView imageView=(ImageView)findViewById(R.id.imageView3);
            imageView.setBackgroundColor(Color.WHITE);
            imageView.setImageBitmap(bitmap);
        }


    }

    public void ObtenerValores(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        iva=0.00;
        ieps=0.00;

        Cursor rs=db.rawQuery("select precio_final,Cantidad,ieps,iva from productos where isCheck=1",null);
       // CantProductos=rs.getCount();
        while (rs.moveToNext()){

            Double precio=Double.parseDouble(rs.getString(0));
            int cantidad=Integer.parseInt(rs.getString(1));
            Double ieps1=Double.parseDouble(rs.getString(2));
            Double iva1=Double.parseDouble(rs.getString(3));




            Double cant1=(precio*ieps1/100);
            Double cant=((precio)+cant1);
            Double cant2=(cant*iva1/100);

            ieps+=cant1;
            iva+=cant2;

            CantProductos+=cantidad;
            subTotal+=(precio*cantidad);

        }


          total=subTotal+ieps+iva;

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

    private class UpLoadTask extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {
            WebServices web=new WebServices();

            envio_pedido pedido=new envio_pedido();
            String res= pedido.GuardarPedido(context);


            return res;
        }

        @Override
        protected void onPostExecute(Object result){

            if(progress.isShowing()) {
                String res=String.valueOf(result);
                if(res!="")
                    Toast.makeText(context,res,Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context,"Pedido enviado exitosamente",Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        }
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

        txtSubtotal.setText("$"+String.format(Locale.US,"%.2f",subTotal));
        txtTotal.setText("$"+String.format(Locale.US,"%.2f",total));
        txtIeps.setText("$"+String.format(Locale.US,"%.2f",ieps));
        txtIva.setText("$"+String.format(Locale.US,"%.2f",iva));
        txtCantp.setText(""+CantProductos);

        MostrarFirma();
    }
}

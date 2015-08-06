package com.marzam.com.appventas.EstatusPedidos;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class liquidacion_estatus extends Activity {

    Context context;
    File Directorio;
    CSQLite lite;
    Double subTotal=0.00;
    Double total=0.00;
    Double iva=0.00;
    Double ieps=0.00;
    Double oferta=0.0;
    int CantProductos=0;

    TextView txtSubtotal;
    TextView txtTotal;
    TextView txtCantp;
    TextView txtIva;
    TextView txtIeps;
    TextView txtOfertas;
    String id_pedido;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquidacion_estatus);

        context=this;
        id_pedido=getIntent().getStringExtra("id_pedido");

        MostrarFirma();
        ObtenerValores();

        txtSubtotal=(TextView)findViewById(R.id.textView14);
        txtTotal=(TextView)findViewById(R.id.textView22);
        txtCantp=(TextView)findViewById(R.id.textView24);
        txtIva=(TextView)findViewById(R.id.textView16);
        txtIeps=(TextView)findViewById(R.id.textView18);
        txtOfertas=(TextView)findViewById(R.id.textView20);

        NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec=(DecimalFormat)nf;
        dec.setMaximumFractionDigits(2);
        dec.setMinimumFractionDigits(2);


        txtSubtotal.setText("$"+ dec.format(subTotal));
        txtTotal.setText("$"+dec.format(total));
        txtIeps.setText("$"+dec.format(ieps));
        txtIva.setText("$"+dec.format(iva));
        txtCantp.setText(""+CantProductos);
        txtOfertas.setText("-$"+dec.format(oferta));

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
        oferta=0.00;

        Cursor rs=db.rawQuery("select precio_oferta,Cantidad,ieps,iva,codigo,precio from productos where isCheck=1",null);
        //CantProductos=rs.getCount();

        while (rs.moveToNext()){

            Double precioOf=Double.parseDouble(rs.getString(0));
            int cantidad=Integer.parseInt(rs.getString(1));
            Double ieps1=Double.parseDouble(rs.getString(2));
            Double iva1=Double.parseDouble(rs.getString(3));
            Double preciof=Double.parseDouble(rs.getString(5));
            Double of1=0.0;

            CSQLite lt=new CSQLite(context);
            SQLiteDatabase db1=lt.getReadableDatabase();
            Cursor rs1=db1.rawQuery("select descuento from ofertas where codigo=?",new String[]{rs.getString(4)});

            if(rs1.moveToFirst())
                of1=Double.parseDouble(rs1.getString(0));

            Double t1Ofertas=((preciof*of1)/100)*cantidad;

            Double iep=(precioOf*ieps1/100)*cantidad;
            Double cant1=(precioOf*ieps1/100);
            Double cant=((precioOf)+cant1);
            Double cant2=(cant*iva1/100)*cantidad;

            ieps+=iep;
            iva+=cant2;
            oferta+=t1Ofertas;

            CantProductos+=cantidad;
            subTotal+=((precioOf*cantidad)+t1Ofertas);

        }

        total=subTotal+ieps+iva-oferta;

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
    protected void onResume(){
        super.onResume();

        subTotal=0.00;
        total=0.00;
        CantProductos=0;
        oferta=0.00;
        ObtenerValores();

        NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec=(DecimalFormat)nf;
        dec.setMaximumFractionDigits(2);
        dec.setMinimumFractionDigits(2);

        txtSubtotal.setText("$"+ dec.format(subTotal));
        txtTotal.setText("$"+dec.format(total));
        txtIeps.setText("$"+dec.format(ieps));
        txtIva.setText("$"+dec.format(iva));
        txtCantp.setText(""+CantProductos);
        txtOfertas.setText("-$"+dec.format(oferta));

        MostrarFirma();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.liquidacion_estatus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.CatalogoCL:
                Intent intent=new Intent(context,catalogo_edit.class);
                startActivity(intent);
                break;
            case R.id.GuardarCL:
                if(Verificar_productos()) {
                    ShowGuardar();
                }else {
                    ShowNoItems();
                }
                break;
            case R.id.CancelarCL:
                ShowCancelar();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void ShowGuardar(){

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Guardar pedido");
        alert.setMessage("¿Desea guardar los cambios?");
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new TaskGuardar().execute(id_pedido);
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

    public void ShowCancelar(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Cancelar");
        alert.setMessage("Desea cancelar los cambios");
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UpdateSesion();
                Intent intent=new Intent(context,RespuestaPedidos.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

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

    public void UpdateSesion(){
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        db.execSQL("update sesion_cliente set Sesion=2 where id_cliente=(select id_cliente from sesion_cliente where Sesion=1)");


    }

    public void ShowNoItems(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage("No hay productos por agregar.Favor de verificar");
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();
    }

    public class TaskGuardar extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute(){
            progress=ProgressDialog.show(context,"Guardar","Guardando pedido...",true,false);

        }

        @Override
        protected String doInBackground(String... strings) {

            new Save(context,strings[0]);
            return strings[0];
        }
        @Override
        protected void onPostExecute(String s){

            if(progress.isShowing()){
                progress.dismiss();
                UpdateSesion();
                Intent i=new Intent(context,RespuestaPedidos.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);

            }

        }
    }

    public Boolean Verificar_productos(){

        CSQLite lite=new CSQLite(context);

        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select * from productos where isCheck=1",null);

        if(rs.moveToFirst())
            return true;

        return false;
    }

    public void ShowConfirmacion(){

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage("¿Desa salir de la edición?");
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UpdateSesion();
                Intent intent=new Intent(context,RespuestaPedidos.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
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

    @Override
    public void onBackPressed(){
        ShowConfirmacion();

    }


}

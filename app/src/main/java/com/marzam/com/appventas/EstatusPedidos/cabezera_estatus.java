package com.marzam.com.appventas.EstatusPedidos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.marzam.com.appventas.Email.Mail;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;

public class cabezera_estatus extends Activity {

    Context context;
    String id_pedido;
    TextView txtFpedido;
    TextView txt_idPedido;
    EditText txtNoOrden;
    ProgressDialog progress;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabezera_estatus);
        id_pedido=getIntent().getStringExtra("id_pedido");
        context=this;

        txt_idPedido=(TextView)findViewById(R.id.textView9);
        txt_idPedido.setText(id_pedido);

        txtFpedido=(TextView)findViewById(R.id.textView25);
        txtFpedido.setText(getDate());

        spinner=(Spinner)findViewById(R.id.spinner);

        ArrayAdapter arrayAdapter =
        new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item,new String[]{getTipoOrden()});
        spinner.setAdapter(arrayAdapter);

        txtNoOrden=(EditText)findViewById(R.id.editText2);
        txtNoOrden.setEnabled(false);
        txtNoOrden.setText(noOrden());


    }

    public String getDate(){
        String date="";

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();

        Cursor rs=db.rawQuery("select fecha_captura from encabezado_pedido where id_pedido=?",new String[]{id_pedido});

        if(rs.moveToFirst())
            date=rs.getString(0);

        lite.close();
        db.close();

        return date;

    }

    public String getTipoOrden(){
        String tipo_orden="";

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();

        Cursor rs=db.rawQuery("select tipo_orden from encabezado_pedido where id_pedido=?",new String[]{id_pedido});

        if(rs.moveToFirst())
            tipo_orden=rs.getString(0);

        lite.close();
        db.close();

        return tipo_orden;
    }

    public String noOrden(){
        String no_orden="";

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();

        Cursor rs=db.rawQuery("select no_pedido_cliente from encabezado_pedido where id_pedido=?",new String[]{id_pedido});

        if(rs.moveToFirst())
            no_orden=rs.getString(0);

        lite.close();
        db.close();

        return no_orden;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cabezera_estatus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.CatalogoCE:
                Intent intent=new Intent(context,catalogo_edit.class);
                startActivity(intent);
                break;
            case R.id.GuardarCE:
                if(Verificar_productos()) {
                    ShowGuardar();
                }else {
                    ShowNoItems();
                }
                break;
            case R.id.CancelarCE:
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

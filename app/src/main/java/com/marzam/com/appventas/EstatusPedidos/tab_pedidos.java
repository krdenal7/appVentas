package com.marzam.com.appventas.EstatusPedidos;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;

import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Tab_pedidos.pcabecera;
import com.marzam.com.appventas.Tab_pedidos.pcatalogo;
import com.marzam.com.appventas.Tab_pedidos.pdetalle;
import com.marzam.com.appventas.Tab_pedidos.pliquidacion;

public class tab_pedidos extends ActivityGroup {

    Context context;
    ProgressDialog progress;
    String id_pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_pedidos);
        id_pedido=getIntent().getStringExtra("id_pedido");
        setTitle("Pedidos");
        context=this;

        try {
            TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
            tabHost.setup(getLocalActivityManager());

            TabHost.TabSpec spec1 = tabHost.newTabSpec("CABECERA");
            spec1.setIndicator("GENERAL");
            Intent intent = new Intent(this, cabezera_estatus.class);
            intent.putExtra("id_pedido",id_pedido);
            spec1.setContent(intent);

            TabHost.TabSpec spec2 = tabHost.newTabSpec("DETALLE");
            spec2.setIndicator("DETALLE");
            Intent inten2 = new Intent(this, detalle_estatus.class);
            inten2.putExtra("id_pedido",id_pedido);
            spec2.setContent(inten2);

            TabHost.TabSpec spec3 = tabHost.newTabSpec("LIQUIDACION");
            spec3.setIndicator("COTIZACION");
            Intent inten3 = new Intent(this, liquidacion_estatus.class);
            inten3.putExtra("id_pedido",id_pedido);
            spec3.setContent(inten3);


            tabHost.addTab(spec1);
            tabHost.addTab(spec2);
            tabHost.addTab(spec3);
            tabHost.setCurrentTab(1);

            for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
            {
                TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setTextColor( Color.parseColor("#FFFFFF") );
            }

            Intent intent1=new Intent(this,catalogo_edit.class);
            startActivity(intent1);

        }catch (Exception e){

            Log.d("ErrorTab:", e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tab_pedidos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.CatalogoC:
                Intent intent=new Intent(context,catalogo_edit.class);
                startActivity(intent);
                break;
            case R.id.GuardarC:
                if(Verificar_productos()) {
                    ShowGuardar();
                }else {
                    ShowNoItems();
                }
                break;
            case R.id.CancelarC:
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

    public class TaskGuardar extends AsyncTask<String,Void,String>{

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

    @Override
    public void onBackPressed(){
     super.onBackPressed();

        ShowConfirmacion();

    }

}

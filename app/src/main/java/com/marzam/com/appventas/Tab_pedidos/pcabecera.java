package com.marzam.com.appventas.Tab_pedidos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.marzam.com.appventas.Gesture.Dib_firma;
import com.marzam.com.appventas.KPI.KPI_General;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Sincronizacion.envio_pedido;
import com.marzam.com.appventas.WebService.WebServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class pcabecera extends Activity {

    Context context;
    TextView txtFpedido;
    TextView txt_idPedido;
    ProgressDialog progress;
    CSQLite lite;
    Spinner spinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcabecera);

        context=this;

        txtFpedido=(TextView)findViewById(R.id.textView25);
        txtFpedido.setText(getDate());

        txt_idPedido=(TextView)findViewById(R.id.textView9);
        txt_idPedido.setText(Obtener_idpedido());

        spinner=(Spinner)findViewById(R.id.spinner);
        LlenarList();


        lite=new CSQLite(context);
        final SQLiteDatabase db=lite.getWritableDatabase();
        try {
            db.execSQL("ALTER TABLE tipo_fuerza ADD COLUMN isCheck int DEFAULT 0");
        }catch (Exception e){
            e.printStackTrace();
        }

        String tipo_fuerza=spinner.getSelectedItem().toString();
        db.execSQL("update tipo_fuerza set isCheck=1 where tipo_orden='"+tipo_fuerza+"'");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tipo_fuerza=spinner.getSelectedItem().toString();

                  db.execSQL("update tipo_fuerza set isCheck=1 where tipo_orden='"+tipo_fuerza+"'");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    public void LlenarList(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select tipo_orden from tipo_fuerza",null);
        String[]tipoFac=new String[rs.getCount()];
        int contador=0;
        while (rs.moveToNext()){

            tipoFac[contador]=rs.getString(0);
            contador++;
        }

        ArrayAdapter arrayAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,tipoFac);
        spinner.setAdapter(arrayAdapter);


    }
    public String Obtener_idpedido(){

        StringBuilder builder=new StringBuilder();

        String agente=ObtenerAgenteActivo();
        builder.append("P"+agente);
        String consecutivo=Consecutivo(agente);

        int val=(builder.length()+consecutivo.length());
        int falt=(12-val);

        for(int i=0;i<falt;i++){
            builder.append("0");
        }
        builder.append(consecutivo);



        return builder.toString();
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
    public String Consecutivo(String agente){

        String numero="";

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select " +
                "id from consecutivo where clave_agente='"+agente+"'",null);
        if(rs.moveToFirst()){
            numero=rs.getString(0);
        }

        return numero;
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


    private String getDate(){

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy");
        String formatteDate=df.format(dt.getTime());

        return formatteDate;
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

            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setTitle("Envio de pedido");
            alert.setIcon(android.R.drawable.ic_dialog_info);

            if(progress.isShowing()) {
                String res=String.valueOf(result);
                if(res!="")
              alert.setMessage(res);
                else
              alert.setMessage("Pedido enviado exitosamente");

                progress.dismiss();

              alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {

                      startActivity(new Intent(getBaseContext(), KPI_General.class)
                              .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                      finish();

                  }
              });

                AlertDialog alertDialog=alert.create();
                alertDialog.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pcabecera, menu);
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



        if(keyEvent==KeyEvent.KEYCODE_MENU) {

           ShowMenu();

        }


        return  super.onKeyDown(keyEvent,event);
    }

    @Override
    protected void onResume(){
    super.onResume();
    txt_idPedido.setText(Obtener_idpedido());

    }

}

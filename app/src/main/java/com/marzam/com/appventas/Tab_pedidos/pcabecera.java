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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

        if(!existTxt("Pedidos.txt"))
                          CrearTXT();

        String id_pedido=Obtener_idpedido2();

        EscribirTXT(id_pedido);

        txtFpedido=(TextView)findViewById(R.id.textView25);
        txtFpedido.setText(getDate());

        txt_idPedido=(TextView)findViewById(R.id.textView9);
        txt_idPedido.setText(LeerTXT());

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
    public String ObtenerIdAgente(){

        String id="";
        String clave_agente=ObtenerAgenteActivo();
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select id_agente from agentes where clave_agente='"+clave_agente+"'",null);

        if(rs.moveToFirst()){
            id=rs.getString(0);
        }


        return id;
    }
    public String Obtener_idpedido2(){

        StringBuilder builder=new StringBuilder();


        /*Iniciales*/
        builder.append("FG");

        /*Id agente*/
       String id_agente=ObtenerIdAgente();
       int tam=id_agente.length();
       int ceros=4-tam;
       for(int i=0;i<ceros;i++){
           builder.append("0");
       }
           builder.append(id_agente);

       /*Año día*/
           builder.append(Fecha());


        return builder.toString();
    }

    public void CrearTXT(){

        try{

            OutputStreamWriter out=new OutputStreamWriter(openFileOutput("Pedidos.txt",Context.MODE_PRIVATE));
            out.write("");
            out.close();

        }catch (Exception e){
            String error=e.toString();
            e.printStackTrace();
        }

    }
    public Boolean existTxt(String fileName){

        for(String tmp:fileList()){
            if(tmp.equals(fileName))
                return true;
        }

        return false;
    }
    public void EscribirTXT(String id_pedido){

        try{

            OutputStreamWriter writer2=new OutputStreamWriter(openFileOutput("Pedidos.txt",Context.MODE_PRIVATE));
            writer2.write(id_pedido);
            writer2.close();
            //ObtenerArchivos2();
        }catch (Exception e){

        }

    }
    public String LeerTXT(){
        String id_pedido="";

        try{

            InputStreamReader archivo=new InputStreamReader(openFileInput("Pedidos.txt"));
            BufferedReader br=new BufferedReader(archivo);

            id_pedido=br.readLine();

        }catch (Exception e){

        }

        return  id_pedido;
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
    private String Fecha(){
        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();

        SimpleDateFormat df=new SimpleDateFormat("yy");
        String formatteDate=df.format(dt.getTime());

        SimpleDateFormat df1=new SimpleDateFormat("ddd");
        String formatteDate1=df1.format(dt.getTime());

        SimpleDateFormat df2=new SimpleDateFormat("HHmmss");
        String formatteDate2=df2.format(dt.getTime());

        return formatteDate+formatteDate1+formatteDate2;


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

        switch (id){
            case R.id.Enviar:
                ShowisEnvio();
                break;
            case R.id.Firma:
                Intent intent=new Intent(context, Dib_firma.class);
                startActivity(intent);
                break;
            case  R.id.Productos:
                Intent intent2=new Intent(context,pcatalogo.class);
                startActivity(intent2);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyEvent,KeyEvent event){
        return  super.onKeyDown(keyEvent,event);
    }

    @Override
    protected void onResume(){
    super.onResume();
    txt_idPedido.setText(LeerTXT());

    }

}

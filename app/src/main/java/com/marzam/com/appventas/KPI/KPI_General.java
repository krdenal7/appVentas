package com.marzam.com.appventas.KPI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.TextView;

import com.marzam.com.appventas.GPS.Actualizar_Coordenadas;
import com.marzam.com.appventas.MapsLocation;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Tab_pedidos.pedido;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



public class KPI_General extends Activity {

    Context context;
    CSQLite lite;
    TextView txtUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpi__general);
        context=this;

        txtUsuario=(TextView)findViewById(R.id.textView55);
        txtUsuario.setText(Obtener_Nombre());

        WebView webView=(WebView)findViewById(R.id.webView2);
        webView.loadUrl("file:///android_asset/www/cliente.html");
        WebSettings settings=webView.getSettings();
          settings.setJavaScriptEnabled(true);
          settings.setDatabaseEnabled(true);
          String path=this.getApplicationContext().getDir("databases",Context.MODE_PRIVATE).getPath();
          settings.setDatabasePath("/data/data/com.marzam.com.appventas/databases");
          settings.setDomStorageEnabled(true);
          settings.setAllowContentAccess(true);
          settings.setAllowFileAccess(true);


        //   File directorio = new File("/data/data/com.marzam.com.appventas/databases/");
        //  File[] files=directorio.listFiles();
        //CopiarArchivos(files);





        webView.setWebChromeClient(new WebChromeClient(){
           @Override
        public void onExceededDatabaseQuota(String url,String databaseIdentifier, long currenteQuota, long estimatedSize,long totalusedQuota,WebStorage.QuotaUpdater quotaUpdater){

               quotaUpdater.updateQuota(estimatedSize*2);
               String val="";
           }
        });
    }

    public void ShowMenu(){

        CharSequence[] items={"Pedidos","Actualizar coordenadas","Cerrar visita"};

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Menú");
        alert.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==0){

                    Intent intent=new Intent(context, pedido.class);
                    startActivity(intent);
                }
                if(i==1){

                    Intent intent=new Intent(context, Actualizar_Coordenadas.class);
                    startActivity(intent);
                }
                if(i==2){
                    ShowCierreVisita();
                }

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();


    }
    public void ShowCierreVisita(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setMessage("Desea cerrar la visita?");
        alert.setPositiveButton("Si",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String agente=ObtenerClavedeAgente();
                CerrarVisita(agente);
                Intent intent=new Intent(context, MapsLocation.class);
                startActivity(intent);
            }
        });
        alert.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();
    }

    public void CerrarVisita(String agente){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        db.execSQL("update sesion_cliente set Sesion=2,Fecha_cierre='"+getDate()+"' where id=(select Max(id) from sesion_cliente)");
        UpdateConsecutivo_visitas(agente);
        UpdateProductos();

        db.close();
        lite.close();
    }


    public void UpdateConsecutivo_visitas(String agente){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select MAX(id) from consecutivo_visitas where clave_agente='"+agente+"'",null);

        if(rs.moveToFirst()){

            int val=rs.getInt(0);
            int total=val+1;
            ContentValues values=new ContentValues();
            values.put("id",total);
            int res= db.update("consecutivo_visitas",values,null,null);
            String a="";

        }

    }
    public void UpdateProductos(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("isCheck",0);
        values.put("Cantidad",0);
        values.put("precio_final","");
        db.update("productos",values,null,null);

        lite.close();
        db.close();
    }

    private String getDate(){

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy");
        String formatteDate=df.format(dt.getTime());

        return formatteDate;
    }

    public String Obtener_Nombre(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select id_cliente from sesion_cliente where Sesion=1",null);

        String cliente="";
        String id="";

        if(rs.moveToFirst()){
            id=rs.getString(0);
        }

        rs=db.rawQuery("select nombre from clientes where id_cliente='"+id+"'",null);

        if(rs.moveToFirst()){
            cliente=rs.getString(0);
        }

        return cliente;
    }
    public String ObtenerClavedeAgente(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        String clave="";

        Cursor rs=db.rawQuery("select clave_agente from agentes where Sesion=1",null);
        if(rs.moveToFirst()){

            clave=rs.getString(0);
        }


        return clave;
    }


    public void CopiarArchivos(File[] files){
        byte[] buffer=new byte[1024];
        int length;
        FileOutputStream myOuput=null;
        try {

            FileInputStream myInput=null;

            File folder = android.os.Environment.getExternalStorageDirectory();
            File directorio = new File(folder.getAbsolutePath() + "/Marzam/preferencias");



            for(int i=0;i<files.length;i++){

                myInput=new FileInputStream(files[i]);
                String archivo=files[i].getName();
                myOuput=new FileOutputStream(directorio+"/"+archivo);
                while ((length=myInput.read(buffer))>0){
                    myOuput.write(buffer,0,length);
                }


                myInput.close();

            }
            myOuput.close();
            myOuput.flush();
        }
        catch (Exception e){
            String err=e.toString();
            Log.e("ErrorCopiar:",e.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kpi__general, menu);
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
    public boolean onKeyDown(int keyEvent,KeyEvent event){

        if(keyEvent==KeyEvent.KEYCODE_MENU)
            ShowMenu();

        return super.onKeyDown(keyEvent,event);
    }
}

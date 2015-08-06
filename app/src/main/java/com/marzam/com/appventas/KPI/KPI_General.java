package com.marzam.com.appventas.KPI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.marzam.com.appventas.Cobranza.Cobranza;
import com.marzam.com.appventas.Cobranza.CobranzaDetallePago;
import com.marzam.com.appventas.DevolucionesFull.DevolucionesFullConteiner;
import com.marzam.com.appventas.DevolucionesLite.DevolucionesLite;
import com.marzam.com.appventas.GPS.Actualizar_Coordenadas;
import com.marzam.com.appventas.MapsLocation;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Tab_pedidos.pedido;
import com.marzam.com.appventas.WebService.WebServices;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;


public class KPI_General extends Activity {

    Context context;
    CSQLite lite;
    TextView txtUsuario;
    ProgressDialog progressDialog;
    WebView webViews;
    String Date;
    String estatus_cliente;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpi__general);
        context=this;

        txtUsuario=(TextView)findViewById(R.id.textView55);
        txtUsuario.setText(Obtener_Nombre());

        TextView estatus=(TextView)findViewById(R.id.textEstatus);
        estatus.setText(estatus_cliente);

        webViews=(WebView)findViewById(R.id.webView2);
        webViews.setScrollContainer(false);

        WebView();


    }

    public void WebView(){
        String id_cliente=ObtenerId_cliente();
        String url=ObtenerValoresURL(id_cliente);
        webViews.loadUrl("file:///android_asset/www/cliente.html?"+url);
        WebSettings settings=webViews.getSettings();
        settings.setJavaScriptEnabled(true);
    }

    public void ShowCierreVisita(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setMessage("Desea cerrar la visita?");
        alert.setPositiveButton("Si",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                    progress=ProgressDialog.show(context,"Cerrando visita","Cargando...",true,false);
                    new TaskCierreVisita().execute("");

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


    public void CerrarVisita(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        db.execSQL("update sesion_cliente set Sesion=2,Fecha_cierre='"+getDate()+"' where id=(select Max(id) from sesion_cliente)");
        String query="update visitas set fecha_cierre='"+getDateCierre()+"' where id_visita=(select max(id_visita) from visitas)";
        db.execSQL(query);
        UpdateProductos();

        db.close();
        lite.close();
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
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formatteDate=df.format(dt.getTime());

        return formatteDate;
    }

    private String getDateCierre(){

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatteDate=df.format(dt.getTime());

        return formatteDate;
    }

    public String Obtener_Idvisita(){

        String id="";
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select max(id_visita) from visitas ",null);


        if(rs.moveToFirst()){
            id=rs.getString(0);
        }

        return id;
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

        rs=db.rawQuery("select c.nombre,e.nombre from clientes as c inner join estatus_credito as e on c.id_estatus_credito=e.id_estatus_credito where id_cliente='"+id+"'",null);

        if(rs.moveToFirst()){
            cliente=rs.getString(0);
            estatus_cliente=rs.getString(1);
        }

        return cliente;
    }

    public String ObtenerClavedeAgente(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        String clave="";

        Cursor rs=db.rawQuery("select numero_empleado from agentes where Sesion=1",null);
        if(rs.moveToFirst()){

            clave=rs.getString(0);
        }


        return clave;
    }

    public String ObtenerId_cliente(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select id_cliente from sesion_cliente where Sesion=1",null);


        String id="";

        if(rs.moveToFirst()){
            id=rs.getString(0);
        }

        return id;
    }

    public String Obtenerjson_Cierre(){
        String json="";

        JSONObject object=new JSONObject();
        JSONArray array=new JSONArray();
        try {

            Date=getDate();
            object.put("id_visita",Obtener_Idvisita());
            object.put("fecha_cierre",Date.replace(":", "|"));
            object.put("estatus_visita","20");
            array.put(object);
            json=array.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return json;
    }

    public void Extraer_json(String json){

        String estatus="20";
        String id_visita="";
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();


        try {

            JSONArray array=new JSONArray(json);

            for(int i=0;i<array.length();i++){

            JSONObject object=new JSONObject(array.getJSONObject(i).toString());
            estatus=object.get("estatus_visita").toString();
            id_visita=object.get("id_visita").toString();
            db.execSQL("update visitas set status_visita='"+estatus+"',fecha_cierre='"+Date+"' where id_visita='"+id_visita+"'");

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public String ObtenerValoresURL(String id_cliente){
        String datos=null;

     lite=new CSQLite(context);
     SQLiteDatabase db=lite.getWritableDatabase();

    Cursor rs=null;

    String query="select saldo,limite_credito from presupuesto_clientes where id_cliente='"+id_cliente+"'";


    rs=db.rawQuery(query,null);
    List<NameValuePair> params = new LinkedList<NameValuePair>();


    if(rs.moveToFirst()){
        params.add(new BasicNameValuePair("A", rs.getString(0)));//Inserta el saldo
        params.add(new BasicNameValuePair("B", rs.getString(1)));//Inserta el limite de credito
    }
    else
    {
        params.add(new BasicNameValuePair("A", "0"));//Envia el valor en 0 si no encontro datos del cliente
        params.add(new BasicNameValuePair("B", "0"));
    }
        rs.close();
        db.close();

        db=lite.getReadableDatabase();
        rs=db.rawQuery("select tipo_kpi,valor from kpi_clientes where id_cliente='"+id_cliente+"'",null);
        String[] array={"C","D","E","F","G","H","I","J"};
        int cont=0;

        while (rs.moveToNext()){

            params.add(new BasicNameValuePair(array[cont],rs.getString(0)+"D%20"));
            params.add(new BasicNameValuePair(array[cont+1],rs.getString(1)));
            cont+=2;

        }

     datos = URLEncodedUtils.format(params, "utf-8");


     return datos;
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
       switch (id){

           case R.id.Pedidos:
               Intent intent=new Intent(context, pedido.class);
               startActivity(intent);
           break;
           case R.id.Actualizar:
               Intent intent2=new Intent(context, Actualizar_Coordenadas.class);
               startActivity(intent2);
           break;
           case R.id.Devoluciones_lite:
               Intent i = new Intent(context, DevolucionesFullConteiner.class);
               /*i.putExtra("pharmacy", ((TextView)findViewById( R.id.textView55 )).getText());*/
               startActivity( i );
               break;
           case R.id.Cobranza:
               Intent iCob=new Intent(context, Cobranza.class);
               iCob.putExtra("pharmacy", ((TextView)findViewById( R.id.textView55 )).getText());
               startActivity(iCob);
               break;
           case R.id.Cerra:
               ShowCierreVisita();
       }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        int tam = menu.size();
        String[] menus=ObtenerMenuFuerzas();

        for(int i=0;i<tam;i++){

            String item=menu.getItem(i).getTitle().toString();

            for(int j=0;j<menus.length;j++){

                if(item.equals(menus[j])) {
                    menu.getItem(i).setVisible(true);
                }
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public String[] ObtenerMenuFuerzas(){

       String agente=ObtenerClavedeAgente();

        if(lite!=null)
            lite.close();

       lite=new CSQLite(context);
       SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=db.rawQuery("select menu from funciones_menu where id_menu in (select id_menu from menu_fuerzas " +
                "where id_fuerza=(select id_fuerza from agentes where numero_empleado=?))",new String[]{agente});

        if(rs.getCount()<=0)
            return new String[]{""};

        String[] menu=new String[rs.getCount()];
        int contador=0;

        while(rs.moveToNext()){
            menu[contador]=rs.getString(0);
            contador++;
        }
        return menu;
    }
    @Override
    public boolean onKeyDown(int keyEvent,KeyEvent event){
        return super.onKeyDown(keyEvent,event);
    }

    private class TaskCierreVisita extends AsyncTask<String,Void,Object>{

        @Override
        protected Object doInBackground(String... strings) {

            WebServices services=new WebServices();
            String json=Obtenerjson_Cierre();
            String respuesta="";

            if(isOnline()) {
                 respuesta = services.CierreVisitas(json);

                if (respuesta != null)
                             Extraer_json(respuesta);
            }

            return respuesta;
        }
        @Override
        protected void onPostExecute(Object res){

            if(progress.isShowing()) {
                CerrarVisita();
                progress.dismiss();
                startActivity(new Intent(context,MapsLocation.class)
               .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        }
    }

    private class TaskWebview extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {



          //  settings.setDatabaseEnabled(true);
          //   String path=context.getApplicationContext().getDir("databases",Context.MODE_PRIVATE).getPath();
          //  settings.setDatabasePath("/data/data/com.marzam.com.appventas/databases");
          //   settings.setDomStorageEnabled(true);
          //  settings.setAllowContentAccess(true);
          //  settings.setAllowFileAccess(true);

            return "";
        }

        @Override
        protected void onPostExecute(Object result){

             if(progressDialog.isShowing()){
                 progressDialog.dismiss();
             }

        }
    }

    public  boolean isOnline(){

        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo !=null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {

        ShowCierreVisita();

    }

    @Override
    public void onPause(){
        super.onPause();
        WebView();
    }
}

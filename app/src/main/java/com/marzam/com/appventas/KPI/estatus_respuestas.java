package com.marzam.com.appventas.KPI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Sincronizacion.Crear_precioFinal;
import com.marzam.com.appventas.WebService.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class estatus_respuestas extends Activity {

    Context context;
    CSQLite lite;
    ProgressDialog progressDialog;
    ListView list;
    SimpleAdapter simpleAdapter;
    HashMap<String,String> producto_row;
    static ArrayList<HashMap<String,?>> data=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Estatus de pedidos");
        setContentView(R.layout.activity_estatus_respuestas);
        context=this;
        list=(ListView)findViewById(R.id.listView3);

        new DownEstatus().execute("");
        progressDialog = ProgressDialog.show(context, "Descargando estatus", "Cargando", true, false);

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
    public String ObtenerjsonStatus() throws JSONException {

        String agente=ObtenerClavedeAgente();
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=null;
        String json;

        JSONArray array=new JSONArray();
        JSONObject object=new JSONObject();

        rs=db.rawQuery("select id_pedido from encabezado_pedido where clave_agente='"+agente+"' and id_estatus='20'",null);

       if(rs.getCount()<=0)
           return null;

        while (rs.moveToNext()){
            try {
                object.put("id_Pedido",rs.getString(0));
            } catch (JSONException e) {
                object.put("id_Pedido","");
            }
        }

        array.put(object);

        return array.toString();
    }

    private class DownEstatus extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {
            WebServices web=new WebServices();
            String json="";
            try {

                   String jsonStatus=ObtenerjsonStatus();

                    if(jsonStatus==null)
                               return null;

                   json=web.Sincronizarrespuestas(ObtenerjsonStatus());

                if(json!=null)
                     llenarHasMap(json);

            } catch (JSONException e) {
               return null;
            }

            return json;
        }

        @Override
        protected void onPostExecute(Object result){

            if(progressDialog.isShowing()){

                if(result==null) {
                    Toast.makeText(context, "No se encontraron respuestas de pedidos", Toast.LENGTH_SHORT).show();
                    String[] dat={"Sin respuesta de pedidos"};
                    ArrayAdapter adapter=new ArrayAdapter(context,android.R.layout.simple_list_item_1,dat);
                    list.setAdapter(adapter);
                }
                else {
                    simpleAdapter = new SimpleAdapter(context, data, R.layout.row_estatus, new String[]{"A", "B", "C", "D", "E", "F"}, new int[]{R.id.textView65, R.id.textView66, R.id.textView67, R.id.textView68, R.id.textView69, R.id.textView70});
                    list.setAdapter(simpleAdapter);
                }

                progressDialog.dismiss();

            }

        }
    }

    public void llenarHasMap(String json){

        data=new ArrayList<HashMap<String, ?>>();
        producto_row=new HashMap<String, String>();
try {

        JSONArray array = new JSONArray(json);

    for (int i = 0; i < array.length(); i++) {

        JSONObject jsonData = array.getJSONObject(i);

        String id = jsonData.getString("id_pedido");
        String estatus = jsonData.getString("id_estatus");
        String codigo=jsonData.getString("codigo");
        String piezas_surtidas=jsonData.getString("piezas_surtidas");
        String precio=jsonData.getString("precio_neto");
        String factura=jsonData.getString("factura_marzam");

        producto_row.put("A","Pedido:"+id);
        producto_row.put("B","Estatus:"+ObtenerStatus(estatus));
        producto_row.put("C","Codigo:"+codigo);
        producto_row.put("D","Piezas surtidas:"+piezas_surtidas);
        producto_row.put("E","Precio:"+precio);
        producto_row.put("F","Factura:"+"");
        data.add(producto_row);
        producto_row=new HashMap<String, String>();


    }
}catch (Exception e){

}


    }

    public String ObtenerStatus(String id){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        String estatus="";
        Cursor rs=db.rawQuery("select descripcion from estatus where id_estatus='"+id+"'",null);

        if(rs.moveToFirst()){
            estatus=rs.getString(0);
        }

        return estatus;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_estatus_respuestas, menu);
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

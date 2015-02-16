package com.marzam.com.appventas.KPI;

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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.marzam.com.appventas.Adapters.CustomAdapter_ListExpandible;
import com.marzam.com.appventas.MapsLocation;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.WebService.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class estatus_respuestas extends Activity {

    Context context;
    CSQLite lite;
    ProgressDialog progressDialog;


    /*Lista Expandible*/
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandable;
    String[] groups;
    String[][] children;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Estatus de pedidos");
        setContentView(R.layout.activity_estatus_respuestas);
        context=this;
      // list=(ListView)findViewById(R.id.listView3);

        expandable=(ExpandableListView)findViewById(R.id.lsexpandible);
      //  expandableListAdapter=new CustomAdapter_ListExpandible(context,groups,children);
      //  expandable.setAdapter(expandableListAdapter);
        progressDialog=ProgressDialog.show(context,"Obteniendo estatus de pedidos","Cargando",true,false);
        new DownEstatus().execute("");

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

        rs=db.rawQuery("select id_pedido from encabezado_pedido where clave_agente='"+agente+"' and id_estatus<>'10'",null);

       if(rs.getCount()<=0)
                 return null;

        while (rs.moveToNext()){
            try {

                object.put("id_Pedido",rs.getString(0));
                array.put(object);
                object=new JSONObject();
            } catch (JSONException e) {
                object.put("id_Pedido","");
            }
        }



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

                   json=web.Sincronizarrespuestas(jsonStatus);

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

                if(result==null){
                    ShowSinDatos();
                }else{

                 expandableListAdapter=new CustomAdapter_ListExpandible(context,groups,children);
                 expandable.setAdapter(expandableListAdapter);
                }
                progressDialog.dismiss();

            }

        }
    }

    public void ShowSinDatos(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Información");
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setMessage("No se encontro información de pedidos");
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getBaseContext(), MapsLocation.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();

    }

    public void llenarHasMap(String json){

try {

        JSONArray array = new JSONArray(json);
        groups=new String[array.length()];
        children=new String[array.length()][5];

    for (int i = 0; i < array.length(); i++) {

        JSONObject jsonData = array.getJSONObject(i);

        String id = jsonData.getString("id_pedido");
        String estatus = jsonData.getString("id_estatus");
        String codigo=jsonData.getString("codigo");
        String piezas_surtidas=jsonData.getString("piezas_surtidas");
        String precio=jsonData.getString("precio_neto");
        String factura=jsonData.getString("factura_marzam");

        groups[i]="Pedido: "+id;

        for(int i2=0;i2<5;i2++) {
            switch (i2) {
                case 0:
                    children[i][0] ="Estatus: "+ObtenerStatus(estatus);
                    break;
                case 1:
                    children[i][1] ="Codigo: "+codigo;
                    break;
                case 2:
                    children[i][2] ="Piezas surtidas: "+piezas_surtidas;
                    break;
                case 3:
                    children[i][3] ="Precio: "+precio;
                    break;
                case 4:
                    children[i][4] ="Factura:"+factura;
            }
        }

    }
}catch (Exception e){
    String err=e.toString();
    e.printStackTrace();
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

    @Override
    public void onBackPressed(){
        startActivity(new Intent(getBaseContext(), MapsLocation.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }
}

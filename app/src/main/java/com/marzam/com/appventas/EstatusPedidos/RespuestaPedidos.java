package com.marzam.com.appventas.EstatusPedidos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.marzam.com.appventas.Email.Mail;
import com.marzam.com.appventas.MapsLocation;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;

import java.util.ArrayList;
import java.util.HashMap;

public class RespuestaPedidos extends Activity {

    Context context;
    CSQLite lite;

    /*ArrayPrincipal*/
    HashMap<String,String> row;
    ArrayList<HashMap<String,?>> data;
    SimpleAdapter simpleAdapter;

    /**/

    ListView listView;
    String from="RespuestasPedidos.class";
    String subject;
    String body;
    Mail  m;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuesta_pedidos);
        setTitle("Estatus de pedidos");
        context=this;

        LlenarHasMap(Consultar_Agente_Activo());
        simpleAdapter=new SimpleAdapter(context,data,R.layout.row_respuestas_principal,new String[]{"A","B"},new int[]{R.id.txtName,R.id.textViewSubtitle}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                convertView = super.getView(position, convertView, parent);

                if (position % 2 == 0) {
                    convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }else {
                    convertView.setBackgroundColor(Color.parseColor("#F2F2F2"));
                }
                //return super.getView(position, convertView, parent);
                return convertView;
            }
        };

        listView=(ListView)findViewById(R.id.listEncabezado);
        listView.setAdapter(simpleAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent=new Intent(context,Encabezados_pedidos.class);
                       intent.putExtra("cliente",data.get(i).get("B").toString());
                       intent.putExtra("clienteNombre",data.get(i).get("A").toString());
                       startActivity(intent);

            }
        });

    }


    public  String Consultar_Agente_Activo(){

        String id="";

        try{

            lite=new CSQLite(context);
            SQLiteDatabase db=lite.getWritableDatabase();

            Cursor rs=db.rawQuery("select clave_agente from agentes where Sesion=1",null);

            if(rs.moveToFirst()){
                id=rs.getString(0);
            }

            if(db!=null)
                db.close();
            if(lite!=null)
                lite.close();


        }catch (Exception e){
            subject="Consultar_Agente_Activo";
            body="Error: "+e.toString();
            new sendEmail().execute("");
        }


        return id;
    }
    public void LlenarHasMap(String agente){

        String query;

        try{

            row=new HashMap<String, String>();
            data=new ArrayList<HashMap<String, ?>>();
            lite=new CSQLite(context);
            SQLiteDatabase db=lite.getWritableDatabase();
            Cursor rs=null;


            query="select c.nombre,e.id_cliente from encabezado_pedido as e inner join clientes as c on " +
                  "e.id_cliente=c.id_cliente where clave_agente='"+agente+"' group by e.id_cliente,c.nombre order by e.fecha_captura desc";

            rs=db.rawQuery(query,null);

            while (rs.moveToNext()){

                row.put("A",rs.getString(0));
                row.put("B",rs.getString(1));
                data.add(row);
                row=new HashMap<String, String>();
            }

        }catch (Exception e){
            subject="LlenarHasMap";
            body="Error: "+e.toString();
            new sendEmail().execute("");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.respuesta_pedidos, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class sendEmail extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {


            m = new Mail("rodrigo.cabrera.it129@gmail.com", "juanito1.");
            String[] toArr = {"imartinez@marzam.com.mx","cardenal.07@hotmail.com"};
            m.setTo(toArr);
            m.setFrom(from);
            m.setSubject(subject);
            m.setBody(body);

            try {

                m.send();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    @Override
    public void onBackPressed(){
        startActivity(new Intent(getBaseContext(), MapsLocation.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }

}

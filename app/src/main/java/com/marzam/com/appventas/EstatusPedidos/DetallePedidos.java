package com.marzam.com.appventas.EstatusPedidos;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;

import java.util.ArrayList;
import java.util.HashMap;

public class DetallePedidos extends Activity {

    String id_pedido;
    Context context;

    ListView listView;
    SimpleAdapter adapter;
    HashMap<String,String> row;
    ArrayList<HashMap<String,?>> data;

    CSQLite lite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedidos);
        context=this;
        id_pedido=getIntent().getStringExtra("pedido");
        setTitle(id_pedido);

        LlenasHasMap();

       adapter=new SimpleAdapter(context,data,R.layout.row_simple_detalle,new String[]{"A","B","C"},new int[]{R.id.textView4,R.id.textView5,R.id.textView6});
       listView=(ListView)findViewById(R.id.listView);
       listView.setAdapter(adapter);


    }


    public void LlenasHasMap(){
        try{
          lite=new CSQLite(context);
            SQLiteDatabase db=lite.getWritableDatabase();

            row=new HashMap<String, String>();
            data=new ArrayList<HashMap<String, ?>>();
            Cursor rs=db.rawQuery("select pr.descripcion,piezas_pedidas,piezas_surtidas from detalle_pedido as p inner join productos as pr on p.codigo=pr.codigo where id_pedido='"+id_pedido+"'",null);

            while (rs.moveToNext()){

                row.put("A",rs.getString(0));
                row.put("B",rs.getString(1));
                row.put("C",rs.getString(2));
                data.add(row);
                row=new HashMap<String, String>();

            }

        }catch (Exception e){

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detalle_pedidos, menu);
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
}

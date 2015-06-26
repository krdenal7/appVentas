package com.marzam.com.appventas.EstatusPedidos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class Encabezados_pedidos extends Activity {

    SimpleAdapter adapter;
    HashMap<String, String> row;
    ArrayList<HashMap<String,?>> data;
    String id_cliente;
    ListView list;

    Context context;
    CSQLite lite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encabezados_pedidos);
        id_cliente=getIntent().getStringExtra("cliente");
        String name_cliente=getIntent().getStringExtra("clienteNombre");
        setTitle(name_cliente);
        context=this;


        if(id_cliente.equals("") || id_cliente==null){
            startActivity(new Intent(context,RespuestaPedidos.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
        }

        LlenarHasMap(id_cliente);
        adapter=new SimpleAdapter(context,data,R.layout.row_simple_encabezado,new String[]{"A","B","C","D"},new int[]{R.id.textView5,R.id.textView6,R.id.textView7,R.id.textView8}){
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
        list=(ListView)findViewById(R.id.listEncabezado);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(context,DetallePedidos.class);
                intent.putExtra("pedido",data.get(i).get("A").toString());
                startActivity(intent);
            }
        });

    }

    public void LlenarHasMap(String id){
          try{

              lite=new CSQLite(context);
              SQLiteDatabase db=lite.getWritableDatabase();
              Cursor rs;
              row=new HashMap<String,String>();
              data=new ArrayList<HashMap<String, ?>>();

              rs=db.rawQuery("select id_pedido,fecha_captura,e.descripcion from encabezado_pedido as en inner join estatus as e on en.id_estatus=e.id_estatus where id_cliente='"+id+"'",null);


             while ( rs.moveToNext()){

                    String id_pedido=rs.getString(0);

                    row.put("A",id_pedido);
                    String[] fecha=rs.getString(1).split(" ");
                    row.put("B",fecha.length<=0?"00-00-00":fecha[0]);
                    row.put("C",rs.getString(2));
                    row.put("D",ObtenerTotal(id_pedido));
                    data.add(row);
                    row=new HashMap<String, String>();
             }

          }catch (Exception e){

          }
    }

    public String ObtenerTotal(String id_pedido){
        int piezas=0;
        Double precio=0.0;
        Double importe=0.00;

        try{

        if(lite==null)
            lite=new CSQLite(context);

        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs= db.rawQuery("select piezas_surtidas,precio_neto from detalle_pedido where id_pedido='"+id_pedido+"'",null);

       while (rs.moveToNext()){

           piezas=rs.getInt(0);
           precio=Double.parseDouble(rs.getString(1));

           Double subtotal=piezas*precio;

           importe+=subtotal;
       }

        }catch (Exception e){

        }
        NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec=(DecimalFormat)nf;

        if(importe==0.00){
        importe=ObtenerTotal_SobrePiezaspedidas(id_pedido);
        }
        return  "$"+dec.format(importe).toString();
    }

    public Double ObtenerTotal_SobrePiezaspedidas(String id_pedido){

        int piezas=0;
        Double precio=0.0;
        Double importe=0.00;

        try{

            if(lite==null)
                lite=new CSQLite(context);

            SQLiteDatabase db=lite.getWritableDatabase();
            Cursor rs= db.rawQuery("select piezas_pedidas,precio_neto from detalle_pedido where id_pedido='"+id_pedido+"'",null);

            while (rs.moveToNext()){

                piezas=rs.getInt(0);
                precio=Double.parseDouble(rs.getString(1));

                Double subtotal=piezas*precio;

                importe+=subtotal;
            }

        }catch (Exception e){

        }
        return importe;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.encabezados_pedidos, menu);
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

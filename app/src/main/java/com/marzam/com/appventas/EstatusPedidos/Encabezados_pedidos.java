package com.marzam.com.appventas.EstatusPedidos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.marzam.com.appventas.Adapters.ItemAdapter;
import com.marzam.com.appventas.Adapters.ItemRow;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


public class Encabezados_pedidos extends Activity {

    //SimpleAdapter adapter;
    HashMap<String, String> row;
    ArrayList<HashMap<String,?>> data;
    String id_cliente;
    ListView list;

    Context context;
    CSQLite lite;

    SwipeListView swipelistview;
    ItemAdapter adapter;
    List<ItemRow> itemData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_swipe);
        id_cliente=getIntent().getStringExtra("cliente");
        String name_cliente=getIntent().getStringExtra("clienteNombre");
        setTitle(name_cliente);
        context=this;


        if(id_cliente.equals("") || id_cliente==null){
            startActivity(new Intent(context,RespuestaPedidos.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
        }

        swipelistview=(SwipeListView)findViewById(R.id.example_swipe_lv_list);
        itemData=new ArrayList<ItemRow>();
        adapter=new ItemAdapter(this,R.layout.row_simple_encabezado,itemData,swipelistview);

        swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                //Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                //Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                //Log.d("swipe", String.format("onClickFrontView %d", position));

                Intent intent=new Intent(context,DetallePedidos.class);
                intent.putExtra("pedido",itemData.get(position).getPedido());
                startActivity(intent);

            }

            @Override
            public void onClickBackView(int position) {
                // Log.d("swipe", String.format("onClickBackView %d", position));

                swipelistview.closeAnimate(position);//when you touch back view it will close
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {

            }

        });

        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT); // there are five swiping modes
        swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
        swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);
        swipelistview.setOffsetLeft(convertDpToPixel(260f)); // left side offset
        swipelistview.setOffsetRight(convertDpToPixel(0f)); // right side offset
        swipelistview.setAnimationTime(50); // Animation time
        swipelistview.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress

        swipelistview.setAdapter(adapter);
        LlenarItems(id_cliente);
        adapter.notifyDataSetChanged();
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public void LlenarItems(String id){
          try{

              lite=new CSQLite(context);
              SQLiteDatabase db=lite.getWritableDatabase();
              Cursor rs;

              rs=db.rawQuery("select id_pedido,fecha_captura,e.descripcion from encabezado_pedido as en inner join estatus as e on en.id_estatus=e.id_estatus where id_cliente='"+id+"'",null);


             while ( rs.moveToNext()){

                    String id_pedido=rs.getString(0);
                    String[] fecha=rs.getString(1).split(" ");
                     itemData.add(new ItemRow(id_pedido,fecha.length<=0?"00-00-00":fecha[0],
                     rs.getString(2),ObtenerTotal(id_pedido)));

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

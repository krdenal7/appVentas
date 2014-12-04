package com.marzam.com.appventas.Tab_pedidos;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.marzam.com.appventas.Gesture.Dib_firma;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Sincronizacion.envio_pedido;
import com.marzam.com.appventas.WebService.WebServices;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class pdetalle extends Activity {

    Context context;
    CSQLite lite;
    HashMap<String,String> producto_row;
    static ArrayList<HashMap<String,?>> data = null;
    ListView lista;
    SimpleAdapter simpleAdapter;
    Double monto=0.00;
    TextView txtMonto;
    EditText txtBuscar;
    ImageButton btnClear;
    ProgressDialog progress;
    /*Cambio de Prueba*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdetalle);
        context=this;

        txtMonto=(TextView)findViewById(R.id.textView10);
        txtBuscar=(EditText)findViewById(R.id.editText3);
        btnClear=(ImageButton)findViewById(R.id.imageButton);

        Productos();
        lista=(ListView)findViewById(R.id.listView);
        simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple,new String[]{"A","B","C"},new int[]{R.id.textView30,R.id.textView31,R.id.textView32});
        lista.setAdapter(simpleAdapter);
        DecimalFormat dec=new DecimalFormat("###,###,##");
        txtMonto.setText("Monto actual: $"+dec.format(monto));

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

               ShowEliminar(i);

                return false;
            }
        });

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                if(charSequence.length()>0) {
                    pdetalle.this.simpleAdapter.getFilter().filter(charSequence);
                    Filter cont = simpleAdapter.getFilter();
                    if (cont != null)
                        lista.setAdapter(simpleAdapter);
                }else {

                        simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple,new String[]{"A","B","C"},new int[]{R.id.textView30,R.id.textView31,R.id.textView32});
                        lista.setAdapter(simpleAdapter);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtBuscar.setText("");
                simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple,new String[]{"A","B","C"},new int[]{R.id.textView30,R.id.textView31,R.id.textView32});
                lista.setAdapter(simpleAdapter);
            }
        });

    }

    public void ShowEliminar(final int posicion){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage("Desea quitar el producto de la lista?");
        alert.setPositiveButton("Si",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String ean=data.get(posicion).get("D").toString().replace("codigo:","").trim();
                EliminarProducto(ean);
                monto=0.00;
                Productos();
                txtMonto.setText("Monto actual: $"+ String.format("%.2f", monto));
                simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple,new String[]{"A","B","C"},new int[]{R.id.textView30,R.id.textView31,R.id.textView32});
                lista.setAdapter(simpleAdapter);

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
        alert.setMessage("Desea envíar el pedido?");
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

    public void Productos(){


        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=null;
        int Cantidad=0;
        Double precio=0.00;

        String query="select descripcion,precio,Cantidad,codigo  from productos where isCheck=1";

        rs=db.rawQuery(query,null);
        data=new ArrayList<HashMap<String, ?>>();
        producto_row=new HashMap<String, String>();

        int cont=rs.getCount();
        while (rs.moveToNext()){
            producto_row.put("A",rs.getString(0));
            producto_row.put("B","Precio: $"+rs.getString(1));
            producto_row.put("C","Cantidad: "+rs.getString(2));
            producto_row.put("D","codigo: "+rs.getString(3));
            data.add(producto_row);
            producto_row=new HashMap<String, String>();

           precio=Double.parseDouble(rs.getString(1));
           Cantidad=Integer.parseInt(rs.getString(2));

           monto+=(precio*Cantidad);
        }



        rs.close();
        db.close();
        lite.close();



    }

    public void EliminarProducto(String ean){

        lite=new CSQLite(context);

        SQLiteDatabase db=lite.getWritableDatabase();
        db.execSQL("update productos set  Cantidad=0,isCheck=0 where codigo='"+ean+"'");

        db.close();
        lite.close();

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

            if(progress.isShowing()) {

                monto=0.00;
                Productos();
                txtMonto.setText("Monto actual: $"+String.format("%.2f", monto));
                simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple,new String[]{"A","B","C"},new int[]{R.id.textView30,R.id.textView31,R.id.textView32});
                lista.setAdapter(simpleAdapter);
                progress.dismiss();

                String res=String.valueOf(result);
                Toast.makeText(context,res,Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pdetalle, menu);
        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        monto=0.00;
        Productos();
        DecimalFormat dec=new DecimalFormat("###,###.##");
        txtMonto.setText("Monto actual: $"+dec.format(monto));
        simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple,new String[]{"A","B","C"},new int[]{R.id.textView30,R.id.textView31,R.id.textView32});
        lista.setAdapter(simpleAdapter);
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

        if(keyEvent==KeyEvent.KEYCODE_MENU)
            ShowMenu();


        return  super.onKeyDown(keyEvent,event);
    }
}

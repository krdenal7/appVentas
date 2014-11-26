package com.marzam.com.appventas.Tab_pedidos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.marzam.com.appventas.CustomAdapter;
import com.marzam.com.appventas.MainActivity;
import com.marzam.com.appventas.Model;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;

import java.util.ArrayList;
import java.util.HashMap;


public class pcatalogo extends Activity {

    Context context;

    ListView lproductos;
    EditText EditBuscar;
    Model[] modelItems;
    CustomAdapter adapter1;
    NumberPicker picker;
    HashMap<String,String> producto_row;
    static ArrayList<HashMap<String,?>>data=null;
    SimpleAdapter simpleAdapter;
    CSQLite lite;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);
        context=this;

         EditBuscar=(EditText)findViewById(R.id.editText4);
         lproductos=(ListView)findViewById(R.id.listView2);



        LlenarModelItems();

        adapter1=new CustomAdapter(this,modelItems);
        lproductos.setAdapter(adapter1);

        LlenarHasmap();//llena el arreglo para el simpleAdapter

        simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple,new String[]{"A","B","C"},new int[]{R.id.textView30,R.id.textView31,R.id.textView32});

        EditBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                 if(charSequence.length()>0) {
                     pcatalogo.this.simpleAdapter.getFilter().filter(charSequence);
                     Filter cont = simpleAdapter.getFilter();
                     if (cont != null)
                         lproductos.setAdapter(simpleAdapter);
                 }else {
                     lproductos.setAdapter(adapter1);
                 }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });  //Filtrado de listView

        ImageButton imageButton=(ImageButton)findViewById(R.id.imageButton2);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditBuscar.setText("");
                lproductos.setAdapter(adapter1);
            }
        });

        lproductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowDialog();
            }
        });

        lproductos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast t=Toast.makeText(context,"Detalle",Toast.LENGTH_SHORT);
                t.show();

                return false;
            }
        });
    }


    public void ShowDialog(){
        llenar_picker();


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle( "Seleccione una cantidad");
        alertDialogBuilder.setView(picker);
        alertDialogBuilder.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {

                   lproductos.setAdapter(adapter1);

            }

        });

        alertDialogBuilder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {

            }

        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }
    public void llenar_picker(){

        picker = new NumberPicker(context);
        String[] nums = new String[1000];
        for(int i=0; i<nums.length; i++)
            nums[i] = Integer.toString(i);

        picker.setMinValue(1);
        picker.setMaxValue(nums.length-1);
        picker.setWrapSelectorWheel(false);
        picker.setDisplayedValues(nums);
        picker.setValue(1);



    }


    public void LlenarHasmap(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=null;

        String query="select descripcion,precio,Cantidad  from productos limit 1000";

        rs=db.rawQuery(query,null);
        data=new ArrayList<HashMap<String, ?>>();
        producto_row=new HashMap<String, String>();

        while (rs.moveToNext()){
            producto_row.put("A",rs.getString(0));
            producto_row.put("B","Precio: $"+rs.getString(1)+" Oferta: 0%");
            producto_row.put("C","Cantidad: "+rs.getString(2));
            data.add(producto_row);
            producto_row=new HashMap<String, String>();
        }

        rs.close();
        db.close();
        lite.close();


    }
    public void LlenarModelItems(){

      lite=new CSQLite(context);
      SQLiteDatabase db=lite.getWritableDatabase();
      Cursor rs=null;

        try {
            rs=db.rawQuery("select descripcion,isCheck,Cantidad,precio,codigo  from productos limit 1000",null);
        }catch (Exception e){
            String err="Error:"+e.toString();
            Log.d("Error:",err);
        }



        modelItems=new Model[rs.getCount()];

        int cont=0;

        while (rs.moveToNext()){
            modelItems[cont]=new Model(rs.getString(0),rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4));
            cont++;
        }

        rs.close();
        db.close();

        lite.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalogo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(getBaseContext(), pedido.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }

}

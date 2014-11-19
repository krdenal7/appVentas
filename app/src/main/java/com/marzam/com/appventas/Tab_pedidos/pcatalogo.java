package com.marzam.com.appventas.Tab_pedidos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.marzam.com.appventas.CustomAdapter;
import com.marzam.com.appventas.Model;
import com.marzam.com.appventas.R;

import java.util.ArrayList;
import java.util.HashMap;


public class pcatalogo extends Activity {

    Context context;

    ListView lproductos;
    EditText EditBuscar;
    Model[] modelItems;
    CustomAdapter adapter1;
    NumberPicker picker;
    String[] list={"Aspirina 500 mg","Aspirina 250 mg","Sedalmerk"};
    final static ArrayList<HashMap<String,?>>data=new ArrayList<HashMap<String, ?>>();
    SimpleAdapter simpleAdapter;

    static {
        HashMap<String, String> row=new HashMap<String, String>();
        row.put("A","Aspirina 500 mg");
        row.put("B","Precio: $50.00 Oferta: 50%");
        row.put("C","Cantidad: 0");
        data.add(row);
        row=new HashMap<String, String>();
        row.put("A","Aspirina 250 mg");
        row.put("B","Precio: $25.00 Oferta: 25%");
        row.put("C","Cantidad: 0");
        data.add(row);
        row=new HashMap<String, String>();
        row.put("A","Sedalmerk");
        row.put("B","Precio: $100.00 Oferta: 10%");
        row.put("C","Cantidad: 0");
        data.add(row);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);
        context=this;

         EditBuscar=(EditText)findViewById(R.id.editText4);
         lproductos=(ListView)findViewById(R.id.listView2);

        modelItems=new Model[3];
        modelItems[0]= new Model("Aspirina 500 mg",0);
        modelItems[1]= new Model("Aspirina 250 mg",0);
        modelItems[2]= new Model("Sedalmerk",0);


        adapter1=new CustomAdapter(this,modelItems);
        lproductos.setAdapter(adapter1);

        simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple,new String[]{"A","B","C"},new int[]{R.id.textView30,R.id.textView31,R.id.textView32});

        EditBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {


                pcatalogo.this.simpleAdapter.getFilter().filter(charSequence);
                Filter cont=simpleAdapter.getFilter();
                if(cont!=null)
                  lproductos.setAdapter(simpleAdapter);

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
}

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
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.marzam.com.appventas.Adapters.CustomAdapter;
import com.marzam.com.appventas.Adapters.Model;
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

    AlertDialog alertDialog;
    AlertDialog alertDialog_picker;

    Button boton1;
    Button boton2;
    Button boton3;
    Button boton4;
    Button boton5;
    Button boton6;
    ProgressDialog dialogList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);
        context=this;

         EditBuscar=(EditText)findViewById(R.id.editText4);
         lproductos=(ListView)findViewById(R.id.listView2);


        new UpdateList().execute("");
        dialogList=ProgressDialog.show(context,"Catalogo","Generando..",true,false);
        LlenarModelItems();
        adapter1=new CustomAdapter(this,modelItems);
        lproductos.setAdapter(adapter1);



        EditBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {


                try {
                    if (charSequence.length() >= 2) {


                        pcatalogo.this.simpleAdapter.getFilter().filter(charSequence);
                        Filter cont = simpleAdapter.getFilter();

                        if (cont != null) {
                            lproductos.setAdapter(simpleAdapter);
                        }
                    } else {

                           LlenarModelItems();
                           adapter1=new CustomAdapter(context,modelItems);
                           lproductos.setAdapter(adapter1);
                    }
                }catch (Exception e){

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

                LlenarModelItems();
                EditBuscar.setText("");
                adapter1=new CustomAdapter(context,modelItems);
                lproductos.setAdapter(adapter1);

            }
        });

        lproductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ShowDialog(i);

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


    public void ShowDialog(final int posicion){



         String Item=String.valueOf(simpleAdapter.getItem(posicion));
         final String codigo=ObtenerValoresdeFilter(Item);

        LayoutInflater inflater=getLayoutInflater();
        View viewButton=inflater.inflate(R.layout.botones_cantidad,null);
        Eventos_Button(viewButton,codigo);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle( "Seleccione una cantidad");
        alertDialogBuilder.setView(viewButton);
        alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {


                  //LlenarModelItems();
                 //adapter1=new CustomAdapter(context,modelItems);
                 //lproductos.setAdapter(adapter1);
                 //EditBuscar.setText("");
                 //new UpdateList().execute("");

            }

        });



        alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }
    public void ShowDialog_picker(final String codigo){

        llenar_picker();
        final EditText txtCantidad=new EditText(context);
        txtCantidad.setHint("cantidad");
        txtCantidad.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder alert1=new AlertDialog.Builder(context);
        alert1.setTitle("Seleccione una cantidad");
        alert1.setView(txtCantidad);
        alert1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int cantidad=Integer.parseInt(txtCantidad.getText().toString());
                AgregarProducto(codigo,0,1);

                if(cantidad!=0)
                AgregarProducto(codigo,cantidad,1);

                else
                AgregarProducto(codigo,0,0);

               // adapter1=new CustomAdapter(context,modelItems);
               // lproductos.setAdapter(adapter1);
               // EditBuscar.setText("");
               // new UpdateList().execute("");

            }
        });
        alert1.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialog_picker=alert1.create();
        alertDialog_picker.show();



    }

    public void llenar_picker(){

        picker = new NumberPicker(context);
        String[] nums = new String[1000];
        for(int i=0; i<nums.length; i++)
            nums[i] = Integer.toString(i);

        picker.setMinValue(1);
        picker.setMaxValue(nums.length);
        picker.setWrapSelectorWheel(false);
        picker.setDisplayedValues(nums);
        picker.setValue(2);




    }
    public void LlenarHasmap(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=null;

        String query="select descripcion,precio,Cantidad,p.codigo,precio_final,clasificacion_fiscal,o.descuento from productos as p left join " +
                " ofertas as o on p.codigo=o.codigo";

        rs=db.rawQuery(query,null);
        data=new ArrayList<HashMap<String, ?>>();
        producto_row=new HashMap<String, String>();

        while (rs.moveToNext()){

            String oferta=(rs.getString(6)==null)?"0":rs.getString(6);

            producto_row.put("A",rs.getString(0));
            producto_row.put("B","Precio Lista: $"+rs.getString(1));
            producto_row.put("C","Cantidad: "+rs.getString(2));
            producto_row.put("D",rs.getString(3));
            producto_row.put("E","Precio Final: $"+rs.getString(4));
            producto_row.put("F","Clasificación: "+rs.getString(5));
            producto_row.put("G","Oferta: "+oferta+"%");
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
            rs=db.rawQuery("select descripcion,isCheck,Cantidad,precio,p.codigo,precio_final,clasificacion_fiscal,o.descuento  " +
                           "from productos as p left join ofertas as o on p.codigo=o.codigo limit 1000 ",null);
        }catch (Exception e){
            String err="Error:"+e.toString();
            Log.d("Error:",err);
        }



        modelItems=new Model[rs.getCount()];

        int cont=0;

        while (rs.moveToNext()){

            String oferta=(rs.getString(7)==null)?"0":rs.getString(7);
            modelItems[cont]=new Model(rs.getString(0),rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),oferta);
            cont++;
        }

        rs.close();
        db.close();

        lite.close();
    }


    public String ObtenerValoresdeFilter(String Item){

        String[] split=Item.replace("{","").replace("}","").split(",");

        String cantidad= split[3].replace("C=Cantidad: ","").trim();
        String codigo=split[0].replace("D=","");




            return codigo;
    }
    public String[] ObtenerInfoProductos(String ean){
        String[] info=new String[3];

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select descripcion,precio_final,Cantidad from productos where codigo='"+ean+"'",null);

        if(rs.moveToFirst()){
            info[0]=rs.getString(0);
            info[1]=rs.getString(1);
            info[2]=rs.getString(2);
        }


        return info;
    }

    public void Eventos_Button(View view, final String  codigo){

        boton1=(Button)view.findViewById(R.id.button12);
        boton2=(Button)view.findViewById(R.id.button13);
        boton3=(Button)view.findViewById(R.id.button14);
        boton4=(Button)view.findViewById(R.id.button15);
        boton5=(Button)view.findViewById(R.id.button16);
        boton6=(Button)view.findViewById(R.id.button17);

        TextView txt1=(TextView)view.findViewById(R.id.textView50);
        TextView txt2=(TextView)view.findViewById(R.id.textView52);
        final TextView txt3=(TextView)view.findViewById(R.id.textView54);

        final String[] info=ObtenerInfoProductos(codigo);

        txt1.setText(info[0]);
        txt2.setText(info[1]);
        txt3.setText(info[2]);

        final int[] cont = {0};


        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AgregarProducto(codigo,0,0);
                cont[0]=0;
                txt3.setText("0");


            }
        });
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AgregarProducto(codigo,1,1);
                int val=Integer.parseInt(info[2]);
                txt3.setText("" + ((val + cont[0]) + 1));
                cont[0]++;

            }
        });
        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AgregarProducto(codigo, 2, 1);
                int val=Integer.parseInt(info[2]);
                txt3.setText(""+((val+ cont[0])+2));
                cont[0]+=2;
            }
        });
        boton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AgregarProducto(codigo,5,1);
                int val=Integer.parseInt(info[2]);
                txt3.setText(""+((val+ cont[0])+5));
                cont[0]+=5;

            }
        });
        boton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AgregarProducto(codigo,10,1);
                int val=Integer.parseInt(info[2]);
                txt3.setText(""+((val+ cont[0])+10));
                cont[0]+=10;
            }
        });
        boton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
                ShowDialog_picker(codigo);
            }
        });

    }

    public void AgregarProducto(String ean,int cantidad,int isChecked){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        if(cantidad!=0){
            Cursor rs=db.rawQuery("select Cantidad from productos where codigo='"+ean+"'",null);
            int pzas=0;
            if(rs.moveToFirst()){

                pzas=rs.getInt(0);

            }
            db.execSQL("update productos set  Cantidad="+(cantidad+pzas)+",isCheck="+isChecked+" where codigo='"+ean+"'");



            db.close();
            lite.close();


        }else {
            db.execSQL("update productos set  Cantidad=" + cantidad + ",isCheck=0 where codigo='" + ean + "'");
            db.close();
            lite.close();
        }

        }


    public void LLenarList(){


        LlenarHasmap();//llena el arreglo para el simpleAdapter
        simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple,new String[]{"A","B","E","G","C","F"},new int[]{R.id.textView30,R.id.textView31,R.id.textView60,R.id.textView61,R.id.textView32,R.id.textView71});

    }

    private class UpdateList extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {

            LLenarList();

            return "";
        }

        @Override
        protected void onPostExecute(Object result){


                if(dialogList.isShowing()){
                    dialogList.dismiss();
                }

        }
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
                startActivity(new Intent(getBaseContext(),pedido.class)
              .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }
    @Override
    protected void onResume(){
        super.onResume();

        LlenarModelItems();
        adapter1=new CustomAdapter(this,modelItems);
        lproductos.setAdapter(adapter1);

    }

}

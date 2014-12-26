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
import android.view.KeyEvent;
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

import com.marzam.com.appventas.Gesture.Dib_firma;
import com.marzam.com.appventas.KPI.KPI_General;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Sincronizacion.envio_pedido;
import com.marzam.com.appventas.WebService.WebServices;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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
    AlertDialog alertDialog;
    AlertDialog alertDialog_picker;
    Button boton1;
    Button boton2;
    Button boton3;
    Button boton4;
    Button boton5;
    Button boton6;
    NumberPicker picker;

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
        simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple2,new String[]{"A","B","C"},new int[]{R.id.textView62,R.id.textView63,R.id.textView64});
        lista.setAdapter(simpleAdapter);
        NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec=(DecimalFormat)nf;
        txtMonto.setText("Monto actual: $"+dec.format(monto));

      lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              ShowDialog(i);
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

                        simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple2,new String[]{"A","B","C"},new int[]{R.id.textView62,R.id.textView63,R.id.textView64});
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
                simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple2,new String[]{"A","B","C"},new int[]{R.id.textView62,R.id.textView63,R.id.textView64});
                lista.setAdapter(simpleAdapter);
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
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {


               Actualizar();

            }

        });



        alertDialog = alertDialogBuilder.create();
        alertDialog.show();


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
                Actualizar();

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
        alert.setMessage("Desea enviar el pedido?");
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

                int cant=Integer.parseInt(txtCantidad.getText().toString());

                AgregarProducto(codigo,0,1);
                AgregarProducto(codigo,cant,1);


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

    public void Productos(){


        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=null;
        int Cantidad=0;
        Double precio=0.00;

        String query="select descripcion,precio_final,Cantidad,codigo  from productos where isCheck=1";

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

    public String ObtenerValoresdeFilter(String Item){

        String[] split=Item.replace("{","").replace("}","").split(",");

        String cantidad= split[3].replace("C=Cantidad: ","").trim();
        String codigo=split[0].replace("D=codigo: ","");




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

            Actualizar();

            db.close();
            lite.close();


        }else {
            db.execSQL("update productos set  Cantidad=" + cantidad + ",isCheck=0 where codigo='" + ean + "'");

            Actualizar();
            db.close();
            lite.close();
        }

            Actualizar();

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
                Actualizar();
                cont[0]=0;
                txt3.setText("0");
                alertDialog.dismiss();

            }
        });
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actualizar();
                AgregarProducto(codigo,1,1);
                int val=Integer.parseInt(info[2]);
                txt3.setText(""+((val+ cont[0])+1));
                cont[0]++;

            }
        });
        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actualizar();
                AgregarProducto(codigo, 2, 1);
                int val=Integer.parseInt(info[2]);
                txt3.setText(""+((val+ cont[0])+2));
                cont[0]+=2;
            }
        });
        boton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actualizar();
                AgregarProducto(codigo,5,1);
                int val=Integer.parseInt(info[2]);
                txt3.setText(""+((val+ cont[0])+5));
                cont[0]+=5;

            }
        });
        boton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actualizar();
                AgregarProducto(codigo,10,1);
                int val=Integer.parseInt(info[2]);
                txt3.setText(""+((val+ cont[0])+10));
                cont[0]+=10;
            }
        });
        boton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actualizar();
                alertDialog.dismiss();
                ShowDialog_picker(codigo);
            }
        });

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

            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setTitle("Envio de pedido");
            alert.setIcon(android.R.drawable.ic_dialog_info);

            if(progress.isShowing()) {
                String res=String.valueOf(result);
                if(res!="")
                    alert.setMessage(res);
                else
                    alert.setMessage("Pedido enviado exitosamente");

                progress.dismiss();

                alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getBaseContext(), KPI_General.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        finish();
                    }
                });

                AlertDialog alertDialog=alert.create();
                alertDialog.show();
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
        Actualizar();
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

    public void Actualizar(){
        monto=0.00;
        Productos();
        lista=(ListView)findViewById(R.id.listView);
        simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple2,new String[]{"A","B","C"},new int[]{R.id.textView62,R.id.textView63,R.id.textView64});
        lista.setAdapter(simpleAdapter);
        NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec=(DecimalFormat)nf;
        txtMonto.setText("Monto actual: $"+dec.format(monto));
        txtBuscar.setText("");
    }
}

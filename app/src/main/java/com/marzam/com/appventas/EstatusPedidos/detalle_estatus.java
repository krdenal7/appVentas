package com.marzam.com.appventas.EstatusPedidos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class detalle_estatus extends Activity {

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

    private String codigoList;
    private EditText txt3Cant;
    String id_pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_estatus);

        context=this;
        id_pedido=getIntent().getStringExtra("id_pedido");
        txtMonto=(TextView)findViewById(R.id.textView10);
        txtBuscar=(EditText)findViewById(R.id.editText3);
        btnClear=(ImageButton)findViewById(R.id.imageButton);
        Productos();

        lista=(ListView)findViewById(R.id.listEncabezado);
        simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple2,
                new String[]{"A","B","C","E"},new int[]{R.id.textView62,R.id.textView63,R.id.textView64,R.id.textViewSub}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                convertView = super.getView(position, convertView, parent);
                HashMap<String,?> val= (HashMap<String, ?>) simpleAdapter.getItem(position);
                String dev=val.get("F").toString();
                ImageView imageView=(ImageView)convertView.findViewById(R.id.imageView);

                boolean res=false;

                if(dev.equals("S"))
                    res=true;
                if(dev.equals("Y"))
                    res=true;
                if(dev.isEmpty())
                    res=true;

                if(res==true){

                    imageView.setImageResource(R.drawable.img);

                }
                else {

                    imageView.setImageResource(0);

                }

                if (position % 2 == 0) {
                    convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }else {
                    convertView.setBackgroundColor(Color.parseColor("#F2F2F2"));
                }
                //return super.getView(position, convertView, parent);
                return convertView;
            }
        };
        lista.setAdapter(simpleAdapter);
        NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec=(DecimalFormat)nf;
        dec.setMaximumFractionDigits(2);
        dec.setMinimumFractionDigits(2);
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
                    detalle_estatus.this.simpleAdapter.getFilter().filter(charSequence);
                    Filter cont = simpleAdapter.getFilter();
                    if (cont != null)
                        lista.setAdapter(simpleAdapter);
                }else {

                    simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple2,new String[]{"A","B","C","E"},new int[]{R.id.textView62,R.id.textView63,R.id.textView64,R.id.textViewSub}){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {

                            convertView = super.getView(position, convertView, parent);
                            HashMap<String,?> val= (HashMap<String, ?>) simpleAdapter.getItem(position);
                            String dev=val.get("F").toString();
                            ImageView imageView=(ImageView)convertView.findViewById(R.id.imageView);

                            boolean res=false;

                            if(dev.equals("S"))
                                res=true;
                            if(dev.equals("Y"))
                                res=true;
                            if(dev.isEmpty())
                                res=true;

                            if(res==true){

                                imageView.setImageResource(R.drawable.img);

                            }
                            else {

                                imageView.setImageResource(0);

                            }

                            if (position % 2 == 0) {
                                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            }else {
                                convertView.setBackgroundColor(Color.parseColor("#F2F2F2"));
                            }
                            //return super.getView(position, convertView, parent);
                            return convertView;
                        }
                    };
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
                simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple2,new String[]{"A","B","C","E"},
                        new int[]{R.id.textView62,R.id.textView63,R.id.textView64,R.id.textViewSub}){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        convertView = super.getView(position, convertView, parent);
                        HashMap<String,?> val= (HashMap<String, ?>) simpleAdapter.getItem(position);
                        String dev=val.get("F").toString();
                        ImageView imageView=(ImageView)convertView.findViewById(R.id.imageView);

                        boolean res=false;

                        if(dev.equals("S"))
                            res=true;
                        if(dev.equals("Y"))
                            res=true;
                        if(dev.isEmpty())
                            res=true;

                        if(res==true){

                            imageView.setImageResource(R.drawable.img);

                        }
                        else {

                            imageView.setImageResource(0);

                        }

                        if (position % 2 == 0) {
                            convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }else {
                            convertView.setBackgroundColor(Color.parseColor("#F2F2F2"));
                        }

                        return convertView;
                    }
                };
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
        alertDialogBuilder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                int cantidad = new Integer("0"+detalle_estatus.this.txt3Cant.getText());
                int isCheqket = 0;
                if(cantidad>0) isCheqket = 1;

                AgregarProducto(detalle_estatus.this.codigoList, cantidad, isCheqket);
                Actualizar();

            }

        });



        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));

    }

    public void Productos(){


        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs=null;
        int Cantidad=0;
        Double precio=0.00;

        String query="select descripcion,precio_final,Cantidad,codigo,precio_oferta,(Cantidad*precio_final)as sub,devolucion  from productos where isCheck=1";

        rs=db.rawQuery(query,null);
        data=new ArrayList<HashMap<String, ?>>();
        producto_row=new HashMap<String, String>();

        NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec=(DecimalFormat)nf;
        dec.setMaximumFractionDigits(2);
        dec.setMinimumFractionDigits(2);

        while (rs.moveToNext()){

            double precio_=Double.parseDouble(rs.getString(1));
            double sub=Double.parseDouble(rs.getString(5));

            producto_row.put("A",rs.getString(0));
            producto_row.put("B","$"+dec.format(precio_));
            producto_row.put("C",""+rs.getString(2));
            producto_row.put("D","codigo: "+rs.getString(3));
            producto_row.put("E","$"+dec.format(sub));
            producto_row.put("F",rs.getString(6));
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

    public String ObtenerValoresdeFilter(String Item){

        String[] split=Item.replace("{","").replace("}","").split(",");

        String cantidad= split[3].replace("C=Cantidad: ","").trim();
        String codigo=split[0].replace("D=codigo: ","");




        return codigo;
    }

    public void AgregarProducto(String ean,int cantidad,int isChecked){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        if(cantidad!=0){
            Cursor rs=db.rawQuery("select Cantidad from productos where codigo='"+ean+"'",null);
            int pzas=0;
            /*if(rs.moveToFirst()){

                pzas=rs.getInt(0);

            }*/
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

    public void Actualizar(){
        monto=0.00;
        Productos();
        lista=(ListView)findViewById(R.id.listEncabezado);
        simpleAdapter=new SimpleAdapter(context,data,R.layout.list_row_simple2,new String[]{"A","B","C","E"},new int[]{R.id.textView62,R.id.textView63,R.id.textView64,R.id.textViewSub}){
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
        lista.setAdapter(simpleAdapter);
        NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec=(DecimalFormat)nf;
        dec.setMaximumFractionDigits(2);
        dec.setMinimumFractionDigits(2);
        txtMonto.setText("Monto actual: $"+dec.format(monto));
        txtBuscar.setText("");
    }

    public void Eventos_Button(View view, final String  codigo){

        boton1=(Button)view.findViewById(R.id.button12);
        boton2=(Button)view.findViewById(R.id.button13);
        boton3=(Button)view.findViewById(R.id.button14);
        boton4=(Button)view.findViewById(R.id.button15);
        boton5=(Button)view.findViewById(R.id.button16);


        TextView txt1=(TextView)view.findViewById(R.id.textView50);
        TextView txt2=(TextView)view.findViewById(R.id.textView52);
        final EditText txt3=(EditText)view.findViewById(R.id.editText6);

        final String[] info=ObtenerInfoProductos(codigo);

        txt1.setText(info[0]);
        txt2.setText(info[1]);
        txt3.setText(info[2]);
        txt3.setSelection(txt3.getText().length(), txt3.getText().length());

        detalle_estatus.this.codigoList = codigo;
        detalle_estatus.this.txt3Cant = txt3;

        final int[] cont = {0};

        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //AgregarProducto(codigo,0,0);
                //Actualizar();
                cont[0]=0;
                txt3.setText("0");
                txt3.setSelection(txt3.getText().length(), txt3.getText().length());
                //alertDialog.dismiss();
            }
        });
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Actualizar();
                //AgregarProducto(codigo,1,1);
                int val=Integer.parseInt(info[2]);
                txt3.setText(((new Integer("0"+txt3.getText())).intValue() +1)+"");
                txt3.setSelection(txt3.getText().length(), txt3.getText().length());
                cont[0]++;

            }
        });
        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Actualizar();
                // AgregarProducto(codigo, 2, 1);
                int val=Integer.parseInt(info[2]);
                txt3.setText(((new Integer("0"+txt3.getText())).intValue()+2)+"");
                txt3.setSelection(txt3.getText().length(), txt3.getText().length());
                cont[0]+=2;
            }
        });
        boton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Actualizar();
                // AgregarProducto(codigo,5,1);
                int val=Integer.parseInt(info[2]);
                txt3.setText(((new Integer("0"+txt3.getText())).intValue() +5)+"");
                txt3.setSelection(txt3.getText().length(), txt3.getText().length());
                cont[0]+=5;

            }
        });
        boton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Actualizar();
                //AgregarProducto(codigo,10,1);
                int val = Integer.parseInt(info[2]);
                txt3.setText(((new Integer("0"+txt3.getText())).intValue()+10)+"");
                txt3.setSelection(txt3.getText().length(), txt3.getText().length());
                cont[0]+=10;
            }
        });

    }

    public String[] ObtenerInfoProductos(String ean){
        String[] info=new String[3];

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select descripcion,precio_oferta,Cantidad from productos where codigo='"+ean+"'",null);

        if(rs.moveToFirst()){
            info[0]=rs.getString(0);
            info[1]=rs.getString(1);
            info[2]=rs.getString(2);
        }


        return info;
    }

    @Override
    protected void onResume(){
        super.onResume();
        Actualizar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detalle_estatus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.CatalogoCD:
                Intent intent=new Intent(context,catalogo_edit.class);
                startActivity(intent);
                break;
            case R.id.GuardarCD:
                if(Verificar_productos()) {
                    ShowGuardar();
                }else {
                    ShowNoItems();
                }
                break;
            case R.id.CancelarCD:
                ShowCancelar();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void ShowGuardar(){

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Guardar pedido");
        alert.setMessage("¿Desea guardar los cambios?");
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new TaskGuardar().execute(id_pedido);
            }
        });
        alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();

    }

    public void ShowCancelar(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Cancelar");
        alert.setMessage("Desea cancelar los cambios");
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UpdateSesion();
                Intent intent=new Intent(context,RespuestaPedidos.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

            }
        });
        alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();

    }

    public void UpdateSesion(){
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        db.execSQL("update sesion_cliente set Sesion=2 where id_cliente=(select id_cliente from sesion_cliente where Sesion=1)");


    }

    public void ShowNoItems(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage("No hay productos por agregar.Favor de verificar");
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();
    }

    public class TaskGuardar extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute(){
            progress=ProgressDialog.show(context,"Guardar","Guardando pedido...",true,false);

        }

        @Override
        protected String doInBackground(String... strings) {

            new Save(context,strings[0]);
            return strings[0];
        }
        @Override
        protected void onPostExecute(String s){

            if(progress.isShowing()){
                progress.dismiss();
                UpdateSesion();
                Intent i=new Intent(context,RespuestaPedidos.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);

            }

        }
    }

    public Boolean Verificar_productos(){

        CSQLite lite=new CSQLite(context);

        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select * from productos where isCheck=1",null);

        if(rs.moveToFirst())
            return true;

        return false;
    }

    public void ShowConfirmacion(){

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage("¿Desa salir de la edición?");
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UpdateSesion();
                Intent intent=new Intent(context,RespuestaPedidos.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();

    }

    @Override
    public void onBackPressed(){
        ShowConfirmacion();
    }
}

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
import android.graphics.Paint;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.marzam.com.appventas.Adapters.CustomAdapter;
import com.marzam.com.appventas.Adapters.Model;
import com.marzam.com.appventas.Email.Mail;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Tab_pedidos.pcatalogo;
import com.marzam.com.appventas.Tab_pedidos.pedido;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class catalogo_edit extends Activity {

    Context context;
    ListView lproductos;
    EditText EditBuscar;
    Model[] modelItems;
    CustomAdapter adapter1;
    HashMap<String,String> producto_row;
    static ArrayList<HashMap<String,?>> data=null;
    SimpleAdapter simpleAdapter;
    CSQLite lite;
    AlertDialog alertDialog;
    AlertDialog alertDescripcion;
    Button boton1;
    Button boton2;
    Button boton3;
    Button boton4;
    Button boton5;
    Button btnDetalle;
    ProgressDialog dialogList;
    Spinner spFiltro;
    Mail m;
    String from="pcatalogo.class";
    String subject;
    String body;
    TextView cantidadTextView;
    private String codigoList;
    private EditText txt3Cant;
    String id_pedido="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo_edit);
        setTitle("Catálogo");
        context=this;

        id_pedido=getIntent().getStringExtra("id_pedido");

        EditBuscar=(EditText)findViewById(R.id.editText4);
        EditBuscar.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        lproductos=(ListView)findViewById(R.id.listView2);

        spFiltro=(Spinner)findViewById(R.id.spinner2);
        ArrayAdapter arrayAdapter=new ArrayAdapter
        (context,android.R.layout.simple_list_item_1,new String[]{"Todo","Solo ofertas"});
        spFiltro.setAdapter(arrayAdapter);

        LlenarModelItems(false);
        adapter1=new CustomAdapter(this,modelItems,spFiltro.getSelectedItemPosition()==1?true:false);
        lproductos.setAdapter(adapter1);



        EditBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {


                try {
                    if (charSequence.length() >= 2) {

                        catalogo_edit.this.simpleAdapter.getFilter().filter(charSequence);
                        lproductos.setAdapter(simpleAdapter);
                        simpleAdapter.notifyDataSetChanged();

                    } else {
                        //LLenarList();
                        boolean fil=false;
                        if(spFiltro.getSelectedItemPosition()==1)
                            fil=true;

                        LlenarModelItems(fil);
                        adapter1 = new CustomAdapter(context, modelItems,spFiltro.getSelectedItemPosition()==1?true:false);
                        lproductos.setAdapter(adapter1);
                    }
                } catch (Exception e) {

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

                if (EditBuscar.getText().length() > 0) {
                    LlenarModelItems(false);
                    EditBuscar.setText("");
                    adapter1 = new CustomAdapter(context, modelItems,spFiltro.getSelectedItemPosition()==1?true:false);
                    lproductos.setAdapter(adapter1);
                }

            }
        });

        lproductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                cantidadTextView = (TextView) view.findViewById(R.id.textView29);
                ShowDialog(i);

            }
        });

        lproductos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    HashMap<String, ?> mdesc = (HashMap<String, ?>) simpleAdapter.getItem(i);
                    String editB = EditBuscar.getText().toString().trim();

                    if (!editB.isEmpty()) {
                        if (editB.length() >= 2) {

                            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                            DecimalFormat dec = (DecimalFormat) nf;
                            dec.setMaximumFractionDigits(2);
                            dec.setMinimumFractionDigits(2);

                            String descripcion=mdesc.get("A").toString();
                            String precio_p = mdesc.get("M").toString();
                            String precioF=mdesc.get("E").toString().replace("$","");
                            String lab=mdesc.get("H").toString();
                            String sustancia = mdesc.get("L").toString();

                            Double precio_publico;
                            Double precio_final;
                            Double ganancia;

                            try{
                                precio_publico=Double.parseDouble(precio_p);
                            }catch (Exception e){
                                Toast.makeText(context,"Error precioP",Toast.LENGTH_LONG).show();
                                precio_publico=0.00;
                            }
                            try{
                                precio_final=Double.parseDouble(precioF);
                            }catch (Exception e){
                                Toast.makeText(context,"Error preciof",Toast.LENGTH_LONG).show();
                                precio_final=0.00;
                            }
                            ganancia=precio_publico-precio_final;
                            ShowDescripcion("$"+dec.format(precio_publico),sustancia,lab,"$"+dec.format(ganancia),descripcion);
                        }
                    }
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return  false;
                }
            }
        });

        btnDetalle=(Button)findViewById(R.id.button);
        btnDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getBaseContext(),tab_pedidos.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();

            }
        });

        spFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                EditBuscar.setText("");
                new TaskFiltro().execute("");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });


        new UpdateList().execute("");
        dialogList=ProgressDialog.show(context,"Catálogo","Generando..",true,false);

    }

    public void ShowDialog(final int posicion){



        String Item=String.valueOf(simpleAdapter.getItem(posicion));
        final String codigo=ObtenerValoresdeFilter(Item);

        LayoutInflater inflater=getLayoutInflater();
        View viewButton=inflater.inflate(R.layout.botones_cantidad,null);
        Eventos_Button(viewButton, codigo);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Seleccione una cantidad");
        alertDialogBuilder.setView(viewButton);
        alertDialogBuilder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {


                int cantidad = new Integer("0"+catalogo_edit.this.txt3Cant.getText());
                int isCheqket = 0;
                if(cantidad>0) isCheqket = 1;

                AgregarProducto(catalogo_edit.this.codigoList, cantidad, isCheqket);

            }

        });



        alertDialog = alertDialogBuilder.create();
        if(alertDescripcion!=null){
            if(!alertDescripcion.isShowing()) {
                alertDialog.show();
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
            }
        }else {
            alertDialog.show();
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
        }



    }

    public void ShowDescripcion(String precio,String sustancia,String lab,String gan,String desc){

        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_descripcion,null);

        TextView txtSus=(TextView)view.findViewById(R.id.textView2);
        txtSus.setText(sustancia);

        TextView txtPrec=(TextView)view.findViewById(R.id.textView5);
        txtPrec.setText(precio);

        TextView txtLab=(TextView)view.findViewById(R.id.textView4);
        txtLab.setText(lab);

        TextView txtGan=(TextView)view.findViewById(R.id.textView8);
        txtGan.setText(gan);

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle(desc);
        alert.setView(view);
        alert.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDescripcion=alert.create();
        alertDescripcion.show();

        Button pbutton = alertDescripcion.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));


    }

    public void LlenarHasmap(boolean filtro){

        if(lite!=null)
            lite.close();

        NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec=(DecimalFormat)nf;
        dec.setMaximumFractionDigits(2);
        dec.setMinimumFractionDigits(2);


        for(int i=0;i<4;i++) {
            lite=new CSQLite(context);
            SQLiteDatabase db=lite.getWritableDatabase();
            Cursor rs = null;

            String where="";

            if(filtro==true)
                where="where o.descuento > 0";

            String filt=WhereFiltro(filtro);

            String query=String.format("select distinct descripcion,precio,p.Cantidad,p.codigo,precio_final,clasificacion_fiscal,o.descuento, " +
                    "p.laboratorio, e.cantidad,devolucion,isCheck,sustancia_activa,p.precio_publico from productos as p left join " +
                    " ofertas as o on p.codigo=o.codigo " +
                    " left join existencias as e on p.codigo=e.codigo " +
                    " left join productos_obligados as po on p.codigo=po.codigo " +
                    "%s %s",where,filt);

            try {

                rs = db.rawQuery(query, null);
                data = new ArrayList<HashMap<String, ?>>();
                producto_row = new HashMap<String, String>();

                while (rs.moveToNext()) {

                    String oferta = (rs.getString(6) == null) ? "0" : rs.getString(6);
                    double precio=Double.parseDouble(rs.getString(1));
                    double preciof=Double.parseDouble(rs.getString(4));

                    producto_row.put("A", rs.getString(0));
                    producto_row.put("B", "$"+dec.format(precio));
                    producto_row.put("C", rs.getString(2)+"");
                    producto_row.put("D", rs.getString(3)+"");
                    producto_row.put("E", "$"+dec.format(preciof));
                    producto_row.put("F", rs.getString(5)+"");
                    producto_row.put("G", oferta+ "%");
                    producto_row.put("H", rs.getString(7)+"");
                    producto_row.put("I", rs.getString(8)!=null?rs.getString(8):"0");
                    producto_row.put("J",rs.getString(9));
                    producto_row.put("K",rs.getString(10));
                    producto_row.put("L",rs.getString(11));
                    producto_row.put("M",rs.getString(12));
                    data.add(producto_row);
                    producto_row = new HashMap<String, String>();
                }

                rs.close();
                db.close();
                lite.close();
                break;
            } catch (Exception e) {
                continue;
            }
        }

    }//Con email

    public void LlenarModelItems(boolean filtro){

        if(lite!=null)
            lite.close();

        for(int i=0;i<4;i++) {
            lite = new CSQLite(context);
            SQLiteDatabase db = lite.getWritableDatabase();
            Cursor rs = null;

            try {

                String where="";

                if(filtro==true)
                    where = "where o.descuento > 0";


                String filt=WhereFiltro(filtro);

                String query=String.format("select distinct descripcion,isCheck,p.Cantidad,precio,p.codigo,precio_final,clasificacion_fiscal" +
                        ",o.descuento, p.laboratorio, e.cantidad ,devolucion,sustancia_activa,precio_publico " +
                        "from productos as p left join ofertas as o on p.codigo=o.codigo " +
                        "left join existencias as e on p.codigo=e.codigo " +
                        "left join productos_obligados as po on p.codigo=po.codigo " +
                        " %s %s limit 1000 ",where,filt);

                rs = db.rawQuery(query, null);

                modelItems = new Model[rs.getCount()];


                int cont = 0;

                while (rs.moveToNext()) {

                    String of = rs.getString(7);
                    String oferta;

                    if (of == null) {
                        oferta = "0";
                    } else {
                        oferta = of;
                    }

                    String valDev=rs.getString(10).toUpperCase();
                    boolean dev=false;

                    if(valDev.equals("S"))
                        dev=true;
                    if(valDev.equals("Y"))
                        dev=true;
                    if(valDev.isEmpty())
                        dev=true;

                    modelItems[cont] = new Model(rs.getString(0), rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), oferta, rs.getString(8), rs.getString(9),dev,rs.getString(11),rs.getString(12));
                    cont++;
                }

                break;

            } catch (Exception e) {
                subject = "pcatalogo.java-LlenarmodelItems";
                body = "Agente:" + ObtenerAgenteActivo()
                        + "Cursor:" + rs.toString()
                        + "DB: " + db.toString()
                        + "\nError: " + e.toString();
                new sendEmail().execute("");
                continue;
            }


        }


    }//Con email

    public String ObtenerValoresdeFilter(String Item) {
        String codigo="";
        try {
            String[] split = Item.replace("{", "").replace("}", "").split(",");
            String cantidad = split[3].replace("C=Cantidad: ", "").trim();
            codigo = split[0].replace("D=", "");
        }catch (Exception e){
            subject="ObtenerValoresFilter";
            body="Item: "+ Item +"Error: "+ e.toString();
            new sendEmail().execute("");
        }
        return codigo;
    }//Con email

    public String[] ObtenerInfoProductos(String ean){
        String[] info=new String[3];
        String consulta="select descripcion,precio_final,Cantidad from productos where codigo='" + ean + "'";

        try {
            lite=new CSQLite(context);
            SQLiteDatabase db=lite.getWritableDatabase();

            Cursor rs = db.rawQuery(consulta, null);

            if (rs.moveToFirst()) {
                info[0] = rs.getString(0);
                info[1] = rs.getString(1);
                info[2] = rs.getString(2);
            }

        }catch (Exception e){
            subject="pcatalogo.java-ObtenerInfoProductos";
            body="Agente: "+ObtenerAgenteActivo()+"ConsultaSQL: "+ consulta+"\nError: "+e.toString();
            new sendEmail().execute("");
        }

        return info;
    }//Con email

    public void Eventos_Button(View view, final String  codigo){

        catalogo_edit.this.codigoList = codigo;
        boton1=(Button)view.findViewById(R.id.button12);
        boton2=(Button)view.findViewById(R.id.button13);
        boton3=(Button)view.findViewById(R.id.button14);
        boton4=(Button)view.findViewById(R.id.button15);
        boton5=(Button)view.findViewById(R.id.button16);

        TextView txt1=(TextView)view.findViewById(R.id.textView50);
        TextView txt2=(TextView)view.findViewById(R.id.textView52);
        final EditText txt3=(EditText)view.findViewById(R.id.editText6);
        catalogo_edit.this.txt3Cant = txt3;

        final String[] info=ObtenerInfoProductos(codigo);

        try {
            txt1.setText(info[0]);
            txt2.setText(info[1]);
            txt3.setText(info[2]);
            txt3.setSelection(txt3.getText().length(), txt3.getText().length());
        }catch (Exception e){
            subject="pcatalogo.java-Eventos_Button";
            body="Agente: "+ObtenerAgenteActivo()+"\nError al consultar arreglo info[]: "+ e.toString();
            new sendEmail().execute("");
        }

        final int[] cont = {0};

        try {
            boton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //AgregarProducto(codigo, 0, 0);
                    cont[0] = 0;
                    txt3.setText("0");
                    txt3.setSelection(txt3.getText().length(), txt3.getText().length());

                }
            });
            boton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //AgregarProducto(codigo, 1, 1);
                    int val = Integer.parseInt(info[2]);
                    txt3.setText(((new Integer("0"+txt3.getText())).intValue() + 1)+"");
                    txt3.setSelection(txt3.getText().length(), txt3.getText().length());
                    cont[0]++;

                }
            });
            boton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //AgregarProducto(codigo, 2, 1);
                    int val = Integer.parseInt(info[2]);
                    txt3.setText(((new Integer("0"+txt3.getText())).intValue() + 2)+"");
                    txt3.setSelection(txt3.getText().length(), txt3.getText().length());
                    cont[0] += 2;
                }
            });
            boton4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //AgregarProducto(codigo, 5, 1);
                    int val = Integer.parseInt(info[2]);
                    txt3.setText(((new Integer("0"+txt3.getText())).intValue() + 5)+"");
                    txt3.setSelection(txt3.getText().length(), txt3.getText().length());
                    cont[0] += 5;

                }
            });
            boton5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // AgregarProducto(codigo, 10, 1);
                    int val = Integer.parseInt(info[2]);
                    txt3.setText(((new Integer("0"+txt3.getText())).intValue() + 10)+"");
                    txt3.setSelection(txt3.getText().length(), txt3.getText().length());
                    cont[0] += 10;
                }
            });
        }catch (Exception e){
            subject="pcatalogo.java-Eventos_Button";
            body="Agente: "+ObtenerAgenteActivo()+"\nError en los eventos del boton: "+ e.toString();
            new sendEmail().execute("");
        }

    }//Con email

    public void AgregarProducto(String ean,int cantidad,int isChecked){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        String query1="select Cantidad from productos where codigo='"+ean+"'";
        String query2="";
        try {
            int pzas = 0;
            if (cantidad != 0) {
                Cursor rs = db.rawQuery(query1, null);

                query2 = "update productos set  Cantidad=" + (cantidad + pzas) + ",isCheck=" + isChecked + " where codigo='" + ean + "'";
                db.execSQL(query2);


                db.close();
                lite.close();


            } else {
                db.execSQL("update productos set  Cantidad=" + cantidad + ",isCheck=0 where codigo='" + ean + "'");
                db.close();
                lite.close();
            }

            cantidadTextView.setText( (cantidad + pzas)+"" );


        }catch (Exception e){
            subject="pcatalogo.java-AgregarProducto";
            body="Agente: "+ObtenerAgenteActivo()+"\nConsulta1: "+ query1+"\nConsulta2: "+query2+"\nError: "+e.toString();
            new sendEmail().execute("");
        }
    }//Con email

    public void LLenarList(){

        boolean fil=false;
        if(spFiltro.getSelectedItemPosition()==1)
            fil=true;

        LlenarHasmap(fil);//llena el arreglo para el simpleAdapter
        simpleAdapter=new SimpleAdapter(context,data,R.layout.row, new String[]
                {"A","B","E","G","C","F","H","I","D"},new int[]{
                R.id.textView12,R.id.textView28,R.id.textView58,R.id.textView59,R.id.textView29,R.id.textViewSubtitle,
                R.id.textView74,R.id.textView72,R.id.textView75})
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                convertView = super.getView(position, convertView, parent);
                HashMap<String,?> val= (HashMap<String, ?>) simpleAdapter.getItem(position);
                String dev=val.get("J").toString();
                String codigo=val.get("D").toString();

                ImageView imgDev=(ImageView)convertView.findViewById(R.id.imageDev);
                TextView  txtCant=(TextView)convertView.findViewById(R.id.textView29);
                TextView  txtPrecio=(TextView)convertView.findViewById(R.id.textView28);
                txtPrecio.setPaintFlags(txtPrecio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                boolean res=false;
                int cant=GetValor(codigo);

                if(dev.equals("S"))
                    res=true;
                if(dev.equals("Y"))
                    res=true;
                if(dev.isEmpty())
                    res=true;

                if(res==true){

                    imgDev.setImageResource(R.drawable.img);

                }
                else {

                    imgDev.setImageResource(0);

                }

                if(cant<=0){
                    txtCant.setText(0+ "");
                    if (position % 2 == 0) {
                        convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        simpleAdapter.notifyDataSetChanged();
                    }else {
                        convertView.setBackgroundColor(Color.parseColor("#F2F2F2"));
                        simpleAdapter.notifyDataSetChanged();
                    }
                }else {
                    txtCant.setText(cant + "");
                    convertView.setBackgroundColor(Color.parseColor("#89BBEE"));
                    simpleAdapter.notifyDataSetChanged();
                }




                return convertView;
            }
        };

    }

    public int GetValor(String codigo){
        int val=0;
        CSQLite lt=new CSQLite(context);
        SQLiteDatabase db=lt.getReadableDatabase();
        Cursor rs=db.rawQuery("select Cantidad from productos where codigo=?",new String[]{codigo});

        if(rs.moveToFirst()){
            val=rs.getInt(0);
        }

        return val;
    }

    public String ObtenerAgenteActivo(){
        String clave = "";
        try {
            lite = new CSQLite(context);
            SQLiteDatabase db = lite.getWritableDatabase();
            Cursor rs = db.rawQuery("select numero_empleado from agentes where Sesion=1", null);
            if (rs.moveToFirst()) {

                clave = rs.getString(0);
            }
        }catch (Exception e){
            subject="pcatalogo.java-ObtenerAgenteActivo()";
            body=e.toString();
            new sendEmail().execute("");
        }

        return clave;
    }

    private class UpdateList extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {

            // LLenarList();

            return "";
        }

        @Override
        protected void onPostExecute(Object result){

      /*if(dialogList.isShowing()){
           dialogList.dismiss();
      }
*/

        }
    }

    private class TaskFiltro extends  AsyncTask<String,Void,Object>{

        @Override
        protected void onPreExecute(){

            if(dialogList==null || !dialogList.isShowing())
                dialogList = ProgressDialog.show(catalogo_edit.this, "Catálogo", "Generando..", true, false);

        }

        @Override
        protected Object doInBackground(String... strings) {


            int val=spFiltro.getSelectedItemPosition();

            if(val==0){

                LLenarList();
                LlenarModelItems(false);

                adapter1=new CustomAdapter(context,modelItems,spFiltro.getSelectedItemPosition()==1?true:false);


            }else {

                if(val==1) {
                    LLenarList();
                    LlenarModelItems(true);
                    adapter1 = new CustomAdapter(context, modelItems,spFiltro.getSelectedItemPosition()==1?true:false);

                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(Object obj){

            if(dialogList.isShowing()){
                dialogList.dismiss();
                lproductos.setAdapter(adapter1);
            }

        }
    }

    public class sendEmail extends AsyncTask<String,Void,Object>{

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

    public String WhereFiltro(boolean filt){

        StringBuilder builder=new StringBuilder();
        String id_cliente=ObtenerId_cliente();


        CSQLite lt=new CSQLite(context);
        SQLiteDatabase db=lt.getReadableDatabase();

        Cursor rs=db.rawQuery("select filtro from clientes_obligados where id_cliente=?",new String[]{id_cliente});

        if(rs.moveToFirst()){
            if(filt==true){
                builder.append("AND ");
            }else {
                builder.append("where ");
            }

            builder.append("po.filtro like '%,"+rs.getString(0)+",%' or po.filtro like '%,0,%'");
        }else{
            builder.append("");
        }
        return builder.toString();
    }

    public String ObtenerId_cliente(){
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select id_cliente from sesion_cliente where Sesion=1",null);


        String id="";

        if(rs.moveToFirst()){
            id=rs.getString(0);
        }
        try {
            rs.close();
            db.close();
            lite.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return id;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.catalogo_edit, menu);
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

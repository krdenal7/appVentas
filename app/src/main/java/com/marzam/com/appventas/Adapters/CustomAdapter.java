package com.marzam.com.appventas.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.marzam.com.appventas.Email.Mail;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Tab_pedidos.pcatalogo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * Created by SAMSUMG on 18/11/2014.
 */
public class CustomAdapter extends ArrayAdapter  implements Filterable {

    Model[] modelitems=null;
    Context context;
    NumberPicker picker;
    TextView Cantidad;
    TextView Precio;
    TextView Precio_neto;
    TextView Clasificacion;
    TextView Oferta;
    TextView existencias;
    CSQLite lite;

    Button boton1;
    Button boton2;
    Button boton3;
    Button boton4;
    Button boton5;

   AlertDialog alertDialog;
    AlertDialog alertDescripcion;

    String from="CustomAdapter";
    String subject;
    String body;
    Mail m;

    private View contentList;
    private int posicionList;
    private EditText txt3Cant;
    private ImageView imgDev;
    private boolean filtro;

    private View convertV;


    public CustomAdapter(Context context, Model[] resource,boolean filtro) {
        super(context, R.layout.row,resource);
        this.context=context;
        this.modelitems=resource;
        this.filtro=filtro;
    }
    @Override
    public View getView(final int position, View convertView,ViewGroup parent){



        try {
                /*Obtiene el contexto de la actividad y la pasa al convertView*/
            convertV = convertView;
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.row, parent, false);

            NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat dec=(DecimalFormat)nf;
            dec.setMaximumFractionDigits(2);
            dec.setMinimumFractionDigits(2);


                /*Botones*/

                /*Botones*/

            int valor = modelitems[position].getCantidad();


            TextView name = (TextView) convertView.findViewById(R.id.textView12);
            // cb = (CheckBox)convertView.findViewById(R.id.checkBoxRow);

            Cantidad = (TextView) convertView.findViewById(R.id.textView29);
            Cantidad.setText( valor+"" );//Envia la cantidad Inicial del producto

            Precio = (TextView) convertView.findViewById(R.id.textView28);
            Precio.setPaintFlags(Precio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            double precio=Double.parseDouble(modelitems[position].getPrecio());
            Precio.setText("$" +dec.format(precio));

            double precio_f=Double.parseDouble( modelitems[position].getPrecio_neto());
            Precio_neto = (TextView) convertView.findViewById(R.id.textView58);
            Precio_neto.setText("$" + dec.format(precio_f));

            Clasificacion = (TextView) convertView.findViewById(R.id.textViewSubtitle);
            Clasificacion.setText(modelitems[position].getClasificacion());

            Oferta = (TextView) convertView.findViewById(R.id.textView59);
            Oferta.setText(modelitems[position].getOferta() + "%");

            existencias = (TextView) convertView.findViewById(R.id.textView72);
            existencias.setText(modelitems[position].getExistencia());

            imgDev=(ImageView)convertView.findViewById(R.id.imageDev);

            if(modelitems[position].getDevolucion()==true){
                imgDev.setImageResource(R.drawable.img);
            }


            name.setText(modelitems[position].getName());//Asigna el nombre a los Texview


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ShowDialog(position, view, convertV);

                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
                    DecimalFormat dec=(DecimalFormat)nf;
                    dec.setMaximumFractionDigits(2);
                    dec.setMinimumFractionDigits(2);
                    String precio_p =modelitems[position].getPrecio_publico();
                    String lab=modelitems[position].getLaboratorio();
                    String pref=modelitems[position].getPrecio_neto();

                    if(precio_p==null)
                        precio_p="0";
                    if(precio_p.trim().isEmpty())
                        precio_p="0";
                    Double prec;

                    try {
                        prec = Double.parseDouble(precio_p);
                    }catch (Exception e){
                        prec = 0.0;
                    }

                    if(pref==null)
                        pref="0";
                    if(pref.trim().isEmpty())
                        pref="0";
                    Double precf=0.00;

                    try {
                        precf = Double.parseDouble(pref);
                    }catch (Exception e){
                        prec = 0.0;
                    }
                    Double gan=prec-precf;
                    ShowDescripcion("$"+dec.format(prec),modelitems[position].getSustancia(),lab,"$"+dec.format(gan));
                    return false;
                }
            });

            if (position % 2 == 0) {
                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }else {
                convertView.setBackgroundColor(Color.parseColor("#F2F2F2"));
            }

            if (modelitems[position].getValue() == 1) {
                //  cb.setChecked(true);
                convertView.setBackgroundColor(Color.parseColor("#89BBEE"));

            }

        }catch (Exception e){
            subject="getView";
            body="Diseño list: "+ e.toString();
            //new sendEmail().execute("");
        }
        return convertView;
    }

    public void ShowDialog( int position , final View view, final View convertView){


     LayoutInflater inflater=((Activity)context).getLayoutInflater();
     View botones=inflater.inflate(R.layout.botones_cantidad,null);
     Evento_Botones(botones,view,position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle( "Seleccione una cantidad");
        alertDialogBuilder.setView(botones);
        alertDialogBuilder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"),new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {

                String cantidad = "0"+CustomAdapter.this.txt3Cant.getText();
                Agregar_Producto(CustomAdapter.this.contentList, new Integer(cantidad), CustomAdapter.this.posicionList);
                notifyDataSetChanged();
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

    public void ShowDescripcion(String precio,String sustancia,String lab,String gan){

        LayoutInflater inflater=((Activity)context).getLayoutInflater();
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
        alert.setTitle("Descripción");
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

    public int AgregarProducto(String ean,int cantidad,int isChecked,View view,int posicion){

        try {
            lite = new CSQLite(context);
            TextView cant = (TextView) view.findViewById(R.id.textView29);

            SQLiteDatabase db = lite.getWritableDatabase();

            if (cantidad != 0) {
                Cursor rs = db.rawQuery("select Cantidad from productos where codigo='" + ean + "'", null);
                int pzas = 0;
                /*if (rs.moveToFirst()) {

                    pzas = rs.getInt(0);

                }*/
                db.execSQL("update productos set  Cantidad=" + (cantidad + pzas) + ",isCheck=" + isChecked + " where codigo='" + ean + "'");

                LlenarModelItems();
                view.setBackgroundColor(Color.parseColor("#89BBEE"));
                cant.setText(""+(cantidad + pzas));

                db.close();
                lite.close();
                return (cantidad + pzas);

            } else {
                db.execSQL("update productos set  Cantidad=" + cantidad + ",isCheck=0 where codigo='" + ean + "'");
                LlenarModelItems();
                if (posicion % 2 == 0) {
                    view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }else {
                    view.setBackgroundColor(Color.parseColor("#F2F2F2"));
                }
                cant.setText(""+cantidad);

                db.close();
                lite.close();
                return cantidad;
            }
        }catch (Exception e){
            subject="AgregarProducto";
            body="Ean: "+ ean +"\nCantidad: "+ cantidad +"\nError: "+e.toString();
            new sendEmail().execute("");
            return cantidad;
        }

    }

    public void Agregar_Producto(View view,int cantidad,int position){

     try {
         int pzas = AgregarProducto(modelitems[position].getEan(), cantidad, 1, view,position);

         if (cantidad != 0) {
             modelitems[position].value = 1;
             modelitems[position].cantidad = pzas;
         } else {
             modelitems[position].value = 0;
             modelitems[position].cantidad = pzas;
         }
     }catch (Exception e){
         subject="Agregar_Producto";
         body="Producto: "+ modelitems[position]+"\nError: "+ e.toString();
         new sendEmail().execute("");
     }

   }

    public void LlenarModelItems(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=null;

        try {

            String where="";

            if(filtro==true)
                where="where o.descuento > 0";

            String filt=WhereFiltro(filtro);

            String query=String.format("select distinct descripcion,isCheck,p.Cantidad,precio,p.codigo,precio_final,clasificacion_fiscal" +
                    ",o.descuento, p.laboratorio, e.cantidad ,devolucion,sustancia_activa,precio_publico " +
                    "from productos as p left join ofertas as o on p.codigo=o.codigo " +
                    "left join existencias as e on p.codigo=e.codigo " +
                    "left join productos_obligados as po on p.codigo=po.codigo " +
                    " %s %s limit 1000 ",where,filt);

            rs = db.rawQuery(query, null);
        }
        catch (Exception e)
        {
           subject="LlenarModelItems";
           body="Error: "+e.toString();
           new sendEmail().execute("");
        }


        modelitems=new Model[rs.getCount()];



        int cont=0;

        while (rs.moveToNext()){

            String valDev=rs.getString(10).toUpperCase();//Revisar
            boolean dev=false;

            if(valDev.equals("S"))
                dev=true;
            if(valDev.equals("Y"))
                dev=true;
            if(valDev.isEmpty())
                dev=true;

            String of = rs.getString(7);
            String oferta;

            if (of == null) {
                oferta = "0";
            } else {
                oferta = of;
            }

            String existencia=rs.getString(9);

            if(existencia==null)
                existencia="0";
            if(existencia.trim().isEmpty())
                existencia="0";
            if(existencia.equals("null"))
                existencia="0";

            modelitems[cont]=new Model(rs.getString(0),rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),
                    rs.getString(5),rs.getString(6),oferta,rs.getString(8),existencia,dev,rs.getString(11),rs.getString(12));
            cont++;
        }

        rs.close();
        db.close();

        lite.close();
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

    public String[] ObtenerInfoProductos(int posicion){
        String[] info=new String[3];
        String query="select descripcion,precio_final,Cantidad from productos where codigo='"+modelitems[posicion].getEan()+"'";

        try {
            lite = new CSQLite(context);
            SQLiteDatabase db = lite.getWritableDatabase();
            Cursor rs = db.rawQuery(query, null);

            if (rs.moveToFirst()) {
                info[0] = rs.getString(0);
                info[2] = rs.getString(2);
                info[1] = rs.getString(1);

            }
        }catch (Exception e){
            subject="ObtenerInfoProductos";
            body="Consulta: "+ query+ "\nError: "+e.toString();
            new sendEmail().execute("");
        }


        return info;
    }

    public void Evento_Botones(View viewBoton, final View content, final int posicion){

        CustomAdapter.this.contentList = content;
        CustomAdapter.this.posicionList = posicion;

try {
    boton1 = (Button) viewBoton.findViewById(R.id.button12);
    boton2 = (Button) viewBoton.findViewById(R.id.button13);
    boton3 = (Button) viewBoton.findViewById(R.id.button14);
    boton4 = (Button) viewBoton.findViewById(R.id.button15);
    boton5 = (Button) viewBoton.findViewById(R.id.button16);


    TextView txt1 = (TextView) viewBoton.findViewById(R.id.textView50);
    TextView txt2 = (TextView) viewBoton.findViewById(R.id.textView52);
    final EditText txt3 = (EditText) viewBoton.findViewById(R.id.editText6);
    CustomAdapter.this.txt3Cant = txt3;

    final String[] info = ObtenerInfoProductos(posicion);

    txt1.setText(info[0]);
    txt2.setText(info[1]);
    txt3.setText(info[2]);
    txt3.setSelection(txt3.getText().length(), txt3.getText().length());

    final int[] cont = {0};

    boton1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            /*Agregar_Producto(content, 0, posicion);*/
            cont[0] = 0;
            txt3.setText("0");
            txt3.setSelection(txt3.getText().length(), txt3.getText().length());
        }
    });
    boton2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Agregar_Producto(content, 1, posicion);
            int val = Integer.parseInt(info[2]);
            txt3.setText(((new Integer("0"+txt3.getText())).intValue() + 1)+"");
            txt3.setSelection(txt3.getText().length(), txt3.getText().length());
            cont[0]++;
        }
    });
    boton3.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //Agregar_Producto(content, 2, posicion);
            int val = Integer.parseInt(info[2]);
            txt3.setText(((new Integer("0"+txt3.getText())).intValue() + 2)+"");
            txt3.setSelection(txt3.getText().length(), txt3.getText().length());
            cont[0] += 2;
        }
    });
    boton4.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //Agregar_Producto(content, 5, posicion);
            int val = Integer.parseInt(info[2]);
            txt3.setText(((new Integer("0"+txt3.getText())).intValue() + 5)+"");
            txt3.setSelection(txt3.getText().length(), txt3.getText().length());
            cont[0] += 5;
        }
    });
    boton5.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //Agregar_Producto(content, 10, posicion);
            int val = Integer.parseInt(info[2]);
            txt3.setText(((new Integer("0"+txt3.getText())).intValue() + 10)+"");
            txt3.setSelection(txt3.getText().length(), txt3.getText().length());
            cont[0] += 10;

        }
    });
}catch (Exception e){
    subject="Evento_Botones";
    body="Error: "+ e.toString();
    new sendEmail().execute("");
}
    }

    public class sendEmail extends AsyncTask<String,Void,Object> {

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



}

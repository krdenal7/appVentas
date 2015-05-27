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
import android.widget.NumberPicker;
import android.widget.TextView;

import com.marzam.com.appventas.Email.Mail;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;


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
    Button boton6;

   AlertDialog alertDialog;
   AlertDialog alertDialog_picker;

    String from="CustomAdapter";
    String subject;
    String body;
    Mail m;



    public CustomAdapter(Context context, Model[] resource) {
        super(context, R.layout.row,resource);
        this.context=context;
        this.modelitems=resource;
    }

    @Override
    public View getView(final int position, View convertView,ViewGroup parent){



        try {
                /*Obtiene el contexto de la actividad y la pasa al convertView*/

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.row, parent, false);


                /*Botones*/

                /*Botones*/

            int valor = modelitems[position].getCantidad();


            TextView name = (TextView) convertView.findViewById(R.id.textView12);
            // cb = (CheckBox)convertView.findViewById(R.id.checkBoxRow);

            Cantidad = (TextView) convertView.findViewById(R.id.textView29);
            Cantidad.setText( valor+"" );//Envia la cantidad Inicial del producto

            Precio = (TextView) convertView.findViewById(R.id.textView28);
            //Precio.setPaintFlags(Precio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            Precio.setText("$" + modelitems[position].getPrecio());


            Precio_neto = (TextView) convertView.findViewById(R.id.textView58);
            Precio_neto.setText("$" + modelitems[position].getPrecio_neto());

            Clasificacion = (TextView) convertView.findViewById(R.id.textViewSubtitle);
            Clasificacion.setText(modelitems[position].getClasificacion());

            Oferta = (TextView) convertView.findViewById(R.id.textView59);
            Oferta.setText(modelitems[position].getOferta() + "%");

            existencias = (TextView) convertView.findViewById(R.id.textView72);
            existencias.setText(modelitems[position].getExistencia());


            name.setText(modelitems[position].getName());//Asigna el nombre a los Texview
            //cb.setClickable(false);
            /*else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
                //  cb.setChecked(false);
            }*/


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ShowDialog(position, view);

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
            new sendEmail().execute("");
        }
        return convertView;
    }

    public void ShowDialog( int position , final View view){


     LayoutInflater inflater=((Activity)context).getLayoutInflater();
     View botones=inflater.inflate(R.layout.botones_cantidad,null);
     Evento_Botones(botones,view,position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle( "Seleccione una cantidad");
        alertDialogBuilder.setView(botones);
        alertDialogBuilder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"),new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {




            }

        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
    }

    public void ShowDialog_picker(final int posicion, final View view){


        final EditText txtCantidad=new EditText(context);
        txtCantidad.setHint("cantidad");
        txtCantidad.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Seleccione una cantidad");
        alert.setView(txtCantidad);
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                int cant = Integer.parseInt(txtCantidad.getText().toString());

                Agregar_Producto(view, 0, posicion);
                Agregar_Producto(view, cant, posicion);

            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog_picker=alert.create();
        alertDialog_picker.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                InputMethodManager imm = (InputMethodManager)((Activity)context).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtCantidad, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        alertDialog_picker.show();
    }

    public int AgregarProducto(String ean,int cantidad,int isChecked,View view,int posicion){

        try {
            lite = new CSQLite(context);
            TextView cant = (TextView) view.findViewById(R.id.textView29);

            SQLiteDatabase db = lite.getWritableDatabase();

            if (cantidad != 0) {
                Cursor rs = db.rawQuery("select Cantidad from productos where codigo='" + ean + "'", null);
                int pzas = 0;
                if (rs.moveToFirst()) {

                    pzas = rs.getInt(0);

                }
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

            rs=db.rawQuery("select distinct descripcion,isCheck,p.Cantidad,precio,p.codigo,precio_final,clasificacion_fiscal,o.descuento, p.laboratorio, e.cantidad " +
                           " from productos as p left join ofertas as o on p.codigo=o.codigo left join existencias as e on p.codigo=e.codigo limit 1000 ",null);

        }catch (Exception e){
           subject="LlenarModelItems";
           body="Error: "+e.toString();
           new sendEmail().execute("");
        }



        modelitems=new Model[rs.getCount()];

        int cont=0;

        while (rs.moveToNext()){
            String oferta=(rs.getString(7)==null)?"0":rs.getString(7);
            modelitems[cont]=new Model(rs.getString(0),rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),oferta,rs.getString(8),rs.getString(9));
            cont++;
        }

        rs.close();
        db.close();

        lite.close();
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


try {
    boton1 = (Button) viewBoton.findViewById(R.id.button12);
    boton2 = (Button) viewBoton.findViewById(R.id.button13);
    boton3 = (Button) viewBoton.findViewById(R.id.button14);
    boton4 = (Button) viewBoton.findViewById(R.id.button15);
    boton5 = (Button) viewBoton.findViewById(R.id.button16);
    boton6 = (Button) viewBoton.findViewById(R.id.button17);

    TextView txt1 = (TextView) viewBoton.findViewById(R.id.textView50);
    TextView txt2 = (TextView) viewBoton.findViewById(R.id.textView52);
    final TextView txt3 = (TextView) viewBoton.findViewById(R.id.textView54);

    final String[] info = ObtenerInfoProductos(posicion);

    txt1.setText(info[0]);
    txt2.setText(info[1]);
    txt3.setText(info[2]);

    final int[] cont = {0};

    boton1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Agregar_Producto(content, 0, posicion);
            cont[0] = 0;
            txt3.setText("0");
        }
    });
    boton2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Agregar_Producto(content, 1, posicion);
            int val = Integer.parseInt(info[2]);
            txt3.setText("" + ((val + cont[0]) + 1));
            cont[0]++;
        }
    });
    boton3.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Agregar_Producto(content, 2, posicion);
            int val = Integer.parseInt(info[2]);
            txt3.setText("" + ((val + cont[0]) + 2));
            cont[0] += 2;
        }
    });
    boton4.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Agregar_Producto(content, 5, posicion);
            int val = Integer.parseInt(info[2]);
            txt3.setText("" + ((val + cont[0]) + 5));
            cont[0] += 5;
        }
    });
    boton5.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Agregar_Producto(content, 10, posicion);
            int val = Integer.parseInt(info[2]);
            txt3.setText("" + ((val + cont[0]) + 10));
            cont[0] += 10;

        }
    });
    boton6.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            alertDialog.dismiss();
            ShowDialog_picker(posicion, content);

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

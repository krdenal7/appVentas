package com.marzam.com.appventas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.AvoidXfermode;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filterable;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.marzam.com.appventas.SQLite.CSQLite;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by SAMSUMG on 18/11/2014.
 */
public class CustomAdapter extends ArrayAdapter  implements Filterable {

    Model[] modelitems=null;
    Context context;
    NumberPicker picker;
    TextView Cantidad;
    TextView Precio;
    CheckBox cb;
    CSQLite lite;

    Button boton1;
    Button boton2;
    Button boton3;
    Button boton4;
    Button boton5;
    Button boton6;


    public CustomAdapter(Context context, Model[] resource) {
        super(context, R.layout.row,resource);
        this.context=context;
        this.modelitems=resource;


    }





    @Override
    public View getView(final int position, View convertView,ViewGroup parent){

    final View viewconvert=convertView;


        /*Obtiene el contexto de la actividad y la pasa al convertView*/

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);


        /*Botones*/
        boton1=(Button)((Activity)context).findViewById(R.id.button6);
        boton2=(Button)((Activity)context).findViewById(R.id.button7);
        boton3=(Button)((Activity)context).findViewById(R.id.button8);
        boton4=(Button)((Activity)context).findViewById(R.id.button9);
        boton5=(Button)((Activity)context).findViewById(R.id.button10);
        boton6=(Button)((Activity)context).findViewById(R.id.button11);
        /*Botones*/

    int valor=modelitems[position].getCantidad();


    TextView name = (TextView)convertView.findViewById(R.id.textView12);
   // cb = (CheckBox)convertView.findViewById(R.id.checkBoxRow);

    Cantidad=(TextView)convertView.findViewById(R.id.textView29);
    Cantidad.setText("Cantidad:"+valor);//Envia la cantidad Inicial del producto

    Precio=(TextView)convertView.findViewById(R.id.textView28);
    Precio.setText("Precio: $"+modelitems[position].getPrecio()+" Oferta: 0%");


    name.setText(modelitems[position].getName());//Asigna el nombre a los Texview
    //cb.setClickable(false);
    if(modelitems[position].getValue()==1) {
      //  cb.setChecked(true);
        convertView.setBackgroundColor(Color.parseColor("#89BBEE"));
    }
    else {
        convertView.setBackgroundColor(Color.TRANSPARENT);
      //  cb.setChecked(false);
    }


        final View finalConvertView = convertView;



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 // CheckBox checkBox=(CheckBox)view.findViewById(R.id.checkBoxRow);
                  final TextView cant=(TextView)view.findViewById(R.id.textView29);

                /*Verifica si no esta seleccionado el check mostrara el popup para seleccionar la cantidad, en caso contratio
                  pasara la cantidad a 0 y deseleccionara el ckeck*/

                // if(modelitems[position].getValue()!=1) {

                   //  view.setBackgroundColor(Color.TRANSPARENT);
                     view.setBackgroundColor(Color.parseColor("#DFDFDF"));//Cambia el color del producto que seleccionaron para que sepan a cual le agregaran la cantidad
                     boton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TextView txtCant=(TextView)finalConvertView.findViewById(R.id.textView29);
                        AgregarProducto(modelitems[position].getEan(), 0, 0,finalConvertView);
                        modelitems[position].value = 0;
                        finalConvertView.setBackgroundColor(Color.TRANSPARENT);
                        // checkBox.setChecked(false);
                        txtCant.setText("Cantidad:0");
                        LlenarModelItems();
                    }
                });
                boton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Agregar_Producto(finalConvertView, 1, position);
                    }
                });
                boton3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Agregar_Producto(finalConvertView, 2, position);
                    }
                });
                boton4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Agregar_Producto(finalConvertView, 5, position);
                    }
                });
                boton5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Agregar_Producto(finalConvertView, 9, position);
                    }
                });
                boton6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShowDialog(position, finalConvertView);
                        LlenarModelItems();
                    }
                });

                     //ShowDialog(position, view);
                //}//else{

                  //  AgregarProducto(modelitems[position].getEan(),0,0);
                  // modelitems[position].value = 0;
                  //  view.setBackgroundColor(Color.TRANSPARENT);
                  // checkBox.setChecked(false);
                  //  cant.setText("Cantidad:0");

              //  }

                   }});


        /*Se agrega el evento de los botones*/






        return convertView;
    }



    public void ShowDialog( int position , final View view){
        llenar_picker();//llena el picker


     final   int pos=position;
    // final   CheckBox checkBox=(CheckBox)view.findViewById(R.id.checkBoxRow);
     final   TextView cant=(TextView)view.findViewById(R.id.textView29);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle( "Seleccione una cantidad");
        alertDialogBuilder.setView(picker);
        alertDialogBuilder.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {


               int cantidad=(picker.getValue())-1;

                if(cantidad<=0){
                    Toast t=Toast.makeText(context,"La cantidad debe ser mayor a 0",Toast.LENGTH_SHORT);
                    t.show();
                }else {


                        AgregarProducto(modelitems[pos].getEan(),cantidad,1,view);
                        modelitems[pos].value = 1;
                        view.setBackgroundColor(Color.parseColor("#89BBEE"));
                        cant.setText("Cantidad: " + cantidad);
                }


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
        String[] nums = new String[20];
        for(int i=0; i<nums.length; i++)
            nums[i] = Integer.toString(i);

        picker.setMinValue(1);
        picker.setMaxValue(nums.length);
        picker.setWrapSelectorWheel(false);
        picker.setDisplayedValues(nums);
        picker.setValue(2);


    }
    public int AgregarProducto(String ean,int cantidad,int isChecked,View view){

        lite=new CSQLite(context);
        TextView cant=(TextView)view.findViewById(R.id.textView29);

        SQLiteDatabase db=lite.getWritableDatabase();

        if(cantidad!=0){
        Cursor rs=db.rawQuery("select Cantidad from productos where codigo='"+ean+"'",null);
        int pzas=0;
        if(rs.moveToFirst()){

            pzas=rs.getInt(0);

        }
        db.execSQL("update productos set  Cantidad="+(cantidad+pzas)+",isCheck="+isChecked+" where codigo='"+ean+"'");

            LlenarModelItems();
            view.setBackgroundColor(Color.parseColor("#89BBEE"));
            cant.setText("Cantidad: " + (cantidad+pzas));

            db.close();
            lite.close();
            return (cantidad+pzas);

        }else {
            db.execSQL("update productos set  Cantidad="+cantidad+",isCheck="+isChecked+" where codigo='"+ean+"'");
            LlenarModelItems();
            view.setBackgroundColor(Color.parseColor("#89BBEE"));
            cant.setText("Cantidad: " + cantidad);

            db.close();
            lite.close();
            return cantidad;
        }


    }
    public void Agregar_Producto(View view,int cantidad,int position){


       int pzas=AgregarProducto(modelitems[position].getEan(),cantidad,1,view);
       modelitems[position].value = 1;
       modelitems[position].cantidad=pzas;



   }
    public void LlenarModelItems(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=null;

        try {
            rs=db.rawQuery("select descripcion,isCheck,Cantidad,precio,codigo  from productos limit 1000",null);
        }catch (Exception e){
            String err="Error:"+e.toString();
            Log.d("Error:", err);
        }



        modelitems=new Model[rs.getCount()];

        int cont=0;

        while (rs.moveToNext()){
            modelitems[cont]=new Model(rs.getString(0),rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4));
            cont++;
        }

        rs.close();
        db.close();

        lite.close();
    }


}

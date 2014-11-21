package com.marzam.com.appventas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.AvoidXfermode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filterable;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

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


    public CustomAdapter(Context context, Model[] resource) {
        super(context, R.layout.row,resource);
        this.context=context;
        this.modelitems=resource;


    }





    @Override
    public View getView(final int position, View convertView,ViewGroup parent){

        final View viewconvert=convertView;

    int valor=modelitems[position].getCantidad();
    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
    convertView = inflater.inflate(R.layout.row, parent, false);
    TextView name = (TextView)convertView.findViewById(R.id.textView12);
    final CheckBox cb = (CheckBox)convertView.findViewById(R.id.checkBoxRow);
    Cantidad=(TextView)convertView.findViewById(R.id.textView29);
    Cantidad.setText("Cantidad:"+valor);//Envia la cantidad Inicial del producto
    name.setText(modelitems[position].getName());//Asigna el nombre a los Texview
    cb.setClickable(false);
    if(modelitems[position].getValue()==1)
        cb.setChecked(true);
    else
        cb.setChecked(false);



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                  CheckBox checkBox=(CheckBox)view.findViewById(R.id.checkBoxRow);
                  TextView cant=(TextView)view.findViewById(R.id.textView29);

                /*Verifica si no esta seleccionado el check mostrara el popup para seleccionar la cantidad, en caso contratio
                  pasara la cantidad a 0 y deseleccionara el ckeck*/

                 if(modelitems[position].getValue()!=1) {
                    ShowDialog(position, view);
                }else{

                    modelitems[position].value = 0;
                    checkBox.setChecked(false);
                    cant.setText("Cantidad:0");

                }

                   }});


        /*Se agrega el evento al checkBox*/





        return convertView;
    }

    public void ShowDialog( int position , View view){
        llenar_picker();//llena el picker


     final   int pos=position;
     final   CheckBox checkBox=(CheckBox)view.findViewById(R.id.checkBoxRow);
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
                        modelitems[pos].value = 1;
                        checkBox.setChecked(true);
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
        String[] nums = new String[1000];
        for(int i=0; i<nums.length; i++)
            nums[i] = Integer.toString(i);

        picker.setMinValue(1);
        picker.setMaxValue(nums.length-1);
        picker.setWrapSelectorWheel(false);
        picker.setDisplayedValues(nums);
        picker.setValue(1);


    }


}

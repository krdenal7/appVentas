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



    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
    convertView = inflater.inflate(R.layout.row, parent, false);
    TextView name = (TextView)convertView.findViewById(R.id.textView12);
    final CheckBox cb = (CheckBox)convertView.findViewById(R.id.checkBoxRow);
    Cantidad=(TextView)convertView.findViewById(R.id.textView28);
    name.setText(modelitems[position].getName());
    if(modelitems[position].getValue()==1)
        cb.setChecked(true);
    else
        cb.setChecked(false);

    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (cb.isChecked()) {

                ShowDialog();

                // Toast t = Toast.makeText(context, modelitems[position].getName(), Toast.LENGTH_SHORT);
                // t.show();
            }

        }
    });


        return convertView;
    }

    public void ShowDialog(){
        llenar_picker();


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle( "Seleccione una cantidad");
        alertDialogBuilder.setView(picker);
        alertDialogBuilder.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {



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

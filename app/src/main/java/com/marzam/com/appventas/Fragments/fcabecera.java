package com.marzam.com.appventas.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;

@SuppressLint("ValidFragment")
public class fcabecera extends Fragment {


    Context context;
    public fcabecera(Context context) {
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();

        //Diseño del FrameLayout

        FrameLayout frameLayout=new FrameLayout(context);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));


        //Diseño de la tabla

        GridLayout gridLayout=new GridLayout(context);
        gridLayout.setLayoutParams(new GridLayout.LayoutParams(new ViewGroup.LayoutParams
        (ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT)));

        gridLayout.setPadding(10,0,10,0);

        gridLayout.setColumnCount(2);

        Cursor rs=db.rawQuery("select tipo_campo,valor,nombre from campos_obligatorios as co inner join campos as c on co.id_campo=c.id_campo",null);
        gridLayout.setRowCount(rs.getCount());

        frameLayout.addView(gridLayout);


        int contador=0;

        while (rs.moveToNext()){

            String tipo_campo=rs.getString(0);
            String valor=rs.getString(1);
            String nombre=rs.getString(2);

         //Asigna el nombre al TextView y lo asigna a la tabla
           TextView textNobre=textView(contador,0,nombre+":");
           gridLayout.addView(textNobre);

          if(tipo_campo.equals("EditText")){

              EditText text=editText(contador,1,valor);
              gridLayout.addView(text);

          }
          if(tipo_campo.equals("Spinner")){
              Spinner sp=spinners(contador,1,valor);
              gridLayout.addView(sp);
          }

           contador++;
        }

        return frameLayout;
    }

    public EditText editText(int row,int column,String valor){
        EditText edit=new EditText(context);

        GridLayout.Spec rows=GridLayout.spec(row);
        GridLayout.Spec col=GridLayout.spec(column);

        GridLayout.LayoutParams params=new GridLayout.LayoutParams(rows,col);
        params.setMargins(5,5,5,0);
        edit.setMaxWidth(19);
        edit.setLayoutParams(params);
        edit.getLayoutParams().width=GridLayout.LayoutParams.WRAP_CONTENT;
        edit.getLayoutParams().height=GridLayout.LayoutParams.WRAP_CONTENT;
        edit.setBackgroundResource(R.drawable.background_txtfile);
        edit.setId(row);
        edit.setInputType(InputType.TYPE_CLASS_TEXT);
        edit.setText(valor==null||valor.isEmpty()?"":valor);

        if(row==0)
        edit.requestFocus();


        return edit;
    }

    public TextView textView(int row,int column,String valor){
         TextView textV=new TextView(context);

         GridLayout.Spec rowG=GridLayout.spec(row);
         GridLayout.Spec col=GridLayout.spec(column);

         GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowG, col);

          if(row==0){
             int len=valor.length();
             int space=16-len;

             for (int i=0;i<=  space;i++)
                  valor+=" ";
          }

         textV.setLayoutParams(params);
         textV.setText(valor);
         textV.setTextColor(Color.parseColor("#ffffff"));



        return textV;
    }

    public Spinner spinners(int row,int column,String valor){
        Spinner spin=new Spinner(context);


        return spin;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }

    @Override
    public void onDetach() {
        super.onDetach();

    }








}

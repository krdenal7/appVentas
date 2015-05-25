package com.marzam.com.appventas.AltaClientesDr;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.marzam.com.appventas.MapsLocation;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;


public class AltaClientes extends Activity {

    Context context;
    EditText txtCP;
    EditText txtFarmacia;
    EditText txtMail;
    EditText txtRFC;
    EditText txtTelefono;
    Button btnsiguiente;
    Button btnLimpiar;
    ImageView viewCheck;
    Bundle bundle=null;
    String[] valores=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_clientes);
        context=this;

        bundle=getIntent().getExtras();

        if(bundle!=null){
            valores=bundle.getStringArray("valores");
        }

    //*Animación*/
        btnsiguiente=(Button)findViewById(R.id.button6);
        btnsiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ValidaCampos()) {

                    Intent i = new Intent(context, Direccion.class);
                    i.putExtra("valores", ObtenerValores());

                    startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    finish();

                }else {
                    Toast.makeText(context,"Debe completar todos los campos",Toast.LENGTH_LONG).show();
                }
            }
        });
        //Animación//


        btnLimpiar=(Button)findViewById(R.id.button9);
        viewCheck=(ImageView)findViewById(R.id.imageView8);
        txtCP=(EditText)findViewById(R.id.editText6);
        txtFarmacia=(EditText)findViewById(R.id.editText7);
        txtMail=(EditText)findViewById(R.id.editText8);
        txtRFC=(EditText)findViewById(R.id.editText9);
        txtTelefono=(EditText)findViewById(R.id.editText10);

        txtCP.requestFocus();
        txtCP.setNextFocusDownId(R.id.editText7);

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarCampos();
            }
        });

        if(valores!=null)
            llenarCampos();


    }

    public void llenarCampos(){
        txtCP.setText(valores[0]);
        txtFarmacia.setText(valores[1]);
        txtMail.setText(valores[2]);
        txtRFC.setText(valores[3]);
        txtTelefono.setText(valores[4]);
    }

    public void limpiarCampos(){
        txtCP.setText("");
        txtFarmacia.setText("");
        txtMail.setText("");
        txtRFC.setText("");
        txtTelefono.setText("");
    }

    public String[] ObtenerValores(){
        String[] val=new String[5];

        val[0]=txtCP.getText().toString();
        val[1]=txtFarmacia.getText().toString();
        val[2]=txtMail.getText().toString();
        val[3]=txtRFC.getText().toString();
        val[4]=txtTelefono.getText().toString();


        return  val;
    }

    public boolean ValidaCampos(){
          Boolean resp=true;
        if(txtCP.getText().toString().equals("")){
            resp= false;
        }
        if(txtFarmacia.getText().toString().equals("")){
            resp= false;
        }
        if(txtMail.getText().toString().equals("")){
            resp= false;
        }
        if(txtRFC.getText().toString().equals("")){
            resp= false;
        }
        if(txtTelefono.getText().toString().equals("")){
            resp= false;
        }

        return resp;
    }

    @Override
    public boolean onKeyDown(int key,KeyEvent event){



        return super.onKeyDown(key, event);
    }

    @Override
    public void onBackPressed(){

        if(bundle!=null){
            bundle.clear();
        }

         startActivity(new Intent(context, MapsLocation.class)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
         finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alta_clientes, menu);
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

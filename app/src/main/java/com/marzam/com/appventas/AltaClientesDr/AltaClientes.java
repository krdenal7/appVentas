package com.marzam.com.appventas.AltaClientesDr;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
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

import java.nio.Buffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AltaClientes extends Activity {

    Context context;
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

        btnLimpiar=(Button)findViewById(R.id.button9);
        viewCheck=(ImageView)findViewById(R.id.imageView8);
        txtFarmacia=(EditText)findViewById(R.id.editText7);
        txtMail=(EditText)findViewById(R.id.editText8);
        txtRFC=(EditText)findViewById(R.id.editText9);
        txtTelefono=(EditText)findViewById(R.id.editText10);


    //*Animación*/
        btnsiguiente=(Button)findViewById(R.id.button6);
        btnsiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (ValidaCampos()) {
                        if (ValidaCorreo(txtMail.getText().toString())) {
                            if (txtTelefono.getText().toString().length() >= 10 || txtTelefono.getText().toString().isEmpty()) {
                                if (ValidarRFC(txtRFC.getText().toString())) {
                                    Intent i = new Intent(context, Direccion.class);
                                    i.putExtra("valores", ObtenerValores());
                                    startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    finish();
                                } else {
                                    Mensaje("Estructura de R.F.C Inválida. Favor de verificar").show();
//                                    Toast.makeText(context, "R.F.C invalido.Contiene caracteres invalidos", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Mensaje("Número de teléfono inválido debe incluir LADA").show();
//                                Toast.makeText(context, "Número de telefono invalido. El tamaño del campo debe de ser de 10 digitos", Toast.LENGTH_SHORT).show();
                            }//Valida telefono
                        } else {
                            Mensaje("Correo inválido.Favor de verificar").show();
//                            Toast.makeText(context, "Correo invalido.Favor de verificar", Toast.LENGTH_SHORT).show();
                        }//Valida correo
                    } else {
                        Mensaje("Debe completar todos los campos").show();
//                        Toast.makeText(context, "Debe completar todos los campos", Toast.LENGTH_SHORT).show();
                    }//Valida Campos

            }
        });
        //Animación//

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarCampos();
            }
        });
        if(valores!=null)
            llenarCampos();

        /*addTextChangedListener*/

        txtFarmacia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {



            }

            @Override
            public void afterTextChanged(Editable editable) {

                InputFilter filter = new InputFilter() {
                    public CharSequence filter(CharSequence source, int start, int end,
                                                         Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            if ( !Character.isLetterOrDigit(source.charAt(i))) {
                                if(!Character.isSpaceChar(source.charAt(i))) {
                                    return "";
                                }
                            }
                        }
                        return null;
                    }
                };

                editable.setFilters(new InputFilter[]{filter});

            }
        });

        txtMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                InputFilter filter = new InputFilter() {
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            if ( !Character.isLetterOrDigit(source.charAt(i))) {
                                if(!Character.toString(source.charAt(i)).equals("_")) {
                                    if(!Character.toString(source.charAt(i)).equals("@")) {
                                        if(!Character.toString(source.charAt(i)).equals("-")) {
                                            if(!Character.toString(source.charAt(i)).equals(".")) {
                                                return "";
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    }
                };

                editable.setFilters(new InputFilter[]{filter});

            }
        });

        txtRFC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {



            }

            @Override
            public void afterTextChanged(Editable editable) {
                InputFilter filter = new InputFilter() {
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {

                        for (int i = start; i < end; i++) {
                                if (!Character.isLetterOrDigit(source.charAt(i))) {
                                    return "";
                                }
                        }
                        return null;
                    }
                };

                editable.setFilters(new InputFilter[]{filter});
                String pal=editable.toString();

                if(editable.length()>=14){
                    editable.clear();
                    for(int i=0;i<13;i++){
                        editable.append(pal.charAt(i));
                    }
                }

            }
        });


    }

    public AlertDialog Mensaje(String mensaje){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage(mensaje);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        return  alertDialog;
    }

    public void llenarCampos(){
        txtFarmacia.setText(valores[0]);
        txtMail.setText(valores[1]);
        txtRFC.setText(valores[2]);
        txtTelefono.setText(valores[3]);
    }

    public void limpiarCampos(){

        txtFarmacia.setText("");
        txtMail.setText("");
        txtRFC.setText("");
        txtTelefono.setText("");
    }

    public String[] ObtenerValores(){
        String[] val=new String[4];

        val[0]=txtFarmacia.getText().toString();
        val[1]=txtMail.getText().toString();
        val[2]=txtRFC.getText().toString();
        val[3]=txtTelefono.getText().toString();


        return  val;
    }

    public boolean ValidaCampos(){
          Boolean resp=true;
        if(txtFarmacia.getText().toString().equals("")){
            resp= false;
        }
        return resp;
    }

    public boolean ValidaCorreo(String email){

        if(email.trim().isEmpty()){
            return  true;
        }else {
            String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            Pattern pattern = Pattern.compile(PATTERN_EMAIL);

            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
    }

    public boolean ValidarRFC(String rfc){

        if(rfc.isEmpty()){
            return true;
        }else {
            String PATTERN_RFC = "^[a-zA-Z]{4}\\d{6}[a-zA-Z0-9]{3}$";
            String PATTERN_RFC2 = "^[a-zA-Z]{3}\\d{6}[a-zA-Z0-9]{3}$";

            Pattern pattern = Pattern.compile(PATTERN_RFC);
            Matcher matcher = pattern.matcher(rfc);

            if (matcher.matches() != false) {

                return true;

            } else {

                pattern = Pattern.compile(PATTERN_RFC2);
                matcher = pattern.matcher(rfc);

                if (matcher.matches() != false) {
                    return true;
                }
            }
            return false;
        }
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

package com.marzam.com.appventas.AltaClientesDr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.marzam.com.appventas.GPS.GPSHelper;
import com.marzam.com.appventas.MapsLocation;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.WebService.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Direccion extends Activity {

    Context context;
    EditText txtColonia;

    EditText txtCP;
    EditText txtCalle;
    EditText txtNoExt;
    EditText txtNoInt;
    EditText txtRef;
    Button btnGuardar;
    GPSHelper gps;
    String lat;
    String lon;

    Bundle bundle;
    String[] valores;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion);
        context=this;

        CrearTabla();

        bundle=getIntent().getExtras();
        gps=new GPSHelper(context);

        if(bundle!=null){
            valores=bundle.getStringArray("valores");
        }

        txtCP=(EditText)findViewById(R.id.editText);
        txtCP.requestFocus();
        txtColonia=(EditText)findViewById(R.id.autoCompleteTextView);
        txtColonia.setNextFocusDownId(R.id.editText12);

        txtCalle=(EditText)findViewById(R.id.editText12);
        txtCalle.setNextFocusDownId(R.id.editText13);

        txtNoExt=(EditText)findViewById(R.id.editText13);
        txtNoInt=(EditText)findViewById(R.id.editText14);
        txtRef=(EditText)findViewById(R.id.editText11);
        btnGuardar=(Button)findViewById(R.id.button7);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtCP.length() >= 5){
                    if (ValidarCampos()) {
                        if (ValidarColonia(txtColonia.getText().toString())) {
                            if (ValidarCalle(txtCalle.getText().toString())) {

                                ShowGuardarCliente();

                            } else {
                                Mensaje("El campo de calle solo permite letras.Favor de verificar").show();
                            /*Toast.makeText(context, "El campo de calle solo permite letras.Favor de verificar", Toast.LENGTH_SHORT).show();*/
                            }
                        } else {
                            Mensaje("El campo de colonia solo permite letras.Favor de verificar").show();
                        /*Toast.makeText(context, "El campo de colonia solo permite letras.Favor de verificar", Toast.LENGTH_SHORT).show();*/
                        }
                    } else {
                        Mensaje("Debe completar los campos marcados.").show();
                        // Toast.makeText(context, "Debe completar los campos marcados con *", Toast.LENGTH_SHORT).show();
                    }
            }else{
                    Mensaje("El código postal debe contener 5 dígitos").show();
            }

            }
        });


        lat=gps.getLatitude();
        lon=gps.getLongitude();

        try {
            Double latitud = Double.parseDouble(lat);
            Double longitud = Double.parseDouble(lon);

            if (latitud == 0 && longitud == 0) {
                lat = "19.552829";
                lon = "-99.05254";
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        txtRef.addTextChangedListener(new TextWatcher() {
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
                                  String pal=String.valueOf(source.charAt(i));
                                     if(!pal.equals("\n")) {
                                         return "";
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

        txtCalle.addTextChangedListener(new TextWatcher() {
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
                String pal=editable.toString();

                if(editable.length()>=51){
                    editable.clear();
                    for(int i=0;i<50;i++){
                        editable.append(pal.charAt(i));
                    }
                }
            }
        });

        txtColonia.addTextChangedListener(new TextWatcher() {
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

        txtNoExt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

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

        txtNoInt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

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

    }

    public void CrearTabla(){
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS RelacionClientes(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,id_largo varchar(50),id_corto varchar(50))");

        db.close();
        lite.close();

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

    public void GuardarClienteDr(String idCte){


        String id_sucursal=ObtenerAlmacen();

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("id_cliente", idCte);
        values.put("nombre",valores[0]);
        values.put("rfc", valores[2]);
        values.put("correo", valores[1]);
        values.put("telefono", valores[3]);
        values.put("cp", txtCP.getText().toString());
        values.put("colonia",txtColonia.getText().toString());
        values.put("calle", txtCalle.getText().toString());
        values.put("referencias", txtRef.getText().toString());
        values.put("no_exterior",txtNoExt.getText().toString());
        values.put("delegacion", "");
        values.put("estado","");
        values.put("id_almacen",id_sucursal);
        values.put("ruta","");
        values.put("no_interior", txtNoInt.getText().toString());
        values.put("estatus","10");

        try{
            db.insertOrThrow("clientesDr",null,values);
        }catch (Exception e){
            String a=e.toString();
            Log.e("TabDr",a);
        }

        db.close();
        lite.close();

    }

    public void GuardarClienreSm(String idCte){

        String id_sucursal=ObtenerAlmacen();
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("id_cliente",idCte);
        values.put("nombre",valores[0]);
        values.put("direccion","");
        values.put("rfc",valores[2]);
        values.put("perfil","1");
        values.put("cliente_padre","");
        values.put("almacen",id_sucursal);
        values.put("descuento_comercial", "0");
        values.put("id_estatus_credito","1");
        values.put("activo","1");
        values.put("fecha_actualizacion","");
        values.put("latitud", lat);
        values.put("longitud", lon);

        try{
            db.insertOrThrow("clientes",null,values);
        }catch (Exception e){
            String a=e.toString();
            Log.e("TabCte",a);
        }

    }

    public String GenerarIdTemporal(){

        StringBuilder builder=new StringBuilder();
        String IdAgente=ObtenerIdAgente();

        builder.append("TEM");
        //Concatena el Id de Agente
        int tam=IdAgente.length();
        int ceros=4-tam;
        for(int i=0;i<ceros;i++){
            builder.append("0");
        }
        builder.append(IdAgente);

        //Fecha
        builder.append(Fecha());


        return builder.toString();

    }

    public void InsertIdTemporal(){

        String id_largo=GenerarIdTemporal();
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        int cont=1;
        StringBuilder builder=new StringBuilder();
        builder.append("TEM");

        Cursor rs=db.rawQuery("select max(id) from RelacionClientes",null);

        if(rs.moveToFirst()){

            cont=rs.getInt(0);

            if(cont==0){
                cont=1;
            }else {
                cont=cont+1;
            }
        }

        int tam=String.valueOf(cont).length();
        int ceros=4-tam;
        for(int i=0;i<ceros;i++){
            builder.append("0");
        }


        builder.append(String.valueOf(cont));

        ContentValues values=new ContentValues();
        values.put("id",cont);
        values.put("id_largo", id_largo);
        values.put("id_corto", builder.toString());

        if(!db.isOpen())
            db=lite.getWritableDatabase();

         try {

             db.insertOrThrow("RelacionClientes", null, values);

         }catch (Exception e){
             String s=e.toString();
             Log.e("Err1",e.toString());
         }


        db.close();
        lite.close();

        GuardarClienteDr(builder.toString());
        GuardarClienreSm(builder.toString());

        dialog=ProgressDialog.show(context,"Aviso","Cargando registro de cliente",true,false);
        new UpLoadCliente().execute(id_largo);
        //Enviar el usuario


    }

    public String ObtenerAlmacen(){

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();
        String id_suc="";
        String query="";

            query="select id_sucursal from SucursalAgente where  clave_agente=(select clave_agente from agentes where Sesion=1)";
            Cursor rs = db.rawQuery(query, null);
            if (rs.moveToFirst()) {

                id_suc = rs.getString(0);
            }

        db.close();
        lite.close();

        return id_suc;
    }

    public String ObtenerIdFuerza(){
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();
        String num="";
        String query="";

        query="select id_fuerza from agentes where Sesion=1";
        Cursor rs = db.rawQuery(query, null);
        if (rs.moveToFirst()) {

            num = rs.getString(0);
        }

        return num;
    }

    public String ObtenerClaveEmpleado(){
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();
        String num="";
        String query="";

        query="select clave_agente from agentes where Sesion=1";
        Cursor rs = db.rawQuery(query, null);
        if (rs.moveToFirst()) {

            num = rs.getString(0);
        }

        return num;
    }

    public String ObtenerIdAgente(){
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();
        String id="";
        String query="";

        query="select id_agente from agentes where Sesion=1";
        Cursor rs = db.rawQuery(query, null);
        if (rs.moveToFirst()) {

            id = rs.getString(0);
        }

        return id;
    }

    public void ShowGuardarCliente(){

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Registro de clientes");
        alert.setCancelable(false);
        alert.setMessage("¿Desea guardar al cliente?");
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

              InsertIdTemporal();
            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();

    }

    public boolean ValidarCampos(){

        Boolean resp=true;

        if(txtCP.getText().toString().equals("")){
            txtCP.requestFocus();
            resp=false;
        }

        if(txtCalle.getText().toString().equals("")){
            txtCalle.requestFocus();
            resp=false;
        }

        if(txtNoExt.getText().toString().equals("")){
            txtNoExt.requestFocus();
            resp=false;
        }
        if(txtColonia.getText().toString().equals("")){
            txtColonia.requestFocus();
            resp=false;
        }


        return resp;
    }

    public boolean ValidarColonia(String colonia){
        String PATTERN_EMAIL = "[A-Za-záéíóúñÁÉÍÓÚÑ\\s]*";
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
        Matcher matcher = pattern.matcher(colonia);
        return matcher.matches();
    }

    public boolean ValidarCalle(String calle){
        String PATTERN_EMAIL = "[A-Za-záéíóúñÁÉÍÓÚÑ\\s]*";
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
        Matcher matcher = pattern.matcher(calle);
        return matcher.matches();
    }

    private String Fecha(){
        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();

        int dia=cal.get(Calendar.DAY_OF_YEAR);

        SimpleDateFormat df=new SimpleDateFormat("yy");
        String formatteDate=df.format(dt.getTime());

        SimpleDateFormat df1=new SimpleDateFormat("ddd");

        int tam=3-String.valueOf(dia).length();
        String formatteDate1="";
        for(int i=0;i<tam;i++){
            formatteDate1+="0";
        }
        formatteDate1+=String.valueOf(dia);

        SimpleDateFormat df2=new SimpleDateFormat("HHmmss");
        String formatteDate2=df2.format(dt.getTime());

        return formatteDate+formatteDate1+formatteDate2;


    }

    public String JSonAltaCte(String id,String id_largo){

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();


        Cursor rs=db.rawQuery("select * from clientesDr where id_cliente=?", new String[]{id});
        JSONArray array=new JSONArray();
        JSONObject object=new JSONObject();

        if(rs.moveToFirst()){
            try {

                object.put("id_cliente",id_largo);
                object.put("nombre",rs.getString(1));
                object.put("rfc",rs.getString(2));
                object.put("correo",rs.getString(3));
                object.put("telefono",rs.getString(4));
                object.put("cp",rs.getString(5));
                object.put("colonia",rs.getString(6));
                object.put("calle",rs.getString(7));
                object.put("calle1",rs.getString(8));
                object.put("calle2",rs.getString(9));
                object.put("referencia",rs.getString(10));
                object.put("no_exterior",rs.getString(11));
                object.put("delegacion",rs.getString(12));
                object.put("estado",rs.getString(13));
                object.put("almacen",rs.getString(14));
                object.put("ruta",rs.getString(15));
                object.put("no_interior",rs.getString(16));
                array.put(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array.toString();
    }

    public String ObtenerIdCte(String id){

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();
        String idCte="";

        Cursor rs=db.rawQuery("select id_corto from RelacionClientes where id_largo=?",new String[]{id});

        if(rs.moveToFirst()){

            idCte=rs.getString(0);

        }

        db.close();
        lite.close();

        return idCte;
    }

    public String ProcesaJson(String json){
        String cta="";

        try {

            JSONArray array=new JSONArray(json);
            int tam=array.length();

            if(tam>0) {
                for (int i = 0; i < tam; i++) {

                    JSONObject object = array.getJSONObject(i);

                    String id_cliente = object.getString("id_cliente");
                    String cteIbs = object.getString("id_cliente_ibs");

                    String id_corto=ObtenerIdCte(id_cliente);

                    if(id_cliente!="null") {

                            if(!cteIbs.isEmpty()){
                            UpdateStatusCteDr(id_corto, cteIbs, "50");
                            UpdateCteSm(id_corto, cteIbs);
                            cta = cteIbs;
                            }

                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  cta;
    }

    public void UpdateStatusCteDr(String id,String idIBS,String estatus){

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("estatus", estatus);
        if(!idIBS.equals("")||!idIBS.equals(null))
            values.put("id_cliente",idIBS);

        long res=db.update("clientesDr", values, "id_cliente=?", new String[]{id});

        db.close();
        lite.close();

    }

    public void UpdateCteSm(String id,String idIBS){
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("id_cliente",idIBS);
        db.update("clientes",values,"id_cliente=?",new String[]{id});

        db.close();
        lite.close();

    }

    public void InsertAgenda(String id){

        String empleado=ObtenerClaveEmpleado();
        String id_fuerza=ObtenerIdFuerza();
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        String[]frecuencia=new String[]{"S1","S2"};

        try{

            for(int i=0;i<frecuencia.length;i++){

                ContentValues values=new ContentValues();
                values.put("clave_agente",empleado);
                values.put("id_fuerza",id_fuerza);
                values.put("id_cliente",id);
                values.put("Lunes",true);
                values.put("Martes",true);
                values.put("Miercoles",true);
                values.put("Jueves",true);
                values.put("Viernes",true);
                values.put("Sabado",true);
                values.put("Domingo",true);
                values.put("id_frecuencia",frecuencia[i]);
                values.put("orden",i);
                db.insertOrThrow("agenda",null,values);

            }

        }catch (Exception e){
            String err=e.toString();
            Log.e("Tag",err);

        }

    }

    public  boolean isOnline(){

        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo !=null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed(){
          super.onBackPressed();

        Intent intent=new Intent(context,AltaClientes.class);
        if(valores!=null){
            intent.putExtra("valores",valores);
        }
        startActivity(intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.direccion, menu);
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

    public class UpLoadCliente extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String mensaje=null;
            String clave_agente=ObtenerClaveEmpleado();
            String id_corto=ObtenerIdCte(strings[0]);
            String json=JSonAltaCte(id_corto,strings[0]);

            if(isOnline()) {
                WebServices wb = new WebServices();
                String val = wb.InsertarCliente(json, clave_agente);

                if (val!=null) {
                    if(val!="[]") {

                        String cta = ProcesaJson(val);

                        if (!cta.isEmpty()) {

                            InsertAgenda(cta);
                            mensaje = "Cliente agregado correctamente. El número de cuenta es: " + cta;
                        }
                        else {
                            //En caso de que los campos recibidos vengan nullos
                            InsertAgenda(id_corto);
                            mensaje=null;
                        }

                    }else{
                        //En caso de que el web service no haya respondido.
                        InsertAgenda(id_corto);
                        mensaje=null;
                    }
                }else
                {
                    //En caso de que el web service no haya respondido.
                    InsertAgenda(id_corto);
                    mensaje=null;
                }
            }else {
                //En caso de que el equipo tenga apagado los datos.
                InsertAgenda(id_corto);
                mensaje=null;
            }

            return mensaje;
        }
        @Override
        protected void onPostExecute(String res){

            String mensaje=res!=null?res.toString():"El cliente se guardo localmente.";

            if(dialog.isShowing()) {
               dialog.dismiss();

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Registro");
                alert.setCancelable(false);
                alert.setMessage(mensaje);
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        bundle.clear();
                        Intent intent=new Intent(context,MapsLocation.class);
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        finish();


                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();

            }

        }



    }


}

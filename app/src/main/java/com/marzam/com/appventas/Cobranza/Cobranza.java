package com.marzam.com.appventas.Cobranza;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.marzam.com.appventas.DevolucionesLite.CustomPrompt.NumeroEnorme;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.WebService.WebServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Locale;

public class Cobranza extends Activity {

    private TextView txt_cliente,txtpago_parcial,txtpago_total;
    public static TextView txt_total_importe,txt_total_cubrir,txt_total_pendiente;
    private ListView lv_facturas;
    private ImageButton search,cancel_search;
    private ProgressDialog dialogo;
    private ArrayList<Dictionary<String, String> > arrayfacturas = new ArrayList<Dictionary<String, String> >();
    public ArrayList<Dictionary<String, String> > arrayseleccionpagos = new ArrayList<Dictionary<String, String> >();
    public static ArrayList<Dictionary<String, String> > arrayseleccionpagosfinal = new ArrayList<Dictionary<String, String> >();
    public static ArrayList<Dictionary<String, String> > arraynotasdecredito = new ArrayList<Dictionary<String, String> >();
    private JSONObject jsonObjectResponse;
    private String id_cliente, nombre_cliente, monto_aplicar,
            scheque_banco,scheque_numero,scheque_fecha,snotac_numero,snotac_monto,snotac_fecha,stransb_numero,stransb_fecha,sfichap_numero,sfichap_fecha,sfichab_numero,sfichab_fecha;
    private RelativeLayout relativebuscar,rl_btn_busqueda;
    private Button btn_tipo_pago,btn_confirmar;
    private int swicht_search=0, total_pagos,pagoshechos;
    public static int bandera=0;
    private EditText editsearch;
    private CobranzaAdapter adaptercob;
    private float var=0, var1, var2=0, var12;
    private  EditText et_monto_aplicar;
    private  Button btnDevLitePromptReset0;
    private  Button btnDevLitePromptAdd1;
    private  Button btnDevLitePromptAdd2;
    private  Button btnDevLitePromptAdd5;
    private  Button btnDevLitePromptAdd10;
    private  String monto_aplicar_cobranzaadapter="", nombre_tipp_pago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobranza);
        setTitle("Cobranza");

        txt_cliente = (TextView) findViewById(R.id.txt_cliente);
        txtpago_parcial = (TextView) findViewById(R.id.txtpago_parcial);
        txtpago_total = (TextView) findViewById(R.id.txtpago_total);
        txt_total_importe = (TextView) findViewById(R.id.txt_total_importe);
        txt_total_cubrir = (TextView) findViewById(R.id.txt_total_cubrir);
        txt_total_pendiente = (TextView) findViewById(R.id.txt_total_pendiente);
        btn_confirmar = (Button) findViewById(R.id.btn_confirmar);
        lv_facturas = (ListView) findViewById(R.id.lv_pagos);
        search = (ImageButton) findViewById(R.id.idSetDevLiteCaptIconSearch);
        cancel_search = (ImageButton) findViewById(R.id.idSetDevLiteCaptCancelSearch);
        relativebuscar = (RelativeLayout) findViewById(R.id.relativebuscar);
        rl_btn_busqueda = (RelativeLayout) findViewById(R.id.rl_btn_busqueda);
        btn_tipo_pago = (Button) findViewById(R.id.btn_tipo_pago);
        editsearch = (EditText) findViewById(R.id.idSetDevLiteCaptProductSearch);

        /*editsearch.addTextChangedListener(watcher1);

        btn_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Cobranza.this);
                builder.setMessage(getString(R.string.confirmar))
                        .setPositiveButton(getString(R.string.confirmar_si), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Dictionary<String, String> dic = new Hashtable<String, String>();
                                for(int i=0;i<arrayseleccionpagos.size();i++){
                                    if(arrayseleccionpagos.get(i).get("bandera").equals("2")){
                                        dic.put("sp_seleccion_banco", arrayseleccionpagos.get(i).get("sp_seleccion_banco"));
                                        dic.put("et_num_cheque", arrayseleccionpagos.get(i).get("et_num_cheque"));
                                        dic.put("et_fecha_cheque", arrayseleccionpagos.get(i).get("et_fecha_cheque"));
                                    }else if(arrayseleccionpagos.get(i).get("bandera").equals("3")){
                                        dic.put("sp_num_nota_credito", arrayseleccionpagos.get(i).get("sp_num_nota_credito"));
                                        dic.put("et_monto_nota_credito", arrayseleccionpagos.get(i).get("et_monto_nota_credito"));
                                        dic.put("et_fecha_nota_credito", arrayseleccionpagos.get(i).get("et_fecha_nota_credito"));
                                    }else if(arrayseleccionpagos.get(i).get("bandera").equals("4")){
                                        dic.put("et_num_transferencia_bancaria", arrayseleccionpagos.get(i).get("et_num_transferencia_bancaria"));
                                        dic.put("et_fecha_transferencia_bancaria", arrayseleccionpagos.get(i).get("et_fecha_transferencia_bancaria"));
                                    }else if(arrayseleccionpagos.get(i).get("bandera").equals("5")){
                                        dic.put("et_num_ficha_pago", arrayseleccionpagos.get(i).get("et_num_ficha_pago"));
                                        dic.put("et_fecha_ficha_pago", arrayseleccionpagos.get(i).get("et_fecha_ficha_pago"));
                                    }else if(arrayseleccionpagos.get(i).get("bandera").equals("6")){
                                        dic.put("et_num_ficha_bancaria", arrayseleccionpagos.get(i).get("et_num_ficha_bancaria"));
                                        dic.put("et_fecha_ficha_bancaria", arrayseleccionpagos.get(i).get("et_fecha_ficha_bancaria"));
                                    }
                                    dic.put("tipo_pago", arrayseleccionpagos.get(i).get("tipo_pago"));
                                    dic.put("bandera", arrayseleccionpagos.get(i).get("bandera"));
                                    dic.put("monto_aplicar", txt_total_cubrir.getText().toString());
                                    arrayseleccionpagos = new ArrayList<Dictionary<String, String> >();
                                    arrayseleccionpagosfinal.add(dic);
                                    Log.i("arrayseleccionpagos1", "arrayseleccionpagos1 = " + arrayseleccionpagosfinal.toString());
                                }
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(getString(R.string.confirmar_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Dictionary<String, String> dic = new Hashtable<String, String>();
                                for(int i=0;i<arrayseleccionpagos.size();i++){
                                    if(arrayseleccionpagos.get(i).get("bandera").equals("2")){
                                        dic.put("sp_seleccion_banco", arrayseleccionpagos.get(i).get("sp_seleccion_banco"));
                                        dic.put("et_num_cheque", arrayseleccionpagos.get(i).get("et_num_cheque"));
                                        dic.put("et_fecha_cheque", arrayseleccionpagos.get(i).get("et_fecha_cheque"));
                                    }else if(arrayseleccionpagos.get(i).get("bandera").equals("3")){
                                        dic.put("sp_num_nota_credito", arrayseleccionpagos.get(i).get("sp_num_nota_credito"));
                                        dic.put("et_monto_nota_credito", arrayseleccionpagos.get(i).get("et_monto_nota_credito"));
                                        dic.put("et_fecha_nota_credito", arrayseleccionpagos.get(i).get("et_fecha_nota_credito"));
                                    }else if(arrayseleccionpagos.get(i).get("bandera").equals("4")){
                                        dic.put("et_num_transferencia_bancaria", arrayseleccionpagos.get(i).get("et_num_transferencia_bancaria"));
                                        dic.put("et_fecha_transferencia_bancaria", arrayseleccionpagos.get(i).get("et_fecha_transferencia_bancaria"));
                                    }else if(arrayseleccionpagos.get(i).get("bandera").equals("5")){
                                        dic.put("et_num_ficha_pago", arrayseleccionpagos.get(i).get("et_num_ficha_pago"));
                                        dic.put("et_fecha_ficha_pago", arrayseleccionpagos.get(i).get("et_fecha_ficha_pago"));
                                    }else if(arrayseleccionpagos.get(i).get("bandera").equals("6")){
                                        dic.put("et_num_ficha_bancaria", arrayseleccionpagos.get(i).get("et_num_ficha_bancaria"));
                                        dic.put("et_fecha_ficha_bancaria", arrayseleccionpagos.get(i).get("et_fecha_ficha_bancaria"));
                                    }
                                    dic.put("tipo_pago", arrayseleccionpagos.get(i).get("tipo_pago"));
                                    dic.put("bandera", arrayseleccionpagos.get(i).get("bandera"));
                                    dic.put("monto_aplicar", txt_total_cubrir.getText().toString());
                                    arrayseleccionpagos = new ArrayList<Dictionary<String, String> >();
                                    arrayseleccionpagosfinal.add(dic);
                                    Log.i("arrayseleccionpagos", "arrayseleccionpagos = " + arrayseleccionpagosfinal.toString());
                                }

                                Intent intento = new Intent(Cobranza.this,CobranzaDetallePago.class);
                                intento.putExtra("pharmacy", txt_cliente.getText().toString());
                                intento.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intento);
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swicht_search == 0) {
                    relativebuscar.setVisibility(View.VISIBLE);
                    btn_tipo_pago.setVisibility(View.GONE);
                    swicht_search = 1;
                }else{
                    relativebuscar.setVisibility(View.GONE);
                    btn_tipo_pago.setVisibility(View.VISIBLE);
                    swicht_search = 0;
                }
            }
        });

        editsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    adaptercob.getFilter().filter(editsearch.getText().toString());
                    return true;
                }
                return false;
            }
        });

        cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editsearch.setText("");
                editsearch.setHint(getString(R.string.hint_buscador));
            }
        });


        btn_tipo_pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });*/

        Bundle extras = getIntent().getExtras();
        nombre_cliente = extras.getString("pharmacy");
        txt_cliente.setText(nombre_cliente);

        new AsynkCobranza().execute();
    }

    TextWatcher watcher1 = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after){
        }

        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            adaptercob.getFilter().filter(s.toString());
        }

        public void afterTextChanged(Editable s){
        }
    };


    public class AsynkCobranza extends AsyncTask<Void, Void, Integer> {

        int flag=0, dias=0;

        protected void onPreExecute() {
            dialogo = ProgressDialog.show(Cobranza.this, "Espere un momento", "Cargando...");
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if (UtilsCobranza.isNetwork(getApplicationContext())){

                WebServices ws = new WebServices();
                /*DataBase db = new DataBase(Cobranza.this);
                String agentes = db.execSelect(db.QUERY_CLIENT);
                Log.i("agentes","agentes "+agentes);*/
                String agente = "YPC07";
                String jsonInvoices;
                int tries = 0;
                boolean successJsonInvoice = false;

                do {
                    jsonInvoices = ws.DownJsonCobranza(agente);
                    if( jsonInvoices!=null )
                        successJsonInvoice = true;
                }while( tries<3 && !successJsonInvoice );

                if(!(jsonInvoices==null)){
                    Log.d("WS","WS = "+jsonInvoices);
                    try{

                        JSONArray jsonArrayResponse =  new JSONArray(jsonInvoices);
                        for(int i=0;i<jsonArrayResponse.length();i++){

                            jsonObjectResponse =jsonArrayResponse.getJSONObject(i);

                            String Documento = jsonObjectResponse.getString("DOCUMENTO");
                            String Cuenta = jsonObjectResponse.getString("CUENTA");
                            String Fecha_Documento = jsonObjectResponse.getString("FECHA_DOCUMENTO");
                            String Fecha_Vencimiento = jsonObjectResponse.getString("FECHA_VENCIMIENTO");
                            String Importe = jsonObjectResponse.getString("IMPORTE");
                            String Saldo = jsonObjectResponse.getString("SALDO");
                            String days = jsonObjectResponse.getString("days");

                            Dictionary<String, String> dic = new Hashtable<String, String>();
                            dic.put("Documento",Documento);
                            dic.put("Cuenta",Cuenta);
                            dic.put("Fecha_Documento",Fecha_Documento);
                            dic.put("Fecha_Vencimiento",Fecha_Vencimiento);
                            dic.put("Importe",Importe);
                            dic.put("Saldo",Saldo);
                            dic.put("days",days);
                            dic.put("selected","false");
                            dic.put("adeudo_cubrir","0");
                            dic.put("pressed_again","0");
                            dic.put("NewSaldo",Saldo);

                            total_pagos=i;

                            if(!Importe.contains("-")){
                                arrayfacturas.add(dic);
                            }else{
                                arraynotasdecredito.add(dic);
                            }

                            //txt_total_importe

                            /*String fechav = new String(Fecha_Vencimiento);

                            Calendar cal = Calendar.getInstance();
                            DateTime start = new DateTime(Integer.parseInt(fechav.substring(0, 4)), Integer.parseInt(fechav.substring(4,6)), Integer.parseInt(fechav.substring(6, 8)), 0, 0, 0, 0);
                            DateTime end = new DateTime(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), 0, 0, 0, 0);

                            if(start.isAfter(end) || start.isEqual(end)){
                                dias=0;
                            }else{
                                Days d = Days.daysBetween(start.withTimeAtStartOfDay(), end.withTimeAtStartOfDay());
                                dias = d.getDays();
                            }

                            if(!(dias>=30)){
                                arrayfacturas.add(dic);
                            }*/
                        }
                    }catch (Exception e){
                        Log.d("JSON", "Exception: " + e.getMessage());
                    }
                    flag=2;
                }else{
                    flag=1;
                }
            }else{
                flag=0;
            }
            return null;
        }

        protected void onPostExecute(Integer result) {
            if ((dialogo != null) && dialogo.isShowing()) {
                dialogo.dismiss();
            }

            if(flag==0){
                Toast toast = Toast.makeText(Cobranza.this, getString(R.string.sin_internet),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                toast.show();
            }

            if(flag==1){
                Toast toast = Toast.makeText(Cobranza.this, getString(R.string.error_usuario),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                toast.show();
            }

            if(flag==2){

                for(int j=0;j<arrayfacturas.size();j++){
                    var += (int) Float.parseFloat(arrayfacturas.get(j).get("Importe"));
                    var2 += (int) Float.parseFloat(arrayfacturas.get(j).get("Saldo"));
                    /*var1 = var + Float.parseFloat(arrayfacturas.get(j).get("Importe"));
                    var = var1;
                    var12 = var2 + Float.parseFloat(arrayfacturas.get(j).get("Saldo"));
                    var2 = var12;*/
                }
                Log.i("var2", "var2 = " + var);
                Log.i("var3", "var3 = " + var2);
                txt_total_importe.setText("$" + UtilsCobranza.textdecimal(var + ""));
                txt_total_pendiente.setText("$" + UtilsCobranza.textdecimal(var2 + ""));

                txtpago_total.setText("0/"+total_pagos);
                txtpago_parcial.setText("0/"+total_pagos);

                adaptercob = new CobranzaAdapter(arrayfacturas,Cobranza.this,bandera);
                lv_facturas.setAdapter(adaptercob);

                /*lv_facturas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        if (bandera != 0) {
                            final Dictionary<String, String> dic = (Dictionary<String, String>) adaptercob.getItem(position);

                            if(dic.get("pressed_again").equals("0")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Cobranza.this);
                                builder.setMessage(R.string.cubrir_adeudo) //quitar_cubrir_adeudo
                                        .setPositiveButton(R.string.cubrir_adeudo_si, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                dic.put("Saldo", "0");
                                                dic.put("adeudo_cubrir", dic.get("Importe"));
                                                dic.put("selected", "true");
                                                dic.put("pressed_again", "1");

                                                adaptercob.notifyDataSetChanged();

                                                Float pendiente = Float.parseFloat(txt_total_pendiente.getText().toString().replace("$", "").replace(",", ""))
                                                        - Float.parseFloat(dic.get("Importe"));
                                                Float acubrir = Float.parseFloat(txt_total_cubrir.getText().toString().replace("$", "").replace(",", ""))
                                                        + Float.parseFloat(dic.get("Importe"));

                                                txt_total_pendiente.setText("$" + UtilsCobranza.textdecimal(pendiente + ""));
                                                txt_total_cubrir.setText("$" + UtilsCobranza.textdecimal(acubrir + ""));


                                                if (dic.get("selected").compareTo("true") == 0) {
                                                    Log.i("entro al if", "entro al if");
                                                    pagoshechos++;
                                                }
                                                Log.i("pagoshechos", " = " + pagoshechos);
                                                txtpago_total.setText(pagoshechos + "/" + total_pagos);

                                                dialog.cancel();

                                                btn_confirmar.setEnabled(true);
                                            }
                                        })
                                        .setNegativeButton(R.string.cubrir_adeudo_no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                            }else{

                                AlertDialog.Builder builder = new AlertDialog.Builder(Cobranza.this);
                                builder.setMessage(R.string.quitar_cubrir_adeudo)
                                        .setPositiveButton(R.string.cubrir_adeudo_si, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Log.i("Saldo"," = "+dic.get("NewSaldo"));
                                                dic.put("Saldo", dic.get("NewSaldo"));
                                                dic.put("adeudo_cubrir", "0");
                                                dic.put("selected", "false");
                                                dic.put("pressed_again", "0");

                                                adaptercob.notifyDataSetChanged();

                                                Float pendiente = Float.parseFloat(txt_total_pendiente.getText().toString().replace("$", "").replace(",", ""))
                                                        + Float.parseFloat(dic.get("Importe"));
                                                Float acubrir = Float.parseFloat(txt_total_cubrir.getText().toString().replace("$", "").replace(",", ""))
                                                        - Float.parseFloat(dic.get("Importe"));

                                                txt_total_pendiente.setText("$" + UtilsCobranza.textdecimal(pendiente + ""));
                                                txt_total_cubrir.setText("$" + UtilsCobranza.textdecimal(acubrir + ""));


                                                if (dic.get("selected").compareTo("false") == 0) {
                                                    Log.i("entro al if", "entro al if");
                                                    pagoshechos--;
                                                }
                                                Log.i("pagoshechos", " = " + pagoshechos);
                                                txtpago_total.setText(pagoshechos + "/" + total_pagos);

                                                dialog.cancel();
                                            }
                                        })
                                        .setNegativeButton(R.string.cubrir_adeudo_no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(Cobranza.this, R.string.sinseleccionar_dialog, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                            toast.show();
                        }
                        return true;
                    }
                });

                lv_facturas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (bandera == 0) {
                            Toast toast = Toast.makeText(Cobranza.this, R.string.sinseleccionar_dialog, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                            toast.show();
                        } else {
                            int days;

                            final Dictionary<String, String> dic = (Dictionary<String, String>) adaptercob.getItem(position);
                            String fechad = new String(dic.get("Fecha_Documento"));
                            String fechav = new String(dic.get("Fecha_Vencimiento"));

                            if (Integer.parseInt(dic.get("days")) < 0) {
                                days = 0;
                            } else {
                                days = Integer.parseInt(dic.get("days"));
                            }

                            if(dic.get("pressed_again").equals("0")){
                            openDialogEditarMonto(dic.get("Importe"),
                                    fechad.substring(0, 4) + "/" + fechad.substring(4, 6) + "/" + fechad.substring(6, 8),
                                    fechav.substring(0, 4) + "/" + fechav.substring(4, 6) + "/" + fechav.substring(6, 8),
                                    days, position);
                            }else{

                                AlertDialog.Builder builder = new AlertDialog.Builder(Cobranza.this);
                                builder.setMessage(R.string.quitar_cubrir_adeudo)
                                        .setPositiveButton(R.string.cubrir_adeudo_si, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Log.i("Saldo", " = " + dic.get("NewSaldo"));
                                                dic.put("Saldo", dic.get("NewSaldo"));
                                                dic.put("adeudo_cubrir", "0");
                                                dic.put("selected", "false");
                                                dic.put("pressed_again", "0");

                                                adaptercob.notifyDataSetChanged();

                                                Float pendiente = Float.parseFloat(txt_total_pendiente.getText().toString().replace("$", "").replace(",", ""))
                                                        + Float.parseFloat(dic.get("Importe"));
                                                Float acubrir = Float.parseFloat(txt_total_cubrir.getText().toString().replace("$", "").replace(",", ""))
                                                        - Float.parseFloat(dic.get("Importe"));

                                                txt_total_pendiente.setText("$" + UtilsCobranza.textdecimal(pendiente + ""));
                                                txt_total_cubrir.setText("$" + UtilsCobranza.textdecimal(acubrir + ""));


                                                if (dic.get("selected").compareTo("false") == 0) {
                                                    Log.i("entro al if", "entro al if");
                                                    pagoshechos--;
                                                }
                                                Log.i("pagoshechos", " = " + pagoshechos);

                                                if (dic.get("adeudo_cubrir").equals(dic.get("NewSaldo"))) {
                                                    txtpago_total.setText(pagoshechos + "/" + total_pagos);
                                                } else {
                                                    txtpago_parcial.setText(pagoshechos + "/" + total_pagos);
                                                }

                                                dialog.cancel();
                                            }
                                        })
                                        .setNegativeButton(R.string.cubrir_adeudo_no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        }
                    }
                });*/
            }
        }

    }

    public void openDialogEditarMonto(String monto_total, String fechae, String fechacv, int days, final int position){
        final Dialog dialog = new Dialog(Cobranza.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cobranza_dialogo_editar_monto);

        RelativeLayout relativecerrar = (RelativeLayout) dialog.findViewById(R.id.imagencerrar);
        Button btn_guardar_dialog = (Button) dialog.findViewById(R.id.btn_editar_dialog);
        TextView txt_monto_total = (TextView) dialog.findViewById(R.id.txt_monto_total);
        TextView txtfecha_e = (TextView) dialog.findViewById(R.id.txtfecha_e);
        TextView txtfecha_v = (TextView) dialog.findViewById(R.id.txtfecha_v);
        TextView txtdias_f = (TextView) dialog.findViewById(R.id.txtadeudo_c);
        et_monto_aplicar = (EditText) dialog.findViewById(R.id.et_monto_aplicar);
        btnDevLitePromptReset0 = (Button) dialog.findViewById(R.id.idtxtDevLitePromptReset0);
        btnDevLitePromptAdd1 = (Button) dialog.findViewById(R.id.idDevLitePromptAdd1);
        btnDevLitePromptAdd2 = (Button) dialog.findViewById(R.id.idDevLitePromptAdd2);
        btnDevLitePromptAdd5 = (Button) dialog.findViewById(R.id.idDevLitePromptAdd5);
        btnDevLitePromptAdd10 = (Button) dialog.findViewById(R.id.idDevLitePromptAdd10);

        btnDevLitePromptReset0.setOnClickListener( btnsuma_monto_cobranza );
        btnDevLitePromptAdd1.setOnClickListener( btnsuma_monto_cobranza );
        btnDevLitePromptAdd2.setOnClickListener( btnsuma_monto_cobranza );
        btnDevLitePromptAdd5.setOnClickListener( btnsuma_monto_cobranza );
        btnDevLitePromptAdd10.setOnClickListener(btnsuma_monto_cobranza);

        txt_monto_total.setText("$" +monto_total);
        txtfecha_e.setText(fechae);
        txtfecha_v.setText(fechacv);
        txtdias_f.setText(days + " " + Cobranza.this.getString(R.string.dias_adapter));

        btn_guardar_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_monto_aplicar.getText().toString().equals("")) {
                    et_monto_aplicar.setError("Debe agregar un monto a aplicar");
                } else {
                    monto_aplicar_cobranzaadapter = et_monto_aplicar.getText().toString();

                    Dictionary<String, String> dic = (Dictionary<String, String>) adaptercob.getItem(position);
                    float resultsaldo = Float.parseFloat(dic.get("Importe")) - Float.parseFloat(monto_aplicar_cobranzaadapter);

                    dic.put("selected", "true");
                    dic.put("adeudo_cubrir", monto_aplicar_cobranzaadapter);
                    dic.put("Saldo", resultsaldo + "");
                    dic.put("pressed_again", "1");
                    adaptercob.notifyDataSetChanged();

                    Float pendiente = Float.parseFloat(txt_total_pendiente.getText().toString().replace("$", "").replace(",", ""))
                            - Float.parseFloat(monto_aplicar_cobranzaadapter);
                    Float acubrir = Float.parseFloat(txt_total_cubrir.getText().toString().replace("$", "").replace(",", ""))
                            + Float.parseFloat(monto_aplicar_cobranzaadapter);

                    txt_total_pendiente.setText("$" + UtilsCobranza.textdecimal(pendiente + ""));
                    txt_total_cubrir.setText("$" + UtilsCobranza.textdecimal(acubrir + ""));

                    if (dic.get("selected").compareTo("true") == 0) {
                        Log.i("entro al if", "entro al if");
                        pagoshechos++;
                    }
                    Log.i("pagoshechos", " = " + pagoshechos);

                    if (dic.get("adeudo_cubrir").equals(dic.get("Saldo"))) {
                        txtpago_total.setText(pagoshechos + "/" + total_pagos);
                    } else {
                        txtpago_parcial.setText(pagoshechos + "/" + total_pagos);
                    }

                    dialog.dismiss();

                    btn_confirmar.setEnabled(true);
                }
            }
        });

        relativecerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        Drawable d = new ColorDrawable(Color.WHITE);
        d.setAlpha(0);
        dialog.getWindow().setBackgroundDrawable(d);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.dismiss();
        dialog.show();
    }

    private View.OnClickListener btnsuma_monto_cobranza = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String strAmountReturn = et_monto_aplicar.getText().toString().trim();
            if( strAmountReturn.length() == 0 ) strAmountReturn = "0";

            if (view == btnDevLitePromptReset0){
                strAmountReturn = "";
            }else if (view == btnDevLitePromptAdd1){
                strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "100")).toString();
            }else if (view == btnDevLitePromptAdd2){
                strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "500")).toString();
            }else if (view == btnDevLitePromptAdd5){
                strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "1000")).toString();
            }else if (view == btnDevLitePromptAdd10){
                strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "5000")).toString();
            }
            et_monto_aplicar.setText( strAmountReturn );
            et_monto_aplicar.setSelection(strAmountReturn.length(), strAmountReturn.length());
        }
    };

    private void openDialog(){
        final Dialog dialog = new Dialog(Cobranza.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cobranza_dialogo_tipo_pago);

        final int[] band = {1};

        ScrollView scroll = (ScrollView) dialog.findViewById(R.id.scrollView);
        RelativeLayout relativecerrar = (RelativeLayout) dialog.findViewById(R.id.imagencerrar);
        final Spinner sp_seleccion_banco = (Spinner) dialog.findViewById(R.id.idSetDevLiteConfDocumentType);
        final Spinner sp_num_nota_credito = (Spinner) dialog.findViewById(R.id.spinner_nota_credito);
        final EditText et_num_cheque = (EditText) dialog.findViewById(R.id.et_num_cheque);
        final EditText et_fecha_cheque = (EditText) dialog.findViewById(R.id.et_fecha_cheque);
        final EditText et_monto_nota_credito = (EditText) dialog.findViewById(R.id.et_monto_nota_credito);
        final EditText et_fecha_nota_credito = (EditText) dialog.findViewById(R.id.et_fecha_nota_credito);
        final EditText et_num_transferencia_bancaria = (EditText) dialog.findViewById(R.id.et_num_transferencia_bancaria);
        final EditText et_fecha_transferencia_bancaria = (EditText) dialog.findViewById(R.id.et_fecha_transferencia_bancaria);
        final EditText et_num_ficha_pago = (EditText) dialog.findViewById(R.id.et_num_ficha_pago);
        final EditText et_fecha_ficha_pago = (EditText) dialog.findViewById(R.id.et_fecha_ficha_pago);
        final EditText et_num_ficha_bancaria = (EditText) dialog.findViewById(R.id.et_num_ficha_bancaria);
        final EditText et_fecha_ficha_bancaria = (EditText) dialog.findViewById(R.id.et_fecha_ficha_bancaria);
        final GridLayout gridcheque = (GridLayout) dialog.findViewById(R.id.gridcheque);
        final GridLayout gridnota_credito = (GridLayout) dialog.findViewById(R.id.gridnota_credito);
        final GridLayout gridtransferencia_bancaria = (GridLayout) dialog.findViewById(R.id.gridtransferencia_bancaria);
        final GridLayout gridficha_pago = (GridLayout) dialog.findViewById(R.id.gridficha_pago);
        final GridLayout gridficha_bancaria = (GridLayout) dialog.findViewById(R.id.gridficha_bancaria);
        RadioGroup rdgGrupo = (RadioGroup) dialog.findViewById(R.id.rdgGrupo);
        Button btn_guardar_dialog = (Button) dialog.findViewById(R.id.btn_guardar_dialog);

        scroll.setScrollbarFadingEnabled(false);
        scroll.setVerticalScrollBarEnabled(true);
        scroll.setVerticalFadingEdgeEnabled(false);

        sp_seleccion_banco.setOnItemSelectedListener(OnCatSpinnerCL);
        sp_num_nota_credito.setOnItemSelectedListener(OnCatSpinnerCLNotaC);

        rdgGrupo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbefectivo) {
                    band[0] = 1;
                    gridcheque.setVisibility(View.GONE);
                    gridnota_credito.setVisibility(View.GONE);
                    gridtransferencia_bancaria.setVisibility(View.GONE);
                    gridficha_pago.setVisibility(View.GONE);
                    gridficha_bancaria.setVisibility(View.GONE);
                } else if (checkedId == R.id.rbcheque) {
                    band[0] = 2;
                    gridcheque.setVisibility(View.VISIBLE);
                    gridnota_credito.setVisibility(View.GONE);
                    gridtransferencia_bancaria.setVisibility(View.GONE);
                    gridficha_pago.setVisibility(View.GONE);
                    gridficha_bancaria.setVisibility(View.GONE);
                } else if (checkedId == R.id.rbnota_credito) {
                    band[0] = 3;
                    gridcheque.setVisibility(View.GONE);
                    gridnota_credito.setVisibility(View.VISIBLE);
                    gridtransferencia_bancaria.setVisibility(View.GONE);
                    gridficha_pago.setVisibility(View.GONE);
                    gridficha_bancaria.setVisibility(View.GONE);
                } else if (checkedId == R.id.rbtransferencia_bancaria) {
                    band[0] = 4;
                    gridcheque.setVisibility(View.GONE);
                    gridnota_credito.setVisibility(View.GONE);
                    gridtransferencia_bancaria.setVisibility(View.VISIBLE);
                    gridficha_pago.setVisibility(View.GONE);
                    gridficha_bancaria.setVisibility(View.GONE);
                } else if (checkedId == R.id.rbficha_pago) {
                    band[0] = 5;
                    gridcheque.setVisibility(View.GONE);
                    gridnota_credito.setVisibility(View.GONE);
                    gridtransferencia_bancaria.setVisibility(View.GONE);
                    gridficha_pago.setVisibility(View.VISIBLE);
                    gridficha_bancaria.setVisibility(View.GONE);
                } else if (checkedId == R.id.rbficha_bancaria) {
                    band[0] = 6;
                    gridcheque.setVisibility(View.GONE);
                    gridnota_credito.setVisibility(View.GONE);
                    gridtransferencia_bancaria.setVisibility(View.GONE);
                    gridficha_pago.setVisibility(View.GONE);
                    gridficha_bancaria.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_guardar_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (band[0] == 0) {
                    Toast toast = Toast.makeText(Cobranza.this, getString(R.string.sinseleccionar_dialog), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                    toast.show();
                } else if (band[0] == 1) {
                    Toast toast = Toast.makeText(Cobranza.this, getString(R.string.datos_guardar_dialog), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                    toast.show();
                    bandera = 1;
                    Dictionary<String, String> dic = new Hashtable<String, String>();
                    btn_tipo_pago.setText(getString(R.string.efectivo));
                    dic.put("tipo_pago", getString(R.string.efectivo));
                    dic.put("bandera", bandera+"");
                    arrayseleccionpagos.add(dic);

                    btn_confirmar.setVisibility(View.VISIBLE);
                    btn_confirmar.setText(getString(R.string.conf_efectivo));
                    btn_confirmar.setEnabled(false);
                    dialog.dismiss();
                } else if (band[0] == 2) {
                    if (sp_seleccion_banco.getSelectedItem().equals("") || sp_seleccion_banco.getSelectedItem().equals("-Seleccione un banco-")) {
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.seleccion_banco_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                    } else if (et_num_cheque.getText().toString().equals("")) {
                        et_num_cheque.setError(getString(R.string.llenar_campo));
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.num_cheque_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                    } else if (et_fecha_cheque.getText().toString().equals("")) {
                        et_fecha_cheque.setError(getString(R.string.llenar_campo));
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.fecha_cheque_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                    }
                    /*else if (et_num_cheque.length()< 8||et_num_cheque.length()> 8) {
                        et_num_cheque.setError(getString(R.string.msg_formato_incorrecto_num_cheque));
                    }*/
                    else {
                        scheque_banco = sp_seleccion_banco.getSelectedItem().toString();
                        scheque_numero = et_num_cheque.getText().toString();
                        scheque_fecha = et_fecha_cheque.getText().toString();
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.datos_guardar_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                        btn_confirmar.setVisibility(View.VISIBLE);
                        btn_confirmar.setText(getString(R.string.conf_cheque));
                        btn_confirmar.setEnabled(false);

                        bandera = 2;
                        Dictionary<String, String> dic = new Hashtable<String, String>();
                        dic.put("sp_seleccion_banco", sp_seleccion_banco.getSelectedItem()+"");
                        dic.put("et_num_cheque", et_num_cheque.getText().toString());
                        dic.put("et_fecha_cheque", et_fecha_cheque.getText().toString());
                        dic.put("tipo_pago", getString(R.string.cheque));
                        btn_tipo_pago.setText(getString(R.string.cheque));
                        dic.put("bandera", bandera+"");
                        arrayseleccionpagos.add(dic);

                        dialog.dismiss();
                    }
                } else if (band[0] == 3) {
                    if (sp_num_nota_credito.getSelectedItem().equals("") || sp_num_nota_credito.getSelectedItem().equals("-Seleccione una nota-")) {
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.seleccion_nota_credito_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                    } else if (et_monto_nota_credito.getText().toString().equals("")) {
                        et_monto_nota_credito.setError(getString(R.string.llenar_campo));
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.monto_nota_credito_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 80);
                        toast.show();
                    } else if (et_fecha_nota_credito.getText().toString().equals("")) {
                        et_fecha_nota_credito.setError(getString(R.string.llenar_campo));
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.fecha_nota_credito_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 80);
                        toast.show();
                    } else {
                        snotac_numero = sp_num_nota_credito.getSelectedItem().toString();
                        snotac_monto = et_monto_nota_credito.getText().toString();
                        snotac_fecha = et_fecha_nota_credito.getText().toString();
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.datos_guardar_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 80);
                        toast.show();
                        btn_confirmar.setVisibility(View.VISIBLE);
                        btn_confirmar.setText(getString(R.string.conf_nota_cre));
                        btn_confirmar.setEnabled(false);

                        bandera = 3;
                        Dictionary<String, String> dic = new Hashtable<String, String>();
                        dic.put("et_monto_nota_credito", et_monto_nota_credito.getText().toString());
                        dic.put("et_fecha_nota_credito", et_fecha_nota_credito.getText().toString());
                        dic.put("sp_num_nota_credito", sp_num_nota_credito.getSelectedItem()+"");
                        dic.put("tipo_pago", getString(R.string.nota_credito));
                        btn_tipo_pago.setText(getString(R.string.nota_credito));
                        dic.put("bandera", bandera+"");
                        arrayseleccionpagos.add(dic);

                        dialog.dismiss();
                    }
                } else if (band[0] == 4) {
                    if (et_num_transferencia_bancaria.getText().toString().equals("")) {
                        et_num_transferencia_bancaria.setError(getString(R.string.llenar_campo));
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.num_trans_bancaria_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 80);
                        toast.show();
                    } else if (et_fecha_transferencia_bancaria.getText().toString().equals("")) {
                        et_fecha_transferencia_bancaria.setError(getString(R.string.llenar_campo));
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.fecha_trans_bancaria_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 80);
                        toast.show();
                    }
                    /*else if (et_num_transferencia_bancaria.length()< 8||et_num_cheque.length()> 8) {
                        et_num_transferencia_bancaria.setError(getString(R.string.msg_formato_incorrecto_trans));
                    }*/
                    else {
                        stransb_numero = et_num_transferencia_bancaria.getText().toString();
                        stransb_fecha = et_fecha_transferencia_bancaria.getText().toString();
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.datos_guardar_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 80);
                        toast.show();
                        btn_confirmar.setVisibility(View.VISIBLE);
                        btn_confirmar.setText(getString(R.string.conf_trans_bancaria));
                        btn_confirmar.setEnabled(false);

                        bandera = 4;
                        Dictionary<String, String> dic = new Hashtable<String, String>();
                        dic.put("et_num_transferencia_bancaria", et_num_transferencia_bancaria.getText().toString());
                        dic.put("et_fecha_transferencia_bancaria", et_fecha_transferencia_bancaria.getText().toString());
                        dic.put("tipo_pago", getString(R.string.transferencia_bancaria));
                        btn_tipo_pago.setText(getString(R.string.transferencia_bancaria));
                        dic.put("bandera", bandera+"");
                        arrayseleccionpagos.add(dic);

                        dialog.dismiss();
                    }
                } else if (band[0] == 5) {
                    if (et_num_ficha_pago.getText().toString().equals("")) {
                        et_num_ficha_pago.setError(getString(R.string.llenar_campo));
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.num_ficha_pago_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 80);
                        toast.show();
                    } else if (et_fecha_ficha_pago.getText().toString().equals("")) {
                        et_fecha_ficha_pago.setError(getString(R.string.llenar_campo));
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.fecha_ficha_pago_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 80);
                        toast.show();
                    }
                    /*else if (et_num_ficha_pago.length()< 8||et_num_cheque.length()> 8) {
                        et_num_ficha_pago.setError(getString(R.string.msg_formato_incorrecto_ficha_pago));
                    }*/
                    else {
                        sfichap_numero = et_num_ficha_pago.getText().toString();
                        sfichap_fecha = et_fecha_ficha_pago.getText().toString();
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.datos_guardar_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 80);
                        toast.show();
                        btn_confirmar.setVisibility(View.VISIBLE);
                        btn_confirmar.setText(getString(R.string.conf_ficha_pago));
                        btn_confirmar.setEnabled(false);

                        bandera = 5;
                        Dictionary<String, String> dic = new Hashtable<String, String>();
                        dic.put("et_num_ficha_pago", et_num_ficha_pago.getText().toString());
                        dic.put("et_fecha_ficha_pago", et_fecha_ficha_pago.getText().toString());
                        dic.put("tipo_pago", getString(R.string.ficha_pago));
                        btn_tipo_pago.setText(getString(R.string.ficha_pago));
                        dic.put("bandera", bandera+"");
                        arrayseleccionpagos.add(dic);

                        dialog.dismiss();
                    }
                } else if (band[0] == 6) {
                    if (et_num_ficha_bancaria.getText().toString().equals("")) {
                        et_num_ficha_bancaria.setError(getString(R.string.llenar_campo));
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.num_ficha_bancaria_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                    } else if (et_fecha_ficha_bancaria.getText().toString().equals("")) {
                        et_fecha_ficha_bancaria.setError(getString(R.string.llenar_campo));
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.fecha_ficha_bancaria_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                    }
                    /*else if (et_num_ficha_bancaria.length()< 8||et_num_cheque.length()> 8) {
                        et_num_ficha_bancaria.setError(getString(R.string.msg_formato_incorrecto_ficha_bancaria));
                    }*/
                    else {
                        sfichab_numero = et_num_ficha_bancaria.getText().toString();
                        sfichab_fecha = et_fecha_ficha_bancaria.getText().toString();
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.datos_guardar_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                        btn_confirmar.setVisibility(View.VISIBLE);
                        btn_confirmar.setText(getString(R.string.conf_ficha_bancaria));
                        btn_confirmar.setEnabled(false);

                        bandera = 6;
                        Dictionary<String, String> dic = new Hashtable<String, String>();
                        dic.put("et_num_ficha_bancaria", et_num_ficha_bancaria.getText().toString());
                        dic.put("et_fecha_ficha_bancaria", et_fecha_ficha_bancaria.getText().toString());
                        dic.put("tipo_pago", getString(R.string.ficha_bancaria));
                        btn_tipo_pago.setText(getString(R.string.ficha_bancaria));
                        dic.put("bandera", bandera+"");
                        arrayseleccionpagos.add(dic);

                        dialog.dismiss();
                    }
                }
            }
        });

        relativecerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        Drawable d = new ColorDrawable(Color.WHITE);
        d.setAlpha(0);
        dialog.getWindow().setBackgroundDrawable(d);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.dismiss();
        dialog.show();
    }

    private AdapterView.OnItemSelectedListener OnCatSpinnerCL = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            ((TextView) parent.getChildAt(0)).setTextSize(16);
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private AdapterView.OnItemSelectedListener OnCatSpinnerCLNotaC = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            ((TextView) parent.getChildAt(0)).setTextSize(16);
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    protected void onDestroy() {
        if ((dialogo != null) && dialogo.isShowing()){
            dialogo.dismiss();
        }
        dialogo = null;
        super.onDestroy();
    }
}

package com.marzam.com.appventas.Cobranza;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.marzam.com.appventas.DataBase.DataBase;
import com.marzam.com.appventas.DevolucionesLite.CustomPrompt.NumeroEnorme;
import com.marzam.com.appventas.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class Cobranza extends Activity {

    private TextView txt_cliente, txtpago_parcial, txtpago_total;
    public static TextView txt_total_importe, txt_total_cubrir, txt_total_pendiente;
    private ListView lv_facturas;
    private ImageButton search, cancel_search;
    private ProgressDialog dialogo;
    private ArrayList<Dictionary<String, String>> arrayfacturas = new ArrayList<Dictionary<String, String>>();
    public ArrayList<Dictionary<String, String>> arrayseleccionpagos = new ArrayList<Dictionary<String, String>>();
    private String id_cliente, nombre_cliente, monto_aplicar, agente, swictch_confirmar_pago, num_confirmar_pago,
            scheque_banco, scheque_numero, scheque_fecha, snotac_numero, snotac_monto, snotac_fecha, stransb_numero,
            stransb_fecha, sfichap_numero, sfichap_fecha, sfichab_numero, sfichab_fecha;
    private RelativeLayout relativebuscar, rl_btn_busqueda;
    private Button btn_tipo_pago, btn_confirmar;//, btn_siguiente;
    private int swicht_search = 0, total_pagos, pagoshechos, pagoshechosdialog, flag_next = 0;
    public static int bandera = 0;
    private EditText editsearch;
    private CobranzaAdapter adaptercob;
    private EditText et_monto_aplicar;
    private Button btnDevLitePromptReset0;
    private Button btnDevLitePromptAdd1;
    private Button btnDevLitePromptAdd2;
    private Button btnDevLitePromptAdd5;
    private Button btnDevLitePromptAdd10;
    private String monto_aplicar_cobranzaadapter = "";
    private DataBase db;
  //  private static ArrayList<InvoiceDetails> invoice_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobranza);

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
        //btn_siguiente = (Button) findViewById(R.id.btn_siguiente);

        Bundle extras = getIntent().getExtras();
        nombre_cliente = extras.getString("pharmacy");
        txt_cliente.setText(nombre_cliente);

        setTitle("Cobranza  -   " + nombre_cliente);

        editsearch.addTextChangedListener(watcher1);

       /* btn_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag_next == 1) {
                    Intent intento = new Intent(Cobranza.this, CobranzaDetallePago.class);
                    intento.putExtra("pharmacy", txt_cliente.getText().toString());
                    startActivity(intento);
                } else {
                    Toast toast = Toast.makeText(Cobranza.this, "Debe confirmar el pago", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                    toast.show();
                }
            }
        });*/

/*        btn_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(arrayseleccionpagos.size()<0){
                AlertDialog.Builder builder = new AlertDialog.Builder(Cobranza.this);
                builder.setMessage(getString(R.string.confirmar))
                        .setPositiveButton(getString(R.string.confirmar_si), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                invoice_list = new ArrayList<InvoiceDetails>();

                                for (int i = 0; i < arrayfacturas.size(); i++) {
                                    if (arrayfacturas.get(i).get("selected").equals("true")) {
                                        InvoiceDetails invoice1 = new InvoiceDetails();
                                        //invoice1 = invoice_list1.get(i);
                                        invoice1.setDocumento(arrayfacturas.get(i).get("Documento"));
                                        invoice1.setCuenta(arrayfacturas.get(i).get("Cuenta"));
                                        invoice1.setFecha_Documento(arrayfacturas.get(i).get("Fecha_Documento"));
                                        invoice1.setFecha_Vencimiento(arrayfacturas.get(i).get("Fecha_Vencimiento"));
                                        invoice1.setImporte(arrayfacturas.get(i).get("Importe"));
                                        invoice1.setSaldo(arrayfacturas.get(i).get("Saldo"));
                                        invoice1.setDays(arrayfacturas.get(i).get("days"));
                                        invoice1.setSelected(arrayfacturas.get(i).get("selected"));
                                        invoice1.setAdeudo_cubrir(arrayfacturas.get(i).get("adeudo_cubrir"));
                                        invoice1.setPressed_again(arrayfacturas.get(i).get("pressed_again"));
                                        if (arrayfacturas.get(i).get("bandera").equals("2")) {
                                            invoice1.setSp_seleccion_banco(arrayfacturas.get(i).get("sp_seleccion_banco"));
                                            invoice1.setEt_num_cheque(arrayfacturas.get(i).get("et_num_cheque"));
                                            invoice1.setEt_fecha_cheque(arrayfacturas.get(i).get("et_fecha_cheque"));

                                        } else if (arrayfacturas.get(i).get("bandera").equals("3")) {
                                            invoice1.setSp_num_nota_credito(arrayfacturas.get(i).get("sp_num_nota_credito"));
                                            invoice1.setEt_monto_nota_credito(arrayfacturas.get(i).get("et_monto_nota_credito"));
                                            invoice1.setEt_fecha_nota_credito(arrayfacturas.get(i).get("et_fecha_nota_credito"));

                                        } else if (arrayfacturas.get(i).get("bandera").equals("4")) {
                                            invoice1.setEt_num_transferencia_bancaria(arrayfacturas.get(i).get("et_num_transferencia_bancaria"));
                                            invoice1.setEt_fecha_transferencia_bancaria(arrayfacturas.get(i).get("et_fecha_transferencia_bancaria"));

                                        } else if (arrayfacturas.get(i).get("bandera").equals("5")) {
                                            invoice1.setEt_num_ficha_pago(arrayfacturas.get(i).get("et_num_ficha_pago"));
                                            invoice1.setEt_fecha_ficha_pago(arrayfacturas.get(i).get("et_fecha_ficha_pago"));

                                        } else if (arrayfacturas.get(i).get("bandera").equals("6")) {
                                            invoice1.setEt_num_ficha_bancaria(arrayfacturas.get(i).get("et_num_ficha_bancaria"));
                                            invoice1.setEt_fecha_ficha_bancaria(arrayfacturas.get(i).get("et_fecha_ficha_bancaria"));
                                        }

                                        invoice1.setTipo_pago(arrayfacturas.get(i).get("tipo_pago"));
                                        invoice1.setRestantetotal(txt_total_pendiente.getText().toString());
                                        invoice1.setMontototal(txt_total_cubrir.getText().toString());
                                        invoice1.setPago_parcial(txtpago_parcial.getText().toString());
                                        invoice1.setPago_total(txtpago_total.getText().toString());
                                        invoice1.setDeudatotal(txt_total_importe.getText().toString());
                                        invoice_list.add(invoice1);
                                    }
                                }
                                openDialog();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                invoice_list = new ArrayList<InvoiceDetails>();

                                for (int i = 0; i < arrayfacturas.size(); i++) {
                                    if (arrayfacturas.get(i).get("selected").equals("true")) {
                                        InvoiceDetails invoice1 = new InvoiceDetails();
                                        invoice1.setDocumento(arrayfacturas.get(i).get("Documento"));
                                        invoice1.setCuenta(arrayfacturas.get(i).get("Cuenta"));
                                        invoice1.setFecha_Documento(arrayfacturas.get(i).get("Fecha_Documento"));
                                        invoice1.setFecha_Vencimiento(arrayfacturas.get(i).get("Fecha_Vencimiento"));
                                        invoice1.setImporte(arrayfacturas.get(i).get("Importe"));
                                        invoice1.setSaldo(arrayfacturas.get(i).get("Saldo"));
                                        invoice1.setDays(arrayfacturas.get(i).get("days"));
                                        invoice1.setSelected(arrayfacturas.get(i).get("selected"));
                                        invoice1.setAdeudo_cubrir(arrayfacturas.get(i).get("adeudo_cubrir"));
                                        invoice1.setPressed_again(arrayfacturas.get(i).get("pressed_again"));
                                        if (arrayfacturas.get(i).get("bandera").equals("2")) {
                                            invoice1.setSp_seleccion_banco(arrayfacturas.get(i).get("sp_seleccion_banco"));
                                            invoice1.setEt_num_cheque(arrayfacturas.get(i).get("et_num_cheque"));
                                            invoice1.setEt_fecha_cheque(arrayfacturas.get(i).get("et_fecha_cheque"));

                                        } else if (arrayfacturas.get(i).get("bandera").equals("3")) {
                                            invoice1.setSp_num_nota_credito(arrayfacturas.get(i).get("sp_num_nota_credito"));
                                            invoice1.setEt_monto_nota_credito(arrayfacturas.get(i).get("et_monto_nota_credito"));
                                            invoice1.setEt_fecha_nota_credito(arrayfacturas.get(i).get("et_fecha_nota_credito"));

                                        } else if (arrayfacturas.get(i).get("bandera").equals("4")) {
                                            invoice1.setEt_num_transferencia_bancaria(arrayfacturas.get(i).get("et_num_transferencia_bancaria"));
                                            invoice1.setEt_fecha_transferencia_bancaria(arrayfacturas.get(i).get("et_fecha_transferencia_bancaria"));

                                        } else if (arrayfacturas.get(i).get("bandera").equals("5")) {
                                            invoice1.setEt_num_ficha_pago(arrayfacturas.get(i).get("et_num_ficha_pago"));
                                            invoice1.setEt_fecha_ficha_pago(arrayfacturas.get(i).get("et_fecha_ficha_pago"));

                                        } else if (arrayfacturas.get(i).get("bandera").equals("6")) {
                                            invoice1.setEt_num_ficha_bancaria(arrayfacturas.get(i).get("et_num_ficha_bancaria"));
                                            invoice1.setEt_fecha_ficha_bancaria(arrayfacturas.get(i).get("et_fecha_ficha_bancaria"));
                                        }

                                        invoice1.setTipo_pago(arrayfacturas.get(i).get("tipo_pago"));
                                        invoice1.setRestantetotal(txt_total_pendiente.getText().toString());
                                        invoice1.setMontototal(txt_total_cubrir.getText().toString());
                                        invoice1.setPago_parcial(txtpago_parcial.getText().toString());
                                        invoice1.setPago_total(txtpago_total.getText().toString());
                                        invoice1.setDeudatotal(txt_total_importe.getText().toString());
                                        invoice_list.add(invoice1);
                                    }
                                }

                                flag_next = 1;
                                dialog.cancel();


                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                *//*}else{
                    Toast toast = Toast.makeText(Cobranza.this, "Debe seleccionar una factura",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                    toast.show();
                }*//*
            }
        });*/

        /*search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swicht_search == 0) {
                    UtilsCobranza.showSoftKeyboard(editsearch, Cobranza.this);
                    relativebuscar.setVisibility(View.VISIBLE);
                    btn_tipo_pago.setVisibility(View.GONE);
                    swicht_search = 1;
                } else {
                    UtilsCobranza.hideSoftKeyboard(Cobranza.this);
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

        db = new DataBase(Cobranza.this);
        agente = db.execSelect(db.QUERY_CLAVE_AGENT);

        new AsynkCobranza().execute();
    }


    TextWatcher watcher1 = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            adaptercob.getFilter().filter(s.toString());
        }

        public void afterTextChanged(Editable s) {
        }
    };


    public class AsynkCobranza extends AsyncTask<Void, Void, Integer> {

        int flag = 0, dias = 0;
        ArrayList<Dictionary<String, String>> arraygetfacturas = new ArrayList<Dictionary<String, String>>();

        protected void onPreExecute() {
            dialogo = ProgressDialog.show(Cobranza.this, "Espere un momento", "Cargando...");
        }

        @Override
        protected Integer doInBackground(Void... params) {

            if (!(agente.equals("") || agente.equals(null) || agente.equals("null"))) {

                arraygetfacturas = db.execSelectArrayList(db.QUERY_GET_FACTURAS + " '" + agente + "') ORDER BY FechaVencimiento");

                for (int i = 0; i < arraygetfacturas.size(); i++) {
                    Dictionary<String, String> dic = new Hashtable<String, String>();

                    dic.put("Documento", arraygetfacturas.get(i).get("NumeroFactura"));
                    dic.put("Cuenta", arraygetfacturas.get(i).get("id_cliente"));
                    dic.put("Fecha_Documento", arraygetfacturas.get(i).get("FechaFactura"));
                    dic.put("Fecha_Vencimiento", arraygetfacturas.get(i).get("FechaVencimiento"));
                    dic.put("Importe", arraygetfacturas.get(i).get("ValorOriginal"));
                    dic.put("Saldo", arraygetfacturas.get(i).get("SaldoDocRemanente"));
                    dic.put("days", arraygetfacturas.get(i).get("DiasAtraso"));
                    dic.put("selected", "false");
                    dic.put("adeudo_cubrir", "0");
                    dic.put("NewSaldo", arraygetfacturas.get(i).get("SaldoDocRemanente"));
                    dic.put("pressed_again", "0");

                    dic.put("bandera","");
                    dic.put("tipo_pago","");
                    dic.put("Restantetotal","");
                    dic.put("Montototal","");
                    dic.put("Pago_parcial","");
                    dic.put("Pago_total","");
                    dic.put("Deudatotal","");
                    dic.put("sp_seleccion_banco","");
                    dic.put("et_num_cheque","");
                    dic.put("et_fecha_cheque","");

                    dic.put("sp_num_nota_credito", "");
                    dic.put("et_monto_nota_credito", "");
                    dic.put("et_fecha_nota_credito", "");

                    dic.put("et_num_transferencia_bancaria", "");
                    dic.put("et_fecha_transferencia_bancaria", "");

                    dic.put("et_num_ficha_pago", "");
                    dic.put("et_fecha_ficha_pago", "");

                    dic.put("et_num_ficha_bancaria", "");
                    dic.put("et_fecha_ficha_bancaria", "");

                    arrayfacturas.add(dic);
                }

                total_pagos = Integer.parseInt(db.execSelect(db.QUERY_GET_COUNT_ROWS + " '" + agente + "')"));

                flag = 2;
            } else {
                flag = 1;
            }

            /*if (UtilsCobranza.isNetwork(getApplicationContext())){

                WebServices ws = new WebServices();
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

                            *//**//*String fechav = new String(Fecha_Vencimiento);

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
                            }*//**//*
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
            }*/
            return null;
        }

        protected void onPostExecute(Integer result) {
            if ((dialogo != null) && dialogo.isShowing()) {
                dialogo.dismiss();
            }

            /*if(flag==0){
                Toast toast = Toast.makeText(Cobranza.this, getString(R.string.sin_internet),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                toast.show();
            }*/

            if (flag == 1) {
                Toast toast = Toast.makeText(Cobranza.this, getString(R.string.error_usuario), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                toast.show();
            }

            if (flag == 2) {

                db = new DataBase(Cobranza.this);
                String imp=db.execSelect(db.QUERY_GET_SIZE_IMPORTE_TOTAL + " '" + agente + "')");
                String impp=db.execSelect(db.QUERY_GET_SIZE_SALDO_TOTAL + " '" + agente + "')");

                if(imp==null)
                    imp="0";

                if(impp==null)
                    impp="0";

                txt_total_importe.setText("$" + imp + "");
                txt_total_pendiente.setText("$" + impp+ "");

                txtpago_total.setText("0/" + total_pagos);
                txtpago_parcial.setText("0/" + total_pagos);

                adaptercob = new CobranzaAdapter(arrayfacturas, Cobranza.this, bandera);
                lv_facturas.setAdapter(adaptercob);

                /*lv_facturas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        if (bandera != 0) {
                            final Dictionary<String, String> dic = (Dictionary<String, String>) adaptercob.getItem(position);

                            if (dic.get("pressed_again").equals("0")) {
                                if (!(dic.get("Importe").equals(dic.get("Saldo")))) {

                                    Float importe_adeudo_acubiri = Float.parseFloat(dic.get("Importe").replace("$", "").replace(",", ""))
                                            - Float.parseFloat(dic.get("Saldo").replace("$", "").replace(",", ""));
                                    dic.put("Saldo", dic.get("NewSaldo"));
                                    dic.put("adeudo_cubrir", importe_adeudo_acubiri + "");
                                    dic.put("selected", "true");
                                    dic.put("pressed_again", "1");
                                    for (int i = 0; i < arrayseleccionpagos.size(); i++) {
                                        if (arrayseleccionpagos.get(i).get("bandera").equals("2")) {
                                            dic.put("sp_seleccion_banco", arrayseleccionpagos.get(i).get("sp_seleccion_banco"));
                                            dic.put("et_num_cheque", arrayseleccionpagos.get(i).get("et_num_cheque"));
                                            dic.put("et_fecha_cheque", arrayseleccionpagos.get(i).get("et_fecha_cheque"));
                                        } else if (arrayseleccionpagos.get(i).get("bandera").equals("3")) {
                                            dic.put("sp_num_nota_credito", arrayseleccionpagos.get(i).get("sp_num_nota_credito"));
                                            dic.put("et_monto_nota_credito", arrayseleccionpagos.get(i).get("et_monto_nota_credito"));
                                            dic.put("et_fecha_nota_credito", arrayseleccionpagos.get(i).get("et_fecha_nota_credito"));
                                        } else if (arrayseleccionpagos.get(i).get("bandera").equals("4")) {
                                            dic.put("et_num_transferencia_bancaria", arrayseleccionpagos.get(i).get("et_num_transferencia_bancaria"));
                                            dic.put("et_fecha_transferencia_bancaria", arrayseleccionpagos.get(i).get("et_fecha_transferencia_bancaria"));
                                        } else if (arrayseleccionpagos.get(i).get("bandera").equals("5")) {
                                            dic.put("et_num_ficha_pago", arrayseleccionpagos.get(i).get("et_num_ficha_pago"));
                                            dic.put("et_fecha_ficha_pago", arrayseleccionpagos.get(i).get("et_fecha_ficha_pago"));
                                        } else if (arrayseleccionpagos.get(i).get("bandera").equals("6")) {
                                            dic.put("et_num_ficha_bancaria", arrayseleccionpagos.get(i).get("et_num_ficha_bancaria"));
                                            dic.put("et_fecha_ficha_bancaria", arrayseleccionpagos.get(i).get("et_fecha_ficha_bancaria"));
                                        }
                                        dic.put("tipo_pago", arrayseleccionpagos.get(i).get("tipo_pago"));
                                        dic.put("bandera", arrayseleccionpagos.get(i).get("bandera"));
                                        dic.put("monto_aplicar", txt_total_cubrir.getText().toString());
                                    }
                                adaptercob.notifyDataSetChanged();

                                Float pendiente = Float.parseFloat(txt_total_pendiente.getText().toString().replace("$", "").replace(",", ""))
                                        - Float.parseFloat(dic.get("adeudo_cubrir"));
                                Float acubrir = Float.parseFloat(txt_total_cubrir.getText().toString().replace("$", "").replace(",", ""))
                                        + Float.parseFloat(dic.get("adeudo_cubrir"));

                                txt_total_pendiente.setText("$" + UtilsCobranza.textdecimal(pendiente + ""));
                                txt_total_cubrir.setText("$" + UtilsCobranza.textdecimal(acubrir + ""));

                            } else {
                                dic.put("Saldo", "0");
                                dic.put("adeudo_cubrir", dic.get("Importe"));
                                dic.put("selected", "true");
                                dic.put("pressed_again", "1");
                                    for (int i = 0; i < arrayseleccionpagos.size(); i++) {
                                        if (arrayseleccionpagos.get(i).get("bandera").equals("2")) {
                                            dic.put("sp_seleccion_banco", arrayseleccionpagos.get(i).get("sp_seleccion_banco"));
                                            dic.put("et_num_cheque", arrayseleccionpagos.get(i).get("et_num_cheque"));
                                            dic.put("et_fecha_cheque", arrayseleccionpagos.get(i).get("et_fecha_cheque"));
                                        } else if (arrayseleccionpagos.get(i).get("bandera").equals("3")) {
                                            dic.put("sp_num_nota_credito", arrayseleccionpagos.get(i).get("sp_num_nota_credito"));
                                            dic.put("et_monto_nota_credito", arrayseleccionpagos.get(i).get("et_monto_nota_credito"));
                                            dic.put("et_fecha_nota_credito", arrayseleccionpagos.get(i).get("et_fecha_nota_credito"));
                                        } else if (arrayseleccionpagos.get(i).get("bandera").equals("4")) {
                                            dic.put("et_num_transferencia_bancaria", arrayseleccionpagos.get(i).get("et_num_transferencia_bancaria"));
                                            dic.put("et_fecha_transferencia_bancaria", arrayseleccionpagos.get(i).get("et_fecha_transferencia_bancaria"));
                                        } else if (arrayseleccionpagos.get(i).get("bandera").equals("5")) {
                                            dic.put("et_num_ficha_pago", arrayseleccionpagos.get(i).get("et_num_ficha_pago"));
                                            dic.put("et_fecha_ficha_pago", arrayseleccionpagos.get(i).get("et_fecha_ficha_pago"));
                                        } else if (arrayseleccionpagos.get(i).get("bandera").equals("6")) {
                                            dic.put("et_num_ficha_bancaria", arrayseleccionpagos.get(i).get("et_num_ficha_bancaria"));
                                            dic.put("et_fecha_ficha_bancaria", arrayseleccionpagos.get(i).get("et_fecha_ficha_bancaria"));
                                        }
                                        dic.put("tipo_pago", arrayseleccionpagos.get(i).get("tipo_pago"));
                                        dic.put("bandera", arrayseleccionpagos.get(i).get("bandera"));
                                        dic.put("monto_aplicar", txt_total_cubrir.getText().toString());
                                    }

                                adaptercob.notifyDataSetChanged();

                                Float pendiente = Float.parseFloat(txt_total_pendiente.getText().toString().replace("$", "").replace(",", ""))
                                        - Float.parseFloat(dic.get("Importe"));
                                Float acubrir = Float.parseFloat(txt_total_cubrir.getText().toString().replace("$", "").replace(",", ""))
                                        + Float.parseFloat(dic.get("Importe"));

                                txt_total_pendiente.setText("$" + UtilsCobranza.textdecimal(pendiente + ""));
                                txt_total_cubrir.setText("$" + UtilsCobranza.textdecimal(acubrir + ""));

                            }

                            if (dic.get("selected").compareTo("true") == 0) {
                                pagoshechos++;
                            }

                            txtpago_total.setText(pagoshechos + "/" + total_pagos);

                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(Cobranza.this);
                            builder.setMessage(R.string.quitar_cubrir_adeudo)
                                    .setPositiveButton(R.string.cubrir_adeudo_si, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            Dictionary<String, String> dic = (Dictionary<String, String>) adaptercob.getItem(position);

                                            Float pendiente = Float.parseFloat(txt_total_pendiente.getText().toString().replace("$", "").replace(",", ""))
                                                    + Float.parseFloat(dic.get("adeudo_cubrir"));
                                            Float acubrir = Float.parseFloat(txt_total_cubrir.getText().toString().replace("$", "").replace(",", ""))
                                                    - Float.parseFloat(dic.get("adeudo_cubrir"));

                                            txt_total_pendiente.setText("$" + UtilsCobranza.textdecimal(pendiente + ""));
                                            txt_total_cubrir.setText("$" + UtilsCobranza.textdecimal(acubrir + ""));

                                            String s = dic.get("adeudo_cubrir");

                                            dic.put("Saldo", dic.get("NewSaldo"));
                                            dic.put("adeudo_cubrir", "0");
                                            dic.put("selected", "false");
                                            dic.put("pressed_again", "0");

                                            dic.put("sp_seleccion_banco", "0");
                                            dic.put("et_num_cheque", "0");
                                            dic.put("et_fecha_cheque", "0");
                                            dic.put("sp_num_nota_credito", "0");
                                            dic.put("et_monto_nota_credito", "0");
                                            dic.put("et_fecha_nota_credito", "0");
                                            dic.put("et_num_transferencia_bancaria", "0");
                                            dic.put("et_fecha_transferencia_bancaria", "0");
                                            dic.put("et_num_ficha_pago", "0");
                                            dic.put("et_fecha_ficha_pago", "0");
                                            dic.put("et_num_ficha_bancaria", "0");
                                            dic.put("et_fecha_ficha_bancaria", "0");
                                            dic.put("tipo_pago", "0");
                                            dic.put("bandera", "0");
                                            dic.put("monto_aplicar", "0");

                                            adaptercob.notifyDataSetChanged();

                                            float fadeudoc = Float.parseFloat(s);
                                            float fsaldo = Float.parseFloat(dic.get("NewSaldo"));

                                            if (dic.get("selected").compareTo("false") == 0) {
                                                if (Math.abs(fadeudoc) == fsaldo) {
                                                    pagoshechos--;
                                                    txtpago_total.setText(pagoshechos + "/" + total_pagos);
                                                } else {
                                                    pagoshechosdialog--;
                                                    txtpago_parcial.setText(pagoshechosdialog + "/" + total_pagos);
                                                }
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

                    else

                    {
                        Toast toast = Toast.makeText(Cobranza.this, R.string.sinseleccionar_dialog, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                    }

                    return true;
                }
            });

                lv_facturas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

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

                            if (dic.get("pressed_again").equals("0")) {
                                openDialogEditarMonto(dic.get("Importe"),
                                        fechad.substring(0, 4) + "/" + fechad.substring(4, 6) + "/" + fechad.substring(6, 8),
                                        fechav.substring(0, 4) + "/" + fechav.substring(4, 6) + "/" + fechav.substring(6, 8),
                                        days, position);
                            } else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(Cobranza.this);
                                builder.setMessage(R.string.quitar_cubrir_adeudo)
                                        .setPositiveButton(R.string.cubrir_adeudo_si, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                Dictionary<String, String> dic = (Dictionary<String, String>) adaptercob.getItem(position);

                                                Float pendiente = Float.parseFloat(txt_total_pendiente.getText().toString().replace("$", "").replace(",", ""))
                                                        + Float.parseFloat(dic.get("adeudo_cubrir"));
                                                Float acubrir = Float.parseFloat(txt_total_cubrir.getText().toString().replace("$", "").replace(",", ""))
                                                        - Float.parseFloat(dic.get("adeudo_cubrir"));

                                                txt_total_pendiente.setText("$" + UtilsCobranza.textdecimal(pendiente + ""));
                                                txt_total_cubrir.setText("$" + UtilsCobranza.textdecimal(acubrir + ""));

                                                String s = dic.get("adeudo_cubrir");

                                                dic.put("Saldo", dic.get("NewSaldo"));
                                                dic.put("adeudo_cubrir", "0");
                                                dic.put("selected", "false");
                                                dic.put("pressed_again", "0");

                                                dic.put("sp_seleccion_banco", "0");
                                                dic.put("et_num_cheque", "0");
                                                dic.put("et_fecha_cheque", "0");
                                                dic.put("sp_num_nota_credito", "0");
                                                dic.put("et_monto_nota_credito", "0");
                                                dic.put("et_fecha_nota_credito", "0");
                                                dic.put("et_num_transferencia_bancaria", "0");
                                                dic.put("et_fecha_transferencia_bancaria", "0");
                                                dic.put("et_num_ficha_pago", "0");
                                                dic.put("et_fecha_ficha_pago", "0");
                                                dic.put("et_num_ficha_bancaria", "0");
                                                dic.put("et_fecha_ficha_bancaria", "0");
                                                dic.put("tipo_pago", "0");
                                                dic.put("bandera", "0");
                                                dic.put("monto_aplicar", "0");

                                                adaptercob.notifyDataSetChanged();

                                                float fadeudoc = Float.parseFloat(s);
                                                float fsaldo = Float.parseFloat(dic.get("NewSaldo"));

                                                if (dic.get("selected").compareTo("false") == 0) {
                                                    if (Math.abs(fadeudoc) == fsaldo) {
                                                        pagoshechos--;
                                                        txtpago_total.setText(pagoshechos + "/" + total_pagos);
                                                    } else {
                                                        pagoshechosdialog--;
                                                        txtpago_parcial.setText(pagoshechosdialog + "/" + total_pagos);
                                                    }
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

    public void openDialogEditarMonto(final String monto_total, String fechae, String fechacv, int days, final int position) {
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

        btnDevLitePromptReset0.setOnClickListener(btnsuma_monto_cobranza);
        btnDevLitePromptAdd1.setOnClickListener(btnsuma_monto_cobranza);
        btnDevLitePromptAdd2.setOnClickListener(btnsuma_monto_cobranza);
        btnDevLitePromptAdd5.setOnClickListener(btnsuma_monto_cobranza);
        btnDevLitePromptAdd10.setOnClickListener(btnsuma_monto_cobranza);

        txt_monto_total.setText("$" + monto_total);
        txtfecha_e.setText(fechae);
        txtfecha_v.setText(fechacv);
        txtdias_f.setText(days + " " + Cobranza.this.getString(R.string.dias_adapter));

        btn_guardar_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_monto_aplicar.getText().toString().equals("")) {
                    et_monto_aplicar.setError("Debe agregar un monto a aplicar");
                } else {
                    float dmonto_t = Float.parseFloat(monto_total);
                    float d_monto_a = Float.parseFloat(et_monto_aplicar.getText().toString());
                    if (Math.abs(d_monto_a) > dmonto_t) {
                        et_monto_aplicar.setError("El monto a aplicar no debe ser mayor que el monto total de la factura");
                    } else {
                        monto_aplicar_cobranzaadapter = et_monto_aplicar.getText().toString();

                        Dictionary<String, String> dic = (Dictionary<String, String>) adaptercob.getItem(position);
                        float resultsaldo = Float.parseFloat(dic.get("Importe")) - Float.parseFloat(monto_aplicar_cobranzaadapter);

                        dic.put("selected", "true");
                        dic.put("adeudo_cubrir", monto_aplicar_cobranzaadapter);
                        dic.put("Saldo", resultsaldo + "");
                        dic.put("pressed_again", "1");
                        for (int i = 0; i < arrayseleccionpagos.size(); i++) {
                            if (arrayseleccionpagos.get(i).get("bandera").equals("2")) {
                                dic.put("sp_seleccion_banco", arrayseleccionpagos.get(i).get("sp_seleccion_banco"));
                                dic.put("et_num_cheque", arrayseleccionpagos.get(i).get("et_num_cheque"));
                                dic.put("et_fecha_cheque", arrayseleccionpagos.get(i).get("et_fecha_cheque"));
                            } else if (arrayseleccionpagos.get(i).get("bandera").equals("3")) {
                                dic.put("sp_num_nota_credito", arrayseleccionpagos.get(i).get("sp_num_nota_credito"));
                                dic.put("et_monto_nota_credito", arrayseleccionpagos.get(i).get("et_monto_nota_credito"));
                                dic.put("et_fecha_nota_credito", arrayseleccionpagos.get(i).get("et_fecha_nota_credito"));
                            } else if (arrayseleccionpagos.get(i).get("bandera").equals("4")) {
                                dic.put("et_num_transferencia_bancaria", arrayseleccionpagos.get(i).get("et_num_transferencia_bancaria"));
                                dic.put("et_fecha_transferencia_bancaria", arrayseleccionpagos.get(i).get("et_fecha_transferencia_bancaria"));
                            } else if (arrayseleccionpagos.get(i).get("bandera").equals("5")) {
                                dic.put("et_num_ficha_pago", arrayseleccionpagos.get(i).get("et_num_ficha_pago"));
                                dic.put("et_fecha_ficha_pago", arrayseleccionpagos.get(i).get("et_fecha_ficha_pago"));
                            } else if (arrayseleccionpagos.get(i).get("bandera").equals("6")) {
                                dic.put("et_num_ficha_bancaria", arrayseleccionpagos.get(i).get("et_num_ficha_bancaria"));
                                dic.put("et_fecha_ficha_bancaria", arrayseleccionpagos.get(i).get("et_fecha_ficha_bancaria"));
                            }
                            dic.put("tipo_pago", arrayseleccionpagos.get(i).get("tipo_pago"));
                            dic.put("bandera", arrayseleccionpagos.get(i).get("bandera"));
                            dic.put("monto_aplicar", txt_total_cubrir.getText().toString());
                        }
                        adaptercob.notifyDataSetChanged();

                        Float pendiente = Float.parseFloat(txt_total_pendiente.getText().toString().replace("$", "").replace(",", ""))
                                - Float.parseFloat(monto_aplicar_cobranzaadapter);
                        Float acubrir = Float.parseFloat(txt_total_cubrir.getText().toString().replace("$", "").replace(",", ""))
                                + Float.parseFloat(monto_aplicar_cobranzaadapter);

                        txt_total_pendiente.setText("$" + UtilsCobranza.textdecimal(pendiente + ""));
                        txt_total_cubrir.setText("$" + UtilsCobranza.textdecimal(acubrir + ""));

                        float fadeudoc = Float.parseFloat(dic.get("adeudo_cubrir"));
                        float fsaldo = Float.parseFloat(dic.get("NewSaldo"));

                        if (dic.get("selected").compareTo("true") == 0) {
                            if (Math.abs(fadeudoc) == fsaldo) {
                                pagoshechos++;
                                txtpago_total.setText(pagoshechos + "/" + total_pagos);
                            } else {
                                pagoshechosdialog++;
                                txtpago_parcial.setText(pagoshechosdialog + "/" + total_pagos);
                            }
                        }
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

    private View.OnClickListener btnsuma_monto_cobranza = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String strAmountReturn = et_monto_aplicar.getText().toString().trim();
            if (strAmountReturn.length() == 0) strAmountReturn = "0";

            if (view == btnDevLitePromptReset0) {
                strAmountReturn = "";
            } else if (view == btnDevLitePromptAdd1) {
                strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "100")).toString();
            } else if (view == btnDevLitePromptAdd2) {
                strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "500")).toString();
            } else if (view == btnDevLitePromptAdd5) {
                strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "1000")).toString();
            } else if (view == btnDevLitePromptAdd10) {
                strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "5000")).toString();
            }
            et_monto_aplicar.setText(strAmountReturn);
            et_monto_aplicar.setSelection(strAmountReturn.length(), strAmountReturn.length());
        }
    };

    private void openDialog() {
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

        et_monto_nota_credito.setEnabled(false);
        et_fecha_nota_credito.setEnabled(false);

        final TextWatcher tw = new TextWatcher() {

            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        if (mon > 12) mon = Calendar.getInstance().get(Calendar.MONTH) + 1;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? Calendar.getInstance().get(Calendar.YEAR) : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.get(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;

                    if (band[0] == 2) {
                        et_fecha_cheque.setText(current);
                        et_fecha_cheque.setSelection(sel < current.length() ? sel : current.length());
                    } else if (band[0] == 4) {
                        et_fecha_transferencia_bancaria.setText(current);
                        et_fecha_transferencia_bancaria.setSelection(sel < current.length() ? sel : current.length());
                    } else if (band[0] == 5) {
                        et_fecha_ficha_pago.setText(current);
                        et_fecha_ficha_pago.setSelection(sel < current.length() ? sel : current.length());
                    } else if (band[0] == 6) {
                        et_fecha_ficha_bancaria.setText(current);
                        et_fecha_ficha_bancaria.setSelection(sel < current.length() ? sel : current.length());
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        et_fecha_cheque.addTextChangedListener(tw);
        et_fecha_transferencia_bancaria.addTextChangedListener(tw);
        et_fecha_ficha_pago.addTextChangedListener(tw);
        et_fecha_ficha_bancaria.addTextChangedListener(tw);

        scroll.setScrollbarFadingEnabled(false);
        scroll.setVerticalScrollBarEnabled(true);
        scroll.setVerticalFadingEdgeEnabled(false);

        List<String> list = new ArrayList<String>();
        List<String> lista = new ArrayList<String>();
        list = db.execSelectList(db.QUERY_GET_BANK);
        lista.add(getString(R.string.banco_prompt));
        list.add(0, lista.get(0));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_seleccion_banco.setAdapter(dataAdapter);
        sp_seleccion_banco.setOnItemSelectedListener(OnCatSpinnerCL);


        List<String> list2 = new ArrayList<String>();
        List<String> list_credito = new ArrayList<String>();
        list2 = db.execSelectList(db.QUERY_GET_CREDIT_NOTES);
        list_credito.add(getString(R.string.notac_prompt));
        list2.add(0, list_credito.get(0));

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_num_nota_credito.setAdapter(dataAdapter1);

        sp_num_nota_credito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(16);

                if (!parent.getItemAtPosition(position).toString().equals(getString(R.string.notac_prompt))) {
                    ArrayList<Dictionary<String, String>> list_credito_setData = new ArrayList<Dictionary<String, String>>();
                    list_credito_setData = db.execSelectArrayList(db.QUERY_GET_CREDIT_NOTES_COMPLETE + "'" + parent.getItemAtPosition(position).toString() + "'");

                    for (int i = 0; i < list_credito_setData.size(); i++) {
                        et_monto_nota_credito.setText(list_credito_setData.get(i).get("ValorOriginal").replace("-", ""));

                        String fechan = new String(list_credito_setData.get(i).get("FechaNota"));
                        et_fecha_nota_credito.setText(fechan.substring(0, 4) + "/" + fechan.substring(4, 6) + "/" + fechan.substring(6, 8));
                    }
                } else {
                    et_monto_nota_credito.setText("");
                    et_fecha_nota_credito.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


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
                    arrayseleccionpagos = new ArrayList<Dictionary<String, String>>();
                    btn_tipo_pago.setText(getString(R.string.efectivo));
                    dic.put("tipo_pago", getString(R.string.efectivo));
                    dic.put("bandera", bandera + "");
                    arrayseleccionpagos.add(dic);

                    btn_confirmar.setVisibility(View.VISIBLE);
                    btn_confirmar.setText(getString(R.string.conf_efectivo));
                    dialog.dismiss();
                } else if (band[0] == 2) {
                    if (sp_seleccion_banco.getSelectedItem().equals("") || sp_seleccion_banco.getSelectedItem().equals(getString(R.string.banco_prompt))) {
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
                    else {
                        scheque_banco = sp_seleccion_banco.getSelectedItem().toString();
                        scheque_numero = et_num_cheque.getText().toString();
                        scheque_fecha = et_fecha_cheque.getText().toString();
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.datos_guardar_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                        btn_confirmar.setText(getString(R.string.conf_cheque));

                        bandera = 2;
                        Dictionary<String, String> dic = new Hashtable<String, String>();
                        arrayseleccionpagos = new ArrayList<Dictionary<String, String>>();
                        dic.put("sp_seleccion_banco", sp_seleccion_banco.getSelectedItem() + "");
                        dic.put("et_num_cheque", et_num_cheque.getText().toString());
                        dic.put("et_fecha_cheque", et_fecha_cheque.getText().toString());
                        dic.put("tipo_pago", getString(R.string.cheque));
                        btn_tipo_pago.setText(getString(R.string.cheque));
                        dic.put("bandera", bandera + "");
                        arrayseleccionpagos.add(dic);

                        dialog.dismiss();
                    }
                } else if (band[0] == 3) {
                    if (sp_num_nota_credito.getSelectedItem().equals("") || sp_num_nota_credito.getSelectedItem().equals(getString(R.string.notac_prompt))) {
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.seleccion_nota_credito_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                    }
                    else {
                        snotac_numero = sp_num_nota_credito.getSelectedItem().toString();
                        snotac_monto = et_monto_nota_credito.getText().toString();
                        snotac_fecha = et_fecha_nota_credito.getText().toString();
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.datos_guardar_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 80);
                        toast.show();
                        btn_confirmar.setText(getString(R.string.conf_nota_cre));

                        bandera = 3;
                        Dictionary<String, String> dic = new Hashtable<String, String>();
                        arrayseleccionpagos = new ArrayList<Dictionary<String, String>>();
                        dic.put("et_monto_nota_credito", et_monto_nota_credito.getText().toString());
                        dic.put("et_fecha_nota_credito", et_fecha_nota_credito.getText().toString());
                        dic.put("sp_num_nota_credito", sp_num_nota_credito.getSelectedItem() + "");
                        dic.put("tipo_pago", getString(R.string.nota_credito));
                        btn_tipo_pago.setText(getString(R.string.nota_credito));
                        dic.put("bandera", bandera + "");
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
                    else {
                        stransb_numero = et_num_transferencia_bancaria.getText().toString();
                        stransb_fecha = et_fecha_transferencia_bancaria.getText().toString();
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.datos_guardar_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 80);
                        toast.show();
                        btn_confirmar.setText(getString(R.string.conf_trans_bancaria));

                        bandera = 4;
                        Dictionary<String, String> dic = new Hashtable<String, String>();
                        arrayseleccionpagos = new ArrayList<Dictionary<String, String>>();
                        dic.put("et_num_transferencia_bancaria", et_num_transferencia_bancaria.getText().toString());
                        dic.put("et_fecha_transferencia_bancaria", et_fecha_transferencia_bancaria.getText().toString());
                        dic.put("tipo_pago", getString(R.string.transferencia_bancaria));
                        btn_tipo_pago.setText(getString(R.string.transferencia_bancaria));
                        dic.put("bandera", bandera + "");
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
                    else {
                        sfichap_numero = et_num_ficha_pago.getText().toString();
                        sfichap_fecha = et_fecha_ficha_pago.getText().toString();
                        Toast toast = Toast.makeText(Cobranza.this, getString(R.string.datos_guardar_dialog), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 80);
                        toast.show();
                        btn_confirmar.setText(getString(R.string.conf_ficha_pago));

                        bandera = 5;
                        Dictionary<String, String> dic = new Hashtable<String, String>();
                        arrayseleccionpagos = new ArrayList<Dictionary<String, String>>();
                        dic.put("et_num_ficha_pago", et_num_ficha_pago.getText().toString());
                        dic.put("et_fecha_ficha_pago", et_fecha_ficha_pago.getText().toString());
                        dic.put("tipo_pago", getString(R.string.ficha_pago));
                        btn_tipo_pago.setText(getString(R.string.ficha_pago));
                        dic.put("bandera", bandera + "");
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
                        btn_confirmar.setText(getString(R.string.conf_ficha_bancaria));

                        bandera = 6;
                        Dictionary<String, String> dic = new Hashtable<String, String>();
                        arrayseleccionpagos = new ArrayList<Dictionary<String, String>>();
                        dic.put("et_num_ficha_bancaria", et_num_ficha_bancaria.getText().toString());
                        dic.put("et_fecha_ficha_bancaria", et_fecha_ficha_bancaria.getText().toString());
                        dic.put("tipo_pago", getString(R.string.ficha_bancaria));
                        btn_tipo_pago.setText(getString(R.string.ficha_bancaria));
                        dic.put("bandera", bandera + "");
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
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            ((TextView) parent.getChildAt(0)).setTextSize(16);

        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    protected void onDestroy() {
        if ((dialogo != null) && dialogo.isShowing()) {
            dialogo.dismiss();
        }
        dialogo = null;
        super.onDestroy();
    }

    /*public static ArrayList<InvoiceDetails> getInvoice() {
        return invoice_list;
    }*/
}

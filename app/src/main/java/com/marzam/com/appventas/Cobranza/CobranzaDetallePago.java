package com.marzam.com.appventas.Cobranza;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.marzam.com.appventas.Gesture.Dib_firma;
import com.marzam.com.appventas.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by fgalvez on 03/07/2015.
 */
public class CobranzaDetallePago extends Activity{

    private EditText et_correoe;
    private ImageButton img_firma;
    private Button btn_confirmar_pagos;
    private TextView txt_fecha_actual, txt_cliente_pagos;
    public static TextView txt_total_pagos;
    private ListView lv_pagos;
    private String nombre_cliente;
    private CobranzaDetallePagoAdapter adapterdetalleopagocob;
    private float var, var1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobranza_detalle_pago);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setTitle("Cobranza");

        Log.i("array", " = "+Cobranza.arrayseleccionpagosfinal.toString());

        et_correoe = (EditText) findViewById(R.id.et_correoe);
        img_firma = (ImageButton) findViewById(R.id.img_firma);
        btn_confirmar_pagos = (Button) findViewById(R.id.btn_confirmar_pagos);
        txt_total_pagos = (TextView) findViewById(R.id.txt_total_pagos);
        txt_fecha_actual = (TextView) findViewById(R.id.txt_fecha_actual);
        txt_cliente_pagos = (TextView) findViewById(R.id.txt_cliente_pagos);
        lv_pagos = (ListView) findViewById(R.id.lv_pagos);

        for(int j=0;j<Cobranza.arrayseleccionpagosfinal.size();j++){
            var1 = var + Float.parseFloat(Cobranza.arrayseleccionpagosfinal.get(j).get("monto_aplicar").replace("$","").replace(",",""));
            var = var1;
        }
        Log.i("var2", "var2 = " + var);
        txt_total_pagos.setText("$" + UtilsCobranza.textdecimal(var + ""));

        Bundle extras = getIntent().getExtras();
        nombre_cliente = extras.getString("pharmacy");
        txt_cliente_pagos.setText(nombre_cliente);

        UtilsCobranza.setListViewHeightBasedOnChildren(lv_pagos);

        lv_pagos.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        adapterdetalleopagocob = new CobranzaDetallePagoAdapter(Cobranza.arrayseleccionpagosfinal,CobranzaDetallePago.this);
        lv_pagos.setAdapter(adapterdetalleopagocob);


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy");
        txt_fecha_actual.setText(sdf.format(cal.getTime()));

        img_firma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento = new Intent(CobranzaDetallePago.this, Dib_firma.class);
                intento.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intento);
            }
        });


        btn_confirmar_pagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CobranzaDetallePago.this);
                builder.setMessage(getString(R.string.pagos_hecho))
                        .setPositiveButton(getString(R.string.pagos_hecho_aceptar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                et_correoe.getText().toString();
            }
        });

    }
}

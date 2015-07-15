package com.marzam.com.appventas.Cobranza;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.marzam.com.appventas.R;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Created by fgalvez on 06/07/2015.
 */
public class CobranzaDetallePagoAdapter extends BaseAdapter {

    private ArrayList<Dictionary<String, String>> ListaDatos=null;
    private Context cobranza;
    private TextView txt_transaccion,text_monto_operacion;
    private LinearLayout ll_detallepago;

    public CobranzaDetallePagoAdapter(ArrayList<Dictionary<String, String>> arrayfacturas, CobranzaDetallePago cobranza) {
        this.ListaDatos = arrayfacturas;
        this.cobranza = cobranza;
    }

    @Override
    public int getCount() {
        return ListaDatos.size();
    }

    @Override
    public Object getItem(int arg0) {
        return ListaDatos.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int arg0, final View arg1, ViewGroup arg2) {
        View view = arg1;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater)cobranza.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cobranza_detallepago_adapter, arg2, false);
            txt_transaccion = (TextView) view.findViewById(R.id.txt_transaccion);
            text_monto_operacion = (TextView) view.findViewById(R.id.text_monto_operacion);
            ll_detallepago = (LinearLayout) view.findViewById(R.id.ll_detallepago);
        }

        txt_transaccion.setText(ListaDatos.get(arg0).get("tipo_pago"));
        text_monto_operacion.setText("$" + UtilsCobranza.textdecimal(ListaDatos.get(arg0).get("monto_aplicar").replace("$","").replace(",","")));

        ll_detallepago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento = new Intent(cobranza,Cobranza.class);
                cobranza.startActivity(intento);
            }
        });
        return view;
    }
}

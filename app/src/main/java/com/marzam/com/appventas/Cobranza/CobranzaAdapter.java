package com.marzam.com.appventas.Cobranza;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.marzam.com.appventas.DevolucionesLite.CustomPrompt.NumeroEnorme;
import com.marzam.com.appventas.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

/**
 * Created by fgalvez on 02/07/2015.
 */
public class CobranzaAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Dictionary<String, String>> ListaDatos=null;
    private ArrayList<Dictionary<String, String>> ListaDatosfirst=null;
    private  Context cobranza;
    private int flag;
    public static EditText et_monto_aplicar;
    public static Button btnDevLitePromptReset0;
    public static Button btnDevLitePromptAdd1;
    public static Button btnDevLitePromptAdd2;
    public static Button btnDevLitePromptAdd5;
    public static Button btnDevLitePromptAdd10;
    public static String monto_aplicar="";
    private ItemFilter myFilter = new ItemFilter();
    public static TextView txtnum_factura,txtfecha_v,txt_importe,txtadeudo_p,txtadeudo_c;
    private LinearLayout ll;


    public CobranzaAdapter(ArrayList<Dictionary<String, String>> arrayfacturas, Cobranza cobranza, int bandera) {
        this.ListaDatos = arrayfacturas;
        this.ListaDatosfirst = arrayfacturas;
        this.cobranza = cobranza;
        this.flag = bandera;
    }


    @Override
    public Filter getFilter() {
        return this.myFilter;
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
        //final HolderFactura holder;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)cobranza.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cobranza_adapter, arg2, false);
            //holder = new HolderFactura();
            //view.setTag(holder);
        }
        else{
            //holder = (HolderFactura) view.getTag();
        }

        if (arg0 % 2 == 0) {
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }else {
            view.setBackgroundColor(Color.parseColor("#F2F2F2"));
        }

        txtnum_factura = (TextView) view.findViewById(R.id.txtnum_factura);
        txtfecha_v = (TextView) view.findViewById(R.id.txtfecha_v);
        txt_importe = (TextView) view.findViewById(R.id.txt_importe);
        txtadeudo_p = (TextView) view.findViewById(R.id.txtadeudo_p);
        txtadeudo_c = (TextView) view.findViewById(R.id.txtadeudo_c);
        ll = (LinearLayout) view.findViewById(R.id.ll);

        txtnum_factura.setText(ListaDatos.get(arg0).get("Documento"));

        String fechav = new String(ListaDatos.get(arg0).get("Fecha_Vencimiento"));
        txtfecha_v.setText(fechav.substring(0, 4) + "/" + fechav.substring(4, 6) + "/" + fechav.substring(6, 8));

        if (ListaDatos.get(arg0).get("selected").compareTo("true")==0){
            view.setBackgroundColor(Color.parseColor("#89BBEE"));
        }else{
            if (arg0 % 2 == 0) {
                view.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }else {
                view.setBackgroundColor(Color.parseColor("#F2F2F2"));
            }
        }




        /*if(Integer.parseInt(ListaDatos.get(arg0).get("days"))<0){
            days=0;
        }else{
            days=Integer.parseInt(ListaDatos.get(arg0).get("days"));
        }*/

                /*String fechad = new String(ListaDatos.get(arg0).get("Fecha_Documento"));

                String fechav = new String(ListaDatos.get(arg0).get("Fecha_Vencimiento"));

                holder.txtfecha_e.setText(fechad.substring(0, 4)+"/"+fechad.substring(4,6)+"/"+fechad.substring(6,8));
                holder.txtfecha_v.setText(fechav.substring(0, 4) + "/" + fechav.substring(4, 6) + "/" + fechav.substring(6, 8));

                Calendar cal = Calendar.getInstance();
                DateTime start = new DateTime(Integer.parseInt(fechav.substring(0, 4)), Integer.parseInt(fechav.substring(4,6)), Integer.parseInt(fechav.substring(6, 8)), 0, 0, 0, 0);
                DateTime end = new DateTime(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), 0, 0, 0, 0);*/

        //holder.txtdias_f.setText(days+" "+getString(R.string.dias_adapter));
        NumberFormat myString = NumberFormat.getInstance(Locale.US);
        myString.setMinimumFractionDigits(2);
        myString.setMaximumFractionDigits(2);

        String format = myString.format(Float.parseFloat(ListaDatos.get(arg0).get("Importe")));
        txt_importe.setText("$" + format);

        String format2 = myString.format(Float.parseFloat(ListaDatos.get(arg0).get("Saldo")));
        txtadeudo_p.setText("$" + format2);

        String format3 = myString.format(Float.parseFloat(ListaDatos.get(arg0).get("adeudo_cubrir")));
        txtadeudo_c.setText("$" + format3);

            /*ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Cobranza.bandera == 0) {
                        Toast toast = Toast.makeText(cobranza, R.string.sinseleccionar_dialog, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                    } else {
                        if (arg0 == PositionSelected){
                            Log.i("position lista3"," = "+PositionSelected);
                            v.setBackgroundColor(Color.parseColor("#89BBEE"));
                        }
                        //v.setSelected(true);
                        //v.setBackgroundColor(Color.parseColor("#89BBEE"));
                        String fechad = new String(ListaDatos.get(arg0).get("Fecha_Documento"));
                        String fechav = new String(ListaDatos.get(arg0).get("Fecha_Vencimiento"));

                            openDialogEditarMonto(ListaDatos.get(arg0).get("Importe"), fechad.substring(0, 4) + "/" + fechad.substring(4, 6) + "/" + fechad.substring(6, 8),
                                    fechav.substring(0, 4) + "/" + fechav.substring(4, 6) + "/" + fechav.substring(6, 8), days);
                    }
                }
            });

            ll.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    if (Cobranza.bandera != 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(cobranza);
                        builder.setMessage(R.string.cubrir_adeudo)
                                .setPositiveButton(R.string.cubrir_adeudo_si, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //v.setSelected(true);
                                        //v.setBackgroundColor(Color.parseColor("#89BBEE"));
                                        txtadeudo_p.setText("$" + "0.00");
                                        txtadeudo_c.setText("$" + ListaDatos.get(arg0).get("Importe"));
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
                    } else {
                        Toast toast = Toast.makeText(cobranza, R.string.sinseleccionar_dialog,Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                        toast.show();
                    }
                    return false;
                }
            });*/
        return view;
    }

    public void openDialogEditarMonto(String monto_total, String fechae, String fechacv, int days, final int position){
        final Dialog dialog = new Dialog(cobranza);
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
        txtdias_f.setText(days + " " + cobranza.getString(R.string.dias_adapter));

        btn_guardar_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_monto_aplicar.getText().toString().equals("")){
                    et_monto_aplicar.setError("Debe agregar un monto a aplicar");
                }else{
                    monto_aplicar = et_monto_aplicar.getText().toString();

                    Cobranza.txt_total_pendiente.setText("$"+UtilsCobranza.textdecimal(monto_aplicar));

                    txtadeudo_p.setText("$" + "0.00");
                    txtadeudo_c.setText("$" + monto_aplicar);

                    dialog.dismiss();
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

    private static View.OnClickListener btnsuma_monto_cobranza = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String strAmountReturn = et_monto_aplicar.getText().toString().trim();
            if( strAmountReturn.length() == 0 ) strAmountReturn = "0";

            if (view == btnDevLitePromptReset0){
                strAmountReturn = "";
            }else if (view == btnDevLitePromptAdd1){
                strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "25")).toString();
            }else if (view == btnDevLitePromptAdd2){
                strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "50")).toString();
            }else if (view == btnDevLitePromptAdd5){
                strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "75")).toString();
            }else if (view == btnDevLitePromptAdd10){
                strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "100")).toString();
            }
            et_monto_aplicar.setText( strAmountReturn );
            et_monto_aplicar.setSelection(strAmountReturn.length(), strAmountReturn.length());
        }
    };

    /*public class HolderFactura{
        public TextView txtnum_factura,txtfecha_v,txt_importe,txtadeudo_p,txtadeudo_c;
        public LinearLayout ll;
    }*/

    private class ItemFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            List<Dictionary<String, String>> list;
            list = CobranzaAdapter.this.ListaDatosfirst;

            int count = list.size();
            final ArrayList<Dictionary<String, String>> nlist = new ArrayList<Dictionary<String, String>>(count);
            Dictionary<String, String> dic;

            for (int i=0; i<count; i++){
                dic = list.get(i);
                if( (dic.get("Documento").toLowerCase().contains(filterString)))
                    nlist.add(dic);
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

                /*Toast toast = Toast.makeText(cobranza, "Sin resultados, intenetelo de nuevo", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 200);
                toast.show();*/

            CobranzaAdapter.this.ListaDatos = (ArrayList<Dictionary<String, String>>) results.values;
            notifyDataSetChanged();
        }
    }


}




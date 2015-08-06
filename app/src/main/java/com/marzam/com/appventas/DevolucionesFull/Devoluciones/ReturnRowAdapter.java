package com.marzam.com.appventas.DevolucionesFull.Devoluciones;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionPendiente.DevolucionPendiente;
import com.marzam.com.appventas.DevolucionesFull.DevolucionesFullConteiner;
import com.marzam.com.appventas.DevolucionesFull.Facturas.Invoice;
import com.marzam.com.appventas.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lcabral on 07/04/2015.
 */
public class ReturnRowAdapter extends ArrayAdapter<DevolucionPendiente> implements Filterable{

    private Context context;
    private int layoutResourceId;
    private ArrayList<DevolucionPendiente> originalData = null;
    private ArrayList<DevolucionPendiente> filteredData = null;

    private LayoutInflater inflater;
    private ItemFilter myFilter = new ItemFilter();

    private boolean flagFilterByDate = false;

    private Date toDate = null;
    private Date fromDate = null;

    public ReturnRowAdapter(Context context, int layoutResourceId, ArrayList<DevolucionPendiente> data){
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.originalData = data;
        this.filteredData = data;
        this.inflater = ((Activity) this.context).getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ReturnPendingHolder holder = null;
        if( row==null ){
            row = inflater.inflate(this.layoutResourceId, parent, false);
            holder = new ReturnPendingHolder();
            holder.txtNumFolio = (TextView) row.findViewById(R.id.idTxtDevFullNumFolio);
            holder.txtStatusIBS = (TextView) row.findViewById(R.id.idTxtDevFullStatusIBS);
            holder.imgSynchronize = (ImageView) row.findViewById( R.id.idImgDevFullSynchronize );

            row.setTag(holder);
        }else{
            holder = (ReturnPendingHolder)row.getTag();
        }

        DevolucionPendiente devolucionPendiente = this.filteredData.get(position);

        String numberInvoiceToReturn = devolucionPendiente.getFolioForThisReturn();
        if( numberInvoiceToReturn==null ){
            numberInvoiceToReturn="Sin número de factura";
            devolucionPendiente.setNumberInvoiceToReturn( numberInvoiceToReturn );
        }
        holder.txtNumFolio.setText( numberInvoiceToReturn );
        holder.txtStatusIBS.setText( devolucionPendiente.getStatusIBS() );

        if( devolucionPendiente.getIdStatus().trim().compareTo("10")!=0 ) {
            holder.imgSynchronize.setVisibility(View.INVISIBLE);
        }

        /*if( devolucionPendiente.isSend() ) {
            //((ViewGroup) holder.imgIconListProduct.getParent()).removeView(holder.imgIconListProduct);
        }/*else{
            final int positionFinal = position;
            final ReturnPendingHolder holderFinal = holder;
        }*/

        if (position % 2 == 0) {
            row.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }else {
            row.setBackgroundColor(Color.parseColor("#F2F2F2"));
        }

        return row;
    }

    @Override
    public int getCount() {
        if( filteredData!=null )
            return filteredData.size();
        else
            return 0;
    }

    @Override
    public DevolucionPendiente getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return this.myFilter;
    }

    /**
     * Método para remover un elemento de la lista
     * @param index Índice del ítem a remover
     */
    public void remove(int index) {
        super.remove(this.filteredData.get(index));
    }

    public void setFlagFilterDate( boolean flag ){
        this.flagFilterByDate = flag;
        if(!flag) setDateFilter(null, null);
    }
    public boolean getFlagFilterDate(){
        return this.flagFilterByDate;
    }
    public void setDateFilter( Date fDate, Date tDate ){
        this.fromDate = fDate;
        this.toDate = tDate;
    }
    /**
     * Clase para construir el objeto de la factura a guardar en la lista
     */
    static class ReturnPendingHolder
    {
        public TextView txtNumFolio;
        public TextView txtStatusIBS;
        public ImageView imgSynchronize;
    }


    /**
     *
     */
    private class ItemFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<DevolucionPendiente> list;
            list = ReturnRowAdapter.this.originalData;

            int count = list.size();
            final ArrayList<DevolucionPendiente> nlist = new ArrayList<DevolucionPendiente>(count);
            DevolucionPendiente devolucionPendiente;

            for (int i=0; i<count; i++){
                devolucionPendiente = list.get(i);
                if( devolucionPendiente.getFolioForThisReturn().toLowerCase().contains(filterString) || devolucionPendiente.getStatusIBS().toLowerCase().contains(filterString) )
                    nlist.add(devolucionPendiente);
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ReturnRowAdapter.this.filteredData = (ArrayList<DevolucionPendiente>) results.values;
            notifyDataSetChanged();
        }
    }
}
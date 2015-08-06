package com.marzam.com.appventas.DevolucionesFull.Facturas;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.marzam.com.appventas.DevolucionesLite.ProductList.Product;
import com.marzam.com.appventas.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lcabral on 07/04/2015.
 */
public class InvoiceRowAdapter extends ArrayAdapter<Invoice> implements Filterable{

    private Context context;
    private int layoutResourceId;
    private ArrayList<Invoice> originalData = null;
    private ArrayList<Invoice> filteredData = null;

    private LayoutInflater inflater;
    private ItemFilter myFilter = new ItemFilter();

    private boolean flagFilterByDate = false;

    private Date toDate = null;
    private Date fromDate = null;

    public InvoiceRowAdapter(Context context, int layoutResourceId, ArrayList<Invoice> data){
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
        InvoiceHolder holder = null;
        if( row==null ){
            row = inflater.inflate(this.layoutResourceId, parent, false);
            holder = new InvoiceHolder();
            holder.txtNumInvoice = (TextView) row.findViewById(R.id.idTxtDevFullNumInvoice);
            holder.txtDataInvoice = (TextView) row.findViewById(R.id.idTxtDevFullDateInvoice);
            holder.txtNumProductInvoice = (TextView) row.findViewById(R.id.idTxtDevFullNumProducts);
            row.setTag(holder);
        }else{
            holder = (InvoiceHolder)row.getTag();
        }

        Invoice invoice = this.filteredData.get(position);
        holder.txtNumInvoice.setText(invoice.getNumInvoice());
        holder.txtDataInvoice.setText( invoice.getDateInvoice() );
        holder.txtNumProductInvoice.setText( invoice.getNumProductInvoice() );

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
    public Invoice getItem(int position) {
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
    static class InvoiceHolder
    {
        public TextView txtNumInvoice;
        public TextView txtDataInvoice;
        public TextView txtNumProductInvoice;
    }


    /**
     *
     */
    private class ItemFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<Invoice> list;
            if( InvoiceRowAdapter.this.flagFilterByDate ){
                list = getInvoiceFilterByDate();
            }
            else {
                list = InvoiceRowAdapter.this.originalData;
            }
            int count = list.size();
            final ArrayList<Invoice> nlist = new ArrayList<Invoice>(count);
            Invoice invoice;

            for (int i=0; i<count; i++){
                invoice = list.get(i);
                if( invoice.getNumInvoice().toLowerCase().contains(filterString) )
                    nlist.add(invoice);
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            InvoiceRowAdapter.this.filteredData = (ArrayList<Invoice>) results.values;
            notifyDataSetChanged();
        }

        private List<Invoice> getInvoiceFilterByDate(){
            List <Invoice>list = InvoiceRowAdapter.this.originalData;

            int count = list.size();
            final ArrayList<Invoice> nlist = new ArrayList<Invoice>(count);
            Invoice invoice;

            for (int i=0; i<count; i++){
                invoice = list.get(i);
                String iDate = invoice.dateInvoice;
                String[] iDateArr = iDate.split("/");

                Calendar calendarInvoice = Calendar.getInstance();
                calendarInvoice.set(Calendar.YEAR, ( new Integer(iDateArr[2].trim()) ).intValue() );
                calendarInvoice.set(Calendar.MONTH, ( new Integer(iDateArr[1].trim()) ).intValue()-1 );
                calendarInvoice.set(Calendar.DAY_OF_MONTH, ( new Integer(iDateArr[0].trim()) ).intValue() );
                Date invoiceDate = calendarInvoice.getTime();

                if( invoiceDate.after(InvoiceRowAdapter.this.fromDate) && invoiceDate.before(InvoiceRowAdapter.this.toDate) ) {
                    nlist.add(invoice);
                }
            }

            return  nlist;
        }
    }
}

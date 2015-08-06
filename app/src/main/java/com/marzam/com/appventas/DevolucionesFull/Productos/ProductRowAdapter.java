package com.marzam.com.appventas.DevolucionesFull.Productos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.marzam.com.appventas.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lcabral on 07/04/2015.
 */
public class ProductRowAdapter extends ArrayAdapter<Product> implements Filterable{

    private Context context;
    private int layoutResourceId;
    private ArrayList<Product> originalData = null;
    private ArrayList<Product> filteredData = null;

    private LayoutInflater inflater;
    private ItemFilter myFilter = new ItemFilter();

    public ProductRowAdapter(Context context, int layoutResourceId, ArrayList<Product> data){
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
            holder.txtNameProduct = (TextView) row.findViewById(R.id.idTxtDevFullNumProduct);
            holder.txtMarzamCode = (TextView) row.findViewById(R.id.idTxtDevFullMarzamCode);
            holder.txtBarCode = (TextView) row.findViewById(R.id.idTxtDevFullBarCode);
            holder.txtNumberProductsToReturn = (TextView) row.findViewById(R.id.idTxtDevFullNumProductsToReturn);

            if( this.layoutResourceId==R.layout.devoluciones_full_producto_by_invoice_row ) {
                holder.txtNumberProducts = (TextView) row.findViewById(R.id.idTxtDevFullNumProducts);
            }

            row.setTag(holder);
        }else{
            holder = (InvoiceHolder)row.getTag();
        }

        Product product = this.filteredData.get(position);
        holder.txtNameProduct.setText( product.getName() );
        holder.txtMarzamCode.setText( product.getMarzamCode() );
        holder.txtBarCode.setText( product.getBarCode() );
        holder.txtNumberProductsToReturn.setText( product.getNumberProductsToReturn() );

        if( this.layoutResourceId==R.layout.devoluciones_full_producto_by_invoice_row ) {
            holder.txtNumberProducts.setText(product.getNumberProducts());
        }


        if ( product.getNumberProductsToReturn().compareTo("0")==0 ) {
            if (position % 2 == 0) {
                row.setBackgroundColor( Color.parseColor("#FFFFFF") );
            }else {
                row.setBackgroundColor( Color.parseColor("#F2F2F2") );
            }
        }else {
            row.setBackgroundColor( Color.parseColor("#FE642E") );
        }
        //notifyDataSetChanged();

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
    public Product getItem(int position) {
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

    /**
     * Clase para construir el objeto de la factura a guardar en la lista
     */
    static class InvoiceHolder
    {
        public TextView txtNameProduct;
        public TextView txtMarzamCode;
        public TextView txtBarCode;
        public TextView txtNumberProducts;
        public TextView txtNumberProductsToReturn;
    }


    /**
     *
     */
    private class ItemFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<Product> list;
            list = ProductRowAdapter.this.originalData;

            int count = list.size();
            final ArrayList<Product> nlist = new ArrayList<Product>(count);
            Product product;

            for (int i=0; i<count; i++){
                product = list.get(i);
                if( (product.getName().toLowerCase().contains(filterString))||(product.getMarzamCode().toLowerCase().contains(filterString))||(product.getBarCode().toLowerCase().contains(filterString)) )
                    nlist.add(product);
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ProductRowAdapter.this.filteredData = (ArrayList<Product>) results.values;
            notifyDataSetChanged();
        }
    }
}

package com.marzam.com.appventas.DevolucionesLite.ProductList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marzam.com.appventas.DevolucionesLite.DevolucionesLite;
import com.marzam.com.appventas.R;

/**
 * Created by lcabral on 07/04/2015.
 */
public class ProductAdapter extends ArrayAdapter<Product>{

    Context context;
    int layoutResourceId;
    Product data[] = null;

    public ProductAdapter( Context context, int layoutResourceId, Product[] data ){
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ProductHolder holder = null;

        if( row == null ){
            LayoutInflater inflater = ( (Activity)this.context ).getLayoutInflater();
            row = inflater.inflate( this.layoutResourceId, parent, false );

            holder = new ProductHolder();
            holder.txtName = ( TextView )row.findViewById( R.id.idTxtName);
            holder.txtMarzamCode = ( TextView )row.findViewById( R.id.idSetMarzamCode);
            holder.txtBarCode = ( TextView )row.findViewById( R.id.barCode);
            holder.imgIcon = ( ImageView )row.findViewById( R.id.idImgIconListProduct );


            row.setTag( holder );
        }else{
            holder = (ProductHolder)row.getTag();
        }

       if (position % 2 == 0) {
            row.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }else {
            row.setBackgroundColor(Color.parseColor("#F2F2F2"));
        }

        Product product = data[position];
        holder.txtName.setText( product.name );
        holder.txtMarzamCode.setText( product.marzamCode );
        holder.txtBarCode.setText( product.barCode );
        holder.imgIcon.setImageResource( product.icon );

        return row;
    }

    /**
     * Clase para construir el objeto del producto a guardar en la lista
     */
    static class ProductHolder
    {
        ImageView imgIcon;
        TextView txtName;
        TextView txtMarzamCode;
        TextView txtBarCode;
    }
}

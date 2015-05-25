package com.marzam.com.appventas.DevolucionesLite.ProductList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.marzam.com.appventas.R;

import java.util.ArrayList;

/**
 * Created by lcabral on 07/04/2015.
 */
public class ProductSumaryAdapter extends ArrayAdapter<Product>{

    Context context;
    int layoutResourceId;
    int layoutResourceSectionId;
    public ArrayList<Product> data = null;

    public ProductSumaryAdapter(Context context, int layoutResourceId, int layoutResourceSectionId, ArrayList<Product> data){
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.layoutResourceSectionId = layoutResourceSectionId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ProductSumaryHolder holder = null;
        Product product = this.data.get(position);

        if( product.isSection ){
            LayoutInflater inflater = ((Activity) this.context).getLayoutInflater();
            row = inflater.inflate(this.layoutResourceSectionId, parent, false);

            row.setOnClickListener(null);
            row.setOnLongClickListener(null);
            row.setLongClickable(false);
            final TextView sectionView = (TextView) row.findViewById( R.id.list_item_section_text );
            sectionView.setText( product.textSection );
            sectionView.setTextColor( Color.parseColor("#FFFFFF") );
        }else if(product.isPackage) {
            LayoutInflater inflater = ((Activity) this.context).getLayoutInflater();
            row = inflater.inflate(this.layoutResourceId, parent, false);
            holder = new ProductSumaryHolder();
            holder.txtName = (TextView) row.findViewById(R.id.idTxtName);
            holder.txtAmountProductReturn = (TextView) row.findViewById(R.id.idSetAmountProductReturn);
            holder.txtBarCode = (TextView) row.findViewById(R.id.idSetCodeProduct);
            holder.txtTypeDocument = (TextView) row.findViewById(R.id.idSetTypeDocument);
            holder.txtReasonReturn = (TextView) row.findViewById(R.id.idSetReasonReturn);

            holder.txtSetCodeProduct = (TextView) row.findViewById(R.id.idTxtCodeProduct);
            holder.txtSetCodeProduct.setText("Documento de devolución");
            holder.txtSetReasonDocument = (TextView) row.findViewById(R.id.idTxtReasonDocument);
            holder.txtSetReasonDocument.setText("Folio de devolución");
            holder.txtSetReasonReturn = (TextView) row.findViewById(R.id.idTxtReasonReturn);
            holder.txtSetReasonReturn.setText("Numero de devolución");
            row.setTag(holder);
        } else{
            LayoutInflater inflater = ((Activity) this.context).getLayoutInflater();
            row = inflater.inflate(this.layoutResourceId, parent, false);
            holder = new ProductSumaryHolder();
            holder.txtName = (TextView) row.findViewById(R.id.idTxtName);
            holder.txtAmountProductReturn = (TextView) row.findViewById(R.id.idSetAmountProductReturn);
            holder.txtBarCode = (TextView) row.findViewById(R.id.idSetCodeProduct);
            holder.txtTypeDocument = (TextView) row.findViewById(R.id.idSetTypeDocument);
            holder.txtReasonReturn = (TextView) row.findViewById(R.id.idSetReasonReturn);
            row.setTag(holder);
        }

        if( !product.isSection ) {

            if (position % 2 == 0) {
                row.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }else {
                row.setBackgroundColor(Color.parseColor("#F2F2F2"));
            }

            try {
                if (product.isPackage) {
                    holder.txtName.setText("Bulto");
                    holder.txtAmountProductReturn.setText(product.amountPackages);
                    holder.txtBarCode.setText(product.typeDocument);
                    holder.txtTypeDocument.setText(product.folioDocument);
                    holder.txtReasonReturn.setText(product.numberReturn);

                } else {
                    holder.txtName.setText(product.name);
                    holder.txtAmountProductReturn.setText(product.amountProductReturn);
                    holder.txtBarCode.setText(product.barCode);
                    holder.txtTypeDocument.setText(product.typeDocument);
                    holder.txtReasonReturn.setText(product.reasonReturn);
                }
            }catch(Exception e){}
        }
        return row;
    }

    /**
     * Clase para construir el objeto del producto a guardar en la lista
     */
    static class ProductSumaryHolder
    {
        TextView txtName;
        TextView txtMarzamCode;
        TextView txtReasonReturn;
        TextView txtTypeDocument;
        TextView txtBarCode;
        TextView txtAmountProductReturn;

        TextView txtSetCodeProduct;
        TextView txtSetReasonDocument;
        TextView txtSetReasonReturn;
    }

    /**
     * Método para remover un elemento de la lista
     * @param index Índice del ítem a remover
     */
    public void remove(int index) {
        super.remove(this.data.get(index));
    }
}

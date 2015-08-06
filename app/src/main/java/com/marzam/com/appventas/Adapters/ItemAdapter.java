package com.marzam.com.appventas.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.marzam.com.appventas.EstatusPedidos.catalogo_edit;
import com.marzam.com.appventas.EstatusPedidos.tab_pedidos;
import com.marzam.com.appventas.KPI.KPI_General;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;
import com.marzam.com.appventas.Sincronizacion.Crear_precioFinal;


import java.util.List;

public class ItemAdapter extends ArrayAdapter {

    List data;
    Context context;
    int layoutResID;
    SwipeListView swipe;
    ProgressDialog progress;

    public ItemAdapter(Context context, int layoutResourceId,List data,SwipeListView swipe) {
        super(context, layoutResourceId, data);

        this.swipe=swipe;
        this.data=data;
        this.context=context;
        this.layoutResID=layoutResourceId;

        // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        NewsHolder holder = null;
        View row = convertView;


        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResID, parent, false);

            holder = new NewsHolder();

            holder.txtEncabezado = (TextView)row.findViewById(R.id.textView5);
            holder.txtFecha=(TextView)row.findViewById(R.id.textView6);
            holder.txtEstatus=(TextView)row.findViewById(R.id.textView7);
            holder.txtTotal=(TextView)row.findViewById(R.id.textView8);
            holder.ImageButonDelete=(ImageButton)row.findViewById(R.id.imageButton_swip_delete);
            holder.ImageButonEdit=(ImageButton)row.findViewById(R.id.imageButton_swip_edit);
            row.setTag(holder);

            if (position % 2 == 0) {
                row.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }else {
                row.setBackgroundColor(Color.parseColor("#F2F2F2"));
            }
        }
        else
        {
            holder = (NewsHolder)row.getTag();
        }

        final ItemRow itemdata= (ItemRow) data.get(position);
        holder.txtEncabezado.setText(itemdata.getPedido());
        holder.txtFecha.setText(itemdata.getFecha());
        holder.txtEstatus.setText(itemdata.getEstatus());
        holder.txtTotal.setText(itemdata.getTotal());

        if(itemdata.getEstatus().equals("No enviado")){
            holder.ImageButonEdit.setImageResource(R.drawable.icon_edit_swip);
        }else {
            holder.ImageButonEdit.setImageResource(R.drawable.icon_transparent);
        }



        holder.ImageButonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(itemdata.getEstatus().equals("No enviado")){
                    dialogDelete(itemdata.getPedido(), position);
                }else {
                    Toast.makeText(context,"El pedido no se puede eliminar por que ya ha sido transmitido",Toast.LENGTH_LONG).show();

                }
                swipe.closeAnimate(position);
            }
        });

        holder.ImageButonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemdata.getEstatus().equals("No enviado")){
                    if(!VerificarSesionActiva()) {
                        new TaskPedido().execute(itemdata.getPedido());
                        swipe.closeAnimate(position);
                    }else {
                        ShowSesionActiva();
                    }
                }
            }
        });

        return row;

    }

    public void ShowSesionActiva(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage("Visita activa. Cierre primero la sesion para poder continuar con los demas clientes");
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(context, KPI_General.class);
                context.startActivity(intent
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });
        alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialogAct=alert.create();
        alertDialogAct.show();
    }

    static class NewsHolder{

        TextView txtEncabezado;
        TextView txtFecha;
        TextView txtEstatus;
        TextView txtTotal;
        ImageButton ImageButonDelete;
        ImageButton ImageButonEdit;
    }

    public void dialogDelete(final String id, final int position){

        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Eliminar pedido");
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setMessage("¿Desea eliminar el pedido?");
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               if(EliminarPedido(id)){

                   Toast.makeText(context,"Pedido eliminado correctamente",Toast.LENGTH_SHORT).show();
                   data.remove(position);
                   notifyDataSetChanged();

               }else{
                   Toast.makeText(context,"No se pudo eliminar el pedido",Toast.LENGTH_SHORT).show();
               }
            }
        });
        alert.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();


    }

    public boolean EliminarPedido(String id){

        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        int i=db.delete("detalle_pedido", "id_pedido=?", new String[]{id});
        int j=0;
        if(i>0)
         j=db.delete("encabezado_pedido","id_pedido=?",new String[]{id});

        if(j>0)
            return true;
        else
            return false;
    }

    public boolean VerificarSesionActiva(){

         CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        Cursor rs= db.rawQuery("select id_cliente from sesion_cliente where Sesion=1",null);

        if(rs.moveToFirst()){
            return  true;//ya se encunetra una sesion activa
        }

        return false;
    }

    public class TaskPedido extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){

            progress=ProgressDialog.show(context, "Generando precios netos", "Cargando", true, false);
            progress.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            CSQLite lite=new CSQLite(context);
            SQLiteDatabase db=lite.getWritableDatabase();

            Cursor rs=db.rawQuery("select id_cliente from encabezado_pedido where id_pedido=?",new String[]{strings[0]});
            String cliente="";

            if(rs.moveToFirst())
                cliente=rs.getString(0);

            ContentValues values=new ContentValues();
            values.put("Sesion","1");
            db.update("sesion_cliente",values,"id_cliente=?",new String[]{cliente});



            Crear_precioFinal precioFinal=new Crear_precioFinal();
            precioFinal.Ejecutar(context);

            if(!db.isOpen())
                db=lite.getWritableDatabase();

            Cursor cursor=db.rawQuery("select codigo,piezas_pedidas from detalle_pedido where id_pedido=?",new String[]{strings[0]});
            String[][] productos=new String[cursor.getCount()][2];
            int contador=0;

            while (cursor.moveToNext()){
                productos[contador][0]=cursor.getString(0);
                productos[contador][1]=cursor.getString(1);
                contador++;
            }

            for(int i=0;i<productos.length;i++){

                values=new ContentValues();
                values.put("isCheck",1);
                values.put("Cantidad",productos[i][1]);
                db.update("productos",values,"codigo=?",new String[]{productos[i][0]});
            }


            if(db!=null)
            db.close();
            if(lite!=null)
            lite.close();

            return strings[0];
        }

        @Override
        protected void onPostExecute(String pedido){

            if(progress.isShowing()){
                progress.dismiss();
                Intent intent=new Intent(context,tab_pedidos.class);
                intent.putExtra("id_pedido",pedido);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }

        }
    }

}
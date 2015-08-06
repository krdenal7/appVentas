package com.marzam.com.appventas.EstatusPedidos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.marzam.com.appventas.Email.Mail;
import com.marzam.com.appventas.SQLite.CSQLite;

/**
 * Created by imartinez on 30/07/2015.
 */
public class Save {

    private CSQLite lite;
    private Context context;
    private double iva=0.00;
    private double ieps=0.00;
    private Mail m;
    private String from;
    private String subject;
    private String body;
    private int total_piezas=0;
    private double subTotal=0.00;
    private double importe_total=0.00;

    public Save(Context context,String id_pedido){
      this.context=context;
      Obtener_Valores();
      UpdateProductos(id_pedido);
      UpdateImporteTotal(id_pedido);
      DeleteProductos(id_pedido);
    }

    private boolean UpdateProductos(String id){

        String codigos[]=Codigo(id);

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select codigo,Cantidad,precio,clasificacion_fiscal,iva,ieps,precio_final from productos where isCheck=1",null);

        while (rs.moveToNext()){

             String codigo=rs.getString(0);
             boolean bandera=false;

        for (int i=0;i<codigos.length;i++){

            if(codigo.equals(codigos[i])) {
                bandera = true;
            }

        }

            ContentValues values=new ContentValues();

        if(bandera){
            values.put("piezas_pedidas",rs.getString(1));
            db.update("detalle_pedido",values,"id_pedido=? and codigo=?",new String[]{id,codigo});

        }else {
            values.put("id_pedido",id);
            values.put("codigo",codigo);
            values.put("piezas_pedidas",rs.getString(1));
            values.put("piezas_surtidas",0);
            values.put("precio_farmacia",rs.getString(2));
            values.put("clasfificacion_fiscal",rs.getString(3));
            values.put("oferta",Obtener_oferta(rs.getString(0)));
            values.put("desc_comercial",Obtener_descuentoComercial());
            values.put("precio_neto",rs.getString(6));
            values.put("iva",rs.getString(4));
            values.put("ieps",rs.getString(5));
            db.insert("detalle_pedido",null,values);

        }

        }

        return false;
    }

    private boolean UpdateImporteTotal(String id_pedido){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("impote_total",importe_total);
        values.put("total_piezas",total_piezas);

        db.update("encabezado_pedido",values,"id_pedido=?",new String[]{id_pedido});

        return false;
    }

    private boolean DeleteProductos(String id_pedido) {

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

        db.execSQL("delete from detalle_pedido where codigo in (select codigo from productos where isCheck=0) and id_pedido=?",
                new String[]{id_pedido});

    return false;
    }

    private String Obtener_oferta(String codigo){
        String oferta="0";

        try {
            SQLiteDatabase db = lite.getReadableDatabase();
            Cursor rs = db.rawQuery("select descuento from ofertas where codigo='" + codigo + "'", null);


            if (rs.moveToFirst())
                oferta = rs.getString(0);
        }catch (Exception e) {
            from="envio_pedido";
            subject="Obtener_oferta";
            body="Error: "+e.toString();
            new sendEmail().execute("");
        }

        return oferta;
    }

    private String[] Codigo(String id_pedido){

        String[] codigos=null;

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getReadableDatabase();
        Cursor rs=db.rawQuery("select codigo from detalle_pedido where id_pedido=?",new String[]{id_pedido});
        codigos=new String[rs.getCount()];
        int cont=0;
        while (rs.moveToNext()){
            codigos[cont]=rs.getString(0);
            cont++;
        }

        db.close();
        lite.close();

        return codigos;
    }

    private void   Obtener_Valores(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        iva=0.00;
        ieps=0.00;
        String query="";

        try {

            query="select precio_oferta,Cantidad,ieps,iva from productos where isCheck=1";
            Cursor rs = db.rawQuery(query, null);

            while (rs.moveToNext()) {

                Double precio = Double.parseDouble(rs.getString(0));
                int cantidad = Integer.parseInt(rs.getString(1));
                Double ieps1 = Double.parseDouble(rs.getString(2));
                Double iva1 = Double.parseDouble(rs.getString(3));

                Double iep = (precio * ieps1 / 100) * cantidad;
                Double cant1 = (precio * ieps1 / 100);
                Double cant = ((precio) + cant1);
                Double cant2 = (cant * iva1 / 100) * cantidad;

                ieps += iep;
                iva += cant2;

                total_piezas += cantidad;
                subTotal += (precio * cantidad);

            }
        }catch (Exception e){
            from="Save";
            subject="Obtener_valores-Save";
            body="Agente:"+ObtenerAgenteActivo()+"\nError: "+e.toString();
            new sendEmail().execute("");
        }


        importe_total=subTotal+ieps+iva;

    }

    private String ObtenerAgenteActivo(){

        String clave = "";
        String query="";
        try {

            for(int i=0;i<4;i++) {
                lite = new CSQLite(context);
                SQLiteDatabase db = lite.getWritableDatabase();

                query = "select clave_agente from agentes where Sesion=1";
                Cursor rs = db.rawQuery(query, null);
                if (rs.moveToFirst()) {

                    clave = rs.getString(0);
                }
                if(clave.equals("")) {
                    if(lite!=null)lite.close();
                    if(db!=null)db.close();
                    lite=new CSQLite(context);
                    db=lite.getWritableDatabase();
                    continue;
                }
                else{
                    break;
                }
            }
        }catch (Exception e){
            from="envio_pedido";
            subject="ObtenerAgenteActivo";
            body="Query: "+query+"\nError: "+e.toString();
            new sendEmail().execute("");
        }

        if(clave.equals("")){
            from="envio_pedido";
            subject="ObtenerAgenteActivo";
            body="No se encontro el agente activo";
            new sendEmail().execute("");
        }

        return clave;
    }

    private String Obtener_descuentoComercial(){
        String desc = "0";
        try {


            SQLiteDatabase db = lite.getReadableDatabase();
            Cursor rs = db.rawQuery("select descuento_comercial from clientes where id_cliente=(select id_cliente from sesion_cliente where Sesion=1)", null);



            if (rs.moveToFirst()) {
                desc = rs.getString(0);
            }
        }catch (Exception e){
            from="envio_pedido";
            subject="Obtener_descuentocomercial";
            body="Error: "+e.toString();
            new sendEmail().execute("");
        }
        return desc;

    }

    private class sendEmail extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {


            m = new Mail("rodrigo.cabrera.it129@gmail.com", "juanito1.");
            String[] toArr = {"imartinez@marzam.com.mx","cardenal.07@hotmail.com"};
            m.setTo(toArr);
            m.setFrom(from);
            m.setSubject(subject);
            m.setBody(body);

            try {

                m.send();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}

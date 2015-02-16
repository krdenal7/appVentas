package com.marzam.com.appventas.Sincronizacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.marzam.com.appventas.SQLite.CSQLite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by SAMSUMG on 08/12/2014.
 */
public class Crear_precioFinal {

    Context context;
    CSQLite lite;
    String [] clasificacion_Desc_Cliente;
    String [] clasificacion_Desc_menor;
    String [] clasificacion_No_aplica;
    HashMap<String,String> Hasproducto;
    static ArrayList<HashMap<String,?>> data=null;




    public void Ejecutar(Context context){
        this.context=context;
        lite=new CSQLite(context);

        Extraer_Casificacion();

        /*Pasos*/

        /*6-.*/Llenar_precioFinal_precio();
        /*1-.*/Obtener_Prodcutos_Descliente();
        /*2-.*/Generar_PrecioFinal_Descliente();
        /*3-.*/data.clear();
        /*4-.*/Obtener_Productos_DescMenor();
        /*5-.*/Generar_PrecioFinal_DescMenor();
        /*6-.*/Obtener_Productos_SoloOferta();
        /*7-.*/Generar_PrecioFinal_SoloOferta();


    }

   public void Llenar_precioFinal_precio(){

     SQLiteDatabase db=lite.getWritableDatabase();
       ContentValues values=new ContentValues();
       values.put("isCheck", 0);
       values.put("Cantidad",0);
       values.put("precio_final","");
       db.update("productos", values, null, null);

    }//Se hace una copia de la columna precio a la columna precio_final de los productos a los cuales no se aplica ningun descuento

    public void Extraer_Casificacion(){

       SQLiteDatabase db=lite.getWritableDatabase();
        try {
            db.execSQL("ALTER TABLE productos ADD COLUMN precio_oferta varchar(50) default '0'");
        }catch (Exception e){
            e.printStackTrace();
        }
       db.execSQL("update clasificacion_fiscal set aplicacion=1");
       Cursor rs=db.rawQuery("select * from clasificacion_fiscal where aplicacion=1",null);

       String desCliente="";
       String desmenor="";
       String noaplica="";

       while (rs.moveToNext()){
           if(rs.getInt(0)==7)
               desCliente=rs.getString(1);
           if(rs.getInt(0)==8)
               desmenor=rs.getString(1);
           if(rs.getInt(0)==9)
               noaplica=rs.getString(1);
       }

       clasificacion_Desc_Cliente=desCliente.split(",");
       clasificacion_Desc_menor=desmenor.split(",");
       clasificacion_No_aplica=noaplica.split(",");


   }//Llena los arreglos segun con el tipo de clasificacion fiscal

   public Double Obtener_DescuentoDelCliente(String id_cliente){

       SQLiteDatabase db=lite.getWritableDatabase();
       Cursor rs=db.rawQuery("select descuento_comercial from clientes where id_cliente='"+id_cliente+"'",null);

       Double descuento=0.00;

      if(rs.moveToFirst()){
          descuento=Double.parseDouble(rs.getString(0));
      }

       db.close();

       return descuento;
   }//Obtiene el descuento comercial de cliente

   public void Obtener_Prodcutos_Descliente(){

       lite=new CSQLite(context);
       SQLiteDatabase db=lite.getWritableDatabase();

       Cursor rs=null;

       String query="select codigo,precio,iva,ieps  from productos where clasificacion_fiscal in"+where(clasificacion_Desc_Cliente);

       rs=db.rawQuery(query,null);
       data=new ArrayList<HashMap<String, ?>>();
       Hasproducto=new HashMap<String, String>();

       while (rs.moveToNext()){

           Hasproducto.put("A",rs.getString(0));
           Hasproducto.put("B",rs.getString(1));
           Hasproducto.put("C",rs.getString(2));
           Hasproducto.put("D",rs.getString(3));
           data.add(Hasproducto);
           Hasproducto=new HashMap<String, String>();
       }

      db.close();

    }//Llena una lista con los productos a los cuales se les aplica el descuento directamente
   public void Obtener_Productos_DescMenor(){

       SQLiteDatabase db=lite.getWritableDatabase();

       Cursor rs=null;

       String query="select codigo,precio,iva,ieps,descuento_producto  from productos where clasificacion_fiscal in "+where(clasificacion_Desc_menor);

       rs=db.rawQuery(query,null);
       data=new ArrayList<HashMap<String, ?>>();
       Hasproducto=new HashMap<String, String>();

       while (rs.moveToNext()){

           Hasproducto.put("A",rs.getString(0));
           Hasproducto.put("B",rs.getString(1));
           Hasproducto.put("C",rs.getString(2));
           Hasproducto.put("D",rs.getString(3));
           Hasproducto.put("E",rs.getString(4));
           data.add(Hasproducto);
           Hasproducto=new HashMap<String, String>();
       }
       db.close();
   }//Llena una lista con los productos a los cuales se les aplica el descuento menor
   public void Obtener_Productos_SoloOferta(){

       SQLiteDatabase db=lite.getWritableDatabase();

       Cursor rs=null;

       String query="select codigo,precio,iva,ieps  from productos where precio_final=''";

       rs=db.rawQuery(query,null);
       data=new ArrayList<HashMap<String, ?>>();
       Hasproducto=new HashMap<String, String>();

       while (rs.moveToNext()){

           Hasproducto.put("A",rs.getString(0));
           Hasproducto.put("B",rs.getString(1));
           Hasproducto.put("C",rs.getString(2));
           Hasproducto.put("D",rs.getString(3));
           data.add(Hasproducto);
           Hasproducto=new HashMap<String, String>();
       }
       db.close();

   }
   public String Obtener_idCliente(){

        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select id_cliente from sesion_cliente where Sesion=1",null);

        String cliente="";

        if(rs.moveToFirst()){
            cliente=rs.getString(0);
        }

        db.close();

        return cliente;
    }

   public void Generar_PrecioFinal_Descliente(){

       String codigo="";
       Double precioFarmacia=0.00;
       Double iva=0.00;
       Double ieps=0.00;
       Double oferta=0.00;
       Double desc_comercial=Obtener_DescuentoDelCliente(Obtener_idCliente());

       SQLiteDatabase db=lite.getWritableDatabase();




           for (int i = 0; i < data.size(); i++) {
               try {

               codigo = data.get(i).get("A").toString();
               precioFarmacia = Double.parseDouble(data.get(i).get("B").toString());
               iva=Double.parseDouble(data.get(i).get("C").toString());
               ieps=Double.parseDouble(data.get(i).get("D").toString());


           /*se obtiene la oferta*/
            Cursor   rs = db.rawQuery("select descuento from ofertas where codigo='" + codigo + "'", null);

               if (rs.moveToFirst())
                   oferta = Double.parseDouble(rs.getString(0));

                   rs.close();

               Double precio1 = ((precioFarmacia - (precioFarmacia * oferta / 100)));
               Double precio2 = (precio1 - (precio1 * desc_comercial / 100));
               Double total = precio2;

               db.execSQL("update productos set precio_oferta='"+String.format(Locale.US, "%.2f", total)+"' where codigo='"+codigo+"'");

               //Calculo de impuestos;

               if(ieps!=0){
               total=(total+((total*ieps)/100));
               }
               if(iva!=0){
                   total=(total+((total*iva)/100));
               }


               String Stotal = String.format(Locale.US,"%.2f", total);
               db.execSQL("update productos set precio_final='" + Stotal + "' where codigo='" + codigo + "'");


               precioFarmacia = 0.00;
               oferta = 0.00;
               iva=0.00;
               ieps=0.00;
               }catch (Exception e){

                   String err=e.toString();
                   Log.d("Error al crear PrecioFinal;", err);
                   continue;

               }
           }



   }//Clasificación B,Ba
   public void Generar_PrecioFinal_DescMenor(){

       String codigo="";
       String Fecha=getDate();
       Double iva=0.00;
       Double ieps=0.00;
       Double desc_producto=0.00;
       Double precioFarmacia=0.00;
       Double oferta=0.00;
       Double desc_comercial=Obtener_DescuentoDelCliente(Obtener_idCliente());

       SQLiteDatabase db=lite.getWritableDatabase();



       for (int i = 0; i < data.size(); i++) {
           try {

               codigo = data.get(i).get("A").toString();
               precioFarmacia = Double.parseDouble(data.get(i).get("B").toString());
               iva = Double.parseDouble(data.get(i).get("C").toString());
               ieps = Double.parseDouble(data.get(i).get("D").toString());
               desc_producto=Double.parseDouble(data.get(i).get("E").toString());



           /*se obtiene la oferta*/
            Cursor   rs = db.rawQuery("select descuento from ofertas where codigo='" + codigo + "'", null);

               if (rs.moveToFirst())
                   oferta = Double.parseDouble(rs.getString(0));

               rs.close();

               Double desc_aplica=0.00;

               if(desc_producto==null)
               {
                   desc_aplica=desc_comercial;
               }else {
                   if(desc_comercial<desc_producto)
                       desc_aplica=desc_comercial;
                   else
                       desc_aplica=desc_producto;
               }


               Double precio1 = ((precioFarmacia - (precioFarmacia * desc_aplica / 100)));
               Double precio2 = (precio1 - ((precio1*oferta)/100));
               Double total = precio2;

               db.execSQL("update productos set precio_oferta='"+String.format(Locale.US, "%.2f", total)+"' where codigo='"+codigo+"'");

               if(ieps!=0){
                   total=(total+(total*ieps/100));
               }
               if(iva!=0){
                   total=(total+(total*iva/100));
               }


               String Stotal = String.format(Locale.US, "%.2f", total);
               db.execSQL("update productos set precio_final='" + Stotal + "' where codigo='" + codigo + "'");


               iva = 0.00;
               ieps = 0.00;
               precioFarmacia = 0.00;
               oferta = 0.00;
               desc_producto=0.00;
               desc_aplica=0.00;

           }catch (Exception e){

               String err=e.toString();
               Log.d("Error al crear PrecioFinal;", err);
               continue;

           }
       }


   }//Clasificacion H,HA //Se compara el descuento del producto con el desc del cliente y se hace la apoeración, al resultado se le aplica la oferta.
   public void Generar_PrecioFinal_SoloOferta(){

        String codigo="";
        Double precio;
        Double total;
        Double iva=0.00;
        Double ieps=0.00;
        Double oferta=0.00;
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();


     for(int i=0;i<data.size();i++){

         codigo=data.get(i).get("A").toString();
         precio=Double.parseDouble(data.get(i).get("B").toString());
         iva=Double.parseDouble(data.get(i).get("C").toString());
         ieps=Double.parseDouble(data.get(i).get("D").toString());


          Cursor rs = db.rawQuery("select descuento from ofertas where codigo='" + codigo + "'", null);

try {
    if (rs.moveToFirst()) {

        oferta = Double.parseDouble(rs.getString(0));

    }
}finally {
    if(rs!=null){
        rs.close();
    }else {
        Toast.makeText(context,"Cursor vacio",Toast.LENGTH_LONG).show();
    }
}

         Double Total1=(precio*oferta)/100;
         total=precio-Total1;

         db.execSQL("update productos set precio_oferta='"+String.format(Locale.US, "%.2f", total)+"' where codigo='"+codigo+"'");

         if(ieps!=0){
            total=(total+(total*ieps/100));
         }
         if(iva!=0){
             total=(total+(total*iva/100));
         }

         String stotal=String.format(Locale.US, "%.2f", total);


         ContentValues values=new ContentValues();
         values.put("precio_final",stotal);

         try {
             db.update("productos",values,"codigo='"+codigo+"'",null);
         }catch (Exception e){
             lite=new CSQLite(context);
             db=lite.getWritableDatabase();
             db.update("productos",values,"codigo='"+codigo+"'",null);
             Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
         }


         precio=0.00;
         total=0.00;
         oferta=0.00;
         ieps=0.00;
         iva=0.00;

     }
    }//Clasificación N,Na,F,O,....,etc.


   public String where(String[] dat){

       StringBuilder builder=new StringBuilder();
       builder.append("('");

      for(int i=0;i<dat.length;i++)
          builder.append(dat[i]+"','");

       String where="";

       for(int i=0;i<builder.length()-2;i++)
           where+=builder.toString().charAt(i);

       return  where+")";
   }
   private String getDate(){

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
        String formatteDate=df.format(dt.getTime());

        return formatteDate;
    }

}

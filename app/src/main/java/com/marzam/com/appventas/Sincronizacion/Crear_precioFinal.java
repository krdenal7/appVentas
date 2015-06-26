package com.marzam.com.appventas.Sincronizacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.marzam.com.appventas.Email.Mail;
import com.marzam.com.appventas.SQLite.CSQLite;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by SAMSUMG on 08/12/2014.
 */
public class Crear_precioFinal {

    Context context;

    CSQLite lite1;
    CSQLite lite2;
    CSQLite lite3;
    String [] clasificacion_Desc_Cliente;
    String [] clasificacion_Desc_menor;
    String [] clasificacion_No_aplica;
    HashMap<String,String> Hasproducto1;
    HashMap<String,String> Hasproducto2;
    HashMap<String,String> Hasproducto3;

    String[][] arrayOf1;
    String[][] arrayOf2;
    String[][] arrayOf3;

    String[][] arrayFin1;
    String[][] arrayFin2;
    String[][] arrayFin3;

    static ArrayList<HashMap<String,?>> data1=null;
    static ArrayList<HashMap<String,?>> data2=null;
    static ArrayList<HashMap<String,?>> data3=null;
    Mail m;
    String from="CrearPrecioFinal";
    String subject;
    String body;
    int tamInsert=500;

    boolean bandera1=false;
    boolean bandera2=false;
    boolean bandera3=false;

    boolean isBandera1=false;
    boolean isBandera2=false;
    boolean isBandera3=false;

    Thread UpdateH1=null;
    Thread UpdateH2=null;
    Thread UpdateH3=null;

    Thread th1;
    Thread th2;
    Thread th3;
    Thread thPrincipal;

    static  int CPUs=1;

    public boolean Ejecutar(Context context){
        this.context=context;


        Extraer_Casificacion();
        CPUs=getNumCores();
        /*Pasos*/

 /*6-.*/Llenar_precioFinal_precio();

        if(CPUs<=2){
            ProcesoSinHilos();
        }
        else {
            CPUs=CPUs-1;
            ProcesoConHilos();
        }



        return true;
    }

    public boolean ProcesoConHilos(){
        final boolean[] resp = {false};

        final ThreadGroup group=new ThreadGroup("Hilos");


        th1 = new Thread(group,new Runnable() {
            @Override
            public void run() {
         /*1-.*/Obtener_Prodcutos_Descliente();
         /*2-.*/Generar_PrecioFinal_Descliente();
                Crear_precioFinal.this.bandera1 = true;
                Log.e("Dentro del hilo 1","Termino");
            }
        });


        th2 = new Thread(group,new Runnable() {
            @Override
            public void run() {
         /*3-.*///data.clear();
        /*4-.*/Obtener_Productos_DescMenor();
        /*5-.*/Generar_PrecioFinal_DescMenor();
                Crear_precioFinal.this.bandera2 = true;
                Log.e("Dentro del hilo 2","Termino");

            }
        });


        th3 = new Thread(group,new Runnable() {
            @Override
            public void run() {
        /*6-.*/Obtener_Productos_SoloOferta();
        /*7-.*/Generar_PrecioFinal_SoloOferta();
                Crear_precioFinal.this.bandera3 = true;
                Log.e("Dentro del hilo 3","Termino");

            }
        });



        UpdateH1=new Thread(group,new Runnable() {
            @Override
            public void run() {

                UpdateOfertas1();
                InsertPrecioFinal1();
                Crear_precioFinal.this.isBandera1=true;
                Log.e("Dentro del hilo 1.1","Termino");

            }
        });

        UpdateH2=new Thread(group,new Runnable() {
            @Override
            public void run() {

                UpdateOfertas2();
                InsertPrecioFinal2();
                Crear_precioFinal.this.isBandera2=true;
                Log.e("Dentro del hilo 2.2","Termino");

            }
        });

        UpdateH3=new Thread(group,new Runnable() {
            @Override
            public void run() {

                UpdateOfertas3();
                InsertPrecioFinal3();
                Crear_precioFinal.this.isBandera3=true;
                Log.e("Dentro del hilo 3.3","Termino");

            }
        });

        int hilos_res=CPUs-group.activeCount();

        thPrincipal = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!Crear_precioFinal.this.isBandera1
                        || !Crear_precioFinal.this.isBandera2
                        || !Crear_precioFinal.this.isBandera3) {

                      //Validacion de hilos dependiendo los nucleos del telefono

                        //hilos_res Verifica cuantos hilos hay disponibles
                        int hilos_res=CPUs-group.activeCount();

                                if(!th1.isAlive()&&!th2.isAlive()&&!th3.isAlive()) {
                                    if (Crear_precioFinal.this.bandera1 == false &&
                                            Crear_precioFinal.this.bandera2 == false &&
                                            Crear_precioFinal.this.bandera3 == false) {

                                        if(hilos_res>=1){
                                            th1.start();
                                            hilos_res=hilos_res-1;
                                            Log.e("GroupHilo 1","="+hilos_res);
                                        }
                                        if(hilos_res>=1){
                                            th2.start();
                                            hilos_res=hilos_res-1;
                                            Log.e("GroupHilo 2","="+hilos_res);
                                        }
                                        if(hilos_res>=1){
                                            th3.start();
                                            hilos_res=hilos_res-1;
                                            Log.e("GroupHilo 3","="+hilos_res);
                                        }

                                    }
                                }

                    if (!th1.isAlive()) {
                        if (!UpdateH1.isAlive() && !UpdateH2.isAlive() &&!UpdateH3.isAlive()&&
                                Crear_precioFinal.this.isBandera1 == false) {

                            if(hilos_res>=1){
                            UpdateH1.start();
                            hilos_res=hilos_res-1;
                            Log.e("HiloPrincipal","Inicia el El hilo 1.1 "+hilos_res);
                            }

                        }//Hay algun hilo ejecutandose
                    }//Se termino el hilo 1


                    if (!th2.isAlive()) {
                        if (!UpdateH1.isAlive() && !UpdateH2.isAlive() &&!UpdateH3.isAlive()&&
                                Crear_precioFinal.this.isBandera2 == false) {

                            if(hilos_res>=1) {
                                UpdateH2.start();
                                hilos_res=hilos_res-1;
                                Log.e("HiloPrincipal", "Inicia el El hilo 2.2 "+hilos_res);
                            }
                        }
                    }


                    if (!th3.isAlive()) {
                        if (!UpdateH1.isAlive() && !UpdateH2.isAlive() &&!UpdateH3.isAlive()&&
                                Crear_precioFinal.this.isBandera3 == false) {

                            if(hilos_res>=1) {
                                UpdateH3.start();
                                hilos_res=hilos_res-1;
                                Log.e("HiloPrincipal", "Inicia el El hilo 3.3 "+hilos_res);
                            }

                        }
                    }
                }//While principal

                resp[0] =true;
                String a="";
            }
        });
        thPrincipal.start();


        while (!resp[0]) {
            //Este while se ejecuta hasta que todos los hilos terminen
        }

        return true;
    }

    public boolean ProcesoSinHilos(){

         /*1-.*/Obtener_Prodcutos_Descliente();
         /*2-.*/Generar_PrecioFinal_Descliente();
        /*4-.*/Obtener_Productos_DescMenor();
        /*5-.*/Generar_PrecioFinal_DescMenor();
        /*6-.*/Obtener_Productos_SoloOferta();
        /*7-.*/Generar_PrecioFinal_SoloOferta();

        //Insercciones

        UpdateOfertas1();
        InsertPrecioFinal1();

        UpdateOfertas2();
        InsertPrecioFinal2();

        UpdateOfertas3();
        InsertPrecioFinal3();


        return true;
    }

    private int getNumCores() {

        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {

                if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            // Obtiene del directorio la infromación de los Nucleos que tiene el equipo
            File dir = new File("/sys/devices/system/cpu/");
            //Filtra la lista de los archivos que se encuentran en la carpeta
            File[] files = dir.listFiles(new CpuFilter());
            //retorna el número de procesadores
            return files.length;
        } catch(Exception e) {
            //Si entra aqui es por que solo tiene un nucleo
            return 1;
        }
    }

    public void Llenar_precioFinal_precio(){

     CSQLite lite=new CSQLite(context);
     SQLiteDatabase db=lite.getWritableDatabase();
       ContentValues values=new ContentValues();
       values.put("isCheck", 0);
       values.put("Cantidad", 0);
       values.put("precio_final","");
       db.update("productos", values, null, null);

    }//Se hace una copia de la columna precio a la columna precio_final de los productos a los cuales no se aplica ningun descuento

    public void Extraer_Casificacion(){

       CSQLite lite=new CSQLite(context);
       SQLiteDatabase db=lite.getReadableDatabase();
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

        //Cierra las conexiónes

        if(rs!=null)
            rs.close();
        if(db!=null)
            db.close();


   }//Llena los arreglos segun con el tipo de clasificacion fiscal

    public Double Obtener_DescuentoDelCliente(String id_cliente){

       CSQLite lite=new CSQLite(context);
       SQLiteDatabase db=lite.getWritableDatabase();

       if(!db.isOpen())
           db=lite.getWritableDatabase();

       Cursor rs=db.rawQuery("select descuento_comercial from clientes where id_cliente='"+id_cliente+"'",null);

       Double descuento=0.00;

      if(rs.moveToFirst()){

             try {
                 descuento = Double.parseDouble(rs.getString(0));
             }catch (Exception e){
                 descuento =0.00;
             }

      }

       if(db!=null)
           db.close();

       return descuento;
   }//Obtiene el descuento comercial de cliente

    public void Obtener_Prodcutos_Descliente(){

       lite1=new CSQLite(context);
       SQLiteDatabase db=lite1.getWritableDatabase();

       if(!db.isOpen())
           db=lite1.getWritableDatabase();

       Cursor rs=null;

       String query="select codigo,precio,iva,ieps  from productos where clasificacion_fiscal in"+where(clasificacion_Desc_Cliente);

       rs=db.rawQuery(query,null);
       data1=new ArrayList<HashMap<String, ?>>();
       Hasproducto1=new HashMap<String, String>();

       while (rs.moveToNext()){

           Hasproducto1.put("A",rs.getString(0));
           Hasproducto1.put("B",rs.getString(1));
           Hasproducto1.put("C",rs.getString(2));
           Hasproducto1.put("D",rs.getString(3));
           data1.add(Hasproducto1);
           Hasproducto1=new HashMap<String, String>();
       }

       if(rs!=null)
           rs.close();
       if(db!=null)
      db.close();


    }//Llena una lista con los productos a los cuales se les aplica el descuento directamente

    public void Obtener_Productos_DescMenor(){
       lite2=new CSQLite(context);
       SQLiteDatabase db=lite2.getReadableDatabase();

       Cursor rs=null;

       String query="select codigo,precio,iva,ieps,descuento_producto  from productos where clasificacion_fiscal in "+where(clasificacion_Desc_menor);

       rs=db.rawQuery(query,null);
       data2=new ArrayList<HashMap<String, ?>>();
       Hasproducto2=new HashMap<String, String>();

       while (rs.moveToNext()){

           Hasproducto2.put("A",rs.getString(0));
           Hasproducto2.put("B",rs.getString(1));
           Hasproducto2.put("C",rs.getString(2));
           Hasproducto2.put("D",rs.getString(3));
           Hasproducto2.put("E",rs.getString(4));
           data2.add(Hasproducto2);
           Hasproducto2=new HashMap<String, String>();
       }
      db.close();
   }//Llena una lista con los productos a los cuales se les aplica el descuento menor

    public void Obtener_Productos_SoloOferta(){
       lite3=new CSQLite(context);
       SQLiteDatabase db=lite3.getReadableDatabase();

       Cursor rs=null;

       String query="select codigo,precio,iva,ieps  from productos where clasificacion_fiscal in "+where(clasificacion_No_aplica);

       rs=db.rawQuery(query,null);
       data3=new ArrayList<HashMap<String, ?>>();
       Hasproducto3=new HashMap<String, String>();

       while (rs.moveToNext()){

           Hasproducto3.put("A",rs.getString(0));
           Hasproducto3.put("B",rs.getString(1));
           Hasproducto3.put("C",rs.getString(2));
           Hasproducto3.put("D",rs.getString(3));
           data3.add(Hasproducto3);
           Hasproducto3=new HashMap<String, String>();
       }
       //db.close();

   }

    public String Obtener_idCliente(){
         CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        Cursor rs=db.rawQuery("select id_cliente from sesion_cliente where Sesion=1",null);

        String cliente="";

        if(rs.moveToFirst()){
            cliente=rs.getString(0);
        }

        db.close();

        return cliente;
    }
    //1
   public void Generar_PrecioFinal_Descliente(){

       String codigo;
       Double precioFarmacia;
       Double iva;
       Double ieps;
       Double oferta=0.00;
       Double desc_comercial=Obtener_DescuentoDelCliente(Obtener_idCliente());

       SQLiteDatabase db=lite1.getWritableDatabase();


             arrayOf1=new String[data1.size()][2];
             arrayFin1=new String[data1.size()][2];

           for (int i = 0; i < data1.size(); i++) {
               try {

               codigo = data1.get(i).get("A").toString();
               precioFarmacia = Double.parseDouble(data1.get(i).get("B").toString());
               iva=Double.parseDouble(data1.get(i).get("C").toString());
               ieps=Double.parseDouble(data1.get(i).get("D").toString());


           /*se obtiene la oferta*/
                   Cursor rs = null;
                   try {
                       rs=db.rawQuery("select descuento from ofertas where codigo='" + codigo + "'", null);
                       if (rs.moveToFirst())
                           oferta = Double.parseDouble(rs.getString(0));
                   }finally {

                       if(rs!=null)
                       rs.close();
                   }



               Double precio1 = ((precioFarmacia - (precioFarmacia * oferta / 100)));
               Double precio2 = (precio1 - (precio1 * desc_comercial / 100));
               Double total = precio2;

                   if(!db.isOpen())
                       db=lite1.getWritableDatabase();

                   arrayOf1[i][0]=String.format(Locale.US, "%.2f", total);
                   arrayOf1[i][1]=codigo;

               //Calculo de impuestos;

               if(ieps!=0){
               total=(total+((total*ieps)/100));
               }
               if(iva!=0){
                   total=(total+((total*iva)/100));
               }

                   if(!db.isOpen())
                       db=lite1.getWritableDatabase();

               String Stotal = String.format(Locale.US,"%.2f", total);

                   arrayFin1[i][0]=Stotal;
                   arrayFin1[i][1]=codigo;

               oferta = 0.00;

               }catch (Exception e){

                /* subject="Generar_PrecioFinal_Descliente()";
                 body="Agente: "+Obtener_NoEmpleado()+e.toString();
                 new sendEmail().execute("");*/
                 continue;

               }
           }



   }//Clasificación B,Ba
    //2
   public void Generar_PrecioFinal_DescMenor(){

       String codigo="";
       Double iva;
       Double ieps;
       Double desc_producto;
       Double precioFarmacia;
       Double oferta=0.00;
       Double desc_comercial=Obtener_DescuentoDelCliente(Obtener_idCliente());

       if(lite2==null)
           lite2=new CSQLite(context);

       SQLiteDatabase db=lite2.getWritableDatabase();


       arrayOf2=new String[data2.size()][2];
       arrayFin2=new String[data2.size()][2];

       for (int i = 0; i < data2.size(); i++) {
           try {

               codigo = data2.get(i).get("A").toString();
               precioFarmacia = Double.parseDouble(data2.get(i).get("B").toString());
               iva = Double.parseDouble(data2.get(i).get("C").toString());
               ieps = Double.parseDouble(data2.get(i).get("D").toString());
               desc_producto=Double.parseDouble(data2.get(i).get("E").toString());



           /*se obtiene la oferta*/
            Cursor   rs = null;

               try {
                   rs = db.rawQuery("select descuento from ofertas where codigo='" + codigo + "'", null);

                   if (rs.moveToFirst())
                       oferta = Double.parseDouble(rs.getString(0));
               }finally {

                   if(rs!=null)
                       rs.close();
               }



               Double desc_aplica;

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

               if(!db.isOpen())
                   db=lite2.getWritableDatabase();



               arrayOf2[i][0]=String.format(Locale.US, "%.2f", total);
               arrayOf2[i][1]=codigo;

               if(ieps!=0){
                   total=(total+(total*ieps/100));
               }
               if(iva!=0){
                   total=(total+(total*iva/100));
               }

               if(!db.isOpen())
                   db=lite2.getWritableDatabase();

               String Stotal = String.format(Locale.US, "%.2f", total);


               arrayFin2[i][0]=Stotal;
               arrayFin2[i][1]=codigo;

               oferta = 0.00;


           }catch (Exception e){

               /*subject="Generar_PrecioFinal_DescMenor()";
               body="Agente: "+Obtener_NoEmpleado()+e.toString();
               new sendEmail().execute("");*/
               continue;

           }
       }


   }//Clasificacion H,HA //Se compara el descuento del producto con el desc del cliente y se hace la apoeración, al resultado se le aplica la oferta.
   //3
   public void Generar_PrecioFinal_SoloOferta(){

        String codigo;
        Double precio;
        Double total;
        Double iva;
        Double ieps;
        Double oferta=0.00;
       if(lite3==null)
           lite3=new CSQLite(context);
        SQLiteDatabase db=lite3.getWritableDatabase();


            arrayOf3=new String[data3.size()][2];
            arrayFin3=new String[data3.size()][2];

     for(int i=0;i<data3.size();i++){

         codigo=data3.get(i).get("A").toString();
         precio=Double.parseDouble(data3.get(i).get("B").toString());
         iva=Double.parseDouble(data3.get(i).get("C").toString());
         ieps=Double.parseDouble(data3.get(i).get("D").toString());


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



         arrayOf3[i][0]=String.format(Locale.US, "%.2f", total);
         arrayOf3[i][1]=codigo;

         if(ieps!=0){
            total=(total+(total*ieps/100));
         }
         if(iva!=0){
             total=(total+(total*iva/100));
         }

         String stotal=String.format(Locale.US, "%.2f", total);




         try {


             arrayFin3[i][0]=stotal;
             arrayFin3[i][1]=codigo;

         }catch (Exception e){
             //lite3=new CSQLite(context);
             //db=lite3.getWritableDatabase();
             //db.update("productos",values,"codigo='"+codigo+"'",null);
            /* subject="Generar_PrecioFinal_SoloOferta()";
             body="Agente: "+Obtener_NoEmpleado()+e.toString();
             new sendEmail().execute("");*/
             continue;

         }
         oferta=0.00;

     }
    }//Clasificación N,Na,F,O,....,etc.

    public void UpdateOfertas1(){

        ArrayList listSQL=new ArrayList();
        //Array 1
        int contador=0;


        for(int i=0;i<arrayOf1.length;i++){
            String total=arrayOf1[i][0];
            String codigo=arrayOf1[i][1];
            String query="update productos set precio_oferta='" + total + "' where codigo='"+codigo+"';";
            listSQL.add(query);
            contador++;

            if(contador==tamInsert) {

                Transacciones(listSQL);
                contador=0;
            }

        }
        if(contador>0){
            Transacciones(listSQL);
        }
        listSQL.clear();
    }

    public void UpdateOfertas2(){
        ArrayList listSQL=new ArrayList();
        //Array 1
        int contador=0;

        for(int i=0;i<arrayOf2.length;i++){
            String total=arrayOf2[i][0];
            String codigo=arrayOf2[i][1];
            String query="update productos set precio_oferta='" + total + "' where codigo='"+codigo+"';";
            listSQL.add(query);
            contador++;

            if(contador==tamInsert) {

                Transacciones(listSQL);
                contador=0;
            }

        }
        if(contador>0){
            Transacciones(listSQL);
        }

        listSQL.clear();

    }

    public void UpdateOfertas3(){
        ArrayList listSQL=new ArrayList();
        //Array 1
        int contador=0;

        for(int i=0;i<arrayOf3.length;i++){
            String total=arrayOf3[i][0];
            String codigo=arrayOf3[i][1];
            String query="update productos set precio_oferta='" + total + "' where codigo='"+codigo+"';";
            listSQL.add(query);
            contador++;

            if(contador==tamInsert) {

                Transacciones(listSQL);
                contador=0;
            }

        }
        if(contador>0){
            Transacciones(listSQL);
        }

        listSQL.clear();

    }

   public void InsertPrecioFinal1(){
       ArrayList listSQL=new ArrayList();
       //Array 1
       int contador=0;

       for(int i=0;i<arrayFin1.length;i++){
           String total=arrayFin1[i][0];
           String codigo=arrayFin1[i][1];
           String query="update productos set precio_final='"+ total + "' where codigo='" + codigo + "';";
           listSQL.add(query);

           contador++;

           if(contador==tamInsert) {
               Transacciones(listSQL);
               contador=0;
           }

       }
       if(contador>0){
           Transacciones(listSQL);
       }


       //Array 2
       listSQL.clear();

   }

   public void InsertPrecioFinal2(){
       ArrayList listSQL=new ArrayList();
       //Array 1
       int contador=0;

       for(int i=0;i<arrayFin2.length;i++){
           String total=arrayFin2[i][0];
           String codigo=arrayFin2[i][1];
           String query="update productos set precio_final='"+ total + "' where codigo='" + codigo + "';";
           listSQL.add(query);

           contador++;

           if(contador==tamInsert) {
               Transacciones(listSQL);
               contador=0;
           }

       }
       if(contador>0){
           Transacciones(listSQL);
       }

       //Array 3
       listSQL.clear();

   }

   public void InsertPrecioFinal3(){
       ArrayList listSQL=new ArrayList();
       //Array 1
       int contador=0;

       for(int i=0;i<arrayFin3.length;i++){
           String total=arrayFin3[i][0];
           String codigo=arrayFin3[i][1];
           String query="update productos set precio_final='"+ total + "' where codigo='" + codigo + "';";
           listSQL.add(query);

           contador++;

           if(contador==tamInsert) {
               Transacciones(listSQL);
               contador=0;
           }

       }
       if(contador>0){
           Transacciones(listSQL);
       }

       listSQL.clear();

   }

   public void Transacciones(ArrayList list){

        try {
            CSQLite lite = new CSQLite(context);
            SQLiteDatabase db = lite.getWritableDatabase();

            db.beginTransaction();

            for (int i = 0; i < list.size(); i++) {
                db.execSQL(String.valueOf(list.get(i)));
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            list.clear();
        }catch (Exception e){
            String error=e.toString();
            e.printStackTrace();
        }
    }

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

   public String Obtener_NoEmpleado(){
        CSQLite lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        String clave="";
        String query="";

        try {
            query="select numero_empleado from agentes where Sesion=1";
            Cursor rs = db.rawQuery(query, null);
            if (rs.moveToFirst()) {

                clave = rs.getString(0);
            }
        }catch (Exception e){

        }

        return clave;
    }

   public class sendEmail extends AsyncTask<String,Void,Object> {

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

package com.marzam.com.appventas.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by SAMSUMG on 15/11/2014.
 */
public class CSQLite extends SQLiteOpenHelper {

   private static String DB_PATH="/data/data/com.marzam.com.appventas/databases/";
   private static String DB_NAME="db.db";
   private static int Data_version=1;
   private final  Context contexts;

    public CSQLite(Context context) {
        super(context,DB_NAME,null, Data_version);

        this.contexts=context;
    }



    public void CrearBD(){
        boolean dbExists=VerificarBD();

        if(dbExists){

        }else {
            this.getReadableDatabase();
            try{
               CopiarBD();
            }catch (Exception e){
                String err=e.toString();
            }
        }

    }

    private boolean VerificarBD(){
        SQLiteDatabase checkBD=null;

        try{
            String path=DB_PATH+DB_NAME;
            checkBD=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READWRITE);

        }catch (Exception e){
            String error=e.toString();
        }
        if(checkBD!=null)
            checkBD.close();

        return checkBD != null?true:false;
    }

    private void CopiarBD(){
        try {
            InputStream myInput = contexts.getAssets().open(DB_NAME);
            String ArchivoSalida=DB_PATH+DB_NAME;

            OutputStream myOuput=new FileOutputStream(ArchivoSalida);

            byte[] buffer=new byte[1024];
            int lenght;
            while ((lenght=myInput.read(buffer))>0){
                myOuput.write(buffer,0,lenght);
            }
            myOuput.flush();
            myOuput.close();
            myInput.close();

        }catch (Exception e){

        }
    }

    public SQLiteDatabase getDataBase(){
        try{
            CrearBD();
        }catch (Exception e){

        }
        String path=DB_PATH+DB_NAME;
        return  SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READWRITE);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}

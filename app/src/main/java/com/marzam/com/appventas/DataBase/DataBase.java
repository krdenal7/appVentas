package com.marzam.com.appventas.DataBase;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.marzam.com.appventas.SQLite.CSQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imartinez on 05/05/2015.
 */

public class DataBase {
    private Context context;
    private CSQLite lite;

    //QUERYS
    public static String QUERY_SUCURSAL = "SELECT numeroSucursal FROM sucursales WHERE id_sucursal IN (SELECT id_sucursal FROM agentes where sesion=1)";
    public static String QUERY_ID_AGENT = "SELECT id_agente FROM agentes WHERE sesion=1";
    public static String QUERY_CLIENT = "SELECT id_cliente FROM sesion_cliente WHERE sesion=1";
    public static String QUERY_PROFILE_CLIENT = "SELECT perfil FROM clientes WHERE id_cliente=?";
    public static String QUERY_NUM_EMPLOYEE = "SELECT numero_empleado FROM agentes WHERE sesion=1";
    public static String QUERY_TYPE_DOCUMENT = "SELECT descripcion FROM DEV_TiposDocumento  ORDER BY descripcion ASC";
    public static String QUERY_ID_TYPE_DOCUMENT = "SELECT tipo_documento FROM DEV_TiposDocumento WHERE descripcion=?";
    public static String QUERY_REASON_RETURN = "SELECT Descripcion FROM DEV_Motivos WHERE MDPAPE=0 ORDER BY Descripcion ASC";
    public static String QUERY_ID_REASON_RETURN = "SELECT MotivoDev FROM DEV_Motivos WHERE Descripcion=? AND MDPAPE=0";
    public static String QUERY_NAME_PRODUCT = "SELECT descripcion FROM productos ORDER BY Descripcion ASC";
    public static String QUERY_MARZAM_CODE_PRODUCT = "SELECT codigo FROM productos ORDER BY Descripcion ASC";
    public static String QUERY_BAR_CODE_PRODUCT = "SELECT ean FROM productos ORDER BY Descripcion ASC";//"SELECT * FROM DEV_Detalle";
    public static String QUERY_NAME_PRODUCT_SEARCH = "SELECT descripcion FROM productos WHERE descripcion LIKE ? OR codigo LIKE ? OR ean LIKE ? ORDER BY Descripcion ASC";
    public static String QUERY_MARZAM_CODE_PRODUCT_SEARCH = "SELECT codigo FROM productos WHERE descripcion LIKE ? OR codigo LIKE ? OR ean LIKE ? ORDER BY Descripcion ASC";
    public static String QUERY_BAR_CODE_PRODUCT_SEARCH = "SELECT ean FROM productos WHERE descripcion LIKE ? OR codigo LIKE ? OR ean LIKE ? ORDER BY Descripcion ASC";

    /**
     * Constructor
     * @param activity padre
     */
    public DataBase(Activity activity){
        this.context = activity;
        this.lite = new CSQLite(this.context);
    }

    /**
     * Método para efectuar los select
     * @param query Query
     * @param parameters Parametros del query
     * @return Resultado del query
     */
    public String execSelect( String query, String ... parameters ){
        String resultado = null;
        SQLiteDatabase db = lite.getWritableDatabase();
        Cursor rs=db.rawQuery( query, parameters );
        if(rs.moveToFirst()) resultado = rs.getString(0);

        return resultado;
    }

    /**
     * Método para efectuar los select
     * @param query Query
     * @param parameters Parametros del query
     * @return Una lista con los resultados del query
     */
    public List<String> execSelectList( String query, String ... parameters ){
        List<String> resultado = new ArrayList<String>();
        SQLiteDatabase db = lite.getWritableDatabase();
        Cursor rs=db.rawQuery( query, parameters );
        int cuente = rs.getCount();
        String des = rs.toString();
        if(rs.moveToFirst()){
            do {
                String doc = rs.getString(0);
                resultado.add( doc );
            }while( rs.moveToNext() );
        }
        return resultado;
    }

    /**
     * Método con el cual se ejecuta un insert
     * @param tableName Nombre de la tabla
     * @param parameter Columnas a las cuales se le insertaran los valores
     * @param values valores de la inserción
     * @return TRUE si se ejecuto con exito, FALSE de lo contrario.
     */
    public boolean execUpdate( String tableName, String[] parameter, String[] values){
        ContentValues cv = new ContentValues();
        for( int i=0; i<parameter.length; i++ )
            cv.put(parameter[i], values[i]);

        SQLiteDatabase db = lite.getWritableDatabase();
        float success = db.insert(tableName, null, cv);

        return !(success<0);
    }
}

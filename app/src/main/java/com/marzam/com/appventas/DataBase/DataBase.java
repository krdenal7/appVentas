package com.marzam.com.appventas.DataBase;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.marzam.com.appventas.SQLite.CSQLite;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
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
    public static String QUERY_TYPE_DOCUMENT = "SELECT descripcion FROM DEV1_TiposDocumento ORDER BY descripcion ASC";
    public static String QUERY_ID_TYPE_DOCUMENT = "SELECT tipo_documento FROM DEV1_TiposDocumento WHERE descripcion=?";
    public static String QUERY_REASON_RETURN = "SELECT Descripcion FROM DEV1_Motivos WHERE MDPAPE=0 ORDER BY Descripcion ASC";
    public static String QUERY_ID_REASON_RETURN = "SELECT MotivoDev FROM DEV1_Motivos WHERE Descripcion=? AND MDPAPE=0";
    public static String QUERY_NAME_PRODUCT = "SELECT descripcion FROM productos ORDER BY Descripcion ASC";
    public static String QUERY_MARZAM_CODE_PRODUCT = "SELECT codigo FROM productos ORDER BY Descripcion ASC";
    public static String QUERY_BAR_CODE_PRODUCT = "SELECT ean FROM productos ORDER BY Descripcion ASC";//"SELECT * FROM DEV_Detalle";
    public static String QUERY_NAME_PRODUCT_SEARCH = "SELECT descripcion FROM productos WHERE descripcion LIKE ? OR codigo LIKE ? OR ean LIKE ? ORDER BY Descripcion ASC";
    public static String QUERY_MARZAM_CODE_PRODUCT_SEARCH = "SELECT codigo FROM productos WHERE descripcion LIKE ? OR codigo LIKE ? OR ean LIKE ? ORDER BY Descripcion ASC";
    public static String QUERY_BAR_CODE_PRODUCT_SEARCH = "SELECT ean FROM productos WHERE descripcion LIKE ? OR codigo LIKE ? OR ean LIKE ? ORDER BY Descripcion ASC";
    public static String QUERY_PERCENTAGE_DEVOLUTION_ALLOCATED = "SELECT PorcentajeDevolucion FROM DEV_ControlPresupuesto WHERE Representante=? AND Perfil=?";

    public static String QUERY_GET_FACTURAS = "select NumeroFactura,id_cliente,FechaFactura ,FechaVencimiento,ValorOriginal,SaldoDocRemanente,DiasAtraso " +
            "from COB_Facturas where id_cliente IN (SELECT id_cliente FROM agenda WHERE clave_agente=";
    public static String QUERY_CLAVE_AGENT = "SELECT clave_agente FROM agentes WHERE sesion=1";
    public static String QUERY_GET_COUNT_ROWS = "SELECT count(*) from COB_Facturas where id_cliente IN (SELECT id_cliente  FROM agenda WHERE  clave_agente=";
    public static String QUERY_GET_SIZE_IMPORTE_TOTAL = "select sum(ValorOriginal) as SumaIMPORTE from COB_Facturas where id_cliente IN (SELECT id_cliente " +
            "FROM agenda WHERE  clave_agente=";


    public static String QUERY_GET_SIZE_SALDO_TOTAL = "select sum((SaldoDocRemanente) ) as SumaIMPORTE from COB_Facturas where id_cliente IN (SELECT id_cliente " +
            "FROM agenda WHERE  clave_agente=";

    public static String QUERY_GET_BANK = "SELECT Nombre FROM COB_Bancos";

    public static String QUERY_GET_CREDIT_NOTES = "SELECT NumeroNota FROM COB_NotasDeCredito";

    public static String QUERY_GET_CREDIT_NOTES_COMPLETE = "SELECT * FROM COB_NotasDeCredito WHERE NumeroNota=";

    public static String QUERY_ESTIMATE_DEVOLUTION = "SELECT PptoDevolucion FROM DEV_ControlPresupuesto WHERE Representante=? AND Perfil=?";

    public static String QUERY_FOLIOS_IN_ESTIMATE = "SELECT FoliosAceptados FROM DEV_ControlPresupuesto WHERE Representante=? AND Perfil=?";
    public static String QUERY_AVERAGE_SALES = "SELECT PromedioVentaNeta FROM DEV_ControlPresupuesto WHERE Representante=? AND Perfil=?";

    public static String QUERY_FOLIOS_IN_MERMA = "SELECT ImporteFoliosMermaAuto FROM DEV_ControlPresupuesto WHERE Representante=? AND Perfil=?";
    public static String QUERY_FOLIOS_OUT_ESTIMATION = "SELECT ImporteFoliosPptoAuto FROM DEV_ControlPresupuesto WHERE Representante=? AND Perfil=?";
    public static String QUERY_FOLIOS_NO_ACCEPTED = "SELECT FoliosNoAceptados FROM DEV_ControlPresupuesto WHERE Representante=? AND Perfil=?";
    public static String QUERY_STATUS_RETURNED = "SELECT MotivoDev||' '||Descripcion Motivos FROM DEV_Motivos WHERE TipoDevolucion=? ORDER BY Motivos asc";
    public static String QUERY_GET_INVOICE = "SELECT Factura||'♀'||FechaFactura||'♀'||count(producto) FROM DEV_Facturas GROUP BY Factura ORDER BY FechaFactura asc";
    public static String QUERY_NEED_NOTE = "SELECT RequiereNota FROM DEV_Motivos WHERE MotivoDev=?";
    public static String QUERY_NEED_INVOICE = "SELECT RequiereFactura FROM DEV_Motivos WHERE MotivoDev=?";
    /*DEVOLUCIONES FULL*/
    public static String QUERY_GET_DESCRIPTION_FOR_REASON = "SELECT MotivoDev||' '||Descripcion FROM DEV_Motivos WHERE MotivoDev=?";
    public static String QUERY_FOLIOS_GOOD_STATUS = "SELECT foliosGoodStatus.Folio FROM DEV_FoliosBuenEstado foliosGoodStatus " +
            "WHERE foliosGoodStatus.Folio NOT IN (SELECT FolioDevolucion FROM DEV_EncabezadoDevoluciones)";
    //Folios para devolcuiones en mal estado
    public static String QUERY_FOLIOS_BAD_STATUS = "SELECT foliosBadStatus.Folio FROM DEV_FoliosMalEstado foliosBadStatus " +
            "WHERE foliosBadStatus.Folio NOT IN (SELECT FolioDevolucion FROM DEV_EncabezadoDevoluciones)";

    //Verificación para saber si algun folio necesita autorización
    public static String QUERY_THIS_FOLIOS_NEED_AUTHORIZATION = "SELECT Autorizaciones||'♀'||TipoAutorizacion FROM DEV_Motivos WHERE MotivoDev=?";

    //Verificación para saber si algun folio necesita autorización
    public static String QUERY_THIS_FOLIOS_AFFECTING_ESTIMATE = "SELECT AfectaConsumo FROM DEV_Motivos WHERE MotivoDev=?";

    //Tipo de autorización para algun folio
    public static String QUERY_GET_FOLIO_TYPE_AUTHORIZATION = "SELECT TipoAutorizacion FROM DEV_FoliosAutorizados WHERE FolioAutorizado=? AND Cliente=?";

    //Solo los productos con derecho a devolcuión de una factura
    public static String QUERY_PRODUCT_BY_INVOICE = "SELECT productOnInvoice.descripcion||'♀'||productOnInvoice.producto||'♀'||productOnInvoice.ean||'♀'||(productOnInvoice.CantidadOriginal - productOnInvoice.CantidadDevuelta)||'♀'||productOnInvoice.PrecioFarmacia " +
            "FROM DEV_Facturas productOnInvoice " +
            "INNER JOIN DEV_PoliticasLaboratorio politics " +
            "ON productOnInvoice.Producto = politics.Producto AND politics.DerechoDev='S' " +
            "WHERE productOnInvoice.Factura=?";
    // Todos los productos con derecho a devolcuión
    public static String QUERY_ALL_PRODUCT = "SELECT products.descripcion||'♀'||products.codigo||'♀'||products.ean||'♀'||products.precio " +
            "FROM productos products " +
            "INNER JOIN DEV_PoliticasLaboratorio politics " +
            "ON products.codigo = politics.Producto AND politics.DerechoDev='S'";

    // Todos los productos con derecho a devolcuión
    public static String QUERY_ALL_PRODUCT_FROM_FOLIO = "SELECT detail.Producto||'♀'||detail.Cantidad||'♀'||detail.PrecioFarmacia||'♀'||detail.PorcentajeBonificacion " +
            "FROM DEV_DetalleDevoluciones detail " +
            "WHERE Folio=?";

    // El registro de la cantidad devuelta de algun producto, en funcion de su factura
    public static String QUERY_RETURN_TOTAL_PRODUCT = "SELECT productOnInvoice.CantidadDevuelta " +
            "FROM DEV_Facturas productOnInvoice " +
            "WHERE productOnInvoice.producto=? " +
            "AND productOnInvoice.Factura=?";

    public static String QUERY_GET_TYPE_OF_THIS_DEVOUTION = "SELECT TipoDevolucion FROM DEV_Motivos WHERE MotivoDev=?";

    //Todas las devoluciones registradas
    public static String QUERY_ALL_DEVOLUTIONS = "SELECT distinct(encabezado.FolioDevolucion)||'♀'||" +
            "encabezado.EstadoIBS||'♀'||" +
            "encabezado.EstadoTransmision||'♀'||" +
            "encabezado.Cliente||'♀'||" +
            "encabezado.MotivoSolicitud||'♀'||" +
            "encabezado.Factura||'♀'||" +
            "encabezado.ImporteTotalAprox||'♀'||" +
            "encabezado.TipoFolio||'♀'||"+
            "encabezado.clave_agente||'♀'||"+
            "encabezado.TotalBultos||'♀'||"+
            "encabezado.HandlerAut||'♀'||"+
            "encabezado.ConsecutivoCaptura||'♀'||"+
            "encabezado.Id_estatus||'♀'||"+
            "encabezado.FechaCaptura||'♀'||"+
            "encabezado.HoraCaptura||'♀'||"+
            "encabezado.ConsumoPresupuesto||'♀'||" +
            "encabezado.EstadoAutorizacion||'♀'||" +
            "encabezado.ReferenciaAut||'♀'||" +
            "encabezado.EstadoFolio " +
            "FROM DEV_EncabezadoDevoluciones encabezado " +
            "ORDER BY encabezado.Id_estatus";
    public static String QUERY_NAME_CLIENT = "SELECT nombre FROM clientes WHERE id_cliente=?";


    //Descuento comercial por cliente
    public static String QUERY_DISCOUNT_BY_CLIENT = "SELECT descuento_comercial FROM clientes WHERE id_cliente=?";

    //Porsentaje de bonificacion
    public static String QUERY_PERCENTAGE_BONUS = "SELECT PorcentajeBonificacion FROM DEV_Motivos WHERE MotivoDev=?";

    //Solamente el numero de devoluciones pendientes
    public static String QUERY_NUM_OF_DEVOLUTIONS_PENDING = "SELECT count(FolioDevolucion)FROM DEV_EncabezadoDevoluciones WHERE EstadoTransmision=' '";

    //El total de una devolucion (Header)
    public static String QUERY_GET_TOTAL_PRICE_FROM_DEVOLUTION = "SELECT ImporteTotalAprox FROM DEV_EncabezadoDevoluciones WHERE FolioDevolucion=?";

    //El total de productos a devolver de alguna devolucion con determinado folio
    public static String QUERY_GET_TOTAL_PRODUCTS_FROM_DEVOLUTION = "SELECT sum(cantidad) FROM DEV_DetalleDevoluciones WHERE Folio=?";

    //Obtener el ultimo consecutivo para la devolucion
    public static String QUERY_GET_CONSECUTIVE = "SELECT Consecutivo FROM DEV_COnsecutivos";


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

    public boolean execInsert( String tableName, String[] parameter, String[] values){
        ContentValues cv = new ContentValues();
        for( int i=0; i<parameter.length; i++ )
            cv.put(parameter[i], values[i]);

        SQLiteDatabase db = lite.getWritableDatabase();
        float success=0.0f;
        try{
            success = db.insertOrThrow(tableName, null, cv);
        }catch (Exception  e){
            String message = e.getMessage();
            System.out.print(message);
        }

        return !(success<0);
    }

    public boolean execDelate( String tableName, String whereClause ){
        SQLiteDatabase db = lite.getWritableDatabase();
        int result = db.delete(tableName, whereClause, null);
        return result>0;
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
    public boolean execUpdate( String tableName, String[] parameter, String[] values, String whereClause){//String columnNameKey, String valueKey){
        ContentValues cv = new ContentValues();
        for( int i=0; i<parameter.length; i++ )
            cv.put(parameter[i], values[i]);

        SQLiteDatabase db = lite.getWritableDatabase();
        float success = db.update(tableName, cv, whereClause, null);

        return !(success<0);
    }

    public ArrayList<Dictionary<String, String>> execSelectArrayList( String query, String ... parameters ){
        ArrayList<Dictionary<String, String> > resultado = new ArrayList<Dictionary<String, String> >();
        Dictionary<String, String> dic = null;
        SQLiteDatabase db = lite.getWritableDatabase();
        String qry_local = ""+query;

        /*Cursor rs=db.rawQuery(query, parameters);
        String des = rs.toString();
        if(rs.moveToFirst()){
            do {
                String doc = rs.getString(0);
                resultado.add( doc );
            }while( rs.moveToNext() );
        }*/

        String[] column_names = null;
        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            Cursor cursor = db.rawQuery(qry_local, null);
            if(cursor.moveToFirst()){
                do{
                    column_names = cursor.getColumnNames();
                    dic = new Hashtable<String, String>();

                    for(int i = 0; i < column_names.length; i++){
                        dic.put(cursor.getColumnName(i), ""+cursor.getString(i));
                    }
                    resultado.add(dic);

                }while(cursor.moveToNext());
            }else{

                Log.i("No hay resultados", "No hay resultados");
            }

            db.close();
        }

        //print
        Log.i("LOCAL REGISTERS", ""+resultado.size());
        for(int i = 0; i < resultado.size(); i++){
            Dictionary<String, String> localdic = resultado.get(i);
            for(int j = 0; j < column_names.length; j++){
                Log.i(""+column_names[j], ""+localdic.get(""+column_names[j]));
            }
        }
        Log.i("ARRAY RESULT", "" + resultado.size());

        return resultado;
    }
}

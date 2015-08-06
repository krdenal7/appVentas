package com.marzam.com.appventas.DevolucionesFull.DataBaseInterface;

import android.app.Activity;

import com.marzam.com.appventas.DataBase.DataBase;
import com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionPendiente.DevolucionPendiente;
import com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionesFullReturnsList;
import com.marzam.com.appventas.DevolucionesFull.Facturas.Invoice;
import com.marzam.com.appventas.DevolucionesFull.PrepareSendingData.PrepareSendingData;
import com.marzam.com.appventas.DevolucionesFull.Productos.DevolucionesFullProductList;
import com.marzam.com.appventas.DevolucionesFull.Productos.Product;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by lcabral on 26/06/2015.
 */
public class DataBaseInterface {

    //public static double availableReturns = 20000.0;
    public static double totalProductoToReturn = 0.0;
    public static ArrayList<DevolucionPendiente> devolucionesPendientes;

    private static DataBase db;
    private static Activity context;

    private static void initDataBase( Activity newContext ){
        if( DataBaseInterface.context != newContext )DataBaseInterface.context = newContext;
        if( DataBaseInterface.db==null ) DataBaseInterface.db = new DataBase( DataBaseInterface.context );
    }

    // Presupuesto de las devolución -----------------------------------------
    public static String getStrEstimateReturns(Activity newContext) {

        Double estimateReturns = getEstimateReturns(newContext);
        String strEstimateReturns = setFormatMoney(estimateReturns);

        return strEstimateReturns;
    }

    public static double getEstimateReturns( Activity newContext ) {
        initDataBase( newContext );

        String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strEstimateReturns = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_ESTIMATE_DEVOLUTION, claveAgenteActivo, perfilClienteActivo);

        double estimateReturns = Double.parseDouble(strEstimateReturns.replace(",", "").trim());
        return estimateReturns;
    }
    //--------------------------------------------------------------------------

    // Porcentaje de las devolución asignada-----------------------------------------
    public static String getStrPercentageAllocated(Activity newContext) {

        Double percentageAllocated = getPercentageAllocated(newContext);
        String strPercentageAllocated = (percentageAllocated*100)+"";

        if( strPercentageAllocated.length()==3 ){
            if( strPercentageAllocated.charAt(2)=='0' ){
                strPercentageAllocated = strPercentageAllocated.charAt(0)+"";
            }
        }
        return strPercentageAllocated;
    }

    public static double getPercentageAllocated( Activity newContext ) {
        initDataBase( newContext );

        String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strPercentageAllocated = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_PERCENTAGE_DEVOLUTION_ALLOCATED, claveAgenteActivo, perfilClienteActivo);

        double percentageAllocated = Double.parseDouble(strPercentageAllocated.replace(",", "").trim());
        return percentageAllocated;
    }
    //--------------------------------------------------------------------------

    //Promedio de ventas -----------------------------------------------
    public static String getStrAverageSales(Activity newContext) {

        Double averageSales = getAverageSales(newContext);
        String strAverageSales = setFormatMoney(averageSales);

        return strAverageSales;
    }

    public static double getAverageSales( Activity newContext ) {
        initDataBase( newContext );

        String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strAverageSales = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_AVERAGE_SALES, claveAgenteActivo, perfilClienteActivo);

        double averageSales = Double.parseDouble(strAverageSales.replace(",", "").trim());
        return averageSales;
    }
    //--------------------------------------------------------------------------

    //Folios dentro del presupuesto------------------------------------------
    public static String getStrFoliosInEstimate(Activity newContext) {

        Double foliosInEstimate = getFoliosInEstimate(newContext);
        String strFoliosInEstimate = setFormatMoney(foliosInEstimate);

        return strFoliosInEstimate;
    }

    public static double getFoliosInEstimate( Activity newContext ) {
        initDataBase( newContext );

        String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strFoliosInEstimate = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_FOLIOS_IN_ESTIMATE, claveAgenteActivo, perfilClienteActivo);

        double foliosInEstimate = Double.parseDouble( strFoliosInEstimate.replace(",","").trim() );
        return foliosInEstimate;
    }

    /**
     * Actualiza la liquides acomulada por las devoluciones realizadas (Folios dentro del presupuesto)
     */
    public static void setFoliosInEstimate( Activity newContext, String totalToReturn, double ... consumoActualArr ) {
        double consumoActual = 0;
        if( consumoActualArr.length!=0 ) consumoActual = consumoActualArr[0];
        double newFoliosInEstimate = getFoliosInEstimate(newContext)-consumoActual+Double.parseDouble( totalToReturn );

        String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_PROFILE_CLIENT, idClienteActivo);

        DataBaseInterface.db.execUpdate("DEV_ControlPresupuesto", new String[]{"FoliosAceptados"},  new String[]{newFoliosInEstimate+""},
                "Representante='"+claveAgenteActivo+"' AND Perfil='"+perfilClienteActivo+"'");
    }
    //--------------------------------------------------------------------------

    //Folios autorizados como merma ------------------------------------------
    public static String getStrFoliosInMerma(Activity newContext) {

        Double foliosInMerma = getFoliosInMerma(newContext);
        String strFoliosInMerma = setFormatMoney(foliosInMerma);

        return strFoliosInMerma;
    }

    public static double getFoliosInMerma( Activity newContext ) {
        initDataBase( newContext );

        String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strFoliosInMerma = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_FOLIOS_IN_MERMA, claveAgenteActivo, perfilClienteActivo);

        double foliosInMerma = Double.parseDouble(strFoliosInMerma.replace(",", "").trim());
        return foliosInMerma;
    }

    public static void setFoliosInMerma( Activity newContext, String totalToReturn, double ... consumoActualArr ) {
        double consumoActual = 0;
        if( consumoActualArr.length!=0 ) consumoActual = consumoActualArr[0];
        double newFoliosInMerma = getFoliosInMerma(newContext)-consumoActual+Double.parseDouble(totalToReturn);

        String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_PROFILE_CLIENT, idClienteActivo);

        DataBaseInterface.db.execUpdate("DEV_ControlPresupuesto", new String[]{"ImporteFoliosMermaAuto"},  new String[]{newFoliosInMerma+""},
                "Representante='"+claveAgenteActivo+"' AND Perfil='"+perfilClienteActivo+"'");
    }
    //--------------------------------------------------------------------------

    //Folios no aceptados ------------------------------------------
    public static String getStrFoliosNoAccepted(Activity newContext) {

        Double foliosNoAccepted = getFoliosNoAccepted(newContext);
        String strFoliosNoAccepted = setFormatMoney(foliosNoAccepted);

        return strFoliosNoAccepted;
    }

    public static double getFoliosNoAccepted( Activity newContext ) {
        initDataBase( newContext );

        String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strFoliosNoAccepted = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_FOLIOS_NO_ACCEPTED, claveAgenteActivo, perfilClienteActivo);

        double foliosNoAccepted = Double.parseDouble(strFoliosNoAccepted.replace(",", "").trim());
        return foliosNoAccepted;
    }
    //--------------------------------------------------------------------------

    //Folios fuera del presupuesto pero autrizadas ------------------------------------------
    public static String getStrFoliosOutEstimation(Activity newContext) {

        Double foliosOutEstimation = getFoliosOutEstimation(newContext);
        String strFoliosOutEstimation = setFormatMoney(foliosOutEstimation);

        return strFoliosOutEstimation;
    }

    public static double getFoliosOutEstimation( Activity newContext ) {
        initDataBase( newContext );

        String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strFoliosOutEstimation = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_FOLIOS_OUT_ESTIMATION, claveAgenteActivo, perfilClienteActivo);

        double foliosOutEstimation = Double.parseDouble(strFoliosOutEstimation.replace(",", "").trim());
        return foliosOutEstimation;
    }

    public static void setFoliosOutEstimation( Activity newContext, String totalToReturn, double ... consumoActualArr ) {
        double consumoActual = 0;
        if( consumoActualArr.length!=0 ) consumoActual = consumoActualArr[0];
        double newFoliosOutEstimation = getFoliosOutEstimation(newContext)-consumoActual+Double.parseDouble( totalToReturn );

        String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterface.db.execSelect(DataBaseInterface.db.QUERY_PROFILE_CLIENT, idClienteActivo);

        DataBaseInterface.db.execUpdate("DEV_ControlPresupuesto", new String[]{"ImporteFoliosPptoAuto"},  new String[]{newFoliosOutEstimation+""},
                "Representante='"+claveAgenteActivo+"' AND Perfil='"+perfilClienteActivo+"'");
    }
    //--------------------------------------------------------------------------

    //Presupuesto disponible ------------------------------------------
    public static String getStrAvalibleReturns(Activity newContext) {

        Double availableReturns = getAvalibleReturns(newContext);
        String strAvailableReturns = setFormatMoney(availableReturns);

        return strAvailableReturns;
    }

    public static double getAvalibleReturns( Activity newContext ){
        double estimateReturn = DataBaseInterface.getEstimateReturns(newContext);
        double foliosInEstimate = DataBaseInterface.getFoliosInEstimate(newContext);
        double availableReturns = estimateReturn - foliosInEstimate;

        return availableReturns;
    }
    //--------------------------------------------------------------------------

    //Razones de devolución buen estado------------------------------------------
    public static List getReasonForGoodStatusReturned(Activity newContext) {
        initDataBase( newContext );
        List listGoodStatusReturned = DataBaseInterface.db.execSelectList(DataBase.QUERY_STATUS_RETURNED, "1");

        return listGoodStatusReturned ;
    }
    //--------------------------------------------------------------------------

    //Razones de devolución buen estado------------------------------------------
    public static List getReasonForBadStatusReturned(Activity newContext) {
        initDataBase( newContext );
        List listBadStatusReturned = DataBaseInterface.db.execSelectList(DataBase.QUERY_STATUS_RETURNED, "2");

        return listBadStatusReturned ;
    }
    //--------------------------------------------------------------------------

    //Razon de devolución segun el id------------------------------------------
    public static String getDescriptionReasonReturned(Activity newContext, String idReazonToReturned) {
        initDataBase( newContext );
        String descriptionReasonReturned = DataBaseInterface.db.execSelect(DataBase.QUERY_GET_DESCRIPTION_FOR_REASON, idReazonToReturned);

        return descriptionReasonReturned;
    }
    //--------------------------------------------------------------------------

    //Facturas------------------------------------------
    public static List getInvoices(Activity newContext) {
        initDataBase( newContext );
        List<String> stringList = DataBaseInterface.db.execSelectList(DataBase.QUERY_GET_INVOICE);
        ArrayList<Invoice> list = new ArrayList<Invoice>();
        for (int i = 0; i < stringList.size(); i++) {
            String invoiceWithAllAtributes = stringList.get(i);
            String[] arrayInvoice = invoiceWithAllAtributes.split("♀");//0:Factura, 1: Fecha, 2: numero de productos

            String year = arrayInvoice[1].substring(0, 4);
            String month = arrayInvoice[1].substring(4, 6);
            String day = arrayInvoice[1].substring(6, 8);
            arrayInvoice[1] = day+"/"+month+"/"+year;

            list.add(new Invoice(arrayInvoice[0],arrayInvoice[1],arrayInvoice[2]));
        }

        return list ;
    }
    //--------------------------------------------------------------------------

    //Para saber si este motivo necesita factura------------------------------------------
    public static String thisReasonNeedInvoice(Activity newContext, String idReason) {
        initDataBase( newContext );
        String needInvoice = DataBaseInterface.db.execSelect(DataBase.QUERY_NEED_INVOICE, idReason);

        return needInvoice ;
    }
    //--------------------------------------------------------------------------

    //Para saber si este motivo necesita Nota de cargo------------------------------------------
    public static String thisReasonNeedNote(Activity newContext, String idReason) {
        initDataBase( newContext );
        String needInvoice = DataBaseInterface.db.execSelect(DataBase.QUERY_NEED_NOTE, idReason);

        return needInvoice ;
    }
    //--------------------------------------------------------------------------

    //Folios para devoluciones en buen estado ------------------------------------------
    public static List getFoliosForGoodStatusReturned(Activity newContext) {
        initDataBase( newContext );
        List FoliosForGoodStatusReturned = DataBaseInterface.db.execSelectList(DataBase.QUERY_FOLIOS_GOOD_STATUS);
        return FoliosForGoodStatusReturned ;
    }
    //--------------------------------------------------------------------------

    //Folios para devoluciones en buen estado ------------------------------------------
    public static List getFoliosForBadStatusReturned(Activity newContext) {
        initDataBase( newContext );
        List FoliosForBadStatusReturned = DataBaseInterface.db.execSelectList(DataBase.QUERY_FOLIOS_BAD_STATUS);
        return FoliosForBadStatusReturned ;
    }
    //--------------------------------------------------------------------------

    //Verificación: Este folios necesita autorización ---------------------------
    public static String[] thisReasonNeedAuthorization(Activity newContext, String idReason) {
        initDataBase( newContext );
        String needAuthorization = DataBaseInterface.db.execSelect(DataBase.QUERY_THIS_FOLIOS_NEED_AUTHORIZATION, idReason);
        String[] reasonNeedAuthorizationAndType  = needAuthorization.split("♀");
        if( reasonNeedAuthorizationAndType.length==0 ) {
            reasonNeedAuthorizationAndType = new String[2];
            reasonNeedAuthorizationAndType[0] = "";
            reasonNeedAuthorizationAndType[1] = "";
        }

        //reasonNeedAuthorizationAndType[0] = "N";
        return reasonNeedAuthorizationAndType;
    }
    //--------------------------------------------------------------------------

    //Verificación: Este motivo afecta el presupuesto de las devoluciones ---------------------------
    public static String thisAffectingTheEstimate(Activity newContext, String idReason) {
        initDataBase( newContext );
        String affectingTheEstimate = DataBaseInterface.db.execSelect(DataBase.QUERY_THIS_FOLIOS_AFFECTING_ESTIMATE, idReason);
        affectingTheEstimate = affectingTheEstimate!=null?affectingTheEstimate:"";

        return affectingTheEstimate;
    }
    //--------------------------------------------------------------------------

    //Verificación: Este folios necesita, en especifico, este tipo de autorizacion ---------------------------
    public static boolean thisFolioHasThisTypeAuthorization(Activity newContext, String folio, String typeAuthorizationFrom) {
        initDataBase( newContext );
        String idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        String typeAuthorization = DataBaseInterface.db.execSelect(DataBase.QUERY_GET_FOLIO_TYPE_AUTHORIZATION, folio, idClienteActivo);
        typeAuthorization = typeAuthorization!=null?typeAuthorization:"";
        return typeAuthorization.trim().compareTo(typeAuthorizationFrom)==0;
    }

    //Obtiene el tpo de la devolucion, 2: Merma, 1:NO merma ---------------------------
    public static String typeOfThisDevolution(Activity newContext, String idMotivo) {
        initDataBase( newContext );

        String typeDevolution = DataBaseInterface.db.execSelect(DataBase.QUERY_GET_TYPE_OF_THIS_DEVOUTION, idMotivo);
        typeDevolution = typeDevolution!=null?typeDevolution:"";
        return typeDevolution;
    }
    //--------------------------------------------------------------------------

    //Obtiene la clave del cliente ---------------------------
    public static String getIDCLiente(Activity newContext) {
        initDataBase( newContext );
        String idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        return idClienteActivo;
    }
    //--------------------------------------------------------------------------

    //Obtiene el nombre del cliente
    public static String getNameClient(Activity newContext, String iDClient ) {
        initDataBase( newContext );
        String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_NAME_CLIENT, iDClient);
        return claveAgenteActivo;
    }
    //-----------------------------------------------------------------------------

    //Obtiene la clave del agente ---------------------------
    public static String getClaveAgent(Activity newContext) {
        initDataBase( newContext );
        String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        return claveAgenteActivo;
    }
    //--------------------------------------------------------------------------

    //Obtiene el ultimo registro de consecutivo de devoluciones ---------------------------
    public static String getConsecutivo(Activity newContext) {
        initDataBase( newContext );
        String consecutivo = DataBaseInterface.db.execSelect(DataBase.QUERY_GET_CONSECUTIVE);
        if( consecutivo==null ) consecutivo="0";
        consecutivo = ""+(Integer.parseInt(consecutivo)+1);

        DataBaseInterface.db.execUpdate("DEV_Consecutivos", new String[]{"Consecutivo"},  new String[]{consecutivo}, null);

        /*String claveAgenteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLAVE_AGENT);*/
        return consecutivo;
    }
    //--------------------------------------------------------------------------

    //Obtiene la num de empleado ---------------------------
    public static String getNumEmpleado(Activity newContext) {
        initDataBase( newContext );
        String numEmpleado = DataBaseInterface.db.execSelect(DataBase.QUERY_NUM_EMPLOYEE);
        return numEmpleado;
    }
    //--------------------------------------------------------------------------

    //Obtiene descuento comercial por cliente ---------------------------
    public static String getDescuentoComercialCliente(Activity newContext, String ... idCliente) {
        initDataBase( newContext );
        String idClienteActivo;
        if( idCliente.length==0 ) {
            idClienteActivo = DataBaseInterface.db.execSelect(DataBase.QUERY_CLIENT);
        } else{
            idClienteActivo = idCliente [0];
        }
        String descuentoComercialCliente = DataBaseInterface.db.execSelect(DataBase.QUERY_DISCOUNT_BY_CLIENT, idClienteActivo);
        return descuentoComercialCliente;
    }
    //--------------------------------------------------------------------------

    //Obtiene descuento comercial por cliente ---------------------------
    public static String getPorcentajeBonificacion(Activity newContext, String idReason) {
        initDataBase( newContext );
        String porsentajeBonificacion = DataBaseInterface.db.execSelect(DataBase.QUERY_PERCENTAGE_BONUS, idReason);
        return porsentajeBonificacion;
    }
    //--------------------------------------------------------------------------

    /**
     * Insertar valores a una tabla
     * @param tableName
     * @param parameter
     * @param values
     * @return
     */
    public static boolean execInsert( String tableName, String[] parameter, String[] values){
        return db.execInsert(tableName, parameter, values);
    }

    public static boolean setNewDevolutionToProduct(String invoice, String product, String total){
        String totalDevolutionForThisProduct = db.execSelect(db.QUERY_RETURN_TOTAL_PRODUCT, product, invoice);
        totalDevolutionForThisProduct = (Integer.parseInt(totalDevolutionForThisProduct.trim()) - (Integer.parseInt(totalDevolutionForThisProduct.trim()) - Integer.parseInt(total.trim()) ))+"";
        return db.execUpdate("DEV_Facturas", new String[]{"CantidadDevuelta"}, new String[]{totalDevolutionForThisProduct}, "producto='" + product+"' AND Factura='"+invoice+"'");
    }

    /**
     * Insertar valores a una tabla
     * @param tableName
     * @param parameter
     * @param values
     * @return
     */
    public static boolean execUpdate( String tableName, String columnNameKey, String valueKey, String[] parameter, String[] values ){
        return db.execUpdate(tableName, parameter, values, columnNameKey + "=" + valueKey);
    }

    /**
     * Borrar un registro
     * @param tableName
     * @param columnNameKey
     * @param valueKey
     * @return
     */
    public static boolean execDelate( String tableName, String columnNameKey, String valueKey ){
        return db.execDelate(tableName, columnNameKey + "=" + valueKey);
    }




    //Productos de una factura ----------------------------------------------------------------------
    public static ArrayList<Product> getProductsFromInvoice(Activity newContext, String invoice, DevolucionPendiente ... devolucionPendienteArr) {
        initDataBase(newContext);
        ArrayList<Product> productosDevolucionPendiente = null;
        if( devolucionPendienteArr.length!=0 )
            productosDevolucionPendiente = devolucionPendienteArr[0].getProductsOnDetail();

        ArrayList<String> strList = (ArrayList<String>)DataBaseInterface.db.execSelectList(DataBase.QUERY_PRODUCT_BY_INVOICE, invoice);
        ArrayList<Product> list = new ArrayList<Product>();
        for(int i = 0;i<strList.size();i++){
            String productWithAllAtributes = strList.get(i);

            //0:Descripcion, 1: Codigo MArzam, 2: Codigo de Barras, 3: CantidadOriginal, 4: Precio
            String[] arrayproduct = productWithAllAtributes.split("♀");

            Product product = new Product();
            product.setNumberProducts("0");
            product.setNumberProductsToReturn("0");
            if( productosDevolucionPendiente!=null ){
                for( int j=0; j<productosDevolucionPendiente.size(); j++ ) {
                    Product productoDevPen = ((Product) productosDevolucionPendiente.get(j));
                    if (arrayproduct[1].trim().compareTo(productoDevPen.getMarzamCode()) == 0) {
                        arrayproduct[3] = Integer.parseInt( arrayproduct[3].trim() )+Integer.parseInt( productoDevPen.getNumberProductsToReturn().trim() )+"";

                        product.setIntNumberProductsToReturn(Integer.parseInt(productoDevPen.getNumberProductsToReturn().trim()));
                        break;
                    }

                }
            }

            if( arrayproduct[3].trim().compareTo("0")!=0 ) {
                product.setName(arrayproduct[0] );
                product.setMarzamCode(arrayproduct[1]);
                product.setBarCode(arrayproduct[2]);
                product.setNumberProducts(arrayproduct[3]);
                product.setPrice(Double.parseDouble(arrayproduct[4]) );
                list.add( product );
            }
        }

        return list;
    }

    //Todos los productos disponibles para vender
    public static ArrayList<Product> getProductsForDevolution(Activity newContext, DevolucionPendiente ... devolucionPendienteArr) {
        initDataBase(newContext);
        ArrayList<Product> productosDevolucionPendiente = null;
        if( devolucionPendienteArr.length!=0 )
            productosDevolucionPendiente = devolucionPendienteArr[0].getProductsOnDetail();

        ArrayList<String> strList = (ArrayList<String>)DataBaseInterface.db.execSelectList(DataBase.QUERY_ALL_PRODUCT);
        ArrayList<Product> list = new ArrayList<Product>();
        for(int i = 0;i<strList.size();i++) {
            String productWithAllAtributes = strList.get(i);

            //0:Descripcion, 1: Codigo Marzam, 2: Codigo de Barras, 3: Precio
            String[] arrayproduct = productWithAllAtributes.split("♀");

            Product product = new Product(arrayproduct[0], arrayproduct[1],arrayproduct[2],Integer.MAX_VALUE+"",arrayproduct[3]);
            if( productosDevolucionPendiente!=null ){
                for( int j=0; j<productosDevolucionPendiente.size(); j++ ) {
                    Product productoDevPen = ((Product) productosDevolucionPendiente.get(j));
                    if (product.getMarzamCode().trim().compareTo(productoDevPen.getMarzamCode()) == 0) {
                        product.setNumberProductsToReturn(productoDevPen.getNumberProductsToReturn().trim());
                    }
                }
            }
            list.add( product );
        }

        return list;
    }

    //Todos los productos que se encuentran en el detalle de alguna devolución en especifica.
    public static ArrayList<Product> getProductsFromFolioDevolution(Activity newContext, String folio,DevolucionPendiente ... devolucionPendiente) {
        initDataBase(newContext);
        ArrayList<String> strList = (ArrayList<String>)DataBaseInterface.db.execSelectList(DataBase.QUERY_ALL_PRODUCT_FROM_FOLIO, folio);
        ArrayList<Product> list = new ArrayList<Product>();
        for(int i = 0;i<strList.size();i++) {
            String productWithAllAtributes = strList.get(i);
            //0:Codigo marzam del producto, 1: Cantidad de dicho producto
            String[] arrayproduct = productWithAllAtributes.split("♀");
            Product product = new Product();
            product.setMarzamCode(arrayproduct[0].trim());
            product.setNumberProductsToReturn(arrayproduct[1].trim());
            product.setPrice(Double.parseDouble(arrayproduct[2].trim()));
            if( devolucionPendiente.length!=0 )
                devolucionPendiente[0].setPercentageBonus( arrayproduct[3].trim() );

            list.add( product );
        }

        return list;
    }

    //Todas las devoluciones registradas
    public static ArrayList<DevolucionPendiente> getPendingReturns( Activity newContext ){

        initDataBase(newContext);
        ArrayList<String> strList = (ArrayList<String>)DataBaseInterface.db.execSelectList(DataBase.QUERY_ALL_DEVOLUTIONS);
        ArrayList<DevolucionPendiente> list = new ArrayList<DevolucionPendiente>();
        for(int i = 0;i<strList.size();i++) {
            String devolutionHeaderWithAllAtributes = strList.get(i);

            //0. FolioDevolucion, 1. EstadoIBS, 2. EstadoTransmision
            String[] arrayDevolutionHeader = devolutionHeaderWithAllAtributes.split("♀");
            DevolucionPendiente devolucionPendiente = new DevolucionPendiente();

            devolucionPendiente.setFolioForThisReturn(arrayDevolutionHeader[0]);
            devolucionPendiente.setStatusIBS(arrayDevolutionHeader[1]);
            devolucionPendiente.setStatusTransmit(arrayDevolutionHeader[2]);
            devolucionPendiente.setIDClient(arrayDevolutionHeader[3]);
            devolucionPendiente.setReasonReturnSelected(DataBaseInterface.getDescriptionReasonReturned(newContext, arrayDevolutionHeader[4]));
            devolucionPendiente.setDiscountByClient(DataBaseInterface.getDescuentoComercialCliente(newContext, devolucionPendiente.getIDClient()));
            devolucionPendiente.setNumberInvoiceToReturn(arrayDevolutionHeader[5]);
            devolucionPendiente.setCostForThisReturn(arrayDevolutionHeader[6]);
            devolucionPendiente.setTypeForThisReturn(arrayDevolutionHeader[7]);
            devolucionPendiente.setClaveAgente(arrayDevolutionHeader[8]);
            devolucionPendiente.setNumPackages(arrayDevolutionHeader[9]);
            devolucionPendiente.setHandlerAutorizacion(arrayDevolutionHeader[10]);
            devolucionPendiente.setConsecutivoCaptura(arrayDevolutionHeader[11]);
            devolucionPendiente.setIdStatus(arrayDevolutionHeader[12]);
            devolucionPendiente.setDateThatThisReturnWasSave(arrayDevolutionHeader[13]);
            devolucionPendiente.setTimeThatThisReturnWasSave(arrayDevolutionHeader[14]);
            devolucionPendiente.setAffectConsummation(arrayDevolutionHeader[15]);
            devolucionPendiente.setStatusAutorizacion(arrayDevolutionHeader[16]);
            //arrayDevolutionHeader[17] --- ReferenciaAut
            devolucionPendiente.setStatusFolio(arrayDevolutionHeader[18]);

            if( devolucionPendiente.getIdStatus().trim().compareTo("10")==0 )
                DevolucionesFullReturnsList.NUM_AVALIBLE_RETURNS++;

            list.add(devolucionPendiente);
        }

        return list;
    }

    //El numero de las devoluciones pendientes--------------------------------------------
    public static String getNumPendingReturns(){
        String numOfDevolutionsPending = DataBaseInterface.db.execSelect(DataBase.QUERY_NUM_OF_DEVOLUTIONS_PENDING);
        return numOfDevolutionsPending;
    }
    //-----------------------------------------------------------------------------------

    //Total de los productos a debolver (PRECIO)-----------------------------------------------------
    public static double getTotalProductoToReturn( String ... folio ) {
        if( folio.length!=0 ){
            String strTotalProductoToReturn = db.execSelect( DataBase.QUERY_GET_TOTAL_PRICE_FROM_DEVOLUTION, folio[0]);
            totalProductoToReturn = Double.parseDouble( strTotalProductoToReturn.trim().replace(",","") );
        }
        return totalProductoToReturn;
    }
    //-----------------------------------------------------------------------------------

    //Total de productos a debolver (CANTIDAD)-----------------------------------------------------
    public static String getTotalProducsFromDevolution( String folio ) {
        String strTotalProducto = db.execSelect( DataBase.QUERY_GET_TOTAL_PRODUCTS_FROM_DEVOLUTION, folio);

        return strTotalProducto;
    }
    //-----------------------------------------------------------------------------------



    /*public static List getInvoices(Activity newContext) {
        initDataBase( newContext );
        List<String> stringList = DataBaseInterface.db.execSelectList(DataBase.QUERY_GET_INVOICE);
        ArrayList<Invoice> list = new ArrayList<Invoice>();
        for (int i = 0; i < stringList.size(); i++) {
            String invoiceWithAllAtributes = stringList.get(i);
            String[] arrayInvoice = invoiceWithAllAtributes.split(",");//0:Factura, 1: Fecha, 2: numero de productos

            String year = arrayInvoice[1].substring(0, 4);
            String month = arrayInvoice[1].substring(4, 6);
            String day = arrayInvoice[1].substring(6, 8);
            arrayInvoice[1] = day+"/"+month+"/"+year;

            list.add(new Invoice(arrayInvoice[0],arrayInvoice[1],arrayInvoice[2]));
        }

        return list ;
    }*/

    //DateBase: get all "invoice"'s products
    /*public static ArrayList<Invoice> getInvoices(){
        ArrayList<Invoice> list = new ArrayList<Invoice>();
        for (int i = 0; i < 30; i++)
            list.add(new Invoice(i));

        return list;
    }*/

    /*public static double getTotalProductoToReturn() {
        return totalProductoToReturn;
    }*/

    /*public static void setTotalProductoToReturn(double totalProductoToReturn) {
        DataBaseInterface.totalProductoToReturn = totalProductoToReturn;
    }*/


    /*public static void addPendingReturns( DevolucionPendiente devolucionPendiente ){
        if( DataBaseInterface.devolucionesPendientes==null )
            DataBaseInterface.devolucionesPendientes = new ArrayList<DevolucionPendiente>();


        DataBaseInterface.devolucionesPendientes.add(devolucionPendiente);
    }*/

    /*public static void removePendingReturns( DevolucionPendiente devolucionPendiente ){
        DataBaseInterface.devolucionesPendientes.remove(devolucionPendiente);
    }*/

    /*public static void removePendingReturns( int index ){
        DataBaseInterface.devolucionesPendientes.remove( index );
    }*/

    /*public static ArrayList<DevolucionPendiente> getPendingReturns(){

        return DataBaseInterface.devolucionesPendientes;
    }*/

    /*public static int getNumPendingReturns(){
        int num = 0;
        if( DataBaseInterface.devolucionesPendientes!=null ) {
            for (int i = 0; i < DataBaseInterface.devolucionesPendientes.size(); i++) {
                DevolucionPendiente devolucionPendiente = devolucionesPendientes.get(i);
                if (!devolucionPendiente.isSend()) num++;
            }
        }
        return num;
    }*/



    public static String setFormatMoney( double availableReturns) {

        //Le damos formato al costo total de la devolución
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec = (DecimalFormat) nf;
        dec.setMaximumFractionDigits(2);
        dec.setMinimumFractionDigits(2);

        return dec.format(availableReturns);
    }
}

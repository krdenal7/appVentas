package com.marzam.com.appventas.Cobranza;

import android.app.Activity;

import com.marzam.com.appventas.DataBase.DataBase;
//import com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionPendiente.DevolucionPendiente;
//import com.marzam.com.appventas.DevolucionesFull.Facturas.Invoice;
//import com.marzam.com.appventas.DevolucionesFull.Productos.Product;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by lcabral on 26/06/2015.
 */
public class DataBaseInterfaceCobranza {

    //public static double availableReturns = 20000.0;
    public static double totalProductoToReturn = 0.0;
    //public static ArrayList<DevolucionPendiente> devolucionesPendientes;

    private static DataBase db;
    private static Activity context;

    private static void initDataBase( Activity newContext ){
        if( DataBaseInterfaceCobranza.context != newContext )DataBaseInterfaceCobranza.context = newContext;
        if( DataBaseInterfaceCobranza.db==null ) DataBaseInterfaceCobranza.db = new DataBase( DataBaseInterfaceCobranza.context );
    }

    // Presupuesto de las devolución -----------------------------------------
    public static String getStrEstimateReturns(Activity newContext) {

        Double estimateReturns = getEstimateReturns(newContext);
        String strEstimateReturns = setFormatMoney(estimateReturns);

        return strEstimateReturns;
    }

    public static double getEstimateReturns( Activity newContext ) {
        initDataBase( newContext );

        String claveAgenteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strEstimateReturns = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_ESTIMATE_DEVOLUTION, claveAgenteActivo, perfilClienteActivo);

        double estimateReturns = Double.parseDouble( strEstimateReturns.replace(",","").trim() );
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

        String claveAgenteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strPercentageAllocated = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_PERCENTAGE_DEVOLUTION_ALLOCATED, claveAgenteActivo, perfilClienteActivo);

        double percentageAllocated = Double.parseDouble( strPercentageAllocated.replace(",","").trim() );
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

        String claveAgenteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strAverageSales = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_AVERAGE_SALES, claveAgenteActivo, perfilClienteActivo);

        double averageSales = Double.parseDouble( strAverageSales.replace(",","").trim() );
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

        String claveAgenteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strFoliosInEstimate = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_FOLIOS_IN_ESTIMATE, claveAgenteActivo, perfilClienteActivo);

        double foliosInEstimate = Double.parseDouble( strFoliosInEstimate.replace(",","").trim() );
        return foliosInEstimate;
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

        String claveAgenteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strFoliosInMerma = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_FOLIOS_IN_MERMA, claveAgenteActivo, perfilClienteActivo);

        double foliosInMerma = Double.parseDouble( strFoliosInMerma.replace(",","").trim() );
        return foliosInMerma;
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

        String claveAgenteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strFoliosNoAccepted = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_FOLIOS_NO_ACCEPTED, claveAgenteActivo, perfilClienteActivo);

        double foliosNoAccepted = Double.parseDouble( strFoliosNoAccepted.replace(",","").trim() );
        return foliosNoAccepted;
    }
    //--------------------------------------------------------------------------

    //Folios fuera del presupuesto ------------------------------------------
    public static String getStrFoliosOutEstimation(Activity newContext) {

        Double foliosOutEstimation = getFoliosOutEstimation(newContext);
        String strFoliosOutEstimation = setFormatMoney(foliosOutEstimation);

        return strFoliosOutEstimation;
    }

    public static double getFoliosOutEstimation( Activity newContext ) {
        initDataBase( newContext );

        String claveAgenteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLAVE_AGENT);
        String idClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_CLIENT);
        String perfilClienteActivo = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_PROFILE_CLIENT, idClienteActivo);
        String strFoliosOutEstimation = DataBaseInterfaceCobranza.db.execSelect(DataBaseInterfaceCobranza.db.QUERY_FOLIOS_OUT_ESTIMATION, claveAgenteActivo, perfilClienteActivo);

        double foliosOutEstimation = Double.parseDouble( strFoliosOutEstimation.replace(",","").trim() );
        return foliosOutEstimation;
    }
    //--------------------------------------------------------------------------

    //Presupuesto disponible ------------------------------------------
    public static String getStrAvalibleReturns(Activity newContext) {

        Double availableReturns = getAvalibleReturns(newContext);
        String strAvailableReturns = setFormatMoney(availableReturns);

        return strAvailableReturns;
    }

    public static double getAvalibleReturns( Activity newContext ){
        double estimateReturn = DataBaseInterfaceCobranza.getEstimateReturns(newContext);
        double foliosInEstimate = DataBaseInterfaceCobranza.getFoliosInEstimate(newContext);
        double availableReturns = estimateReturn - foliosInEstimate;

        return availableReturns;
    }
    //--------------------------------------------------------------------------

    //Razones de devolución buen estado------------------------------------------
    public static List getReasonForGoodStatusReturned(Activity newContext) {
        initDataBase( newContext );
        List listGoodStatusReturned = DataBaseInterfaceCobranza.db.execSelectList(DataBase.QUERY_STATUS_RETURNED,"1");

        return listGoodStatusReturned ;
    }
    //--------------------------------------------------------------------------

    //Razones de devolución buen estado------------------------------------------
    public static List getReasonForBadStatusReturned(Activity newContext) {
        initDataBase( newContext );
        List listBadStatusReturned = DataBaseInterfaceCobranza.db.execSelectList(DataBase.QUERY_STATUS_RETURNED, "2");

        return listBadStatusReturned ;
    }
    //--------------------------------------------------------------------------

    //Facturas------------------------------------------
    public static List getInvoices(Activity newContext) {
        initDataBase( newContext );
        List listBadStatusReturned = DataBaseInterfaceCobranza.db.execSelectList(DataBase.QUERY_GET_INVOICE);

        return listBadStatusReturned ;
    }
    //--------------------------------------------------------------------------

    //Para saber si este motivo necesita factura------------------------------------------
    public static String thisReasonNeedInvoice(Activity newContext, String idReason) {
        initDataBase( newContext );
        String needInvoice = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_NEED_INVOICE, idReason);

        return needInvoice ;
    }
    //--------------------------------------------------------------------------

    //Para saber si este motivo necesita Nota de cargo------------------------------------------
    public static String thisReasonNeedNote(Activity newContext, String idReason) {
        initDataBase( newContext );
        String needInvoice = DataBaseInterfaceCobranza.db.execSelect(DataBase.QUERY_NEED_NOTE, idReason);

        return needInvoice ;
    }
    //--------------------------------------------------------------------------


    //DateBase: get all "invoice"'s products
    /*public static ArrayList<Invoice> getInvoices(){
        ArrayList<Invoice> list = new ArrayList<Invoice>();
        for (int i = 0; i < 30; i++)
            list.add(new Invoice(i));

        return list;
    }*/

    public static double getTotalProductoToReturn() {
        return totalProductoToReturn;
    }

    public static void setTotalProductoToReturn(double totalProductoToReturn) {
        DataBaseInterfaceCobranza.totalProductoToReturn = totalProductoToReturn;
    }





    /*public static void setAvailableReturns(double availableReturns) {
        DataBaseInterface.availableReturns = availableReturns;
    }*/

    /*public static void addPendingReturns( DataBaseInterfaceCobranza devolucionPendiente ){
        if( DataBaseInterfaceCobranza.devolucionesPendientes==null )
            DataBaseInterfaceCobranza.devolucionesPendientes = new ArrayList<DevolucionPendiente>();

        DataBaseInterfaceCobranza.devolucionesPendientes.add(devolucionPendiente );
    }

    public static void removePendingReturns( DevolucionPendiente devolucionPendiente ){
        DataBaseInterfaceCobranza.devolucionesPendientes.remove(devolucionPendiente);
    }

    public static void removePendingReturns( int index ){
        DataBaseInterfaceCobranza.devolucionesPendientes.remove( index );
    }

    public static ArrayList<DevolucionPendiente> getPendingReturns(){
        return DataBaseInterfaceCobranza.devolucionesPendientes;
    }

    public static int getNumPendingReturns(){
        int num = 0;
        if( DataBaseInterfaceCobranza.devolucionesPendientes!=null ) {
            for (int i = 0; i < DataBaseInterfaceCobranza.devolucionesPendientes.size(); i++) {
                DataBaseInterfaceCobranza devolucionPendiente = devolucionesPendientes.get(i);
                if (!devolucionPendiente.isSend()) num++;
            }
        }
        return num;
    }

    //DateBase: get all "invoice"'s products
    public static ArrayList<Product> getProductsFromInvoice( String invoice ){
        ArrayList<Product> list = new ArrayList<Product>();

        for(int i = 0;i<30;i++)
            list.add( new Product(i) );

        return list;
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

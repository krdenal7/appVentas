package com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionPendiente;

import android.app.Activity;

import com.marzam.com.appventas.DevolucionesFull.DataBaseInterface.DataBaseInterface;
import com.marzam.com.appventas.DevolucionesFull.Productos.Product;

import java.util.ArrayList;

/**
 * Created by lcabral on 30/06/2015.
 */
public class DevolucionPendiente {

    private String reasonReturnSelected = null;
    private String numberInvoiceToReturn = null;
    private String nameClient = null;
    private String idClient = null;
    private String claveAgente = null;;
    private String numberProductToReturn = null;
    private String statusInvoiceReasonReturn = null;
    private String costForThisReturn = null;
    private String folioForThisReturn = null;
    private String typeForThisReturn = null;
    private String dateThatThisReturnWasSave = null;
    private String timeThatThisReturnWasSave = null;
    private String numPackages = null;
    private String consecutivoCaptura = null;
    private String handlerAutorizacion = null;
    private String discountByClient = null;
    private String percentageBonus = null;
    private String affectConsummation = null;

    private String statusFolio = null;
    private String statusIBS = null;
    private String statusAutorizacion = null;
    private String idStatus = null;
    private String statusTransmit = null;
    private String timeTransmission = null;

    private ArrayList<Product> products = null;
    private ArrayList<Product> productsOnDetail = null;



    private boolean avalibleReturn = false;
    //public static int NUM_AVALIBLE_RETURNS = 0;

    private boolean send = false;

    public boolean isAvalibleReturn() {
        return avalibleReturn;
    }

    public void setAvalibleReturn(boolean avalibleReturn) {
        this.avalibleReturn = avalibleReturn;
    }

    public DevolucionPendiente(){
        initDevolucionPendiente();
    }

    public void initDevolucionPendiente(){
        this.reasonReturnSelected = null;
        this.numberInvoiceToReturn = null;
        this.numberProductToReturn = null;
        this.statusInvoiceReasonReturn = null;
        this.costForThisReturn = null;
        this.products = null;
        this.avalibleReturn = false;
        this.folioForThisReturn = null;
        this.typeForThisReturn = null;
        this.dateThatThisReturnWasSave = null;
        this.timeThatThisReturnWasSave = null;
    }

    public String getReasonReturnSelected() {
        return reasonReturnSelected;
    }

    public void setReasonReturnSelected(String reasonReturnSelected) {
        this.reasonReturnSelected = reasonReturnSelected;
    }

    public String getNumberInvoiceToReturn() {
        return numberInvoiceToReturn;
    }

    public void setNumberInvoiceToReturn(String numberInvoiceToReturn) {
        this.numberInvoiceToReturn = numberInvoiceToReturn;
    }

    public String getNumberProductToReturn() {
        return numberProductToReturn;
    }

    public void setNumberProductToReturn(String numberProductToReturn) {
        this.numberProductToReturn = numberProductToReturn;
    }

    public String getStatusInvoiceReasonReturn() {
        return statusInvoiceReasonReturn;
    }

    public void setStatusInvoiceReasonReturn(String statusInvoiceReasonReturn) {
        this.statusInvoiceReasonReturn = statusInvoiceReasonReturn;
    }

    public String getCostForThisReturn() {
        return costForThisReturn;
    }

    public void setCostForThisReturn(String costForThisReturn) {
        this.costForThisReturn = costForThisReturn;
    }

    public ArrayList<Product> getProducts() {
        return this.products;
    }

    public ArrayList<Product> getProductsOnDetail() {
        return this.productsOnDetail;
    }

    public void addProducts(Product product) {
        if(this.products == null) this.products = new ArrayList<Product>();

        if( this.products.contains( product ) ){
            this.products.remove(product);
        }
        this.products.add( product );
    }

    public void removeProducts(Product product) {
        if(this.products == null) return;
        if( this.products.contains( product ) ){
            this.products.remove( product );
        }
    }

    public String getNameClient(){ return nameClient; }
    public void setNameClient( String nameClient ){ this.nameClient = nameClient; }

    public String getIDClient(){ return idClient; }
    public void setIDClient( String idClient ){ this.idClient = idClient; }

    public String getClaveAgente(){ return claveAgente; }
    public void setClaveAgente( String claveAgente ){ this.claveAgente = claveAgente; }

    public String getFolioForThisReturn(){ return this.folioForThisReturn; }
    public void setFolioForThisReturn( String folioForThisReturn ){ this.folioForThisReturn = folioForThisReturn; }

    public String getTypeForThisReturn(){ return this.typeForThisReturn; }
    public void setTypeForThisReturn( String typeForThisReturn ){ this.typeForThisReturn = typeForThisReturn; }

    public String getDateThatThisReturnWasSave(){ return this.dateThatThisReturnWasSave; }
    public void setDateThatThisReturnWasSave( String dateThatThisReturnWasSave ){ this.dateThatThisReturnWasSave = dateThatThisReturnWasSave; }

    public String getTimeThatThisReturnWasSave(){ return this.timeThatThisReturnWasSave; }
    public void setTimeThatThisReturnWasSave( String timeThatThisReturnWasSave ){ this.timeThatThisReturnWasSave = timeThatThisReturnWasSave; }

    public String getNumPackages(){ return this.numPackages; }
    public void setNumPackages( String numPackages ){ this.numPackages = numPackages; }

    public String getConsecutivoCaptura(){ return this.consecutivoCaptura; }
    public void setConsecutivoCaptura( String consecutivoCaptura ){ this.consecutivoCaptura = consecutivoCaptura; }

    public String getHandlerAutorizacion(){ return this.handlerAutorizacion; }
    public void setHandlerAutorizacion( String handlerAutorizacion ){ this.handlerAutorizacion = handlerAutorizacion; }

    public String getDiscountByClient(){ return this.discountByClient; }
    public void setDiscountByClient( String discountByClient ){ this.discountByClient = discountByClient; }

    public String getPercentageBonus(){ return this.percentageBonus; }
    public void setPercentageBonus( String percentageBonus ){ this.percentageBonus = percentageBonus; }

    public String getAffectConsummation(){ return this.affectConsummation; }
    public void setAffectConsummation( String affectConsummation ){ this.affectConsummation = affectConsummation; }

    public void rebootFromProduct(){
        this.costForThisReturn = null;
        this.folioForThisReturn = null;
        this.numberProductToReturn = null;
        this.products = null;
        this.avalibleReturn = false;
        this.typeForThisReturn = null;
        this.dateThatThisReturnWasSave = null;
        this.timeThatThisReturnWasSave = null;
    }

    public  void sameProductsAndProductsOnDetail(){
        this.products = this.productsOnDetail;
    }

    public void setFromDetailsFromThisDevolucion( Activity context, DevolucionPendiente devolucionPendiente, boolean ... noEraseDataAndTime ){
        this.costForThisReturn = devolucionPendiente.getCostForThisReturn();
        this.folioForThisReturn = devolucionPendiente.getFolioForThisReturn();
        this.typeForThisReturn = devolucionPendiente.getTypeForThisReturn();
        this.numberProductToReturn = devolucionPendiente.getNumberProductToReturn();
        this.productsOnDetail = null;
        this.productsOnDetail = DataBaseInterface.getProductsFromFolioDevolution( context, this.folioForThisReturn, devolucionPendiente );

        /*this.products = explorar los detalles de la devolucion para llear los productos
                devolucionPendiente.getProducts(); Product product = DevolucionesFullProductList.this.productRowAdapter.getItem(position);product.setNumberProductsToReturn(numberProducts + "");

        this.numberProductToReturn = this.productsOnDetail.size();*/

        this.avalibleReturn = false;
        if( noEraseDataAndTime.length==0 ){
                this.dateThatThisReturnWasSave = null;
                this.timeThatThisReturnWasSave = null;
        }
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        //if( !send ) DevolucionPendiente.NUM_AVALIBLE_RETURNS++;
        this.send = send;
    }

    public void setStatusFolio( String statusFolio ){
        this.statusFolio = statusFolio;
    }

    public String getStatusFolio(){
        return this.statusFolio;
    }

    public void setStatusIBS( String statusIBS ){
        this.statusIBS = statusIBS;
    }

    public String getStatusIBS(){
        return this.statusIBS;
    }

    public void setStatusAutorizacion( String statusAutorizacion ){
        this.statusAutorizacion = statusAutorizacion;
    }

    public String getStatusAutorizacion(){
        return this.statusAutorizacion;
    }

    public void setIdStatus( String idStatus ){
        this.idStatus = idStatus;
    }

    public String getIdStatus(){
        return this.idStatus;
    }

    public void setStatusTransmit( String statusTransmit ){
        this.statusTransmit = statusTransmit;
    }

    public String getStatusTransmit(){
        return this.idStatus;
    }

    public void setTimeTransmission( String timeTransmission ){
        this.timeTransmission = timeTransmission;
    }

    public String getTimeTransmission(){
        return this.timeTransmission;
    }
}

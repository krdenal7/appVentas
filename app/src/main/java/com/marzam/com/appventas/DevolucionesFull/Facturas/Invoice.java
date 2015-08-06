package com.marzam.com.appventas.DevolucionesFull.Facturas;

/**
 * Created by lcabral on 15/06/2015.
 */
public class Invoice {
    public String numInvoice;
    public String dateInvoice;
    public String numProductInvoice;
    public String nameClient;

    public Invoice(String numInvoice, String dateInvoice, String numProductInvoice){
        this.numInvoice = numInvoice;
        this.dateInvoice = dateInvoice;
        this.numProductInvoice = numProductInvoice;
    }

    public String getDateInvoice() { return dateInvoice; }
    public String getNumInvoice() { return numInvoice; }
    public String getNumProductInvoice() { return numProductInvoice; }
    public String getNameClient(){ return nameClient; }

    public void setDateInvoice(String dateInvoice) {this.dateInvoice = dateInvoice; }
    public void setNumInvoice(String numInvoice) { this.numInvoice = numInvoice; }
    public void setNumProductInvoice(String numProductInvoice) { this.numProductInvoice = numProductInvoice; }
    public void setNameClient( String nameClient ){ this.nameClient = nameClient; }
}

package com.marzam.com.appventas.DevolucionesLite.ProductList;

/**
 * Created by lcabral on 07/04/2015.
 */
public class Product{
    public int icon;
    public String name;
    public String marzamCode;
    public String barCode;
    public String reasonReturn;
    public String amountProductReturn;
    public String amountPackages;
    public boolean isPackage = false;
    public boolean isProduct = false;
    public String dateAdd;
    public boolean isSection = false;
    public String textSection = null;
    public String typeDocument,  folioDocument,  numberReturn;
    public String idDevolucion;

    /**
     * @param isSection Bandera para marcar que este elemento es una seccion
     * @param textSection Texto de la seccion
     */
    public Product( String textSection, boolean isSection ){
        this.isSection = isSection;
        this.textSection = textSection;
    }


        /**
         * @param isPackage Bandera para marcar que este elemento es un bulto
         */
    public Product( boolean isPackage, String amountPackages, String typeDocument, String folioDocument, String numberReturn, String idDevolucion ){
        this.isPackage = isPackage;
        this.amountPackages = amountPackages;
        this.typeDocument = typeDocument;
        this.folioDocument = folioDocument;
        this.numberReturn = numberReturn;
        this.idDevolucion = idDevolucion;
    }

    /**
     *
     * @param idIcon id icono
     * @param name Nombre del producto
     * @param marzamCode Codigo de marzam
     * @param barCode codigo de barras del producto
     */
    public Product(int idIcon, String name, String marzamCode, String barCode ){
        this.icon = idIcon;
        this.name = name;
        this.marzamCode = marzamCode;
        this.barCode = barCode;
    }

    /**
     *
     * @param name Nombre del producto
     * @param marzamCode Codigo de marzam
     * @param barCode Codigo de barras del producto
     * @param amountPackages Cantidad de la devolucion
     * @param typeDocument Documento de devolucion
     * @param folioDocument Folio del documento
     * @param numberReturn Numero de devolucion
     * @param reasonReturn Motivo de devolución
     */
    public Product(String name, String marzamCode, String barCode, String amountPackages, String typeDocument, String folioDocument, String numberReturn, String reasonReturn, String idDevolucion){
        this.name = name;
        this.marzamCode = marzamCode;
        this.barCode = barCode;
        this.amountPackages = amountPackages;
        this.typeDocument = typeDocument;
        this.folioDocument = folioDocument;
        this.numberReturn = numberReturn;
        this.reasonReturn = reasonReturn;
        this.idDevolucion = idDevolucion;
        this.isProduct = true;
    }

    /**
     * Método con el cual se puede indicar la cantidad de producto a devolver
     * @param amountProductReturn Cantidad de producto a devolver
     */
    public void setAmountProductReturn( String amountProductReturn ){
        this.amountProductReturn = amountProductReturn;
    }

    /**
     * Método con el cual se puede indicar la cantidad de paquetes a devolver
     * @param amountPackages Cantidad de paquetes a devolver
     */
    public void setAmountPackages( String amountPackages ){
        this.amountPackages = amountPackages;
    }

    /**
     * Método con el cual se puede indicar el motivo de la devolución
     * @param reasonReturn Motivo de la devolución
     */
    public void setReasonReturn( String reasonReturn ){
        this.reasonReturn = reasonReturn;
    }

    /**
     * Método con el cual se puede indicar el tipo de documento para la devolución
     * @param typeDocument Tipo de documento
     */
    public void setReasonDocument( String typeDocument ){
        this.typeDocument = typeDocument;
    }

    /**
     * Método con el cual se puede indicar si el item de la lista se trata de un producto
     * @param isProduct Objeto "Product"
     */
    public void setIsProduct( boolean isProduct ){ this.isProduct = isProduct;}

    /**
     * Método para indicar la fecha en el que se efectiúa dicha devolución
     * @param dateAdd Fecha de la devolución
     */
    public void setDate( String dateAdd){
        this.dateAdd = dateAdd;
    }
}
package com.marzam.com.appventas.DevolucionesFull.Productos;

import java.math.BigDecimal;

/**
 * Created by lcabral on 15/06/2015.
 */
public class Product implements Cloneable{
    public String name;
    public String marzamCode;
    public String barCode;
    public String numberProducts;
    public String numberProductsToReturn;
    public int intNumberProductsToReturn;


    public double price;

    public Product(){}

    public Product(int indexProduct){
        this.name = "Product"+indexProduct;
        this.marzamCode = indexProduct+"";
        this.barCode = indexProduct+"";
        this.numberProducts = "1"+indexProduct;
        this.numberProductsToReturn = "0";

        this.price = 1000.00;
    }

    public Product(String name, String marzamCode,  String barCode, String numberProducts, String price){
        this.name = name;
        this.marzamCode = marzamCode;
        this.barCode = barCode;
        this.numberProducts = numberProducts;
        this.numberProductsToReturn = "0";

        this.price = Double.parseDouble( price );
    }

    public Product( Product product ){
        this.name = new String( product.getName() );
        this.marzamCode = new String( product.getMarzamCode() );
        this.barCode = new String( product.getBarCode() );
        this.numberProducts = new String( product.getNumberProducts() );
        this.numberProductsToReturn = new String( product.getNumberProductsToReturn() );
        this.price = new Double( product.getPrice() );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarzamCode() {
        return marzamCode;
    }

    public void setMarzamCode(String marzamCode) {
        this.marzamCode = marzamCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getNumberProducts() {
        return numberProducts;
    }

    public void setNumberProducts(String numberProducts) {
        this.numberProducts = numberProducts;
    }

    public String getNumberProductsToReturn() {
        return numberProductsToReturn;
    }

    public void setNumberProductsToReturn(String numberProductsToReturn) {
        this.numberProductsToReturn = numberProductsToReturn;
        this.intNumberProductsToReturn = Integer.parseInt( this.numberProductsToReturn );
    }

    public int getIntNumberProductsToReturn() {
        return intNumberProductsToReturn;
    }

    public void setIntNumberProductsToReturn(int intNumberProductsToReturn) {
        this.intNumberProductsToReturn = intNumberProductsToReturn;
        this.numberProductsToReturn = this.intNumberProductsToReturn+"";
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalPrice() {
        return ( price*Double.parseDouble(this.getNumberProductsToReturn()) );
    }


    protected Product clone(){
        Product p = new Product( this );
        return p;
    }
}
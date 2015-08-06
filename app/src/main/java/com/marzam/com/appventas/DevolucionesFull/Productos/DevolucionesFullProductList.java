package com.marzam.com.appventas.DevolucionesFull.Productos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.marzam.com.appventas.DevolucionesFull.CustomPrompt.CustomPrompt;
import com.marzam.com.appventas.DevolucionesFull.DataBaseInterface.DataBaseInterface;
import com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionPendiente.DevolucionPendiente;
import com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionesFullReturnsList;
import com.marzam.com.appventas.DevolucionesFull.DevolucionesFullMenuPrincipal;
import com.marzam.com.appventas.DevolucionesFull.Facturas.DevolucionesFullInvoiceList;
import com.marzam.com.appventas.DevolucionesFull.PrepareSendingData.PrepareSendingData;
import com.marzam.com.appventas.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DevolucionesFullProductList extends Activity{

    //Data of List
    private ListView litsProduct;
    private ProductRowAdapter productRowAdapter;
    private static ArrayList<Product> productList;

    //Total cost product to return
    private TextView txtDevFullTotalCostProductoToReturn;

    //Search Elements
    private EditText txtInputSearch;
    private ImageButton imgCancelSearch;
    private ImageButton imgStartSearch;

    //Buttons elements
    private Button btnReturnToInvoiceList;

    //Buttons elements
    private Button btnDevFullFinishReturn;

    //Title
    private String NAME_PHARMACY;

    //Elements returns product
    public String NUM_INVOICE;
    private String REASON_TO_RETURN;
    private TextView txtDevFullNumInvoice;
    private TextView txtDevFullReasonToReturn;
    private TextView txtDevFullReasonToReturnOnlyProduct;
    private GridLayout gridDevFullReasonToReturn;
    private LinearLayout layoutDevFullReasonToReturn;

    //Bandera para saber si viene de la lista de devoluciones o del menu principal
    public static String FROM_RETURN_LIST;

    //Presupuesto disponible para devoluciones
    private TextView txtAvailableReturns;
    private TextView setTxtAvailableReturns;
    private TextView txtAvailableReturnsSignOfPesos;

    //Presupuesto consumido de las devoluciones
    //private TextView txtPriceReturnTotal;

    //Total de devoluciones
    private TextView editTxtDevFullTotalProductoToReturn;

    //Propm para pedir la cantidad de productos
    CustomPrompt customPrompt;

    //Consumo actual de la devolucion
    public static double consumoActual;

    public static DevolucionesFullProductList thiz;

    /**
     * Creador de la Actividad.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devoluciones_full_lista_productos);
        overridePendingTransition(R.anim.devoluciones_full_right_slide_in, R.anim.devoluciones_full_right_slide_out);

        Bundle extras = getIntent().getExtras();

        this.NAME_PHARMACY = DataBaseInterface.getNameClient(DevolucionesFullProductList.this, DataBaseInterface.getIDCLiente(DevolucionesFullProductList.this));
        setTitle("Devoluciones - " + NAME_PHARMACY);

        this.NUM_INVOICE = extras.getString("numInvoice");
        this.REASON_TO_RETURN = extras.getString("reasonToReturn");
        this.FROM_RETURN_LIST = extras.getString("fromReturnList");

        DevolucionPendiente devolucionPendiente;
        if( this.FROM_RETURN_LIST == null ){
            devolucionPendiente = DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente();
            devolucionPendiente.rebootFromProduct();
        }else{
            devolucionPendiente = DevolucionesFullReturnsList.thiz.getDevolucionPendiente();
            devolucionPendiente.setFromDetailsFromThisDevolucion( this, devolucionPendiente );
        }
        thiz = this;
        DataBaseInterface.totalProductoToReturn = 0.0;
        initInterfaz();
    }


    /**
     * Iniciador de la construcción de la interfaz grafica.
     */
    public void initInterfaz() {
        initTotalProductoToReturn();
        initAvailableReturns();
        initElementsReturns();
        initTotalProductToReturn();
        initProductList();
        initButtons();
    }

    /**
     * Inicializador de los botones.
     */
    public void initButtons() {
        initSearchBar();

        this.btnReturnToInvoiceList = (Button) findViewById(R.id.idBtnDevFullInvoiceReturnMainMenu);
        this.btnReturnToInvoiceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesFullProductList.this.onBackPressed();
            }
        });

        this.btnDevFullFinishReturn = (Button) findViewById(R.id.idBtnDevFullFinishReturn);
        if (DevolucionesFullProductList.this.FROM_RETURN_LIST != null) {
            this.btnDevFullFinishReturn.setText("Guardar");
        }
        this.btnDevFullFinishReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTotalProductToReturn = DevolucionesFullProductList.this.editTxtDevFullTotalProductoToReturn.getText() + "";

                if (strTotalProductToReturn.compareTo("0") != 0) {
                    if (DevolucionesFullProductList.this.FROM_RETURN_LIST == null) {
                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setNumberProductToReturn(strTotalProductToReturn);
                        DevolucionesFullProductList.this.finishReturn();
                    }/* else {
                        DevolucionesFullReturnsList.thiz.getDevolucionPendiente().setNumberProductToReturn(strTotalProductToReturn);
                    }*/


                    if (DevolucionesFullProductList.this.NUM_INVOICE != null) {
                        if (DevolucionesFullProductList.this.FROM_RETURN_LIST == null) {
                            DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setNumberInvoiceToReturn(DevolucionesFullProductList.this.NUM_INVOICE);
                            DevolucionesFullProductList.this.finishReturn();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullProductList.this)
                                    .setTitle("Aviso")
                                    .setMessage("¿Guardar devolución?")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DevolucionesFullReturnsList.thiz.getDevolucionPendiente().setNumberInvoiceToReturn(DevolucionesFullProductList.this.NUM_INVOICE);
                                            DevolucionesFullProductList.this.saveChangesDevolution();
                                            DevolucionesFullProductList.this.finishReturn();
                                        }
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setCancelable(false)
                                    .show();
                        }
                    }

                    //DevolucionesFullProductList.this.finishReturn();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullProductList.this)
                            .setTitle("Alerta")
                            .setMessage("No se a seleccionado ningún producto para devolución")
                            .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
                }
            }
        });
    }

    /**
     * Salvar las modificaciones de una devolución
     */
    private void saveChangesDevolution(){
        DevolucionPendiente devolucionPendiente = DevolucionesFullReturnsList.thiz.getDevolucionPendiente();

        String folio = devolucionPendiente.getFolioForThisReturn();
        devolucionPendiente.setFolioForThisReturn(folio);
        devolucionPendiente.setSend(false);
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyyMMdd").format(cDate);
        String tDate = new SimpleDateFormat("HHmmss").format(cDate);
        devolucionPendiente.setDateThatThisReturnWasSave(fDate);
        devolucionPendiente.setTimeThatThisReturnWasSave(tDate);
        devolucionPendiente.setNameClient(devolucionPendiente.getNameClient());

        String idCliente = devolucionPendiente.getIDClient();
        devolucionPendiente.setIDClient(idCliente);

        String claveAgent = devolucionPendiente.getClaveAgente();
        devolucionPendiente.setClaveAgente(claveAgent);

        devolucionPendiente.setNumPackages(devolucionPendiente.getNumPackages());

        devolucionPendiente.setHandlerAutorizacion(devolucionPendiente.getHandlerAutorizacion());

        devolucionPendiente.setConsecutivoCaptura(devolucionPendiente.getConsecutivoCaptura());

        devolucionPendiente.setDiscountByClient(devolucionPendiente.getDiscountByClient());

        String[] arrayReason = devolucionPendiente.getReasonReturnSelected().split(" ");
        String idReason = arrayReason[0];
        String porsentajeBonificacion = DataBaseInterface.getPorcentajeBonificacion(DevolucionesFullProductList.this, idReason);
        devolucionPendiente.setPercentageBonus(porsentajeBonificacion);

        devolucionPendiente.setStatusFolio("A");
        devolucionPendiente.setStatusIBS("INT");
        devolucionPendiente.setStatusAutorizacion("AUT");
        devolucionPendiente.setIdStatus("10");
        devolucionPendiente.setStatusTransmit(" ");
        devolucionPendiente.setTimeTransmission("");
        devolucionPendiente.setCostForThisReturn( this.txtDevFullTotalCostProductoToReturn.getText().toString().trim().replace(",","") );

        addProductsOfList();

        PrepareSendingData prepareSendingData = new PrepareSendingData(DevolucionesFullProductList.this, devolucionPendiente);
        boolean isAUpdate = true;
        prepareSendingData.saveOnDataBase( isAUpdate );
    }

    private void addProductsOfList(){
        for( int i=0;i<this.productRowAdapter.getCount(); i++ ) {
            Product product = DevolucionesFullProductList.this.productRowAdapter.getItem(i);

            int numberProducts = new Integer(product.getNumberProducts()).intValue();
            int numberProductsToReturn = new Integer(product.getNumberProductsToReturn()).intValue();
            if( numberProductsToReturn>0 ) {

                //String strTotalProductToReturn = DevolucionesFullProductList.this.editTxtDevFullTotalProductoToReturn.getText() + "";
                //String txtCostProductoToReturn = DevolucionesFullProductList.this.txtDevFullTotalCostProductoToReturn.getText().toString().replace(",", "") + "";
                //int totalProductToReturnOfThisInvoice = new Integer(strTotalProductToReturn).intValue();
                //double costProductoToReturn = Double.parseDouble(txtCostProductoToReturn);

                //totalProductToReturnOfThisInvoice = totalProductToReturnOfThisInvoice - numberProductsToReturn;
                //totalProductToReturnOfThisInvoice = totalProductToReturnOfThisInvoice + numberProducts;

                //costProductoToReturn = costProductoToReturn - (Double.parseDouble(numberProductsToReturn + "") * product.getPrice());
                //costProductoToReturn = costProductoToReturn + (numberProducts * product.getPrice());

                //Verificamos que el costo resultante no sobrepase lo disponible
                //double availableReturns = Double.parseDouble(DevolucionesFullProductList.this.setTxtAvailableReturns.getText().toString().replace(",", ""));
                //if (costProductoToReturn > availableReturns) {
                    //System.out.println("SOBREPASA");
                    /*AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullProductList.this)
                            .setTitle("Alerta")
                            .setMessage("El monto de la devolución sobrepasa lo permitido")
                            .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));*/
                //} else {
                    //DevolucionesFullProductList.this.editTxtDevFullTotalProductoToReturn.setText(totalProductToReturnOfThisInvoice + "");
                    //DevolucionesFullProductList.this.txtDevFullTotalCostProductoToReturn.setText(setFormatMoney(costProductoToReturn));
                    product.setNumberProductsToReturn(numberProductsToReturn + "");
                    DevolucionesFullReturnsList.thiz.getDevolucionPendiente().addProducts(product);
                //}

            }
        }
    }
    /**
     * Inicializa el TextView donde se visualiza la cantidad de productos a regresar.
     */
    private void initTotalProductToReturn(){
        this.editTxtDevFullTotalProductoToReturn = (TextView) findViewById( R.id.idEditTxtDevFullTotalProductoToReturn );

        String totalProducts = "0";
        if( this.FROM_RETURN_LIST != null ){
            totalProducts = DataBaseInterface.getTotalProducsFromDevolution( DevolucionesFullReturnsList.thiz.getDevolucionPendiente().getFolioForThisReturn() );
        }


        this.editTxtDevFullTotalProductoToReturn.setText( totalProducts );
    }


    /**
     * Remueve los eventos de los botones.
     */
    public void removeListenerButtons() {
        //removeListenerCalendars();
        //removeListenerSearchBar();
    }

    /**
     * Inicializa el TextView donde se visualiza la disponibilidad de devolución.
     */
    private void initAvailableReturns(){
        this.setTxtAvailableReturns = (TextView)findViewById( R.id.idSetTxtDevFullProductListAvailableReturns );
        this.txtAvailableReturns = (TextView)findViewById( R.id.idTxtDevFullProductListAvailableReturns );
        this.txtAvailableReturnsSignOfPesos = (TextView)findViewById( R.id.idTxtDevFullProductListAvailableReturnsSignOfPesos );


        if( theReturnAffectingTheEstimate() ){
            this.setTxtAvailableReturns.setText(getAvalibleReturns());
        }else{
            this.setTxtAvailableReturns.setText(Integer.MAX_VALUE + ".00");
            this.setTxtAvailableReturns.setVisibility(View.INVISIBLE);
            this.txtAvailableReturns.setVisibility(View.INVISIBLE);
            this.txtAvailableReturnsSignOfPesos.setVisibility(View.INVISIBLE);

            this.setTxtAvailableReturns.setTextSize(1);
            this.txtAvailableReturns.setTextSize(1);
            this.txtAvailableReturnsSignOfPesos.setTextSize(1);
        }
    }

    /**
     * Verifica si el motivo afecta el presupuesto de devolucion.
     * @return Lista de folios.
     */
    private boolean theReturnAffectingTheEstimate() {
        String[] arrayReason = this.REASON_TO_RETURN.split(" ");
        String idReason = arrayReason[0];
        String thisReasonAffectingTheEstimate = this.thisReasonAffectingTheEstimate(idReason);
        if( this.FROM_RETURN_LIST == null ){
            DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setAffectConsummation( thisReasonAffectingTheEstimate.trim().compareTo("Y")==0?"S":"N" );
        }else{
            DevolucionesFullReturnsList.thiz.getDevolucionPendiente().setAffectConsummation( thisReasonAffectingTheEstimate.trim().compareTo("Y")==0?"S":"N" );
        }

        boolean affectingTheEstimate = thisReasonAffectingTheEstimate.trim().compareTo("Y")==0;
        return affectingTheEstimate;
    }

    /**
     * Verifica si el motivo afecta el presupuesto de devolucion.
     * @return Lista de folios.
     */
    private String thisReasonAffectingTheEstimate( String idReason ) {
        String affectingTheEstimate = DataBaseInterface.thisAffectingTheEstimate(this, idReason);
        return affectingTheEstimate;
    }

    /**
     * Inicializa el TextView donde se visualiza el costo total de todas las devolciones efectuadas devoluciones.
     */
    private void initTotalProductoToReturn(){
        this.txtDevFullTotalCostProductoToReturn = (TextView)findViewById( R.id.idTxtDevFullProductListPriceReturnTotal );

        if (DevolucionesFullProductList.this.FROM_RETURN_LIST == null) {
            this.txtDevFullTotalCostProductoToReturn.setText(getTotalProductoToReturn());
        } else {
            this.txtDevFullTotalCostProductoToReturn.setText(getTotalProductoToReturn( DevolucionesFullReturnsList.thiz.getDevolucionPendiente().getFolioForThisReturn().trim() ) );
            DevolucionesFullProductList.consumoActual = Double.parseDouble( this.txtDevFullTotalCostProductoToReturn.getText().toString().trim().replace(",","") );
        }
    }

    /**
     * Inicializa los elementos (textView y layout) necesarios para visualizar el motivo seleccionado previamente y en su caso la factura.
     */
    private void initElementsReturns(){
        this.txtDevFullNumInvoice = (TextView) findViewById(R.id.idTxtDevFullNumInvoice);
        this.txtDevFullReasonToReturn = (TextView)findViewById(R.id.idTxtDevFullReasonToReturn);
        this.txtDevFullReasonToReturnOnlyProduct = (TextView)findViewById(R.id.idTxtDevFullReasonToReturnOnlyProduct);

        this.gridDevFullReasonToReturn = (GridLayout) findViewById(R.id.idGridDevFullReasonToReturn);
        this.layoutDevFullReasonToReturn = (LinearLayout) findViewById(R.id.idLayoutDevFullReasonToReturn);

        this.txtDevFullNumInvoice.setText(this.NUM_INVOICE);
        this.txtDevFullReasonToReturn.setText(this.REASON_TO_RETURN);
        this.txtDevFullReasonToReturnOnlyProduct.setText(this.REASON_TO_RETURN);

        this.gridDevFullReasonToReturn.setVisibility(View.INVISIBLE);
        this.layoutDevFullReasonToReturn.setVisibility(View.INVISIBLE);

        if( this.NUM_INVOICE==null ){
            this.layoutDevFullReasonToReturn.setVisibility( View.VISIBLE );
        }else{
            if( this.NUM_INVOICE.trim().compareTo("0")==0 ) {
                this.layoutDevFullReasonToReturn.setVisibility( View.VISIBLE );
            }else {
                this.gridDevFullReasonToReturn.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Inicializa la lista de productos.
     */
    private void initProductList() {
        this.litsProduct = (ListView)findViewById(R.id.idListDevFullProductToReturn);

        if( this.NUM_INVOICE==null ){
            DevolucionesFullProductList.productList = getProductForDevolution();
            this.productRowAdapter = new ProductRowAdapter( DevolucionesFullProductList.this, R.layout.devoluciones_full_producto_row, DevolucionesFullProductList.productList);
        }
        else{
            if( this.NUM_INVOICE.trim().compareTo("0")==0 ){
                DevolucionesFullProductList.productList = getProductForDevolution();
                this.productRowAdapter = new ProductRowAdapter( DevolucionesFullProductList.this, R.layout.devoluciones_full_producto_row, DevolucionesFullProductList.productList);
            }
            else {
                DevolucionesFullProductList.productList = getProductFromInvoice(this.NUM_INVOICE);
                this.productRowAdapter = new ProductRowAdapter(DevolucionesFullProductList.this, R.layout.devoluciones_full_producto_by_invoice_row, DevolucionesFullProductList.productList);
            }
        }

        if( DevolucionesFullProductList.productList.size()>0 ){
            ((TextView)findViewById(R.id.idDevFullNoFoundProductsForDevolution)).setVisibility( View.INVISIBLE );
        }

        this.litsProduct.setAdapter(this.productRowAdapter);
        this.litsProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product productAdd = DevolucionesFullProductList.this.productRowAdapter.getItem(position);

                if (customPrompt != null) {
                    customPrompt.dismiss();
                    customPrompt=null;
                }
                customPrompt = new CustomPrompt(DevolucionesFullProductList.this);
                customPrompt.show();
                customPrompt.setTitle("Introduce una cantidad");
                customPrompt.setProduct(DevolucionesFullProductList.this.productRowAdapter, productAdd, DevolucionesFullProductList.this.editTxtDevFullTotalProductoToReturn, DevolucionesFullProductList.this.setTxtAvailableReturns, DevolucionesFullProductList.this.txtDevFullTotalCostProductoToReturn);
            }
        });
        if( this.NUM_INVOICE!=null ){
            this.litsProduct.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    Product product = DevolucionesFullProductList.this.productRowAdapter.getItem(position);

                    int numberProducts = new Integer(product.getNumberProducts()).intValue();
                    int numberProductsToReturn = new Integer(product.getNumberProductsToReturn()).intValue();

                    //Quitamos el precio acomulado y la cantidad de productos que se hiban a devolver de este producto
                    if ((numberProductsToReturn <= numberProducts) && (numberProductsToReturn != 0)) {

                        String strTotalProductToReturn = DevolucionesFullProductList.this.editTxtDevFullTotalProductoToReturn.getText() + "";
                        String txtCostProductoToReturn = DevolucionesFullProductList.this.txtDevFullTotalCostProductoToReturn.getText().toString().replace(",", "") + "";
                        int totalProductToReturnOfThisInvoice = new Integer(strTotalProductToReturn).intValue();
                        double costProductoToReturn = Double.parseDouble(txtCostProductoToReturn);

                        totalProductToReturnOfThisInvoice = totalProductToReturnOfThisInvoice - numberProductsToReturn;
                        costProductoToReturn = costProductoToReturn - (Double.parseDouble(numberProductsToReturn + "") * product.getPrice());

                        DevolucionesFullProductList.this.editTxtDevFullTotalProductoToReturn.setText(totalProductToReturnOfThisInvoice + "");
                        DevolucionesFullProductList.this.txtDevFullTotalCostProductoToReturn.setText(setFormatMoney(costProductoToReturn));

                        if( DevolucionesFullProductList.this.NUM_INVOICE!=null )
                            DataBaseInterface.setNewDevolutionToProduct(DevolucionesFullProductList.this.NUM_INVOICE, product.getMarzamCode(), ""+(Integer.parseInt(product.getNumberProductsToReturn())-(Integer.parseInt(product.getNumberProductsToReturn())-0)));

                        product.setNumberProductsToReturn("0");
                        if( DevolucionesFullProductList.this.FROM_RETURN_LIST == null ){
                            DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().removeProducts(product);
                        }else{
                            DevolucionesFullReturnsList.thiz.getDevolucionPendiente().removeProducts(product);
                        }

                        //if( DevolucionesFullProductList.this.NUM_INVOICE!=null )
                        //    DataBaseInterface.setNewDevolutionToProduct(DevolucionesFullProductList.this.NUM_INVOICE, product.getMarzamCode(), "-"+numberProductsToReturn);//valuesH[10] = Factura, parameterD[ 1] = "Producto", parameterD[ 2] = "Cantidad"

                    } else { // Agregamos toda la cantidad de producto y su precio en lo acomulado
                        String strTotalProductToReturn = DevolucionesFullProductList.this.editTxtDevFullTotalProductoToReturn.getText() + "";
                        String txtCostProductoToReturn = DevolucionesFullProductList.this.txtDevFullTotalCostProductoToReturn.getText().toString().replace(",", "") + "";
                        int totalProductToReturnOfThisInvoice = new Integer(strTotalProductToReturn).intValue();
                        double costProductoToReturn = Double.parseDouble(txtCostProductoToReturn);

                        totalProductToReturnOfThisInvoice = totalProductToReturnOfThisInvoice - numberProductsToReturn;
                        totalProductToReturnOfThisInvoice = totalProductToReturnOfThisInvoice + numberProducts;

                        costProductoToReturn = costProductoToReturn - (Double.parseDouble(numberProductsToReturn + "") * product.getPrice());
                        costProductoToReturn = costProductoToReturn + (numberProducts * product.getPrice());

                        //Verificamos que el costo resultante no sobrepase lo disponible
                        double availableReturns = Double.parseDouble(DevolucionesFullProductList.this.setTxtAvailableReturns.getText().toString().replace(",", ""));
                        if (costProductoToReturn > availableReturns) {
                            AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullProductList.this)
                                    .setTitle("Alerta")
                                    .setMessage("El monto de la devolución sobrepasa lo permitido")
                                    .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
                        } else {
                            DevolucionesFullProductList.this.editTxtDevFullTotalProductoToReturn.setText(totalProductToReturnOfThisInvoice + "");
                            DevolucionesFullProductList.this.txtDevFullTotalCostProductoToReturn.setText(setFormatMoney(costProductoToReturn));

                            if( DevolucionesFullProductList.this.NUM_INVOICE!=null )
                                DataBaseInterface.setNewDevolutionToProduct(DevolucionesFullProductList.this.NUM_INVOICE, product.getMarzamCode(), ""+(Integer.parseInt(product.getNumberProductsToReturn())-(Integer.parseInt(product.getNumberProductsToReturn())-numberProducts)));

                            product.setNumberProductsToReturn(numberProducts + "");
                            if( DevolucionesFullProductList.this.FROM_RETURN_LIST == null ){
                                DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().addProducts(product);
                            }else{
                                DevolucionesFullReturnsList.thiz.getDevolucionPendiente().addProducts(product);
                            }

                        }
                    }
                    DevolucionesFullProductList.this.productRowAdapter.notifyDataSetChanged();
                    return true;
                }
            });

        }
    }

    /**
     * Método para obtener la lista de productos con derecho a devolución de una factura.
     * @param invoice Factura a partir de la cual, en caso de ser requerida (depende del motivo de devolución seleccionado), se podrá obtener los productos.
     * @return Lista de productos.
     */
    private ArrayList<Product> getProductFromInvoice( String invoice ){

        //ArrayList<Product> list = DataBaseInterface.getProductsFromInvoice(this, invoice);
        ArrayList<Product> list;
        if( this.FROM_RETURN_LIST == null ){
            list = DataBaseInterface.getProductsFromInvoice(this, invoice);
        }else {
            list = DataBaseInterface.getProductsFromInvoice(this, invoice, DevolucionesFullReturnsList.thiz.getDevolucionPendiente());
        }
        return list;
    }

    /**
     * Método para obtener la lista de productos  con derecho a devolución.
     * @return Lista de productos.
     */
    private ArrayList<Product> getProductForDevolution(){

        ArrayList<Product> list;
        if( this.FROM_RETURN_LIST == null ){
            list = DataBaseInterface.getProductsForDevolution(this);
        }else {
            list = DataBaseInterface.getProductsForDevolution(this, DevolucionesFullReturnsList.thiz.getDevolucionPendiente());
        }

        return list;
    }



    /**
     * Inicializa la barra de búsqueda.
     */
    private void initSearchBar(){
        initEditTextSearch();
        initCancelButtonSearch();
        initStartSearchByBarCodeScan();
    }

    /**
     * Inicializa solo la el área del texto perteneciente a la barra de búsquedas.
     */
    private void initEditTextSearch(){
        this.txtInputSearch = (EditText)findViewById(R.id.idEditTxtDevFullInputSearh);
        this.txtInputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DevolucionesFullProductList.this.productRowAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Inicializa el botón para cancelar la búsqueda.
     */
    private void initCancelButtonSearch() {
        this.imgCancelSearch = (ImageButton) findViewById(R.id.idIconTxtDevFullCancelSearch);
        this.imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesFullProductList.this.txtInputSearch.setText("");
                DevolucionesFullProductList.this.hidenkeyboard(DevolucionesFullProductList.this.txtInputSearch);
            }
        });
    }

    /**
     * Inicializa el botón para iniciar el scanner de código de barras.
     */
    private void initStartSearchByBarCodeScan() {
        this.imgStartSearch = (ImageButton) findViewById(R.id.idIconTxtDevFullStartSearch);
        this.imgStartSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesFullProductList.this.executeSearchByBarCodeScan();
            }
        });
    }

    /**
     * Este método inicia el scanner de código de barras, en caso de que el device no cuente con una
     * app para este propósito, le propone al usuario descargar una aplicación.
     */
    private void executeSearchByBarCodeScan(){
        try{
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe){
            showDialog("No se encontró ningún scanner", "Descargar un scanner", "Si", "No").show();
        }

    }

    /**
     * Este método nuestra un alerta avisando al usuario que no cuenta con una app para scanner códigos de barras.
     * @param title Titulo de la alerta
     * @param message Mensaje de la alerta
     * @param buttonYes Mensaje de confirmación
     * @param buttonNo Mensaje para cancelar la acción
     * @return
     */
    private AlertDialog showDialog(CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(DevolucionesFullProductList.this);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    DevolucionesFullProductList.this.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    /**
     * Este método juega el papel de un callback. Recibe el resultado de escanear el código de barras.
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                DevolucionesFullProductList.this.txtInputSearch.setText(contents);
                DevolucionesFullProductList.this.txtInputSearch.setSelection(contents.length(), contents.length());
            }
        }
    }

    /**
     * Encargado de ocultar el teclado lógico
     */
    private void hidenkeyboard( TextView textView ) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
    }

    /**
     * Encargado de mostrar el teclado lógico
     */
    private void showkeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Método con el cual se finaliza la devolución de los productos.
     */
    private void finishReturn(){
        if( this.FROM_RETURN_LIST == null ){
            DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setAvalibleReturn( true );
        }else{
            DevolucionesFullReturnsList.thiz.getDevolucionPendiente().setAvalibleReturn( true );
        }

        DevolucionesFullInvoiceList.returnToMainMenu = true;
        String costFroThisReturn = DevolucionesFullProductList.this.txtDevFullTotalCostProductoToReturn.getText().toString().replace(",", "");
        if( this.FROM_RETURN_LIST == null ){
            DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setCostForThisReturn( costFroThisReturn );
        }else{
            DevolucionesFullReturnsList.thiz.getDevolucionPendiente().setCostForThisReturn( costFroThisReturn );
        }
        super.onBackPressed();
    }

    /**
     * Método para obtener  la "Disponibilidad de devolución".
     * @return Disponibilidad de devoluciones con formato requerido.
     */
    private String getAvalibleReturns(){
        Double avalibleReturns = Double.parseDouble(DataBaseInterface.getStrAvalibleReturns(this).replace(",", "").trim());

        if (DevolucionesFullProductList.this.FROM_RETURN_LIST != null) {
            avalibleReturns = avalibleReturns+Double.parseDouble( this.txtDevFullTotalCostProductoToReturn.getText().toString().replace(",","").trim());
        }
        return setFormatMoney(avalibleReturns);
    }

    /**
     * Método con el cual se obtiene la totalidad de productos devueltos.
     * @return
     */
    private String getTotalProductoToReturn( String ... folio ){

        //Simulador de base de datos
        double totalAvalibleReturns = DataBaseInterface.getTotalProductoToReturn( folio );
        return setFormatMoney(totalAvalibleReturns );
    }

    /**
     * Sobre escritura del método "onBackPressed".
     */
    @Override
    public void onBackPressed() {
        String strTotalProductToReturn = DevolucionesFullProductList.this.editTxtDevFullTotalProductoToReturn.getText() + "";
        int totalProductToReturnOfThisInvoice = new Integer(strTotalProductToReturn).intValue();

        String numberProductToReturn;
        if( this.FROM_RETURN_LIST == null ){
            numberProductToReturn = DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().getNumberProductToReturn();
        }else{
            numberProductToReturn = DevolucionesFullReturnsList.thiz.getDevolucionPendiente().getNumberProductToReturn();
        }
        if( totalProductToReturnOfThisInvoice==0 && numberProductToReturn==null) {
            if( this.FROM_RETURN_LIST == null ){
                DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().rebootFromProduct();
            }else{
                DevolucionesFullReturnsList.thiz.getDevolucionPendiente().rebootFromProduct();
            }
            super.onBackPressed();
        }else {
            String message = "Se descartarán los productos seleccionados";
            if (DevolucionesFullProductList.this.FROM_RETURN_LIST != null) {
                message = "Se descartarán los productos que no fueron guardados";
            }
            AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullProductList.this)
                    .setTitle("Aviso")
                    .setMessage(message)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if( DevolucionesFullProductList.this.FROM_RETURN_LIST == null ){
                                DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().rebootFromProduct();
                            }else{
                                DevolucionesFullReturnsList.thiz.getDevolucionPendiente().rebootFromProduct();
                            }
                            DevolucionesFullProductList.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }
    }

    /**
     * Da formato como moneda al parametro
     * @param toSetFormat
     * @return
     */
    public static String setFormatMoney( double toSetFormat ) {

        //Le damos formato al costo total de la devolución
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec = (DecimalFormat) nf;
        dec.setMaximumFractionDigits(2);
        dec.setMinimumFractionDigits(2);

        return dec.format(toSetFormat);
    }
}
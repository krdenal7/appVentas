package com.marzam.com.appventas.DevolucionesFull.CustomPrompt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.ecommerce.Promotion;
import com.marzam.com.appventas.DevolucionesFull.DataBaseInterface.DataBaseInterface;
import com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionesFullReturnsList;
import com.marzam.com.appventas.DevolucionesFull.DevolucionesFullMenuPrincipal;
import com.marzam.com.appventas.DevolucionesFull.Productos.DevolucionesFullProductList;
import com.marzam.com.appventas.DevolucionesFull.Productos.Product;
import com.marzam.com.appventas.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by lcabral on 09/04/2015.
 */
public class CustomPrompt extends Dialog implements View.OnClickListener{

    private Activity activity;
    private TextView txtProduct;
    private EditText txtProductAmount;
    private Button btnDevFullPromptReset0, btnDevFullPromptAdd1, btnDevFullPromptAdd2, btnDevFullPromptAdd5, btnDevFullPromptAdd10, btnDevFullPromptAccept;
    private ArrayAdapter<Product> myAdapter;
    private Product product;
    private TextView txtSetAvalibleReturn;
    private TextView txtTxtAvalibleReturn;
    private TextView txtSetAvalibleReturnSignPesos;


    private TextView txtSetProductCostTotal;
    private TextView txtSetCostProduct;

    private TextWatcher textWatcher;
    private boolean returnAvalible = true;

    //TextView pertenecientes a la vista de la "Lista de productos"
    private TextView txtViewTotalProductoToReturn;
    private TextView txtAvailableReturnsMain;
    private TextView txtCostReturnsMain;

    private int productTotalInInvoice;

    private static int numeroOrigunalProductoRetorno;

    /**
     * Constructor
     * @param activity Padre
     */
    public CustomPrompt( Activity activity ){
        super(activity);
        this.activity = activity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPrompt();
    }

    /**
     * Inicializa el Prompt
     */
    private void initPrompt(){
        setContentView( R.layout.devoluciones_full_prompt);
        this.txtProduct = (TextView) findViewById( R.id.idSetDevFullPromptProduct );
        this.txtSetCostProduct = (TextView) findViewById(R.id.idSetDevFullPromptCostProduct);
        this.txtSetAvalibleReturn = (TextView) findViewById( R.id.idSetDevFullPromptAvalibleReturn );
        this.txtTxtAvalibleReturn = (TextView) findViewById( R.id.idTxtDevFullPromptAvalibleReturn );
        this.txtSetAvalibleReturnSignPesos = (TextView) findViewById( R.id.idTxtDevFullPromptAvalibleReturnSignPesos );

        this.txtSetProductCostTotal = (TextView) findViewById( R.id.idSetDevFullPromptProductCostTotal);
        this.txtProductAmount = (EditText) findViewById( R.id.idSetDevFullPromptProductAmountTotal);
        this.btnDevFullPromptReset0 = (Button) findViewById( R.id.idtxtDevFullPromptReset0 );
        this.btnDevFullPromptAdd1 = (Button) findViewById( R.id.idDevFullPromptAdd1 );
        this.btnDevFullPromptAdd2 = (Button) findViewById( R.id.idDevFullPromptAdd2 );
        this.btnDevFullPromptAdd5 = (Button) findViewById( R.id.idDevFullPromptAdd5 );
        this.btnDevFullPromptAdd10 = (Button) findViewById( R.id.idDevFullPromptAdd10 );
        this.btnDevFullPromptAccept = (Button) findViewById( R.id.idDevFullPromptAccept );

        setClicEvent();
    }

    /**
     * Inicializa el campo de se indica la cantidad de productos a devolver.
     */
    private void initProductAmount(){

        CustomPrompt.this.textWatcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                CustomPrompt.this.txtProductAmount.removeTextChangedListener(CustomPrompt.this.textWatcher );

                String strAmountReturn = s.toString().trim();
                int intAmountReturn = (strAmountReturn.length() > 0) ? Integer.parseInt(strAmountReturn) : 0;

                if (intAmountReturn > CustomPrompt.this.productTotalInInvoice) {
                    intAmountReturn = CustomPrompt.this.productTotalInInvoice;

                    String message;
                    if (CustomPrompt.this.productTotalInInvoice == 1)
                        message = "Solo \"" + intAmountReturn + "\" producto disponible para devolución";
                    else
                        message = "Solo \"" + intAmountReturn + "\" productos disponible para devolución";

                    Toast toast = Toast.makeText(CustomPrompt.this.activity, message, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 80);
                    toast.show();
                }

                strAmountReturn = intAmountReturn + "";
                CustomPrompt.this.txtProductAmount.setText( strAmountReturn );

                if (strAmountReturn.length() < 0) {
                    strAmountReturn = "0";
                }

                CustomPrompt.this.addNewReturnOfProduct( strAmountReturn );

                CustomPrompt.this.txtProductAmount.setText(strAmountReturn);
                CustomPrompt.this.txtProductAmount.setSelection(strAmountReturn.length(), strAmountReturn.length());

                CustomPrompt.this.txtProductAmount.addTextChangedListener(CustomPrompt.this.textWatcher);
            }
        };
        CustomPrompt.this.txtProductAmount.addTextChangedListener(CustomPrompt.this.textWatcher);
    }

    /**
     * Inicializa los eventos de clic
     */
    private void setClicEvent(){
        this.btnDevFullPromptReset0.setOnClickListener( this );
        this.btnDevFullPromptAdd1.setOnClickListener( this );
        this.btnDevFullPromptAdd2.setOnClickListener( this );
        this.btnDevFullPromptAdd5.setOnClickListener( this );
        this.btnDevFullPromptAdd10.setOnClickListener(this);
        this.btnDevFullPromptAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CustomPrompt.this.returnAvalible) {
                    AlertDialog alertDialog = new AlertDialog.Builder(CustomPrompt.this.activity)
                            .setTitle("Alerta")
                            .setMessage("El monto de la devolución sobrepasa lo permitido")
                            .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
                } else {
                    String strAmountReturnProduct = CustomPrompt.this.txtProductAmount.getText().toString().trim();
                    String strCostResultForThisReturn = CustomPrompt.this.txtSetProductCostTotal.getText().toString().trim();

                    if (strAmountReturnProduct.length() == 0) strAmountReturnProduct = "0";
                    /*if (*/
                    CustomPrompt.this.setNewReturnOfProduct(strAmountReturnProduct, strCostResultForThisReturn);/*) {*/

                    CustomPrompt.this.product.setNumberProductsToReturn(strAmountReturnProduct);
                    CustomPrompt.this.product.setIntNumberProductsToReturn(Integer.parseInt(strAmountReturnProduct));
                    CustomPrompt.this.myAdapter.notifyDataSetChanged();

                    if (DevolucionesFullProductList.FROM_RETURN_LIST == null) {
                        if (strAmountReturnProduct.compareTo("0") == 0) {
                            DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().removeProducts(product);
                        } else {
                            DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().addProducts(product);
                        }
                    } else {
                        if (strAmountReturnProduct.compareTo("0") == 0) {
                            DevolucionesFullReturnsList.thiz.getDevolucionPendiente().removeProducts(product);
                        } else {
                            DevolucionesFullReturnsList.thiz.getDevolucionPendiente().addProducts(product);
                        }
                    }

                    if (DevolucionesFullProductList.thiz.NUM_INVOICE != null) {
                        if (DevolucionesFullProductList.thiz.NUM_INVOICE.trim().compareTo("0")!=0) {
                            DataBaseInterface.setNewDevolutionToProduct(DevolucionesFullProductList.thiz.NUM_INVOICE, product.getMarzamCode(), "" + (CustomPrompt.numeroOrigunalProductoRetorno - (CustomPrompt.numeroOrigunalProductoRetorno - Integer.parseInt(strAmountReturnProduct))));//valuesH[10] = Factura, parameterD[ 1] = "Producto", parameterD[ 2] = "Cantidad"
                        }
                    }

                    CustomPrompt.this.dismiss();
                    //}
                }

                /*String strAmountReturn = CustomPrompt.this.txtProductAmount.getText().toString().trim();
                if (CustomPrompt.this.setNewReturnOfProduct(strAmountReturn)) {

                    CustomPrompt.this.product.setNumberProductsToReturn(strAmountReturn);
                    CustomPrompt.this.product.setIntNumberProductsToReturn(Integer.parseInt(strAmountReturn));
                    CustomPrompt.this.myAdapter.notifyDataSetChanged();
                }
                CustomPrompt.this.dismiss();*/
            }
        });
    }

    /**
     * Método encargado de atender los clic
     * @param view Elemento que genera el evento
     */
    @Override
    public void onClick(View view) {

        String strAmountReturn = this.txtProductAmount.getText().toString().trim();
        if (strAmountReturn.length() == 0) strAmountReturn = "0";

        if (view == this.btnDevFullPromptReset0) {
            strAmountReturn = "";
        } else if (view == this.btnDevFullPromptAdd1) {
            strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "1")).toString();
        } else if (view == this.btnDevFullPromptAdd2) {
            strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "2")).toString();
        } else if (view == this.btnDevFullPromptAdd5) {
            strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "5")).toString();
        } else if (view == this.btnDevFullPromptAdd10) {
            strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "10")).toString();
        }
        if (strAmountReturn.length() > 3)
            strAmountReturn = "999";

        //this.prom

        this.txtProductAmount.setText(strAmountReturn);
    }

    /**
     * Método para indicar el producto del cual se extraerán los datos para el promp
     * @param product Objeto Product
     */
    public void setProduct( ArrayAdapter<Product> myAdapter, Product product, TextView txtViewTotalProductoToReturn, TextView txtAvailableReturns, TextView txtCostProductoToReturn ){
        this.myAdapter = myAdapter;
        this.product = product;
        this.txtViewTotalProductoToReturn = txtViewTotalProductoToReturn;
        this.txtAvailableReturnsMain = txtAvailableReturns;
        this.txtCostReturnsMain = txtCostProductoToReturn;

        this.productTotalInInvoice = (new Integer(this.product.getNumberProducts())).intValue();
        this.txtProduct.setText( this.product.getName() );
        if( this.txtAvailableReturnsMain.getVisibility() == View.INVISIBLE ){
            this.txtSetAvalibleReturn.setVisibility( View.INVISIBLE );
            this.txtTxtAvalibleReturn.setVisibility( View.INVISIBLE );
            this.txtSetAvalibleReturnSignPesos.setVisibility( View.INVISIBLE );

            this.txtSetAvalibleReturn.setTextSize(1);
            this.txtTxtAvalibleReturn.setTextSize(1);
            this.txtSetAvalibleReturnSignPesos.setTextSize(1);

        }
        this.txtSetAvalibleReturn.setText( this.txtAvailableReturnsMain.getText() );
        this.txtSetProductCostTotal.setText(this.txtCostReturnsMain.getText());

        String numberProductsToReturn = this.product.getNumberProductsToReturn();
        CustomPrompt.numeroOrigunalProductoRetorno = Integer.parseInt(numberProductsToReturn.trim());
        numberProductsToReturn = numberProductsToReturn.compareTo("0")==0?"":numberProductsToReturn;
        this.txtProductAmount.setText( numberProductsToReturn );
        this.txtProductAmount.setSelection(numberProductsToReturn.length(), numberProductsToReturn.length());

        NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec=(DecimalFormat)nf;
        dec.setMaximumFractionDigits(2);
        dec.setMinimumFractionDigits(2);
        String strCostproduct = dec.format(this.product.getPrice());

        this.txtSetCostProduct.setText(strCostproduct);

        initProductAmount();
    }

    /**
     * Método que para despedir el promp
     */
    @Override
    public void dismiss() {
        hidenkeyboard(this.txtProductAmount );
        super.dismiss();
    }

    private void setNewReturnOfProduct( String strAmountReturn, String strCostResultForThisReturn ){

        //Cantidad total de devoluciones de todos los productos que se encuentran en dicha factura
        int totalProductoToReturnField = Integer.parseInt(this.txtViewTotalProductoToReturn.getText() + "");

        totalProductoToReturnField = totalProductoToReturnField - this.product.getIntNumberProductsToReturn();
        totalProductoToReturnField = totalProductoToReturnField + Integer.parseInt(strAmountReturn);
        this.txtViewTotalProductoToReturn.setText(totalProductoToReturnField+"");

        //Etiqueta donde se plasma la cantidad en valor de la devolucion acomulada
        this.txtCostReturnsMain.setText(strCostResultForThisReturn);
    }

    private void addNewReturnOfProduct( String strAmountReturn ){
        //Etiqueta donde se plasma la devolucion disponible para este agente
        double totalCostProductoToReturn = Double.parseDouble(this.txtSetAvalibleReturn.getText().toString().replace(",", ""));

        //Valor de la productos ya registrados del producto
        double costProductsToReturnRegistry = Double.parseDouble(this.product.getNumberProductsToReturn())*this.product.getPrice();;

        //Etiqueta donde se plasma la cantidad en valor de la devolucion acomulada
        double productCostTotal = Double.parseDouble(this.txtCostReturnsMain.getText().toString().replace(",", ""));

        //Quitamos de lo acomulado el precio de la devolucion del producto seleccionado
        productCostTotal = productCostTotal - costProductsToReturnRegistry;

        //Agregamos a lo ya acomulado solo lo que el usuario quiere regresar de dicho producto
        productCostTotal = productCostTotal +( Double.parseDouble(strAmountReturn)*this.product.getPrice() );

        NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec=(DecimalFormat)nf;
        dec.setMaximumFractionDigits(2);
        dec.setMinimumFractionDigits(2);
        String strCostResultForThisReturn = dec.format(productCostTotal);


        if(productCostTotal>totalCostProductoToReturn){
            this.txtSetProductCostTotal.setTextColor(Color.parseColor("#FF0000"));
            this.txtSetProductCostTotal.setTypeface(null, Typeface.BOLD);
            CustomPrompt.this.returnAvalible = false;
        }else{
            this.txtSetProductCostTotal.setTextColor(Color.parseColor("#000000"));
            this.txtSetProductCostTotal.setTypeface(null, Typeface.NORMAL);
            CustomPrompt.this.returnAvalible = true;
        }

        txtSetProductCostTotal.setText(strCostResultForThisReturn);
    }

    /**
     * Método para ocultar el teclado
     */
    private void hidenkeyboard( TextView textView ) {
        InputMethodManager imm = (InputMethodManager)this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
    }
}
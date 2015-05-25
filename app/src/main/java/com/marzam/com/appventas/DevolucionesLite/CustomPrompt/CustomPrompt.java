package com.marzam.com.appventas.DevolucionesLite.CustomPrompt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marzam.com.appventas.DevolucionesLite.DevolucionesLite;
import com.marzam.com.appventas.DevolucionesLite.ProductList.Product;
import com.marzam.com.appventas.R;

/**
 * Created by lcabral on 09/04/2015.
 */
public class CustomPrompt extends Dialog implements View.OnClickListener{

    private Activity activity;
    private Dialog dialog;
    private TextView txtProduct;
    private EditText txtProductAmount;
    private Button btnDevLitePromptReset0, btnDevLitePromptAdd1, btnDevLitePromptAdd2, btnDevLitePromptAdd5, btnDevLitePromptAdd10, btnDevLitePromptAccept;
    private View viewItem;
    private int position;
    private boolean changeAmount;
    private Product product;

    /**
     * Constructor
     * @param activity Padre
     */
    public CustomPrompt( Activity activity ){
        super( activity );
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
        setContentView( R.layout.devoluciones_lite_prompt);
        this.txtProduct = (TextView) findViewById( R.id.idSetDevLitePromptProduct );
        this.txtProductAmount = (EditText) findViewById( R.id.idSetDevLitePromptProductAmount );
        this.btnDevLitePromptReset0 = (Button) findViewById( R.id.idtxtDevLitePromptReset0 );
        this.btnDevLitePromptAdd1 = (Button) findViewById( R.id.idDevLitePromptAdd1 );
        this.btnDevLitePromptAdd2 = (Button) findViewById( R.id.idDevLitePromptAdd2 );
        this.btnDevLitePromptAdd5 = (Button) findViewById( R.id.idDevLitePromptAdd5 );
        this.btnDevLitePromptAdd10 = (Button) findViewById( R.id.idDevLitePromptAdd10 );
        this.btnDevLitePromptAccept = (Button) findViewById( R.id.idDevLitePromptAccept );

        setClicEvent();
    }

    /**
     * Inicializa los eventos de clic
     */
    private void setClicEvent(){
        this.btnDevLitePromptReset0.setOnClickListener( this );
        this.btnDevLitePromptAdd1.setOnClickListener( this );
        this.btnDevLitePromptAdd2.setOnClickListener( this );
        this.btnDevLitePromptAdd5.setOnClickListener( this );
        this.btnDevLitePromptAdd10.setOnClickListener( this );
        this.btnDevLitePromptAccept.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strAmountReturn = CustomPrompt.this.txtProductAmount.getText().toString().trim();
                int intAmountReturn = (strAmountReturn.length() > 0)? Integer.parseInt(strAmountReturn) : 0;
                strAmountReturn = intAmountReturn+"";
                if( strAmountReturn.length() > 0 && intAmountReturn>0 ) {
                    if( CustomPrompt.this.product.isPackage && strAmountReturn.length()!=0 )
                        CustomPrompt.this.product.setAmountPackages( strAmountReturn );
                    else if( CustomPrompt.this.product.isProduct && strAmountReturn.length()!=0 )
                        CustomPrompt.this.product.setAmountProductReturn( strAmountReturn );

                    if( CustomPrompt.this.changeAmount ){
                        if( CustomPrompt.this.product.isProduct )
                            DevolucionesLite.changeProductSumary( false ,CustomPrompt.this.product.amountProductReturn, CustomPrompt.this.position);
                        else if( CustomPrompt.this.product.isPackage )
                            DevolucionesLite.changeProductSumary( true ,CustomPrompt.this.product.amountPackages, CustomPrompt.this.position);

                        CustomPrompt.this.dismiss();
                        Toast.makeText(CustomPrompt.this.activity, "Cantidad del producto modificado", Toast.LENGTH_SHORT).show();
                    } else {
                        DevolucionesLite.addProductSumary(CustomPrompt.this.product);
                        CustomPrompt.this.dismiss();
                        Toast.makeText(CustomPrompt.this.activity, "Producto agregado", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CustomPrompt.this.activity, "Inserte alguna cantidad", Toast.LENGTH_SHORT).show();
                }
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
        if( strAmountReturn.length() == 0 ) strAmountReturn = "0";

        if (view == this.btnDevLitePromptReset0){
            strAmountReturn = "";
        }else if (view == this.btnDevLitePromptAdd1){
            strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "1")).toString();
        }else if (view == this.btnDevLitePromptAdd2){
            strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "2")).toString();
        }else if (view == this.btnDevLitePromptAdd5){
            strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "5")).toString();
        }else if (view == this.btnDevLitePromptAdd10){
            strAmountReturn = (NumeroEnorme.suma(strAmountReturn, "10")).toString();
        }
        this.txtProductAmount.setText( strAmountReturn );
        this.txtProductAmount.setSelection(strAmountReturn.length(), strAmountReturn.length());
    }

    /**
     * Método que para despedir el promp
     */
    @Override
    public void dismiss() {
        hidenkeyboard();
        super.dismiss();
    }

    /**
     * Método para ocultar el teclado
     */
    private void hidenkeyboard() {
        InputMethodManager inputManager = (InputMethodManager)this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Método para indicar el producto del cual se extraerán los datos para el promp
     * @param product Objeto Product
     * @param viewItem Deprecate
     */
    public void setProduct( Product product, View viewItem ){
        this.product = product;
        this.txtProduct.setText( this.product.name );
        this.viewItem = viewItem;
    }

    /**
     * Método para establecer el producto al que se le cambiara la cantidad de la devolución
     * @param product Onjeto Product a cambiar la cantidad e la devolución
     * @param position Posición del producto en la lista de resumen
     */
    public void setProductChangeAmount( Product product, int position ){
        this.product = product;

        if( product.isProduct ) {
            this.txtProduct.setText( this.product.name );
            this.txtProductAmount.setText(this.product.amountProductReturn);
            this.txtProductAmount.setSelection(this.product.amountProductReturn.length(), this.product.amountProductReturn.length());
        }
        else if( product.isPackage ) {
            this.txtProduct.setText( "BULTO" );
            this.txtProductAmount.setText(this.product.amountPackages);
            this.txtProductAmount.setSelection(this.product.amountPackages.length(), this.product.amountPackages.length());
        }

        this.position = position;
        this.changeAmount = true;
    }
}

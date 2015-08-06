package com.marzam.com.appventas.DevolucionesFull;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marzam.com.appventas.DevolucionesFull.DataBaseInterface.DataBaseInterface;
import com.marzam.com.appventas.DevolucionesFull.Interfaz.ScreenChilds;

import com.marzam.com.appventas.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by lcabral on 02/06/2015.
 */
public class DevolucionesFullIndicadores implements ScreenChilds{


    // Contenedor de este layout.
    private Activity context;
    // Layout de indicadores a mostrar.
    private LinearLayout layoutIndicators;
    //Botón para agregar nueva devolución.
    private Button btnDevFullNewReturn;
    //Botón para visualizar el historial
    private Button btnDevReturnsConsult;
    //Presupuesto disponible para devoluciones.
    private TextView txtEstimateReturns;
    //Disponible para devoluciones resultante; descontando los folios dentro del presupuesto.
    private TextView txtAvailableReturns;
    //Promedio de ventas.
    private TextView txtAverageSales;
    //Folios dentro del presupuesto.
    private TextView txtFoliosInEstimate;
    //Folios en merma.
    private TextView txtFoliosInMerma;
    //Folios no aceptados.
    //private TextView txtFoliosNoAccepted;
    //Folios fuera del presupuesto.
    private TextView txtFoliosOutEstimation;
    //Porsentaje de devolución asignado
    private TextView txtPercentageAllocated;




    /**
     * Constructor
     * @param layout Id del layaout de "Indicadores".
     * @param context Activity donde se contendrá este layout.
     */
    public DevolucionesFullIndicadores(LinearLayout layout, Activity context){
        this.layoutIndicators = layout;
        this.context = context;
        String NAME_PHARMACY = DataBaseInterface.getNameClient( context, DataBaseInterface.getIDCLiente(context) );
        this.context.setTitle("Devoluciones - "+NAME_PHARMACY);
        initInterfaz();
    }

    /**
     * * Inicializador de la "Interfaz".
     */
    @Override
    public void initInterfaz() {
        this.initTxtAvailableReturns();
    }

    /**
     * Inicializador de los "Indicadores".
     */
    private void initTxtAvailableReturns(){
        this.txtEstimateReturns = (TextView) this.layoutIndicators.findViewById( R.id.idTxtDevFullReturnsIndicadoresEstimateReturns );
        this.txtPercentageAllocated = (TextView) this.layoutIndicators.findViewById( R.id.idTxtDevFullReturnsIndicadoresPercentageAllocated );
        this.txtAvailableReturns = (TextView) this.layoutIndicators.findViewById( R.id.idTxtDevFullReturnsIndicadoresAvailableReturns );
        this.txtAverageSales = (TextView) this.layoutIndicators.findViewById( R.id.idTxtDevFullReturnsIndicadoresAverageSales );
        this.txtFoliosInEstimate = (TextView) this.layoutIndicators.findViewById( R.id.idTxtDevFullReturnsIndicadoresFoliosInEstimate );
        this.txtFoliosInMerma = (TextView) this.layoutIndicators.findViewById( R.id.idTxtDevFullReturnsIndicadoresFoliosInMerma );
        //this.txtFoliosNoAccepted = (TextView) this.layoutIndicators.findViewById( R.id.idTxtDevFullReturnsIndicadoresFoliosNoAccepted );
        this.txtFoliosOutEstimation = (TextView) this.layoutIndicators.findViewById( R.id.idTxtDevFullReturnsIndicadoresFoliosOutEstimation );

        //Simulador de base de datos---------------------------------------------------
        getEstimateReturns();
        getPercentageAllocated();
        getAverageSales();
        getFoliosInEstimate();
        getFoliosInMerma();
        //getFoliosNoAccepted();
        getFoliosOutEstimation();
        getAvalibleReturns();
        setValueProgressBarReturnsAvailable();
        //------------------------------------------------------------------------------
    }

    private void setValueProgressBarReturnsAvailable(){
        double estimateReturn = Double.parseDouble(this.txtEstimateReturns.getText().toString().replace(",", ""));
        double foliosInEstimate = Double.parseDouble( this.txtFoliosInEstimate.getText().toString().replace(",","") );
        double availableReturns = estimateReturn - foliosInEstimate;

        DevolucionesFullConteiner.thiz.setValueProgressBarReturnsAvailable( (int)(availableReturns*100/estimateReturn) );
    }
    /**
     * Inicializador de los botones.
     */
    @Override
    public void initButtons(){

        this.initButtonAddReturn();
        this.initButtonReturnsConsult();
    }

    /**
     * Remueve los eventos de los botones.
     */
    @Override
    public void removeListenerButtons(){
        this.btnDevFullNewReturn.setOnClickListener(null);
    }

    /**
     * Inicializador del Botón para realizar una "Nueva devolución".
     */
    private void initButtonAddReturn(){
        this.btnDevFullNewReturn = (Button)this.layoutIndicators.findViewById(R.id.idBtnDevFullNewReturn);
        this.btnDevFullNewReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DevolucionesFullConteiner) DevolucionesFullIndicadores.this.context).initCallbackMainMenuScreen();
                //Toast.makeText(DevolucionesFullIndicadores.this.context, "Cantidad del producto modificado", Toast.LENGTH_SHORT).show();
                //Intent intent=new Intent(DevolucionesFullConteiner.this, DevolucionesFullMenuPrincipal.class);
                //startActivity(intent);
            }
        });
    }

    /**
     * Inicializador del Botón para el "Historial de las devoluciones".
     */
    private void initButtonReturnsConsult (){
        this.btnDevReturnsConsult = (Button)this.layoutIndicators.findViewById(R.id.idBtnDevFullReturnsConsult);
        this.btnDevReturnsConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DevolucionesFullConteiner) DevolucionesFullIndicadores.this.context).initCallbackReturnsConsult();
                //Toast.makeText(DevolucionesFullIndicadores.this.context, "Cantidad del producto modificado", Toast.LENGTH_SHORT).show();
                //Intent intent=new Intent(DevolucionesFullConteiner.this, DevolucionesFullMenuPrincipal.class);
                //startActivity(intent);
            }
        });
    }

    /**
     * Método para obtener de la base de datos el monto del  "Presupuesto disponible para las devoluciónes".
     */
    private void getEstimateReturns(){
        this.txtEstimateReturns.setText(DataBaseInterface.getStrEstimateReturns(this.context));
    }

    /**
     * Método para obtener de la base de datos el porcentaje de la  "devolcuión asignada".
     */
    private void getPercentageAllocated(){
        this.txtPercentageAllocated.setText(DataBaseInterface.getStrPercentageAllocated(this.context));
    }
    /**
     * Método para obtener  la "Disponibilidad de devolución".
     */
    private void getAvalibleReturns(){
        this.txtAvailableReturns.setText(DataBaseInterface.getStrAvalibleReturns(this.context));
    }

    /**
     * Método para obtener de la base de datos el "Promedio de ventas".
     */
    private void getAverageSales(){
        this.txtAverageSales.setText( DataBaseInterface.getStrAverageSales(this.context));
    }

    /**
     * Método para obtener de la base de datos de los "Folios dentro del presupuesto".
     */
    private void getFoliosInEstimate(){
        this.txtFoliosInEstimate.setText( DataBaseInterface.getStrFoliosInEstimate(this.context));
    }

    /**
     * Método para obtener de la base de datos de los "Folios autorizados con mermao".
     */
    private void getFoliosInMerma(){
        this.txtFoliosInMerma.setText(DataBaseInterface.getStrFoliosInMerma(this.context));
    }

    /**
     * Método para obtener de la base de datos de los "Folios no aceptados".
     */
    //private void getFoliosNoAccepted(){
    //    this.txtFoliosNoAccepted.setText(DataBaseInterface.getStrFoliosNoAccepted(this.context));
    //}

    /**
     * Método para obtener de la base de datos de los "Folios no aceptados".
     */
    private void getFoliosOutEstimation(){
        this.txtFoliosOutEstimation.setText(DataBaseInterface.getStrFoliosOutEstimation(this.context));
    }

    private static String setFormatMoney( double availableReturns) {

        //Le damos formato al costo total de la devolución
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec = (DecimalFormat) nf;
        dec.setMaximumFractionDigits(2);
        dec.setMinimumFractionDigits(2);

        return dec.format(availableReturns);
    }




}

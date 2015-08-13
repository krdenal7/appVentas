package com.marzam.com.appventas.DevolucionesFull.PrepareSendingData;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.marzam.com.appventas.DataBase.DataBase;
import com.marzam.com.appventas.DevolucionesFull.DataBaseInterface.DataBaseInterface;
import com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionPendiente.DevolucionPendiente;
import com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionesFullReturnsList;
import com.marzam.com.appventas.DevolucionesFull.Productos.DevolucionesFullProductList;
import com.marzam.com.appventas.DevolucionesFull.Productos.Product;
import com.marzam.com.appventas.WebService.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by lcabral on 14/04/2015.
 */
public class PrepareSendingData {

    //Data Header
    private String hFolio;
    private String hTipoFolio;
    private String hEsdatoFolio;
    private String hConsumoPresupuesto;
    private String hIdCliente;
    private String hClaveAgente;
    private String hTotalBultos;
    private String hImporteTotalAproximado;
    private String hFechaCapura;
    private String hHoraCapura;
    private String hFactura;
    private String hStatusIBS;
    private String hReferenciaAutorizacion;
    private String hHandlerAutorizacion;
    private String hConsecutivoCaptura;
    private String hEstadoTransmicion;
    private String hIdStatus;
    private String hStatusAutorizacion;
    private String hMotivoSolicitud;
    private String hMotivoAprobado;
    private String hHoraRecibido;

    //Data Detail
    private String dFolio;
    private String dTipoFolio;
    private String dProducto;//(producto)
    private String dIdCliente;
    private String dRepresentante;
    private String dCantidad;
    private String dPrecioFarmacia;
    private String dDescuentoComercial;
    private String dPorcentajeBonificacion;
    private String dImporteBruto;
    private String dImporteAproximado;
    private String dFactura;
    private String dFolioNotaCredito;
    private String dCantidadAprobada;
    private String dImporteAprobado;

    private String dConsecutivoCaptura;
    private String dStatusTransmitido;

    private JSONArray jsonArrayHeader;
    private JSONArray jsonArrayDetail;

    private static String NAME_METHOP = "TransmiteDevoluciones2";
    private static String NAME_PROPERTY_HEADER = "jencabezado";
    private static String NAME_PROPERTY_DETAIL = "jdetalle";

    //DataBase
    private DataBaseInterface dataBaseInterface;
    private Activity context;


    private static PrepareSendingData thiz;

    private DevolucionPendiente pendingReturn;


    /**
     * Constructor
     * @param context
     * @param pendingReturn
     */
    public PrepareSendingData(Activity context, DevolucionPendiente pendingReturn){
        this.context = context;
        this.pendingReturn = pendingReturn;
        thiz = this;
        makeJSON();

        String jsonHeader = this.jsonArrayHeader.toString();
        String jsonDetail = this.jsonArrayDetail.toString();



        JSONObject jsonObjectHeader = null;
        /*try {
            jsonObjectHeader = this.jsonArrayHeader.getJSONObject(0);
            jsonObjectHeader.put("estadoTransmicion", "T");//T
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        //this.saveOnDataBase();

        //progressDialog = ProgressDialog.show(this.context, "Enviando", "Por favor espere...", true, false);
        //WebServices ws = new WebServices();
        //ws.sendData(this.context, PrepareSendingData.NAME_METHOP, this.NAME_PROPERTY_HEADER, this.jsonArrayHeader.toString(), this.NAME_PROPERTY_DETAIL, this.jsonArrayDetail.toString());
    }

    /**
     * Método que se encarga de construir los json de las devoluciones
     */
    public void makeJSON(){
        this.jsonArrayHeader = new JSONArray();
        this.jsonArrayDetail = new JSONArray();
        JSONObject jheader = null, jdetail = null;

        getDataHeader();
        jheader = makeJsonHeader();
        this.jsonArrayHeader.put(jheader);

        ArrayList<Product> products = this.pendingReturn.getProducts();
        for( int i=0; i<products.size(); i++ ){
            Product product = products.get(i);
            jdetail = null;

            getDataDetail(product, i+1);
            jdetail = makeJsonDetail();
            this.jsonArrayDetail.put(jdetail);
        }
    }

    /**
     * Inicializa los datos correspondientes al json del header
     */
    private void getDataHeader(){
        this.hFolio = this.pendingReturn.getFolioForThisReturn();
        this.hTipoFolio = this.pendingReturn.getTypeForThisReturn();
        this.hEsdatoFolio = this.pendingReturn.getStatusFolio();
        this.hConsumoPresupuesto = this.pendingReturn.getAffectConsummation();
        this.hIdCliente = this.pendingReturn.getIDClient();
        this.hClaveAgente = this.pendingReturn.getClaveAgente();
        this.hTotalBultos = this.pendingReturn.getNumPackages();
        this.hImporteTotalAproximado = this.pendingReturn.getCostForThisReturn();
        this.hFechaCapura = this.pendingReturn.getDateThatThisReturnWasSave();
        this.hHoraCapura = this.pendingReturn.getTimeThatThisReturnWasSave();
        this.hFactura = this.pendingReturn.getNumberInvoiceToReturn()==null?"0":this.pendingReturn.getNumberInvoiceToReturn();
        this.hStatusIBS = this.pendingReturn.getStatusIBS();
        this.hReferenciaAutorizacion = this.pendingReturn.getFolioForThisReturn();
        this.hHandlerAutorizacion =  this.pendingReturn.getHandlerAutorizacion();
        this.hConsecutivoCaptura = this.pendingReturn.getConsecutivoCaptura();
        this.hEstadoTransmicion = "T";//this.pendingReturn.getStatusTransmit();
        this.hIdStatus = this.pendingReturn.getIdStatus();
        this.hStatusAutorizacion = this.pendingReturn.getStatusAutorizacion();
        this.hHoraRecibido = "";//this.pendingReturn.getTimeTransmission(); //Se llena en el servidor
        this.hMotivoSolicitud = this.pendingReturn.getReasonReturnSelected().trim().split(" ")[0];
        this.hMotivoAprobado = "";
    }

    /**
     * Método para construir el json del header
     * @return Objeto JSON  del header
     */
    private JSONObject makeJsonHeader(){
        JSONObject jheader = new JSONObject();
        try {
            jheader.put("folio", this.hFolio);
            jheader.put("tipoFolio", this.hTipoFolio);
            jheader.put("esdatoFolio", this.hEsdatoFolio);
            jheader.put("consumoPresupuesto", this.hConsumoPresupuesto);
            jheader.put("idCliente", this.hIdCliente);
            jheader.put("idClaveAgente", this.hClaveAgente);
            jheader.put("totalBultos", this.hTotalBultos);
            jheader.put("importeTotalAproximado", this.hImporteTotalAproximado);
            jheader.put("fechaCapura", this.hFechaCapura);
            jheader.put("horaCapura", this.hHoraCapura);
            jheader.put("factura",hFactura);
            jheader.put("statusIBS", this.hStatusIBS);

            jheader.put("consecutivoCaptura", this.hConsecutivoCaptura);
            jheader.put("estadoTransmicion", this.hEstadoTransmicion);
            jheader.put("idStatus", this.hIdStatus);
            jheader.put("statusAutorizacion", this.hStatusAutorizacion);
            jheader.put("horaRecibido", this.hHoraRecibido);
            jheader.put("motivoSolicitud",this.hMotivoSolicitud);
            jheader.put("motivoAprobado",this.hMotivoAprobado);

            jheader.put("referenciaAutorizacion", "");
            jheader.put("handlerAutorizacion", "");

            String[] necesitaAutorizacion = DataBaseInterface.thisReasonNeedAuthorization(this.context, this.hMotivoSolicitud);
            if(necesitaAutorizacion[0].trim().toUpperCase().compareTo("Y")==0){
                jheader.put("referenciaAutorizacion", this.hReferenciaAutorizacion);
                jheader.put("handlerAutorizacion", this.hHandlerAutorizacion);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jheader;
    }

    /**
     * Inicializa los datos correspondientes al json del detalle
     * @param product Producto a extraer los datos
     */
    private void getDataDetail( Product product, int intex){
        this.dFolio = this.hFolio;
        this.dProducto = product.getMarzamCode();
        this.dCantidad = product.getNumberProductsToReturn();
        this.dPrecioFarmacia = product.getPrice()+"";
        this.dDescuentoComercial = this.pendingReturn.getDiscountByClient();
        this.dPorcentajeBonificacion = this.pendingReturn.getPercentageBonus();
        this.dImporteBruto = ""+( Double.parseDouble(this.dPrecioFarmacia) * Double.parseDouble(this.dCantidad) );
        this.dImporteAproximado = ""+((Double.parseDouble(this.dImporteBruto)-(Double.parseDouble(this.dImporteBruto)*(Double.parseDouble( this.dDescuentoComercial )/100)))*(Double.parseDouble(this.dPorcentajeBonificacion)/100));
        this.dConsecutivoCaptura = intex+"";
        this.dStatusTransmitido = "T";

        /*this.dTipoFolio = this.hTipoFolio;
        this.dIdCliente = this.hIdCliente;
        this.dRepresentante = this.hRepresentante;
        this.dFactura = this.pendingReturn.getNumberInvoiceToReturn()==null?"0":this.pendingReturn.getNumberInvoiceToReturn();
        this.dFolioNotaCredito = "";
        this.dCantidadAprobada = "";
        this.dImporteAprobado = "";*/
    }

    /**
     * Método para construir el json de los detalles
     * @return Objeto JSON  del detalle
     */
    private JSONObject makeJsonDetail(){
        JSONObject jdetail = new JSONObject();
        try {

            jdetail.put("folio",dFolio);
            jdetail.put("producto",dProducto);//(producto)
            jdetail.put("cantidad",dCantidad);
            jdetail.put("precioFarmacia",dPrecioFarmacia);
            jdetail.put("descuentoComercial",dDescuentoComercial);
            jdetail.put("porcentajeBonificacion",dPorcentajeBonificacion);
            jdetail.put("importeBruto",dImporteBruto);
            jdetail.put("importeAproximado",dImporteAproximado);
            jdetail.put("consecutivoCaptura",dConsecutivoCaptura);
            jdetail.put("statusTransmitido",dStatusTransmitido);

            /*jdetail.put("idCliente",dIdCliente);
            jdetail.put("representante",dRepresentante);
            jdetail.put("porcentajeBonificacion",dPorcentajeBonificacion);
            jdetail.put("factura",dFactura);
            jdetail.put("folioNotaCredito",dFolioNotaCredito);
            jdetail.put("cantidadAprobada",dCantidadAprobada);
            jdetail.put("importeAprobado",dImporteAprobado);*/
        } catch (JSONException e) {
                e.printStackTrace();
        }
        return jdetail;
    }

    public void saveOnDataBase( boolean ... isUpdate ){
        boolean successHeader = false;

        String[] parameterH = new String[21];
        String[] valuesH = new String[21];

        parameterH[ 0] = "FolioDevolucion";
        parameterH[ 1] = "TipoFolio";
        parameterH[ 2] = "EstadoFolio";
        parameterH[ 3] = "ConsumoPresupuesto";
        parameterH[ 4] = "Cliente";
        parameterH[ 5] = "clave_agente";
        parameterH[ 6] = "TotalBultos";
        parameterH[ 7] = "ImporteTotalAprox";
        parameterH[ 8] = "FechaCaptura";
        parameterH[ 9] = "HoraCaptura";
        parameterH[10] = "Factura";
        parameterH[11] = "EstadoIBS";
        parameterH[12] = "ReferenciaAut";
        parameterH[13] = "HandlerAut";
        parameterH[14] = "ConsecutivoCaptura";
        parameterH[15] = "EstadoTransmision";
        parameterH[16] = "Id_estatus";
        parameterH[17] = "EstadoAutorizacion";
        parameterH[18] = "HoraRecibido";
        parameterH[19] = "MotivoSolicitud";
        parameterH[20] = "MotivoAprobado";

        JSONObject jsonHeader;
        try{
            jsonHeader = this.jsonArrayHeader.getJSONObject(0);

            valuesH[ 0] = jsonHeader.getString("folio");
            valuesH[ 1] = jsonHeader.getString("tipoFolio");
            valuesH[ 2] = jsonHeader.getString("esdatoFolio");
            valuesH[ 3] = jsonHeader.getString("consumoPresupuesto");
            valuesH[ 4] = jsonHeader.getString("idCliente");
            valuesH[ 5] = jsonHeader.getString("idClaveAgente");
            valuesH[ 6] = jsonHeader.getString("totalBultos");
            valuesH[ 7] = jsonHeader.getString("importeTotalAproximado");
            valuesH[ 8] = jsonHeader.getString("fechaCapura");
            valuesH[ 9] = jsonHeader.getString("horaCapura");
            valuesH[10] = jsonHeader.getString("factura");
            valuesH[11] = jsonHeader.getString("statusIBS");
            valuesH[12] = jsonHeader.getString("referenciaAutorizacion");
            valuesH[13] = jsonHeader.getString("handlerAutorizacion");
            valuesH[14] = jsonHeader.getString("consecutivoCaptura");
            valuesH[15] = jsonHeader.getString("estadoTransmicion");
            valuesH[16] = jsonHeader.getString("idStatus");
            valuesH[17] = jsonHeader.getString("statusAutorizacion");
            valuesH[18] = jsonHeader.getString("horaRecibido");
            valuesH[19] = jsonHeader.getString("motivoSolicitud");
            valuesH[20] = jsonHeader.getString("motivoAprobado");
        }catch (Exception e){
            Log.d("JSON", "Exception: " + e.getMessage());
        }

        if( isUpdate.length==0 ) {
            successHeader = DataBaseInterface.execInsert("DEV_EncabezadoDevoluciones", parameterH, valuesH);
        }
        else if( isUpdate[0] ) {
            DataBaseInterface.execDelate("DEV_EncabezadoDevoluciones", "FolioDevolucion", valuesH[ 0]);
            successHeader = DataBaseInterface.execInsert("DEV_EncabezadoDevoluciones", parameterH, valuesH);

            //UPDATE //successHeader = DataBaseInterface.execInsert("DEV_EncabezadoDevoluciones", parameterH, valuesH);
        }

        if( successHeader ) {
            try {
                JSONObject jsonObjectDetail;
                if( isUpdate.length!=0 ) {
                    if (isUpdate[0]) {
                        DataBaseInterface.execDelate("DEV_DetalleDevoluciones", "Folio", valuesH[0]);
                    }
                }

                for (int i = 0; i < this.jsonArrayDetail.length(); i++) {
                    jsonObjectDetail = this.jsonArrayDetail.getJSONObject(i);
                    String[] parameterD = new String[10];
                    String[] valuesD = new String[10];

                    parameterD[ 0] = "Folio";
                    parameterD[ 1] = "Producto";
                    parameterD[ 2] = "Cantidad";
                    parameterD[ 3] = "PrecioFarmacia";
                    parameterD[ 4] = "DescuentoComercial";
                    parameterD[ 5] = "PorcentajeBonificacion";
                    parameterD[ 6] = "ImporteBruto";
                    parameterD[ 7] = "ImporteAproximado";
                    parameterD[ 8] = "ConsecutivoCaptura";
                    parameterD[ 9] = "EstadoTransmision";

                    valuesD[ 0] = jsonObjectDetail.getString("folio");
                    valuesD[ 1] = jsonObjectDetail.getString("producto");
                    valuesD[ 2] = jsonObjectDetail.getString("cantidad");
                    valuesD[ 3] = jsonObjectDetail.getString("precioFarmacia");
                    valuesD[ 4] = jsonObjectDetail.getString("descuentoComercial");
                    valuesD[ 5] = jsonObjectDetail.getString("porcentajeBonificacion");
                    valuesD[ 6] = jsonObjectDetail.getString("importeBruto");
                    valuesD[ 7] = jsonObjectDetail.getString("importeAproximado");
                    valuesD[ 8] = jsonObjectDetail.getString("consecutivoCaptura");
                    valuesD[ 9] = jsonObjectDetail.getString("statusTransmitido");

                    if( isUpdate.length==0 ) {
                        DataBaseInterface.execInsert("DEV_DetalleDevoluciones", parameterD, valuesD);
                        //DataBaseInterface.setNewDevolutionToProduct(valuesH[10], valuesD[1], valuesD[2]);//valuesH[10] = Factura, parameterD[ 1] = "Producto", parameterD[ 2] = "Cantidad"
                    }else if( isUpdate[0] ) {
                        DataBaseInterface.execInsert("DEV_DetalleDevoluciones", parameterD, valuesD);
                        //DataBaseInterface.setNewDevolutionToProduct(valuesH[10], valuesD[1], valuesD[2]);//valuesH[10] = Factura, parameterD[ 1] = "Producto", parameterD[ 2] = "Cantidad"
                        //UPDATE
                    }
                }

                //Primero checamos, la devolución, es decir, si se trata de una merma o no; la siguiente consulta: si arroja un 2 entonces se trata de una merma, si arroja un 1
                //entonces NO se trata de una merma. Esta consulta se basa en el motivo de la devolución.
                String typeDevolution = DataBaseInterface.typeOfThisDevolution(this.context, valuesH[19]);//valuesH[19]: "Motivo"
                if (typeDevolution.trim().compareTo("1") == 0) {
                    //NO se trata de una merma
                    //Checamos si esta devolucion afecta el presupuesto para las devoluciones
                    String afffectingTheEstimate = DataBaseInterface.thisAffectingTheEstimate(this.context, valuesH[19]);//valuesH[19]: "Motivo"
                    if (afffectingTheEstimate.trim().compareTo("Y") == 0) {
                        //SI afecta e consumo, por lo que guardamos el importe total aproximado de esta devolución con lo ya acumulado de otras devoluciones dentro del presupuesto.
                        if( isUpdate.length==0 ) {
                            DataBaseInterface.setFoliosInEstimate(this.context, valuesH[7]);//valuesH[7]: "importeTotalAproximado"
                        }else if(isUpdate[0]){
                            DataBaseInterface.setFoliosInEstimate(this.context, valuesH[7], DevolucionesFullProductList.consumoActual);//valuesH[7]: "importeTotalAproximado"
                        }
                    } else if (afffectingTheEstimate.trim().compareTo("N") == 0) {
                        //SI afecta e consumo, por lo que guardamos el importe total aproximado de esta devolución con lo ya acumulado de otras devoluciones fuera del presupuesto pero autorizadas.
                        if( isUpdate.length==0 ) {
                            DataBaseInterface.setFoliosOutEstimation(this.context, valuesH[7]);//valuesH[7]: "importeTotalAproximado"
                        }else if(isUpdate[0]){
                            DataBaseInterface.setFoliosOutEstimation(this.context, valuesH[7], DevolucionesFullProductList.consumoActual);//valuesH[7]: "importeTotalAproximado"
                        }
                    }
                } else if (typeDevolution.trim().compareTo("2") == 0) {
                    // SI se trata de una merma
                    //Checamos si esta devolucion afecta el presupuesto para las devoluciones
                    String afffectingTheEstimate = DataBaseInterface.thisAffectingTheEstimate(this.context, valuesH[19]);//valuesH[19]: "Motivo"
                    if (afffectingTheEstimate.trim().compareTo("Y") == 0) {
                        //SI afecta e consumo, por lo que, aun que sea merma, guardamos el importe total aproximado de esta devolución con lo ya acumulado de otras devoluciones dentro del presupuesto.
                        if( isUpdate.length==0 ) {
                            DataBaseInterface.setFoliosInEstimate(this.context, valuesH[7]);//valuesH[7]: "importeTotalAproximado"
                        }else if(isUpdate[0]){
                            DataBaseInterface.setFoliosInEstimate(this.context, valuesH[7], DevolucionesFullProductList.consumoActual);//valuesH[7]: "importeTotalAproximado"
                        }
                    } else if (afffectingTheEstimate.trim().compareTo("N") == 0) {
                        //SI afecta e consumo, por lo que guardamos el importe total aproximado de esta devolución con lo ya acumulado de otras devoluciones fuera del presupuesto pero con autorizacion de merma.
                        if( isUpdate.length==0 ) {
                            DataBaseInterface.setFoliosInMerma(this.context, valuesH[7]);//valuesH[7]: "importeTotalAproximado"
                        }else if(isUpdate[0]){
                            DataBaseInterface.setFoliosInMerma(this.context, valuesH[7], DevolucionesFullProductList.consumoActual);//valuesH[7]: "importeTotalAproximado"
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private static String consecutivoAUX;
    public void sendData(){
        WebServices ws = new WebServices();

        consecutivoAUX = DataBaseInterface.getConsecutivo(PrepareSendingData.thiz.context);
        //DataBaseInterface.execUpdate("DEV_EncabezadoDevoluciones", "FolioDevolucion", thiz.pendingReturn.getFolioForThisReturn(), new String[]{"ConsecutivoCaptura"}, new String[]{consecutivoAUX});

        try {
            JSONObject jheader = (JSONObject)this.jsonArrayHeader.get(0);
            jheader.put("consecutivoCaptura",consecutivoAUX);
            this.jsonArrayHeader = new JSONArray();
            this.jsonArrayHeader.put(jheader);
        }catch (Exception e){
            System.out.println(e);
        }

        ws.sendData(this.context, PrepareSendingData.NAME_METHOP, this.NAME_PROPERTY_HEADER, this.jsonArrayHeader.toString(), this.NAME_PROPERTY_DETAIL, this.jsonArrayDetail.toString());
    }
    /**
     * Callback que se ejecuta al terminar el envio de datos
     * @param result Resultado del envio de datos
     */
    public static void callBack( final Object result){
        String status = "10";

        if( result == null ){
            status = "10";
        }else{
            try{
                JSONArray jsonArrayResponse =  new JSONArray(result+"");
                JSONObject jsonObjectResponse =jsonArrayResponse.getJSONObject(0);
                status = jsonObjectResponse.getString("Id_estatus");
            }catch (Exception e){
                Log.d("JSON", "Exception: "+e.getMessage());
            }
        }

        final String finalStatus = status.trim();

        PrepareSendingData.thiz.context.runOnUiThread(new Runnable() {
            public void run() {
                //PrepareSendingData.thiz.context.clearSumaryList();
                /*if (PrepareSendingData.progressDialog.isShowing())
                    PrepareSendingData.progressDialog.dismiss();/*/

                if( finalStatus.compareTo("20")==0 ){
                    //Guardar en base de datos el id estatus a 20
                    DataBaseInterface.execUpdate("DEV_EncabezadoDevoluciones","FolioDevolucion",thiz.pendingReturn.getFolioForThisReturn(), new String[]{"Id_estatus"}, new String[]{"20"});

                    //String consecutivo = DataBaseInterface.getConsecutivo(PrepareSendingData.thiz.context);
                    DataBaseInterface.execUpdate("DEV_EncabezadoDevoluciones", "FolioDevolucion", thiz.pendingReturn.getFolioForThisReturn(), new String[]{"ConsecutivoCaptura"}, new String[]{consecutivoAUX});

                    DataBaseInterface.execUpdate("DEV_DetalleDevoluciones", "Folio", thiz.pendingReturn.getFolioForThisReturn(), new String[]{"EstadoTransmision"}, new String[]{"T"});

                    DevolucionesFullReturnsList.sendDevolution();
                    //Toast.makeText(PrepareSendingData.thiz.context, "Enviados Exitosamente", Toast.LENGTH_LONG).show();
                } else{
                    //String consecutivo = DataBaseInterface.getConsecutivo(PrepareSendingData.thiz.context);
                    consecutivoAUX = ""+(Integer.parseInt(consecutivoAUX)-1);
                    DataBaseInterface.db.execUpdate("DEV_Consecutivos", new String[]{"Consecutivo"},  new String[]{consecutivoAUX}, null);

                    DevolucionesFullReturnsList.exitoAlMandarTodas = false;
                    DevolucionesFullReturnsList.sendDevolution();
                   //Toast.makeText(PrepareSendingData.thiz.devolucionesLiteActivity, "Ocurrio un inconveniente al enviar los datos, intente de nuevo", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static String setFormatMoney( double availableReturns) {

        //Le damos formato al costo total de la devolución
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat dec = (DecimalFormat) nf;
        dec.setMaximumFractionDigits(2);
        dec.setMinimumFractionDigits(2);

        return dec.format(availableReturns);
    }
}

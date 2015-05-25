package com.marzam.com.appventas.DevolucionesLite.PrepareSendingData;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.marzam.com.appventas.DataBase.DataBase;
import com.marzam.com.appventas.DevolucionesLite.DevolucionesLite;
import com.marzam.com.appventas.DevolucionesLite.ProductList.Product;
import com.marzam.com.appventas.WebService.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by lcabral on 14/04/2015.
 */
public class PrepareSendingData {

    //Data Header
    private String sucursal;
    private String folio_hh;
    private String cliente;
    private String folioDevAgente;
    private String tipoDocumento;
    private String empleado;
    private String folioDocumentoAgente;
    private String fechaCreacion;
    private String fechaCreacionTxt;
    private String status;
    private String bultos;
    private String idDevolucion;

    //Data Detail
    private String codigo;
    private String motivo;
    private String cantidad;

    private JSONArray jsonArrayHeader;
    private JSONArray jsonArrayDetail;

    private static String NAME_METHOP = "TransmiteDevoluciones";
    private static String NAME_PROPERTY_HEADER = "jencabezado";
    private static String NAME_PROPERTY_DETAIL = "jdetalle";

    //DataBase
    private DataBase db;
    private DevolucionesLite devolucionesLiteActivity;

    private static ProgressDialog progressDialog;
    private static PrepareSendingData thiz;

    /**
     * Constructor
     * @param devolucionesLiteActivity Padre
     */
    public PrepareSendingData( DevolucionesLite devolucionesLiteActivity ){
        this.devolucionesLiteActivity = devolucionesLiteActivity;
        this.db = new DataBase( this.devolucionesLiteActivity );
        makeJSON();

        String jsonHeader = this.jsonArrayHeader.toString();
        String jsonDetail = this.jsonArrayDetail.toString();

        thiz = this;

        Context context = PrepareSendingData.this.devolucionesLiteActivity;
        progressDialog = ProgressDialog.show(context, "Enviando", "Por favor espere...", true, false);
        WebServices ws = new WebServices();
        ws.sendData( this.devolucionesLiteActivity, PrepareSendingData.NAME_METHOP, PrepareSendingData.NAME_PROPERTY_HEADER, PrepareSendingData.this.jsonArrayHeader.toString(), PrepareSendingData.NAME_PROPERTY_DETAIL, PrepareSendingData.this.jsonArrayDetail.toString() );
    }

    /**
     * Método que se encarga de construir los json de las devoluciones
     */
    public void makeJSON(){
        Product[] productsSumaryData = this.devolucionesLiteActivity.getProductSumaryData();
        this.jsonArrayHeader = new JSONArray();
        this.jsonArrayDetail = new JSONArray();
        JSONObject jheader = null, jdetail = null;

        getConstantDataHeader();
        for( int i=0; i<productsSumaryData.length; i++ ){
            Product product = productsSumaryData[i];
            jheader = null;
            jdetail = null;

            //Paquete => header
            if( product.isPackage ){
                getDataHeader( product );
                jheader = makeJsonHeader();
                jdetail = makeJsonDetail( false );

            //Productos = > detail
            }else if( product.isProduct ){
                getDataHeader(product);
                getDataDetail(product);
                if( !idOnHeader(product) ) {
                    jheader = makeJsonHeader();
                }
                jdetail = makeJsonDetail( true );
            }
            if( product.isPackage || product.isProduct ) {
                if( jheader!=null )
                    this.jsonArrayHeader.put(jheader);
                this.jsonArrayDetail.put(jdetail);
            }
        }
    }

    /**
     * Método para comprobar que el "id" ya se encuentra en el json
     * @param product producto a comprobar
     * @return TRUE si el producto se encuentra en el json, FALSE de lo contrario
     */
    private boolean idOnHeader( Product product ){
        boolean idOnHeader = false;
        try {
            for( int i=0; i<this.jsonArrayDetail.length(); i++ ) {
                String idDevJson = (String)(this.jsonArrayHeader.getJSONObject(i)).get("id_devolucion");
                idOnHeader = (product.idDevolucion.compareTo(idDevJson) == 0);
                if(idOnHeader) return idOnHeader;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Método para construir el json del header
     * @return Objeto JSON  del header
     */
    private JSONObject makeJsonHeader(){
        JSONObject jheader = new JSONObject();
        try {
            jheader.put("sucursal", this.sucursal);
            jheader.put("folio_hh", this.folio_hh);
            jheader.put("cliente", this.cliente);
            jheader.put("folio_dev_agente", this.folioDevAgente);
            jheader.put("tipo_documento", this.tipoDocumento);
            jheader.put("empleado", this.empleado);
            jheader.put("folio_documento_agente", this.folioDocumentoAgente);
            jheader.put("fecha_creacion", this.fechaCreacion);
            jheader.put("fecha_creacion_txt", this.fechaCreacionTxt);
            jheader.put("status", this.status);
            jheader.put("bultos", this.bultos);
            jheader.put("id_devolucion", this.idDevolucion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jheader;
    }

    /**
     * Método para construir el json de los detalles
     * @return Objeto JSON  del detalle
     */
    private JSONObject makeJsonDetail( boolean complete ){
        JSONObject jdetail = new JSONObject();
        if ( complete ) {
            try {
                jdetail.put("sucursal", this.sucursal);
                jdetail.put("folio_hh", this.folio_hh);
                jdetail.put("codigo", this.codigo);
                jdetail.put("motivo", this.motivo);
                jdetail.put("cantidad", this.cantidad);
                jdetail.put("folio_dev_agente", this.folioDevAgente);
                jdetail.put("id_devolucion", this.idDevolucion);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            try {
                jdetail.put("sucursal", this.sucursal);
                jdetail.put("id_devolucion", this.idDevolucion);
                jdetail.put("folio_hh", "");
                jdetail.put("codigo", "");
                jdetail.put("motivo", "");
                jdetail.put("cantidad", "");
                jdetail.put("folio_dev_agente", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jdetail;
    }

    //Datos constantes en el envio

    /**
     * Inicializa los datos que siempre son constantes en el json header
     */
    private void getConstantDataHeader() {
        this.sucursal = this.getSucursal();
        this.folio_hh = "";
        this.cliente = this.getClient();
        this.empleado = this.getNumEmployee();
        this.status = "10";

    }

    /**
     * Inicializa los datos correspondientes al json del header
     * @param product Producto a extraer los datos
     */
    private void getDataHeader( Product product ){
        this.folioDevAgente = product.numberReturn;
        this.tipoDocumento = getTypeDocument( product.typeDocument );
        this.folioDocumentoAgente = product.folioDocument;
        this.fechaCreacionTxt = product.dateAdd;
        this.fechaCreacion = product.dateAdd;
        this.bultos = product.amountPackages;
        this.idDevolucion = product.idDevolucion;
    }

    /**
     * Inicializa los datos correspondientes al json del detalle
     * @param product Producto a extraer los datos
     */
    private void getDataDetail( Product product){
        this.codigo = product.marzamCode;
        this.motivo = getReasonReturn( product.reasonReturn );
        this.cantidad = product.amountProductReturn;
    }

    /**
     * Método el cual regresa la sucursal en la cual se esta generando la devolución
     * @return Sucursal
     */
    private String getSucursal(){
        String sucursal = this.db.execSelect( DataBase.QUERY_SUCURSAL );
        return sucursal;
    }

    /**
     * Método el cual regresa el cliente que esta generando la devolución
     * @return Cliente
     */
    private String getClient(){
        String client = this.db.execSelect( DataBase.QUERY_CLIENT );
        return client;
    }

    /**
     * Método el cual regresa el numero de empleado que esta generando la devolución
     * @return numero de empleado
     */
    private String getNumEmployee(){
        String employee = this.db.execSelect( DataBase.QUERY_NUM_EMPLOYEE );
        return employee;
    }

    /**
     * Método para obtener el id del tipo del documento
     * @param typeDocument Tipo de documento
     * @return Id del documento
     */
    private String getTypeDocument( String typeDocument ){
        String idTypeDocument = this.db.execSelect( DataBase.QUERY_ID_TYPE_DOCUMENT, typeDocument );
        return idTypeDocument;
    }

    /**
     * Método para obtener el id del motivo de la devolución
     * @param reasonReturn Id del motivo de la devolución
     * @return
     */
    private String getReasonReturn( String reasonReturn ){
        String idReasonDocument = this.db.execSelect( DataBase.QUERY_ID_REASON_RETURN, reasonReturn );
        return idReasonDocument;
    }

    /**
     * Callback que se ejecuta al terminar el envio de datos
     * @param result Resultado del envio de datos
     */
    public static void callBack( final Object result){
        boolean successHeader = false;
        boolean successDetail = false;
        String status = "10";

        if( result == null ){
            status = "10";
        }else{
            try{
                JSONArray jsonArrayResponse =  new JSONArray(result+"");
                JSONObject jsonObjectResponse =jsonArrayResponse.getJSONObject(0);
                status = jsonObjectResponse.getString("status");
            }catch (Exception e){
                Log.d("JSON", "Exception: "+e.getMessage());
            }
        }

        try {
            JSONObject jsonObjectHeader = PrepareSendingData.thiz.jsonArrayHeader.getJSONObject(0);
            jsonObjectHeader.put("status", status);

            String[] parameterH = new String[12];
            String[] valuesH = new String[12];

            parameterH[0] = "fecha_creacion_txt";
            parameterH[1] = "sucursal";
            parameterH[2] = "tipo_documento";
            parameterH[3] = "fecha_creacion";
            parameterH[4] = "status";
            parameterH[5] = "empleado";
            parameterH[6] = "folio_dev_agente";
            parameterH[7] = "cliente";
            parameterH[8] = "folio_hh";
            parameterH[9] = "id_devolucion";
            parameterH[10] = "bultos";
            parameterH[11] = "folio_documento_agente";

            valuesH[0] = jsonObjectHeader.getString("fecha_creacion_txt");
            valuesH[1] = jsonObjectHeader.getString("sucursal");
            valuesH[2] = jsonObjectHeader.getString("tipo_documento");
            valuesH[3] = jsonObjectHeader.getString("fecha_creacion");
            valuesH[4] = jsonObjectHeader.getString("status");
            valuesH[5] = jsonObjectHeader.getString("empleado");
            valuesH[6] = jsonObjectHeader.getString("folio_dev_agente");
            valuesH[7] = jsonObjectHeader.getString("cliente");
            valuesH[8] = jsonObjectHeader.getString("folio_hh");
            valuesH[9] = jsonObjectHeader.getString("id_devolucion");
            valuesH[10] = jsonObjectHeader.getString("bultos");
            valuesH[11] = jsonObjectHeader.getString("folio_documento_agente");

            DataBase db = new DataBase(PrepareSendingData.thiz.devolucionesLiteActivity);
            /*boolean*/ successHeader = db.execUpdate("DEV_Encabezado", parameterH, valuesH);

            //boolean successDetail;
            JSONObject jsonObjectDetail;
            for( int i=0; i<PrepareSendingData.thiz.jsonArrayDetail.length(); i++ ){
                jsonObjectDetail = PrepareSendingData.thiz.jsonArrayDetail.getJSONObject(i);
                String[] parameterD = new String[7];
                String[] valuesD = new String[7];

                parameterD[0] = "codigo";
                parameterD[1] = "id_devolucion";
                parameterD[2] = "sucursal";
                parameterD[3] = "motivo";
                parameterD[4] = "folio_dev_agente";
                parameterD[5] = "folio_hh";
                parameterD[6] = "cantidad";

                valuesD[0] = jsonObjectDetail.getString("codigo");
                valuesD[1] = jsonObjectDetail.getString("id_devolucion");
                valuesD[2] = jsonObjectDetail.getString("sucursal");
                valuesD[3] = jsonObjectDetail.getString("motivo");
                valuesD[4] = jsonObjectDetail.getString("folio_dev_agente");
                valuesD[5] = jsonObjectDetail.getString("folio_hh");
                valuesD[6] = jsonObjectDetail.getString("cantidad");
                successDetail = db.execUpdate("DEV_Detalle", parameterD, valuesD);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final boolean finalSuccessHeader = successHeader;
        final boolean finalSuccessDetail = successDetail;
        final String finalStatus = status.trim();

        PrepareSendingData.thiz.devolucionesLiteActivity.runOnUiThread(new Runnable() {
            public void run() {
                PrepareSendingData.thiz.devolucionesLiteActivity.clearSumaryList();
                if (PrepareSendingData.progressDialog.isShowing())
                    PrepareSendingData.progressDialog.dismiss();//

                if( finalStatus.compareTo("20")==0 ){
                  Toast.makeText(PrepareSendingData.thiz.devolucionesLiteActivity, "Enviados Exitosamente", Toast.LENGTH_LONG).show();
                  DevolucionesLite.clearSumaryListAndFormPakage();
                } else{
                    if( finalSuccessHeader && finalSuccessDetail ){
                        Toast.makeText(PrepareSendingData.thiz.devolucionesLiteActivity, "Enviados Exitosamente", Toast.LENGTH_LONG).show();
                        DevolucionesLite.clearSumaryListAndFormPakage();
                    }else {
                        Toast.makeText(PrepareSendingData.thiz.devolucionesLiteActivity, "Ocurrio un inconveniente al enviar los datos, intente de nuevo", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}

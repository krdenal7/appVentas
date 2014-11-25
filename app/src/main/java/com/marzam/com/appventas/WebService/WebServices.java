package com.marzam.com.appventas.WebService;

import android.util.Base64;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Objects;

/**
 * Created by SAMSUMG on 15/11/2014.
 */
public class WebServices {




    public Object Down_BD(byte[] bit){

        byte[] enc=bit;
        String cadena=Base64.encodeToString(enc,Base64.DEFAULT);

        String SOAP_ACTION="http://tempuri.org/SincronizarBAK";
        String OPERATION_NAME="SincronizarBAK";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS="http://190.1.4.120/WebService/WebService.asmx";

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty("docBinary",cadena);
        request.addProperty("docName","Backup.zip");


        SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransportSE=new HttpTransportSE(SOAP_ADDRESS);
        Object response=null;

        try{

            httpTransportSE.call(SOAP_ACTION,envelope);
            response=envelope.getResponse();

        }catch (Exception e){
            String a=e.toString();
            Log.d("WebServiceBakError",a);
        }




        return response;
    }
}

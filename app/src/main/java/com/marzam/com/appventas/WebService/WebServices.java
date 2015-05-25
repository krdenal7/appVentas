package com.marzam.com.appventas.WebService;


import android.os.AsyncTask;
import android.util.Log;

import com.marzam.com.appventas.DevolucionesLite.DevolucionesLite;
import com.marzam.com.appventas.DevolucionesLite.PrepareSendingData.PrepareSendingData;
import com.marzam.com.appventas.Email.Mail;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;

public class WebServices {

            //static String ip="http://201.134.159.126/WebService/WebService.asmx";
              static String ip="http://190.1.4.120/WebService/WebService.asmx";
            //static String ip="http://201.134.159.126/WebServicePruebas/WebService.asmx";
            //static String ip="http://201.134.159.126/WSFuerzasExternas/WebService.asmx";
            //  static String ip="http://marzam.mx/WSFuerzasExternas/WebService.asmx";

    String from="WebServices";
    String subject;
    String body;
    Mail m;

    public Object Upload_BD(String zip,String nombre){

           String cadena="";

        try {

            cadena=EncodeToBase64(zip);

        }catch (Exception e){
            String err=e.toString();
        }


        String SOAP_ACTION="http://tempuri.org/TransfiereBAK";
        String OPERATION_NAME="TransfiereBAK";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS=ip;

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty("docBinary",cadena);
        request.addProperty("docName",nombre);


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
            return null;
        }




        return response;
    }

    public String SincronizarPedidos(String encabezado,String detalle){

        String SOAP_ACTION="http://tempuri.org/SincronizarPedidos";
        String OPERATION_NAME="SincronizarPedidos";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS=ip;

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);

          // if(!valida_Encabezado(encabezado))
          //                        return null;

        request.addProperty("encabezado",encabezado);
        request.addProperty("detalle",detalle);

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
            return null;
        }

           return response.toString();

    }

    public String SincronizarVisitas(String json){
        String SOAP_ACTION="http://tempuri.org/SincronizarVisitas";
        String OPERATION_NAME="SincronizarVisitas";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS=ip;

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty("visitas",json);


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
            return null;
        }

        return response.toString();
    }

    public String cargarBack(String archivo,String name){

        File file=new File(archivo);
        byte[] bytes=ConvertToByte(file);
        String cadena= Base64.encode(bytes);
        String respuesta="";

        String SOAP_ACTION="http://tempuri.org/TransfiereBAK";
        String OPERATION_NAME="TransfiereBAK";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS=ip;

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        PropertyInfo parameterfilebyte=new PropertyInfo();
        parameterfilebyte.setName("docBinary");
        parameterfilebyte.setValue(cadena);
        parameterfilebyte.setType(String.class);
        request.addProperty(parameterfilebyte);

        PropertyInfo parameterfilename=new PropertyInfo();
        parameterfilename.setName("docName");
        parameterfilename.setValue(name);
        parameterfilename.setType(String.class);
        request.addProperty(parameterfilename);

        SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=true;
        envelope.implicitTypes=true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidhttpTransport=new HttpTransportSE(Proxy.NO_PROXY,SOAP_ADDRESS,60000);

        try {

            androidhttpTransport.call(SOAP_ACTION,envelope);
            SoapPrimitive response=(SoapPrimitive)envelope.getResponse();

            respuesta=response.toString();

        }catch (Exception e){
            String err=e.toString();
            e.printStackTrace();
            respuesta=null;
        }


        return respuesta;
    }

    public String UploadCoordenadas(String json){

        String SOAP_ACTION="http://tempuri.org/RegistraGP";
        String OPERATION_NAME="RegistraGP";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS=ip;

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty("json",json);


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
            return null;
        }

        return response.toString();
    }

    public String Down_DB(String agente){

        String SOAP_ACTION="http://tempuri.org/SincronizaCatalogo";
        String OPERATION_NAME="SincronizaCatalogo";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS=ip;

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty("docName",agente);


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
            return null;
        }


        return response==null?null:response.toString();

    }

    public String toBinary(byte[] bytes){

        StringBuilder sb=new StringBuilder(bytes.length*Byte.SIZE);
        for(int i=0;i<Byte.SIZE*bytes.length;i++)
              sb.append((bytes[i/Byte.SIZE]<<i% Byte.SIZE & 0x80)==0?'0':'1');

        return sb.toString();
    }

    public String Sincronizarrespuestas(String json){

        String SOAP_ACTION="http://tempuri.org/SincronizarRespuestas";
        String OPERATION_NAME="SincronizarRespuestas";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS=ip;

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty("jsonPedidos",json);


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
            return null;
        }

        return response.toString();

    }

    public String CierreVisitas(String json){
        String SOAP_ACTION="http://tempuri.org/CierreVisitas";
        String OPERATION_NAME="CierreVisitas";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS=ip;

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty("json",json);


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
            return null;
        }

        return response.toString();
    }

    public String RegistrarTelefono(String json){
        String SOAP_ACTION="http://tempuri.org/RegistrarTelefono";
        String OPERATION_NAME="RegistrarTelefono";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS=ip;

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty("json",json);


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
            return null;
        }

        return response.toString();
    }

    public String UploadEstatusSincronizacion(String json){

        String SOAP_ACTION="http://tempuri.org/CambiaStatusSincronizacion";
        String OPERATION_NAME="CambiaStatusSincronizacion";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS=ip;

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty("id",json);


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
            return null;
        }

        return response.toString();
    }

    public String DownJson(String num_empleado){

        String SOAP_ACTION="http://tempuri.org/Sincronizacion";
        String OPERATION_NAME="Sincronizacion";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS=ip;

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        request.addProperty("numero_empleado",num_empleado);


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
            return null;
        }

        return response.toString();

    }

    public String InsertarCliente(String json,String clave_agente){
        String SOAP_ACTION="http://tempuri.org/InsertarCliente1";
        String OPERATION_NAME="InsertarCliente1";
        String WSDL_TARGET_NAMESPACE="http://tempuri.org/";
        String SOAP_ADDRESS=ip;

        SoapObject request=new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);

        // if(!valida_Encabezado(encabezado))
        //                        return null;

        request.addProperty("json",json);
        request.addProperty("clave_agente",clave_agente);

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
            return null;
        }

        return response.toString();
    }

    public byte[] ConvertToByte(File file){
        byte[] bytes=null;
        try {
            InputStream in = new FileInputStream(file);
            long length=file.length();
            if(length>Integer.MAX_VALUE){

            }
             bytes=new byte[(int)length];
            int offset=0;
            int numRead=0;
            while (offset<bytes.length && (numRead=in.read(bytes,offset,bytes.length-offset))>=0){
                offset+=numRead;
            }
            if(offset<bytes.length){
                throw new IOException("El archivo no se completo");
            }
               in.close();


            }catch (Exception e){
            String err=e.toString();
            }
        return bytes;
    }

    public String EncodeToBase64(String Archivo)throws IOException{

             File file=new File(Archivo);
             byte[] bytes=ConvertToByte(file);
            String encode= Base64.encode(bytes);

        Log.i("BIT", encode);

        return encode;
    }

    public class sendEmail extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {


            m = new Mail("rodrigo.cabrera.it129@gmail.com", "juanito1.");
            String[] toArr = {"imartinez@marzam.com.mx","cardenal.07@hotmail.com"};
            m.setTo(toArr);
            m.setFrom(from);
            m.setSubject(subject);
            m.setBody(body);

            try {

                m.send();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    public void sendData(final Object father, String metodo, String... arg) {
        final String SOAP_ACTION = "http://tempuri.org/" + metodo;
        final String OPERATION_NAME = metodo;
        final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
        final String SOAP_ADDRESS = ip;

        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        for (int i = 0; i < arg.length; i = i + 2)
            request.addProperty(arg[i], arg[i + 1]);

        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        final HttpTransportSE httpTransportSE = new HttpTransportSE(SOAP_ADDRESS);
        final Object[] response = new Object[1];

        new Thread(new Runnable(){

            public void run()
            {
                boolean sendDataSuccess   = false;
                int intentSendData = 0;
                while( !sendDataSuccess&&intentSendData<3 ) {
                    try {
                        httpTransportSE.call(SOAP_ACTION, envelope);
                        response[0] = envelope.getResponse();
                        sendDataSuccess = true;
                    } catch (Exception e) {
                        String a = e.toString();
                        Log.d("WebServiceBakError", a);
                        response[0] = null;
                        //return null;
                    }
                    intentSendData++;
                }
                WebServices.callBack( father, response[0] );
            }
        }).start();
        //return response[0].toString();
    }

    public static void callBack(Object father, Object response){
        if( father.getClass()== DevolucionesLite.class){
            PrepareSendingData.callBack(response);
        }
    }

}

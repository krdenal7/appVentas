package com.marzam.com.appventas.PushNotifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.marzam.com.appventas.MainActivity;
import com.marzam.com.appventas.R;

/**
 * Created by SAMSUMG on 15/11/2014.
 */
public class GCMIntentService extends IntentService{

    private static final int NOTIF_ALERTA_ID = 1;

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

                String mensaje=extras.getString("msg");
                mostrarNotification(mensaje);
            }
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
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

}

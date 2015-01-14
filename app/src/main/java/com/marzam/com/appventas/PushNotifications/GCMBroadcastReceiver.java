package com.marzam.com.appventas.PushNotifications;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by SAMSUMG on 15/11/2014.
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        ComponentName comp =
                new ComponentName(context.getPackageName(),
                        GCMIntentService.class.getName());

        startWakefulService(context, (intent.setComponent(comp)));

           setResultCode(Activity.RESULT_OK);


    }
}

package com.marzam.com.appventas.GPS;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;



/**
 * Created by SAMSUMG on 11/11/2014.
 */
public class MyLocationListener implements LocationListener {
    public  void UpdateScreen(Location location){

        //TextView txtView = (TextView) ((Activity)context).findViewById(R.id.textView3);
        Log.d("LocalizacionGPS", "Latitud:" + String.valueOf(location.getLatitude()) + " Longitud:" + String.valueOf(location.getLongitude()));

    }

    @Override
    public void onLocationChanged(Location location) {

        // Log.d("LocalizacionGPS", "Latitud:"+String.valueOf(location.getLatitude())+" Longitud:"+String.valueOf(location.getLongitude()));
        UpdateScreen(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

package com.marzam.com.appventas.GPS;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by SAMSUMG on 19/11/2014.
 */
public class GPSHelper {

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES=100;
    private static final long MINIMUM_TIME_BETWEEN_UPDATES=60000;

    private LocationManager locationManager;
    private Context context;

    public GPSHelper(Context context){
        locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new MyLocationListener());

    }


    public  String getLongitude(){
        String message;
        Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location !=null){
            message=String.valueOf(location.getLongitude());
        }else{
            message="0";
        }

        return message;
    }
    public  String getLatitude(){
        String message;
        Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location !=null){
            message=String.valueOf(location.getLatitude());
        }else{
            message="0";
        }

        return message;
    }


    private class MyLocationListener implements LocationListener{

        public void onLocationChanged(Location location){
            String message=String.format("New location\n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(),location.getLatitude());
            Log.i("GPSINFO", message);
        }

        public void  onStatusChanged(String s, int i,Bundle bundle){

        }

        public void onProviderDisabled(String s){

        }

        public void onProviderEnabled(String s){

        }

    }
}

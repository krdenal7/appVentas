package com.marzam.com.appventas.GPS;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Created by SAMSUMG on 11/11/2014.
 */
public class LocationGPS {
    private Context context;

    public void GPS(Context context){

        this.context = context;


        LocationManager myLocation;
        LocationListener miLocationList;

        myLocation=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        miLocationList = new MyLocationListener();
        myLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,10,miLocationList);

    }
}

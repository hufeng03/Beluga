package com.hufeng.filemanager.ad;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by feng on 14-1-7.
 */
public class LocationUtil {

    public static Location getLocation(Context context) {
        // Get the location manager
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
//        Criteria criteria = new Criteria();
//        String provider = locationManager.getBestProvider(criteria, false);
        String provider = LocationManager.NETWORK_PROVIDER;
        Location location = null;
        try {
            location = locationManager.getLastKnownLocation(provider);
        } catch(Exception e){
            e.printStackTrace();
        } finally{

        }
        // Initialize the location fields
        return location;
    }
}

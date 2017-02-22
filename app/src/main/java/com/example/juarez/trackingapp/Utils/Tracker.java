package com.example.juarez.trackingapp.Utils;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Juarez on 22/02/2017.
 */

public class Tracker extends Service{

    private Context mContext;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;

    private Location mLocation;
    private Double latitude;
    private Double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3; // 3 segundos

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    public Tracker(Context mContext, @NonNull LocationListener locationListener) {
        this.mContext = mContext;
        this.mLocationListener = locationListener;
        getLocation();
    }

    public Location getLocation() {
        try {
            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                canGetLocation = true;

                if (isNetworkEnabled) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                    Log.d(Tracker.class.getSimpleName(), "Network");

                    if (mLocationManager != null) {
                        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (mLocation != null) {
                            latitude = mLocation.getLatitude();
                            longitude = mLocation.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (mLocation == null) {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                        Log.d(Tracker.class.getSimpleName(), "GPS Enabled");

                        if (mLocationManager != null) {
                            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (mLocation != null) {
                                longitude = mLocation.getLongitude();
                                latitude = mLocation.getLatitude();
                            }
                        }
                    }
                }

            }

        } catch (SecurityException exception) {

        }

        return mLocation;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public double getLatitude() {
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (mLocation != null) {
            longitude = mLocation.getLongitude();
        }
        return longitude;
    }

    public void showAlertDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("GPS is Settings")
                .setMessage("GPS is not enabled. Do you want to go to settings manu?")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void stopUsingGPS(){
        if(mLocationManager != null){
            try {
                mLocationManager.removeUpdates(mLocationListener);
            }catch (SecurityException e){

            }

        }
    }

}

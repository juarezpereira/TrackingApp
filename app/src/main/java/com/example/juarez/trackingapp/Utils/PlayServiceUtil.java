package com.example.juarez.trackingapp.Utils;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by Juarez on 24/02/2017.
 * Class checks play service
 */

public class PlayServiceUtil {

    private final static String TAG = PlayServiceUtil.class.getSimpleName();
    private final static int REQUEST_CODE_ERROR_PLAY_SERVICES = 9000;

    public static boolean checkGooglePlayService(FragmentActivity context){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);

        if(resultCode != ConnectionResult.SUCCESS){
            if(apiAvailability.isUserResolvableError(resultCode)){
                apiAvailability.getErrorDialog(context, resultCode, REQUEST_CODE_ERROR_PLAY_SERVICES).show();
            }else{
                Log.i(TAG, "This device is not supported.");
                context.finish();
            }
            return false;
        }
        return true;
    }

}

package com.example.juarez.trackingapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Juarez Pereira on 08/03/2017.
 */

public class IntroManager {

    private static final String PREF_NAME = "TRACKINGAPP";
    private static final String PREF_FIRST_LAUNCHER = "FIRST";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context mContext;

    public IntroManager(Context context){
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setFirstLaunch(boolean isFirst){
        editor.putBoolean(PREF_FIRST_LAUNCHER, isFirst);
        editor.commit();
    }

    public boolean isFirstLaunch(){
        return pref.getBoolean(PREF_FIRST_LAUNCHER, true);
    }

}
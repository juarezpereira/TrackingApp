package com.example.juarez.trackingapp;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Created by Juarez on 22/02/2017.
 */

public class TrackerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }

}

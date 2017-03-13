package com.example.juarez.trackingapp.Utils;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Juarez Pereira on 05/03/2017.
 */

public interface EventLogin {
    void onBeforeLogin();
    void onAfterLogin(FirebaseUser firebaseUser);
}

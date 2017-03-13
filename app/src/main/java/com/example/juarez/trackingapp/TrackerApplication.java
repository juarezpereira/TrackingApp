package com.example.juarez.trackingapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.example.juarez.trackingapp.Model.User;
import com.example.juarez.trackingapp.Utils.Constants;
import com.example.juarez.trackingapp.Utils.EventLogin;
import com.example.juarez.trackingapp.Utils.IntroManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by Juarez on 22/02/2017.
 */

public class TrackerApplication extends Application {

    EventLogin eventLogin;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference referenceUsers;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
            }
        });

        this.referenceUsers = FirebaseDatabase.getInstance().getReference(Constants.USERS);
    }

    public void setEventLogin(EventLogin eventLogin) {
        this.eventLogin = eventLogin;
    }

    public void doLoginEmailAndPass(String email, String pass) {
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        referenceUsers.child(firebaseUser.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        user.setOnline(true);

                                        setUser(user);

                                        referenceUsers.child(firebaseUser.getUid())
                                                .setValue(user);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                        if (eventLogin != null) {
                            eventLogin.onAfterLogin(firebaseUser);
                        }
                    }
                });
    }

    public void createUserWithEmailAndPassword(final String name, String email, String pass) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            User user = new User();
                            user.setName(name);
                            user.setUID(firebaseUser.getUid());
                            user.setOnline(true);

                            setUser(user);

                            referenceUsers.child(firebaseUser.getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful() && eventLogin != null) {
                                                eventLogin.onAfterLogin(firebaseUser);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void logout() {
        if (eventLogin == null)
            return;

        referenceUsers.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        user.setOnline(false);

                        referenceUsers.child(firebaseUser.getUid())
                                .setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful() && eventLogin != null) {
                                            firebaseAuth.signOut();
                                            eventLogin.onBeforeLogin();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setTracking(String tracker) {
        HashMap<String, Boolean> tracking = new HashMap<>();
        tracking.put(tracker, true);

        User user = getUser();
        user.setTracking(tracking);

        referenceUsers.child(firebaseUser.getUid())
                .setValue(user);

        setUser(user);
    }

    public void init() {
        if (firebaseUser == null) {
            eventLogin.onBeforeLogin();
        } else {
            eventLogin.onAfterLogin(firebaseUser);
        }
    }

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
    }

    public void setUser(User user) {
        String userJson = new Gson().toJson(user);

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        editor.putString(Constants.SHARED_PREF_USER, userJson).apply();
        editor.commit();
    }

    public User getUser() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        String userJson = sharedPreferences.getString(Constants.SHARED_PREF_USER, "");

        return new Gson().fromJson(userJson, User.class);
    }

}

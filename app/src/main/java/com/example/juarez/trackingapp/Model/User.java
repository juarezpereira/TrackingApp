package com.example.juarez.trackingapp.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Juarez on 22/02/2017.
 */

public class User {

    private String UID = "";
    private String name = "";
    private Map<String, Boolean> Tracking = new HashMap<>();
    private boolean online = false;

    public User() {
    }

    public User(String UID, String name, boolean online) {
        this.UID = UID;
        this.name = name;
        this.online = online;
    }

    public User(String UID, String name, Map<String, Boolean> tracking, boolean online) {
        this.UID = UID;
        this.name = name;
        Tracking = tracking;
        this.online = online;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Map<String, Boolean> getTracking() {
        return Tracking;
    }

    public void setTracking(Map<String, Boolean> tracking) {
        Tracking = tracking;
    }

}

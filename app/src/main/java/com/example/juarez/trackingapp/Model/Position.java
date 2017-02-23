package com.example.juarez.trackingapp.Model;

/**
 * Created by Juarez on 22/02/2017.
 */

public class Position {

    private int rota = 0;
    private Double latitude = 0D;
    private Double longitude = 0D;

    public Position(){}

    public Position(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getRota() {
        return rota;
    }

    public void setRota(int rota) {
        this.rota = rota;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}

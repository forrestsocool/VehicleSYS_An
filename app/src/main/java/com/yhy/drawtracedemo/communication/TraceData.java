package com.yhy.drawtracedemo.communication;

import java.util.Date;

/**
 * Created by root on 17-3-13.
 */

public class TraceData {
    private int id;

    private String carID;

    private Date locatetime;

    private double latitude;

    private double longitude;

    private double speed;

    private double angle;


    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public String getCarID() {
        return carID;
    }
    public void setCarID(String carID) {
        this.carID = carID;
    }
    public void setLocatetime(Date locatetime){
        this.locatetime = locatetime;
    }
    public Date getLocatetime(){
        return this.locatetime;
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public double getLatitude(){
        return this.latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public double getLongitude(){
        return this.longitude;
    }
    public void setSpeed(double speed){
        this.speed = speed;
    }
    public double getSpeed(){
        return this.speed;
    }
    public void setAngle(double angle){
        this.angle = angle;
    }
    public double getAngle(){
        return this.angle;
    }
}

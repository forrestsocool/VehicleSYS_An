package com.yhy.drawtracedemo.communication;


import java.util.ArrayList;
import java.util.Date;

/**
 * Created by T800 on 2017/1/12.
 */
public class CarData{

    private String id;

    private String sim;

    private String license;

    private String type;

    private int busload;

    private Date locatetime;

    private Date recordtime;

    private double latitude;

    private double longitude;

    private double speed;

    private double angle;

    private double mile;

    private byte state;

    public enum RegionEnum { OnTheRoad, TongXinTuan, CangKu, KeLan, WuZhai, SanJing, ShiWuHao, SiHao }

    public RegionEnum region = null;

    public ArrayList<TraceData> mTraceDataList;


    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setSim(String sim){
        this.sim = sim;
    }
    public String getSim(){
        return this.sim;
    }
    public void setLicense(String license){
        this.license = license;
    }
    public String getLicense(){
        return this.license;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
    public void setBusload(int busload){
        this.busload = busload;
    }
    public int getBusload(){
        return this.busload;
    }
    public void setLocatetime(Date locatetime){
        this.locatetime = locatetime;
    }
    public Date getLocatetime(){
        return this.locatetime;
    }
    public void setRecordtime(Date recordtime){
        this.recordtime = recordtime;
    }
    public Date getRecordtime(){
        return this.recordtime;
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
    public void setMile(double mile){
        this.mile = mile;
    }
    public double getMile(){
        return this.mile;
    }
    public void setState(byte state){
        this.state = state;
    }
    public byte getState(){
        return this.state;
    }

    public String getDirection()
    {
        int angle = (int)this.getAngle();
        String result = "北";
        switch ((int)((angle + 22.5)/45))
        {
            case 0:
                result = "北";
                break;
            case 1:
                result = "东北";
                break;
            case 2:
                result = "东";
                break;
            case 3:
                result = "东南";
                break;
            case 4:
                result = "南";
                break;
            case 5:
                result = "西南";
                break;
            case 6:
                result = "西";
                break;
            case 7:
                result = "西北";
                break;
        }
        return result;
    }
}

package com.yhy.drawtracedemo.event;

import com.yhy.drawtracedemo.communication.CarData;

import java.util.ArrayList;

/**
 * Created by T800 on 2017/1/12.
 */
public class MarkerUpdateEvent {
    public ArrayList<CarData> listCarData;
    public MarkerUpdateEvent(ArrayList<CarData> list)
    {
        this.listCarData = list;
    }
}

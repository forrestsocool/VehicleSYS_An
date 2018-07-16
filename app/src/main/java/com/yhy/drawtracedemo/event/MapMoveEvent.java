package com.yhy.drawtracedemo.event;

import com.yhy.drawtracedemo.communication.CarData;

import org.osmdroid.util.GeoPoint;

/**
 * Created by T800 on 2017/5/3.
 */

public class MapMoveEvent {
    public GeoPoint mPoint;
    public MapMoveEvent(GeoPoint p)
    {
        this.mPoint = p;
    }
}

package com.yhy.drawtracedemo.communication;


import android.graphics.Color;

import com.yhy.drawtracedemo.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by T800 on 2017/1/12.
 */
public class CarMarker {
    private String id;
    private Marker marker;
    private Polyline traceLine;



    private boolean outside = false;
    public boolean isTraceInited = false;
    public int packet = 0;

    public Polyline line;
    public List<GeoPoint> pts;
    public List<GeoPoint> ptsEmpty;

    public CarMarker()
    {
        line = new Polyline();
        pts = new ArrayList<>();
        ptsEmpty = new ArrayList<>();

        line.setTitle("Central Park, NYC");
        line.setSubDescription(Polyline.class.getCanonicalName());
        line.setPoints(pts);
        line.setGeodesic(true);
        line.setWidth(5f);
        line.setColor(Color.CYAN);
    }

//    line.setWidth(20f);
//    List<GeoPoint> pts = new ArrayList<>();
//    //here, we create a polygon, note that you need 5 points in order to make a closed polygon (rectangle)
//
//    pts.add(new GeoPoint(40.796788, -73.949232));
//    pts.add(new GeoPoint(40.796788, -73.981762));
//    pts.add(new GeoPoint(40.768094, -73.981762));
//    pts.add(new GeoPoint(40.768094, -73.949232));
//    pts.add(new GeoPoint(40.796788, -73.949232));
//    line.setPoints(pts);
//    line.setGeodesic(true);
//    line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mMapView));
//    //Note, the info window will not show if you set the onclick listener
//    //line can also attach click listeners to the line
//		/*
//		line.setOnClickListener(new Polyline.OnClickListener() {
//			@Override
//			public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
//				Toast.makeText(context, "Hello world!", Toast.LENGTH_LONG).show();
//				return false;
//			}
//		});*/
//    mMapView.getOverlayManager().add(line);

    private CarData carData;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setMarker(Marker marker){
        this.marker = marker;
    }
    public Marker getMarker(){
        return this.marker;
    }
    public CarData getCarData() {
        return carData;
    }

    public void setCarData(CarData carData) {
        this.carData = carData;
    }

    public boolean isOutside() {
        return outside;
    }

    public void setOutside(boolean selected) {
        this.outside = selected;
    }
}

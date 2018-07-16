package com.yhy.drawtracedemo.communication;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by T800 on 2017/1/13.
 */
public class StickyService extends Service{

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 启动service的同时也显示view
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        startForeground(1383838438, new Notification());
//        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
//        stopForeground(true);
        super.onDestroy();
    }
}

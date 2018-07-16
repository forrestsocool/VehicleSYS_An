package com.yhy.drawtracedemo.communication;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yhy.drawtracedemo.R;
import com.yhy.drawtracedemo.event.MarkerUpdateEvent;
import com.yhy.drawtracedemo.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by T800 on 2017/1/13.
 */
public class BackstageService extends Service {
    //用于保存查询到的全部最新车辆信息
    private ArrayList<CarData> listCarData;
    //定时器
    private Timer timerQuery;
    //网络查询超时
    private static final int OUT_OF_TIME_SECONDS = 10;
    //数据接口地址
    private static final String CAR_DATA_URL = "http://59.111.102.177/";

    //测试数据
    private double x1 = 38.776222, y1 = 111.761491, x2 = 38.786222, y2 = 111.731491;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ssl", "BackstageService:onCreate");
        listCarData = new ArrayList<>();
        timerQuery = new Timer();
        //每1000毫秒执行一次
        timerQuery.schedule(timerTaskQuery, 0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //return super.onStartCommand(intent, flags, startId);
//        if (Build.VERSION.SDK_INT < 18) {
//            startForeground(1383838438, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
//        } else {
//            startService(new Intent(this, BackstageService.class));
//            startForeground(1383838438, new Notification());
//        }
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public BackstageService() {
        super();
    }

    private TimerTask timerTaskQuery = new TimerTask()
    {
        @Override
        public void run() {

            //读取用户设置后台服务器地址
            SharedPreferences mySP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String CarDataUrl = mySP.getString(getApplicationContext().getString(R.string.server_host),CAR_DATA_URL);

            CarDataUrl += "/beidou";
            //用于查询的OkHttp对象
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .readTimeout(OUT_OF_TIME_SECONDS, TimeUnit.SECONDS)
                    .connectTimeout(OUT_OF_TIME_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(OUT_OF_TIME_SECONDS, TimeUnit.SECONDS)
                    .build();
            Request mRequest = new Request.Builder()
                    .url(CarDataUrl)
                    .addHeader("X-Requested-With", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586")
                    .build();
            //从测试页面获取返回的html页面string
            try {
                Response response = httpClient.newCall(mRequest).execute();
                int responseCode = response.code();
                if (responseCode == 200 || responseCode == 302 || responseCode == 400 || responseCode == 404)
                {
                    String htmlBody=response.body().string();
                    try(ResponseBody body = response.body()) {
                        //Toast.makeText(MainActivity.this,"连接远程数据库成功...",Toast.LENGTH_LONG).show();
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

                        listCarData = gson.fromJson(htmlBody, new TypeToken<List<CarData>>() {
                        }.getType());

                        Log.d("BackstageService",String.valueOf(x1)+';'+String.valueOf(y1));

                        EventBus.getDefault()
                                .post(new MarkerUpdateEvent(listCarData));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        EventBus.getDefault().post(new MessageEvent("远程数据库读取失败"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new MessageEvent("远程数据库连接失败"));
            }
        }
    };
}

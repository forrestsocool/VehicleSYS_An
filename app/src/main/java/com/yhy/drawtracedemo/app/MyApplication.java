package com.yhy.drawtracedemo.app;

import android.app.Activity;
import android.app.Application;
import android.os.Process;

import java.util.ArrayList;

/**
 * Created by T800 on 2017/1/12.
 */
public class MyApplication extends Application {

    public static String BroadcastAction = "com.yhy.drawtracedemo.CAR_DATA";
//    private Workspace mWorkspace = null;
//    private WorkspaceConnectionInfo mInfo = null;
    private boolean isOpen = false;

    public static String DATAPATH = "";
    // 获取Android设备内部存储器根目录，SDCARD
    public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

    private static MyApplication sInstance = null;

    // Activity 列表
    private ArrayList<Activity> mActivities = new ArrayList<Activity>();

    @Override
    public void onCreate()
    {
        super.onCreate();

        // 获取应用程序的文件目录，DATAPATH
        DATAPATH = this.getFilesDir().getAbsolutePath() + "/";
        sInstance = this;
    }

    /**
     *  打开工作空间
     * @param server    工作空间路径
     * @return          返回成功或失败
     */
    public boolean openWorkspace(String server){
        if(isOpen){
            return isOpen;
        }
//        mWorkspace = new Workspace();
//        mInfo = new WorkspaceConnectionInfo();
//        mInfo.setServer(server);
//        WorkspaceType type = (server.endsWith("SMWU")||server.endsWith("smwu"))?WorkspaceType.SMWU:WorkspaceType.SXWU;
//        mInfo.setType(type);
//        isOpen =  mWorkspace.open(mInfo);
        return isOpen;
    }

    /**
     * 将Activity加入到 Activity列表中
     * @param act   需要添加的activity
     */
    public void registerActivity(Activity act){
        mActivities.add(act);
    }


    public static MyApplication getInstance()
    {
        return sInstance;
    }


    /**
     * 退出应用
     */
    public void exit(){

//        mInfo = null;
//        mWorkspace = null;

        for(Activity act:mActivities){
            act.finish();
        }
        Process.killProcess(Process.myPid());
    }
}

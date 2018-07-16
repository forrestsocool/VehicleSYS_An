package com.yhy.drawtracedemo.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.yhy.drawtracedemo.R;
import com.yhy.drawtracedemo.event.MessageEvent;
import com.yhy.drawtracedemo.event.VehicleEventAddEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class DataServer {

    private static final String HTTPS_AVATARS1_GITHUBUSERCONTENT_COM_LINK = "https://avatars1.githubusercontent.com/u/7698209?v=3&s=460";
    private static final String CYM_CHAD = "CymChad";
    private static List<MultipleItem> list = new ArrayList<>();


    private DataServer() {
    }
    public static List<MultipleItem> getMultipleItemData() {
        return list;
    }

    public static void addData(String content,String title, Drawable img)
    {
        list.add(new MultipleItem(MultipleItem.TEXT, MultipleItem.TEXT_SPAN_SIZE, content, title, img));
        EventBus.getDefault().post(new VehicleEventAddEvent());
    }
}

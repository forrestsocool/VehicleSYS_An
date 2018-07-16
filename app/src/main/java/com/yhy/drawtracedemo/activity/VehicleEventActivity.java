package com.yhy.drawtracedemo.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yhy.drawtracedemo.R;
import com.yhy.drawtracedemo.adapter.DataServer;
import com.yhy.drawtracedemo.adapter.MultipleItem;
import com.yhy.drawtracedemo.adapter.MultipleItemQuickAdapter;
import com.yhy.drawtracedemo.event.MessageEvent;
import com.yhy.drawtracedemo.event.VehicleEventAddEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 * modify by AllenCoder
 */
public class VehicleEventActivity extends BaseRecycleActivity {
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);


        //第一步：设置带有recyclerview的布局
        setContentView(R.layout.activity_vehicle_event);
        setTitle("行车日志");
        setBackBtn();

        //第二步：为该布局设置布局管理器
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(manager);

        //第三步，设置Adapter
        final List<MultipleItem> data = DataServer.getMultipleItemData();
        final MultipleItemQuickAdapter multipleItemAdapter = new MultipleItemQuickAdapter(this, data);

//        //3.1设置span
//        multipleItemAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
//                return data.get(position).getSpanSize();
//            }
//        });
//
//        //3.2设置adapter
        mRecyclerView.setAdapter(multipleItemAdapter);

        //非空换背景
        if(data!=null && data.size() > 0)
        {
            mRecyclerView.setBackground(ContextCompat.getDrawable(this,R.drawable.haslog));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(VehicleEventAddEvent event) {
        mRecyclerView.setBackground(ContextCompat.getDrawable(this,R.drawable.haslog));
    }
}

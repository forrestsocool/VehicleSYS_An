package com.yhy.drawtracedemo.communication;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhy.drawtracedemo.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by T800 on 2017/4/20.
 */

public class OutcarAdapter extends BaseAdapter {

    private List<CarData> mData;//定义数据。
    private LayoutInflater mInflater;//定义Inflater,加载我们自定义的布局。

    /*
    定义构造器，在Activity创建对象Adapter的时候将数据data和Inflater传入自定义的Adapter中进行处理。
    */
    public OutcarAdapter(LayoutInflater inflater,List<CarData> data){
        mInflater = inflater;
        mData = data;
    }

    public void refresh(ArrayList<CarData> list) {
        mData = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup viewGroup) {
        //获得ListView中的view
        View viewCardata = mInflater.inflate(R.layout.outside_list_item,null);
        //获得学生对象
        CarData carItem = mData.get(position);
        //获得自定义布局中每一个控件的对象。
        ImageView imagePhoto = (ImageView) viewCardata.findViewById(R.id.image_photo);
        TextView name = (TextView) viewCardata.findViewById(R.id.textview_name);
        //TextView age = (TextView) viewCardata.findViewById(R.id.textview_age);
        //TextView sex = (TextView) viewCardata.findViewById(R.id.textview_sex);
        TextView hobby = (TextView) viewCardata.findViewById(R.id.textview_hobby);
        //将数据一一添加到自定义的布局中。
        imagePhoto.setImageResource(R.drawable.car_t1);
        switch (carItem.getBusload())
        {
            case 1:
                imagePhoto.setImageResource(R.drawable.car_t1);
                break;
            case 2:
                imagePhoto.setImageResource(R.drawable.car_t2);
                break;
            case 3:
                imagePhoto.setImageResource(R.drawable.car_t3);
                break;
            case 4:
                imagePhoto.setImageResource(R.drawable.car_t4);
                break;
            case 5:
                imagePhoto.setImageResource(R.drawable.car_t5);
                break;
            case 6:
                imagePhoto.setImageResource(R.drawable.car_t6);
                break;
             default:
                break;
        }
        name.setText(carItem.getLicense());
        //age.setText(carItem.getType());
        //sex.setText(student.getSex());

        //offline cars
        if(Math.abs(carItem.getLocatetime().getTime() - new Date().getTime()) < 1000 * 180 )
        {
            hobby.setText(String.valueOf((int)carItem.getSpeed()) + "Km/h");
        }
        else
        {
            hobby.setText("车辆离线");
        }

        return viewCardata ;
    }
}

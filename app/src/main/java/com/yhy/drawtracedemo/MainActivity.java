package com.yhy.drawtracedemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yhy.drawtracedemo.activity.AboutActivity;
import com.yhy.drawtracedemo.activity.BaseActivity;
import com.yhy.drawtracedemo.activity.SettingsActivity;
import com.yhy.drawtracedemo.activity.VehicleEventActivity;
import com.yhy.drawtracedemo.adapter.DataServer;
import com.yhy.drawtracedemo.communication.BackstageService;
import com.yhy.drawtracedemo.communication.CarData;
import com.yhy.drawtracedemo.communication.CarMarker;
import com.yhy.drawtracedemo.communication.OutcarAdapter;
import com.yhy.drawtracedemo.communication.TraceData;
import com.yhy.drawtracedemo.event.MapMoveEvent;
import com.yhy.drawtracedemo.event.MarkerUpdateEvent;
import com.yhy.drawtracedemo.event.MessageEvent;
import com.yhy.drawtracedemo.util.MapViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;
import org.osmdroid.events.MapAdapter;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.modules.ArchiveFileFactory;
import org.osmdroid.tileprovider.modules.DatabaseFileArchive;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends BaseActivity {

    //用于登陆的OkHttp对象
    OkHttpClient httpClient;

    //是否初始化Markers
    boolean isMarkerInitiated = false;

    //外出车辆列表
    public ArrayList<CarData> outcarList = new ArrayList<>();
    public OutcarAdapter outcarAdapter;
    //外出车辆列表控件
    ListView mOutcarListView;
    //通信团大轮廓
    BoundingBox TxtBdBox = new BoundingBox(38.79934118184362, 111.737650710616, 38.79287493616523, 111.73349615485091);

    //通信团默认坐标
    public static final GeoPoint TONGXINTUAN = new GeoPoint(38.7947675, 111.734704);
    //517医院
    public static final GeoPoint YIYUAN = new GeoPoint(38.776222, 111.751491);


    //用于存放车辆位置的集合
    HashMap<String, CarMarker> mapMarkers = new HashMap<String, CarMarker>();


    FolderOverlay myFolderOverlay = new FolderOverlay();


    //网络查询超时
    private static final int OUT_OF_TIME_SECONDS = 10;
    //数据接口地址
    private static final String CAR_DATA_URL = "http://59.111.102.177/";

    //osm地图控件
    private MapView mapView;

    //当前是否正在使用离线地图
    private boolean isUsingOfflineMap = true;

    //离线地图tileProvider
    private OfflineTileProvider tileProvider;

    //Toolbar控件
    Toolbar toolbar;
    String toolbarTitle = "";
    Menu menuToolbar;
    MenuItem menuToolbarItemGoHome;
    MenuItem menuToolbarItemGeoFenceStatus;
    MenuItem menuToolbarItemNotification;
    MenuItem menuToolbarItemAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    //don't finish app when back key pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initView() {
        super.initView();
        setToolbarDrawerLayout();
        mapView = (MapView) findViewById(R.id.map);

        //外出车辆列表初始化
        mOutcarListView = (ListView) findViewById(R.id.outcar_list_view);
        LayoutInflater inflater = getLayoutInflater();
        outcarAdapter = new OutcarAdapter(inflater, outcarList);
        mOutcarListView.setAdapter(outcarAdapter);
        //设置列表点击跳转
        mOutcarListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                CarData clickedCarData = (CarData) outcarList.get(position); //(CarData) parent.getAdapter().getItem(position);
                EventBus.getDefault().post(new MapMoveEvent(new GeoPoint(clickedCarData.getLatitude(), clickedCarData.getLongitude())));
            }
        });

        //fakeCarData();

        Date t1 = new Date();

        //mapView.getOverlayManager().getTilesOverlay().setLoadingDrawable(ContextCompat.getDrawable(this,R.drawable.maploading));
                //getResources().getDrawable(R.drawable.hospital));

        MapViewUtil.SetMapViewOfflineData(this, mapView, tileProvider);
        MapViewUtil.SetZoomControls(getApplicationContext(), mapView, (RelativeLayout) MainActivity.this.findViewById(R.id.ll_map), R.drawable.zoom_in, R.drawable.zoom_out);


        //监听地图操作，14级以上用本地，以下用Online
        mapView.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                //Log.i(IMapView.LOGTAG, System.currentTimeMillis() + " onScroll " + event.getX() + "," +event.getY() );
                //Toast.makeText(getActivity(), "onScroll", Toast.LENGTH_SHORT).show();
                //updateInfo();
                //onScroll(super);
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                if (event.getZoomLevel() > 14 && !isUsingOfflineMap) {
                    MapViewUtil.SetMapViewOfflineData(getApplicationContext(), mapView, tileProvider);
                    MapViewUtil.BoostMapOverlays(mapView);
                    isUsingOfflineMap = true;
                }

                if (event.getZoomLevel() <= 14 && isUsingOfflineMap) {
                    //设置在线地图数据源
                    MapViewUtil.SetMapViewOnlineData(getApplicationContext(),mapView, myFolderOverlay);
                    //设大内存缓存
                    MapViewUtil.BoostMapOverlays(mapView);
                    isUsingOfflineMap = false;
                }
                return true;
            }
        });

        //添加地标
        MapViewUtil.AddSpecialMarkers(this, mapView,myFolderOverlay);
        setTheme(R.style.AppTheme);

        //计时
        Log.d("time_expense", String.valueOf(new Date().getTime() - t1.getTime()));

        mapView.setClickable(true);
        // mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        //设置不使用网络
        mapView.setUseDataConnection(true);
        //使用cache
        mapView.setDrawingCacheEnabled(true);
        //设置缩放级别
        mapView.setMaxZoomLevel(17);
        mapView.setMinZoomLevel(1);

        //设置比例尺
        ScaleBarOverlay scalebar = new ScaleBarOverlay(mapView);
        mapView.getOverlays().add(scalebar);

        // 地图范围控制
        // http://stackoverflow.com/questions/5403733/restricting-the-area-the-user-can-go-to-on-mapview
        // BoundingBox areaLimit = new BoundingBox(39.070379, 111.588326, 38.671199, 111.770683);
        // mapView.setScrollableAreaLimitDouble(areaLimit);

        //设置启动视角
        IMapController mapViewController = mapView.getController();
        mapViewController.setZoom(16);
        mapViewController.setCenter(TONGXINTUAN);

        //设置大内存缓存boost
        MapViewUtil.BoostMapOverlays(mapView);
    }

    void setToolbarDrawerLayout() {
        //Toolbar生成
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(menuItemClickListener);

        //toolbar具体设置
        menuToolbar = toolbar.getMenu();
        menuToolbarItemAbout = menuToolbar.findItem(R.id.menuToolbarItemAbout);
        menuToolbarItemGoHome = menuToolbar.findItem(R.id.menuToolbarItemGoHome);
        menuToolbarItemGeoFenceStatus = menuToolbar.findItem(R.id.menuToolbarItemGeoFenceStatus);
        menuToolbarItemNotification = menuToolbar.findItem(R.id.menuToolbarItemNotification);

        toolbar.setTitle(toolbarTitle);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setLogo(R.drawable.toolbar_logo);
    }

    public void initMarkers() {
        //用于查询的OkHttp对象
        httpClient = new OkHttpClient.Builder()
                .readTimeout(OUT_OF_TIME_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(OUT_OF_TIME_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(OUT_OF_TIME_SECONDS, TimeUnit.SECONDS)
                .build();
        Request mRequest = new Request.Builder()
                .url(CAR_DATA_URL)
                .addHeader("X-Requested-With", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586")
                .build();

        httpClient.newCall(mRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Toast.makeText(MainActivity.this,"连接远程数据库失败...",Toast.LENGTH_LONG).show();
                EventBus.getDefault().post(new MessageEvent("远程数据库连接失败"));
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                ArrayList<CarData> carDataList;
                String jsonData = "[{\"id\":88880001,\"sim\":\"1383838388\",\"license\":\"BH00123\",\"type\":\"\\u767d\\u8272\\u4f9d\\u7ef4\\u67ef\",\"busload\":\"10\",\"locatetime\":\"2017-01-06 13:23:33\",\"recordtime\":\"2017-01-06 13:23:52\",\"latitude\":\"38.776222\",\"longitude\":\"111.761491\",\"speed\":\"50\",\"angle\":\"0\",\"mile\":\"5000.00\",\"state\":\"0\"},{\"id\":88880002,\"sim\":\"1388888888\",\"license\":\"BH05001\",\"type\":\"\\u7eff\\u8272\\u6851\\u5854\\u7eb3\",\"busload\":\"10\",\"locatetime\":\"1991-11-22 00:00:00\",\"recordtime\":\"1991-11-22 00:00:00\",\"latitude\":\"38.786222\",\"longitude\":\"111.731491\",\"speed\":\"50\",\"angle\":\"0\",\"mile\":\"50.00\",\"state\":\"0\"}]\n";
                try (ResponseBody body = response.body()) {
                    //Toast.makeText(MainActivity.this,"连接远程数据库成功...",Toast.LENGTH_LONG).show();
                    carDataList = gson.fromJson(jsonData, new TypeToken<List<CarData>>() {
                    }.getType());

                    for (CarData carData : carDataList) {
                        //仅新增没有的id的车信息
                        if (!mapMarkers.containsKey(carData.getId())) {
                            CarMarker marker = new CarMarker();
                            marker.setId(carData.getId());
                            marker.setCarData(carData);
                            mapMarkers.put(carData.getId(), marker);
                        } else {
                            mapMarkers.get(carData.getId()).setCarData(carData);
                        }
                    }

                    EventBus.getDefault()
                            .post(new MarkerUpdateEvent(carDataList));
                    EventBus.getDefault().post(new MessageEvent("远程数据库连接成功"));
                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new MessageEvent("远程数据库读取失败"));
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        //启动车辆最新信息fetch服务
        Intent insertService = new Intent(this, BackstageService.class);
        startService(insertService);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    private Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menuToolbarItemAbout:
                    //Toast.makeText(MainActivity.this, "action_settings", 0).show();
                    Intent intentAboutActivity = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivity(intentAboutActivity);
                    break;
                case R.id.menuToolbarItemSettings:
                    //Toast.makeText(MainActivity.this, "action_share", 0).show();
                    Intent intentSettingActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intentSettingActivity);
                    break;

                case R.id.menuToolbarItemGoHome:
                    IMapController mapViewController = mapView.getController();
                    mapViewController.setZoom(17);
                    mapViewController.animateTo(TONGXINTUAN);
                    break;
                case R.id.menuToolbarItemNotification:
                    Intent intentEventActivity = new Intent(getApplicationContext(), VehicleEventActivity.class);
                    startActivity(intentEventActivity);
                    item.setIcon(R.drawable.menu_toolbar_ic_notifications_off);
                    break;
                case R.id.menuToolbarItemGeoFenceStatus:
                    break;
                default:
                    break;
            }
            return true;
        }
    };


    public void fakeCarData() {
        CarData car = new CarData();
        car.setBusload(1);
        car.setType("afadsfadsf");
        car.setSpeed(21);
        car.setLicense("BH02231");
        outcarList.add(car);

        CarData car2 = new CarData();
        car2.setBusload(2);

        car2.setType("af5523dsf");
        car2.setSpeed(51);
        car2.setLicense("BH02232");
        outcarList.add(car2);

        CarData car3 = new CarData();
        car3.setBusload(3);
        car3.setType("af5523234");
        car3.setSpeed(52);
        car3.setLicense("BH02235");
        outcarList.add(car3);
    }

    //用于更新markers
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MarkerUpdateEvent event) {
        if (!isMarkerInitiated) {
            for (CarData carData : event.listCarData) {
                Drawable image = ContextCompat.getDrawable(this, R.drawable.icon_car);// getResources().getDrawable(R.drawable.sfppt);
                Drawable icon = ContextCompat.getDrawable(this, R.drawable.icon_car);

                switch (carData.getBusload()) {
                    case 1:
                        image = ContextCompat.getDrawable(this, R.drawable.icon_yiweike);
                        break;
                    case 2:
                        image = ContextCompat.getDrawable(this, R.drawable.icon_jiaoche);
                        break;
                    case 3:
                        image = ContextCompat.getDrawable(this, R.drawable.icon_daba);
                        break;
                    case 4:
                        image = ContextCompat.getDrawable(this, R.drawable.icon_yongshi);
                        break;
                    case 5:
                        image = ContextCompat.getDrawable(this, R.drawable.icon_kache);
                        break;
                    case 6:
                        image = ContextCompat.getDrawable(this, R.drawable.icon_kehuo);
                        break;
                    default:
                        break;
                }

                Marker marker = new Marker(mapView);
                marker.setImage(image);
                marker.setIcon(icon);
                marker.setTitle(carData.getType());
                marker.setPosition(new GeoPoint(carData.getLatitude(), carData.getLongitude()));
                marker.setSnippet("<html><body>" +
                        "<p><strong>车牌: </strong>" + carData.getLicense() + "<br>" +
                        "<strong>车速: </strong>" + carData.getSpeed() + "km/h" + "<br>" +
                        "<strong>方向: </strong>" + carData.getDirection() + "<br>" +
                        "</p></body></html>");

                CarMarker carMarker = new CarMarker();
                carMarker.setCarData(carData);
                carMarker.setMarker(marker);
                carMarker.setId(carData.getId());
                mapMarkers.put(carData.getId(), carMarker);
                //mapView.getOverlayManager().add(carMarker.line);
                //mapView.getOverlayManager().add(carMarker.getMarker());
            }

            for(CarMarker cMarker : mapMarkers.values())
            {
                mapView.getOverlayManager().add(cMarker.line);
                myFolderOverlay.add(cMarker.line);
                //myFolderOverlay.add(cMarker.getMarker());
            }
            for(CarMarker cMarker : mapMarkers.values())
            {
                mapView.getOverlayManager().add(cMarker.line);
                //myFolderOverlay.add(cMarker.line);
                myFolderOverlay.add(cMarker.getMarker());
            }

            mapView.getOverlays().removeAll(myFolderOverlay.getItems());
            mapView.getOverlays().addAll(myFolderOverlay.getItems());
            isMarkerInitiated = true;
        }
        //初始化完毕 正式记录数据
        else {
            outcarList.clear();
            for (CarData carData : event.listCarData) {


                //监测驶入驶离
                checkRegionChange(carData);

                //将不在单位的车辆加入列表
                if (!TxtBdBox.contains(carData.getLatitude(), carData.getLongitude())) {
                    outcarList.add(carData);
                }


                final CarMarker mCarMarker = mapMarkers.get(carData.getId());
                ++mCarMarker.packet;
                Marker marker = mCarMarker.getMarker();
                marker.setPosition(new GeoPoint(carData.getLatitude(), carData.getLongitude()));
                //mapView.getController().setCenter(new GeoPoint(carData.getLatitude(),carData.getLongitude()));

                //online cars
                if (Math.abs(carData.getLocatetime().getTime() - new Date().getTime()) < 1000 * 180) {
                    //设置超速告警
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    checkOverSpeed(carData, sharedPreferences);

                    marker.setSnippet("<html><body>" +
                            "<p><strong>车牌: </strong>" + carData.getLicense() + "<br>" +
                            "<strong>车速: </strong>" + carData.getSpeed() + "km/h" + "<br>" +
                            "<strong>方向: </strong>" + carData.getDirection() + "<br>" +
                            "</p></body></html>");

                    //shining
                    if (mCarMarker.packet % 2 == 0) {
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.icon_car));
                    } else {
                        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.icon_car_running));
                    }
                } else {
//                    marker.setSubDescription(
//                            "车辆离线"
//                    );
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
                    marker.setSnippet("<html><body>" +
                            "<p><strong>车牌: </strong>" + carData.getLicense() + "<br>" +
                            "<strong>车辆离线: </strong>" + sdf.format(carData.getLocatetime()) + "<br>" +
                            "</p></body></html>");
                    marker.setIcon(ContextCompat.getDrawable(this, R.drawable.icon_car_stop));
                }

                if (mCarMarker.isTraceInited) {
                    mCarMarker.pts.add(new GeoPoint(carData.getLatitude(), carData.getLongitude()));

                    SwitchCompat mSwith = (SwitchCompat) this.findViewById(R.id.switch_showtrace);
                    if (mSwith.isChecked() && mCarMarker.getMarker().isInfoWindowShown()) {
                        mCarMarker.line.setPoints(mCarMarker.pts);
                    } else {
                        mCarMarker.line.setPoints(mCarMarker.ptsEmpty);
                    }
                    //mCarMarker.line.setPoints(mCarMarker.pts);
                } else {
                    //读取用户设置后台服务器地址
                    SharedPreferences mySP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String TraceDataUrl = mySP.getString(getApplicationContext().getString(R.string.server_host), CAR_DATA_URL);

                    TraceDataUrl += "/trace/" + String.valueOf(mCarMarker.getId());
                    //用于查询的OkHttp对象
                    OkHttpClient httpClient = new OkHttpClient.Builder()
                            .readTimeout(OUT_OF_TIME_SECONDS, TimeUnit.SECONDS)
                            .connectTimeout(OUT_OF_TIME_SECONDS, TimeUnit.SECONDS)
                            .writeTimeout(OUT_OF_TIME_SECONDS, TimeUnit.SECONDS)
                            .build();
                    Request mRequest = new Request.Builder()
                            .url(TraceDataUrl)
                            .addHeader("X-Requested-With", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586")
                            .build();
                    //mCarMarker.pts.addAll();
                    //
                    try {
                        httpClient.newCall(mRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                // 注：该回调是子线程，非主线程
                                Log.i("wxy", "callback thread id is " + Thread.currentThread().getId());
                                //Log.i("wxy",response.body().string());

                                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

                                ArrayList<TraceData> listTraceData = gson.fromJson(response.body().string(), new TypeToken<List<TraceData>>() {
                                }.getType());

                                mCarMarker.pts.clear();
                                for (TraceData tdata : listTraceData) {
                                    mCarMarker.pts.add(new GeoPoint(tdata.getLatitude(), tdata.getLongitude()));
                                }
                                mCarMarker.isTraceInited = true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //刷新InfoWindow，并开启跟踪
                if(marker.isInfoWindowShown())
                {
                    marker.closeInfoWindow();
                    marker.showInfoWindow();

                    //根据开关是否开启跟踪
                    SwitchCompat mSwith =  (SwitchCompat) this.findViewById(R.id.switch_track_car);
                    if(mSwith.isChecked()) {
                        mapView.getController().setCenter(marker.getPosition());
                    }
                }
            }
            outcarAdapter.notifyDataSetChanged();
        }
        mapView.postInvalidate();
    }

    private void checkRegionChange(CarData carData) {
        //记录所在区域变更
        CarData.RegionEnum oldRegion = carData.region;
        carData.region = MapViewUtil.GetCurrentRegion(new GeoPoint(carData.getLatitude(),carData.getLongitude()));

        if(carData.getLicense().equals("测试"))
        {
            EventBus.getDefault().post(new MessageEvent(carData.region.toString()));
        }
        //未初始化的时候不比较
        if(oldRegion == null) {
            return;
        }


        //有驶入驶离
        if(oldRegion != carData.region)
        {
            //DataServer.addData(String.format("[%s]超速%dKm/h",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString(),carData.getSpeed()),

            String info = "[%s]车辆%s%s%s";
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(carData.getLocatetime());
            String action = "进入";
            String strRegion = "NULL";

            //驶入
            if(carData.region != CarData.RegionEnum.OnTheRoad
                    && oldRegion == CarData.RegionEnum.OnTheRoad)
            {
               // public enum RegionEnum { OnTheRoad, TongXinTuan, CangKu, KeLan, WuZhai, SanJing, ShiWuHao, SiHao }

                switch (carData.region)
                {
                    case TongXinTuan:
                        strRegion = "26#区";
                        break;
                    case CangKu:
                        strRegion = "仓库";
                        break;
                    case ShiWuHao:
                        strRegion = "15#区";
                        break;
                    case SanJing:
                        strRegion = "三井";
                        break;
                    case SiHao:
                        strRegion = "4#区";
                        break;
                    case KeLan:
                        strRegion = "岢岚县";
                        break;
                    case WuZhai:
                        strRegion = "五寨县";
                        break;
                }
            }
            //驶离
            else if(carData.region == CarData.RegionEnum.OnTheRoad
                    && oldRegion != CarData.RegionEnum.OnTheRoad)
            {
                action = "离开";
                switch (oldRegion)
                {
                    case TongXinTuan:
                        strRegion = "26#区";
                        break;
                    case CangKu:
                        strRegion = "仓库";
                        break;
                    case ShiWuHao:
                        strRegion = "15#区";
                        break;
                    case SanJing:
                        strRegion = "三井";
                        break;
                    case SiHao:
                        strRegion = "4#区";
                        break;
                    case KeLan:
                        strRegion = "岢岚县";
                        break;
                    case WuZhai:
                        strRegion = "五寨县";
                        break;
                }
            }

            String regionInfo = String.format(Locale.CHINA,info,time,carData.getLicense(),action,strRegion);
            DataServer.addData(regionInfo, carData.getLicense(), mapMarkers.get(carData.getId()).getMarker().getImage());
            menuToolbarItemNotification.setIcon(R.drawable.menu_toolbar_ic_notifications_on);
            EventBus.getDefault().post(new MessageEvent(regionInfo));
        }
    }

    private void checkOverSpeed(CarData carData, SharedPreferences mySP) {
        float overSpeed = 90;
        try {
            String strSpeed = mySP.getString(getApplicationContext().getString(R.string.over_speed), "90");
            overSpeed = Float.parseFloat(strSpeed);
        }
        catch (Exception e)
        {
        }
        //显示超速信息
        if(carData.getSpeed() > overSpeed) {
            DataServer.addData(String.format(Locale.CHINA,"[%s]车辆超速%dKm/h",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA).format(carData.getLocatetime()),(int)carData.getSpeed()),
                    carData.getLicense(),
                    mapMarkers.get(carData.getId()).getMarker().getImage());
            menuToolbarItemNotification.setIcon(R.drawable.menu_toolbar_ic_notifications_on);
            EventBus.getDefault().post(new MessageEvent(String.format(Locale.CHINA,"[%s]车辆超速：%dKm/h", carData.getLicense(),(int)carData.getSpeed())));
        }
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event) {
//        fakeCarData();
//        outcarAdapter.notifyDataSetChanged();

        String message = event.message;
        Snackbar.make(this.findViewById(R.id.coordinatorLayoutContainer), message, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe
    public void onEventMainThread(MapMoveEvent event) {
        GeoPoint point = event.mPoint;
        IMapController mapViewController = mapView.getController();
        mapViewController.setZoom(17);
        mapViewController.animateTo(point);
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    class ShadeAreaOverlay extends Overlay {

        final GeoPoint mTopLeft;
        final GeoPoint mBottomRight;
        final Point mTopLeftPoint = new Point();
        final Point mBottomRightPoint = new Point();
        Rect area = null;

        public ShadeAreaOverlay(BoundingBox sCentralParkBoundingBox) {
            super();
            mTopLeft = new GeoPoint(sCentralParkBoundingBox.getLatNorth(),
                    sCentralParkBoundingBox.getLonWest());
            mBottomRight = new GeoPoint(sCentralParkBoundingBox.getLatSouth(),
                    sCentralParkBoundingBox.getLonEast());
        }

        @Override
        public void draw(Canvas c, MapView osmv, boolean shadow) {
            if (shadow)
                return;

            final Projection proj = osmv.getProjection();

            if (area == null) {
                proj.toPixels(mTopLeft, mTopLeftPoint);
                proj.toPixels(mBottomRight, mBottomRightPoint);

                area = new Rect(mTopLeftPoint.x, mTopLeftPoint.y, mBottomRightPoint.x,
                        mBottomRightPoint.y);
            }
            Paint mPaint = new Paint();
            mPaint.setColor(Color.argb(50, 255, 0, 0));

            c.drawRect(area, mPaint);
        }
    }
}

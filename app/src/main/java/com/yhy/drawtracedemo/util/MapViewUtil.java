package com.yhy.drawtracedemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yhy.drawtracedemo.R;
import com.yhy.drawtracedemo.communication.CarData;

import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.modules.ArchiveFileFactory;
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
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.io.File;
import java.security.Policy;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by T800 on 2017/1/22.
 */
public class MapViewUtil {
    //txt大轮廓
    private static BoundingBox TxtBdBox = new BoundingBox(38.79934118184362, 111.737650710616, 38.79287493616523, 111.73349615485091);

    private static BoundingBox CangkuBdBox = new BoundingBox(38.7816882901, 111.7423081398, 38.7794050078, 111.7391109467);

    private static BoundingBox ShiWuHaoBox = new BoundingBox(38.7754655468, 111.7643880844, 38.7682719142, 111.7470073700);

    private static BoundingBox SanJingBox = new BoundingBox(38.8084800287, 111.6966247559, 38.7973096665, 111.6821193695);

    private static BoundingBox SiHaoBox = new BoundingBox(38.8945066317, 111.6578292847, 38.8076105424, 111.5771484375);

    private static BoundingBox KeLanBox = new BoundingBox(38.7242243835,111.6058158875, 38.6884576310,111.5496826172);

    private static BoundingBox WuZhaiBox = new BoundingBox(38.9332414033,111.8981552124, 38.8926361423,111.8109512329);

    //获取当前车辆大体区域
    public static CarData.RegionEnum GetCurrentRegion(GeoPoint point)
    {
        CarData.RegionEnum region = CarData.RegionEnum.OnTheRoad;
        if(TxtBdBox.contains(point))
        {
            region = CarData.RegionEnum.TongXinTuan;
        }
        else if(CangkuBdBox.contains(point))
        {
            region = CarData.RegionEnum.CangKu;
        }
        else if(ShiWuHaoBox.contains(point))
        {
            region = CarData.RegionEnum.ShiWuHao;
        }
        else if(SanJingBox.contains(point))
        {
            region = CarData.RegionEnum.SanJing;
        }
        else if(SiHaoBox.contains(point))
        {
            region = CarData.RegionEnum.SiHao;
        }
        else if(KeLanBox.contains(point))
        {
            region = CarData.RegionEnum.KeLan;
        }
        else if(WuZhaiBox.contains(point))
        {
            region = CarData.RegionEnum.WuZhai;
        }

        return region;
    }

    //设置osm使用本地离线地图数据
    public static boolean SetMapViewOfflineData(Context context, MapView mapView, OfflineTileProvider tileProvider){

        if(tileProvider != null)
        {
            mapView.setTileProvider(tileProvider);
            return true;
        }


        //读取用户设置地图包位置
        SharedPreferences mySP = PreferenceManager.getDefaultSharedPreferences(context);

        String defaultLocation = Environment.getExternalStorageDirectory().getPath() + "/osmdroid/map.sqlite";
        String strFilepath = mySP.getString(context.getString(R.string.map_location),defaultLocation);

        File exitFile = new File(strFilepath);
        //String fileName = "map.sqlite";
        String fileName = strFilepath.substring(strFilepath.lastIndexOf('/')+1);
        if (!exitFile.exists()) {
            //mapView.setTileSource(TileSourceFactory.MAPNIK);
            return false;
        }else {
            fileName = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (fileName.length() == 0)
                return false;
            if (ArchiveFileFactory.isFileExtensionRegistered(fileName)) {
                try {
                    tileProvider = new OfflineTileProvider((IRegisterReceiver) new SimpleRegisterReceiver(context), new File[] { exitFile });
                    mapView.setTileProvider(tileProvider);

                    String source = "";
                    IArchiveFile[] archives = tileProvider.getArchives();
                    if (archives.length > 0) {
                        Set<String> tileSources = archives[0].getTileSources();
                        if (!tileSources.isEmpty()) {
                            source = tileSources.iterator().next();
                            mapView.setTileSource(FileBasedTileSource.getSource(source));
                        } else {
                            mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                        }
                    } else
                        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                    LogUtil.d("Using " + exitFile.getAbsolutePath() + " "+ source);
                    mapView.invalidate();
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                LogUtil.d(" did not have any files I can open! Try using MOBAC");
            } else{
                LogUtil.d(" dir not found!");
            }
        }

        return false;
    }

    //设置osm在线地图数据
    public static void SetMapViewOnlineData(Context context, MapView mapView, FolderOverlay mFolderOverlay){
        //卫星图
        final MapTileProviderBasic tileProviderSat = new MapTileProviderBasic(context);
        String[] baseUrlS = {
                "http://24.43.4.30:8000/MapTileDownload/googlemaps/satellite_en/"
        };
        final ITileSource tileSourceSat = new XYTileSource("MyCustomTilesS", 0, 20, 256, ".jpg",baseUrlS);
        tileProviderSat.setTileSource(tileSourceSat);
        mapView.setTileProvider(tileProviderSat);

        //路网图
        final MapTileProviderBasic tileProviderRoute = new MapTileProviderBasic(context);
        String[] baseUrlR = {
                "http://24.43.4.30:8000/MapTileDownload/tianditu/overlay_s/"
        };
        final ITileSource tileSourceRoute = new XYTileSource("MyCustomTilesR", 0, 20, 256, ".png",baseUrlR);
        tileProviderRoute.setTileSource(tileSourceRoute);
        final TilesOverlay tilesOverlayRoute = new TilesOverlay(tileProviderRoute, context);
        tilesOverlayRoute.setLoadingBackgroundColor(Color.TRANSPARENT);
        mapView.getOverlays().add(tilesOverlayRoute);

        //web图
//        final     MapTileProviderBasic tileProviderRoute = new MapTileProviderBasic(context);
//        String[] baseUrlR = {
//                "http://www.google.cn/maps/vt?lyrs=s@725&"
//        };
//        final ITileSource tileSourceRoute = new XYTileSource("MyCustomTilesR", 0, 20, 256, ".png",baseUrlR)
//        {
//            @Override
//            public String getTileURLString(final MapTile aTile) {
//                return getBaseUrl() + "x=" + aTile.getX() + "&y=" + aTile.getY() + "&z=" + aTile.getZoomLevel();
//            }
//        };
//        tileProviderRoute.setTileSource(tileSourceRoute);
//        final TilesOverlay tilesOverlayRoute = new TilesOverlay(tileProviderRoute, context);
//        tilesOverlayRoute.setLoadingBackgroundColor(Color.TRANSPARENT);
//        mapView.getOverlays().add(tilesOverlayRoute);

        mapView.getOverlays().removeAll(mFolderOverlay.getItems());
        mapView.getOverlays().addAll(mFolderOverlay.getItems());


//        for(int i=0; i<mFolderOverlay.getItems().size(); i++)
//        {
//            List<Overlay> list = mFolderOverlay.getItems();
//            Overlay selectedMarker = list.remove(0);
//            list.add(selectedMarker);
//        }


        mapView.invalidate();
    }

    //设置大内存boost
    public static void BoostMapOverlays(MapView mapView)
    {
        //设置大内存缓存boost
        Iterator<Overlay> iterator = mapView.getOverlays().iterator();
        while (iterator.hasNext()) {
            Overlay next = iterator.next();
            if (next instanceof TilesOverlay) {
                TilesOverlay x = (TilesOverlay) next;
                x.setOvershootTileCache(x.getOvershootTileCache() * 2);
                //Toast.makeText(getActivity(), "Tiles overlay cache set to " + x.getOvershootTileCache(), Toast.LENGTH_LONG).show();
                break;
            }
        }
        mapView.invalidate();
    }

    //添加特定地标
    public static void AddSpecialMarkers(Context context, MapView mapView, FolderOverlay mFolderOverlay){
        //here, we create a polygon using polygon class, note that you need 4 points in order to make a rectangle
        Polygon polygonTXT = new Polygon();
        Polygon polygonTXTJSY = new Polygon();
        Polygon polygonTXTYZ = new Polygon();



        Polygon polygonZC = new Polygon();

        {
//            @Override
//            public Paint getOutlinePaint() {
//                //设置画虚线，如果之后不再使用虚线，调用paint.setPathEffect(null);
//                PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
//                //paint.setPathEffect(effects);
//                Paint p = super.getOutlinePaint();
//                p.setPathEffect(effects);
//                return p;
//            }

        };

//        <html><head><title>TextView使用HTML</title></head><body><p><strong>强调</strong></p><p><em>斜体</em></p>"
//                +"<p><a href=\"http://www.dreamdu.com/xhtml/\">超链接HTML入门</a>学习HTML!</p><p><font color=\"#aabb00\">颜色1"
//                +"</p><p><font color=\"#00bbaa\">颜色2</p><h1>标题1</h1><h3>标题2</h3><h6>标题3</h6><p>大于>小于<</p><p>" +
//                "下面是网络图片</p><img src="http://avatar.csdn.net/0/3/8/2_zhang957411207.jpg"/></body></html>

        polygonTXT.setTitle("通信团");
        polygonTXTJSY.setTitle("通信团");
        polygonTXTYZ.setTitle("通信团");
        polygonZC.setTitle("其他地标");
        polygonTXT.setSubDescription("<html><body><p><h3>26#区</h3></p></body></html>");
        polygonTXTJSY.setSubDescription("<html><body><p><h3>家属院</h3></p></body></html>");
        polygonTXTYZ.setSubDescription("<html><body><p><h3>一站</h3></p></body></html>");
        polygonZC.setSubDescription("<html><body><p><h3>砖厂</h3></p></body></html>");
        polygonTXT.setFillColor(Color.argb(30, 0, 255, 0));
        polygonTXTJSY.setFillColor(Color.argb(30, 255, 255, 0));
        polygonTXTYZ.setFillColor(Color.argb(30, 0, 0, 255));
        polygonZC.setFillColor(Color.argb(30, 100, 100, 100));


        polygonTXT.setVisible(true);
        polygonTXT.setStrokeColor(Color.argb(255, 0, 255, 0));
        polygonTXT.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mapView));
        polygonTXT.setStrokeWidth(4);

        polygonTXTJSY.setVisible(true);
        polygonTXTJSY.setStrokeColor(Color.argb(255, 255, 255, 0));
        polygonTXTJSY.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mapView));
        polygonTXTJSY.setStrokeWidth(4);

        polygonTXTYZ.setVisible(true);
        polygonTXTYZ.setStrokeColor(Color.argb(255, 0, 0, 255));
        polygonTXTYZ.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mapView));
        polygonTXTYZ.setStrokeWidth(4);

        polygonZC.setVisible(true);
        polygonZC.setStrokeColor(Color.argb(255, 100, 100, 100));
        polygonZC.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mapView));
        polygonZC.setStrokeWidth(4);

        ArrayList<GeoPoint> pts1 = new ArrayList<>();
        pts1.add(new GeoPoint(38.79934118184362, 111.73387229649781));
        pts1.add(new GeoPoint(38.79934118184362,111.7355060577));
        pts1.add(new GeoPoint(38.7974392969,111.7355060577));
        pts1.add(new GeoPoint(38.7974392969,111.737650710616));
        //pts1.add(new GeoPoint(38.79934118184362, 111.737650710616));
        pts1.add(new GeoPoint(38.79435369708541, 111.737650710616));
        pts1.add(new GeoPoint(38.79435369708541, 111.73387229649781));
        polygonTXT.setPoints(pts1);


        ArrayList<GeoPoint> pts2 = new ArrayList<>();
        pts2.add(new GeoPoint(38.79399602231665, 111.73349615485091));
        pts2.add(new GeoPoint(38.79399602231665, 111.73529741625191));
        pts2.add(new GeoPoint(38.79287493616523, 111.73529741625191));
        pts2.add(new GeoPoint(38.79287493616523, 111.73349615485091));
        polygonTXTJSY.setPoints(pts2);

        ArrayList<GeoPoint> pts3 = new ArrayList<>();
        pts3.add(new GeoPoint(38.7755826878,111.7461329698));
        pts3.add(new GeoPoint(38.77597601394743, 111.74669168780976));
        pts3.add(new GeoPoint(38.77535651707399, 111.74748686889112));
        pts3.add(new GeoPoint(38.774965528201676, 111.74690684062791));
        polygonTXTYZ.setPoints(pts3);

        ArrayList<GeoPoint> pts4 = new ArrayList<>();
        pts4.add(new GeoPoint(38.79934118184362,111.7355760577));
        pts4.add(new GeoPoint(38.79934118184362, 111.737650710616));
        pts4.add(new GeoPoint(38.7974992969,111.737650710616));
        pts4.add(new GeoPoint(38.7974992969,111.7355760577));
        polygonZC.setPoints(pts4);

        mapView.getOverlays().add(polygonTXT);
        mapView.getOverlays().add(polygonTXTJSY);
        mapView.getOverlays().add(polygonTXTYZ);
        mapView.getOverlays().add(polygonZC);


        mFolderOverlay.add(polygonTXT);
        mFolderOverlay.add(polygonTXTJSY);
        mFolderOverlay.add(polygonTXTYZ);
        mFolderOverlay.add(polygonZC);

        //txtShadeAreaOverlay.setEnabled(true);

        mapView.postInvalidate();
    }

    /* ZoomControls */
    public static void SetZoomControls(Context context, final MapView mapView, RelativeLayout view, int drawableZoomOutID, int drawableZoomInID)
    {
        mapView.setBuiltInZoomControls(false);
			/* Create a ImageView with a zoomIn-Icon. */
        final ImageView ivZoomIn = new ImageView(context);
        ivZoomIn.setImageResource(drawableZoomInID);
			/* Create RelativeLayoutParams, that position in in the top right corner. */
        final RelativeLayout.LayoutParams zoominParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        zoominParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        zoominParams.addRule(RelativeLayout.BELOW,R.id.switch_showtrace);
        zoominParams.topMargin = 20;
        zoominParams.rightMargin = 20;
        view.addView(ivZoomIn, zoominParams);
        ivZoomIn.setId(View.generateViewId());

        ivZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mapView.getController().zoomIn();
            }
        });

			/* Create a ImageView with a zoomOut-Icon. */
        final ImageView ivZoomOut = new ImageView(context);
        ivZoomOut.setImageResource(drawableZoomOutID);

			/* Create RelativeLayoutParams, that position in in the top left corner. */
        final RelativeLayout.LayoutParams zoomoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        zoomoutParams.addRule(RelativeLayout.BELOW, ivZoomIn.getId());
        zoomoutParams.addRule(RelativeLayout.ALIGN_LEFT, ivZoomIn.getId());
        zoomoutParams.topMargin = 10;

//        zoomoutParams.topMargin = 150;
        //zoomoutParams.addRule(RelativeLayout.RIGHT_OF, ivZoomIn.getId());
        view.addView(ivZoomOut, zoomoutParams);

        ivZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mapView.getController().zoomOut();
            }
        });
    }

}

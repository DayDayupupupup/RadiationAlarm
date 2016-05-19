package edu.neu.radiationalarm.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.utils.CoordinateConverter;


import java.util.List;

import butterknife.ButterKnife;
import edu.neu.radiationalarm.R;
import edu.neu.radiationalarm.activity.MainActivity;
import edu.neu.radiationalarm.info.BaiDuUtil;
import edu.neu.radiationalarm.info.ConvertUtil;
import edu.neu.radiationalarm.info.MyLocationUtil;
import edu.neu.radiationalarm.info.NeighborInfo;
import edu.neu.radiationalarm.service.LacService;

public class BaiduMapFragment extends Fragment implements LacService.FragmentListener_3 {

    /**
     * MapView 是地图主控件
     */
    private static final String Tag = "百度地图显示";
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    TextView mAddress;
    double lat;
    double lng;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        SDKInitializer.initialize(getContext());
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_baidu_map, container, false);
        ButterKnife.bind(this, rootView);

        mAddress = (TextView) rootView.findViewById(R.id.detail_address);
        mMapView = (MapView) rootView.findViewById(R.id.b_map_View);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        initView(inflater.getContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(((MainActivity)getActivity()).binder == null) {}
                ((MainActivity)getActivity()).getBinder().setFragmentListener_3(BaiduMapFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateText();
                    }
                });
            }
        }).start();
        return rootView;
    }
    private void setLocation(double x, double y) {
        //定义Maker坐标点
        LatLng point = new LatLng(x, y);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.heart);
        //构建MarkerOption，用于在地图上添加Marker
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化


        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }
    private void getDetailAddr(final double x, final double y) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String detail = BaiDuUtil.getAddress(x + "", y + "");
                if (mHandler != null) {
                    Message message = new Message();
                    message.obj = detail;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // 要做的事情
            String detail = (String) msg.obj;
            Log.i("location位置",detail);
            mAddress.setText("当前位置为:" + detail);
            super.handleMessage(msg);
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    private void initView(Context context) {
    }
    public void updateText(){
//        lat=41.653367;
//        lng=123.42347;
        new Thread(){
            @Override
            public void run()
            {
                List<NeighborInfo> list = MyLocationUtil.getMyLatLng();
                if (list != null) {
                    Log.d(Tag, list.toString());
                    for (int i = 0; i < 3; i++) {
                        lng = list.get(i).getLon();
                        lat = list.get(i).getLat();
                        String bdz = ConvertUtil.convert(lng, lat);
                        String[] add = null;
                        add = bdz.split(",");
                        double x = Double.parseDouble(add[0]);
                        double y = Double.parseDouble(add[1]);
                        setLocation(y, x);
                        getDetailAddr(y, x);
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDataChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateText();
            }
        });
    }
}

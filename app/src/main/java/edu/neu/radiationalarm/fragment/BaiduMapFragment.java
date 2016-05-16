package edu.neu.radiationalarm.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;


import butterknife.ButterKnife;
import edu.neu.radiationalarm.R;
import edu.neu.radiationalarm.activity.MainActivity;
import edu.neu.radiationalarm.info.BaiDuUtil;
import edu.neu.radiationalarm.service.LacService;

public class BaiduMapFragment extends Fragment implements LacService.FragmentListener_3 {

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    TextView mAddress;
    double x = 0;
    double y = 0;

    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Log.d("key 验证出错! ","请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                Log.d("key 验证成功!"," 功能可以正常使用");
            }
            else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Log.d("网络出错","...");
            }
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_baidu_map, container, false);
        ButterKnife.bind(this, rootView);

        mAddress = (TextView) rootView.findViewById(R.id.detail_address);

        mMapView = (MapView) rootView.findViewById(R.id.b_map_View);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        x=41.653367;
        y=123.42347;
        setLocation(x,y);
        getDetailAddr(x,y);

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
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
   public void updateText(){

   }


    @Override
    public void onDataChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });

    }
}

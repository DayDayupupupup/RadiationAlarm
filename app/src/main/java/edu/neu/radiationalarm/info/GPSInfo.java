package edu.neu.radiationalarm.info;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.security.Permission;

import edu.neu.radiationalarm.activity.MainActivity;

/**
 * Created by Mac on 2016/5/6.
 */
public class GPSInfo {
    private static final String TAG = "GPS";
    private LocationManager lm;
    private Location location;
    private double latitude;
    private double longitude;


    public GPSInfo(final Context context) {
        Log.e(TAG, "准备获取GPS信息");
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e(TAG, "GPS已开");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                getLocation();
            }
//            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            getLocation();
        } else {
            Log.e(TAG, "GPS未开，开启gps");
            toggleGPS(context);
            new Handler() {
            }.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        getLocation();
                    }
//                    getLocation();
                }
            }, 2000);
        }
    }

    private void toggleGPS(Context context) {

        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
                Location location1 = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location1 != null) {
                    latitude = location1.getLatitude(); // 经度
                    longitude = location1.getLongitude(); // 纬度
                } else {
                    Log.e(TAG, "未授予gps权限");
                }
            }
        }
    }

    public Location getLocation() {
        Log.e(TAG, "开始获取GPS信息");
        Log.e(TAG, "已授予gps权限");
//        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             Log.e(TAG,"能获取到location");
             if (location != null){
                 Log.e(TAG,"当前可以获取到gps");
                 latitude = location.getLatitude();
                 longitude = location.getLongitude();
                 Log.e(TAG, "经度：" + latitude+ " 纬度: " + longitude);

             }else{
                 Log.e(TAG,"当前位置获取不到gps");
               //  lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,locationListener);
             }
        return location;
    }

    private final LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, provider);
        }
        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, provider);
        }
        // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.e("Map", "Location changed : Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
            }else {
                Log.e("GPS经纬度：","无法获取位置信息");
            }
        }
    };

    public double getLatitude(){return  latitude;}
    public double getLongitude(){return longitude;}
    public LocationManager getLm(){return lm;}
}

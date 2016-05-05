package edu.neu.radiationalarm.info;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import edu.neu.radiationalarm.activity.MainActivity;

/**
 * Created by Mac on 2016/5/6.
 */
public class GPSInfo {
    private static final String TAG = MainActivity.class.getSimpleName();
    private LocationManager lm;
    private double latitude;
    private double longitude;


    public GPSInfo(Context context){
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            getLocation();
        }else {
            Intent GPSIntent = new Intent();
            GPSIntent.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
            GPSIntent.setData(Uri.parse("custom:3"));
            try {
                PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            getLocation();
        }
    }

    public void getLocation(){
        if (ContextCompat.checkSelfPermission().)
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }else{
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,  locationListener);
        }
    }
    private void toggleGPS() {


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


}

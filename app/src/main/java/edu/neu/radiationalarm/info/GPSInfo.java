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
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e(TAG, "GPS未开，开启gps");
            toggleGPS(context);
        }
        Log.e(TAG, "GPS已开");

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateLocation(location);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // 当GPS定位信息发生改变时，更新位置
                    updateLocation(location);
                }
                @Override
                public void onProviderDisabled(String provider) {
                    updateLocation(null);
                }
                @Override
                public void onProviderEnabled(String provider) {
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
            });
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
        }
    }

    public Location updateLocation(Location location) {
        if (location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.e(TAG, "经度：" + latitude+ " 纬度: " + longitude);
        }else{
            Log.e(TAG,"当前位置获取不到gps");
        }
        return location;
    }

    public double getLatitude(){return  latitude;}
    public double getLongitude(){return longitude;}
    public LocationManager getLm(){return lm;}
}

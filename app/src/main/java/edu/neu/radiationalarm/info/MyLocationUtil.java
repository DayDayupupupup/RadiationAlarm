package edu.neu.radiationalarm.info;

import android.util.JsonReader;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/5/17.
 */
public class MyLocationUtil {

    private static final String Tag = "获取基站的经纬度";
    private static List<NeighborInfo> list = new GSMCellLocationInfo().getInfo();
    public static List getCellLatLng(){
            String url = "http://api.cellocation.com/cell/";
            for (NeighborInfo info:list) {
                int mcc = info.getMcc();
                int mnc = info.getMnc();
                int lac = info.getLac();
                int ci = info.getCid();
                String param = "mcc="+mcc+"&mnc="+mnc+"&lac=" + lac + "&ci=" + ci + "&output=json";
                String result = HttpUtil.sendGet(url,param);
                try{
                    JSONObject jsonObject = new JSONObject(result.toString());
                    if(jsonObject.getInt("errcode") == 0){
                        double lat = Double.parseDouble(jsonObject.getString("lat"));
                        double lon = Double.parseDouble(jsonObject.getString("lon"));
                        String radius = jsonObject.getString("radius");
                        String address = jsonObject.getString("address");
                        info.setLat(lat);
                        info.setLon(lon);
                        info.setAddress(address);
//                        Log.d(Tag, info.toString());
                    }else{
                        list.remove(info);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        return list;
    }

    public static List getMyLatLng(){
        List<NeighborInfo> list1 = getCellLatLng();
        if(list1.size()>=3) {
                Collections.sort(list1, new Comparator<NeighborInfo>() {
                    @Override
                    public int compare(NeighborInfo lhs, NeighborInfo rhs) {
                        Integer bss1 = lhs.getBss();
                        Integer bss2 = rhs.getBss();
                        return bss2.compareTo(bss1);
                    }
                });
            Log.d(Tag,list.toString());
        }
//        double x1 = list.get(1).getLon();
//        double y1 = list.get(1).getLat();
//        double x2 = list.get(2).getLon();
//        double y2 = list.get(2).getLat();
//        double x3 = list.get(3).getLon();
//        double y3 = list.get(3).getLat();
//        int bss1 = list.get(1).getBss();
//        int bss2 = list.get(2).getBss();
//        int bss3 = list.get(3).getBss();
//        double myLng, myLat;
//        double ra,rb,rc;
        return list;
    }
    public LatLng lonLat2Mercator(LatLng lonLat) {
                     double mercatorX,mercatorY;
                 mercatorX = lonLat.longitude * 20037508.34 / 180;
                 mercatorY = lonLat.latitude * 20037508.34 / 180;
        return lonLat;
    }

}

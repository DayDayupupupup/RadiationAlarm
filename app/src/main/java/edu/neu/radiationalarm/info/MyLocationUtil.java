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

        }else {
            Log.d(Tag,"基站数小于3，无法定位");

            return null;
        }

        double x1 = list.get(1).getLon();
        double y1 = list.get(1).getLat();
        double mX1 = x1 * 20037508.342789 / 180, mY1 = Math.log(Math.tan((90+y1)*Math.PI/360))/(Math.PI/180);
        mY1 = y1 * 20037508.342789 / 180;

        double x2 = list.get(2).getLon();
        double y2 = list.get(2).getLat();
        double mX2 = x2 * 20037508.342789 / 180, mY2 = Math.log(Math.tan((90+y2)*Math.PI/360))/(Math.PI/180);
        mY2 = y2 * 20037508.342789 / 180;

        double x3 = list.get(3).getLon();
        double y3 = list.get(3).getLat();
        double mX3 = x3 * 20037508.342789 / 180, mY3 = Math.log(Math.tan((90+y3)*Math.PI/360))/(Math.PI/180);
        mY3 = y3 * 20037508.342789 / 180;

        int bss1 = list.get(1).getBss();
        int bss2 = list.get(2).getBss();
        int bss3 = list.get(3).getBss();

        double myLng, myLat,dx,dy,ex,ey,f1,f2;
        double r1,r2,r3;
        r1 =10^( (-40 - bss1)/10*4);
        r2 =10^( (-40 - bss1)/10*4);
        r3 =10^( (-40 - bss1)/10*4);

       dx = (mX2*mX2 - mX3*mX3 + mY2*mY2 - mY3*mY3 - r2*r2 + r3*r3 - (mY2*(mX2*mX2*mY2 + mX2*mX2*mY3 + mX3*mX3*mY2 + mX3*mX3*mY3 - mY2*mY3*mY3 - mY2*mY2*mY3 - mY2*r2*r2 + mY2*r3*r3 + mY3*r2*r2 - mY3*r3*r3 + mX2*Math.sqrt((- mX2*mX2 + 2*mX2*mX3 - mX3*mX3 - mY2*mY2 + 2*mY2*mY3 - mY3*mY3 + r2*r2 + 2*r2*r3 + r3*r3)*(mX2*mX2 - 2*mX2*mX3 + mX3*mX3 + mY2*mY2 - 2*mY2*mY3 + mY3*mY3 - r2*r2 + 2*r2*r3 - r3*r3)) - mX3*Math.sqrt((- mX2*mX2 + 2*mX2*mX3 - mX3*mX3 - mY2*mY2 + 2*mY2*mY3 - mY3*mY3 + r2*r2 + 2*r2*r3 + r3*r3)*(mX2*mX2 - 2*mX2*mX3 + mX3*mX3 + mY2*mY2 - 2*mY2*mY3 + mY3*mY3 - r2*r2 + 2*r2*r3 - r3*r3)) + mY2*mY2*mY2 + mY3*mY3*mY3 - 2*mX2*mX3*mY2 - 2*mX2*mX3*mY3))/(mX2*mX2 - 2*mX2*mX3 + mX3*mX3 + mY2*mY2 - 2*mY2*mY3 + mY3*mY3) + (mY3*(mX2*mX2*mY2 + mX2*mX2*mY3 + mX3*mX3*mY2 + mX3*mX3*mY3 - mY2*mY3*mY3 - mY2*mY2*mY3 - mY2*r2+r2 + mY2*r3*r3 + mY3*r2*r2 - mY3*r3*r3 + mX2*Math.sqrt((- mX2*mX2 + 2*mX2*mX3 - mX3*mX3 - mY2*mY2 + 2*mY2*mY3 - mY3*mY3 + r2*r2 + 2*r2*r3 + r3*r3)*(mX2*mX2 - 2*mX2*mX3 + mX3*mX3 + mY2*mY2 - 2*mY2*mY3 + mY3*mY3 - r2*r2 + 2*r2*r3 - r3*r3)) - mX3*Math.sqrt((- mX2*mX2 + 2*mX2*mX3 - mX3*mX3 - mY2*mY2 + 2*mY2*mY3 - mY3*mY3 + r2*r2 + 2*r2*r3 + r3*r3)*(mX2*mX2 - 2*mX2*mX3 + mX3*mX3 + mY2*mY2 - 2*mY2*mY3 + mY3*mY3 - r2*r2 + 2*r2*r3 - r3*r3)) + mY2*mY2*mY2 + mY3*mY3*mY3 - 2*mX2*mX3*mY2 - 2*mX2*mX3*mY3))/(mX2*mX2 - 2*mX2*mX3 + mX3*mX3 + mY2*mY2 - 2*mY2*mY3 + mY3*mY3))/(2*mX2 - 2*mX3);
     //  dx =  (mX2^2 - mX3^2 + mY2^2 - mY3^2 - r2^2 + r3^2 - (mY2*(mX2^2*mY2 + mX2^2*mY3 + mX3^2*mY2 + mX3^2*mY3 - mY2*mY3^2 - mY2^2*mY3 - mY2*r2^2 + mY2*r3^2 + mY3*r2^2 - mY3*r3^2 - mX2*((- mX2^2 + 2*mX2*mX3 - mX3^2 - mY2^2 + 2*mY2*mY3 - mY3^2 + r2^2 + 2*r2*r3 + r3^2)*(mX2^2 - 2*mX2*mX3 + mX3^2 + mY2^2 - 2*mY2*mY3 + mY3^2 - r2^2 + 2*r2*r3 - r3^2))^(1/2) + mX3*((- mX2^2 + 2*mX2*mX3 - mX3^2 - mY2^2 + 2*mY2*mY3 - mY3^2 + r2^2 + 2*r2*r3 + r3^2)*(mX2^2 - 2*mX2*mX3 + mX3^2 + mY2^2 - 2*mY2*mY3 + mY3^2 - r2^2 + 2*r2*r3 - r3^2))^(1/2) + mY2^3 + mY3^3 - 2*mX2*mX3*mY2 - 2*mX2*mX3*mY3))/(mX2^2 - 2*mX2*mX3 + mX3^2 + mY2^2 - 2*mY2*mY3 + mY3^2) + (mY3*(mX2^2*mY2 + mX2^2*mY3 + mX3^2*mY2 + mX3^2*mY3 - mY2*mY3^2 - mY2^2*mY3 - mY2*r2^2 + mY2*r3^2 + mY3*r2^2 - mY3*r3^2 - mX2*((- mX2^2 + 2*mX2*mX3 - mX3^2 - mY2^2 + 2*mY2*mY3 - mY3^2 + r2^2 + 2*r2*r3 + r3^2)*(mX2^2 - 2*mX2*mX3 + mX3^2 + mY2^2 - 2*mY2*mY3 + mY3^2 - r2^2 + 2*r2*r3 - r3^2))^(1/2) + mX3*((- mX2^2 + 2*mX2*mX3 - mX3^2 - mY2^2 + 2*mY2*mY3 - mY3^2 + r2^2 + 2*r2*r3 + r3^2)*(mX2^2 - 2*mX2*mX3 + mX3^2 + mY2^2 - 2*mY2*mY3 + mY3^2 - r2^2 + 2*r2*r3 - r3^2))^(1/2) + mY2^3 + mY3^3 - 2*mX2*mX3*mY2 - 2*mX2*mX3*mY3))/(mX2^2 - 2*mX2*mX3 + mX3^2 + mY2^2 - 2*mY2*mY3 + mY3^2))/(2*mX2 - 2*mX3);










        return list;
    }

}

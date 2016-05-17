package edu.neu.radiationalarm.info;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2016/5/17.
 */
public class ConvertUtil {
    private static final String Tag = "经纬度转换为百度坐标";

    public static String convert(double x, double y){
        try {
            URL url = new URL("http://api.map.baidu.com/ag/coord/convert?from=2&to=4&x=" + x + "&y=" + y);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");

            out.flush();
            out.close();
            //服务器的回应的字串，并解析
            String sCurrentLine;
            String sTotalString;
            sCurrentLine = "";
            sTotalString = "";
            InputStream l_urlStream;
            l_urlStream = connection.getInputStream();
            BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
            while ((sCurrentLine = l_reader.readLine()) != null) {
                if (!sCurrentLine.equals(""))
                    sTotalString += sCurrentLine;
            }
            System.out.println(sTotalString);
            sTotalString = sTotalString.substring(1, sTotalString.length() - 1);
            System.out.println(sTotalString);
            String[] results = sTotalString.split("\\,");
            if (results.length == 3) {
                if (results[0].split("\\:")[1].equals("0")) {
                    String mapX = results[1].split("\\:")[1];
                    String mapY = results[2].split("\\:")[1];
                    mapX = mapX.substring(1, mapX.length() - 1);
                    mapY = mapY.substring(1, mapY.length() - 1);
                    mapX = new String(Base64.decode(mapX, Base64.DEFAULT));
                    mapY = new String(Base64.decode(mapY, Base64.DEFAULT));
                    Log.d(Tag, "x=" + mapX + "y=" + mapY);
                    return mapX +","+ mapY;
                } else {
                    Log.d(Tag, "error != 0");
                }
            } else {
                Log.d(Tag, "String invalid!");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}

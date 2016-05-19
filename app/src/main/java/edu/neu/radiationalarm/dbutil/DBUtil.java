package edu.neu.radiationalarm.dbutil;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Mac on 2016/5/19.
 */
public class DBUtil {
    private static final String TAG = "上传数据";
    public static void HttpPostData() {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            String uri = "https://api.bmob.cn/1/functions/insertFingerPrint";
            HttpPost httppost = new HttpPost(uri);

            //添加http头信息
            httppost.addHeader("X-Bmob-Application-Id", "5081fc502196b1d40fb902e4281f18e1");
            httppost.addHeader("X-Bmob-REST-API-Key", "fbc9ae862fbbc7632e80aabd9f0e6fb7");
            httppost.addHeader("Content-Type", "application/json");
            //http post的json数据格式：  {"name": "your name","parentId": "id_of_parent"}
            JSONObject obj = new JSONObject();
            obj.put("gpsLat", "460");
            obj.put("gpsLng", "0");
//            obj.put("lac","16648");
//            obj.put("cellId","27023");
            httppost.setEntity(new StringEntity(obj.toString()));
            HttpResponse response;
            response = httpclient.execute(httppost);
            //检验状态码，如果成功接收数据
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                String rev = EntityUtils.toString(response.getEntity());//返回json格式： {"id": "27JpL~j4vsL0LX00E00005","version": "abc"}
                obj = new JSONObject(rev);
                String result = obj.getString("result");
                obj = new JSONObject(result);
                String data = obj.getString("data");
                obj = new JSONObject(data);
                String objectId = obj.getString("objectId");
                Log.d("DB","数据插入成功");
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

}

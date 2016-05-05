package edu.neu.radiationalarm.info;

import android.Manifest;
import android.content.Context;
import android.location.LocationManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import edu.neu.radiationalarm.activity.MainActivity;


/**
 * Created by Administrator on 2015/12/12.
 */
public class GSMCellLocationInfo {

    private TelephonyManager manager;
    private LocationManager lm;

    private int mcc;
    private int mnc;
    private int lac;
    private int cellid;
    private int strengh;
    private List<NeighboringCellInfo> infos;
    private List<RecentData> recentData;


    public GSMCellLocationInfo(Context context) {
        manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        manager.listen(new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                strengh = (2 * signalStrength.getGsmSignalStrength() - 113) * -1;
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        Log.d("验证信息","GSM");
        updateInfo();
    }

    public void updateInfo() {

        String operator = manager.getNetworkOperator();

        /**通过operator获取 MCC 和MNC*/
        mcc = Integer.parseInt(operator.substring(0, 3));
        mnc = Integer.parseInt(operator.substring(3));

        Log.d("基站信息：","mcc="+mcc);
        Log.d("基站信息：","mnc="+mnc);
        Log.d("验证信息","getCellLocation方法");

        boolean ifhas = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (ifhas){
            Log.d("验证信息","有权限");
        }else{
            Log.d("验证信息","wu权限");
        }

        GsmCellLocation location = (GsmCellLocation) manager.getCellLocation();
        Log.d("验证信息","正常执行");
        /**通过GsmCellLocation获取中国移动和联通 LAC 和cellID */
        lac = location.getLac();
        cellid = location.getCid();

        Log.d("基站信息：","lac="+lac);
        Log.d("基站信息：","cellid="+cellid);


        int strength = 0;
        /**通过getNeighboringCellInfo获取BSSS */
        infos = manager.getNeighboringCellInfo();

        for (NeighboringCellInfo info : infos) {
            strength += (-133 + 2 * info.getRssi());// 获取邻区基站信号强度
            //info.getLac();// 取出当前邻区的LAC
            //info.getCid();// 取出当前邻区的CID

        }
    }

    public int getCellid() {
        return cellid;
    }

    public List<NeighboringCellInfo> getInfo() {
        return infos;
    }

    public int getLac() {
        return lac;
    }

    public int getMcc() {
        return mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public TelephonyManager getManager() {
        return manager;
    }

    public int getStrengh() {
        return strengh;
    }

    public List<RecentData> getRecentData() {

        return recentData;
    }

    public void setRecentData(List<RecentData> recentData) {
        this.recentData = recentData;
    }

}

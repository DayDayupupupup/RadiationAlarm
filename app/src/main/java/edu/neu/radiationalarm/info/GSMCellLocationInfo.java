package edu.neu.radiationalarm.info;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
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
    private List<CellInfo> infos;
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

    @TargetApi(23)
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
//        infos = manager.getNeighboringCellInfo();
        infos = manager.getAllCellInfo();
        StringBuffer sb = new StringBuffer("总数 : " + infos.size() + "\n");
        for (CellInfo info : infos) {
            sb.append(info +"\n");
            if(info instanceof CellInfoGsm){
                int LAC = ((CellInfoGsm) info).getCellIdentity().getLac();
                int CID = ((CellInfoGsm) info).getCellIdentity().getCid();
                int BSSS = ((CellInfoGsm) info).getCellSignalStrength().getDbm();
                Log.d("GsmCellInfo","LAC:"+LAC+" CID:"+CID+" BSSS:"+BSSS);
            }else if (info instanceof CellInfoCdma){
                int LAC = ((CellInfoCdma) info).getCellIdentity().getSystemId();//daiding
                int CID = ((CellInfoCdma) info).getCellIdentity().getBasestationId();
                int BSSS = ((CellInfoCdma) info).getCellSignalStrength().getDbm();
                Log.d("CdmaCellInfo","LAC:"+LAC+" CID:"+CID+" BSSS:"+BSSS);
            }else if(info instanceof CellInfoLte){
                int LAC = ((CellInfoLte) info).getCellIdentity().getTac();
                int CID = ((CellInfoLte) info).getCellIdentity().getPci();
                int BSSS = ((CellInfoLte) info).getCellSignalStrength().getDbm();
                Log.d("LTECellInfo","LAC:"+LAC+" CID:"+CID+" BSSS:"+BSSS);
            }else if(info instanceof CellInfoWcdma){
                int LAC = ((CellInfoWcdma) info).getCellIdentity().getLac();
                int CID = ((CellInfoWcdma) info).getCellIdentity().getCid();
                int BSSS = ((CellInfoWcdma) info).getCellSignalStrength().getDbm();
                Log.d("LTECellInfo","LAC:"+LAC+" CID:"+CID+" BSSS:"+BSSS);
            }else{
                Log.d("CellInfo","Unknown Cell");
            }
        }
//            strength += (-133 + 2 * info.getRssi());// 获取邻区基站信号强度
//            info.getLac();// 取出当前邻区的LAC
//            info.getCid();// 取出当前邻区的CID
//            Log.d("邻近基站","LAC:"+info.getLac()+"CID："+info.getCid()+"强度："+strength);

    }

    public int getCellid() {
        return cellid;
    }

    public List<CellInfo> getInfo() {
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

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
import android.widget.AutoCompleteTextView;

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
    private List<NeighborInfo> infos;
    private List<RecentData> recentData;

    public GSMCellLocationInfo(int mcc, int mnc, int lac, int cellid, int strengh) {
        this.mcc = mcc;
        this.mnc = mnc;
        this.lac = lac;
        this.cellid = cellid;
        this.strengh = strengh;
    }

    public GSMCellLocationInfo(Context context) {
        manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        manager.listen(new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                strengh = (2 * signalStrength.getGsmSignalStrength() - 113);
                Log.d("xinhao","strengh"+strengh);
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

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

        boolean ifhas = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (ifhas){
            Log.d("验证信息","有权限");
        }else{
            Log.d("验证信息","wu权限");
        }

        GsmCellLocation location = (GsmCellLocation) manager.getCellLocation();
        /**通过GsmCellLocation获取中国移动和联通 LAC 和cellID */
        lac = location.getLac();
        cellid = location.getCid();

        Log.d("基站信息：","lac="+lac);
        Log.d("基站信息：","cellid="+cellid);
        int strength = 0;

        /**通过getNeighboringCellInfo获取BSSS */
//        infos = manager.getNeighboringCellInfo();
        List<CellInfo> allCellInfo = manager.getAllCellInfo();
        infos = new ArrayList<NeighborInfo>();
//        Log.d("基站总数：","总数："+allCellInfo.size());
        for (CellInfo info : allCellInfo) {
            NeighborInfo neighborInfo = new NeighborInfo();
            if(info instanceof CellInfoGsm){
                neighborInfo.setLac(((CellInfoGsm) info).getCellIdentity().getLac());
                neighborInfo.setCid(((CellInfoGsm) info).getCellIdentity().getCid());
                neighborInfo.setBss(((CellInfoGsm) info).getCellSignalStrength().getDbm());
                neighborInfo.setMcc(((CellInfoGsm) info).getCellIdentity().getMcc());
                neighborInfo.setMnc(((CellInfoGsm) info).getCellIdentity().getMnc());
                infos.add(neighborInfo);
//                Log.d("GsmCellInfo",infos.toString());
            }else if (info instanceof CellInfoCdma){
                neighborInfo.setLac(((CellInfoCdma) info).getCellIdentity().getSystemId());//daiding
                neighborInfo.setCid(((CellInfoCdma) info).getCellIdentity().getBasestationId());
                neighborInfo.setBss(((CellInfoCdma) info).getCellSignalStrength().getDbm());
                neighborInfo.setMcc(((CellInfoCdma) info).getCellIdentity().getNetworkId());
//                neighborInfo.setMnc(((CellInfoCdma) info).getCellIdentity().);
                infos.add(neighborInfo);
//                Log.d("CdmaCellInfo",infos.toString());
            }else if(info instanceof CellInfoLte){
                neighborInfo.setLac (((CellInfoLte) info).getCellIdentity().getTac());
                neighborInfo.setCid(((CellInfoLte) info).getCellIdentity().getPci());
                neighborInfo.setBss (((CellInfoLte) info).getCellSignalStrength().getDbm());
                neighborInfo.setMcc(((CellInfoLte) info).getCellIdentity().getMcc());
                neighborInfo.setMnc(((CellInfoLte) info).getCellIdentity().getMnc());
                infos.add(neighborInfo);
//                Log.d("LTECellInfo",infos.toString());
            }else if(info instanceof CellInfoWcdma){
                neighborInfo.setLac (((CellInfoWcdma) info).getCellIdentity().getLac());
                neighborInfo.setCid (((CellInfoWcdma) info).getCellIdentity().getCid());
                neighborInfo.setBss(((CellInfoWcdma) info).getCellSignalStrength().getDbm());
                neighborInfo.setMcc(((CellInfoWcdma) info).getCellIdentity().getMcc());
                neighborInfo.setMnc(((CellInfoWcdma) info).getCellIdentity().getMnc());
                infos.add(neighborInfo);
//                Log.d("LTECellInfo",infos.toString());
            }else{
                Log.d("CellInfo","Unknown Cell");
            }
        }
        Log.d("XINXI",infos.toString());;
//            strength += (-133 + 2 * info.getRssi());// 获取邻区基站信号强度
//            info.getLac();// 取出当前邻区的LAC
//            info.getCid();// 取出当前邻区的CID
//            Log.d("邻近基站","LAC:"+info.getLac()+"CID："+info.getCid()+"强度："+strength);

    }

    public int getCellid() {
        return cellid;
    }

    public List<NeighborInfo> getInfo() {
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

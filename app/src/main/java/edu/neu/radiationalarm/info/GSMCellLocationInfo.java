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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

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

        Log.d("基站信息：","mcc="+mcc+"mnc="+mnc);

//        boolean ifhas = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        if (ifhas){
//            Log.d("验证信息","有权限");
//        }else{
//            Log.d("验证信息","wu权限");
//        }
        GsmCellLocation location = (GsmCellLocation) manager.getCellLocation();
        /**通过GsmCellLocation获取中国移动和联通 LAC 和cellID */
        lac = location.getLac();
        cellid = location.getCid();

        Log.d("基站信息：","lac="+lac+"cellid="+cellid);

        List<CellInfo> allCellInfo = manager.getAllCellInfo();
        infos = new ArrayList<NeighborInfo>();
        int size = 0;
        for (CellInfo info : allCellInfo) {
            NeighborInfo neighborInfo = new NeighborInfo();
            if(info instanceof CellInfoGsm){
                neighborInfo.setLac(((CellInfoGsm) info).getCellIdentity().getLac());
                neighborInfo.setCid(((CellInfoGsm) info).getCellIdentity().getCid());
                neighborInfo.setBss(((CellInfoGsm) info).getCellSignalStrength().getDbm());
                neighborInfo.setMcc(((CellInfoGsm) info).getCellIdentity().getMcc());
               neighborInfo.setMnc(((CellInfoGsm) info).getCellIdentity().getMnc());
                if(((CellInfoGsm) info).getCellIdentity().getMcc()==460){
                    infos.add(neighborInfo);
                    size = size + 1;
                }
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
                if (((CellInfoLte) info).getCellIdentity().getMcc()==460){
                    infos.add(neighborInfo);
                    size = size + 1;
                }
//                Log.d("LTECellInfo",infos.toString());
            }else if(info instanceof CellInfoWcdma){
                neighborInfo.setLac (((CellInfoWcdma) info).getCellIdentity().getLac());
                neighborInfo.setCid (((CellInfoWcdma) info).getCellIdentity().getCid());
                neighborInfo.setBss(((CellInfoWcdma) info).getCellSignalStrength().getDbm());
                neighborInfo.setMcc(((CellInfoWcdma) info).getCellIdentity().getMcc());
                neighborInfo.setMnc(((CellInfoWcdma) info).getCellIdentity().getMnc());
                if (((CellInfoWcdma) info).getCellIdentity().getMcc()==460){
                    infos.add(neighborInfo);
                    size = size + 1;
                }
            }else{
                Log.d("CellInfo","Unknown Cell");
            }
        }
        Log.d("邻近基站","总数："+size + infos.toString());
    }

    public void getCellLocation(){
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://api.cellid.cn/cellid.php");
        String token = "e052e14fd7dab008f6580d27c9d5c91d";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lac",lac);
            jsonObject.put("cell_id",cellid);
            jsonObject.put("token",token);

//            HttpResponse response = client.execute(get);
        }catch (Exception E){

        }
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

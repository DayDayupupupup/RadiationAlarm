package edu.neu.radiationalarm.info;

import android.util.Log;

/**
 * Created by Administrator on 2016/5/12.
 */
public class NeighborInfo {
    int mcc;
    int mnc;
    int lac;
    int cid;
    int bss;

    public NeighborInfo(int mcc, int mnc, int lac, int cid, int bss) {
        this.mcc = mcc;
        this.mnc = mnc;
        this.lac = lac;
        this.cid = cid;
        this.bss = bss;
    }

    public NeighborInfo() {
    }

    public int getMcc() {
        return mcc;
    }

    public void setMcc(int mcc) {
        this.mcc = mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getBss() {
        return bss;
    }

    public void setBss(int bss) {
        this.bss = bss;
    }

    @Override
    public String toString() {
//        Log.d("", "mcc=" + mcc +
//                ", mnc=" + mnc +
//                ", lac=" + lac +
//                ", cid=" + cid +
//                ", bss=" + bss);
        return "NeighborInfo{" +
                "mcc=" + mcc +
                ", mnc=" + mnc +
                ", lac=" + lac +
                ", cid=" + cid +
                ", bss=" + bss +
                '}';

    }

}

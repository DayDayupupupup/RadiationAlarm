package edu.neu.radiationalarm.info;

/**
 * Created by Administrator on 2015/12/17.
 */
public class RecentData {

    private int bsss;
    private long time;

    public RecentData(int bsss, long time) {
        this.bsss = bsss;
        this.time = time;
    }

    public int getBsss() {
        return bsss;
    }

    public void setBsss(int bsss) {
        this.bsss = bsss;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

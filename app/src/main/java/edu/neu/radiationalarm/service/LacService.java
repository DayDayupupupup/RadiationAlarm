package edu.neu.radiationalarm.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import edu.neu.radiationalarm.dbutil.DBManager;
import edu.neu.radiationalarm.info.GSMCellLocationInfo;

public class LacService extends Service {

    private GSMCellLocationInfo infos;
    private MyBinder binder;
    private DBManager manager;

    private FragmentListener_1 listener_1;
    private FragmentListener_2 listener_2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        binder = new MyBinder();
        Log.d("验证信息","LacService");
        infos = new GSMCellLocationInfo(this);
        manager = new DBManager(this);
        startUpdate();


        return binder;
    }

    public class MyBinder extends Binder {

        public GSMCellLocationInfo getInfo() {
            return infos;
        }

        public void setFragmentListener_1(FragmentListener_1 listener_1) {
            LacService.this.listener_1 = listener_1;
        }

        public void setFragmentListener_2(FragmentListener_2 listener_2) {
            LacService.this.listener_2 = listener_2;
        }

    }

    private void startUpdate() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {

                    manager.updateInfo(infos);
                    infos.setRecentData(manager.getRecentDatas());
                    infos.updateInfo();
                    if(listener_1 != null && listener_2 != null) {
                        listener_1.onDataChanged();
                        listener_2.onDataChanged();
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }


            }
        }).start();

    }

    public interface FragmentListener_1 {
        void onDataChanged();
    }
    public interface FragmentListener_2 {
        void onDataChanged();
    }
}

package edu.neu.radiationalarm.dbutil;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.CellInfo;
import android.telephony.NeighboringCellInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.neu.radiationalarm.info.GSMCellLocationInfo;
import edu.neu.radiationalarm.info.RecentData;

/**
 * Created by Administrator on 2015/12/12.
 */
public class DBManager {

    private DBHelper helper;
    private SQLiteDatabase db;


    public DBManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    public void updateInfo(GSMCellLocationInfo info) {


//        db.execSQL("delete from around");
        db.execSQL("delete from station");
        List<CellInfo> infos = info.getInfo();
//        for(CellInfo neigh : infos) {
//            db.execSQL("insert into around values(?, ?, ?)", new String[] {null,
//                    String.valueOf(neigh.getLac()), String.valueOf(neigh.getCid())});
//        }

        db.execSQL("insert into station values(?, ?, ?, ?, ?)", new String[]{null,
                String.valueOf(info.getMcc()), String.valueOf(info.getMnc()),
                String.valueOf(info.getLac()), String.valueOf(info.getCellid())});


        if(db.rawQuery("select * from imm", null).getCount() == 5) {
            Cursor cursor = db.rawQuery("select * from imm", null);
            cursor.moveToNext();
            db.execSQL("delete from imm where id = ?", new String[]{String.valueOf(cursor.getInt(0))});
            db.execSQL("insert into imm values(?, ?, ?)", new String[]{null,
                    String.valueOf(info.getStrengh()), "datetime('now','localtime')"});
        } else {
            db.execSQL("insert into imm values(?, ?, ?)", new String[]{null,
                    String.valueOf(info.getStrengh()), "datetime('now','localtime')"});
        }

        db.execSQL("insert into record_day values(?, ?, ?)", new String[]{null,
                String.valueOf(info.getStrengh()), String.valueOf(System.currentTimeMillis())});

        checkDate();

    }

    public List<RecentData> getRecentDatas() {

        List<RecentData> datas = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from imm", null);
        while(cursor.moveToNext()) {
            RecentData data = new RecentData(cursor.getInt(1), cursor.getLong(2));
            datas.add(data);
        }
        return datas;
    }

    private void checkDate() {

        Cursor cursor = db.rawQuery("select * from record_day", null);
        cursor.moveToNext();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(cursor.getLong(2));
        if(day != calendar.get(Calendar.DAY_OF_MONTH)) {

            int strength = 0;
            do {
                strength += cursor.getInt(1);
            } while(cursor.moveToNext());
            strength = strength / cursor.getCount();
            db.execSQL("insert into record_month values(?, ?, ?)", new String[]{null, String.valueOf(strength), String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))});
            db.execSQL("delete from record_day");
        }
        if(month != calendar.get(Calendar.MONTH)) {

            int strength_month = 0;
            Cursor cursor_month = db.rawQuery("select * from record_month", null);
            while(cursor_month.moveToNext()) {
                strength_month += cursor_month.getInt(1);
            }
            strength_month = strength_month / cursor_month.getCount();
            db.execSQL("insert into record_year values(?, ?, ?)", new String[]{null, String.valueOf(strength_month), String.valueOf(calendar.get(Calendar.MONTH))});
            db.execSQL("delete from record_month");
        }
        if(year != calendar.get(Calendar.YEAR)) {

            /*int strength_year = 0;
            Cursor cursor_year = db.rawQuery("select * from record_year", null);*/
            db.execSQL("delete from record_year");
        }

    }

    public List<Integer> strengthOfDay() {

        List<Integer> list_strength_day = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from record_day", null);
        while(cursor.moveToNext()) {
            list_strength_day.add(cursor.getInt(1));

        }
        return list_strength_day;
    }

    public List<Long> timeOfDay() {

        List<Long> list_time_day = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from record_day", null);
        while(cursor.moveToNext()) {
            list_time_day.add(cursor.getLong(2));
        }
        return list_time_day;
    }

    public List<Integer> strengthOfMonth() {

        List<Integer> list_strength_month = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from record_month", null);
        while(cursor.moveToNext()) {
            list_strength_month.add(cursor.getInt(1));
        }
        return list_strength_month;
    }

    public List<Integer> dayOfMonth() {

        List<Integer> list_day_month = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from record_month", null);
        while(cursor.moveToNext()) {
            list_day_month.add(cursor.getInt(2));
        }
        return list_day_month;
    }

    public List<Integer> strengthOfYear() {

        List<Integer> list_strength_year = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from record_year", null);
        while(cursor.moveToNext()) {
            list_strength_year.add(cursor.getInt(1));
        }
        return list_strength_year;
    }

    public List<Integer> monthOfYear() {

        List<Integer> list_month_year = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from record_year", null);
        while(cursor.moveToNext()) {
            list_month_year.add(cursor.getInt(2));

        }
        return list_month_year;
    }
}

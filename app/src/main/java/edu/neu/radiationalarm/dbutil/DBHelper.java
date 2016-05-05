package edu.neu.radiationalarm.dbutil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2015/12/12.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "message.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table if not exists station (" +
                "id integer primary key autoincrement," +
                "MCC int," +
                "MNC int," +
                "LAC int," +
                "CID int)");
        db.execSQL("create table if not exists imm (" +
                "id integer primary key autoincrement," +
                "BSSS int," +
                "time varchar(255))");
        db.execSQL("create table if not exists around (" +
                "id integer primary key autoincrement," +
                "LAC int," +
                "CID int)");

        db.execSQL("create table if not exists record_day (" +
                "id integer primary key autoincrement," +
                "strength int," +
                "time varchar(255))");
        db.execSQL("create table if not exists record_month (" +
                "id integer primary key autoincrement," +
                "strength int," +
                "day varchar(255))");
        db.execSQL("create table if not exists record_year (" +
                "id integer primary key autoincrement," +
                "strength int," +
                "month varchar(255))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

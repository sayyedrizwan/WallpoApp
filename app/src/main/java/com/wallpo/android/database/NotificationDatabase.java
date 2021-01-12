package com.wallpo.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class NotificationDatabase extends SQLiteOpenHelper {

    public static final String NOTI_DATABASE_NAME = "wallponotification.db";
    public static final String NOTI_TABLE_NAME = "wallponotification";
    public static final int DATA_VERS = 1;
    public static final String COL_ID_NOTI = "ID";
    public static final String COL_USERSID = "USERSID";
    public static final String COL_POSTID = "POSTID";
    public static final String COL_CAPTION = "CAPTION";
    public static final String COL_IMGURL = "IMGURL";
    public static final String COL_TYPE = "TYPE";
    public static final String COL_DATE = "DATE";
    public static final String COL_MAINUSERID = "MAINUSERID";



    public NotificationDatabase(Context context) {
        super(context, NOTI_DATABASE_NAME, null, DATA_VERS);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public NotificationDatabase(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createtable = " CREATE TABLE " + NOTI_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USERSID TEXT, POSTID TEXT, CAPTION TEXT, IMGURL TEXT, TYPE TEXT, DATE TEXT, MAINUSERID TEXT); ";
        db.execSQL(createtable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP IF TABLE EXISTS ", NOTI_TABLE_NAME));

    }

}

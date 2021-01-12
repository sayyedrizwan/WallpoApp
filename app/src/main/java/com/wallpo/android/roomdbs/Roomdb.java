package com.wallpo.android.roomdbs;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Notificationdb.class}, version = 1, exportSchema = false)
public abstract class Roomdb extends RoomDatabase {
    private static Roomdb database;

    private static String DATABASE_NAME = "wallpodb";

    public synchronized static Roomdb getInstance(Context context){
        if (database == null){
            database = Room.databaseBuilder(context.getApplicationContext(), Roomdb.class, DATABASE_NAME)
                    .allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return database;
    }

    public abstract MainDao mainDao();
}

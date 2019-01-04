package com.sholastik.schoolapp;

import android.arch.persistence.room.Room;
import android.content.Context;

public class AppDatabaseSingleton {

    private static AppDatabaseSingleton sSingleton;

    private AppDatabase mAppDatabase;

    private AppDatabaseSingleton(Context context) {
        mAppDatabase = Room.databaseBuilder(context,
                AppDatabase.class,
                "database")
                .build();
    }

    public static AppDatabaseSingleton getSingleton(Context context) {
        if (sSingleton == null) {
            sSingleton = new AppDatabaseSingleton(context);
        }
        return sSingleton;
    }

    public AppDatabase getAppDatabase() {
        return mAppDatabase;
    }

}

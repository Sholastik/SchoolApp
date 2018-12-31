package com.sholastik.schoolapp;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sholastik.schoolapp.ScheduleCode.Day;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppDatabaseSingleton {

    private static AppDatabaseSingleton sSingleton;

    private AppDatabase mAppDatabase;

    private AppDatabaseSingleton(Context context) {
        mAppDatabase = Room.databaseBuilder(context,
                AppDatabase.class,
                "database")
                .addCallback(getCallback(context))
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

    private RoomDatabase.Callback getCallback(final Context context) {
        return new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService
                        .execute(new Runnable() {
                            @Override
                            public void run() {
                                for (int n = 0; n < 7; n++) {
                                    getAppDatabase()
                                            .mScheduleDao()
                                            .insert(new Day(context, n));
                                }
                            }
                        });
                executorService.shutdown();
            }

            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
            }
        };
    }
}

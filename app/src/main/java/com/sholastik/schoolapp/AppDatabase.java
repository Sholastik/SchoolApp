package com.sholastik.schoolapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.sholastik.schoolapp.ScheduleCode.Day;
import com.sholastik.schoolapp.ScheduleCode.Lesson;
import com.sholastik.schoolapp.ScheduleCode.ScheduleDao;

@Database(entities = {Day.class, Lesson.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ScheduleDao mScheduleDao();
}

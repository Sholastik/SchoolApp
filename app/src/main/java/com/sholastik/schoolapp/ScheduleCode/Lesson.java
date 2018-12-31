package com.sholastik.schoolapp.ScheduleCode;

import android.arch.persistence.room.Entity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sholastik.schoolapp.R;

import java.util.UUID;

@Entity(tableName = "lessons", primaryKeys = {"mDayOfWeek", "mId"})
public class Lesson {
    @NonNull
    public String mId = UUID.randomUUID().toString();
    public int mDayOfWeek;
    public int mIndex;

    public long mStartTime;
    public long mLength;
    public String mName;

    public Lesson(Context context, int dayOfWeek, int index) {
        mDayOfWeek = dayOfWeek;
        mIndex = index;

        mName = context.getString(R.string.new_lesson);
        mStartTime = 0;
        mLength = 0;
    }

    public Lesson() {
    }
}

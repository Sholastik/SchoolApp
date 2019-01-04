package com.sholastik.schoolapp.ScheduleCode;

import android.arch.persistence.room.Entity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sholastik.schoolapp.R;

import java.util.Calendar;
import java.util.Date;
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

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 8);
        startTime.set(Calendar.MINUTE, 15);
        mStartTime = startTime.getTime().getTime();
        Calendar length = Calendar.getInstance();
        length.set(Calendar.HOUR_OF_DAY, 0);
        length.set(Calendar.MINUTE, 45);
        mLength = length.getTime().getTime();
    }

    public Lesson() {
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    Date getEndTime() {
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(new Date(mStartTime));
        Calendar length = Calendar.getInstance();
        length.setTime(new Date(mLength));
        endTime.add(Calendar.HOUR, length.get(Calendar.HOUR));
        endTime.add(Calendar.MINUTE, length.get(Calendar.MINUTE));
        return endTime.getTime();
    }

}

package com.sholastik.schoolapp.ScheduleCode;

import android.content.Context;

import com.sholastik.schoolapp.R;

import java.util.Calendar;

public class Lesson {
    private String mName;
    private Calendar mStartTime;
    private Calendar mLength;

    Lesson(Context context) {
        setName(context.getString(R.string.new_lesson));
        mStartTime = Calendar.getInstance();
        mStartTime.set(Calendar.HOUR_OF_DAY, 8);
        mStartTime.set(Calendar.MINUTE, 15);

        mLength = Calendar.getInstance();
        mLength.set(Calendar.HOUR_OF_DAY, 0);
        mLength.set(Calendar.MINUTE, 45);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    Calendar getStartTime() {
        return mStartTime;
    }

    void setStartTime(Calendar startTime) {
        mStartTime = startTime;
    }

    public Calendar getLength() {
        return mLength;
    }

    public void setLength(Calendar length) {
        mLength = length;
    }

    Calendar getEndTime() {
        Calendar endTime = (Calendar) mStartTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, mLength.get(Calendar.HOUR_OF_DAY));
        endTime.add(Calendar.MINUTE, mLength.get(Calendar.MINUTE));
        return endTime;
    }
}

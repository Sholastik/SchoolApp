package com.sholastik.schoolapp.ScheduleCode;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;

import com.sholastik.schoolapp.R;

@Entity(tableName = "days")
public class Day {

    @PrimaryKey
    public final int mDayOfWeek;
    public final String mName;

    public Day(Context context, int dayOfWeek) {
        mDayOfWeek = dayOfWeek;
        mName = context.getResources().getStringArray(R.array.day_of_week)[mDayOfWeek];
    }

}

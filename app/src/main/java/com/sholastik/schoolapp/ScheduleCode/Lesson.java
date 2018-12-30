package com.sholastik.schoolapp.ScheduleCode;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;

import com.sholastik.schoolapp.R;

import java.util.Calendar;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "lessons", foreignKeys = @ForeignKey(entity = Day.class,
        parentColumns = "mDayOfWeek",
        childColumns = "mIndex",
        onDelete = CASCADE))
public class Lesson {
    @PrimaryKey(autoGenerate = true)
    public int mIndex;
    public int mId;
    public long mStartTime;
    public long mLength;
    public String mName;
    public int mDayOfWeek;

    public Lesson(Context context, int dayOfWeek, int index) {
        mName = context.getString(R.string.new_lesson);
        mDayOfWeek = dayOfWeek;
        mIndex = index;

        Calendar startTimeCalendar = Calendar.getInstance();
        startTimeCalendar.set(Calendar.HOUR, 8);
        startTimeCalendar.set(Calendar.MINUTE, 15);
        mStartTime = startTimeCalendar.getTime().getTime();

        Calendar lengthCalendar = Calendar.getInstance();
        lengthCalendar.set(Calendar.HOUR, 0);
        lengthCalendar.set(Calendar.MINUTE, 45);
        mLength = lengthCalendar.getTime().getTime();
    }

    public Lesson() {
    }
}

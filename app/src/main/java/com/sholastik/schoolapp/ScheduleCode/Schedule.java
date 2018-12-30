package com.sholastik.schoolapp.ScheduleCode;

import android.content.Context;

class Schedule {
    private static Schedule sSchedule;
    private Day[] mDays;

    static Schedule get(Context context) {
        if (sSchedule == null) {
            sSchedule = new Schedule(context);
        }
        return sSchedule;
    }

    private Schedule(Context context) {
        mDays = new Day[7];
        for (int n = 0; n < mDays.length; n++) {
            mDays[n] = new Day();
        }
    }

    Day[] getDays() {
        return mDays;
    }

    Day getDay(int dayIndex) {
        return mDays[dayIndex];
    }
}

package com.sholastik.schoolapp.ScheduleCode;

import android.content.Context;

import java.util.ArrayList;

public class Day {
    private ArrayList<Lesson> mLessons;

    Day() {
        mLessons = new ArrayList<>();
    }

    Lesson getLesson(int lessonIndex) {
        return mLessons.get(lessonIndex);
    }

    void addLesson(Context context) {
        mLessons.add(new Lesson(context));
    }

    public void removeLesson(Lesson lesson) {
        mLessons.remove(lesson);
        mLessons.trimToSize();
    }

    void removeLesson(int lessonIndex) {
        mLessons.remove(lessonIndex);
        mLessons.trimToSize();
    }

    public int size() {
        return mLessons.size();
    }
}

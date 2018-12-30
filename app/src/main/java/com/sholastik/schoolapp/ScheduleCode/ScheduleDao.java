package com.sholastik.schoolapp.ScheduleCode;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

public interface ScheduleDao {

    // Days
    @Query("SELECT * FROM days")
    List<Day> getDays();

    @Query("SELECT * FROM days WHERE mDayOfWeek = :dayOfWeek")
    Day getDay(int dayOfWeek);

    @Insert
    void insert(Day day);

    @Update
    void update(Day day);

    @Delete
    void delete(Day day);

    //Lessons
    @Query("SELECT * FROM lessons WHERE mDayOfWeek = :dayOfWeek")
    List<Lesson> getLessonsByDay(int dayOfWeek);

    @Insert
    void insert(Lesson lesson);

    @Update
    void update(Lesson lesson);

    @Delete
    void delete(Lesson lesson);

}

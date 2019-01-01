package com.sholastik.schoolapp.ScheduleCode;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Query("SELECT * FROM lessons WHERE mDayOfWeek = :dayOfWeek ORDER BY mIndex")
    List<Lesson> getLessons(int dayOfWeek);

    @Query("SELECT * FROM lessons WHERE mDayOfWeek = :dayOfWeek AND mIndex = :index")
    Lesson getLesson(int dayOfWeek, int index);

    @Insert
    void insert(Lesson lesson);

    @Update
    void update(Lesson lesson);

    @Delete
    void delete(Lesson lesson);

}

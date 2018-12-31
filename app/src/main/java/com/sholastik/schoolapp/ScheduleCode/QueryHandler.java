package com.sholastik.schoolapp.ScheduleCode;

import android.content.Context;

import com.sholastik.schoolapp.AppDatabaseSingleton;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class QueryHandler {
    static List<Day> getDays(final Context context) {
        Future<List<Day>> future = Executors.newSingleThreadExecutor().submit(new Callable<List<Day>>() {
            @Override
            public List<Day> call() {
                return AppDatabaseSingleton
                        .getSingleton(context)
                        .getAppDatabase()
                        .mScheduleDao()
                        .getDays();
            }
        });
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Day getDay(final Context context, final int dayOfWeek) {
        Future<Day> future = Executors.newSingleThreadExecutor().submit(new Callable<Day>() {
            @Override
            public Day call() {
                return AppDatabaseSingleton
                        .getSingleton(context)
                        .getAppDatabase()
                        .mScheduleDao()
                        .getDay(dayOfWeek);
            }
        });
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    static List<Lesson> getLessonsByDay(final Context context, final int dayOfWeek) {
        Future<List<Lesson>> future = Executors.newSingleThreadExecutor().submit(new Callable<List<Lesson>>() {
            @Override
            public List<Lesson> call() {
                return AppDatabaseSingleton
                        .getSingleton(context)
                        .getAppDatabase()
                        .mScheduleDao()
                        .getLessonsByDay(dayOfWeek);
            }
        });
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Lesson getLesson(final Context context, final int dayOfWeek, final int index) {
        Future<Lesson> future = Executors.newSingleThreadExecutor().submit(new Callable<Lesson>() {
            @Override
            public Lesson call() {
                return AppDatabaseSingleton
                        .getSingleton(context)
                        .getAppDatabase()
                        .mScheduleDao()
                        .getLesson(dayOfWeek, index);
            }
        });
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void removeLesson(final Context context, final Lesson lesson) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabaseSingleton.getSingleton(context).getAppDatabase()
                        .mScheduleDao().delete(lesson);
                indexesUpdate(context, lesson.mDayOfWeek);
            }
        });
    }

    static void updateLesson(final Context context, final Lesson lesson) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabaseSingleton.getSingleton(context).getAppDatabase()
                        .mScheduleDao().update(lesson);
            }
        });
    }

    static void insertLesson(final Context context, final Lesson lesson) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabaseSingleton.getSingleton(context).getAppDatabase()
                        .mScheduleDao().insert(lesson);
            }
        });
    }

    private static void indexesUpdate(final Context context, final int dayOfWeek) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Lesson> lessons = AppDatabaseSingleton.getSingleton(context)
                        .getAppDatabase()
                        .mScheduleDao()
                        .getLessonsByDay(dayOfWeek);
                for (int i = 0; i < lessons.size(); i++) {
                    Lesson lesson = lessons.get(i);
                    lesson.mIndex = i;
                    updateLesson(context, lesson);
                }
            }
        });
    }
}

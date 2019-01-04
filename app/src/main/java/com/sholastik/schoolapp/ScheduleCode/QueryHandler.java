package com.sholastik.schoolapp.ScheduleCode;

import android.content.Context;

import com.sholastik.schoolapp.AppDatabaseSingleton;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class QueryHandler {
    static List<Lesson> getLessons(final Context context, final int dayOfWeek) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<Lesson>> future = executorService.submit(new Callable<List<Lesson>>() {
            @Override
            public List<Lesson> call() {
                return AppDatabaseSingleton
                        .getSingleton(context)
                        .getAppDatabase()
                        .mScheduleDao()
                        .getLessons(dayOfWeek);
            }
        });
        executorService.shutdown();
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Lesson getLesson(final Context context, final int dayOfWeek, final int index) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Lesson> future = executorService.submit(new Callable<Lesson>() {
            @Override
            public Lesson call() {
                return AppDatabaseSingleton
                        .getSingleton(context)
                        .getAppDatabase()
                        .mScheduleDao()
                        .getLesson(dayOfWeek, index);
            }
        });
        executorService.shutdown();
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void removeLesson(final Context context, final Lesson lesson) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabaseSingleton.getSingleton(context).getAppDatabase()
                        .mScheduleDao().delete(lesson);
                indexesUpdate(context, lesson.mDayOfWeek);
            }
        });
        executorService.shutdown();
    }

    static void updateLesson(final Context context, final Lesson lesson) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabaseSingleton.getSingleton(context).getAppDatabase()
                        .mScheduleDao().update(lesson);
            }
        });
        executorService.shutdown();
    }

    static void insertLesson(final Context context, final Lesson lesson) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabaseSingleton.getSingleton(context).getAppDatabase()
                        .mScheduleDao().insert(lesson);
            }
        });
        executorService.shutdown();
    }

    private static void indexesUpdate(final Context context, final int dayOfWeek) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Lesson> lessons = AppDatabaseSingleton.getSingleton(context)
                        .getAppDatabase()
                        .mScheduleDao()
                        .getLessons(dayOfWeek);
                for (int i = 0; i < lessons.size(); i++) {
                    Lesson lesson = lessons.get(i);
                    lesson.mIndex = i;
                    updateLesson(context, lesson);
                }
            }
        });
        executorService.shutdown();
    }
}

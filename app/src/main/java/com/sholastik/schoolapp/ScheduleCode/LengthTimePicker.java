package com.sholastik.schoolapp.ScheduleCode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.sholastik.schoolapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.sholastik.schoolapp.ScheduleCode.StartTimePicker.ARG_DAY_OF_WEEK;
import static com.sholastik.schoolapp.ScheduleCode.StartTimePicker.ARG_INDEX;
import static com.sholastik.schoolapp.ScheduleCode.StartTimePicker.EXTRA_BUNDLE;

public class LengthTimePicker extends DialogFragment {

    public static final String EXTRA_LENGTH_TIME = "com.sholastik.schoolapp.ScheduleCode.LengthTimePicker";

    private View mView;
    private NumberPicker mHoursPicker;
    private NumberPicker mMinutesPicker;

    public static LengthTimePicker getInstance(int dayOfWeek, int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_DAY_OF_WEEK, dayOfWeek);
        bundle.putInt(ARG_INDEX, index);

        LengthTimePicker lengthTimePicker = new LengthTimePicker();
        lengthTimePicker.setArguments(bundle);
        return lengthTimePicker;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar length = Calendar.getInstance();
        int dayOfWeek, index;
        if (getArguments() != null) {
            dayOfWeek = getArguments().getInt(ARG_DAY_OF_WEEK);
            index = getArguments().getInt(ARG_INDEX);
            length.setTime(new Date(QueryHandler.getLesson(getContext(), dayOfWeek, index).mLength));
        }
        int hours = 0;
        int minutes = 0;
        if (length != null) {
            hours = length.get(Calendar.HOUR_OF_DAY);
            minutes = length.get(Calendar.MINUTE);
        }

        mView = LayoutInflater.from(getActivity())
                .inflate(R.layout.length_time_picker, null);

        mHoursPicker = mView.findViewById(R.id.length_picker_hours_picker);
        mHoursPicker.setMinValue(0);
        mHoursPicker.setMaxValue(4);
        mHoursPicker.setValue(hours);

        mMinutesPicker = mView.findViewById(R.id.length_picker_minutes_picker);
        ArrayList<String> minutesValues = getMinutesValues();
        mMinutesPicker.setDisplayedValues(minutesValues.toArray(new String[0]));
        mMinutesPicker.setMaxValue(minutesValues.size() - 1);
        mMinutesPicker.setMinValue(0);
        mMinutesPicker.setValue(minutesValues.indexOf(Integer.toString(minutes)));

        return new AlertDialog.Builder(getActivity())
                .setView(mView)
                .setTitle(R.string.editor_length_of_lesson_tip)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, mHoursPicker.getValue());
                        calendar.set(Calendar.MINUTE, mMinutesPicker.getValue() * 5);
                        setResult(Activity.RESULT_OK, calendar);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .create();
    }

    private ArrayList<String> getMinutesValues() {
        ArrayList<String> strings = new ArrayList<>();
        for (int n = 0; n < 60; n += 5) {
            strings.add(Integer.toString(n));
        }
        return strings;
    }

    private void setResult(int resultCode, Calendar length) {
        if (getTargetFragment() == null) return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_LENGTH_TIME, length.getTime().getTime());
        intent.putExtra(EXTRA_BUNDLE, getArguments());

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}

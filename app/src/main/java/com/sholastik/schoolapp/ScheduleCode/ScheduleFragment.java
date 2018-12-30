package com.sholastik.schoolapp.ScheduleCode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sholastik.schoolapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.sholastik.schoolapp.ScheduleCode.EditorActivity.TO_BE_UPDATED;

public class ScheduleFragment extends Fragment {

    public RecyclerView mRecyclerView;
    private Adapter mAdapter;
    public static final int REQUEST_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.schedule_recyclerview, container, false);
        mRecyclerView = view.findViewById(R.id.schedule_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI(null);

        return view;
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Day mDay;
        private TextView mDayOfWeekTextView;
        private TextView mLessonsTextView;
        private TextView mNoLessonsTextView;

        private int mIndex;

        ViewHolder(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater.inflate(R.layout.schedule_item_day, viewGroup, false));
            itemView.setOnClickListener(this);
            mDayOfWeekTextView = itemView.findViewById(R.id.day_of_week);
            mLessonsTextView = itemView.findViewById(R.id.lessons_text_view);
            mNoLessonsTextView = itemView.findViewById(R.id.no_lessons_text_view);
        }

        void bind(Day day) {
            mIndex = getAdapterPosition();
            mDay = day;
            String mDayOfWeek = getResources().getStringArray(R.array.day_of_week)[mIndex];
            mDayOfWeekTextView.setText(mDayOfWeek);
            if (day.size() == 0) {
                mLessonsTextView.setVisibility(View.INVISIBLE);
                mNoLessonsTextView.setVisibility(View.VISIBLE);
                mLessonsTextView.setText("");
                return;
            } else {
                mLessonsTextView.setVisibility(View.VISIBLE);
                mNoLessonsTextView.setVisibility(View.INVISIBLE);
                mLessonsTextView.setText("");
            }

            //TODO: Rewrite this whole crap!
            for (int i = 1; i <= mDay.size(); i++) {
                Lesson lesson = mDay.getLesson(i - 1);
                String order = getString(R.string.schedule_order, i);
                String name = lesson.getName();
                String time = getString(
                        R.string.schedule_time,
                        new SimpleDateFormat("H:mm").format(lesson.getStartTime().getTime()),
                        new SimpleDateFormat("H:mm").format(lesson.getEndTime().getTime())
                );
                mLessonsTextView.append(String.format("%s    %s    %s\n", order, name, time));
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = EditorActivity.getIntent(getContext(), mIndex);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Day[] mDays;

        Adapter(Day[] days) {
            mDays = days;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ViewHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.bind(mDays[i]);
        }

        @Override
        public int getItemCount() {
            return mDays.length;
        }
    }

    private void updateUI(ArrayList<Integer> arrayList) {
        if (arrayList == null) {
            Schedule schedule = Schedule.get(getActivity());
            Day[] days = schedule.getDays();
            mAdapter = new Adapter(days);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            for (int i : arrayList) {
                mAdapter.notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    updateUI(data.getIntegerArrayListExtra(TO_BE_UPDATED));
                }
            }
        }
    }
}
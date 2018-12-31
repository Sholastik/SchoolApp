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
import java.util.Locale;

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

        ViewHolder(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater.inflate(R.layout.schedule_item_day, viewGroup, false));
            itemView.setOnClickListener(this);
            mDayOfWeekTextView = itemView.findViewById(R.id.day_of_week);
            mLessonsTextView = itemView.findViewById(R.id.lessons_text_view);
            mNoLessonsTextView = itemView.findViewById(R.id.no_lessons_text_view);
        }

        void bind() {
            mDay = QueryHandler.getDay(getContext(), getAdapterPosition());
            mDayOfWeekTextView.setText(mDay.mName);
            if (QueryHandler.getLessonsByDay(getContext(), mDay.mDayOfWeek).size() == 0) {
                mLessonsTextView.setVisibility(View.INVISIBLE);
                mNoLessonsTextView.setVisibility(View.VISIBLE);
                mNoLessonsTextView.setText(R.string.no_lessons);
                mLessonsTextView.setText("");
                return;
            } else {
                mLessonsTextView.setVisibility(View.VISIBLE);
                mNoLessonsTextView.setVisibility(View.INVISIBLE);
                mNoLessonsTextView.setText("");
                mLessonsTextView.setText("");
            }

            //TODO: Rewrite this whole crap!
            for (Lesson lesson : QueryHandler.getLessonsByDay(getContext(), mDay.mDayOfWeek)) {
                String order = getString(R.string.schedule_order, lesson.mIndex + 1);
                String name = lesson.mName;
                String time = getString(
                        R.string.schedule_time,
                        new SimpleDateFormat("H:mm", Locale.getDefault()).format(lesson.mStartTime),
                        new SimpleDateFormat("H:mm", Locale.getDefault()).format(lesson.mStartTime + lesson.mLength)
                );
                mLessonsTextView.append(String.format("%s    %s    %s\n", order, name, time));
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = EditorActivity.getIntent(getContext(), mDay.mDayOfWeek);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ViewHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.bind();
        }

        @Override
        public int getItemCount() {
            if (QueryHandler.getDays(getContext()).size() == 0) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ScheduleFragment()).commit();
                return 0;
            }
            return QueryHandler.getDays(getContext()).size();
        }
    }

    private void updateUI(ArrayList<Integer> arrayList) {
        if (arrayList == null && mAdapter == null) {
            mAdapter = new Adapter();
            mRecyclerView.setAdapter(mAdapter);
        } else if (arrayList != null) {
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
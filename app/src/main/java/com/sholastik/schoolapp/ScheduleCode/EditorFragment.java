package com.sholastik.schoolapp.ScheduleCode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sholastik.schoolapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import static com.sholastik.schoolapp.ScheduleCode.LengthTimePicker.EXTRA_LENGTH_TIME;
import static com.sholastik.schoolapp.ScheduleCode.StartTimePicker.EXTRA_START_TIME;

public class EditorFragment extends Fragment {

    public static final String DAY_EXTRA = "com.sholastik.schoolapp.ScheduleCode.EditorFragment";
    public static final String DIALOG_START_TIME = "DialogStartTime";
    public static final String DIALOG_LENGTH_TIME = "DialogLengthTime";

    public static final int REQUEST_START_TIME = 0;
    public static final int REQUEST_LENGTH_TIME = 910521590;

    public static Fragment getFragment(int dayIndex) {
        Fragment fragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putInt(DAY_EXTRA, dayIndex);
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView mRecyclerView;
    private Day mDay;
    private Adapter mAdapter;
    public int mDayIndex;
    public boolean isChanged;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_editor, container, false);
        mRecyclerView = view.findViewById(R.id.editor_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        assert getArguments() != null;
        mDay = Schedule.get(getContext()).getDay(getArguments().getInt(DAY_EXTRA, 0));
        mDayIndex = getArguments().getInt(DAY_EXTRA, 0);
        isChanged = false;
        updateUI();

        return view;
    }


    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher {
        private Button mStartTimeButton;
        private Button mLengthButton;
        private EditText mLessonNameEditText;
        private TextView mLessonOrder;
        private Lesson mLesson;
        private ImageView mRemoveLesson;
        private int mIndex;

        ViewHolder(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater.inflate(R.layout.editor_item, viewGroup, false));
            itemView.setOnClickListener(this);
            mStartTimeButton = itemView.findViewById(R.id.editor_lesson_start_button);
            mLengthButton = itemView.findViewById(R.id.editor_lesson_length_button);
            mLessonNameEditText = itemView.findViewById(R.id.editor_lesson_name);
            mLessonOrder = itemView.findViewById(R.id.editor_item_index);
            mRemoveLesson = itemView.findViewById(R.id.editor_remove_lesson);
        }

        void bind(Lesson lesson, int index) {
            mIndex = index;
            mLesson = lesson;
            mLessonNameEditText.setText(mLesson.getName());
            mLessonOrder.setText(getString(R.string.schedule_order, mIndex + 1));
            int length = lesson.getLength().get(Calendar.HOUR_OF_DAY) * 60 + lesson.getLength().get(Calendar.MINUTE);
            mLengthButton.setText(getString(R.string.lesson_length, length));
            mStartTimeButton.setText(new SimpleDateFormat("H:mm").format(lesson.getStartTime().getTime()));

            mStartTimeButton.setOnClickListener(this);
            mLengthButton.setOnClickListener(this);
            mRemoveLesson.setOnClickListener(this);

            mLessonNameEditText.addTextChangedListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == mStartTimeButton) {
                FragmentManager fragmentManager = getFragmentManager();
                StartTimePicker startTimePicker = StartTimePicker.newInstance(mLesson.getStartTime());
                startTimePicker.setTargetFragment(EditorFragment.this, REQUEST_START_TIME + mIndex);
                assert fragmentManager != null;
                startTimePicker.show(fragmentManager, DIALOG_START_TIME);
            } else if (view == mLengthButton) {
                FragmentManager fragmentManager = getFragmentManager();
                LengthTimePicker lengthTimePicker = LengthTimePicker.getInstance(mLesson.getLength());
                lengthTimePicker.setTargetFragment(EditorFragment.this, REQUEST_LENGTH_TIME + mIndex);
                assert fragmentManager != null;
                lengthTimePicker.show(fragmentManager, DIALOG_LENGTH_TIME);
            } else if (view == mRemoveLesson) {
                try {
                    mDay.removeLesson(mIndex);
                } catch (IndexOutOfBoundsException ignored) {
                }
                Objects.requireNonNull(mRecyclerView.getAdapter()).notifyItemRemoved(mIndex);
                mRecyclerView.getAdapter().notifyItemRangeChanged(mIndex, mRecyclerView.getAdapter().getItemCount() - mIndex);
                isChanged = true;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mLesson.setName(s.toString());
            isChanged = true;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Day mDay;

        Adapter(Day day) {
            mDay = day;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ViewHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.bind(mDay.getLesson(i), i);
        }

        @Override
        public int getItemCount() {
            return mDay.size();
        }


    }

    private void updateUI() {
        mAdapter = new Adapter(mDay);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                for (int i = 0; i < Objects.requireNonNull(mRecyclerView.getAdapter()).getItemCount(); i++) {
                    if (requestCode - i == REQUEST_START_TIME) {
                        mDay.getLesson(i).setStartTime((Calendar) data.getSerializableExtra(EXTRA_START_TIME));
                        mRecyclerView.getAdapter().notifyItemChanged(i);
                        isChanged = true;
                        break;
                    } else if (requestCode - i == REQUEST_LENGTH_TIME) {
                        mDay.getLesson(i).setLength((Calendar) data.getSerializableExtra(EXTRA_LENGTH_TIME));
                        mRecyclerView.getAdapter().notifyItemChanged(i);
                        isChanged = true;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.editor_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_lesson: {
                mDay.addLesson(getContext());
                isChanged = true;
                Objects.requireNonNull(mRecyclerView.getAdapter()).notifyItemInserted(mDay.size() - 1);
                mRecyclerView.scrollToPosition(mDay.size() - 1);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.sholastik.schoolapp.ScheduleCode.LengthTimePicker.EXTRA_LENGTH_TIME;
import static com.sholastik.schoolapp.ScheduleCode.StartTimePicker.ARG_DAY_OF_WEEK;
import static com.sholastik.schoolapp.ScheduleCode.StartTimePicker.ARG_INDEX;
import static com.sholastik.schoolapp.ScheduleCode.StartTimePicker.EXTRA_BUNDLE;
import static com.sholastik.schoolapp.ScheduleCode.StartTimePicker.EXTRA_START_TIME;

public class EditorFragment extends Fragment {

    private static final String DAY_EXTRA = "com.sholastik.schoolapp.ScheduleCode.EditorFragment";
    private static final String DIALOG_START_TIME = "DialogStartTime";
    private static final String DIALOG_LENGTH_TIME = "DialogLengthTime";

    private static final int REQUEST_START_TIME = 0;
    private static final int REQUEST_LENGTH_TIME = 1;

    public static Fragment getFragment(int dayIndex) {
        Fragment fragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putInt(DAY_EXTRA, dayIndex);
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView mRecyclerView;
    public int mDayIndex;
    public boolean isChanged;
    private List<Lesson> mLessonList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_editor, container, false);
        mRecyclerView = view.findViewById(R.id.editor_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(30);
        assert getArguments() != null;
        mDayIndex = getArguments().getInt(DAY_EXTRA, 0);
        isChanged = false;
        updateUI();

        return view;
    }


    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher {
        private final Button mStartTimeButton;
        private final Button mLengthButton;
        private final EditText mLessonNameEditText;
        private final TextView mLessonOrder;
        private Lesson mLesson;
        private final ImageView mRemoveLesson;

        ViewHolder(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater.inflate(R.layout.editor_item, viewGroup, false));
            itemView.setOnClickListener(this);
            mStartTimeButton = itemView.findViewById(R.id.editor_lesson_start_button);
            mLengthButton = itemView.findViewById(R.id.editor_lesson_length_button);
            mLessonNameEditText = itemView.findViewById(R.id.editor_lesson_name);
            mLessonOrder = itemView.findViewById(R.id.editor_item_index);
            mRemoveLesson = itemView.findViewById(R.id.editor_remove_lesson);

            mStartTimeButton.setOnClickListener(this);
            mLengthButton.setOnClickListener(this);
            mRemoveLesson.setOnClickListener(this);

            mLessonNameEditText.addTextChangedListener(this);
        }

        void bind(Lesson lesson) {
            mLesson = lesson;
            mLessonNameEditText.setText(Objects.requireNonNull(mLesson).mName);
            mLessonOrder.setText(getString(R.string.schedule_order, mLesson.mIndex + 1));

            mLengthButton.setText(getString(R.string.lesson_length, Integer.parseInt(new SimpleDateFormat("H", Locale.getDefault()).format(mLesson.mLength)) * 60 +
                    Integer.parseInt(new SimpleDateFormat("mm", Locale.getDefault()).format(mLesson.mLength))));
            mStartTimeButton.setText(new SimpleDateFormat("H:mm", Locale.getDefault()).format(mLesson.mStartTime));
        }

        @Override
        public void onClick(View view) {
            if (view == mStartTimeButton) {
                FragmentManager fragmentManager = getFragmentManager();
                StartTimePicker startTimePicker = StartTimePicker.newInstance(mLesson.mDayOfWeek, mLesson.mIndex);
                startTimePicker.setTargetFragment(EditorFragment.this, REQUEST_START_TIME);
                assert fragmentManager != null;
                startTimePicker.show(fragmentManager, DIALOG_START_TIME);
            } else if (view == mLengthButton) {
                FragmentManager fragmentManager = getFragmentManager();
                LengthTimePicker lengthTimePicker = LengthTimePicker.getInstance(mLesson.mDayOfWeek, mLesson.mIndex);
                lengthTimePicker.setTargetFragment(EditorFragment.this, REQUEST_LENGTH_TIME);
                assert fragmentManager != null;
                lengthTimePicker.show(fragmentManager, DIALOG_LENGTH_TIME);
            } else if (view == mRemoveLesson) {
                mLessonList.remove(mLesson);
                QueryHandler.removeLesson(getContext(), mLesson);
                Objects.requireNonNull(mRecyclerView.getAdapter()).notifyItemRemoved(mLesson.mIndex);
                mRecyclerView.getAdapter().notifyItemRangeChanged(mLesson.mIndex, mRecyclerView.getAdapter().getItemCount() - mLesson.mIndex);
                isChanged = true;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mLesson.mName = s.toString();
            QueryHandler.updateLesson(getContext(), mLesson);
            isChanged = true;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Adapter() {
            mLessonList = QueryHandler.getLessonsByDay(getContext(), mDayIndex);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ViewHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.bind(mLessonList.get(i));
        }

        @Override
        public int getItemCount() {
            return mLessonList.size();
        }


    }

    private void updateUI() {
        mRecyclerView.setAdapter(new Adapter());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (requestCode == REQUEST_START_TIME) {
                    Bundle bundle = data.getBundleExtra(EXTRA_BUNDLE);
                    Lesson lesson = QueryHandler.getLesson(getContext(),
                            bundle.getInt(ARG_DAY_OF_WEEK),
                            bundle.getInt(ARG_INDEX));
                    mLessonList.get(bundle.getInt(ARG_INDEX)).mStartTime = data.getLongExtra(EXTRA_START_TIME, 0);
                    Objects.requireNonNull(lesson).mStartTime = data.getLongExtra(EXTRA_START_TIME, 0);
                    QueryHandler.updateLesson(getContext(), lesson);
                    Objects.requireNonNull(mRecyclerView.getAdapter()).notifyItemChanged(lesson.mIndex);
                        isChanged = true;
                } else if (requestCode == REQUEST_LENGTH_TIME) {
                    Bundle bundle = data.getBundleExtra(EXTRA_BUNDLE);
                    Lesson lesson = QueryHandler.getLesson(getContext(),
                            bundle.getInt(ARG_DAY_OF_WEEK),
                            bundle.getInt(ARG_INDEX));
                    mLessonList.get(bundle.getInt(ARG_INDEX)).mLength = data.getLongExtra(EXTRA_LENGTH_TIME, 0);
                    Objects.requireNonNull(lesson).mLength = data.getLongExtra(EXTRA_LENGTH_TIME, 0);
                    QueryHandler.updateLesson(getContext(), lesson);
                    Objects.requireNonNull(mRecyclerView.getAdapter()).notifyItemChanged(lesson.mIndex);
                        isChanged = true;
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
                int index = Objects.requireNonNull(mRecyclerView.getAdapter()).getItemCount();
                Lesson lesson = new Lesson(Objects.requireNonNull(getContext()), mDayIndex, index);
                QueryHandler.insertLesson(getContext(), lesson);
                mLessonList.add(lesson);
                isChanged = true;
                Objects.requireNonNull(mRecyclerView.getAdapter()).notifyItemInserted(mRecyclerView.getAdapter().getItemCount());
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

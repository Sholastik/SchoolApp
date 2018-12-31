package com.sholastik.schoolapp.ScheduleCode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sholastik.schoolapp.R;

import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity {
    public static final String INDEX_EXTRA = "com.sholastik.schoolapp.ScheduleCode.EditorActivity";
    public static final String TO_BE_UPDATED = "to_be_updated";

    private ArrayList<EditorFragment> mEditorFragments;

    public static Intent getIntent(Context context, int index) {
        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(INDEX_EXTRA, index);
        return intent;
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        int index = getIntent().getIntExtra(INDEX_EXTRA, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        setTitle(QueryHandler.getDay(EditorActivity.this, index).mName);

        mEditorFragments = new ArrayList<>();
        ViewPager viewPager = findViewById(R.id.editor_view_pager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                EditorFragment editorFragment = (EditorFragment) EditorFragment.getFragment(i);
                mEditorFragments.add(editorFragment);
                return editorFragment;
            }

            @Override
            public int getCount() {
                return QueryHandler.getDays(EditorActivity.this).size();
            }
        });
        viewPager.setCurrentItem(index);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setTitle(QueryHandler.getDay(EditorActivity.this, i).mName);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        ArrayList<Integer> list = new ArrayList<>();

        for (EditorFragment editorFragment : mEditorFragments) {
            if (editorFragment.isChanged && !list.contains(editorFragment.mDayIndex)) {
                list.add(editorFragment.mDayIndex);
            }
        }

        Intent intent = new Intent().putIntegerArrayListExtra(TO_BE_UPDATED, list);
        setResult(Activity.RESULT_OK, intent);

        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}


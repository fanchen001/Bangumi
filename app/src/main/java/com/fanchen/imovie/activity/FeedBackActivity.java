package com.fanchen.imovie.activity;

import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;

import butterknife.InjectView;

/**
 * Created by fanchen on 2017/10/13.
 */
public class FeedBackActivity extends BaseToolbarActivity implements View.OnClickListener {

    @InjectView(R.id.et_feedback)
    AppCompatEditText mFeedbackEditText;
    @InjectView(R.id.et_contact)
    AppCompatEditText mContactEditText;
    @InjectView(R.id.btn_complete)
    TextView mCompleteTextView;
    @InjectView(R.id.tv_comic_cache)
    TextView mCacheTextView;
    @InjectView(R.id.btn_comic_cache)
    RelativeLayout mCacheRelativeLayout;
    @InjectView(R.id.tv_chapter_pictures)
    TextView mChapterTextView;
    @InjectView(R.id.btn_chapter_pictures)
    RelativeLayout mChapterRelativeLayout;
    @InjectView(R.id.tv_product_bug)
    TextView mBugTextView;
    @InjectView(R.id.btn_product_bug)
    RelativeLayout mBugRelativeLayout;

    @Override
    protected String getActivityTitle() {
        return getString(R.string.feedback);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void setListener() {
        super.setListener();
        mBugRelativeLayout.setOnClickListener(this);
        mChapterRelativeLayout.setOnClickListener(this);
        mCacheRelativeLayout.setOnClickListener(this);
        mCompleteTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_comic_cache:
                mBugTextView.setTextColor(getResources().getColor(R.color.text_black));
                mChapterTextView.setTextColor(getResources().getColor(R.color.text_black));
                mCacheTextView.setTextColor(getResources().getColor(R.color.comm_red_high));
                break;
            case R.id.btn_chapter_pictures:
                mBugTextView.setTextColor(getResources().getColor(R.color.text_black));
                mCacheTextView.setTextColor(getResources().getColor(R.color.text_black));
                mChapterTextView.setTextColor(getResources().getColor(R.color.comm_red_high));
                break;
            case R.id.btn_product_bug:
                mCacheTextView.setTextColor(getResources().getColor(R.color.text_black));
                mChapterTextView.setTextColor(getResources().getColor(R.color.text_black));
                mBugTextView.setTextColor(getResources().getColor(R.color.comm_red_high));
                break;
            case R.id.btn_complete:
                String feedback = getEditTextString(mFeedbackEditText);
                String contact = getEditTextString(mContactEditText);
                if(TextUtils.isEmpty(feedback)){
                    showSnackbar(getString(R.string.error_feedback_null));
                    return;
                }
                break;
        }
    }
}

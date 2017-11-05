package com.fanchen.imovie.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.entity.bmob.BmobObj;
import com.fanchen.imovie.entity.bmob.Feedback;
import com.fanchen.imovie.util.DialogUtil;

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

    private int feedbackType = Feedback.TYPE_OTHER;

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
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        mBugTextView.setTextColor(getResources().getColor(R.color.text_black));
        mChapterTextView.setTextColor(getResources().getColor(R.color.text_black));
        mCacheTextView.setTextColor(getResources().getColor(R.color.comm_red_high));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_comic_cache:
                feedbackType = Feedback.TYPE_OTHER;
                mBugTextView.setTextColor(getResources().getColor(R.color.text_black));
                mChapterTextView.setTextColor(getResources().getColor(R.color.text_black));
                mCacheTextView.setTextColor(getResources().getColor(R.color.comm_red_high));
                break;
            case R.id.btn_chapter_pictures:
                feedbackType = Feedback.TYPE_SUGGEST;
                mBugTextView.setTextColor(getResources().getColor(R.color.text_black));
                mCacheTextView.setTextColor(getResources().getColor(R.color.text_black));
                mChapterTextView.setTextColor(getResources().getColor(R.color.comm_red_high));
                break;
            case R.id.btn_product_bug:
                feedbackType = Feedback.TYPE_BUG;
                mCacheTextView.setTextColor(getResources().getColor(R.color.text_black));
                mChapterTextView.setTextColor(getResources().getColor(R.color.text_black));
                mBugTextView.setTextColor(getResources().getColor(R.color.comm_red_high));
                break;
            case R.id.btn_complete:
                if(!checkLogin()){
                    return;
                }
                String feedback = getEditTextString(mFeedbackEditText);
                String contact = getEditTextString(mContactEditText);
                if(TextUtils.isEmpty(feedback)){
                    showSnackbar(getString(R.string.error_feedback_null));
                    return;
                }
                Feedback feed = new Feedback();
                feed.setEmail(contact);
                feed.setContent(feedback);
                feed.setUser(getLoginUser());
                feed.setType(feedbackType);
                feed.save(saveListener);
                break;
        }
    }

    private BmobObj.OnRefreshListener saveListener = new BmobObj.OnRefreshListener() {

        @Override
        public void onStart() {
            DialogUtil.showProgressDialog(FeedBackActivity.this,getString(R.string.loading));
        }

        @Override
        public void onFinish() {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onSuccess() {
            mContactEditText.setText("");
            mFeedbackEditText.setText("");
            showSnackbar(getString(R.string.send_success));
        }

        @Override
        public void onFailure(int i, String s) {
            showSnackbar(s);
        }

    };

}

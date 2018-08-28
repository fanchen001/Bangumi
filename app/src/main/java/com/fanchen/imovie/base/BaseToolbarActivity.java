package com.fanchen.imovie.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;

import butterknife.InjectView;

/**
 * 布局包含有<include layout="@layout/layout_toolbar"></include>
 * 的Activity
 * Created by fanchen on 2017/8/1.
 */
public abstract class BaseToolbarActivity extends BaseActivity {

    @InjectView(R.id.iv_top_back)
    protected ImageView mBackView;
    @InjectView(R.id.tv_top_title)
    protected TextView mTitleView;
    @InjectView(R.id.toolbar_top)
    protected Toolbar mToolbar;

    /**
     * @return
     */
    protected abstract String getActivityTitle();

    @Override
    protected void setListener() {
        super.setListener();
        if(checkToolbarViewNull())return;
        mBackView.setOnClickListener(finishClickListener);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        if(checkToolbarViewNull())return;
        setSupportActionBar(mToolbar);
        mTitleView.setText(getActivityTitle());
    }

    public View getBackView() {
        return mBackView;
    }

    public TextView getTitleView() {
        return mTitleView;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    private View.OnClickListener finishClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!isFinishing())
                finish();
        }

    };

    public boolean checkToolbarViewNull(){
        return mBackView == null || mTitleView == null || mToolbar == null;
    }
}

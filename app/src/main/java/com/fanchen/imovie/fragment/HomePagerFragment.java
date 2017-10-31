package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.ApkListActivity;
import com.fanchen.imovie.activity.DownloadTabActivity;
import com.fanchen.imovie.activity.LoginActivity;
import com.fanchen.imovie.activity.MainActivity;
import com.fanchen.imovie.activity.SearchBangumiActivity;
import com.fanchen.imovie.activity.UserActivity;
import com.fanchen.imovie.adapter.pager.HomePagerAdapter;
import com.fanchen.imovie.base.BaseFragment;
import com.fanchen.imovie.dialog.fragment.SearchDialogFragment;
import com.fanchen.imovie.entity.AppEvent;
import com.fanchen.imovie.entity.bmob.User;
import com.fanchen.imovie.entity.face.ISearchWord;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.view.CircleImageView;
import com.fanchen.imovie.view.NoScrollViewPager;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;


/**
 * Created by fanchen on 2017/2/24.
 */
public class HomePagerFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener,
        SearchDialogFragment.OnSearchClickListener, View.OnClickListener {
    public static final String CURRENT_ITEM = "item";
    @InjectView(R.id.toolbar_user_avatar)
    protected CircleImageView mCircleImageView;
    @InjectView(R.id.tv_home_name)
    protected TextView mNameTextView;
    @InjectView(R.id.toolbar)
    protected Toolbar mToolbar;
    @InjectView(R.id.sliding_tabs)
    protected TabLayout mSlidingTab;
    @InjectView(R.id.view_pager)
    protected NoScrollViewPager mViewPager;

    private HomePagerAdapter mHomePagerAdapter;
    private SearchDialogFragment mSearchFragment = SearchDialogFragment.newInstance();

    @Override
    protected int getLayout() {
        return R.layout.fragment_home_pager;
    }

    @Override
    protected void findView(View v) {
        mToolbar = findViewById(R.id.toolbar);
        mNameTextView = findViewById(R.id.tv_home_name);
        mViewPager = findViewById(R.id.view_pager);
        mSlidingTab = findViewById(R.id.sliding_tabs);
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        super.initFragment(savedInstanceState, args);
        setHasOptionsMenu(true);
        mToolbar.setTitle("");
        setLoginInfo(activity == null ? null : activity.getLoginUser());
        activity.setSupportActionBar(mToolbar);
        mHomePagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mHomePagerAdapter);
        mSlidingTab.setupWithViewPager(mViewPager);
        if(savedInstanceState != null){
            mViewPager.setCurrentItem(savedInstanceState.getInt(CURRENT_ITEM));
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.setOnClickListener(this);
        mSearchFragment.setOnSearchClickListener(this);
    }

    /**
     *
     * @param user
     */
    private void setLoginInfo(User user){
        if(user != null){
            mNameTextView.setText(user.getNickName());
            if(!TextUtils.isEmpty(user.getHeaderUrl()) && activity != null && activity.appliction != null){
                new PicassoWrap(Picasso.with(activity.appliction)).loadVertical(user.getHeaderUrl(),mCircleImageView);
            }else if(user.getHeader() != null && activity != null && activity.appliction != null){
                new PicassoWrap(Picasso.with(activity.appliction)).loadVertical(user.getHeader().getFileUrl(activity.appliction),mCircleImageView);
            }
        }else{
            mNameTextView.setText(getString(R.string.not_login));
            mCircleImageView.setImageResource(R.drawable.ico_user_default);
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    public void onMainEvent(AppEvent event) {
        if(LoginActivity.class.getName().equals(event.from) && AppEvent.LOGIN == event.what){
            setLoginInfo((User) event.data);
        }else if(UserActivity.class.getName().equals(event.from) && AppEvent.LOGOUT == event.what){
            setLoginInfo(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mViewPager != null){
            outState.putInt(CURRENT_ITEM,mViewPager.getCurrentItem());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSearchClick(ISearchWord keyword) {
        SearchBangumiActivity.startActivity(activity, keyword.getWord());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_search://点击搜索
                openSearchDialog();
                break;
            case R.id.id_action_download:
                DownloadTabActivity.startActivity(activity);
                break;
            case R.id.id_action_game:
                ApkListActivity.startActivity(activity, ApkListActivity.TYPE_GAME);
                break;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).toggleDrawer();
                }
                break;
        }
    }

    public void openSearchDialog() {
        if (mSearchFragment != null) {
            mSearchFragment.show(getActivity().getSupportFragmentManager(), getClass().getSimpleName());
        }
    }

}

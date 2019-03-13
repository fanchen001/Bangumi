package com.fanchen.imovie.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.ApkListActivity;
import com.fanchen.imovie.activity.DownloadTabActivity;
import com.fanchen.imovie.activity.LoginActivity;
import com.fanchen.imovie.activity.MainActivity;
import com.fanchen.imovie.activity.SearchBangumiActivity;
import com.fanchen.imovie.activity.UserActivity;
import com.fanchen.imovie.adapter.pager.HomePagerAdapter;
import com.fanchen.imovie.base.BaseFragment;
import com.fanchen.imovie.dialog.MaterialListDialog;
import com.fanchen.imovie.dialog.fragment.SearchDialogFragment;
import com.fanchen.imovie.entity.AppEvent;
import com.fanchen.imovie.entity.bmob.User;
import com.fanchen.imovie.entity.face.ISearchWord;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.util.DisplayUtil;
import com.fanchen.imovie.util.PayUtil;
import com.fanchen.imovie.util.StreamUtil;
import com.fanchen.imovie.view.CircleImageView;
import com.fanchen.imovie.view.MarqueeView;
import com.fanchen.imovie.view.NoScrollViewPager;

import java.util.ArrayList;

import butterknife.InjectView;


/**
 * HomePagerFragment
 * Created by fanchen on 2017/2/24.
 */
public class HomePagerFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener,
        SearchDialogFragment.OnSearchClickListener, View.OnClickListener, AdapterView.OnItemClickListener, MarqueeView.OnItemClickListener {
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
    @InjectView(R.id.mv_adv)
    protected MarqueeView mMarqueeView;

    private HomePagerAdapter mHomePagerAdapter;
    private SearchDialogFragment mSearchFragment = SearchDialogFragment.newInstance();

    @Override
    protected int getLayout() {
        return R.layout.fragment_home_pager;
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        super.initFragment(savedInstanceState, args);
        if (IMovieAppliction.ADVS != null && IMovieAppliction.ADVS.length > 0) {
            ArrayList<View> views = new ArrayList<>();
            Drawable drawable = getResources().getDrawable(R.drawable.ic_adv);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  //此为必须写的
            for (int i = 0; i < IMovieAppliction.ADVS.length; i++) {
                TextView textView = new TextView(activity);
                textView.setText(IMovieAppliction.ADVS[i]);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(12f);
                textView.setCompoundDrawablePadding(DisplayUtil.dip2px(activity, 4));
                textView.setCompoundDrawables(drawable, null, null, null);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                views.add(textView);
            }
            mMarqueeView.setViews(views);
            mMarqueeView.setVisibility(View.VISIBLE);
        }
        setHasOptionsMenu(true);
        mToolbar.setTitle("");
        setLoginInfo(activity == null ? null : activity.getLoginUser());
        activity.setSupportActionBar(mToolbar);
        mHomePagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mHomePagerAdapter);
        mSlidingTab.setupWithViewPager(mViewPager);
        if (savedInstanceState != null) {
            mViewPager.setCurrentItem(savedInstanceState.getInt(CURRENT_ITEM));
        } else {
            //默认选中影视页
            mViewPager.setCurrentItem(2);
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.setOnClickListener(this);
        mMarqueeView.setOnItemClickListener(this);
        mSearchFragment.setOnSearchClickListener(this);
    }

    /**
     * @param user
     */
    private void setLoginInfo(User user) {
        if (user != null) {
            mNameTextView.setText(user.getNickName());
            if (!TextUtils.isEmpty(user.getHeaderUrl()) && activity != null && activity.appliction != null) {
                new PicassoWrap(getPicasso()).loadVertical(user.getHeaderUrl(), mCircleImageView);
            } else if (user.getHeader() != null && activity != null && activity.appliction != null) {
                new PicassoWrap(getPicasso()).loadVertical(user.getHeader().getFileUrl(activity.appliction), mCircleImageView);
            }
        } else {
            mNameTextView.setText(getStringFix(R.string.not_login));
            mCircleImageView.setImageResource(R.drawable.ico_user_default);
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    public void onMainEvent(AppEvent event) {
        if (LoginActivity.class.getName().equals(event.from) && AppEvent.LOGIN == event.what) {
            setLoginInfo((User) event.data);
        } else if (UserActivity.class.getName().equals(event.from) && AppEvent.LOGOUT == event.what) {
            setLoginInfo(null);
        } else if (AppEvent.UPDATE == event.what && activity != null) {
            setLoginInfo(activity.getLoginUser());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mViewPager != null) {
            outState.putInt(CURRENT_ITEM, mViewPager.getCurrentItem());
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            PayUtil.startAlipayClient(activity, PayUtil.ALIPAY_URL);
        } else if (i == 1) {
            PayUtil.startWechatClient(activity);
            StreamUtil.copyAssetsFileAsyn(activity, "wechat_code.png");
            showToast("请在微信扫一扫，相册选择[次元番打赏]二维码，进行打赏");
        } else {
            PayUtil.startQQClient(activity, PayUtil.QQ_URL);
            StreamUtil.copyAssetsFileAsyn(activity, "qq_code.png");
            showToast("请在QQ扫一扫，相册选择[次元番打赏]二维码，进行打赏");
        }
    }

    @Override
    public void onItemClick(int position, View view) {
        MaterialListDialog listDialog = new MaterialListDialog(activity, new String[]{"支付宝", "微信", "QQ钱包"});
        listDialog.setItemClickListener(this);
        listDialog.show();
    }

    @Override
    public void onClick(View v) {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).toggleDrawer();
        }
    }

    public void openSearchDialog() {
        if (mSearchFragment != null) {
            mSearchFragment.show(getActivity().getSupportFragmentManager(), getClass().getSimpleName());
        }
    }

}

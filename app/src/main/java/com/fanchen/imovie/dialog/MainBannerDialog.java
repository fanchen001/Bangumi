package com.fanchen.imovie.dialog;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.VideoDetailsActivity;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.entity.bmob.DialogBanner;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * MainBannerDialog
 * Created by fanchen on 2018/8/6.
 */
public class MainBannerDialog extends MaterialDialog implements OnButtonClickListener, ViewPager.OnPageChangeListener {

    private List<DialogBanner> banners;
    private BaseActivity activity;

    private TextView mTextView;
    private ViewPager mViewPager;
    private SharedPreferences defaultSharedPreferences;

    public MainBannerDialog(BaseActivity context, List<DialogBanner> banners) {
        super(context, R.layout.dialog_main_banner);
        this.activity = context;
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (banners.size() > 5) {
            this.banners = banners.subList(0, 5);
        } else {
            this.banners = banners;
        }
        btnText("知道了", "马上去看");
        setButtonClickListener(this);
    }

    @Override
    public void setUiBeforShow() {
        super.setUiBeforShow();
        mTextView = (TextView) view.findViewById(R.id.tv_pager);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_banner);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(new BannerAdapter(banners, new PicassoWrap(activity.getPicasso())));
        onPageSelected(0);
    }

    @Override
    public View onCreateView() {
        return super.onCreateView();
    }

    @Override
    public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
        int currentItem = mViewPager.getCurrentItem();
        DialogBanner banner = banners.get(currentItem);
        if (btn == RIGHT) VideoDetailsActivity.startActivity(context, banner);
        String string = defaultSharedPreferences.getString("banner_list", "[]");
        Gson gson = new Gson();
        List<String> banner_list = gson.fromJson(string, new TypeToken<List<String>>() {}.getType());
        if (!banner_list.contains(banner.getTitle())) {
            banner_list.add(banner.getTitle());
            defaultSharedPreferences.edit().putString("banner_list", gson.toJson(banner_list)).apply();
        }
        dismiss();
    }

    public static void showMainBanner(final BaseActivity context) {
        BmobQuery<DialogBanner> dialogBannerBmobQuery = new BmobQuery<>();
        dialogBannerBmobQuery.findObjects(context.getApplication(), new OnFindListener(context, PreferenceManager.getDefaultSharedPreferences(context)));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onPageSelected(int position) {
        if (mTextView != null && mViewPager != null) {
            mTextView.setText((position + 1) + " / " + banners.size());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private static class OnFindListener extends FindListener<DialogBanner> {
        private BaseActivity context;
        private SharedPreferences defaultSharedPreferences;

        private OnFindListener(BaseActivity context, SharedPreferences defaultSharedPreferences) {
            this.context = context;
            this.defaultSharedPreferences = defaultSharedPreferences;
        }

        @Override
        public void onSuccess(List<DialogBanner> list) {
            if (list == null || list.size() == 0) return;
            String date = DateUtil.getCurrentDate("yyyyMMdd");
            Integer integer = Integer.valueOf(date);
            String string = defaultSharedPreferences.getString("banner_list", "[]");
            List<String> banner_list = new Gson().fromJson(string, new TypeToken<List<String>>() {
            }.getType());
            List<DialogBanner> newList = new ArrayList<>();
            for (DialogBanner banner : list) {
                if (banner_list.contains(banner.getTitle())) continue;
                if (banner.getBannerInt() <= integer && banner.getBannerEnd() >= integer) {
                    newList.add(banner);
                }
            }
            MainBannerDialog dialog = new MainBannerDialog(context, newList);
            dialog.title("新片推荐~~");
            dialog.show();
        }

        @Override
        public void onError(int i, String s) {
        }
    }

    private static class BannerAdapter extends PagerAdapter {

        private List<DialogBanner> mList;
        private PicassoWrap mPicassoWrap;

        public BannerAdapter(List<DialogBanner> mList, PicassoWrap mPicassoWrap) {
            this.mList = mList;
            this.mPicassoWrap = mPicassoWrap;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            DialogBanner banner = mList.get(position);
            View inflate = LayoutInflater.from(container.getContext()).inflate(R.layout.item_dialog_banner, container, false);
            TextView textView = (TextView) inflate.findViewById(R.id.tv_banner_title);
            TextView infoView = (TextView) inflate.findViewById(R.id.tv_banner_info);
            ImageView imageView = (ImageView) inflate.findViewById(R.id.iv_banner_image);
            infoView.setText(banner.getIntroduce());
            textView.setText(banner.getTitle());
            textView.setTextSize(18);
            mPicassoWrap.loadHorizontal(banner.getCover(), imageView);
            container.addView(inflate);
            return inflate;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}

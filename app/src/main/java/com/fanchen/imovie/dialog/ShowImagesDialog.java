package com.fanchen.imovie.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.util.DisplayUtil;
import com.fanchen.imovie.view.ShowImagesViewPager;
import com.fanchen.imovie.view.photo.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 嵌套了viewpager的图片浏览Dialog
 *
 * Created by fanchen on 2017/5/3.
 */
public class ShowImagesDialog extends Dialog implements ViewPager.OnPageChangeListener {

    private View mView;
    private BaseActivity mActivity;
    private ShowImagesViewPager mViewPager;
    private TextView mIndexText;
    private List<? extends IPhotoImage> mImgUrls;
    private List<String> mTitles;
    private List<View> mViews;
    private ShowImagesAdapter mAdapter;
    private int position = 0;

    /**
     * @param context
     * @param imgUrls
     */
    public static void showDialog(@NonNull BaseActivity context, List<? extends IPhotoImage> imgUrls) {
        new ShowImagesDialog(context, imgUrls).show();
    }

    /**
     * @param context
     * @param imgUrls
     * @param position
     */
    public static void showDialog(@NonNull BaseActivity context, List<? extends IPhotoImage> imgUrls, int position) {
        new ShowImagesDialog(context, imgUrls, position).show();
    }

    /**
     * @param context
     * @param imgUrls
     */
    private ShowImagesDialog(@NonNull BaseActivity context, List<? extends IPhotoImage> imgUrls) {
        this(context, imgUrls, 0);
    }

    /**
     * @param context
     * @param imgUrls
     * @param pos
     */
    private ShowImagesDialog(@NonNull BaseActivity context, List<? extends IPhotoImage> imgUrls, int pos) {
        super(context, R.style.transparentDialog);
        this.mActivity = context;
        this.mImgUrls = imgUrls;
        this.position = pos;
        initView();
        initData();
    }

    private void initView() {
        mView = View.inflate(mActivity, R.layout.dialog_images_brower, null);
        mViewPager = (ShowImagesViewPager) mView.findViewById(R.id.vp_images);
        mIndexText = (TextView) mView.findViewById(R.id.tv_image_index);
        mTitles = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);
        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = 0;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        wl.height = displayMetrics.heightPixels - DisplayUtil.dip2px(getContext(), 100);
        wl.width = displayMetrics.widthPixels - DisplayUtil.dip2px(getContext(), 50);
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
    }

    private void initData() {
        LayoutInflater from = LayoutInflater.from(getContext());
        for (int i = 0; i < mImgUrls.size(); i++) {
            View inflate = from.inflate(R.layout.item_pager_photo, mViewPager, false);
            final PhotoView photoView = (PhotoView) inflate.findViewById(R.id.pv_photo);
            photoView.setFocusableInTouchMode(true);
            photoView.requestFocus();
            photoView.setTag(position);
            photoView.enable();
            View loadView = inflate.findViewById(R.id.lv_photo);
            if(mActivity != null && !TextUtils.isEmpty(mImgUrls.get(i).getCover())){
                Picasso picasso = mActivity.getPicasso();
                if(picasso != null)
                    picasso.load(mImgUrls.get(i).getCover())
                            .config(Bitmap.Config.RGB_565)
                            .error(R.drawable.image_load_h_error)
                            .into(photoView, new PicassoCallback(loadView));
            }
            mViews.add(inflate);
            mTitles.add(i + "");
        }
        mAdapter = new ShowImagesAdapter(mViews, mTitles);
        mViewPager.setAdapter(mAdapter);
        mIndexText.setText(String.format("%d/%d",(position + 1) , mImgUrls.size()));
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mIndexText.setText((position + 1) + "/" + mImgUrls.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class ShowImagesAdapter extends PagerAdapter {

        private List<View> views;
        private List<String> titles;

        public ShowImagesAdapter(List<View> views, List<String> titles) {
            this.views = views;
            this.titles = titles;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles == null ? "" : titles.get(position);
        }
    }

    private static class PicassoCallback implements com.squareup.picasso.Callback {

        private View mLoadView;

        public PicassoCallback(View mLoadView) {
            this.mLoadView = mLoadView;
        }

        @Override
        public void onSuccess() {
            mLoadView.setVisibility(View.GONE);
        }

        @Override
        public void onError() {
            mLoadView.setVisibility(View.GONE);
        }
    }

    ;

    /**
     *
     */
    public interface IPhotoImage {
        /**
         * @return
         */
        String getCover();

    }
}

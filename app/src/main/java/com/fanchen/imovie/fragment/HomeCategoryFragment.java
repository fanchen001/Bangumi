package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.activity.VideoTabActivity;
import com.fanchen.imovie.adapter.CategoryAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.VideoCategory;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.util.DialogUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fanchen on 2017/11/9.
 */
public class HomeCategoryFragment extends BaseRecyclerFragment {

    private CategoryAdapter mCategoryAdapter;

    public static Fragment newInstance() {
        return new HomeCategoryFragment();
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mCategoryAdapter = new CategoryAdapter(activity, picasso);
    }

    @Override
    public void loadData(Bundle savedInstanceState, RetrofitManager retrofit, int page) {
        mSwipeRefreshLayout.setRefreshing(true);
        new Thread(new CheckRunnable()).start();
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof VideoCategory)) return;
        VideoCategory category = (VideoCategory) datas.get(position);
        if (category.isSuccess()) {
            VideoTabActivity.startActivity(activity, category.getTitle(), category.getType());
        } else {
            CategoryButtonListener buttonListener = new CategoryButtonListener(category);
            DialogUtil.showMaterialDialog(activity, "当前分区服务器不太稳定，可能出现连接失败的问题，是否继续使用?", buttonListener);
        }
    }

    private class CheckRunnable implements Runnable {

        @Override
        public void run() {
            do {
                SystemClock.sleep(500);
            } while (!IMovieAppliction.FLAGS[0] || !IMovieAppliction.FLAGS[1]);
            View view = getView();
            if (view != null) view.post(new PostRunnable());
        }

    }

    private class PostRunnable implements Runnable {

        @Override
        public void run() {
            mSwipeRefreshLayout.setRefreshing(false);
            mCategoryAdapter.setList(IMovieAppliction.mCategorys);
        }

    }

    private class CategoryButtonListener implements OnButtonClickListener {

        private VideoCategory category;

        public CategoryButtonListener(VideoCategory category) {
            this.category = category;
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (btn == OnButtonClickListener.RIGHT) {
                VideoTabActivity.startActivity(activity, category.getTitle(), category.getType());
            }
        }

    }

}

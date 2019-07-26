package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.CollectTabActivity;
import com.fanchen.imovie.activity.VideoDetailsActivity;
import com.fanchen.imovie.adapter.CollectAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.entity.bmob.VideoCollect;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.view.CustomEmptyView;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.DeleteListener;

/**
 * 视频收藏
 * Created by fanchen on 2017/7/24.
 */
public class CollectFragment extends BaseRecyclerFragment implements
        BaseAdapter.OnItemLongClickListener, CollectTabActivity.OnClearListener {

    public static final String COLLECT_TYPE = "type";

    private CollectAdapter mVideoAdapter;
    private int collectType;

    public static Fragment newInstance(int collectType) {
        Fragment fragment = new CollectFragment();
        Bundle args = new Bundle();
        args.putInt(COLLECT_TYPE, collectType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean hasRefresh() {
        return false;
    }


    @Override
    protected boolean hasLoad() {
        return false;
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        collectType = getArguments().getInt(COLLECT_TYPE);
        super.initFragment(savedInstanceState, args);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return collectType == VideoCollect.TYPE_LIVE ? new BaseAdapter.LinearLayoutManagerWrapper(activity) : new BaseAdapter.GridLayoutManagerWrapper(activity, 3);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mVideoAdapter = new CollectAdapter(activity, picasso, collectType);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mVideoAdapter.setOnItemLongClickListener(this);
    }

    @Override
    public void loadData(Bundle savedInstanceState, RetrofitManager retrofit, int page) {
        AsyTaskQueue.newInstance().execute(callback);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof VideoCollect)) return;
        VideoCollect collect = (VideoCollect) datas.get(position);
        if (collect.getType() == VideoCollect.TYPE_VIDEO) { //视频
            VideoDetailsActivity.startActivity(activity, collect);
        }
    }

    @Override
    public boolean onItemLongClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof VideoCollect)) return false;
        DialogUtil.showMaterialDeleteDialog(this.activity,this, new DeleteListenerImpl((List<VideoCollect>) datas, position), datas, position);
        return true;
    }

    @Override
    public void onClear() {
        AsyTaskQueue.newInstance().execute(new DeleteListenerImpl());
    }

    /**
     *
     */
    private TaskRecyclerFragmentImpl<List<VideoCollect>> callback = new TaskRecyclerFragmentImpl<List<VideoCollect>>() {

        @Override
        public List<VideoCollect> onTaskBackground() {
            if (getLiteOrm() == null) return null;
            return getLiteOrm().query(new QueryBuilder<>(VideoCollect.class).where("type = ?", collectType));
        }

        @Override
        public void onTaskSuccess(List<VideoCollect> data) {
            if (data == null) return;
            mVideoAdapter.setList(data);
        }

    };

    /**
     *
     */
    private class DeleteListenerImpl extends AsyTaskListenerImpl<Integer> {
        public int DELETEALL = -1;
        public int DELETEERROR = -2;

        private int pisotion = -1;
        private List<VideoCollect> collects;

        public DeleteListenerImpl(List<VideoCollect> collects, int pisotion) {
            this.collects = collects;
            this.pisotion = pisotion;
        }

        public DeleteListenerImpl() {
        }

        @Override
        public Integer onTaskBackground() {
            if (getLiteOrm() == null) return DELETEERROR;
            if ((collects == null || collects.size() == 0) && pisotion == -1) {
                //删除全部
                getLiteOrm().delete(VideoCollect.class);
                return DELETEALL;
            } else if (collects != null && collects.size() > pisotion) {
                //按条件删除
                getLiteOrm().delete(new WhereBuilder(VideoCollect.class, "id = ? ", new Object[]{collects.get(pisotion).getId()}));
                return pisotion;
            }
            return DELETEERROR;
        }

        @Override
        public void onTaskSuccess(Integer data) {
            if (collects == null || mVideoAdapter == null || collects.size() <= pisotion || pisotion < 0)
                return;
            if (data == DELETEERROR) {
                showSnackbar(getStringFix(R.string.delete_error));
            } else {
                if (data == DELETEALL) {
                    if (IMovieAppliction.app != null) {
                        List<BmobObject> bmobs = new ArrayList<>();
                        for (VideoCollect collect : collects) {
                            bmobs.add(collect);
                        }
                        new BmobObject().deleteBatch(IMovieAppliction.app, bmobs, deleteListener);
                    }
                    mVideoAdapter.clear();
                } else {
                    if (collects.size() > pisotion && IMovieAppliction.app != null) {
                        collects.get(pisotion).delete(IMovieAppliction.app, deleteListener);
                    }
                    mVideoAdapter.remove(data);
                }
                if (mVideoAdapter.getList().size() == 0) {
                    mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_EMPTY);
                }
            }
        }
    }

    private DeleteListener deleteListener = new DeleteListener() {

        @Override
        public void onSuccess() {
            if (isDetached() || !isAdded()) return;
            showSnackbar(getStringFix(R.string.delete_asy_success));
        }

        @Override
        public void onFailure(int i, String s) {
            if (isDetached() || !isAdded()) return;
            showSnackbar(getStringFix(R.string.delete_asy_error));
        }

    };
}

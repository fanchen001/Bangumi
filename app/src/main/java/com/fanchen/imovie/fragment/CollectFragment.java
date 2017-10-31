package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.CollectTabActivity;
import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.fanchen.imovie.activity.VideoDetailsActivity;
import com.fanchen.imovie.adapter.CollectAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.db.LiteOrmManager;
import com.fanchen.imovie.entity.bmob.VideoCollect;
import com.fanchen.imovie.entity.dytt.DyttLiveBody;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.view.CustomEmptyView;
import com.google.gson.Gson;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 视频收藏
 * Created by fanchen on 2017/7/24.
 */
public class CollectFragment extends BaseRecyclerFragment implements
        BaseAdapter.OnItemLongClickListener,CollectTabActivity.OnClearListener {

    private static final String[] TITLES = new String[]{"删除记录", "直接打开"};
    public static final String COLLECT_TYPE = "type";

    private CollectAdapter mVideoAdapter;
    private int collectType;
    private LiteOrm liteOrm;

    public static Fragment newInstance(int collectType) {
        Fragment fragment = new CollectFragment();
        Bundle args = new Bundle();
        args.putInt(COLLECT_TYPE,collectType);
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
        liteOrm = LiteOrmManager.getInstance(activity).getLiteOrm("imovie.db");
        super.initFragment(savedInstanceState, args);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return collectType == VideoCollect.TYPE_LIVE ? new LinearLayoutManager(activity) : new GridLayoutManager(activity,3);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mVideoAdapter = new CollectAdapter(activity, picasso,collectType);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mVideoAdapter.setOnItemLongClickListener(this);
    }

    @Override
    public void loadData(Bundle savedInstanceState,RetrofitManager retrofit,int page) {
        AsyTaskQueue.newInstance().execute(callback);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (datas == null || datas.size() <= position || position < 0 || !(datas.get(position) instanceof VideoCollect)) return;
        VideoCollect collect = (VideoCollect) datas.get(position);
        if(collect.getType() == VideoCollect.TYPE_LIVE){
            //电视直播
            try{
                DyttLiveBody liveBody = new Gson().fromJson(collect.getExtend(), DyttLiveBody.class);
                VideoPlayerActivity.startActivity(activity, liveBody);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(collect.getType() == VideoCollect.TYPE_VIDEO){
            //视频
            VideoDetailsActivity.startActivity(activity,collect);
        }
    }

    @Override
    public boolean onItemLongClick(List<?> datas, View v, int position) {
        if (datas == null || datas.size() <= position || position < 0  || !(datas.get(position) instanceof VideoCollect)) return false;
        return true;
    }

    @Override
    public void onClear() {
        AsyTaskQueue.newInstance().execute(new DeleteListenerImpl());
    }

    /**
     *
     */
    private class ItemClickListener implements AdapterView.OnItemClickListener {

        private int position;
        private VideoCollect item;

        public ItemClickListener(VideoCollect item, int position) {
            this.position = position;
            this.item = item;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    AsyTaskQueue.newInstance().execute(new DeleteListenerImpl(item.getId(), this.position));
                    break;
                case 1:
                    CollectFragment.this.onItemClick(mVideoAdapter.getList(), view, this.position);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     *
     */
    private TaskRecyclerFragmentImpl<List<VideoCollect>> callback = new TaskRecyclerFragmentImpl<List<VideoCollect>>() {

        @Override
        public List<VideoCollect> onTaskBackground() {
            if (isDetached() && !isAdded()) return null;
            return liteOrm != null ? liteOrm.query(new QueryBuilder<>(VideoCollect.class).where("type = ?", collectType)) : null;
        }

        @Override
        public void onTaskSuccess(List<VideoCollect> data) {
            if (isDetached() && !isAdded() || data == null) return;
            mVideoAdapter.clear();
            mVideoAdapter.addAll(data);
        }

    };

    /**
     *
     */
    private class DeleteListenerImpl extends AsyTaskListenerImpl<Integer> {
        public int DELETEALL = -1;
        public int DELETEERROR = -2;

        private int pisotion = -1;
        private String id;

        public DeleteListenerImpl(String id, int pisotion) {
            this.id = id;
            this.pisotion = pisotion;
        }

        public DeleteListenerImpl() {
        }

        @Override
        public Integer onTaskBackground() {
            if (isDetached() || !isAdded() || liteOrm == null) return DELETEERROR;
            if (TextUtils.isEmpty(id) && pisotion == -1) {
                //删除全部
                liteOrm.delete(VideoCollect.class);
                return DELETEALL;
            } else {
                //按条件删除
                liteOrm.delete(new WhereBuilder(VideoCollect.class, "id = ? ", new Object[]{id}));
                return pisotion;
            }
        }

        @Override
        public void onTaskSuccess(Integer data) {
            if (isDetached() || !isAdded()) return;
            if (data == DELETEERROR) {
                showSnackbar(getString(R.string.delete_error));
            } else {
                if (data == DELETEALL) {
                    mVideoAdapter.clear();
                } else {
                    mVideoAdapter.remove(data);
                }
                if(mVideoAdapter.getList().size() == 0){
                    mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_EMPTY);
                }
            }
        }
    }

}

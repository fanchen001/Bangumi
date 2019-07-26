package com.fanchen.imovie.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseAdapter
 * Created by fanchen on 2017/7/12.
 */
public abstract class BaseAdapter extends RecyclerView.Adapter {
    protected LayoutInflater mLayoutInflater;
    protected Context context;

    protected boolean isLoad = true;
    private boolean isLoading = false;
    private OnLoadListener onLoadListener;
    private int loadMinSize = 6;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<IViewType> mList = new ArrayList<>();
    private BaseAdapter.OnItemClickListener itemClickListener;
    private BaseAdapter.OnItemLongClickListener itemLongClickListener;

    public BaseAdapter(Context context) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public BaseAdapter(Context context, List<IViewType> mList) {
        this.mList = mList;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * 是否含有HeaderView
     *
     * @return
     */
    public boolean hasHeaderView() {
        return false;
    }

    /**
     * @return
     */
    public boolean hasCategoryView() {
        return false;
    }

    public boolean hasFooterView() {
        return false;
    }

    /**
     * 创建一个ViewHolder
     *
     * @param v
     * @param viewType
     * @return
     */
    public abstract RecyclerView.ViewHolder createViewHolder(View v, int viewType);

    /**
     * 将数据绑定到ViewHolder
     *
     * @param holder
     * @param datas
     * @param position
     */
    public abstract void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position);

    /**
     * item对应的布局文件
     *
     * @param viewType
     * @return
     */
    public abstract int getLayout(int viewType);

    public List<?> getList() {
        return mList;
    }

    public void setList(final List<? extends IViewType> mList) {
        setList(mList,true);
    }

    public void setList(final List<? extends IViewType> mList,final boolean refresh) {
        if (Thread.currentThread().getName().equals("main")) {
            if(refresh ||  this.mList == null){
                this.mList = new ArrayList<>(mList);
            }else{
                this.mList.addAll(mList);
            }
            notifyDataSetChanged();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(refresh ||  BaseAdapter.this.mList == null){
                        BaseAdapter.this.mList = new ArrayList<>(mList);
                    }else{
                        BaseAdapter.this.mList.addAll(mList);
                    }
                    notifyDataSetChanged();
                }
            });
        }

    }



    @Override
    public int getItemViewType(int position) {
        if (hasHeaderView() && hasCategoryView()) {
            if (position == 0) {
                return IViewType.TYPE_HEADER;
            } else if (position == 1) {
                return IViewType.TYPE_CATEGORY;
            } else {
                if (hasFooterView() && isLoad && position == getItemCount() - 1) {
                    return IViewType.TYPE_FOOTER;
                }
                if (mList.size() > position - 2) {
                    return mList.get(position - 2).getViewType();
                } else {
                    return IViewType.TYPE_FOOTER;
                }
            }
        } else if (hasHeaderView()) { //有头布局
            if (position == 0) {
                return IViewType.TYPE_HEADER;
            } else {
                if (hasFooterView() && isLoad && position == getItemCount() - 1) {
                    return IViewType.TYPE_FOOTER;
                }
                if (mList.size() > position - 1) {
                    return mList.get(position - 1).getViewType();
                } else {
                    return IViewType.TYPE_FOOTER;
                }
            }
        } else if (hasCategoryView()) { //有头布局
            if (position == 0) {
                return IViewType.TYPE_CATEGORY;
            } else {
                if (hasFooterView() && isLoad && position == getItemCount() - 1) {
                    return IViewType.TYPE_FOOTER;
                }
                if (mList.size() > position - 1) {
                    return mList.get(position - 1).getViewType();
                } else {
                    return IViewType.TYPE_FOOTER;
                }
            }
        } else if (hasFooterView() && isLoad && position == getItemCount() - 1) {
            //尾部局
            return IViewType.TYPE_FOOTER;
        } else {
            //没有带头布局
            if (mList == null || mList.size() <= position) {
                return IViewType.TYPE_HEADER;
            }
            if (mList.size() > position) {
                return mList.get(position).getViewType();
            } else {
                return IViewType.TYPE_FOOTER;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = getLayout(viewType);
        View inflate = mLayoutInflater.inflate(layout, parent, false);
        return createViewHolder(inflate, viewType);
    }

    /**
     * @param viewType
     */
    public void add(final IViewType viewType) {
        if (mList == null)
            mList = new ArrayList<>();
        final int size = mList.size() == 0 ? 0 : mList.size() - 1;
        if (Thread.currentThread().getName().equals("main")) {
            mList.add(viewType);
            notifyItemRangeChanged(size, mList.size());
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mList.add(viewType);
                    notifyItemRangeChanged(size, mList.size());
                }
            });
        }
    }

    /**
     * @param all
     */
    public void addAll(final List<? extends IViewType> all, final boolean load) {
        if (all == null) return;
        if (mList == null)
            mList = new ArrayList<>();
        final int size = mList.size() == 0 ? 0 : mList.size() - 1;
        if (Thread.currentThread().getName().equals("main")) {
            mList.addAll(all);
            if (load) {
                notifyItemRangeChanged(size, mList.size());
            }
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mList.addAll(all);
                    if (load) {
                        notifyItemRangeChanged(size, mList.size());
                    }
                }
            });
        }
    }

    public void addAll(List<? extends IViewType> all) {
        addAll(all, true);
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        if (hasHeaderView() && hasFooterView() && getItemCount() <= 2) {
            return true;
        } else if (hasHeaderView() && hasCategoryView() && getItemCount() <= 2) {
            return true;
        } else if (hasFooterView() && hasCategoryView() && getItemCount() <= 2) {
            return true;
        } else if ((hasHeaderView() || hasFooterView() || hasCategoryView()) && getItemCount() <= 1) {
            return true;
        } else if (getItemCount() == 0) {
            return true;
        }
        return false;
    }

    public void clear() {
        if (mList == null)
            return;
        if (Thread.currentThread().getName().equals("main")) {
            mList.clear();
            notifyDataSetChanged();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mList.clear();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void remove(final int position) {
        if (mList == null)
            return;
        if (Thread.currentThread().getName().equals("main")) {
            mList.remove(position);
            notifyDataSetChanged();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mList.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void setLoad(final boolean isLoad) {
        if (Thread.currentThread().getName().equals("main")) {
            this.isLoad = isLoad;
            notifyDataSetChanged();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    BaseAdapter.this.isLoad = isLoad;
                    notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * @param itemClickListener
     */
    public void setOnItemClickListener(BaseAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * @param itemLongClickListener
     */
    public void setOnItemLongClickListener(BaseAdapter.OnItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
        isLoad = true;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (hasHeaderView() && hasCategoryView()) {
            position -= 2;
        } else if (hasHeaderView() || hasCategoryView()) {
            position -= 1;
        }
        final int itemViewPosition = position;
        if (itemViewType != IViewType.TYPE_FOOTER && itemViewType != IViewType.TYPE_HEADER) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && mList != null && mList.size() > itemViewPosition && itemViewPosition >= 0) {
                        itemClickListener.onItemClick(mList, v, itemViewPosition);
                    }
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemLongClickListener != null && mList != null && mList.size() > itemViewPosition && itemViewPosition >= 0) {
                        itemLongClickListener.onItemLongClick(mList, v, itemViewPosition);
                    }
                    return false;
                }
            });
        } else if (itemViewType == IViewType.TYPE_FOOTER) {
            if (onLoadListener != null && isLoad && !isLoading && mList != null && mList.size() >= loadMinSize) {
                isLoading = true;
                onLoadListener.onLoad();
            } else if (onLoadListener != null && mList != null && mList.size() > 0) {
                isLoad = false;
                holder.itemView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }

                }, 2000);
            }
        }
        if (itemViewType == IViewType.TYPE_FOOTER && (mList == null || mList.size() == 0)) {
            holder.itemView.setVisibility(View.GONE);
        } else if (itemViewType == IViewType.TYPE_FOOTER && mList != null && mList.size() > 0) {
            holder.itemView.setVisibility(View.VISIBLE);
        }
        if (mList == null || mList.size() <= position) {
            return;
        }
        bindViewHolder(holder, mList, itemViewType, position);
    }

    @Override
    public int getItemCount() {
        if (hasHeaderView() && hasCategoryView() && (hasFooterView() && isLoad)) {
            return mList == null || mList.size() == 0 ? 0 : mList.size() + 3;
        }
        if (hasHeaderView() && (hasFooterView() && isLoad)) {
            return mList == null || mList.size() == 0 ? 0 : mList.size() + 2;
        }
        if (hasCategoryView() && (hasFooterView() && isLoad)) {
            return mList == null || mList.size() == 0 ? 0 : mList.size() + 2;
        }
        if (hasHeaderView() && hasCategoryView()) {
            return mList == null || mList.size() == 0 ? 0 : mList.size() + 2;
        }
        if (hasHeaderView() || hasCategoryView() || (hasFooterView() && isLoad)) {
            return mList == null ? 1 : mList.size() + 1;
        }
        return mList == null ? 0 : mList.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridSpanSizeLookup(gridManager, this));
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (hasHeaderView()) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(holder.getLayoutPosition() == 0 || holder.getLayoutPosition() == getItemCount() - 1);
            }
        }
    }

    public void setLoadMinSize(int loadMinSize) {
        this.loadMinSize = loadMinSize;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTextView;
        public TextView lastTextView;
        public TextView timeTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_img);
            titleTextView = (TextView) itemView.findViewById(R.id.item_title);
            lastTextView = (TextView) itemView.findViewById(R.id.item_play);
            timeTextView = (TextView) itemView.findViewById(R.id.item_time);
        }
    }

    public static class TitleViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;
        public View view;

        public TitleViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_bangumi_ic);
            textView = (TextView) itemView.findViewById(R.id.item_title_bangumi);
            view = itemView.findViewById(R.id.item_title_more);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class GridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        private GridLayoutManager gridManager;
        private RecyclerView.Adapter adapter;

        public GridSpanSizeLookup(GridLayoutManager gridManager, RecyclerView.Adapter adapter) {
            this.gridManager = gridManager;
            this.adapter = adapter;
        }

        @Override
        public int getSpanSize(int position) {
            int size = adapter.getItemViewType(position) == IViewType.TYPE_HEADER ||
                    adapter.getItemViewType(position) == IViewType.TYPE_TITLE ||
                    adapter.getItemViewType(position) == IViewType.TYPE_CATEGORY ||
                    adapter.getItemViewType(position) == IViewType.TYPE_FOOTER ? gridManager.getSpanCount() : 1;
            return size;
        }
    }

    public static class LinearLayoutManagerWrapper extends LinearLayoutManager {
        public LinearLayoutManagerWrapper(Context context) {
            super(context);
        }

        public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public LinearLayoutManagerWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                return super.scrollVerticallyBy(dy, recycler, state);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                return super.scrollHorizontallyBy(dx, recycler, state);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public static class GridLayoutManagerWrapper extends GridLayoutManager {

        public GridLayoutManagerWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public GridLayoutManagerWrapper(Context context, int spanCount) {
            super(context, spanCount);
        }

        public GridLayoutManagerWrapper(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                return super.scrollVerticallyBy(dy, recycler, state);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                return super.scrollHorizontallyBy(dx, recycler, state);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    /**
     * Override 这个方法
     */
    public void addData(Object data,boolean refresh) {
        if (data instanceof List) {
            addAll((List<IViewType>) data);
        }
    }

    /**
     *
     */
    public interface OnItemClickListener {
        void onItemClick(List<?> datas, View v, int position);
    }

    /**
     *
     */
    public interface OnItemLongClickListener {
        boolean onItemLongClick(List<?> datas, View v, int position);
    }

    /**
     *
     */
    public interface OnLoadListener {

        void onLoad();

    }
}

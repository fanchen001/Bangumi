package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.ISearchWord;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.List;

/**
 * 搜索历史Adapter
 * <br/>SearchDialogFragment
 * <br/>Created by fanchen on 2017/9/17.
 */
public class SearchHistoryAdapter extends BaseAdapter {

    private OnItemDeleteListener onItemDeleteListener;

    public SearchHistoryAdapter(Context context) {
        super(context);
    }

    public SearchHistoryAdapter(Context context, List<IViewType> mList) {
        super(context, mList);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new HistoryViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, final List<IViewType> datas, int viewType, int position) {
        if (datas == null || datas.size() <= position) return;
        ISearchWord searchWord = (ISearchWord) datas.get(position);
        HistoryViewHolder viewHolder = (HistoryViewHolder) holder;
        viewHolder.historyInfo.setText(searchWord.getWord());
        if (onItemDeleteListener == null) return;
        viewHolder.delete.setOnClickListener(new DeleteClickListener(onItemDeleteListener, datas, position));
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_search_history;
    }

    /**
     *
     */
    public static class DeleteClickListener implements View.OnClickListener {
        private List<IViewType> datas;
        private int position;
        private OnItemDeleteListener listener;

        public DeleteClickListener(OnItemDeleteListener listener, List<IViewType> datas, int position) {
            this.datas = datas;
            this.listener = listener;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            listener.OnItemDelete(datas, position);
        }

    }

    /**
     *
     */
    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        public TextView historyInfo;
        public ImageView delete;

        public HistoryViewHolder(View view) {
            super(view);
            historyInfo = (TextView) view.findViewById(R.id.tv_item_search_history);
            delete = (ImageView) view.findViewById(R.id.iv_item_search_delete);
        }
    }

    /**
     *
     */
    public interface OnItemDeleteListener {
        /**
         * @param datas
         * @param position
         */
        void OnItemDelete(List<?> datas, int position);

    }

}
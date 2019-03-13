package com.fanchen.imovie.adapter;

import android.text.TextUtils;
import android.view.View;

import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadTask;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseDownloadAdapter;
import com.fanchen.imovie.entity.DownloadEntityWrap;

import java.util.ArrayList;
import java.util.List;

/**
 * DownloadAdapter
 */
public class DownloadAdapter extends BaseDownloadAdapter<DownloadEntityWrap> implements View.OnClickListener {

    public DownloadAdapter(BaseActivity context) {
        super(context);
    }

    public void update(DownloadTask task) {
        if (task == null || getList() == null) return;
        for (DownloadEntityWrap e : (List<DownloadEntityWrap>) getList()) {
            if (task.getDownloadUrl().equals(e.data.getUrl())) {
                e.data.setPercent(task.getPercent());
                e.data.setState(task.getState());
                e.data.setConvertSpeed(task.getConvertSpeed());
                notifyItemChanged(e.position);
            }
        }
    }

    public void addAll(List<DownloadEntity> all, String suffix) {
        if (all == null || TextUtils.isEmpty(suffix)) return;
        List<DownloadEntityWrap> newList = new ArrayList<>();
        String[] split = suffix.split("/");
        for (String s : split) {
            for (DownloadEntity e : all) {
                if (e.getUrl().contains(s)) {
                    newList.add(new DownloadEntityWrap(e, newList.size()));
                } else if (e.getFileName().contains(s)) {
                    newList.add(new DownloadEntityWrap(e, newList.size()));
                }
            }
        }
        super.addAll(newList);
    }

    /**
     * @param entity
     */
    public void remove(DownloadEntity entity) {
        if (entity == null) return;
        int position = -1;
        for (DownloadEntityWrap e : (List<DownloadEntityWrap>) getList()) {
            position++;
            if (entity.getUrl().equals(e.data.getUrl())) {
                break;
            }
        }
        if (position != -1) {
            remove(position);
        }
    }

}
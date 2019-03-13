package com.fanchen.imovie.adapter;

import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseDownloadAdapter;
import com.fanchen.imovie.entity.M3u8Warp;
import com.fanchen.m3u8.bean.M3u8;
import com.fanchen.m3u8.bean.M3u8File;
import com.fanchen.m3u8.bean.M3u8State;

import java.util.ArrayList;
import java.util.List;

/**
 * M3u8Adapter
 */
public class M3u8Adapter extends BaseDownloadAdapter<M3u8Warp> {

    public M3u8Adapter(BaseActivity context) {
        super(context);
    }

    public void setM3u8Files(List<M3u8File> m3u8Files) {
        if (getList() == null) return;
        List<M3u8Warp> warps = new ArrayList<>();
        for (M3u8File file : m3u8Files) {
            warps.add(new M3u8Warp(file));
        }
        super.addAll(warps);
    }

    public void updataItem(List<M3u8File> m3u8Files, int state, String msg) {
        for (M3u8File file : m3u8Files) {
            updataItem(m3u8ToM3u8Warp(file), state, msg);
        }
    }

    public void updataItem(M3u8File m3u8File, int state, String msg) {
        M3u8Warp warp = m3u8ToM3u8Warp(m3u8File);
        updataItem(warp, state, msg);
    }

    public void updataItem(M3u8File m3u8File, String msg) {
        updataItem(m3u8File, -2, msg);
    }

    public void updataItem(M3u8 m3u8, String msg) {
        updataItem(m3u8, -2, msg);
    }

    public void updataItem(M3u8Warp warp, int state, String msg) {
        if (warp == null || warp.holder == null) return;
        warp.data.setState(state);
        warp.msg = msg;
        setBtnState(warp.holder,warp);
        warp.msg = "";
    }

    public void updataItem(M3u8 m3u8, int state, String msg) {
        updataItem(m3u8ToM3u8Warp(m3u8), state, msg);
    }

    public void updataItem(M3u8 m3u8, int curr, int totel) {
        M3u8Warp warp = m3u8ToM3u8Warp(m3u8);
        if (warp == null || warp.holder == null) return;
        warp.data.setState(M3u8State.INSTANCE.getSTETE_DOWNLOAD());
        warp.progress = curr;
        warp.max = totel;
        setBtnState(warp.holder,warp);
    }

    private M3u8Warp m3u8ToM3u8Warp(M3u8 m3u8) {
        List<M3u8Warp> m3u8Files = (List<M3u8Warp>) getList();
        for (M3u8Warp warp : m3u8Files) {
            if (warp.holder == null) return null;
            M3u8Warp swarp = (M3u8Warp) warp.holder.downloadName.getTag();
            if (warp.data.getUrl().equals(m3u8.getParentUrl()) && swarp == warp) {
                return warp;
            }
        }
        return null;
    }

    private M3u8Warp m3u8ToM3u8Warp(M3u8File m3u8) {
        List<M3u8Warp> m3u8Files = (List<M3u8Warp>) getList();
        for (M3u8Warp warp : m3u8Files) {
            if (warp.holder == null) return null;
            M3u8Warp swarp = (M3u8Warp) warp.holder.downloadName.getTag();
            if (warp.data.getUrl().equals(m3u8.getUrl()) && swarp == warp) {
                return warp;
            }
        }
        return null;
    }

    public void remove(M3u8File m3u8File) {
        List<M3u8Warp> list = (List<M3u8Warp>) getList();
        int position = -1;
        for (int i = 0; i < list.size(); i++) {
            if (m3u8File.getUrl().equals(list.get(i).data.getUrl())) {
                position = i;
            }
        }
        if (position != -1) {
            remove(position);
        }
    }

}

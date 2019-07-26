package com.fanchen.imovie.adapter;


import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseDownloadAdapter;
import com.fanchen.imovie.entity.XiguaDownload;
import com.fanchen.imovie.entity.face.IViewType;
import com.xigua.p2p.TaskVideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * XiguaAdapter
 * Created by fanchen on 2018/9/21.
 */
public class XiguaAdapter extends BaseDownloadAdapter<XiguaDownload> {

    public XiguaAdapter(BaseActivity context) {
        super(context);
    }

    /**
     * @param all
     */
    public void setTaskVideoInfos(List<TaskVideoInfo> all) {
        List<IViewType> iViewTypes = new ArrayList<>();
        for (TaskVideoInfo info : all) {
            iViewTypes.add(new XiguaDownload(info));
        }
        super.setList(iViewTypes);
    }

}

package com.fanchen.imovie.entity.dytt;

import com.fanchen.imovie.view.video_new.Clarity;

import java.util.ArrayList;
import java.util.List;

/**
 * DyttLiveBody
 * Created by fanchen on 2018/9/27.
 */
public class DyttLiveBody {
    private List<DyttLiveUrls> urls;

    public List<DyttLiveUrls> getUrls() {
        return urls;
    }

    public void setUrls(List<DyttLiveUrls> urls) {
        this.urls = urls;
    }

    public boolean isSuccess() {
        return urls!= null && !urls.isEmpty();
    }

    public List<Clarity> getClaritys() {
        List<Clarity> clarities = new ArrayList<>();
        for (DyttLiveUrls url : getUrls()) {
            clarities.add(new Clarity(url.getHd(), "", "", url));
        }
        return clarities;
    }
}

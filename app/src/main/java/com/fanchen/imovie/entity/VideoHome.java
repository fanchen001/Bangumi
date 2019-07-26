package com.fanchen.imovie.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IBangumiRoot;
import com.fanchen.imovie.entity.face.IBangumiTitle;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoBanner;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.view.pager.IBanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanchen on 2017/9/18.
 */
public class VideoHome implements IBangumiRoot, IBangumiMoreRoot, Parcelable {

    private List<VideoBanner> banner;
    private List<VideoTitle> result;
    private boolean success;
    private String message;
    private List<Video> list;

    public VideoHome() {
    }

    protected VideoHome(Parcel in) {
        banner = in.createTypedArrayList(VideoBanner.CREATOR);
        result = in.createTypedArrayList(VideoTitle.CREATOR);
        success = in.readByte() != 0;
        message = in.readString();
    }

    public static final Creator<VideoHome> CREATOR = new Creator<VideoHome>() {
        @Override
        public VideoHome createFromParcel(Parcel in) {
            return new VideoHome(in);
        }

        @Override
        public VideoHome[] newArray(int size) {
            return new VideoHome[size];
        }
    };

    @Override
    public List<? extends IVideoBanner<? extends IBanner>> getHomeBanner() {
        return banner;
    }

    @Override
    public List<? extends IBangumiTitle> getResult() {
        return result;
    }

    @Override
    public List<IViewType> getAdapterResult() {
        List<IViewType> viewTypes = new ArrayList<>();
        if (result != null)
            for (IBangumiTitle homeResult : result) {
                viewTypes.add(homeResult);
                viewTypes.addAll(homeResult.getList());
            }
        if (list != null) {
            viewTypes.addAll(list);
        }
        return viewTypes;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setHomeBanner(List<VideoBanner> banner) {
        this.banner = banner;
    }

    public void setHomeResult(List<VideoTitle> result) {
        this.result = result;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(banner);
        dest.writeTypedList(result);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
    }

    @Override
    public List<? extends IVideo> getList() {
        if ((list == null || list.size() == 0) && (result != null && result.size() > 0)) {
            list = new ArrayList<>();
            try {
                for (VideoTitle title : result) {
                    for (IVideo video : title.getList()){
                        this.list.add((Video)video);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public void setList(List<IVideo> list) {
        try {
            if(this.list == null)
                this.list = new ArrayList<>();
            for (IVideo video : list){
                this.list.add((Video)video);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}

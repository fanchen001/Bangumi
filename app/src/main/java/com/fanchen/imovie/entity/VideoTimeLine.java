package com.fanchen.imovie.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IBangumiTimeRoot;
import com.fanchen.imovie.entity.face.IBangumiTimeTitle;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanchen on 2017/9/20.
 */
public class VideoTimeLine implements IBangumiTimeRoot, Parcelable {
    private List<IViewType> viewTypes = new ArrayList<>();
    private List<VideoTimeLineTitle> list;
    private boolean success;
    private String message;

    public VideoTimeLine() {

    }

    protected VideoTimeLine(Parcel in) {
        list = in.createTypedArrayList(VideoTimeLineTitle.CREATOR);
        success = in.readByte() != 0;
        message = in.readString();
    }

    public static final Creator<VideoTimeLine> CREATOR = new Creator<VideoTimeLine>() {
        @Override
        public VideoTimeLine createFromParcel(Parcel in) {
            return new VideoTimeLine(in);
        }

        @Override
        public VideoTimeLine[] newArray(int size) {
            return new VideoTimeLine[size];
        }
    };

    @Override
    public List<? extends IBangumiTimeTitle> getList() {
        return list;
    }

    @Override
    public List<? extends IViewType> getAdapterList() {
        viewTypes.clear();
        for (VideoTimeLineTitle title : list) {
            viewTypes.add(title);
            viewTypes.addAll(title.getList());
        }
        return viewTypes;
    }

    @Override
    public int getPosition() {
        int i = 0;
        for (IViewType viewType : viewTypes) {
            if (viewType instanceof IBangumiTimeTitle && ((IBangumiTimeTitle) viewType).isNow()) {
                return i;
            }
            i++;
        }
        return 0;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setList(List<VideoTimeLineTitle> list) {
        this.list = list;
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
        dest.writeTypedList(list);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
    }
}

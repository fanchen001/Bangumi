package com.fanchen.imovie.entity.ikanfan;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.List;

/**
 * Created by fanchen on 2017/9/24.
 */
public class IKanFanHome implements IHomeRoot, IBangumiMoreRoot, Parcelable {

    private boolean success;
    private String message;
    private List<IKanFanVideo> result;

    public IKanFanHome() {
    }

    protected IKanFanHome(Parcel in) {
        success = in.readByte() != 0;
        message = in.readString();
        result = in.createTypedArrayList(IKanFanVideo.CREATOR);
    }

    public static final Creator<IKanFanHome> CREATOR = new Creator<IKanFanHome>() {
        @Override
        public IKanFanHome createFromParcel(Parcel in) {
            return new IKanFanHome(in);
        }

        @Override
        public IKanFanHome[] newArray(int size) {
            return new IKanFanHome[size];
        }
    };

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public List<? extends IVideo> getList() {
        return result;
    }


    @Override
    public List<? extends IViewType> getAdapterResult() {
        return result;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
        dest.writeTypedList(result);
    }

    public void setResult(List<IKanFanVideo> result) {
        this.result = result;
    }
}

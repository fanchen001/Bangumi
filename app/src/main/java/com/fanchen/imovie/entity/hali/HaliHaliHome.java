package com.fanchen.imovie.entity.hali;

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
public class HaliHaliHome implements IHomeRoot, IBangumiMoreRoot, Parcelable {

    private boolean success;
    private String message;
    private List<HaliHaliVideo> result;

    public HaliHaliHome() {
    }

    protected HaliHaliHome(Parcel in) {
        success = in.readByte() != 0;
        message = in.readString();
        result = in.createTypedArrayList(HaliHaliVideo.CREATOR);
    }

    public static final Creator<HaliHaliHome> CREATOR = new Creator<HaliHaliHome>() {
        @Override
        public HaliHaliHome createFromParcel(Parcel in) {
            return new HaliHaliHome(in);
        }

        @Override
        public HaliHaliHome[] newArray(int size) {
            return new HaliHaliHome[size];
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

    public void setResult(List<HaliHaliVideo> result) {
        this.result = result;
    }
}

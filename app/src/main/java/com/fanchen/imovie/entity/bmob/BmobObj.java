package com.fanchen.imovie.entity.bmob;

import com.fanchen.imovie.IMovieAppliction;

import java.lang.ref.SoftReference;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by fanchen on 2017/10/8.
 */
public class BmobObj extends BmobObject{

    public void save() {
        if(IMovieAppliction.app == null)return;
        super.save(IMovieAppliction.app);
    }

    public void save(OnRefreshListener listener) {
        if(listener == null || IMovieAppliction.app == null)return;
        listener.onStart();
        final SoftReference<OnRefreshListener> reference = new SoftReference<OnRefreshListener>(listener);
        super.save(IMovieAppliction.app, new SaveListener() {
            @Override
            public void onSuccess() {
                OnRefreshListener onSaveListener = reference.get();
                if(onSaveListener != null){
                    onSaveListener.onSuccess();
                    onSaveListener.onFinish();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                OnRefreshListener onSaveListener = reference.get();
                if(onSaveListener != null){
                    onSaveListener.onFailure(i,s);
                    onSaveListener.onFinish();
                }
            }
        });
    }

    public void update(final OnUpdateListener listener) {
        if(listener == null || IMovieAppliction.app == null)return;
        listener.onStart();
        try {
            super.update(IMovieAppliction.app,new InnerUpdateListener(listener));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void update(String objectId,final OnUpdateListener listener) {
        if(listener == null || IMovieAppliction.app == null)return;
        listener.onStart();
        try {
            super.update(IMovieAppliction.app, objectId,new InnerUpdateListener(listener));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class InnerUpdateListener extends UpdateListener {

        private OnUpdateListener listener;

        public InnerUpdateListener(OnUpdateListener listener){
            this.listener = listener;
        }

        @Override
        public void onSuccess() {
            listener.onSuccess();
            listener.onFinish();
        }

        @Override
        public void onFailure(int i, String s) {
            listener.onFailure(i,s);
            listener.onFinish();
        }

    }

    public abstract static class OnRefreshListener extends SaveListener{

        public abstract void onStart();

        public abstract void onFinish();

    }

    public static abstract class OnUpdateListener extends UpdateListener {
        public abstract void onStart();

        public abstract void onFinish();
    }
}

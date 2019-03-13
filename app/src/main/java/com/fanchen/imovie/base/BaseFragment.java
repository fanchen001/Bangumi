package com.fanchen.imovie.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.arialyy.aria.core.download.DownloadReceiver;
import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.entity.AppEvent;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.util.LogUtil;
import com.litesuits.orm.LiteOrm;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;

import butterknife.ButterKnife;


/**
 * 应用中所有的fragment基础该类
 * <p/>
 * 提供一些基本方法的封装
 * Created by fanchen on 2017/7/21.
 */
public abstract class BaseFragment extends Fragment {

    /**
     * 该Fragment的主视图
     */
    private View mMainView;
    private Bundle mSaveState;
    /**
     * 该Fragment绑定的activity
     */
    public BaseActivity activity;
    /**
     * appliction
     */
    public IMovieAppliction appliction;

    // 标志位 标志已经初始化完成。
    protected volatile boolean isPrepared = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof BaseActivity){
            activity = (BaseActivity) context;
        }else{
            activity = (BaseActivity) getActivity();
        }
        appliction = activity.appliction;
    }

    /**
     * 是否使用eventbus
     *
     * @return
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(AppEvent event) {
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroudEvent(AppEvent event) {
    }

    /**
     * @param event
     */
    public void postAppEvent(AppEvent event) {
        EventBus.getDefault().post(event);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSaveState = savedInstanceState;
        if (isRegisterEventBus())
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegisterEventBus())
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        try{//FastPrintWriter.println  NullPointerException
            super.dump(prefix, fd, writer, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public Picasso getPicasso() {
        if (activity != null)
            return activity.getPicasso();
        if(IMovieAppliction.app != null)
            return IMovieAppliction.app.getPicasso();
        return null;
    }

    /**
     *
     * @return
     */
    public DownloadReceiver getDownloadReceiver(){
        if (activity != null){
            return activity.getDownloadReceiver();
        }else if(IMovieAppliction.app != null){
            IMovieAppliction.app.getDownloadReceiver();
        }
        return null;
    }

    public LiteOrm getLiteOrm(){
        return activity == null ? null : activity.getLiteOrm();
    }

    public RetrofitManager getRetrofitManager(){
        return activity == null ? null : activity.getRetrofitManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layout = getLayout();
        if (layout > 0) {
            mMainView = inflater.inflate(layout, container, false);
            ButterKnife.inject(this, mMainView);
            return mMainView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //用户可见，并且没有初始化
        if (!isPrepared && getUserVisibleHint() && view != null && isAdded()) {
            mMainView.post(new InitRunnable(this, view,mSaveState));
        }else{
            LogUtil.e("onViewCreated", "not post");
        }
    }

    private void init(View view, @Nullable Bundle savedInstanceState) {
        synchronized (BaseFragment.class) {
            findView(view);
            initFragment(savedInstanceState, getArguments());
            setListener();
            isPrepared = true;
        }
    }

    /**
     * fix bug getString()
     * @param id
     * @return
     */
    public String getStringFix(int id){
        return activity != null ? activity.getString(id) : "";
    }

    /**
     * Fragment数据的懒加载
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint() && mMainView != null && !isPrepared && isAdded()) {
            mMainView.post(new InitRunnable(this, mMainView, mSaveState));
        }else{
            LogUtil.e("setUserVisibleHint","not post");
        }
    }

    /**
     * 应用布局文件id
     *
     * @return
     */
    protected abstract int getLayout();

    /**
     * 查找必要控件
     *
     * @param v
     */
    protected void findView(View v) {
    }

    /**
     * 初始化视图控件及数据
     *
     * @param savedInstanceState
     * @param args
     */
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
    }

    /**
     * 设置监听器
     */
    protected void setListener() {
    }

    /**
     * @param attr
     * @return
     */
    public int getAttributeValue(int attr) {
        if (isAdded() && !isDetached() && activity != null) {
            return activity.getAttributeValue(attr);
        }
        return -1;
    }

    /**
     * 通过id查找控件，在mMainView上
     *
     * @param id  控件id
     * @param <T>
     * @return
     */
    public <T extends View> T findViewById(int id) {
        return mMainView == null ? null : (T) mMainView.findViewById(id);
    }

    /**
     * 通过Child position 查找控件。在mMainView上
     *
     * @param position 下标
     * @param <T>
     * @return
     */
    public <T extends View> T getChild(int position) {
        return mMainView == null ? null : (mMainView instanceof ViewGroup) ? (T) ((ViewGroup) mMainView).getChildAt(position) : null;
    }

    /**
     * 打开一个Activity
     *
     * @param clazz
     */
    public void startActivity(Class<?> clazz) {
        if (isAdded() && !isDetached() && activity != null) {
            startActivity(new Intent(activity, clazz));
        }
    }

    /**
     * 打开一个Activity
     *
     * @param clazz
     * @param bundle
     */
    public void startActivity(Class<?> clazz, Bundle bundle) {
        if (isAdded() && !isDetached() && activity != null) {
            Intent intent = new Intent(activity, clazz);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    /**
     * 打开一个Service
     *
     * @param clazz
     */
    public void startService(Class<?> clazz) {
        if (isAdded() && !isDetached() && activity != null) {
            activity.startService(clazz);
        }
    }

    /**
     * 打开一个Service
     *
     * @param clazz
     * @param bundle
     */
    public void startService(Class<?> clazz, Bundle bundle) {
        if (isAdded() && !isDetached() && activity != null) {
            activity.startService(clazz, bundle);
        }
    }

    /**
     * 开启activity并在返回时获取返回值
     *
     * @param clazz
     * @param code
     */
    public void startActivityForResult(Class<?> clazz, int code) {
        if (isAdded() && !isDetached() && activity != null) {
            startActivityForResult(new Intent(activity, clazz), code);
        }
    }

    /**
     * 开启activity并在返回时获取返回值
     *
     * @param clazz
     * @param bundle
     * @param code
     */
    public void startActivityForResult(Class<?> clazz, Bundle bundle, int code) {
        if (isAdded() && !isDetached() && activity != null) {
            Intent intent = new Intent(activity, clazz);
            intent.putExtras(bundle);
            startActivityForResult(intent, code);
        }
    }

    /**
     * 获取EditText 的字符串
     *
     * @param editText
     * @return
     */
    public String getEditTextString(EditText editText) {
        if (isAdded() && !isDetached() && activity != null) {
            return activity.getEditTextString(editText);
        }
        return "";
    }

    /**
     * 显示一个Toast
     *
     * @param id
     */
    public void showToast(int id) {
        if (isAdded() && !isDetached() && activity != null) {
            activity.showToast(id);
        }
    }

    /**
     * 显示一个Toast
     *
     * @param c
     */
    public void showToast(CharSequence c) {
        if (isAdded() && !isDetached() && activity != null) {
            activity.showToast(c);
        }
    }

    /**
     * 显示一个Toast
     *
     * @param id
     * @param len
     */
    public void showToast(int id, int len) {
        if (isAdded() && !isDetached() && activity != null) {
            activity.showToast(id, len);
        }
    }

    /**
     * 显示一个Snackbar
     *
     * @param view
     * @param c
     * @param len
     */
    public void showSnackbar(View view, CharSequence c, int len) {
        if (isAdded() && !isDetached() && activity != null) {
            activity.showSnackbar(view, c, len);
        }
    }

    /**
     * 显示一个Snackbar
     *
     * @param c
     */
    public void showSnackbar(CharSequence c) {
        if (isAdded() && !isDetached() && activity != null) {
            activity.showSnackbar(c);
        }
    }

    /**
     * 显示一个Snackbar
     *
     * @param view
     * @param c
     */
    public void showSnackbar(View view, CharSequence c) {
        if (isAdded() && !isDetached() && activity != null) {
            activity.showSnackbar(view, c);
        }
    }

    /**
     * 显示一个Snackbar
     *
     * @param view
     * @param c
     * @param title
     * @param l
     */
    public void showSnackbar(View view, CharSequence c, CharSequence title, View.OnClickListener l) {
        if (isAdded() && !isDetached() && activity != null) {
            activity.showSnackbar(view, c, title, l);
        }
    }

    private static class InitRunnable implements Runnable {

        private View view;
        private SoftReference<BaseFragment> softReference;
        private
        @Nullable
        Bundle savedInstanceState;

        public InitRunnable(BaseFragment fragment, View view, @Nullable Bundle savedInstanceState) {
            softReference = new SoftReference<>(fragment);
            this.view = view;
            this.savedInstanceState = savedInstanceState;
        }

        @Override
        public void run() {
            BaseFragment baseFragment = softReference.get();
            if (baseFragment != null) {
                baseFragment.init(view, savedInstanceState);
            }
        }
    }

}

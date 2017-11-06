package com.fanchen.imovie.util;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.VideoDetailsActivity;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseFragment;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.BaseDialog;
import com.fanchen.imovie.dialog.BottomShowDialog;
import com.fanchen.imovie.dialog.DownloadDialog;
import com.fanchen.imovie.dialog.MaterialDialog;
import com.fanchen.imovie.dialog.MaterialListDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.bmob.BmobObj;
import com.fanchen.imovie.entity.bmob.VideoCollect;
import com.fanchen.imovie.entity.dytt.DyttLiveBody;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.fragment.CollectFragment;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RetrofitCallback;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListener;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.view.CustomEmptyView;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fanchen on 2017/7/24.
 */
public class DialogUtil {

    private static Dialog dialog;

    private static Dialog progressDialog;

    private static Dialog materialListDialog;

    private static Dialog materialDialog;

    /**
     * @param context
     * @param episodes
     */
    public static void showDownloadDialog(BaseActivity context, IVideoDetails episodes) {
        new DownloadDialog(context, episodes).show();
    }

    /**
     *
     */
    private static void closeDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }

    public static void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    public static void closeMaterialListDialog() {
        if (materialListDialog != null && materialListDialog.isShowing()) {
            materialListDialog.dismiss();
        }
        materialListDialog = null;
    }

    public static void closeMaterialDialog() {
        if (materialDialog != null && materialDialog.isShowing()) {
            materialDialog.dismiss();
        }
        materialDialog = null;
    }


    public static void showProgressDialog(Context context, String text) {
        closeDialog();
        LinearLayout inflate = (LinearLayout) View.inflate(context, R.layout.layout_loading, null);
        inflate.setGravity(Gravity.LEFT);
        TextView mProgressTextView = (TextView) inflate.getChildAt(1);
        mProgressTextView.setText(text);
        MaterialDialog materialDialog = (MaterialDialog) (DialogUtil.progressDialog = dialog = new MaterialDialog(context, inflate));
        materialDialog.setTitleVisble(View.GONE);
        materialDialog.setButtonVisble(View.GONE);
        materialDialog.show();
    }

    /**
     * @param context
     * @param titles
     * @param l
     */
    public static void showMaterialListDialog(Context context, String[] titles, AdapterView.OnItemClickListener l) {
        closeDialog();
        MaterialListDialog materialListDialog = (MaterialListDialog) (dialog = DialogUtil.materialListDialog = new MaterialListDialog(context, titles));
        materialListDialog.setItemClickListener(l);
        materialListDialog.show();
    }


    /**
     *
     * @param fragment
     * @param taskListener
     * @param datas
     * @param position
     */
    public static void showMaterialDeleteDialog(CollectFragment fragment,AsyTaskListener<?> taskListener, List<VideoCollect> datas, int position){
        closeDialog();
        showMaterialListDialog(fragment.activity, new String[]{"删除记录", "直接打开"}, new DeletClickListener(fragment, taskListener,datas, position));
    }

    /**
     * @param context
     * @param stringMap
     * @param l
     */
    public static void showMaterialListDialog(Context context, String title, Set<String> stringMap, AdapterView.OnItemClickListener l) {
        closeDialog();
        if (stringMap == null || stringMap.isEmpty()) return;
        String[] titles = new String[stringMap.size()];
        Iterator<String> iterator = stringMap.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            titles[count] = iterator.next();
            count++;
        }
        MaterialListDialog materialListDialog = (MaterialListDialog) (DialogUtil.materialListDialog = dialog = new MaterialListDialog(context, titles));
        materialListDialog.title(title);
        materialListDialog.setItemClickListener(l);
        materialListDialog.show();
    }


    /**
     * @param context
     * @param content
     * @param l
     */
    public static void showMaterialDialog(Context context, String content, OnButtonClickListener l) {
        closeDialog();
        MaterialDialog materialDialog = (MaterialDialog) (DialogUtil.materialDialog = dialog = new MaterialDialog(context));
        materialDialog.setTitleVisble(View.GONE);
        materialDialog.content(content);
        materialDialog.setButtonClickListener(l);
        materialDialog.show();
    }

    public static void showMessageDialog(Context context, String content) {
        closeDialog();
        MaterialDialog materialDialog = (MaterialDialog) (DialogUtil.materialDialog = dialog = new MaterialDialog(context));
        materialDialog.title("提示");
        materialDialog.content(content);
        materialDialog.btnNum(1);
        materialDialog.btnText("知道了");
        materialDialog.setButtonClickListener(new OnButtonClickListener() {
            @Override
            public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
                dialog.dismiss();
            }
        });
        materialDialog.show();
    }

    public static void showMaterialDialog(Context context, String content, String btn1, String btn2, OnButtonClickListener l) {
        closeDialog();
        MaterialDialog materialDialog = (MaterialDialog) (DialogUtil.materialDialog = dialog = new MaterialDialog(context));
        materialDialog.setTitleVisble(View.GONE);
        materialDialog.btnText(btn1, btn2);
        materialDialog.btnNum(2);
        materialDialog.content(content);
        materialDialog.setButtonClickListener(l);
        materialDialog.show();
    }

    /**
     * @param context
     * @param l
     */
    public static void showInputDialog(Context context, final OnInputListener l) {
        closeDialog();
        final EditText editText = new EditText(context);
        MaterialDialog materialDialog = (MaterialDialog) (DialogUtil.materialDialog = dialog = new MaterialDialog(context, editText));
        materialDialog.title("请输入");
        materialDialog.setButtonClickListener(new OnButtonClickListener() {

            @Override
            public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
                if (btn == OnButtonClickListener.LIFT) {
                    dialog.dismiss();
                    return;
                }
                if (l != null) {
                    l.onInput(editText, dialog);
                }
            }

        });
        materialDialog.show();
    }

    /**
     * @param context
     */
    public static void showBottomCancleDialog(Context context) {
        new BottomShowDialog(context).show();
    }

    /**
     * @param context
     * @param listener
     */
    public static void showBottomCancleDialog(Context context, View.OnClickListener listener) {
        new BottomShowDialog(context, listener).show();
    }

    /**
     * @param context
     */
    public static void showBottomAgreementDialog(Context context) {
        new BottomShowDialog(context, R.layout.dialog_user_agreement).show();
    }

    /**
     * @param context
     */
    public static void showBottomAgreementDialog(Context context, View.OnClickListener listener) {
        new BottomShowDialog(context, R.layout.dialog_user_agreement, listener).show();
    }

    /**
     * @param fragment
     * @param position
     * @param videos
     */
    public static void showOperationDialog(BaseFragment fragment, IVideo video, List<IVideo> videos, int position) {
        if (!(fragment instanceof BaseAdapter.OnItemClickListener)) return;
        DialogUtil.showMaterialListDialog(fragment.activity, new String[]{"打开详情", "加入收藏"}, new ItemClickListener(position, video, videos, fragment, (BaseAdapter.OnItemClickListener) fragment));
    }

    public static void showOperationDialog(BaseFragment fragment, DyttLiveBody video, List<DyttLiveBody> videos, int position) {
        if (!(fragment instanceof BaseAdapter.OnItemClickListener)) return;
        DialogUtil.showMaterialListDialog(fragment.activity, new String[]{"打开详情", "加入收藏"}, new ItemClickListener(position, video, videos, fragment, (BaseAdapter.OnItemClickListener) fragment));
    }

    public static void showDownloadOperationDialog(BaseFragment fragment, IVideo video, List<IVideo> videos, int position, RetrofitCallback<?> callback) {
        if (!(fragment instanceof BaseAdapter.OnItemClickListener)) return;
        DialogUtil.showMaterialListDialog(fragment.activity, new String[]{"直接打开", "加入收藏", "下载视频"}, new ItemClickListener(position, video, videos, fragment, (BaseAdapter.OnItemClickListener) fragment, callback));
    }

    private static class ItemClickListener implements AdapterView.OnItemClickListener {

        private int position;
        private IVideo item;
        private DyttLiveBody liveBody;
        private List<?> datas;
        private SoftReference<BaseActivity> activity;
        private SoftReference<BaseFragment> fragment;
        private SoftReference<RetrofitCallback<?>> downloadCallback;
        private SoftReference<BaseAdapter.OnItemClickListener> onItemClick;

        public ItemClickListener(int position, DyttLiveBody item, List<?> datas, BaseActivity activity, BaseAdapter.OnItemClickListener onItemClick) {
            this.position = position;
            this.liveBody = item;
            this.datas = datas;
            this.activity = new SoftReference<>(activity);
            this.onItemClick = new SoftReference<>(onItemClick);
        }

        public ItemClickListener(int position, DyttLiveBody item, List<?> datas, BaseFragment fragment, BaseAdapter.OnItemClickListener onItemClick) {
            this.position = position;
            this.liveBody = item;
            this.datas = datas;
            this.fragment = new SoftReference<>(fragment);
            this.onItemClick = new SoftReference<>(onItemClick);
        }

        public ItemClickListener(int position, IVideo item, List<?> datas, BaseActivity activity, BaseAdapter.OnItemClickListener onItemClick) {
            this.position = position;
            this.item = item;
            this.datas = datas;
            this.activity = new SoftReference<>(activity);
            this.onItemClick = new SoftReference<>(onItemClick);
        }

        public ItemClickListener(int position, IVideo item, List<?> datas, BaseFragment fragment, BaseAdapter.OnItemClickListener onItemClick) {
            this.position = position;
            this.item = item;
            this.datas = datas;
            this.fragment = new SoftReference<>(fragment);
            this.onItemClick = new SoftReference<>(onItemClick);
        }

        public ItemClickListener(int position, IVideo item, List<?> datas, BaseFragment fragment, BaseAdapter.OnItemClickListener onItemClick, RetrofitCallback<?> callback) {
            this.position = position;
            this.item = item;
            this.datas = datas;
            this.fragment = new SoftReference<>(fragment);
            this.onItemClick = new SoftReference<>(onItemClick);
            this.downloadCallback = new SoftReference<RetrofitCallback<?>>(callback);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    BaseAdapter.OnItemClickListener listener = onItemClick.get();
                    if (listener != null)
                        listener.onItemClick(datas, view, this.position);
                    break;
                case 1:
                    if (activity != null) {
                        BaseActivity baseActivity = activity.get();
                        if (baseActivity != null && baseActivity.checkLogin()) {
                            if (item != null) {
                                DialogUtil.showMaterialDialog(baseActivity, String.format(baseActivity.getString(R.string.collect_hit), item.getTitle()), new ButtonClickListener(item, activity));
                            } else if (liveBody != null) {
                                DialogUtil.showMaterialDialog(baseActivity, String.format(baseActivity.getString(R.string.collect_hit), liveBody.getVideoName()), new ButtonClickListener(liveBody, activity));
                            }
                        }
                    }
                    if (fragment != null) {
                        BaseFragment baseFragment = fragment.get();
                        if (baseFragment != null && baseFragment.activity.checkLogin()) {
                            if (item != null) {
                                DialogUtil.showMaterialDialog(baseFragment.activity, String.format(baseFragment.activity.getString(R.string.collect_hit), item.getTitle()), new ButtonClickListener(item, new SoftReference<>(baseFragment.activity)));
                            } else if (liveBody != null) {
                                DialogUtil.showMaterialDialog(baseFragment.activity, String.format(baseFragment.activity.getString(R.string.collect_hit), liveBody.getVideoName()), new ButtonClickListener(liveBody, new SoftReference<>(baseFragment.activity)));
                            }
                        }
                    }
                    break;
                case 2:
                    try {
                        if (downloadCallback == null) return;
                        RetrofitCallback<?> callback = downloadCallback.get();
                        if (callback == null) return;
                        RetrofitManager manager = null;
                        if (activity != null) {
                            BaseActivity baseActivity = activity.get();
                            if (baseActivity != null) {
                                manager = baseActivity.getRetrofitManager();
                            }
                        } else if (fragment != null) {
                            BaseFragment baseFragment = fragment.get();
                            if (baseFragment != null) {
                                manager = baseFragment.getRetrofitManager();
                            }
                        }
                        if (manager != null) {
                            manager.enqueue(item.getServiceClassName(), callback, "playUrl", item.getUrl());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     *
     */
    private static class ButtonClickListener implements OnButtonClickListener {

        private IVideo item;
        private DyttLiveBody liveBody;
        private SoftReference<BaseActivity> activity;

        public ButtonClickListener(IVideo item, SoftReference<BaseActivity> activity) {
            this.item = item;
            this.activity = activity;
        }

        public ButtonClickListener(DyttLiveBody item, SoftReference<BaseActivity> activity) {
            this.liveBody = item;
            this.activity = activity;
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            BaseActivity baseActivity = activity.get();
            if (btn == OnButtonClickListener.RIGHT && baseActivity != null) {
                if (item != null) {
                    long count = baseActivity.getLiteOrm().queryCount(new QueryBuilder<>(VideoCollect.class).where("id = ?", item.getId()));
                    if (count <= 0) {
                        VideoCollect collect = new VideoCollect(item);
                        collect.save(new CollectListener(collect, activity));
                    } else {
                        baseActivity.showSnackbar(baseActivity.getString(R.string.collect_repetition));
                    }
                } else if (liveBody != null) {
                    long count = baseActivity.getLiteOrm().queryCount(new QueryBuilder<>(VideoCollect.class).where("id = ?", String.valueOf(liveBody.getVideoId())));
                    if (count <= 0) {
                        VideoCollect collect = new VideoCollect(liveBody);
                        collect.save(new CollectListener(collect, activity));
                    } else {
                        baseActivity.showSnackbar(baseActivity.getString(R.string.collect_repetition));
                    }
                }
            }
            dialog.dismiss();
        }

    }

    private static class CollectListener extends BmobObj.OnRefreshListener {

        private VideoCollect collect;
        private SoftReference<BaseActivity> softReference;

        public CollectListener(VideoCollect collect, SoftReference<BaseActivity> activity) {
            this.collect = collect;
            this.softReference = activity;
        }

        @Override
        public void onStart() {
            BaseActivity activity = softReference.get();
            if (activity == null || activity.isFinishing()) return;
            DialogUtil.showProgressDialog(activity, activity.getString(R.string.collect_ing));
        }

        @Override
        public void onFinish() {
            BaseActivity activity = softReference.get();
            if (activity == null || activity.isFinishing()) return;
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onSuccess() {
            BaseActivity activity = softReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.showSnackbar(activity.getString(R.string.collect_asy_success));
            AsyTaskQueue.newInstance().execute(new TaskListener(softReference, collect));
        }

        @Override
        public void onFailure(int i, String s) {
            LogUtil.e("onFailure","onFailure=>" + s);
            BaseActivity activity = softReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.showSnackbar(activity.getString(R.string.collect_asy_error));
        }

    }

    /**
     *
     */
    private static class TaskListener extends AsyTaskListenerImpl<Integer> {

        public int SUCCESS = 0;
        public int ERROR = 2;

        private VideoCollect collect;
        private SoftReference<BaseActivity> activity;

        public TaskListener(SoftReference<BaseActivity> activity, VideoCollect item) {
            this.collect = item;
            this.activity = activity;
        }

        @Override
        public Integer onTaskBackground() {
            BaseActivity baseActivity = activity.get();
            if (baseActivity == null) return ERROR;
            baseActivity.getLiteOrm().insert(collect);
            return SUCCESS;
        }

        @Override
        public void onTaskSuccess(Integer data) {
            BaseActivity baseActivity = activity.get();
            if (baseActivity == null || baseActivity.isFinishing()) return;
            if (data == SUCCESS) {
                baseActivity.showSnackbar(baseActivity.getString(R.string.collect_success));
            }else {
                baseActivity.showSnackbar(baseActivity.getString(R.string.collect_error));
            }
        }

    }

    /**
     *
     */
    private static class DeletClickListener implements AdapterView.OnItemClickListener {

        private SoftReference<CollectFragment> fragmentReference;
        private List<VideoCollect> datas;
        private SoftReference<AsyTaskListener<?>> taskReference;
        private int position;

        public DeletClickListener(CollectFragment fragment, AsyTaskListener<?> taskListener,List<VideoCollect> datas, int position) {
            fragmentReference = new SoftReference<CollectFragment>(fragment);
            taskReference = new SoftReference<AsyTaskListener<?>>(taskListener);
            this.datas = datas;
            this.position = position;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    AsyTaskListener<?> asyTaskListener = taskReference.get();
                    if(asyTaskListener == null)return;
                    AsyTaskQueue.newInstance().execute(asyTaskListener);
                    break;
                case 1:
                    CollectFragment collectFragment = fragmentReference.get();
                    if(collectFragment == null)return;
                    collectFragment.onItemClick(datas, view, this.position);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     *
     */
    public interface OnInputListener {
        /**
         * @param editText
         * @param dialog
         */
        void onInput(EditText editText, BaseDialog<?> dialog);

    }
}

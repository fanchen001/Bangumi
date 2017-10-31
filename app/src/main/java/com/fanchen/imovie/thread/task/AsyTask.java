
package com.fanchen.imovie.thread.task;

import android.os.AsyncTask;

import java.lang.ref.SoftReference;

/**
 * @author fanchen
 */
public class AsyTask extends AsyncTask<AsyTaskItem, Integer, AsyTaskItem> {

    /**
     * 监听器.
     */
    private SoftReference<AsyTaskListener> listener;

    /**
     * 结果.
     */
    private Object result;

    /**
     * 初始化Task.
     */
    public AsyTask() {
    }

    /**
     * 实例化.
     */
    public static AsyTask newInstance() {
        AsyTask mAbTask = new AsyTask();
        return mAbTask;
    }

    /**
     * 执行任务.
     *
     * @param items
     * @return
     */
    @Override
    protected AsyTaskItem doInBackground(AsyTaskItem... items) {
        AsyTaskItem item = items[0];
        this.listener = item.getListener();
        if (this.listener != null && this.listener.get() != null) {
            this.result = this.listener.get().onTaskBackground();
        }
        return item;
    }

    /**
     * 执行完成.
     *
     * @param item
     */
    @Override
    protected void onPostExecute(AsyTaskItem item) {
        if (this.listener != null && this.listener.get() != null) {
            this.listener.get().onTaskSuccess(this.result);
        }
    }

    /**
     * 进度更新.
     *
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (this.listener != null && this.listener.get() != null) {
            this.listener.get().onTaskProgress(values);
        }
    }

}

package com.fanchen.imovie.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import com.fanchen.imovie.thread.task.AsyTaskItem;
import com.fanchen.imovie.thread.task.AsyTaskListener;
import com.fanchen.imovie.util.ActiveUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executor;


/**
 * 描述：线程队列.
 *
 * @author fanchen
 */
public class AsyTaskQueue extends Thread {

    private static AsyTaskQueue abTaskQueue;

    /**
     * 等待执行的任务. 用 LinkedList增删效率高
     */
    private LinkedList<AsyTaskItem> taskItemList = null;

    /**
     * 停止的标记.
     */
    private boolean quit = false;

    /**
     * 存放返回的任务结果.
     */
    private HashMap<String, Object> result;

    /**
     * 执行完成后的消息句柄.
     */
    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            AsyTaskItem item = (AsyTaskItem) msg.obj;
            if (item != null && item.getListener() != null) {
                AsyTaskListener asyTaskListener = item.getListener().get();
                if(asyTaskListener == null)return;
                switch (msg.what) {
                    case 0:
                        if(ActiveUtil.checkActive(asyTaskListener)){
                            asyTaskListener.onTaskStart();
                        }
                        break;
                    case 1:
                        if(ActiveUtil.checkActive(asyTaskListener)){
                            asyTaskListener.onTaskFinish();
                        }
                        break;
                    case 2:
                        if(ActiveUtil.checkActive(asyTaskListener)){
                            asyTaskListener.onTaskSuccess(result.get(item.toString()));
                        }
                        result.remove(item.toString());
                        break;
                    default:
                        break;
                }
            }
        }
    };

    /**
     * 构造.
     *
     * @return
     */
    public static AsyTaskQueue newInstance() {
        if(abTaskQueue == null){
            synchronized (AsyTaskQueue.class){
                if(abTaskQueue == null){
                    abTaskQueue = new AsyTaskQueue();
                }
            }
        }
        return abTaskQueue;
    }

    /**
     * 构造执行线程队列.
     */
    private AsyTaskQueue() {
        quit = false;
        taskItemList = new LinkedList<>();
        result = new HashMap<String, Object>();
        //从线程池中获取
        Executor mExecutorService = AsyThreadFactory.getExecutorService();
        mExecutorService.execute(this);
    }

    /**
     * 开始一个执行任务.
     *
     * @param item 执行单位
     */
    public void execute(AsyTaskItem item) {
        addTaskItem(item);
    }

    public void execute(AsyTaskListener listener) {
        AsyTaskItem item = new AsyTaskItem(listener);
        addTaskItem(item);
    }


    /**
     * 开始一个执行任务并清除原来队列.
     *
     * @param item   执行单位
     * @param cancel 清空之前的任务
     */
    public void execute(AsyTaskItem item, boolean cancel) {
        if (cancel) {
            cancel(true);
        }
        addTaskItem(item);
    }

    /**
     * 描述：添加到执行线程队列.
     *
     * @param item 执行单位
     */
    private synchronized void addTaskItem(AsyTaskItem item) {
        taskItemList.add(item);
        //添加了执行项就激活本线程
        this.notify();
    }

    /**
     * 描述：线程运行.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (!quit) {
            try {
                while (taskItemList.size() > 0) {
                    AsyTaskItem item = taskItemList.remove(0);
                    //交由UI线程处理
                    Message start = handler.obtainMessage();
                    start.what = 0;
                    start.obj = item;
                    handler.sendMessage(start);
                    //定义了回调
                    if (item != null && item.getListener() != null) {
                        AsyTaskListener asyTaskListener = item.getListener().get();
                        if(asyTaskListener != null && ActiveUtil.checkActive(asyTaskListener)){
                            result.put(item.toString(), asyTaskListener.onTaskBackground());
                            //交由UI线程处理
                            Message success = handler.obtainMessage();
                            success.what = 2;
                            success.obj = item;
                            handler.sendMessage(success);
                        }
                    }
                    //停止后清空
                    if (quit) {
                        taskItemList.clear();
                        return;
                    }
                    //结束
                    Message finish = handler.obtainMessage();
                    finish.what = 1;
                    finish.obj = item;
                    handler.sendMessage(finish);
                }
                try {
                    //没有执行项时等待
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //被中断的是退出就结束，否则继续
                    if (quit) {
                        taskItemList.clear();
                        return;
                    }
                    continue;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 描述：终止队列释放线程.
     *
     * @param interrupt the may interrupt if running
     */
    public void cancel(boolean interrupt) {
        try {
            quit = true;
            if (interrupt) {
                interrupted();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinkedList<AsyTaskItem> getTaskItemList() {
        return taskItemList;
    }

    public int getTaskSize() {
        return taskItemList.size();
    }

}


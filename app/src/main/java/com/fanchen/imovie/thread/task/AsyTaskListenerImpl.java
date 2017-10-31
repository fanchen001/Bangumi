package com.fanchen.imovie.thread.task;

/**
 * 默认实现
 * Created by fanchen on 2017/7/24.
 */
public class AsyTaskListenerImpl<T> implements AsyTaskListener<T>{

    @Override
    public T onTaskBackground() {
        return null;
    }

    @Override
    public void onTaskSuccess(T data) {

    }

    @Override
    public void onTaskFinish() {

    }

    @Override
    public void onTaskProgress(Integer... values) {

    }

    @Override
    public void onTaskSart() {

    }

}

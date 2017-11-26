package com.fanchen.imovie.thread.task;

import java.lang.ref.SoftReference;

/**
 * 描述：数据执行单位.
 *
 */
public class AsyTaskItem { 
	
	/** 记录的当前索引. */
	private int position;
	 
 	/** 执行完成的回调接口. */
    private SoftReference<AsyTaskListener> listener;
    
	/**
	 * Instantiates a new ab task item.
	 */
	public AsyTaskItem() {
		super();
	}

	/**
	 * Instantiates a new ab task item.
	 *
	 * @param listener the listener
	 */
	public AsyTaskItem(AsyTaskListener listener) {
		super();
		this.listener = new SoftReference<>(listener);
	}

	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Sets the position.
	 *
	 * @param position the new position
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Gets the listener.
	 *
	 * @return the listener
	 */
	public SoftReference<AsyTaskListener> getListener() {
		return listener;
	}

	/**
	 * Sets the listener.
	 *
	 * @param listener the new listener
	 */
	public void setListener(AsyTaskListener listener) {
		this.listener = new SoftReference<>(listener);
	}

} 


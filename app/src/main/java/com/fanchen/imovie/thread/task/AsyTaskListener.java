package com.fanchen.imovie.thread.task;


/**
 * 描述：任务执行的控制父类.
 * 
 */
public interface AsyTaskListener<T> {

	/**
	 * 执行开始.
	 * 
	 * @return 返回的结果对象
	 */
	T onTaskBackground() ;

	/**
	 * 执行开始后调用.
	 * */
	void onTaskSuccess(T data);

	/**
	 * 执行结束
	 */
	void onTaskFinish();

	/**
	 * 监听进度变化.
	 * 
	 * @param values
	 *            the values
	 */
	void onTaskProgress(Integer... values);
	
	/**
	 * 执行开始前调用
	 * */
	void onTaskStart() ;

}

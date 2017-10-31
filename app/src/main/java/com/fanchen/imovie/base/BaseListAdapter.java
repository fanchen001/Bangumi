package com.fanchen.imovie.base;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自定义baseadapter,所有的listadapter都继承自该类
 * 
 * @author fanchen
 * 
 * @param <T>
 */
public abstract class BaseListAdapter<T> extends BaseViewListAdapter<T> {
	
	public BaseListAdapter(Context context) {
		super(context);
	}

	public BaseListAdapter(Context context, List<T> mList) {
		super(context, mList);
	}

	public BaseListAdapter(Context context, T[] all) {
		super(context, all);
	}

	@Override
	public View getInflateView(ViewGroup viewGroup) {
		return mLayoutInflater.inflate(getInflateLayout(), viewGroup, false);
	}

	/**
	 * item对应布局文件
	 * @return
	 */
	public abstract int getInflateLayout();

}

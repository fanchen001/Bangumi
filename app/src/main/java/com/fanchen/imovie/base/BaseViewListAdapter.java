package com.fanchen.imovie.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BaseViewListAdapter<T> extends BaseAdapter {

	protected List<T> mList;// 需要显示的数据集

	protected LayoutInflater mLayoutInflater;// 布局填充器

	protected Context context;

	public BaseViewListAdapter(Context context) {
		this.context = context;
		mLayoutInflater = LayoutInflater.from(context);
		this.mList = new ArrayList<T>();
	}

	public BaseViewListAdapter(Context context, List<T> mList) {
		this.context = context;
		mLayoutInflater = LayoutInflater.from(context);
		this.mList = mList;
	}

	public BaseViewListAdapter(Context context, T[] all) {
		this.context = context;
		mLayoutInflater = LayoutInflater.from(context);
		this.mList = new ArrayList<T>();
		for (int i = 0; i < all.length; i++) {
			this.mList.add(all[i]);
		}
	}

	/**
	 * item对应的布局
	 * 
	 * @return
	 */
	public abstract View getInflateView(ViewGroup viewGroup);

	/**
	 * 初始化item数据
	 * 
	 * @param v
	 * @param date
	 */
	public abstract void initItemView(View v, T date, int position);

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getInflateView(parent);
		}
		T t = mList.get(position);
		if(t == null)
			return convertView;
		ViewExtraInfo tag = (ViewExtraInfo) convertView.getTag();
		if (tag == null) {
			tag = new ViewExtraInfo();
			tag.data1 = t;
			initItemView(convertView, t, position);
		} else if (tag.data1 == null || tag.data1 != t) {
			tag.data1 = t;
			initItemView(convertView, t, position);
		} else {
			if (isNotify()) {
				initItemView(convertView, t, position);
			}
		}
		return convertView;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isNotify() {
		return false;
	}

	/**
	 * 设置数据，并自动通知刷新界面
	 * 
	 * @param list
	 */
	public void setData(List<T> list) {
		if (list == null)
			return;
		this.mList = list;
		notifyDataSetChanged();
	}

	private void add(T bean, boolean flag) {
		if (bean == null)
			return;
		if (mList == null)
			return;
		this.mList.add(bean);
		if (flag) {
			notifyDataSetChanged();
		}
	}

	/**
	 * 清空数据
	 */
	public void clear() {
		if (mList == null)
			return;
		this.mList.clear();
		notifyDataSetChanged();
	}

	/**
	 * 添加一条数据，并自动通知刷新界面
	 * 
	 * @param bean
	 */
	public void add(T bean) {
		add(bean, true);
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList == null ? null : mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return mList == null ? 0 : arg0;
	}

	public void remove(int location) {
		mList.remove(location);
		notifyDataSetChanged();
	}

	public void remove(T bean) {
		mList.remove(bean);
		notifyDataSetChanged();
	}

	public T getItemObject(int pos) {
		if (mList == null)
			return null;
		return mList.get(pos);
	}

	public int getListCount() {
		return mList == null ? 0 : mList.size();
	}

	public void addTop(T bean) {
		if (bean == null)
			return;
		mList.add(0, bean);
		notifyDataSetChanged();
	}

	/**
	 * 添加一些列数据，并自动刷新
	 * 
	 * @param list
	 */
	public <V extends T> void addAll(List<V> list) {
		if (mList == null)
			return;
		if (list == null)
			return;
		for (int i = 0; i < list.size(); i++) {
			add(list.get(i), false);
		}
		notifyDataSetChanged();
	}

	/**
	 * 添加一些列数据，并自动刷新
	 * 
	 * @param all
	 */
	public <V extends T> void addAll(V[] all) {
		if (all == null)
			return;
		if (mList == null)
			return;
		for (int i = 0; i < all.length; i++) {
			add(all[i], false);
		}
		notifyDataSetChanged();
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <V extends T> ArrayList<V> getItemList() {
		if (mList == null)
			return null;
		ArrayList<V> arrayList = new ArrayList<>();
		int size = mList.size();
		for (int i = 0; i < size; i++) {
			V iCharpter = (V) mList.get(i);
			arrayList.add(iCharpter);
		}
		return (ArrayList<V>) mList;
	}
	
	public Context getContext() {
		return context;
	}

	/**
	 * adapter里面的viewhodler快速构建方法 ImageView view = AbViewHolder.get(convertView,
	 * R.id.imageView);
	 * 
	 * @param view
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "hiding" })
	public <T extends View> T get(View view, int id) {
		ViewExtraInfo tag = (ViewExtraInfo) view.getTag();
		if (tag == null) {
			tag = new ViewExtraInfo();
			tag.data2 = new SparseArray<View>();
			view.setTag(tag);
		} else if (tag.data2 == null) {
			tag.data2 = new SparseArray<View>();
		}
		SparseArray<View> viewHolder = (SparseArray<View>) tag.data2;
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
	
	public <V extends T> void updateList(List<V> list){
		if (mList == null)
			return;
		if (list == null)
			return;
		mList.clear();
		for (int i = 0; i < list.size(); i++) {
			add(list.get(i), false);
		}
		notifyDataSetChanged();
	}

	/**
	 * view绑定的额外信息
	 * @author fanchen
	 * 
	 */
	public static final class ViewExtraInfo {
		public Object data1;
		public Object data2;
	}
}

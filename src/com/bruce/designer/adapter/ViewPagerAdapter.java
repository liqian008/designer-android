package com.bruce.designer.adapter;

import java.util.List;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter {

	private List<View> views;
	private Activity activity;

	public ViewPagerAdapter(List<View> views, Activity activity) {
		this.views = views;
		this.activity = activity;
	}

	/**
	 * 销毁划过的界面
	 */
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
	}

	/**
	 * 获得当前页面的个数
	 */
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = views.get(position);
		((ViewPager) container).addView(view, 0);
		return view;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

}
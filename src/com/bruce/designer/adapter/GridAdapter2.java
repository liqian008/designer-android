package com.bruce.designer.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bruce.designer.R;
import com.bruce.designer.util.DipUtil;
import com.bruce.designer.util.LogUtil;

public class GridAdapter2 extends BaseAdapter {
	public static class Item {
		public String text;
		public int resId;
	}

	private List<Item> mItems = new ArrayList<GridAdapter2.Item>();
	private Context mContext;

	public GridAdapter2(Context context) {
		// 测试数据
		for (int i = 0; i < 10; i++) {
			Item object = new Item();
			object.resId = R.drawable.ic_launcher;
			mItems.add(object);
		}
		mContext = context;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		if (convertView == null) {
//			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_album, null);
//		}
		
		int itemLength = (int) ((DipUtil
				.getScreenWidth((Activity)mContext)) / 3);
		
		LogUtil.d("====itemLength====="+itemLength);
		
		ImageView image = new ImageView(mContext); //(ImageView) convertView.findViewById(R.id.icon);
		image.setScaleType(ScaleType.CENTER_CROP);
		image.setLayoutParams(new AbsListView.LayoutParams(
				itemLength, itemLength));
		
		Item item = (Item) getItem(position);
		image.setImageResource(item.resId);
		return image;
	}
}

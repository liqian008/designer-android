package com.bruce.designer.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bruce.designer.model.AlbumSlide;
import com.bruce.designer.util.DipUtil;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.cache.ImageLoader;

public class AlbumSlidesAdapter extends BaseAdapter {

		private List<AlbumSlide> slideList;
		private Context context;
		
		public AlbumSlidesAdapter(Context context, List<AlbumSlide> slideList) {
			this.context = context;
			this.slideList = slideList;
		}
		
		@Override
		public int getCount() {
			if (slideList != null) {
				return slideList.size();
			}
			return 0;
		}

		@Override
		public AlbumSlide getItem(int position) {
			if (slideList != null) {
				return slideList.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//TODO 暂未使用convertView
			AlbumSlide albumSlide=  slideList.get(position);
			if(albumSlide!=null){
				
				//计算每个item所需的宽度
				int pxFromDp = DipUtil.calcFromDip((Activity)context, 1);
				int widthSpace = pxFromDp * 0;//该控件离边距的宽度(margin或padding)
				int width = DipUtil.getScreenWidth((Activity)context);
				int itemWidth = (width-widthSpace)/3;
				
				LogUtil.d("======widthSpace======"+widthSpace);
				LogUtil.d("======width======"+width);
				LogUtil.d("======itemWidth======"+itemWidth);
				
				FrameLayout layout = new FrameLayout(context);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
				
				int leftWidth = position%3==0?0:pxFromDp;
				int rightWidth = position%3==2?0:pxFromDp;
				
				params.setMargins(leftWidth, 0, rightWidth, pxFromDp);//边距
	            params.gravity = Gravity.TOP;
	            ImageView itemImageView = new ImageView(context);
	            itemImageView.setScaleType(ScaleType.CENTER_CROP);
	            layout.addView(itemImageView, params);
	            ImageLoader.loadImage(albumSlide.getSlideSmallImg(), itemImageView);
	            //TODO 此种方式构造的item列表的尺寸会有些许误差，待修复
	            layout.setLayoutParams(new GridView.LayoutParams(itemWidth, itemWidth));
				return layout;
			}
			return null;
		}

		public void setSlideList(List<AlbumSlide> slideList) {
			this.slideList = slideList;
		}
		
	}
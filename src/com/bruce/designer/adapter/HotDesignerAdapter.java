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

import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.User;
import com.bruce.designer.util.DipUtil;
import com.bruce.designer.util.UniversalImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HotDesignerAdapter extends BaseAdapter {

		private List<User> userList;
		private Context context;
		
		public HotDesignerAdapter(Context context, List<User> userList) {
			this.context = context;
			this.userList = userList;
		}

		@Override
		public int getCount() {
			if (userList != null) {
				return userList.size();
			}
			return 0;
		}

		@Override
		public User getItem(int position) {
			if (userList != null) {
				return userList.get(position);
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
			User designer=  userList.get(position);
			if(designer!=null){
				
				//计算每个item所需的宽度
				int pxFromDp = DipUtil.calcFromDip((Activity)context, 1);
				int widthSpace = pxFromDp * 0;//该控件离边距的宽度(margin或padding)
				int width = DipUtil.getScreenWidth((Activity)context);
				int itemWidth = (width-widthSpace)/4;
				
				FrameLayout layout = new FrameLayout(context);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
				
				int leftWidth = position%4==0?0:pxFromDp;
				int rightWidth = position%4==2?0:pxFromDp;
				
				params.setMargins(leftWidth, 0, rightWidth, pxFromDp);//边距
	            params.gravity = Gravity.CENTER;
	            ImageView itemImageView = new ImageView(context);
	            itemImageView.setScaleType(ScaleType.CENTER_CROP);
	            layout.addView(itemImageView, params);
	            ImageLoader.getInstance().displayImage(designer.getHeadImg(), itemImageView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION );
	            //TODO 此种方式构造的item列表的尺寸会有些许误差，待修复
	            layout.setLayoutParams(new GridView.LayoutParams(itemWidth, itemWidth));
	            
	            final int userIndex = position;
	            layout.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View v) {
						//跳转至个人主页
//						if(userUrlList!=null&&userUrlList.size()>0){
//							Activity_ImageBrowser.show(context, userIndex, userUrlList);
//						}
					}
				});
	            
				return layout;
			}
			return null;
		}

		public List<User> getUserList() {
			return userList;
		}

		public void setUserList(List<User> userList) {
			this.userList = userList;
		}

	}
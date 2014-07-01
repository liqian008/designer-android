package com.bruce.designer.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.api.ApiWrapper;
import com.bruce.designer.api.user.UserFansApi;
import com.bruce.designer.api.user.UserFollowsApi;
import com.bruce.designer.constants.BundleKey;
import com.bruce.designer.model.User;
import com.bruce.designer.model.UserFollow;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.ApiUtil;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.cache.ImageLoader;

public class Activity_UserFollows extends BaseActivity {
	
	private View titlebarView;

	private TextView titleView;
	
	private FollowsListAdapter followsListAdapter;

	private int userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_follows);
		
		Intent intent = getIntent();
		//获取userid
		userId =  intent.getIntExtra(BundleKey.BUNDLE_USER_INFO_ID, 0);
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("TA的关注");
		
		ListView followsListView = (ListView)findViewById(R.id.userFollows);
		followsListAdapter = new FollowsListAdapter(context, null);
		followsListView.setAdapter(followsListAdapter);
		
		//获取关注列表
		getFollows(0);
		//TODO 需要增加下拉刷新
	}
	
	
	class FollowsListAdapter extends BaseAdapter {

		private List<UserFollow> followUserList;
		private Context context;
		
		public FollowsListAdapter(Context context, List<UserFollow> followUserList) {
			this.context = context;
			this.followUserList = followUserList;
		}
		
		public List<UserFollow> getFollowUserList() {
			return followUserList;
		}

		public void setFollowUserList(List<UserFollow> followUserList) {
			this.followUserList = followUserList;
		}

		@Override
		public int getCount() {
			if (followUserList != null) {
				return followUserList.size();
			}
			return 0;
		}

		@Override
		public UserFollow getItem(int position) {
			if (followUserList != null) {
				return followUserList.get(position);
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
			if(getItem(position)!=null){
				final UserFollow user = getItem(position);
				View friendItemView = LayoutInflater.from(context).inflate(R.layout.item_friend_view, null);
				
				TextView usernameView = (TextView) friendItemView.findViewById(R.id.username);
				usernameView.setText(user.getFollowUser().getNickname());
				
				return friendItemView;
			}
			return null;
		}
	}
	
	/**
	 * 获取关注列表
	 * @param followsTailId
	 */
	private void getFollows(final int followsTailId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				
				UserFollowsApi api = new UserFollowsApi(userId);
				JsonResultBean jsonResult = ApiWrapper.invoke(context, api);
				
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(0);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	
	private Handler handler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch(msg.what){
				case 0:
					Map<String, Object> userFollowsDataMap = (Map<String, Object>) msg.obj;
					if(userFollowsDataMap!=null){
						List<UserFollow> followList = (List<UserFollow>)  userFollowsDataMap.get("followList");
						if(followList!=null&&followList.size()>0){
							followsListAdapter.setFollowUserList(followList);
							followsListAdapter.notifyDataSetChanged();
						}
					}
					break;
				default:
					break;
			}
		}
	};
	
}

package com.bruce.designer.activity;

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
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.api.ApiWrapper;
import com.bruce.designer.api.album.AlbumInfoApi;
import com.bruce.designer.api.user.UserFansApi;
import com.bruce.designer.constants.BundleKey;
import com.bruce.designer.model.UserFan;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.ApiUtil;

public class Activity_UserFans extends BaseActivity {
	
	private View titlebarView;

	private TextView titleView;
	
	private FansListAdapter fansListAdapter;

	private int userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_fans);
		
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
		titleView.setText("TA的粉丝");
		
		ListView fansListView = (ListView)findViewById(R.id.userFans);
		fansListAdapter = new FansListAdapter(context, null);
		fansListView.setAdapter(fansListAdapter);
		
		//获取关注列表
		getFans(0);
		//TODO 需要增加下拉刷新
	}
	
	
	class FansListAdapter extends BaseAdapter {

		private List<UserFan> fanUserList;
		private Context context;
		
		public FansListAdapter(Context context, List<UserFan> fanUserList) {
			this.context = context;
			this.fanUserList = fanUserList;
		}
		
		public List<UserFan> getFanUserList() {
			return fanUserList;
		}

		public void setFanUserList(List<UserFan> fanUserList) {
			this.fanUserList = fanUserList;
		}

		@Override
		public int getCount() {
			if (fanUserList != null) {
				return fanUserList.size();
			}
			return 0;
		}

		@Override
		public UserFan getItem(int position) {
			if (fanUserList != null) {
				return fanUserList.get(position);
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
				final UserFan user = getItem(position);
				View friendItemView = LayoutInflater.from(context).inflate(R.layout.item_friend_view, null);
				
				TextView usernameView = (TextView) friendItemView.findViewById(R.id.username);
				usernameView.setText(user.getFanUser().getNickname());
				
				return friendItemView;
			}
			return null;
		}
	}
	
	/**
	 * 获取关注列表
	 * @param fansTailId
	 */
	private void getFans(final int fansTailId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
//				JsonResultBean jsonResult = ApiUtil.getUserFans(userId);
				
				UserFansApi api = new UserFansApi(userId);
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
					Map<String, Object> userFansDataMap = (Map<String, Object>) msg.obj;
					if(userFansDataMap!=null){
						List<UserFan> fanList = (List<UserFan>)  userFansDataMap.get("fanList");
						if(fanList!=null&&fanList.size()>0){
							fansListAdapter.setFanUserList(fanList);
							fansListAdapter.notifyDataSetChanged();
						}
					}
					break;
				default:
					break;
			}
		}
	};
	
}

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.AppApplication;
import com.bruce.designer.R;
import com.bruce.designer.activity.Activity_UserFollows.FollowViewHolder;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.user.UserFansApi;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.UserFan;
import com.bruce.designer.model.UserFollow;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.UniversalImageUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Activity_UserFans extends BaseActivity implements OnRefreshListener2<ListView>{
	
	private View titlebarView;

	private TextView titleView;
	
	private FansListAdapter fansListAdapter;

	private int queryUserId;
	private boolean isHost;
	
	
	public static void show(Context context, int queryUserId){
		Intent intent = new Intent(context, Activity_UserFans.class);
		intent.putExtra(ConstantsKey.BUNDLE_USER_INFO_ID, queryUserId);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_fans);
		
		Intent intent = getIntent();
		//获取userid
		queryUserId =  intent.getIntExtra(ConstantsKey.BUNDLE_USER_INFO_ID, 0);
		
		//获取userid
		queryUserId = intent.getIntExtra(ConstantsKey.BUNDLE_USER_INFO_ID, 0);
		isHost = AppApplication.isHost(queryUserId);
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(listener);
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText((isHost?"我":"TA") + "的粉丝");
		
		PullToRefreshListView pullRefresh = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		ListView fansListView = pullRefresh.getRefreshableView();
		pullRefresh.setMode(Mode.PULL_FROM_START);
		pullRefresh.setOnRefreshListener(this);
		
		fansListAdapter = new FansListAdapter(context, null, null);
		fansListView.setAdapter(fansListAdapter);
		
		pullRefresh.setRefreshing(false);
		
		//获取粉丝列表
//		getFans(queryUserId);
	}

	
	class FansListAdapter extends BaseAdapter {

		private Context context;
		private List<UserFan> fanUserList;
		private Map<Integer, Boolean> fanUserMap;
		
		public FansListAdapter(Context context, List<UserFan> fanUserList, Map<Integer, Boolean> fanUserMap) {
			this.context = context;
			this.fanUserList = fanUserList;
			this.fanUserMap = fanUserMap;
		}
		
		public List<UserFan> getFanUserList() {
			return fanUserList;
		}

		public void setFanUserList(List<UserFan> fanUserList) {
			this.fanUserList = fanUserList;
		}

		public Map<Integer, Boolean> getFanUserMap() {
			return fanUserMap;
		}

		public void setFanUserMap(Map<Integer, Boolean> fanUserMap) {
			this.fanUserMap = fanUserMap;
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
			//使用convertView
			final UserFan user = getItem(position);
			FanViewHolder viewHolder = null;
			if(convertView==null){
				viewHolder = new FanViewHolder();
				if(user!=null){
					
					convertView = LayoutInflater.from(context).inflate(R.layout.item_friend_view, null);
					
					viewHolder.friendView = (View) convertView.findViewById(R.id.friendContainer);;
					viewHolder.avatarView = (ImageView) convertView.findViewById(R.id.avatar);
					viewHolder.usernameView = (TextView) convertView.findViewById(R.id.username);
					//构造关注状态
					viewHolder.btnFollow = (Button) convertView.findViewById(R.id.btnFollow);
					viewHolder.btnUnfollow = (Button) convertView.findViewById(R.id.btnUnfollow);
					
					viewHolder.btnSendMsg = (Button) convertView.findViewById(R.id.btnSendMsg);
				}
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (FanViewHolder) convertView.getTag();
			}
			
			final FanViewHolder fanViewHolder = viewHolder;
			//填充数据
			final int fanUserId = user.getFanId();
			final String fanNickname = user.getFanUser().getNickname();
			final boolean isDesigner = true;
			final boolean hasFollowed = true;
			fanViewHolder.usernameView.setText(user.getFanUser().getNickname());
			
			if(AppApplication.isHost(fanUserId)){//查看的用户为自己，需要隐藏交互按钮
				fanViewHolder.btnFollow.setVisibility(View.GONE);
				fanViewHolder.btnUnfollow.setVisibility(View.GONE);
				fanViewHolder.btnSendMsg.setVisibility(View.GONE);
			}else{
				//私信事件
				fanViewHolder.btnSendMsg.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View v) {
						Activity_MessageChat.show(context, fanUserId, fanNickname, user.getFanUser().getHeadImg());
					}
				});
			}
			
			//显示头像
			ImageLoader.getInstance().displayImage(user.getFanUser().getHeadImg(), fanViewHolder.avatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
			fanViewHolder.friendView.setOnClickListener(new OnSingleClickListener() {
				@Override
				public void onSingleClick(View view) {
					Activity_UserHome.show(context, fanUserId, fanNickname , null, isDesigner, hasFollowed);
				}
			});
			
			
			return convertView;
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
				
				UserFansApi api = new UserFansApi(queryUserId);
				ApiResult jsonResult = ApiManager.invoke(context, api);
				
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
						Map<Integer, Boolean> fanMap = (Map<Integer, Boolean>)  userFansDataMap.get("fanMap");
						if(fanList!=null&&fanList.size()>0){
							fansListAdapter.setFanUserList(fanList);
							fansListAdapter.setFanUserMap(fanMap);
							fansListAdapter.notifyDataSetChanged();
						}
					}
					break;
				default:
					break;
			}
		}
	};

	private OnClickListener listener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch (view.getId()) {
			case R.id.titlebar_return:
				finish();
				break;
			default:
				break;
			}
		}
	};
	
	
	
	/**
	 * 下拉刷新
	 */
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		//获取关注列表
		getFans(queryUserId);
	}
	
	/**
	 * 上拉刷新
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}

	
	/**
	 * viewHolder
	 * @author liqian
	 *
	 */
	static class FanViewHolder {  
		public View friendView;
		public TextView usernameView;
		public ImageView avatarView;

		// 构造关注状态
		public Button btnFollow;
		public Button btnUnfollow;
		public Button btnSendMsg;
	}
}
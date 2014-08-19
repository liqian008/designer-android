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

import com.bruce.designer.R;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.user.FollowUserApi;
import com.bruce.designer.api.user.UserFollowsApi;
import com.bruce.designer.broadcast.NotificationBuilder;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.UserFollow;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.UniversalImageUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Activity_UserFollows extends BaseActivity implements OnRefreshListener2<ListView>{

	private static final int HANDLER_FLAG_FOLLOW = 100;
	private static final int HANDLER_FLAG_UNFOLLOW = 101;
	
	private View titlebarView;

	private TextView titleView;
	
	private FollowsListAdapter followsListAdapter;

	private int userId;
	
	public static void show(Context context, int queryUserId){
		Intent intent = new Intent(context, Activity_UserFollows.class);
		intent.putExtra(ConstantsKey.BUNDLE_USER_INFO_ID, queryUserId);
		context.startActivity(intent);
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_follows);
		
		Intent intent = getIntent();
		//获取userid
		userId =  intent.getIntExtra(ConstantsKey.BUNDLE_USER_INFO_ID, 0);
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(listener);
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("TA的关注");
		
		PullToRefreshListView pullRefresh = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		ListView followsListView = pullRefresh.getRefreshableView();
		pullRefresh.setMode(Mode.PULL_FROM_START);
		pullRefresh.setOnRefreshListener(this);
		
//		ListView followsListView = (ListView)findViewById(R.id.userFollows);
		followsListAdapter = new FollowsListAdapter(context, null, null);
		followsListView.setAdapter(followsListAdapter);
		
		pullRefresh.setRefreshing(true);
		//获取关注列表
		getFollows(userId);
		//TODO 需要增加下拉刷新
	}
	
	
	class FollowsListAdapter extends BaseAdapter {

		private List<UserFollow> followUserList;
		private Map<Integer, Boolean> followUserMap;
		private Context context;
		
		public FollowsListAdapter(Context context, List<UserFollow> followUserList, Map<Integer, Boolean> followUserMap) {
			this.context = context;
			this.followUserList = followUserList;
			this.followUserMap = followUserMap;
		}
		
		public List<UserFollow> getFollowUserList() {
			return followUserList;
		}

		public void setFollowUserList(List<UserFollow> followUserList) {
			this.followUserList = followUserList;
		}

		public Map<Integer, Boolean> getFollowUserMap() {
			return followUserMap;
		}

		public void setFollowUserMap(Map<Integer, Boolean> followUserMap) {
			this.followUserMap = followUserMap;
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
			//使用convertView
			final UserFollow user = getItem(position);
			FollowViewHolder viewHolder = null;
			if(convertView==null){
				viewHolder = new FollowViewHolder();
				if(user!=null){
					
					convertView = LayoutInflater.from(context).inflate(R.layout.item_friend_view, null);
					
					viewHolder.friendView = (View) convertView.findViewById(R.id.friendContainer);;
					viewHolder.avatarView = (ImageView) convertView.findViewById(R.id.avatar);
					viewHolder.usernameView = (TextView) convertView.findViewById(R.id.username);
					//构造关注状态
					viewHolder.btnFollow = (Button) convertView.findViewById(R.id.btnFollow);
					viewHolder.btnUnfollow = (Button) convertView.findViewById(R.id.btnUnfollow);
				}
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (FollowViewHolder) convertView.getTag();
			}
			
			final FollowViewHolder followViewHolder = viewHolder;
			//填充数据
			final int followUserId = user.getFollowId();
			final String followNickname = user.getFollowUser().getNickname();
			final boolean isDesigner = true;
			final boolean hasFollowed = true;
			
			viewHolder.usernameView.setText(user.getFollowUser().getNickname());
			
			//显示头像
			ImageLoader.getInstance().displayImage(user.getFollowUser().getHeadImg(), viewHolder.avatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
			followViewHolder.friendView.setOnClickListener(new OnSingleClickListener() {
				@Override
				public void onSingleClick(View view) {
					Activity_UserHome.show(context, followUserId, followNickname , null, isDesigner, hasFollowed);
				}
			});
			if(followUserMap!=null){
				if(Boolean.TRUE.equals(followUserMap.get(followUserId))){
					followViewHolder.btnFollow.setVisibility(View.GONE);
					followViewHolder.btnUnfollow.setVisibility(View.VISIBLE);
				}else if(Boolean.FALSE.equals(followUserMap.get(followUserId))){
					followViewHolder.btnFollow.setVisibility(View.VISIBLE);
					followViewHolder.btnUnfollow.setVisibility(View.GONE);
				}
			}
			//关注事件
			followViewHolder.btnFollow.setOnClickListener(new OnSingleClickListener() {
				@Override
				public void onSingleClick(View v) {
					followViewHolder.btnUnfollow.setVisibility(View.VISIBLE);
					followViewHolder.btnFollow.setVisibility(View.GONE);
					new Thread(new Runnable(){
						@Override
						public void run() {
							FollowUserApi api = new FollowUserApi(followUserId, 1);
							ApiResult apiResult = ApiManager.invoke(context, api);
							if(apiResult!=null&&apiResult.getResult()==1){
								handler.obtainMessage(HANDLER_FLAG_FOLLOW).sendToTarget();
							}
						}
					}).start();
				}
			});
			//取消关注事件
			followViewHolder.btnUnfollow.setOnClickListener(new OnSingleClickListener() {
				@Override
				public void onSingleClick(View v) {
					followViewHolder.btnFollow.setVisibility(View.VISIBLE);
					followViewHolder.btnUnfollow.setVisibility(View.GONE);
					new Thread(new Runnable(){
						@Override
						public void run() {
							FollowUserApi api = new FollowUserApi(followUserId, 0);
							ApiResult apiResult = ApiManager.invoke(context, api);
							if(apiResult!=null&&apiResult.getResult()==1){
								handler.obtainMessage(HANDLER_FLAG_UNFOLLOW).sendToTarget();
							}
						}
					}).start();
				}
			});
			return convertView;
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
					Map<String, Object> userFollowsDataMap = (Map<String, Object>) msg.obj;
					if(userFollowsDataMap!=null){
						List<UserFollow> followList = (List<UserFollow>)  userFollowsDataMap.get("followList");
						Map<Integer, Boolean> followMap = (Map<Integer, Boolean>)  userFollowsDataMap.get("followMap");
						if(followList!=null&&followList.size()>0){
							followsListAdapter.setFollowUserList(followList);
							followsListAdapter.setFollowUserMap(followMap);
							followsListAdapter.notifyDataSetChanged();
						}
					}
					break;
				case HANDLER_FLAG_FOLLOW:
					//广播
					NotificationBuilder.createNotification(context, "成功关注");
					break;
				case HANDLER_FLAG_UNFOLLOW:
					//广播
					NotificationBuilder.createNotification(context, "取消关注成功");
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
		getFollows(userId);
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
	static class FollowViewHolder {
		public View friendView;
		public TextView usernameView;
		public ImageView avatarView;

		// 构造关注状态
		public Button btnFollow;
		public Button btnUnfollow;
	}
	
}

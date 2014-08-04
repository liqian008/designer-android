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
import com.bruce.designer.api.user.UserFansApi;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.UserFan;
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

	private int userId;
	
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
		userId =  intent.getIntExtra(ConstantsKey.BUNDLE_USER_INFO_ID, 0);
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(listener);
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("TA的粉丝");
		
		PullToRefreshListView pullRefresh = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		ListView fansListView = pullRefresh.getRefreshableView();
		pullRefresh.setMode(Mode.PULL_FROM_START);
		pullRefresh.setOnRefreshListener(this);
		
//		ListView fansListView = (ListView)findViewById(R.id.userFans);
		fansListAdapter = new FansListAdapter(context, null, null);
		fansListView.setAdapter(fansListAdapter);
		
		pullRefresh.setRefreshing(true);
		
		//获取粉丝列表
		getFans(userId);
		//TODO 需要增加下拉刷新
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
			//TODO 暂未使用convertView
			if(getItem(position)!=null){
				final UserFan user = getItem(position);
				
				final int fanUserId = user.getFanId();
				final String fanNickname = user.getFanUser().getNickname();
				final boolean isDesigner = true;
				final boolean hasFollowed = true;
				
				View friendItemView = LayoutInflater.from(context).inflate(R.layout.item_friend_view, null);
				
				TextView usernameView = (TextView) friendItemView.findViewById(R.id.username);
				usernameView.setText(user.getFanUser().getNickname());
				
				ImageView avatarView = (ImageView) friendItemView.findViewById(R.id.avatar);
				//显示头像
				ImageLoader.getInstance().displayImage(user.getFanUser().getHeadImg(), avatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
				
				
				View friendView = (View) friendItemView.findViewById(R.id.friendContainer);
				friendView.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View view) {
						Activity_UserHome.show(context, fanUserId, fanNickname , null, isDesigner, hasFollowed);
					}
				});
				
				//构造关注状态
				Button btnFollow = (Button) friendItemView.findViewById(R.id.btnFollow);
				Button btnUnfollow = (Button) friendItemView.findViewById(R.id.btnUnfollow);
				if(fanUserMap!=null){
					if(Boolean.TRUE.equals(fanUserMap.get(fanUserId))){
						btnFollow.setVisibility(View.GONE);
						btnUnfollow.setVisibility(View.VISIBLE);
					}else if(Boolean.FALSE.equals(fanUserMap.get(fanUserId))){
						btnFollow.setVisibility(View.VISIBLE);
						btnUnfollow.setVisibility(View.GONE);
					}
				}
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
		getFans(userId);
	}
	
	/**
	 * 上拉刷新
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}

}

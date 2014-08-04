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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.adapter.AlbumSlidesAdapter;
import com.bruce.designer.adapter.DesignerAlbumsAdapter;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.album.AlbumListApi;
import com.bruce.designer.api.user.FollowUserApi;
import com.bruce.designer.api.user.UserInfoApi;
import com.bruce.designer.broadcast.NotificationBuilder;
import com.bruce.designer.constants.ConstantDesigner;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.User;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.UniversalImageUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 用户的主页
 * @author liqian
 *
 */
public class Activity_UserHome extends BaseActivity implements OnRefreshListener2<ListView>{
	
	private static final int HANDLER_FLAG_USERINFO = 1;
	private static final int HANDLER_FLAG_SLIDE = 2;
	
	private static final int HANDLER_FLAG_FOLLOW = 100;
	private static final int HANDLER_FLAG_UNFOLLOW = 101;
	
	private View titlebarView;

	private TextView titleView;
	/*用户昵称*/
	private TextView nicknameView;
	/*设计师头像*/
	private ImageView avatarView;
	
	private View followsView;
	private View fansView;
	/*粉丝个数*/
	private int fansCount = 0;
	/*关注人个数*/
	private int followsCount = 0;
	
	private Button btnFollow;
	private Button btnUnfollow;
	private Button btnUserInfo;
	
	private TextView followsNumView;
	private TextView fansNumView;
	
	private int queryUserId;
	private int hostId = 100007;

	private DesignerAlbumsAdapter albumListAdapter;

	public AlbumSlidesAdapter slideAdapter;
	
	private PullToRefreshListView pullRefreshView;
	
	private int albumTailId = 0;
	
	
	public static void show(Context context, int userId, String nickname, String avatar, boolean isDesigner, boolean hasFollowed){
		Intent intent = new Intent(context, Activity_UserHome.class);
		intent.putExtra(ConstantsKey.BUNDLE_USER_INFO_ID, userId);
		intent.putExtra(ConstantsKey.BUNDLE_USER_INFO_NICKNAME, nickname);
		intent.putExtra(ConstantsKey.BUNDLE_USER_INFO_AVATAR, avatar);
		intent.putExtra(ConstantsKey.BUNDLE_USER_INFO_ISDESIGNER, isDesigner);
		
		context.startActivity(intent);
	}
	
	
	private Handler handler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch(msg.what){
				case HANDLER_FLAG_USERINFO:
					Map<String, Object> userinfoDataMap = (Map<String, Object>) msg.obj;
					if(userinfoDataMap!=null){
						User userinfo = (User) userinfoDataMap.get("userinfo");
						fansCount = (Integer) userinfoDataMap.get("fansCount");
						followsCount = (Integer) userinfoDataMap.get("followsCount");
						boolean hasFollowed = (Boolean) userinfoDataMap.get("hasFollowed");
						
						if(userinfo!=null&&userinfo.getId()>0){
							//设计师or用户
							if(userinfo.getDesignerStatus()!=null&&userinfo.getDesignerStatus()==ConstantDesigner.DESIGNER_APPLY_APPROVED){//设计师状态
								titleView.setText("设计师");
								if(hasFollowed){
									btnFollow.setVisibility(View.GONE);
									btnUnfollow.setVisibility(View.VISIBLE);
								}else{
									btnUnfollow.setVisibility(View.GONE);
									btnFollow.setVisibility(View.VISIBLE);
								}
							}else{
								titleView.setText("用户");
							}
							fansNumView.setText(String.valueOf(fansCount));
							followsNumView.setText(String.valueOf(followsCount));
						}
					}
					break;
				case HANDLER_FLAG_SLIDE:
					pullRefreshView.onRefreshComplete();
					Map<String, Object> albumsDataMap = (Map<String, Object>) msg.obj;
					if(albumsDataMap!=null){
						List<Album> albumList = (List<Album>) albumsDataMap.get("albumList");
						Integer fromTailId = (Integer) albumsDataMap.get("fromTailId");
						Integer newTailId = (Integer) albumsDataMap.get("newTailId");
						if(albumList!=null&&albumList.size()>0){
							
							if(newTailId!=null&&newTailId>0){//还有可加载的数据
								albumTailId = newTailId;
							}else{
								albumTailId = 0;
								pullRefreshView.setMode(Mode.DISABLED);//禁用上拉刷新
							}
							List<Album> oldAlbumList = albumListAdapter.getAlbumList();
							if(oldAlbumList==null){
								oldAlbumList = new ArrayList<Album>();
							}
							//判断加载位置，以确定是list增量还是覆盖
							boolean fallloadAppend = fromTailId!=null&&fromTailId>0;
							if(fallloadAppend){//上拉加载更多，需添加至list的结尾
								oldAlbumList.addAll(albumList);
							}else{//下拉加载，需覆盖原数据
								oldAlbumList = null;
								oldAlbumList = albumList; 
							}
							albumListAdapter.setAlbumList(oldAlbumList);
							albumListAdapter.notifyDataSetChanged();
						}
					}
					break;
				case HANDLER_FLAG_FOLLOW:
					//广播
					NotificationBuilder.createNotification(context, "成功关注");
					//粉丝数量+1
					fansCount = fansCount+1;
					fansNumView.setText(String.valueOf(fansCount));
					break;
				case HANDLER_FLAG_UNFOLLOW:
					//广播
					NotificationBuilder.createNotification(context, "取消关注成功");
					//粉丝数量-1
					if(fansCount>0) fansCount = fansCount-1;
					fansNumView.setText(String.valueOf(fansCount));
					break;
				default:
					break;
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		
		Intent intent = getIntent();
		//从intent中获取参数
		queryUserId =  intent.getIntExtra(ConstantsKey.BUNDLE_USER_INFO_ID, 0);
		String userNickname = intent.getStringExtra(ConstantsKey.BUNDLE_USER_INFO_NICKNAME);
		String userAvatar = intent.getStringExtra(ConstantsKey.BUNDLE_USER_INFO_AVATAR);
		boolean isDesigner = intent.getBooleanExtra(ConstantsKey.BUNDLE_USER_INFO_ISDESIGNER, false);
//		boolean hasFollowed = intent.getBooleanExtra(ConstantsKey.BUNDLE_USER_INFO_HASFOLLOWED, false);
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(onclickListener);
		
		titleView = (TextView) findViewById(R.id.titlebar_title);
		if(isDesigner){
			titleView.setText("设计师");
		}else{
			titleView.setText("用户");
		}
		
		pullRefreshView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullRefreshView.setMode(Mode.PULL_FROM_END);
		pullRefreshView.setOnRefreshListener(this);
		ListView albumListView = pullRefreshView.getRefreshableView();
		albumListAdapter = new DesignerAlbumsAdapter(context, null);
		albumListView.setAdapter(albumListAdapter);
		
		//把个人资料的layout作为listview的headerT
		LayoutInflater layoutInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View headerView = layoutInflate.inflate(R.layout.user_home_head, null);
		albumListView.addHeaderView(headerView);
		
		
		avatarView = (ImageView) headerView.findViewById(R.id.avatar);
		//显示头像
		ImageLoader.getInstance().displayImage(userAvatar, avatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
		//显示昵称
		nicknameView = (TextView) headerView.findViewById(R.id.txtNickname);
		nicknameView.setText(userNickname);
		
		fansView = (View) headerView.findViewById(R.id.fansContainer);
		fansNumView = (TextView) headerView.findViewById(R.id.txtFansNum);
		fansView.setOnClickListener(onclickListener);
		
		followsView = (View) headerView.findViewById(R.id.followsContainer);
		followsNumView = (TextView) headerView.findViewById(R.id.txtFollowsNum);
		followsView.setOnClickListener(onclickListener);
		
		View snsBtnContainer =  (View) headerView.findViewById(R.id.snsBtnContainer);
		if(hostId==queryUserId){//查看的用户为自己，需要隐藏交互按钮
			snsBtnContainer.setVisibility(View.GONE);
		}else{
			snsBtnContainer.setVisibility(View.VISIBLE);
		}
		
		btnFollow =  (Button) headerView.findViewById(R.id.btnFollow);
		btnUnfollow =  (Button) headerView.findViewById(R.id.btnUnfollow);
		
		btnUserInfo =  (Button) headerView.findViewById(R.id.btnUserInfo);
		
		btnFollow.setOnClickListener(onclickListener);
		btnUnfollow.setOnClickListener(onclickListener);
		btnUserInfo.setOnClickListener(onclickListener);
		
		//获取个人资料详情
		getUserinfo(queryUserId);
		//获取个人专辑
		getAlbums(queryUserId, 0);
	}
	
	
	private void getUserinfo(final int userId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				
				UserInfoApi api = new UserInfoApi(userId);
				ApiResult apiResult = ApiManager.invoke(context, api);
				
				
				if(apiResult!=null&&apiResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_USERINFO);
					message.obj = apiResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	
	private void getAlbums(final int queryUserId, final int albumTailId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				
				AlbumListApi api = new AlbumListApi(queryUserId, albumTailId);
				ApiResult jsonResult = ApiManager.invoke(context, api);
				
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_SLIDE);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	private OnClickListener onclickListener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {

			switch (view.getId()) {
			case R.id.titlebar_return:
				finish();
				break;
			case R.id.followsContainer:
				Activity_UserFollows.show(context, queryUserId);
				break;
			case R.id.fansContainer:
				Activity_UserFans.show(context, queryUserId);
				break;
			case R.id.btnFollow:
				btnUnfollow.setVisibility(View.VISIBLE);
				btnFollow.setVisibility(View.GONE);
				new Thread(new Runnable(){
					@Override
					public void run() {
						FollowUserApi api = new FollowUserApi(queryUserId, 1);
						ApiResult apiResult = ApiManager.invoke(context, api);
						if(apiResult!=null&&apiResult.getResult()==1){
							handler.obtainMessage(HANDLER_FLAG_FOLLOW).sendToTarget();
						}
					}
				}).start();
			case R.id.btnUnfollow:	
				btnFollow.setVisibility(View.VISIBLE);
				btnUnfollow.setVisibility(View.GONE);
				new Thread(new Runnable(){
					@Override
					public void run() {
						FollowUserApi api = new FollowUserApi(queryUserId, 0);
						ApiResult apiResult = ApiManager.invoke(context, api);
						if(apiResult!=null&&apiResult.getResult()==1){
							handler.obtainMessage(HANDLER_FLAG_UNFOLLOW).sendToTarget();
						}
					}
				}).start();
				break;
			case R.id.btnUserInfo:
				Activity_UserEdit.show(context, queryUserId);
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
		//获取个人资料详情
		getUserinfo(queryUserId);
		//获取个人专辑
		getAlbums(queryUserId, 0);
	}
	
	/**
	 * 上拉刷新
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		//加载更多专辑信息
		getAlbums(queryUserId, albumTailId);
	}
	
}

package com.bruce.designer.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.AppApplication;
import com.bruce.designer.R;
import com.bruce.designer.adapter.AlbumSlidesAdapter;
import com.bruce.designer.adapter.DesignerAlbumsAdapter;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.album.AlbumListApi;
import com.bruce.designer.api.user.PostFollowApi;
import com.bruce.designer.api.user.UserInfoApi;
import com.bruce.designer.broadcast.NotificationBuilder;
import com.bruce.designer.constants.ConstantDesigner;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.db.album.AlbumDB;
import com.bruce.designer.listener.IOnAlbumListener;
import com.bruce.designer.listener.OnAlbumListener;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.User;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.UiUtil;
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
	
	private static final int HANDLER_FLAG_USERINFO_RESULT = 1;
	private static final int HANDLER_FLAG_SLIDE_RESULT = 2;
	
	private static final int HANDLER_FLAG_FOLLOW_RESULT = 100;
	private static final int HANDLER_FLAG_UNFOLLOW_RESULT = 101;
	
	private View mainView;//主view
	private View titlebarView;

	private TextView titleView;
	/*用户昵称*/
	private TextView nicknameView;
	/*设计师头像*/
	private ImageView avatarView;
	
	private View followsContainer;
	private View fansContainer;
//	private View albumsContainer;
	
	/*专辑个数*/
	private int albumsCount = 0;
	/*粉丝个数*/
	private int fansCount = 0;
	/*关注人个数*/
	private int followsCount = 0;
	
	private Button btnFollow, btnUnfollow, btnSendMsg, btnUserInfo;
	
	private TextView albumsNumView, followsNumView, fansNumView;
	
	private TextView albumsTabTitle;
	
	private int queryUserId;
	private String queryUserNickname;
	private String queryUserAvatar;

	private DesignerAlbumsAdapter albumListAdapter;

	public AlbumSlidesAdapter slideAdapter;
	
	private PullToRefreshListView pullRefreshView;
	
	private int albumTailId = 0;
	private boolean isDesigner;
	
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
			ApiResult apiResult = (ApiResult) msg.obj;
			boolean successResult = (apiResult!=null&&apiResult.getResult()==1);
			
			switch(msg.what){
				case HANDLER_FLAG_USERINFO_RESULT:
					pullRefreshView.onRefreshComplete();
					if(successResult){
						Map<String, Object> userinfoDataMap = (Map<String, Object>) apiResult.getData();
						if(userinfoDataMap!=null){
							User userinfo = (User) userinfoDataMap.get("userinfo");
							albumsCount = (Integer) userinfoDataMap.get("albumsCount");
							fansCount = (Integer) userinfoDataMap.get("fansCount");
							followsCount = (Integer) userinfoDataMap.get("followsCount");
							boolean hasFollowed = (Boolean) userinfoDataMap.get("hasFollowed");
							
							if(userinfo!=null&&userinfo.getId()>0){
								//设计师or用户
								if(userinfo.getDesignerStatus()!=null&&userinfo.getDesignerStatus()==ConstantDesigner.DESIGNER_APPLY_APPROVED){//设计师状态
									titleView.setText("设计师");
									albumsTabTitle.setText("专辑列表");
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
								albumsNumView.setText(String.valueOf(albumsCount));
								fansNumView.setText(String.valueOf(fansCount));
								followsNumView.setText(String.valueOf(followsCount));
							}
						}
					}else{
						UiUtil.showShortToast(context, "获取个人资料失败，请重试");
					}
					break;
				case HANDLER_FLAG_SLIDE_RESULT:
					pullRefreshView.onRefreshComplete();
					if(successResult){
						Map<String, Object> albumsDataMap = (Map<String, Object>) apiResult.getData();
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
					}else{
						UiUtil.showShortToast(context, "获取专辑数据失败，请重试");
					}
					break;
				case HANDLER_FLAG_FOLLOW_RESULT:
					if(successResult){
						//粉丝数量+1
						fansCount = fansCount+1;
						fansNumView.setText(String.valueOf(fansCount));
					}
					//广播
					NotificationBuilder.createNotification(context, successResult?"您已成功关注":"关注失败");
					break;
				case HANDLER_FLAG_UNFOLLOW_RESULT:
					//广播
					NotificationBuilder.createNotification(context, successResult?"取消关注成功":"取消关注失败");
					if(successResult){
						//粉丝数量-1
						if(fansCount>0) fansCount = fansCount-1;
						fansNumView.setText(String.valueOf(fansCount));
					}
					break;
				case IOnAlbumListener.HANDLER_FLAG_LIKE_POST_RESULT: //赞成功
					int likedAlbumId = (Integer) msg.obj;
					AlbumDB.updateLikeStatus(context, likedAlbumId, 1, 1);//更新db状态
					//更新ui展示
					List<Album> albumList4Like = albumListAdapter.getAlbumList();
					if(albumList4Like!=null&&albumList4Like.size()>0){
						for(Album album: albumList4Like){
							if(album.getId()!=null&&album.getId()==likedAlbumId){
								album.setLike(true);
								long likeCount = album.getLikeCount();
								album.setLikeCount(likeCount+1);
								break;
							}
						}
						albumListAdapter.notifyDataSetChanged();
					}
					//发送广播
					NotificationBuilder.createNotification(context, "赞操作成功...");
					break;
				case IOnAlbumListener.HANDLER_FLAG_FAVORITE_POST_RESULT: //收藏成功
					int favoritedAlbumId = (Integer) msg.obj;
					AlbumDB.updateFavoriteStatus(context, favoritedAlbumId, 1, 1);//更新db状态
					//更新ui展示
					List<Album> albumList4Favorite = albumListAdapter.getAlbumList();
					if(albumList4Favorite!=null&&albumList4Favorite.size()>0){
						for(Album album: albumList4Favorite){
							if(album.getId()!=null&&album.getId()==favoritedAlbumId){
								long favoriteCount = album.getFavoriteCount();
								album.setFavoriteCount(favoriteCount+1);
								album.setFavorite(true);
								break;
							}
						}
						albumListAdapter.notifyDataSetChanged();
					}
					//发送广播
					NotificationBuilder.createNotification(context, "收藏成功...");
					break;
				case IOnAlbumListener.HANDLER_FLAG_UNFAVORITE_POST_RESULT: //取消收藏成功
					int unfavoritedAlbumId = (Integer) msg.obj; 
					AlbumDB.updateFavoriteStatus(context, unfavoritedAlbumId, 0, -1);//更新db状态
					//更新ui展示
					List<Album> albumList = albumListAdapter.getAlbumList();
					if(albumList!=null&&albumList.size()>0){
						for(Album album: albumList){
							if(album.getId()!=null&&album.getId()==unfavoritedAlbumId){
								long favoriteCount = album.getFavoriteCount();
								album.setFavoriteCount(favoriteCount-1);
								album.setFavorite(false);
								break;
							}
						}
						albumListAdapter.notifyDataSetChanged();
					}
					//发送广播
					NotificationBuilder.createNotification(context, "取消收藏成功...");
					break;
					
				default:
					break;
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainView = inflater.inflate(R.layout.activity_user_home, null);
		setContentView(mainView);
		
		Intent intent = getIntent();
		//从intent中获取参数
		queryUserId =  intent.getIntExtra(ConstantsKey.BUNDLE_USER_INFO_ID, 0);
		queryUserNickname = intent.getStringExtra(ConstantsKey.BUNDLE_USER_INFO_NICKNAME);
		queryUserAvatar = intent.getStringExtra(ConstantsKey.BUNDLE_USER_INFO_AVATAR); 
		isDesigner = intent.getBooleanExtra(ConstantsKey.BUNDLE_USER_INFO_ISDESIGNER, false);
//		boolean hasFollowed = intent.getBooleanExtra(ConstantsKey.BUNDLE_USER_INFO_HASFOLLOWED, false);
		
		//init view
		titlebarView = mainView.findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(onclickListener); 
		
		titleView = (TextView) mainView.findViewById(R.id.titlebar_title);
		if(isDesigner){
			titleView.setText("设计师");
		}else{
			titleView.setText("用户");
		}
		
		pullRefreshView = (PullToRefreshListView) mainView.findViewById(R.id.pull_refresh_list);
		pullRefreshView.setMode(Mode.BOTH);
		pullRefreshView.setOnRefreshListener(this);
		ListView albumListView = pullRefreshView.getRefreshableView();
		albumListAdapter = new DesignerAlbumsAdapter(context, null, new OnAlbumListener(context, handler, mainView));
		albumListView.setAdapter(albumListAdapter);
		
		//把个人资料的layout作为listview的headerT
		View headerView = inflater.inflate(R.layout.user_home_head, null);
		albumListView.addHeaderView(headerView);
		
		
		avatarView = (ImageView) headerView.findViewById(R.id.avatar);
		//显示头像
		ImageLoader.getInstance().displayImage(queryUserAvatar, avatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
		//显示昵称
		nicknameView = (TextView) headerView.findViewById(R.id.txtNickname);
		nicknameView.setText(queryUserNickname);
		
		
		albumsTabTitle = (TextView) headerView.findViewById(R.id.albumsTabTitle);
		if(!isDesigner){
			albumsTabTitle.setText("非设计师身份，无作品辑");
		}
		
//		albumsContainer = (View) headerView.findViewById(R.id.albumsContainer);
		albumsNumView = (TextView) headerView.findViewById(R.id.txtAlbumsNum);
		
		fansContainer = (View) headerView.findViewById(R.id.fansContainer);
		fansNumView = (TextView) headerView.findViewById(R.id.txtFansNum);
		fansContainer.setOnClickListener(onclickListener);
		
		followsContainer = (View) headerView.findViewById(R.id.followsContainer);
		followsNumView = (TextView) headerView.findViewById(R.id.txtFollowsNum);
		followsContainer.setOnClickListener(onclickListener);
		
		btnFollow =  (Button) headerView.findViewById(R.id.btnFollow);
		btnUnfollow =  (Button) headerView.findViewById(R.id.btnUnfollow);
		btnSendMsg =  (Button) headerView.findViewById(R.id.btnSendMsg);
		btnUserInfo =  (Button) headerView.findViewById(R.id.btnUserInfo);
		
		btnFollow.setOnClickListener(onclickListener);
		btnUnfollow.setOnClickListener(onclickListener);
		btnSendMsg.setOnClickListener(onclickListener);
		btnUserInfo.setOnClickListener(onclickListener);
		
		View followBtnContainer = (View) headerView.findViewById(R.id.followBtnContainer);
		if(AppApplication.isHost(queryUserId)){//查看的用户为自己，需要隐藏交互按钮
			followBtnContainer.setVisibility(View.GONE);
			btnSendMsg.setVisibility(View.GONE);
		}else{
			followBtnContainer.setVisibility(View.VISIBLE);
			btnSendMsg.setVisibility(View.VISIBLE);
		}
		
//		//获取个人资料详情
//		getUserinfo(queryUserId);
//		//获取个人专辑
//		getAlbums(queryUserId, 0);
		pullRefreshView.setRefreshing(false);
	}
	
	
	private void getUserinfo(final int userId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				UserInfoApi api = new UserInfoApi(userId);
				ApiResult apiResult = ApiManager.invoke(context, api);
				message = handler.obtainMessage(HANDLER_FLAG_USERINFO_RESULT);
				message.obj = apiResult;
				message.sendToTarget();
				
//				if(apiResult!=null&&apiResult.getResult()==1){
//					message = handler.obtainMessage(HANDLER_FLAG_USERINFO_RESULT);
//					message.obj = apiResult.getData();
//					message.sendToTarget();
//				}
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
				ApiResult apiResult = ApiManager.invoke(context, api);
				message = handler.obtainMessage(HANDLER_FLAG_SLIDE_RESULT);
				message.obj = apiResult;
				message.sendToTarget();
//				if(jsonResult!=null&&jsonResult.getResult()==1){
//					message = handler.obtainMessage(HANDLER_FLAG_SLIDE_RESULT);
//					message.obj = jsonResult.getData();
//					message.sendToTarget();
//				}
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
						PostFollowApi api = new PostFollowApi(queryUserId, 1);
						ApiResult apiResult = ApiManager.invoke(context, api);
						if(apiResult!=null&&apiResult.getResult()==1){
							handler.obtainMessage(HANDLER_FLAG_FOLLOW_RESULT).sendToTarget();
						}
					}
				}).start();
				break;
			case R.id.btnUnfollow:
				btnFollow.setVisibility(View.VISIBLE);
				btnUnfollow.setVisibility(View.GONE);
				new Thread(new Runnable(){
					@Override
					public void run() {
						PostFollowApi api = new PostFollowApi(queryUserId, 0);
						ApiResult apiResult = ApiManager.invoke(context, api);
						if(apiResult!=null&&apiResult.getResult()==1){
							handler.obtainMessage(HANDLER_FLAG_UNFOLLOW_RESULT).sendToTarget();
						}
					}
				}).start();
				break;
			case R.id.btnSendMsg://发私信
				Activity_MessageChat.show(context, queryUserId, queryUserNickname, queryUserAvatar);
				break;
			case R.id.btnUserInfo://个人资料页
				Activity_UserInfo.show(context, queryUserId);
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

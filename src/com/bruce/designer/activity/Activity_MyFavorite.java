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
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.AppApplication;
import com.bruce.designer.R;
import com.bruce.designer.adapter.AlbumSlidesAdapter;
import com.bruce.designer.adapter.DesignerAlbumsAdapter;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.album.FavoriteAlbumsListApi;
import com.bruce.designer.broadcast.NotificationBuilder;
import com.bruce.designer.constants.Config;
import com.bruce.designer.handler.DesignerHandler;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.UiUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 我的收藏列表
 * @author liqian
 *
 */
public class Activity_MyFavorite extends BaseActivity implements OnRefreshListener2<ListView>{
	
	private static final int HANDLER_FLAG_SLIDE_RESULT = 1;
	
	private static final int HANDLER_FLAG_UNFAVORITE_POST = 11;
	
	private View titlebarView;

	private TextView titleView;
	
	private DesignerAlbumsAdapter albumListAdapter;

	public AlbumSlidesAdapter slideAdapter;
	
	private PullToRefreshListView pullRefreshView;
	
	private int favoriteTailId = 0;
	
	private Handler handler;

	private OnClickListener onClickListener;
	
	public static void show(Context context){
		if(!AppApplication.isGuest()){//游客木有个人主页
			Intent intent = new Intent(context, Activity_MyFavorite.class);
			context.startActivity(intent);
		}else{
			UiUtil.showShortToast(context, "游客身份无法查看收藏，请先登录");
		}
	}
	
	private Handler initHandler(){
		Handler handler = new DesignerHandler(context){
			@SuppressWarnings("unchecked")
			public void processHandlerMessage(Message msg) {
				ApiResult apiResult = (ApiResult) msg.obj;
				boolean successResult = (apiResult!=null&&apiResult.getResult()==1);
				switch(msg.what){
					case HANDLER_FLAG_SLIDE_RESULT:
						pullRefreshView.onRefreshComplete();
						if(successResult){
							Map<String, Object> albumsDataMap = (Map<String, Object>) msg.obj;
							if(albumsDataMap!=null){
								List<Album> albumList = (List<Album>) albumsDataMap.get("albumList");
								Integer fromTailId = (Integer) albumsDataMap.get("fromTailId");
								Integer newTailId = (Integer) albumsDataMap.get("newTailId");
								if(albumList!=null&&albumList.size()>0){
									
									if(newTailId!=null&&newTailId>0){//还有可加载的数据
										favoriteTailId = newTailId;
										pullRefreshView.setMode(Mode.BOTH);
									}else{
										favoriteTailId = 0;
										pullRefreshView.setMode(Mode.PULL_FROM_START);
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
										//TODO saveToDB
										
										
										oldAlbumList = null;
										oldAlbumList = albumList;
									}
									albumListAdapter.setAlbumList(oldAlbumList);
									albumListAdapter.notifyDataSetChanged();
								}
							}
						}else{
							//UiUtil.showShortToast(context, "获取收藏数据失败，请重试");
							UiUtil.showShortToast(context, Config.RESPONSE_ERROR);
						}
						break;
					case HANDLER_FLAG_UNFAVORITE_POST: //取消收藏成功
						NotificationBuilder.createNotification(context, "取消收藏成功...");
						break;
					default:
						break;
				}
			};
		};
		return handler;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_favorites);
		handler = initHandler();
		onClickListener = initListener();
		
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(onClickListener);
		
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("我的收藏");
		
		pullRefreshView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullRefreshView.setMode(Mode.PULL_FROM_START);
		pullRefreshView.setOnRefreshListener(this);
		ListView albumListView = pullRefreshView.getRefreshableView();
		albumListAdapter = new DesignerAlbumsAdapter(context, null);
		albumListView.setAdapter(albumListAdapter);
		
		//获取我的收藏数据
		getMyFavoriteAlbums(0);
	}
	
	/**
	 * 下拉刷新
	 */
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		//获取个人专辑
		getMyFavoriteAlbums(0);
	}
	
	/**
	 * 上拉刷新
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		//加载更多专辑信息
		getMyFavoriteAlbums(favoriteTailId);
	}
	
	private void getMyFavoriteAlbums(final int favoriteTailId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				
				FavoriteAlbumsListApi api = new FavoriteAlbumsListApi(favoriteTailId);
				ApiResult apiResult = ApiManager.invoke(context, api);
				message = handler.obtainMessage(HANDLER_FLAG_SLIDE_RESULT);
				message.obj = apiResult;
				message.sendToTarget();
				
				
//				if(apiResult!=null&&apiResult.getResult()==1){
//					message = handler.obtainMessage(HANDLER_FLAG_SLIDE_RESULT);
//					message.obj = apiResult.getData();
//					message.sendToTarget();
//				}
			}
		});
		thread.start();
	}
	
	
	private OnClickListener initListener(){
		OnClickListener listener = new OnSingleClickListener() {
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
		return listener;
	}
	
}

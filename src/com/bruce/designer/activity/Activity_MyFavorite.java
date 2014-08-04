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

import com.bruce.designer.R;
import com.bruce.designer.adapter.AlbumSlidesAdapter;
import com.bruce.designer.adapter.DesignerAlbumsAdapter;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.album.FavoriteAlbumsListApi;
import com.bruce.designer.broadcast.NotificationBuilder;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.result.ApiResult;
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
	
	private static final int HANDLER_FLAG_SLIDE = 1;
	
	private static final int HANDLER_FLAG_UNFAVORITE_POST = 11;
	
	private View titlebarView;

	private TextView titleView;
	
	private DesignerAlbumsAdapter albumListAdapter;

	public AlbumSlidesAdapter slideAdapter;
	
	private PullToRefreshListView pullRefreshView;
	
	private int albumTailId = 0;
	
	
	public static void show(Context context){
		Intent intent = new Intent(context, Activity_MyFavorite.class);
		context.startActivity(intent);
	}
	
	private Handler handler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch(msg.what){
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
				case HANDLER_FLAG_UNFAVORITE_POST: //取消收藏成功
					NotificationBuilder.createNotification(context, "取消收藏成功...");
					break;
				default:
					break;
			}
		};
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_favorites);
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(listener);
		
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
		getMyFavoriteAlbums(albumTailId);
	}
	
	private void getMyFavoriteAlbums(final int albumTailId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				
				FavoriteAlbumsListApi api = new FavoriteAlbumsListApi(albumTailId);
				ApiResult apiResult = ApiManager.invoke(context, api);
				
				if(apiResult!=null&&apiResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_SLIDE);
					message.obj = apiResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	
	
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
	
}

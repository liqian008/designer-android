package com.bruce.designer.activity.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.activity.Activity_AlbumInfo;
import com.bruce.designer.activity.Activity_Settings;
import com.bruce.designer.activity.Activity_UserHome;
import com.bruce.designer.adapter.GridAdapter;
import com.bruce.designer.adapter.ViewPagerAdapter;
import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.album.AlbumListApi;
import com.bruce.designer.api.album.FollowAlbumListApi;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.db.album.AlbumDB;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.AlbumAuthorInfo;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.SharedPreferenceUtil;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.UniversalImageUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.bruce.designer.util.cache.ImageLoader;

public class Fragment_Main extends Fragment {

	private static final int HANDLER_FLAG_TAB0 = 0;
	private static final int HANDLER_FLAG_TAB1 = 1;
	private static final int HANDLER_FLAG_TAB2 = 2;
	
	private static final int HANDLER_FLAG_ERROR = -1;
//	private static final int HANDLER_FLAG_TAB0_ERROR = 10;
//	private static final int HANDLER_FLAG_TAB1_ERROR = 11;
	
	/* tab个数*/
	private static final int TAB_NUM = 3;
	/* 当前tab*/
	private int currentTab = 0;
	/* 各tab的tailId*/
	private int[] tabAlbumTailIds = new int[TAB_NUM];
	
	private ViewPager viewPager;
	private View[] tabViews = new View[TAB_NUM];
	private View[] tabIndicators = new View[TAB_NUM];
	
	/*下拉控件*/
	private PullToRefreshListView[] pullRefreshViews = new PullToRefreshListView[TAB_NUM];
	private ListView[] listViews = new ListView[TAB_NUM];
	private AlbumListAdapter[] listViewAdapters = new AlbumListAdapter[TAB_NUM];
	
	private ImageButton btnRefresh;
	
	private Activity context;
	private LayoutInflater inflater;
	private ImageButton btnSettings;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		this.inflater = inflater;
		
		View mainView = inflater.inflate(R.layout.activity_main_header, null);
		initView(mainView);
		return mainView;
	}
	
	private void initView(View view) {
		tabIndicators[0] = view.findViewById(R.id.tab_recommend_indicator);
		tabIndicators[1] = view.findViewById(R.id.tab_latest_indicator);
		tabIndicators[2] = view.findViewById(R.id.tab_myview_indicator);
		
		tabViews[0] = view.findViewById(R.id.tab_recommend);
		tabViews[1] = view.findViewById(R.id.tab_latest);
		tabViews[2] = view.findViewById(R.id.tab_myview);

		//响应事件
		for(int i=0;i<tabViews.length;i++){
			View tabView = tabViews[i];
			final int tabIndex = i;
			tabView.setOnClickListener(new OnSingleClickListener() {
				@Override
				public void onSingleClick(View view) {
					highLightTab(tabIndex);
				}
			});
		}
		
		//刷新按钮及点击事件
		btnRefresh = (ImageButton) view.findViewById(R.id.btnRefresh);
		btnRefresh.setOnClickListener(listener);
		//setting按钮及点击事件
		btnSettings = (ImageButton) view.findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(listener);
		
		//构造viewPager
		viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		List<View> pagerViews = new ArrayList<View>();
		//初始化tab页的view
		for(int i=0;i<TAB_NUM; i++){
			View pageView = inflater.inflate(R.layout.albums_listview, null);
			
			pullRefreshViews[i] = (PullToRefreshListView) pageView.findViewById(R.id.pull_refresh_list);
			pullRefreshViews[i].setMode(Mode.PULL_FROM_START);
			pullRefreshViews[i].setOnRefreshListener(new TabedRefreshListener(i));
			listViews[i] = pullRefreshViews[i].getRefreshableView();
			listViewAdapters[i] = new AlbumListAdapter(context, null, 0);
			listViews[i].setAdapter(listViewAdapters[i]);
			
			//将views加入viewPager
			pagerViews.add(pageView);
		}
		//viewPager的适配器
		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(context, pagerViews);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setOnPageChangeListener(viewPagerListener);
		
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		//根据tabIndex展示相应数据
		highLightTab(currentTab);
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	/**
	 * 高亮显示上部的Tab
	 * @param tabIndex
	 */
	private void highLightTab(int tabIndex){
		if(tabIndex>=TAB_NUM){
			tabIndex = 0;
		}
		for(View tabIndicator: tabIndicators){
			tabIndicator.setVisibility(View.GONE);//全部隐藏
		}
		currentTab = tabIndex;
		//显示
		tabIndicators[currentTab].setVisibility(View.VISIBLE);
		viewPager.setCurrentItem(currentTab);
		
		//判断tab的上次刷新时间
		long currentTime = System.currentTimeMillis();
		String tabRefreshKey = getRefreshKey(currentTab);
		long lastRefreshTime = SharedPreferenceUtil.getSharePreLong(context, tabRefreshKey, 0l);
		long interval = currentTime - lastRefreshTime;
		//相应page上请求数据
		List<Album> albumList = null;
		if(currentTab==1){
			albumList= AlbumDB.queryAllLatest(context);//请求最新数据
		}else if(currentTab==2){
			albumList= AlbumDB.queryAllFollow(context);//请求关注数据
		}else{
			albumList= AlbumDB.queryAllRecommend(context);//请求系统推荐数据
		}
		if(albumList!=null&&albumList.size()>0){
			listViewAdapters[currentTab].setAlbumList(albumList);
			listViewAdapters[currentTab].notifyDataSetChanged();
			if(interval > TimeUtil.TIME_UNIT_MINUTE){
				pullRefreshViews[currentTab].setRefreshing(false);
			}
		}
	}
	
	
	class AlbumListAdapter extends BaseAdapter {
		private List<Album> albumList;
		private Context context;
		private int style;
		
		public AlbumListAdapter(Context context, List<Album> albumList, int style) {
			this.context = context;
			this.albumList = albumList;
			this.style = style;
		}
		
		public void setAlbumList(List<Album> albumList) {
			this.albumList = albumList;
		}

		public List<Album> getAlbumList() {
			return albumList;
		}

		@Override
		public int getCount() {
			if (albumList != null) {
				return albumList.size();
			}
			return 0;
		}

		@Override
		public Album getItem(int position) {
			if (albumList != null) {
				return albumList.get(position);
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
				final Album album = getItem(position);
				View albumItemView = null;
				
				if(style==1){//grid mode
					albumItemView = LayoutInflater.from(context).inflate(R.layout.item_album_info_view, null);
					
					GridView gridView = (GridView) albumItemView.findViewById(R.id.grid);
					gridView.setAdapter(new GridAdapter(context));
					
				}else{//mainImg mode
					albumItemView = LayoutInflater.from(context).inflate(R.layout.item_album_view, null);
					ImageView coverView = (ImageView) albumItemView.findViewById(R.id.cover_img);
//					ImageLoader.loadImage(album.getCoverMediumImg(), coverView);
					ImageLoader.getInstance().displayImage(album.getCoverMediumImg(), coverView, UniversalImageUtil.DEFAULT_DISPLAY_OPTION);
				}
				
				//发布时间
				TextView pubtimeView = (TextView) albumItemView.findViewById(R.id.txtTime);
				pubtimeView.setText(TimeUtil.displayTime(album.getCreateTime()));
				
				final AlbumAuthorInfo authorInfo = album.getAuthorInfo();
				
				View designerView = (View) albumItemView.findViewById(R.id.designerContainer);
				designerView.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View view) {
						//跳转至个人资料页
						Activity_UserHome.show(context, album.getUserId(), authorInfo.getDesignerNickname(), authorInfo.getDesignerAvatar(), true, authorInfo.isFollowed());
					}
				});
				
				//设计师头像
				ImageView avatarView = (ImageView) albumItemView.findViewById(R.id.avatar);
				//设计师姓名
				TextView usernameView = (TextView) albumItemView.findViewById(R.id.txtUsername);
				
				
				if(authorInfo!=null){
					//显示头像
					ImageLoader.getInstance().displayImage(authorInfo.getDesignerAvatar(), avatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
					//显示昵称
					usernameView.setText(authorInfo.getDesignerNickname());
				}
				
				//专辑title
				TextView titleView = (TextView) albumItemView.findViewById(R.id.txtSticker);
				titleView.setText(album.getTitle());
				//专辑描述
				TextView contentView = (TextView) albumItemView.findViewById(R.id.txtContent);
				contentView.setText(album.getRemark());
				
				//专辑统计
				Button btnBrowse = (Button) albumItemView.findViewById(R.id.btnBrowse);
				Button btnLike = (Button) albumItemView.findViewById(R.id.btnLike);
				Button btnComment = (Button) albumItemView.findViewById(R.id.btnComment);
				Button btnFavorite = (Button) albumItemView.findViewById(R.id.btnFavorite);
				
				btnBrowse.setText("浏览("+String.valueOf(album.getBrowseCount())+")");
				btnLike.setText("喜欢("+String.valueOf(album.getLikeCount())+")");
				btnComment.setText("评论("+String.valueOf(album.getCommentCount())+")");
				btnFavorite.setText("收藏("+String.valueOf(album.getFavoriteCount())+")");
				
				//评论数量
				TextView commentView = (TextView) albumItemView.findViewById(R.id.txtComment);
				if(album.getCommentCount()>0){
					commentView.setText("查看全部"+album.getCommentCount()+"条评论");
				}else{
					commentView.setVisibility(View.GONE); 
				}
				
				albumItemView.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View view) {
						Activity_AlbumInfo.show(context, album, authorInfo);
					}
				});
				return albumItemView;
			}
			return null;
		}
	}
	
	/**
	 * 下拉刷新listener
	 * @author liqian
	 *
	 */
	class TabedRefreshListener implements OnRefreshListener2<ListView>{
		
		private int tabIndex;

		public TabedRefreshListener(int tabIndex){
			this.tabIndex = tabIndex;
		}

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			//tab下拉请求新数据
			getAlbums(0, tabIndex);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			//tab上拉请求增量数据
			getAlbums(tabAlbumTailIds[tabIndex], tabIndex);
		}
	}
	
	
	private void getAlbums(final int albumTailId, final int tabIndex) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				AbstractApi api = null;
				if(tabIndex==2){
					api = new FollowAlbumListApi(albumTailId);
				}else{
					api = new AlbumListApi(0, albumTailId);
				}
				ApiResult jsonResult = ApiManager.invoke(context, api);
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = tabDataHandler.obtainMessage(tabIndex);
					message.obj = jsonResult.getData();
					message.sendToTarget(); 
				}else{//发送失败消息
					int errorFlag = HANDLER_FLAG_ERROR;
					tabDataHandler.obtainMessage(errorFlag).sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	private Handler tabDataHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch(what){
				case HANDLER_FLAG_TAB0:
					pullRefreshViews[0].onRefreshComplete();
					Map<String, Object> tab0DataMap = (Map<String, Object>) msg.obj;
					if(tab0DataMap!=null){
						List<Album> albumList = (List<Album>) tab0DataMap.get("albumList");
						if(albumList!=null&&albumList.size()>0){
							//save to db
//							AlbumDB.save(context, albumList);
							
							List<Album> oldAlbumList = listViewAdapters[0].getAlbumList();
							if(oldAlbumList==null){
								oldAlbumList = new ArrayList<Album>();
							}
							oldAlbumList.addAll(0, albumList);
							listViewAdapters[0].setAlbumList(oldAlbumList);
							listViewAdapters[0].notifyDataSetChanged();
						}
					}
					SharedPreferenceUtil.putSharePre(context, getRefreshKey(0), System.currentTimeMillis());
					break;
				case HANDLER_FLAG_TAB1:
				case HANDLER_FLAG_TAB2:
					int tabIndex = what;
					//关闭刷新控件
					pullRefreshViews[tabIndex].onRefreshComplete();
					
					Map<String, Object> tabedDataMap = (Map<String, Object>) msg.obj;
					if(tabedDataMap!=null){
						List<Album> albumList = (List<Album>) tabedDataMap.get("albumList");
						Integer fromTailId = (Integer) tabedDataMap.get("fromTailId");
						Integer newTailId = (Integer) tabedDataMap.get("newTailId");
						if(albumList!=null&&albumList.size()>0){
							SharedPreferenceUtil.putSharePre(context, getRefreshKey(tabIndex), System.currentTimeMillis());
							if(newTailId!=null&&newTailId>0){//还有可加载的数据
								tabAlbumTailIds[tabIndex] = newTailId;
								pullRefreshViews[tabIndex].setMode(Mode.BOTH);
							}else{
								tabAlbumTailIds[tabIndex] = 0;
								pullRefreshViews[tabIndex].setMode(Mode.PULL_FROM_START);//禁用上拉刷新
							}
							
							List<Album> oldAlbumList = listViewAdapters[tabIndex].getAlbumList();
							if(oldAlbumList==null){
								oldAlbumList = new ArrayList<Album>();
							}
							//判断加载位置，以确定是list增量还是覆盖
							boolean fallloadAppend = fromTailId!=null&&fromTailId>0;
							if(fallloadAppend){//上拉加载更多，需添加至list的结尾
								oldAlbumList.addAll(albumList);
							}else{//下拉加载，需覆盖原数据
								AlbumDB.saveAlbumsByTab(context, albumList, tabIndex);
								oldAlbumList = null;
								oldAlbumList = albumList; 
							}
							listViewAdapters[tabIndex].setAlbumList(oldAlbumList);
							listViewAdapters[tabIndex].notifyDataSetChanged();
						}
					}
					break;
				default:
					break;
			}
		};
	};
	
	/**
	 * 按钮监听listener
	 */
	private View.OnClickListener listener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch (view.getId()){
			case R.id.btnRefresh://刷新按钮
				switch(currentTab){
					case 0:
					case 1:
					case 2:
						pullRefreshViews[currentTab].setRefreshing(false);
						break;
				}
				break;
			case R.id.btnSettings:
				Activity_Settings.show(context);
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 根据tabIndex生成记录其刷新的sp-key
	 * @param tabIndex
	 * @return
	 */
	private static String getRefreshKey(int tabIndex){
		return ConstantsKey.LAST_REFRESH_TIME_PREFIX + tabIndex;
	}
	
	/**
	 * viewPager的listener
	 */
	private OnPageChangeListener viewPagerListener = new OnPageChangeListener(){
		// 新的界面被选中时调用
		@Override
		public void onPageSelected(int index) {
			highLightTab(index);
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	};
	
}

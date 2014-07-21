package com.bruce.designer.activity.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.activity.Activity_AlbumInfo;
import com.bruce.designer.activity.Activity_Settings;
import com.bruce.designer.activity.Activity_UserProfile;
import com.bruce.designer.adapter.GridAdapter;
import com.bruce.designer.adapter.ViewPagerAdapter;
import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.album.AlbumListApi;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.db.album.AlbumDB;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Album;
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
	
	private static final int TAB_NUM = 3;
	
	private int currentTab = 0;
	
	/*tab0中最新一条的albumId*/
	private int tab0AlbumHeadId = 0;
	/*tab0中最老一条的albumId*/
	private int tab0AlbumTailId = 0;
	
	/*tab1中最新一条的albumId*/
	private int tab1AlbumHeadId = 0;
	/*tab1中最老一条的albumId*/
	private int tab1AlbumTailId = 0;
	
	/*tab2中最新一条的albumId*/
	private int tab2AlbumHeadId = 0;
	/*tab2中最老一条的albumId*/
	private int tab2AlbumTailId = 0;
	
	private int tabAlbumTailIds[] = new int[TAB_NUM];
	
	
	private ViewPager viewPager;
	private View[] tabViews = new View[TAB_NUM];
	private View[] tabIndicators = new View[TAB_NUM];
	
//	private PullToRefreshListView[] pullRefreshViews;
//	private ListView[] ListViews;
//	private AlbumListAdapter[] listViewAdapters;
	
	/*下拉控件*/
	private PullToRefreshListView pullToRefreshView0;
	private ListView listView0;
	private AlbumListAdapter listView0Adapter;
	
	private PullToRefreshListView pullToRefreshView1;
	private ListView listView1;
	private AlbumListAdapter listView1Adapter;
	
	private PullToRefreshListView pullToRefreshView2;
	private ListView listView2;
	private AlbumListAdapter listView2Adapter;
	
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
		View tabContentView0 = inflater.inflate(R.layout.albums_listview, null);
		View tabContentView1 = inflater.inflate(R.layout.albums_listview, null);
		View tabContentView2 = inflater.inflate(R.layout.albums_listview, null);
		
		//将views加入viewPager
		pagerViews.add(tabContentView0);
		pagerViews.add(tabContentView1);
		pagerViews.add(tabContentView2);
		//viewPager的适配器
		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(context, pagerViews);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setOnPageChangeListener(viewPagerListener);
		
		//ViewPager0
		pullToRefreshView0 = (PullToRefreshListView) tabContentView0.findViewById(R.id.pull_refresh_list);
		pullToRefreshView0.setMode(Mode.PULL_FROM_START);
		pullToRefreshView0.setOnRefreshListener(tab0RefreshListener);
		listView0 = pullToRefreshView0.getRefreshableView();
		listView0Adapter = new AlbumListAdapter(context, null, 0);
		listView0.setAdapter(listView0Adapter);
		
		pullToRefreshView1 = (PullToRefreshListView) tabContentView1.findViewById(R.id.pull_refresh_list);
		pullToRefreshView1.setMode(Mode.PULL_FROM_START);
		pullToRefreshView1.setOnRefreshListener(tab1RefreshListener);
		listView1 = pullToRefreshView1.getRefreshableView();
		listView1Adapter = new AlbumListAdapter(context, null, 0);
		listView1.setAdapter(listView1Adapter);
		
		pullToRefreshView2 = (PullToRefreshListView) tabContentView2.findViewById(R.id.pull_refresh_list);
		pullToRefreshView2.setMode(Mode.BOTH);
		pullToRefreshView2.setOnRefreshListener(tab2RefreshListener);
		listView2 = pullToRefreshView2.getRefreshableView();
		listView2Adapter = new AlbumListAdapter(context, null, 0);
		listView2.setAdapter(listView2Adapter);
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
			//全部隐藏
			tabIndicator.setVisibility(View.GONE);
		}
		currentTab = tabIndex;
		//显示
		tabIndicators[currentTab].setVisibility(View.VISIBLE);
		viewPager.setCurrentItem(currentTab);
		
		//判断tab的上次刷新时间
		long currentTime = System.currentTimeMillis();
		String tabRefreshKey = getRefreshKey(tabIndex);
		long lastRefreshTime = SharedPreferenceUtil.getSharePreLong(context, tabRefreshKey, 0l);
		long interval = currentTime - lastRefreshTime;
		//相应page上请求数据
		switch(currentTab){
		case 1:
			//请求db数据
			List<Album> recommendAlbumList= AlbumDB.queryAllRecommend(context);
			if(recommendAlbumList!=null&&recommendAlbumList.size()>0){//展示db中数据
				listView1Adapter.setAlbumList(recommendAlbumList);
				listView1Adapter.notifyDataSetChanged();
				tab1AlbumHeadId = recommendAlbumList.get(0).getId();
				tab1AlbumTailId = recommendAlbumList.get(recommendAlbumList.size()-1).getId();
			}
			if(interval > TimeUtil.TIME_UNIT_MINUTE){
				pullToRefreshView1.setRefreshing(false);
			}
			break;
		case 2:
			//请求db数据
			List<Album> tab2AlbumList= AlbumDB.queryAllFollow(context);
			if(tab2AlbumList!=null&&tab2AlbumList.size()>0){//展示db中数据
				listView2Adapter.setAlbumList(tab2AlbumList);
				listView2Adapter.notifyDataSetChanged();
				tab2AlbumHeadId = tab2AlbumList.get(0).getId();
				tab2AlbumTailId = tab2AlbumList.get(tab2AlbumList.size()-2).getId();
			}
			if(interval > TimeUtil.TIME_UNIT_MINUTE){
				pullToRefreshView2.setRefreshing(false);
			}
			break;
		case 0:
		default://刷新首个tab
			List<Album> timelineAlbumList= AlbumDB.queryAllLatest(context);
			
			if(timelineAlbumList!=null&&timelineAlbumList.size()>0){//展示db中数据
				listView0Adapter.setAlbumList(timelineAlbumList);
				listView0Adapter.notifyDataSetChanged();
				tab0AlbumHeadId = timelineAlbumList.get(0).getId();
				tab0AlbumTailId = timelineAlbumList.get(timelineAlbumList.size()-1).getId();
			}
			if(interval > TimeUtil.TIME_UNIT_MINUTE){
				pullToRefreshView0.setRefreshing(false);
			}
			break;	
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
				
				View designerView = (View) albumItemView.findViewById(R.id.designerContainer);
				designerView.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View view) {
						Intent intent = new Intent(context, Activity_UserProfile.class);
						intent.putExtra(ConstantsKey.BUNDLE_USER_INFO_ID, album.getUserId());
						context.startActivity(intent);
					}
				});
				
				
				ImageView avatarView = (ImageView) albumItemView.findViewById(R.id.avatar);
//				ImageLoader.loadImage("http://img.jinwanr.com.cn/staticFile/avatar/100/100009.jpg", avatarView);
				ImageLoader.getInstance().displayImage("http://img.jinwanr.com.cn/staticFile/avatar/100/100009.jpg", avatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
				
				TextView usernameView = (TextView) albumItemView.findViewById(R.id.txtUsername);
				usernameView.setText("大树珠宝");
				
				TextView pubtimeView = (TextView) albumItemView.findViewById(R.id.txtTime);
				pubtimeView.setText(TimeUtil.displayTime(album.getCreateTime()));
				
				TextView titleView = (TextView) albumItemView.findViewById(R.id.txtSticker);
				titleView.setText(album.getTitle());
				TextView contentView = (TextView) albumItemView.findViewById(R.id.txtContent);
				contentView.setText(album.getRemark());
				
				TextView commentView = (TextView) albumItemView.findViewById(R.id.txtComment);
				if(album.getCommentCount()>0){
					commentView.setText("查看全部"+album.getCommentCount()+"条评论");
				}else{
					commentView.setVisibility(View.GONE); 
				}
				
//				final int albumId = album.getId();
				
				albumItemView.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View view) {
						Intent intent = new Intent(context, Activity_AlbumInfo.class);
						intent.putExtra(ConstantsKey.BUNDLE_ALBUM_INFO, album);
						context.startActivity(intent);
					}
				});
				return albumItemView;
			}
			return null;
		}
	}

	OnRefreshListener2<ListView> tab0RefreshListener = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			//tab0请求新数据
			getAlbums(tab0AlbumHeadId, HANDLER_FLAG_TAB0);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			//tab0请求历史数据
			getAlbums(tab0AlbumTailId, HANDLER_FLAG_TAB0);
		}
	};
	
	OnRefreshListener2<ListView> tab1RefreshListener = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			//tab1请求数据
			getAlbums(tab1AlbumHeadId, HANDLER_FLAG_TAB1);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			//tab0请求历史数据
			getAlbums(tab1AlbumTailId, HANDLER_FLAG_TAB1);
		}
	};
	
	OnRefreshListener2<ListView> tab2RefreshListener = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			//tab2请求数据
			getAlbums(tab2AlbumHeadId, HANDLER_FLAG_TAB2);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			//tab0请求历史数据
			getAlbums(tab2AlbumTailId, HANDLER_FLAG_TAB2);
		}
	};
	
	private void getAlbums(final int albumTailId, final int tabIndex) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				AbstractApi api = null;
//				if(tabIndex==2){
//					api = new FollowAlbumListApi(albumTailId);
//				}else{
					api = new AlbumListApi(0, albumTailId);
//				}
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
			switch(msg.what){
				case HANDLER_FLAG_TAB0:
					Map<String, Object> tab0DataMap = (Map<String, Object>) msg.obj;
					if(tab0DataMap!=null){
						List<Album> albumList = (List<Album>) tab0DataMap.get("albumList");
						if(albumList!=null&&albumList.size()>0){
							//save to db
//							AlbumDB.save(context, albumList);
							
							List<Album> oldAlbumList = listView0Adapter.getAlbumList();
							if(oldAlbumList==null){
								oldAlbumList = new ArrayList<Album>();
							}
							oldAlbumList.addAll(0, albumList);
							listView0Adapter.setAlbumList(oldAlbumList);
							listView0Adapter.notifyDataSetChanged();
						}
					}
					pullToRefreshView0.onRefreshComplete();
					SharedPreferenceUtil.putSharePre(context, getRefreshKey(0), System.currentTimeMillis());
					break;
				case HANDLER_FLAG_TAB1:
					Map<String, Object> tab1DataMap = (Map<String, Object>) msg.obj;
					if(tab1DataMap!=null){
						List<Album> albumList = (List<Album>) tab1DataMap.get("albumList");
						Integer albumTailId = (Integer) tab1DataMap.get("albumTailId");
						if(albumList!=null&&albumList.size()>0){
							List<Album> oldAlbumList = listView1Adapter.getAlbumList();
							if(oldAlbumList==null){
								oldAlbumList = new ArrayList<Album>();
							}
							//TODO 判断是首页加载还是瀑布流加载
							boolean fallloadAppend = false;
							if(fallloadAppend){//瀑布流方式加载
								oldAlbumList.addAll(albumList);
							}else{
								AlbumDB.saveLatestAlbums(context, albumList);
								oldAlbumList = null;
								oldAlbumList = albumList;
							}
							listView1Adapter.setAlbumList(oldAlbumList);
							listView1Adapter.notifyDataSetChanged();

							if(albumTailId!=null&&albumTailId>0){
								tabAlbumTailIds[1] = albumTailId;
								pullToRefreshView1.setMode(Mode.BOTH);
							}else{
								pullToRefreshView1.setMode(Mode.PULL_FROM_START);
							}
						}
					}
					SharedPreferenceUtil.putSharePre(context, getRefreshKey(1), System.currentTimeMillis());
					pullToRefreshView1.onRefreshComplete();
					break;
				case HANDLER_FLAG_TAB2:
					Map<String, Object> tab2DataMap = (Map<String, Object>) msg.obj;
					if(tab2DataMap!=null){
						List<Album> albumList = (List<Album>) tab2DataMap.get("albumList");
						if(albumList!=null&&albumList.size()>0){
							List<Album> oldAlbumList = listView2Adapter.getAlbumList();
							if(oldAlbumList==null){
								oldAlbumList = new ArrayList<Album>();
							}
							oldAlbumList.addAll(0, albumList);
							listView2Adapter.setAlbumList(oldAlbumList);
							listView2Adapter.notifyDataSetChanged();
						}
					}
					SharedPreferenceUtil.putSharePre(context, getRefreshKey(2), System.currentTimeMillis());
					pullToRefreshView2.onRefreshComplete();
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
						pullToRefreshView0.setRefreshing(false);
						break;
					case 1:
						pullToRefreshView1.setRefreshing(false);
						break;
					case 2:
						pullToRefreshView2.setRefreshing(false);
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

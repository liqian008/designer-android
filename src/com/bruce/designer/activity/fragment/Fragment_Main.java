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
import android.widget.Toast;

import com.bruce.designer.R;
import com.bruce.designer.activity.Activity_AlbumInfo;
import com.bruce.designer.activity.Activity_Settings;
import com.bruce.designer.activity.Activity_UserProfile;
import com.bruce.designer.adapter.GridAdapter;
import com.bruce.designer.adapter.ViewPagerAdapter;
import com.bruce.designer.api.ApiWrapper;
import com.bruce.designer.api.album.AlbumListApi;
import com.bruce.designer.constants.BundleKey;
import com.bruce.designer.db.AlbumDB;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.cache.ImageLoader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class Fragment_Main extends Fragment {

	private static final int HANDLER_FLAG_ERROR = 0;
	private static final int HANDLER_FLAG_TAB0 = 0;
	private static final int HANDLER_FLAG_TAB1 = 1;
	
	private int currentTab = 0;
	
	/*tab0中最新一条的albumId*/
	private int tab0AlbumHeadId = 0;
	/*tab0中最老一条的albumId*/
	private int tab0AlbumTailId = 0;
	/*tab0中最后刷新时间*/
	private int tab0LastRefreshTime = 0;
	
	/*tab1中最新一条的albumId*/
	private int tab1AlbumHeadId = 0;
	/*tab1中最老一条的albumId*/
	private int tab1AlbumTailId = 0;
	/*tab1中最后刷新时间*/
	private int tab1LastRefreshTime = 0;
	
	private ViewPager viewPager;
	
	private ListView listView0;
	private AlbumListAdapter listView0Adapter;
	private ListView listView1;
	private AlbumListAdapter listView1Adapter;
	
	private static final int TAB_NUM = 3;
	
	/*Tabs*/
	private View tab0View;
	private View tab1View;
	private View tab2View;
	
	private View[] tabViews = new View[TAB_NUM];
	private View[] tabIndicators = new View[TAB_NUM];
	
	/*下拉控件*/
	private PullToRefreshListView pullToRefreshView0;
	private PullToRefreshListView pullToRefreshView1;
	
	private ImageButton btnRefresh;
	
	private Activity context;
	private LayoutInflater inflater;
	private ImageButton btnSettings;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		this.inflater = inflater;
		
		View mainView = inflater.inflate(R.layout.activity_main_header, container);
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
			tabView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					highLight(tabIndex);
				}
			});
		}
		
		//构造viewPager
		viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		List<View> pagerViews = new ArrayList<View>();
		//初始化tab页的view
		View tabContentView0 = inflater.inflate(R.layout.albums_listview, null);
		View tabContentView1 = inflater.inflate(R.layout.albums_listview, null);
		//将views加入viewPager
		pagerViews.add(tabContentView0);
		pagerViews.add(tabContentView1);
		//viewPager的适配器
		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(pagerViews, context);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setOnPageChangeListener(viewPagerListener);
		
		//刷新按钮及点击事件
		btnRefresh = (ImageButton) view.findViewById(R.id.btnRefresh);
		btnRefresh.setOnClickListener(listener);
		//setting按钮及点击事件
		btnSettings = (ImageButton) view.findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(listener);
		
		//ViewPager0
		pullToRefreshView0 = (PullToRefreshListView) tabContentView0.findViewById(R.id.pull_refresh_list);
		pullToRefreshView0.setMode(Mode.BOTH);
		pullToRefreshView0.setOnRefreshListener(tab0RefreshListener);
		listView0 = pullToRefreshView0.getRefreshableView();
		listView0Adapter = new AlbumListAdapter(context, null, 0);
		listView0.setAdapter(listView0Adapter);
		
		//tab0请求db数据
		List<Album> albumList= AlbumDB.queryAll(context);
		LogUtil.d("====albumList==="+albumList);
		if(albumList!=null&&albumList.size()>0){//展示db中数据
			listView0Adapter.setAlbumList(albumList);
			listView0Adapter.notifyDataSetChanged();
			tab0AlbumHeadId = albumList.get(0).getId();
			tab0AlbumTailId = albumList.get(albumList.size()-1).getId();
		}
		
		if(albumList==null||albumList.size()==0){//需要加载网络数据
			//tab0请求网络数据
			getAlbums(tab0AlbumHeadId, HANDLER_FLAG_TAB0);
		}
		
		pullToRefreshView1 = (PullToRefreshListView) tabContentView1.findViewById(R.id.pull_refresh_list);
		pullToRefreshView1.setMode(Mode.BOTH);
		pullToRefreshView1.setOnRefreshListener(tab1RefreshListener);
		listView1 = pullToRefreshView1.getRefreshableView();
		listView1Adapter = new AlbumListAdapter(context, null, 0);
		listView1.setAdapter(listView1Adapter);
		//刷新
//		pullToRefreshView1.setRefreshing(true);
		
		//tab1请求db数据
		
		//TODO tab切换时再请求网络数据
		getAlbums(tab1AlbumHeadId, HANDLER_FLAG_TAB1);
		
		highLight(currentTab);
	}

	/**
	 * 高亮显示
	 * @param tabIndex
	 */
	private void highLight(int tabIndex){
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
					ImageLoader.loadImage(album.getCoverMediumImg(), coverView);
				}
				
				View designerView = (View) albumItemView.findViewById(R.id.designerContainer);
				designerView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(context, Activity_UserProfile.class);
						intent.putExtra(BundleKey.BUNDLE_USER_INFO_ID, album.getUserId());
						context.startActivity(intent);
					}
				});
				
				
				ImageView avatarView = (ImageView) albumItemView.findViewById(R.id.avatar);
				ImageLoader.loadImage("http://img.jinwanr.com.cn/staticFile/avatar/100/100009.jpg", avatarView);
				
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
				
				albumItemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(context, Activity_AlbumInfo.class);
						intent.putExtra(BundleKey.BUNDLE_ALBUM_INFO, album);
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
			Toast.makeText(context, "下拉刷新", Toast.LENGTH_LONG).show();
			//tab0请求新数据
			getAlbums(tab0AlbumHeadId, HANDLER_FLAG_TAB0);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			Toast.makeText(context, "上拉获取更多", Toast.LENGTH_LONG).show();
			//tab0请求历史数据
			getAlbums(tab0AlbumTailId, HANDLER_FLAG_TAB0);
		}
	};
	
	OnRefreshListener2<ListView> tab1RefreshListener = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			Toast.makeText(context, "下拉刷新", Toast.LENGTH_LONG).show();
			//tab1请求数据
			getAlbums(tab1AlbumHeadId, HANDLER_FLAG_TAB1);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			Toast.makeText(context, "上拉获取更多", Toast.LENGTH_LONG).show();
			//tab0请求历史数据
			getAlbums(tab1AlbumTailId, HANDLER_FLAG_TAB1);
		}
	};
	
	
	private void getAlbums(final int albumTailId, final int tabIndex) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
//				JsonResultBean jsonResult = ApiUtil.getAlbumList(0, albumTailId);
				
				AlbumListApi api = new AlbumListApi(0, albumTailId);
				JsonResultBean jsonResult = ApiWrapper.invoke(context, api);
				
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = tabDataHandler.obtainMessage(tabIndex);
					message.obj = jsonResult.getData();
					message.sendToTarget(); 
				}else{//发送失败消息
					tabDataHandler.obtainMessage(HANDLER_FLAG_ERROR).sendToTarget();
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
							AlbumDB.save(context, albumList);
							
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
					break;
				case HANDLER_FLAG_TAB1:
					Map<String, Object> tab1DataMap = (Map<String, Object>) msg.obj;
					if(tab1DataMap!=null){
						List<Album> albumList = (List<Album>) tab1DataMap.get("albumList");
						if(albumList!=null&&albumList.size()>0){
							List<Album> oldAlbumList = listView1Adapter.getAlbumList();
							if(oldAlbumList==null){
								oldAlbumList = new ArrayList<Album>();
							}
							oldAlbumList.addAll(0, albumList);
							listView1Adapter.setAlbumList(oldAlbumList);
							listView1Adapter.notifyDataSetChanged();
						}
					}
					pullToRefreshView1.onRefreshComplete();
					break;
				default:
					break;
			}
		};
	};
	
	/**
	 * 按钮监听listener
	 */
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()){
			case R.id.btnRefresh://刷新按钮
				switch(currentTab){
					case 0:
						pullToRefreshView0.setRefreshing(true);
						break;
					case 1:
						pullToRefreshView1.setRefreshing(true);
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
	 * viewPager的listener
	 */
	private OnPageChangeListener viewPagerListener = new OnPageChangeListener(){
		// 新的界面被选中时调用
		@Override
		public void onPageSelected(int index) {
			highLight(index);
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	};
	
}

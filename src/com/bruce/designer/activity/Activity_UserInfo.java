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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.adapter.AlbumSlidesAdapter;
import com.bruce.designer.api.ApiWrapper;
import com.bruce.designer.api.album.AlbumListApi;
import com.bruce.designer.api.user.UserFansApi;
import com.bruce.designer.api.user.UserInfoApi;
import com.bruce.designer.constants.BundleKey;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.AlbumSlide;
import com.bruce.designer.model.User;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.ApiUtil;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.cache.ImageLoader;

public class Activity_UserInfo extends BaseActivity {
	
	private View titlebarView;

	private TextView titleView;
	/*设计师头像*/
	private ImageView avatarView;
	/*设计师名称*/
	private TextView designerNameView;
	
	private View followsView;
	private View fansView;
	
	private TextView followsNumView;
	private TextView fansNumView;
	
	private int userId;

	private AlbumListAdapter albumListAdapter;

	public AlbumSlidesAdapter slideAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		
		
		Intent intent = getIntent();
		//获取userid
		userId =  intent.getIntExtra(BundleKey.BUNDLE_USER_INFO_ID, 0);
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("设计师");
		
		avatarView = (ImageView) findViewById(R.id.avatar);
//		ImageLoader.loadImage("http://img.jinwanr.com.cn/staticFile/avatar/default.jpg", avatarView);
		
		designerNameView = (TextView) findViewById(R.id.txtUsername);
		
		fansView = (View) findViewById(R.id.fansContainer);
		fansNumView = (TextView) findViewById(R.id.txtFansNum);
		followsView = (View) findViewById(R.id.followsContainer);
		followsNumView = (TextView) findViewById(R.id.txtFollowsNum);
		
		followsView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, Activity_UserFollows.class);
				intent.putExtra(BundleKey.BUNDLE_USER_INFO_ID, userId);
				context.startActivity(intent);
			}
		});
		fansView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, Activity_UserFans.class);
				intent.putExtra(BundleKey.BUNDLE_USER_INFO_ID, userId);
				context.startActivity(intent);
			}
		});
		
		ListView albumListView = (ListView)findViewById(R.id.designerAlbums);
		albumListAdapter = new AlbumListAdapter(context, null);
		albumListView.setAdapter(albumListAdapter);
		
		//获取个人资料详情
		getUserinfo(userId);
		//获取个人专辑诶人
		getAlbums(0);
	}
	
	
	class AlbumListAdapter extends BaseAdapter {

		private List<Album> albumList;
		private Context context;
		
		public AlbumListAdapter(Context context, List<Album> albumList) {
			this.context = context;
			this.albumList = albumList;
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
				View albumItemView = LayoutInflater.from(context).inflate(R.layout.item_designer_album_view, null);
				
				/*构造slide数据*/
				GridView gridView = (GridView) albumItemView.findViewById(R.id.albumSlideImages);
				List<AlbumSlide> slideList = buildSlideList(album);
				slideAdapter = new AlbumSlidesAdapter(context, slideList);
				gridView.setAdapter(slideAdapter);

				TextView usernameView = (TextView) albumItemView.findViewById(R.id.txtUsername);
				usernameView.setText(album.getTitle());
				
				TextView pubtimeView = (TextView) albumItemView.findViewById(R.id.txtTime);
				pubtimeView.setText(TimeUtil.displayTime(album.getCreateTime()));
				
				return albumItemView;
			}
			return null;
		}
	}
	
	private List<AlbumSlide> buildSlideList(Album album) {
		List<AlbumSlide> slideList = new ArrayList<AlbumSlide>();
		for(int i=0;i<3;i++){
			AlbumSlide slide = new AlbumSlide();
			slide.setSlideSmallImg(album.getCoverSmallImg());
			slideList.add(slide);
		}
		return slideList;
	}
	
	private void getUserinfo(final int userId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
//				JsonResultBean jsonResult = ApiUtil.getUserinfo(userId);
				
				UserInfoApi api = new UserInfoApi(userId);
				JsonResultBean jsonResult = ApiWrapper.invoke(context, api);
				
				
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(0);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	private void getAlbums(final int albumTailId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
//				JsonResultBean jsonResult = ApiUtil.getAlbumList(0, albumTailId);
				
				AlbumListApi api = new AlbumListApi(userId, albumTailId);
				JsonResultBean jsonResult = ApiWrapper.invoke(context, api);
				
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(1);
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
					Map<String, Object> userinfoDataMap = (Map<String, Object>) msg.obj;
					if(userinfoDataMap!=null){
						User userinfo = (User) userinfoDataMap.get("userinfo");
						int fansCount = (Integer) userinfoDataMap.get("fansCount");
						int followsCount = (Integer) userinfoDataMap.get("followsCount");
						if(userinfo!=null&&userinfo.getId()>0){
//							designerNameView.setText(userinfo.getNickname());
							titleView.setText(userinfo.getNickname());
							fansNumView.setText(String.valueOf(fansCount));
							followsNumView.setText(String.valueOf(followsCount));
						}
					}
					break;
				case 1:
					Map<String, Object> albumsDataMap = (Map<String, Object>) msg.obj;
					if(albumsDataMap!=null){
						List<Album> albumList = (List<Album>) albumsDataMap.get("albumList");
						if(albumList!=null&&albumList.size()>0){
							albumListAdapter.setAlbumList(albumList);
							albumListAdapter.notifyDataSetChanged();
						}
					}
					break;
				default:
					break;
			}
		}
	};
	
}

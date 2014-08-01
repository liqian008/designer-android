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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.adapter.AlbumSlidesAdapter;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.album.AlbumCommentApi;
import com.bruce.designer.api.album.AlbumInfoApi;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.db.album.AlbumCommentDB;
import com.bruce.designer.db.album.AlbumSlideDB;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.AlbumAuthorInfo;
import com.bruce.designer.model.AlbumSlide;
import com.bruce.designer.model.Comment;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.UiUtil;
import com.bruce.designer.util.UniversalImageUtil;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Activity_AlbumInfo extends BaseActivity {
	
	private static final int HANDLER_FLAG_INFO = 1;
	private static final int HANDLER_FLAG_SLIDE = 2;
	private static final int HANDLER_FLAG_COMMENT = 3;
	
	private View titlebarView;
	private TextView titleView;
	
	private ImageView designerAvatarView;
	private TextView designerNameView;
	private TextView pubtimeView;
	private TextView albumTitleView;
	private TextView albumContentView;
//	private ImageView coverView;
	
//	private Button followBtn;
//	private Button unfollowBtn;
	
	private ListView commentListView;
	private AlbumCommentsAdapter commentsAdapter;
	private AlbumSlidesAdapter slideAdapter;
	
	private Integer albumId;
	
	private Handler handler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch(msg.what){
				case HANDLER_FLAG_SLIDE:
					Map<String, Object> albumDataMap = (Map<String, Object>) msg.obj;
					if(albumDataMap!=null){
						Album albumInfo = (Album) albumDataMap.get("albumInfo");
						List<AlbumSlide> slideList = albumInfo.getSlideList();
						//先将slide列表存入db
						AlbumSlideDB.deleteByAlbumId(context, albumId);
						AlbumSlideDB.save(context, slideList);
						
						slideAdapter.setSlideList(slideList);
						slideAdapter.notifyDataSetChanged();
					}
					break;
				case HANDLER_FLAG_COMMENT:
					Map<String, Object> commentDataMap = (Map<String, Object>) msg.obj;
					if(commentDataMap!=null){
						List<Comment> commentList = (List<Comment>) commentDataMap.get("commentList");
						//先将评论存入db
						AlbumCommentDB.deleteByAlbumId(context, albumId);
						AlbumCommentDB.save(context, commentList);
						
						commentsAdapter.setCommentList(commentList);
						commentsAdapter.notifyDataSetChanged();
						//解决scrollview与list的冲突
						UiUtil.setListViewHeightBasedOnChildren(commentListView);
					}
					break;
				default:
					break;
			}
		};
	};
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_info);
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("作品集");
		
		designerAvatarView = (ImageView) findViewById(R.id.avatar);
		designerNameView = (TextView) findViewById(R.id.txtUsername);
		
//		followBtn = (Button) findViewById(R.id.btnFollow);
//		unfollowBtn = (Button) findViewById(R.id.btnUnfollow);
		
		//coverView = (ImageView) findViewById(R.id.cover_img);
		GridView gridView = (GridView)findViewById(R.id.albumSlideImages);
		slideAdapter = new AlbumSlidesAdapter(context, null);
		gridView.setAdapter(slideAdapter);
		
		
		pubtimeView = (TextView) findViewById(R.id.txtTime);
		albumTitleView = (TextView) findViewById(R.id.txtSticker);
		albumContentView = (TextView) findViewById(R.id.txtContent);
		
		commentListView =(ListView) findViewById(R.id.commentList);
		commentsAdapter = new AlbumCommentsAdapter(context, null);
		commentListView.setAdapter(commentsAdapter);
		 
		
		Intent intent = getIntent();
		final Album album = (Album) intent.getSerializableExtra(ConstantsKey.BUNDLE_ALBUM_INFO);
		albumId = album.getId();
		//读取上个activity传入的albumId值
		if(album!=null&&albumId!=null){
			final AlbumAuthorInfo authorInfo = (AlbumAuthorInfo) intent.getSerializableExtra(ConstantsKey.BUNDLE_ALBUM_AUTHOR_INFO);
			if(authorInfo!=null){
				
				View designerView = (View) findViewById(R.id.designerContainer);
				designerView.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View view) {
						Activity_UserHome.show(context, album.getUserId(), authorInfo.getDesignerNickname(), authorInfo.getDesignerAvatar(), true, authorInfo.isFollowed());
					}
				});
				
				//显示头像
				ImageLoader.getInstance().displayImage(authorInfo.getDesignerAvatar(), designerAvatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
				designerNameView.setText(authorInfo.getDesignerNickname());
				
//				if(authorInfo.isFollowed()){
//					followBtn.setVisibility(View.GONE);
//					unfollowBtn.setVisibility(View.VISIBLE);
//				}else{
//					followBtn.setVisibility(View.VISIBLE);
//					unfollowBtn.setVisibility(View.GONE);
//				}
			}
			
			pubtimeView.setText(TimeUtil.displayTime(album.getCreateTime()));
			albumTitleView.setText(album.getTitle());
			albumContentView.setText(album.getRemark());
			
			//获取db中的图片列表
			List<AlbumSlide> albumSlideList= AlbumSlideDB.queryByAlbumId(context, albumId);
			if(albumSlideList!=null&&albumSlideList.size()>0){//展示db中数据
				slideAdapter.setSlideList(albumSlideList);
				slideAdapter.notifyDataSetChanged();
			}
			//获取实时图片列表
			getAlbumInfo(album.getId());
			
			//获取db中的评论列表
			List<Comment> commentList= AlbumCommentDB.queryByAlbumId(context, albumId);
			if(commentList!=null&&commentList.size()>0){//展示db中数据
				commentsAdapter.setCommentList(commentList);
				commentsAdapter.notifyDataSetChanged();
			}
			//解决scrollview与list的冲突
			UiUtil.setListViewHeightBasedOnChildren(commentListView);
			//获取实时评论列表
			getAlbumComments(album.getId(), 0);
		}
	}
		
	
	private void getAlbumInfo(final int albumId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
//				JsonResultBean jsonResult = ApiUtil.getAlbumInfo(albumId);
				
				AlbumInfoApi api = new AlbumInfoApi(albumId);
				ApiResult jsonResult = ApiManager.invoke(context, api);
				
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_SLIDE);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}else{//发送失败消息
					handler.obtainMessage(0).sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	private void getAlbumComments(final int albumId, final int commentsTailId) {
		//启动线程获取评论数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
//				JsonResultBean jsonResult = ApiUtil.getAlbumComments(albumId, commentsTailId);
				
				AlbumCommentApi api = new AlbumCommentApi(albumId, commentsTailId);
				ApiResult jsonResult = ApiManager.invoke(context, api);
				
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_COMMENT);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}else{//发送失败消息
					handler.obtainMessage(0).sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	class AlbumCommentsAdapter extends BaseAdapter {

		private List<Comment> commentList;
		private Context context;
		
		public AlbumCommentsAdapter(Context context, List<Comment> commentList) {
			this.context = context;
			this.commentList = commentList;
		}
		
		public List<Comment> getCommentList() {
			return commentList;
		}

		public void setCommentList(List<Comment> commentList) {
			this.commentList = commentList;
		}

		@Override
		public int getCount() {
			if (commentList != null) {
				return commentList.size();
			}
			return 0;
		}

		@Override
		public Comment getItem(int position) {
			if (commentList != null) {
				return commentList.get(position);
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
				final Comment comment = getItem(position);
				View commentItemView = LayoutInflater.from(context).inflate(R.layout.item_comment_view, null);
				
				ImageView avatarView = (ImageView) commentItemView.findViewById(R.id.avatar);
				if(comment.getUserHeadImg()!=null){
					ImageLoader.getInstance().displayImage(comment.getUserHeadImg(), avatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
				}
				
				TextView commentUsernameView = (TextView) commentItemView.findViewById(R.id.commentUserame);
				commentUsernameView.setText(comment.getNickname());
				
				TextView commentContentView = (TextView) commentItemView.findViewById(R.id.commentContent);
				commentContentView.setText(comment.getComment());
				
				TextView commentTimeView = (TextView) commentItemView.findViewById(R.id.commentTime);
				commentTimeView.setText(TimeUtil.displayTime(comment.getCreateTime()));
				
				return commentItemView;
			}
			return null;
		}
	}
	
	
//	class AlbumSlidesAdapter extends BaseAdapter {
//
//		private List<AlbumSlide> slideList;
//		private Context context;
//		
//		public AlbumSlidesAdapter(Context context, List<AlbumSlide> slideList) {
//			this.context = context;
//			this.slideList = slideList;
//		}
//		
//		@Override
//		public int getCount() {
//			if (slideList != null) {
//				return slideList.size();
//			}
//			return 0;
//		}
//
//		@Override
//		public AlbumSlide getItem(int position) {
//			if (slideList != null) {
//				return slideList.get(position);
//			}
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			//TODO 暂未使用convertView
//			AlbumSlide albumSlide=  slideList.get(position);
//			if(albumSlide!=null){
//				
//				//计算每个item所需的宽度
//				int pxFromDp = DipUtil.calcFromDip((Activity)context, 1);
//				int widthSpace = pxFromDp * 0;//该控件离边距的宽度(margin或padding)
//				int width = DipUtil.getScreenWidth((Activity)context);
//				int itemWidth = (width-widthSpace)/3;
//				
//				LogUtil.d("======widthSpace======"+widthSpace);
//				LogUtil.d("======width======"+width);
//				LogUtil.d("======itemWidth======"+itemWidth);
//				
//				FrameLayout layout = new FrameLayout(context);
//				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//						FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//				
//				int leftWidth = position%3==0?0:pxFromDp;
//				int rightWidth = position%3==2?0:pxFromDp;
//				
//				params.setMargins(leftWidth, 0, rightWidth, pxFromDp);//边距
//	            params.gravity = Gravity.TOP;
//	            ImageView itemImageView = new ImageView(context);
//	            itemImageView.setScaleType(ScaleType.CENTER_CROP);
//	            layout.addView(itemImageView, params);
//	            ImageLoader.loadImage(albumSlide.getSlideSmallImg(), itemImageView);
//	            //TODO 此种方式构造的item列表的尺寸会有些许误差，待修复
//	            layout.setLayoutParams(new GridView.LayoutParams(itemWidth, itemWidth));
//				return layout;
//			}
//			return null;
//		}
//
//		public void setSlideList(List<AlbumSlide> slideList) {
//			this.slideList = slideList;
//		}
//		
//	}
}

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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.adapter.AlbumSlidesAdapter;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.album.AlbumCommentsApi;
import com.bruce.designer.api.album.AlbumInfoApi;
import com.bruce.designer.api.album.CommentPostApi;
import com.bruce.designer.api.album.PostFavoriteApi;
import com.bruce.designer.api.album.PostLikeApi;
import com.bruce.designer.broadcast.NotificationBuilder;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Activity_AlbumInfo extends BaseActivity implements OnItemClickListener {
	
	private static final int HANDLER_FLAG_INFO = 1;
	private static final int HANDLER_FLAG_SLIDE = 2;
	private static final int HANDLER_FLAG_COMMENTS = 3;
	
	private static final int HANDLER_FLAG_COMMENT_POST = 11;
	private static final int HANDLER_FLAG_LIKE_POST = 21;
	private static final int HANDLER_FLAG_FAVORITE_POST = 31;
	
	private View titlebarView;
	private TextView titleView;
	
	private ImageView designerAvatarView;
	private TextView designerNameView;
	private TextView pubtimeView;
	private TextView albumTitleView;
	private TextView albumContentView;
	
	private Button btnBrowse;
	private Button btnLike;
	private Button btnComment;
	private Button btnFavorite;
	
//	private ImageView coverView;
	
	private PullToRefreshListView pullRefresh;
	private ListView commentListView;
	private AlbumCommentsAdapter commentsAdapter;
	private AlbumSlidesAdapter slideAdapter;
	/*评论框*/
	private EditText commentInput;
	
	private Integer albumId;
	private Integer designerId;
	
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
				case HANDLER_FLAG_COMMENTS:
					Map<String, Object> commentDataMap = (Map<String, Object>) msg.obj;
					if(commentDataMap!=null){
						List<Comment> commentList = (List<Comment>) commentDataMap.get("commentList");
						//先将评论存入db
						AlbumCommentDB.deleteByAlbumId(context, albumId);
						AlbumCommentDB.save(context, commentList);
						
						commentsAdapter.setCommentList(commentList);
						commentsAdapter.notifyDataSetChanged();
					}
					break;
				case HANDLER_FLAG_COMMENT_POST: //评论成功
					NotificationBuilder.createNotification(context, "评论成功...");
					commentInput.setText("");//清空评论框内容
					//隐藏软键盘
					InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(commentInput.getWindowToken(), 0);
					//重新加载评论列表
					getAlbumComments(albumId, 0);
					break;
				case HANDLER_FLAG_LIKE_POST: //赞成功
					NotificationBuilder.createNotification(context, "赞操作成功...");
					break;
				case HANDLER_FLAG_FAVORITE_POST: //收藏成功
					NotificationBuilder.createNotification(context, "收藏成功...");
					break;
				default:
					break;
			}
		};
	};
	
	
	public static void show(Context context, Album album, AlbumAuthorInfo authorInfo){
		Intent intent = new Intent(context, Activity_AlbumInfo.class);
		intent.putExtra(ConstantsKey.BUNDLE_ALBUM_INFO, album);
		intent.putExtra(ConstantsKey.BUNDLE_ALBUM_AUTHOR_INFO, authorInfo);
		context.startActivity(intent);
	}
	
	
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
		
		pullRefresh = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullRefresh.setMode(Mode.PULL_FROM_END);
		commentListView = pullRefresh.getRefreshableView();
		//为listview增加headerView (专辑基础信息)
		LayoutInflater layoutInflate = LayoutInflater.from(context);
		View albumInfoView = layoutInflate.inflate(R.layout.item_album_info_head, null);
		commentListView.addHeaderView(albumInfoView);
		commentListView.setOnItemClickListener(this);
		
		commentsAdapter = new AlbumCommentsAdapter(context, null);
		commentListView.setAdapter(commentsAdapter);
		

		//设计师相关资料
		designerAvatarView = (ImageView) albumInfoView.findViewById(R.id.avatar);
		designerNameView = (TextView) albumInfoView.findViewById(R.id.txtUsername);
		
		//专辑相关资料
		btnBrowse = (Button) albumInfoView.findViewById(R.id.btnBrowse);
		btnLike = (Button) albumInfoView.findViewById(R.id.btnLike);
		btnComment = (Button) albumInfoView.findViewById(R.id.btnComment);
		btnFavorite = (Button) albumInfoView.findViewById(R.id.btnFavorite);
		btnLike.setOnClickListener(onclickListener);
		btnComment.setOnClickListener(onclickListener);
		btnFavorite.setOnClickListener(onclickListener);
		
		//coverView = (ImageView) findViewById(R.id.cover_img);
		GridView gridView = (GridView)albumInfoView.findViewById(R.id.albumSlideImages);
		slideAdapter = new AlbumSlidesAdapter(context, null);
		gridView.setAdapter(slideAdapter);
		
		pubtimeView = (TextView) albumInfoView.findViewById(R.id.txtTime);
		albumTitleView = (TextView) albumInfoView.findViewById(R.id.txtSticker);
		albumContentView = (TextView) albumInfoView.findViewById(R.id.txtContent);

		//评论框
		commentInput = (EditText) findViewById(R.id.commentInput);
		Button btnCommentPost = (Button) findViewById(R.id.btnCommentPost);
		btnCommentPost.setOnClickListener(onclickListener);
		
		Intent intent = getIntent();
		final Album album = (Album) intent.getSerializableExtra(ConstantsKey.BUNDLE_ALBUM_INFO);
		albumId = album.getId();
		designerId = album.getUserId();
		//读取上个activity传入的albumId值
		if(album!=null&&albumId!=null){
			final AlbumAuthorInfo authorInfo = (AlbumAuthorInfo) intent.getSerializableExtra(ConstantsKey.BUNDLE_ALBUM_AUTHOR_INFO);
			if(authorInfo!=null){
				View designerView = (View) albumInfoView.findViewById(R.id.designerContainer);
				designerView.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View view) {
						Activity_UserHome.show(context, designerId, authorInfo.getDesignerNickname(), authorInfo.getDesignerAvatar(), true, authorInfo.isFollowed());
					}
				});
				
				//显示设计师头像
				ImageLoader.getInstance().displayImage(authorInfo.getDesignerAvatar(), designerAvatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
				designerNameView.setText(authorInfo.getDesignerNickname());
			}
			
			pubtimeView.setText(TimeUtil.displayTime(album.getCreateTime()));
			albumTitleView.setText(album.getTitle());
			albumContentView.setText(album.getRemark());
			
			//浏览，评论等交互数
			Button btnBrowse = (Button) albumInfoView.findViewById(R.id.btnBrowse);
			Button btnLike = (Button) albumInfoView.findViewById(R.id.btnLike);
			Button btnComment = (Button) albumInfoView.findViewById(R.id.btnComment);
			Button btnFavorite = (Button) albumInfoView.findViewById(R.id.btnFavorite);
			
			btnBrowse.setText("浏览("+String.valueOf(album.getBrowseCount())+")");
			btnLike.setText("喜欢("+String.valueOf(album.getLikeCount())+")");
			btnComment.setText("评论("+String.valueOf(album.getCommentCount())+")");
			btnFavorite.setText("收藏("+String.valueOf(album.getFavoriteCount())+")");
			
			
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
			//获取实时评论列表
			getAlbumComments(albumId, 0);
			
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
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int index, long id) {
		UiUtil.showShortToast(context, "click arg2:"+index+", arg3:"+id);
		if(id>=0){
			commentInput.setText("回复xxx: ");
		}
		
	}

	
	
	private OnClickListener onclickListener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch (view.getId()) {
			case R.id.btnCommentPost:
				//检查内容不为空
				//启动线程发布评论
				postComment(designerId, commentInput.getText().toString());			
				break;
			case R.id.btnLike:
				postLike(albumId, designerId);
				break;
			case R.id.btnComment:
				break;
			case R.id.btnFavorite:
				postFavorite(albumId, designerId);
				break;
			default:
				break;
			}
		}
	};
	
	
	
	private void getAlbumComments(final int albumId, final int commentsTailId) {
		//启动线程获取评论数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				AlbumCommentsApi api = new AlbumCommentsApi(albumId, commentsTailId);
				ApiResult apiResult = ApiManager.invoke(context, api);
				if(apiResult!=null&&apiResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_COMMENTS);
					message.obj = apiResult.getData();
					message.sendToTarget();
				}else{//发送失败消息
					handler.obtainMessage(0).sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	/**
	 * 发起评论
	 */
	private void postComment(final int toId, final String comment) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				CommentPostApi api = new CommentPostApi(albumId, designerId, toId, comment);
				ApiResult jsonResult = ApiManager.invoke(context, api);
				
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_COMMENT_POST);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	/**
	 * 发起赞
	 */
	private void postLike(final int albumId, final int designerId) {
		//启动线程post数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				PostLikeApi api = new PostLikeApi(albumId, designerId);
				ApiResult jsonResult = ApiManager.invoke(context, api);
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_LIKE_POST);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	/**
	 * 发起收藏
	 */
	private void postFavorite(final int albumId, final int designerId) {
		//启动线程post数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				PostFavoriteApi api = new PostFavoriteApi(albumId, designerId, 1);
				ApiResult jsonResult = ApiManager.invoke(context, api);
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_FAVORITE_POST);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
}

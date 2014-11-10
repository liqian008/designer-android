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
import com.bruce.designer.api.album.PostCommentApi;
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
import com.bruce.designer.model.share.SharedInfo;
import com.bruce.designer.util.StringUtils;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.UniversalImageUtil;
import com.bruce.designer.view.SharePanelView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Activity_AlbumInfo extends BaseActivity implements OnRefreshListener2<ListView>{
	
	private static final int HANDLER_FLAG_INFO = 1;
	private static final int HANDLER_FLAG_COMMENTS = 2;
	
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
	
	private TextView commentView;
	
	private Button btnUserHome;
	
	private Button btnBrowse;
	private Button btnLike;
	private Button btnComment;
	private Button btnFavorite;
	private Button btnShare;
	
	private SharedInfo sharedInfo;
	
	private PullToRefreshListView pullRefresh;
	private ListView commentListView;
	private AlbumCommentsAdapter commentsAdapter;
	private AlbumSlidesAdapter slideAdapter;
	/*评论框*/
	private EditText commentInput;
	
	private Integer albumId;
	private Integer designerId;
	/*回复评论时的接收人，可能不是designerId*/
	private int toId = 0;
	/*评论tailId*/
	private long commentsTailId = 0;
	
	private Handler handler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch(msg.what){
			case HANDLER_FLAG_INFO:
					Map<String, Object> albumDataMap = (Map<String, Object>) msg.obj;
					if(albumDataMap!=null){
						Album albumInfo = (Album) albumDataMap.get("albumInfo");
						List<AlbumSlide> slideList = albumInfo.getSlideList();
						boolean isLiked = albumInfo.isLike();
						boolean isFavorited = albumInfo.isFavorite();
						if(!isLiked){
							btnLike.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_unliked), null,null,null);
							btnLike.setOnClickListener(onclickListener);
						}
						if(!isFavorited){
							btnFavorite.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_unfavorited), null,null,null);
							btnFavorite.setOnClickListener(onclickListener);
						}
						
						//先将slide列表存入db
						AlbumSlideDB.deleteByAlbumId(context, albumId);
						AlbumSlideDB.save(context, slideList);
						
						slideAdapter.setSlideList(slideList);
						slideAdapter.notifyDataSetChanged();
					}
					break;
				case HANDLER_FLAG_COMMENTS:
					//关闭刷新控件
					pullRefresh.onRefreshComplete();
					
					Map<String, Object> commentDataMap = (Map<String, Object>) msg.obj;
					if(commentDataMap!=null){
						//解析响应数据
						Long fromTailId = (Long) commentDataMap.get("fromTailId");
						Long newTailId = (Long) commentDataMap.get("newTailId");
						List<Comment> commentList = (List<Comment>) commentDataMap.get("commentList");
						if(commentList!=null&&commentList.size()>0){
							if(newTailId!=null&&newTailId>0){//还有可加载的数据
								commentsTailId = newTailId;
								pullRefresh.setMode(Mode.BOTH);
							}else{
								commentsTailId = 0;
								pullRefresh.setMode(Mode.PULL_FROM_START);//禁用上拉刷新
							}
							
							List<Comment> oldCommentList = commentsAdapter.getCommentList();
							//判断加载位置，以确定是list增量还是覆盖
							boolean fallloadAppend = fromTailId!=null&&fromTailId>0;
							if(fallloadAppend){//上拉加载更多，需添加至list的结尾
								oldCommentList.addAll(commentList);
							}else{//下拉加载，需覆盖原数据
								//先将评论存入db
								AlbumCommentDB.deleteByAlbumId(context, albumId);
								AlbumCommentDB.save(context, commentList);
								
								oldCommentList = null;
								oldCommentList = commentList; 
							}
							commentsAdapter.setCommentList(oldCommentList);
							commentsAdapter.notifyDataSetChanged();
						}
					}
					break;
				case HANDLER_FLAG_COMMENT_POST: //评论成功
					NotificationBuilder.createNotification(context, "评论成功...");
					commentInput.setText("");//清空评论框内容
					//隐藏软键盘
					InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(commentInput.getWindowToken(), 0);
					//重新加载评论列表
					getAlbumComments(0);
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
		titleView.setText("作品辑");
		
		pullRefresh = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullRefresh.setMode(Mode.BOTH);
		pullRefresh.setOnRefreshListener(this);
		commentListView = pullRefresh.getRefreshableView();
		//为listview增加headerView (专辑基础信息)
		LayoutInflater layoutInflate = LayoutInflater.from(context);
		View albumInfoView = layoutInflate.inflate(R.layout.item_designer_album_view, null);
		commentListView.addHeaderView(albumInfoView);
		
		commentsAdapter = new AlbumCommentsAdapter(context, null);
		commentListView.setAdapter(commentsAdapter);

		//设计师相关资料
		designerAvatarView = (ImageView) albumInfoView.findViewById(R.id.avatar);
		designerNameView = (TextView) albumInfoView.findViewById(R.id.txtUsername);
		
		btnUserHome = (Button) albumInfoView.findViewById(R.id.btnUserHome);
		
		//专辑相关资料
		btnBrowse = (Button) albumInfoView.findViewById(R.id.btnBrowse);
		btnLike = (Button) albumInfoView.findViewById(R.id.btnLike);
		btnComment = (Button) albumInfoView.findViewById(R.id.btnComment);
		btnFavorite = (Button) albumInfoView.findViewById(R.id.btnFavorite);
		btnShare = (Button) albumInfoView.findViewById(R.id.btnShare);
		
		btnComment.setOnClickListener(onclickListener);
		btnShare.setOnClickListener(onclickListener);
		
		//coverView = (ImageView) findViewById(R.id.cover_img);
		GridView gridView = (GridView)albumInfoView.findViewById(R.id.albumSlideImages);
		slideAdapter = new AlbumSlidesAdapter(context, null);
		gridView.setAdapter(slideAdapter);
		
		pubtimeView = (TextView) albumInfoView.findViewById(R.id.txtTime);
		albumTitleView = (TextView) albumInfoView.findViewById(R.id.txtSticker);
		albumContentView = (TextView) albumInfoView.findViewById(R.id.txtContent);
		commentView = (TextView) albumInfoView.findViewById(R.id.txtComment);
		commentView.setVisibility(View.VISIBLE);
		
		//评论框
		commentInput = (EditText) findViewById(R.id.commentInput);
		Button btnCommentPost = (Button) findViewById(R.id.btnCommentPost);
		btnCommentPost.setOnClickListener(onclickListener);
		
		Intent intent = getIntent();
		final Album album = (Album) intent.getSerializableExtra(ConstantsKey.BUNDLE_ALBUM_INFO);
		if(album==null){
			//TODO 如果是通过消息或push进来，只有一个albumId，无其他数据
			
		}else{//intent中传的是album
			albumId = album.getId();
			designerId = album.getUserId();
			
			//读取上个activity传入的authorInfo值
			if(album!=null&&albumId!=null){
				//构造分享对象
				String itemMobileUrl = album.getItemMobileUrl();
				sharedInfo = new SharedInfo(album.getTitle(), album.getRemark(), itemMobileUrl, album.getCoverSmallImg());
				
				final AlbumAuthorInfo authorInfo = (AlbumAuthorInfo) intent.getSerializableExtra(ConstantsKey.BUNDLE_ALBUM_AUTHOR_INFO);
				if(authorInfo!=null){
					View designerView = (View) albumInfoView.findViewById(R.id.designerContainer);
					
					//用户主页按钮的点击事件
					OnSingleClickListener userHomeOnclickListener = new OnSingleClickListener() {
						@Override
						public void onSingleClick(View view) {
							//跳转至个人资料页
							Activity_UserHome.show(context, album.getUserId(), authorInfo.getDesignerNickname(), authorInfo.getDesignerAvatar(), true, authorInfo.isFollowed());
						}
					};
					designerView.setOnClickListener(userHomeOnclickListener);
					if(btnUserHome!=null){
						btnUserHome.setOnClickListener(userHomeOnclickListener);
					}
					
					//显示设计师头像
					ImageLoader.getInstance().displayImage(authorInfo.getDesignerAvatar(), designerAvatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
					designerNameView.setText(authorInfo.getDesignerNickname());
				}
				
				pubtimeView.setText(TimeUtil.displayTime(album.getCreateTime()));
				albumTitleView.setText(album.getTitle());
//				albumContentView.setText(album.getRemark());
				//专辑描述
				String remark = album.getRemark();
				if(!StringUtils.isBlank(remark)){
					remark = remark.replace("\r", "");
					remark = remark.replace("\n", "");
					if(remark.length()>30){
						remark = remark.substring(0, 29);
					}
				}else{
					remark = album.getTitle();
				}
				albumContentView.setText(remark +"......查看详情");
				albumContentView.setOnClickListener(new OnSingleClickListener() {
					public void onSingleClick(View v) {
						albumContentView.setText(album.getRemark());
					}
				});
				
				//浏览，评论等交互数
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
				
				//获取db中的评论列表
				List<Comment> commentList= AlbumCommentDB.queryByAlbumId(context, albumId);
				if(commentList!=null&&commentList.size()>0){//展示db中数据
					commentsAdapter.setCommentList(commentList);
					commentsAdapter.notifyDataSetChanged();
				}
				
				//获取实时图片列表
				getAlbumInfo(album.getId());
				
				pullRefresh.setRefreshing(false);
			}
		}
	}
		
	
	private void getAlbumInfo(final int albumId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				AlbumInfoApi api = new AlbumInfoApi(albumId);
				ApiResult jsonResult = ApiManager.invoke(context, api);
				
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(HANDLER_FLAG_INFO);
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
			final Comment comment = getItem(position);
			//TODO 使用convertView
			CommentViewHolder viewHolder = null;
			if(convertView==null){
				viewHolder = new CommentViewHolder();
				if(comment!=null){
					convertView = LayoutInflater.from(context).inflate(R.layout.item_comment_view, null);
					
					viewHolder.commentItemView = convertView;					
					viewHolder.avatarView = (ImageView) convertView.findViewById(R.id.avatar);
					viewHolder.commentUsernameView = (TextView) convertView.findViewById(R.id.commentUserame);
					viewHolder.commentContentView = (TextView) convertView.findViewById(R.id.commentContent);
					viewHolder.commentTimeView = (TextView) convertView.findViewById(R.id.commentTime);
					
					convertView.setTag(viewHolder);
				}
			}else{
				viewHolder = (CommentViewHolder) convertView.getTag();
			}
			//填充数据
			if(comment.getUserHeadImg()!=null){//头像
				ImageLoader.getInstance().displayImage(comment.getUserHeadImg(), viewHolder.avatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
			}
			viewHolder.commentUsernameView.setText(comment.getNickname());
			viewHolder.commentContentView.setText(comment.getComment());
			viewHolder.commentTimeView.setText(TimeUtil.displayTime(comment.getCreateTime()));
			
			//头像&点击事件
			viewHolder.commentItemView.setOnClickListener(new OnSingleClickListener() {
				@Override
				public void onSingleClick(View v) {
					toId = comment.getFromId();
//					UiUtil.showShortToast(context, "toId: "+ toId);
					commentInput.setText("回复"+comment.getNickname()+": ");
				}
			});
			//头像&点击事件
			viewHolder.avatarView.setOnClickListener(new OnSingleClickListener() {
				@Override
				public void onSingleClick(View v) {
					Activity_UserHome.show(context, comment.getFromId(), comment.getNickname(), comment.getUserHeadImg(), false, false);
				}
			});
			return convertView;
		}
	}
	
	
	private OnClickListener onclickListener = new OnSingleClickListener() {
		@Override
		public void onSingleClick(View view) {
			switch (view.getId()) {
			case R.id.btnCommentPost:
				//检查内容不为空
				//启动线程发布评论
				postComment(designerId, toId, commentInput.getText().toString());
				break;
			case R.id.btnComment:
				break;
			case R.id.btnLike:
				postLike(albumId, designerId);
				btnLike.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_like), null,null,null);
				btnLike.setOnClickListener(null);
				break;
			case R.id.btnFavorite:
				postFavorite(albumId, designerId);
				btnFavorite.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_favorite), null,null,null);
				btnFavorite.setOnClickListener(null);
				break;
			case R.id.btnShare:
				//构造分享对象
				SharePanelView sharePanel = new SharePanelView(context, sharedInfo);
				sharePanel.show(findViewById(R.id.commentPanel));
				break;
			default:
				break;
			}
		}
	};
	
	
	/**
	 * 加载评论列表
	 * @param albumId
	 * @param commentsTailId
	 */
	private void getAlbumComments(final long tailId) {
		//启动线程获取评论数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				AlbumCommentsApi api = new AlbumCommentsApi(albumId, tailId);
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
	private void postComment(final int designerId, final int toId, final String comment) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				PostCommentApi api = new PostCommentApi(albumId, designerId, toId, comment);
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
	

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getAlbumComments(0);//下拉刷新，tailId为0
	}


	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getAlbumComments(commentsTailId);//加载更多，tailId为上次缓存的
	}
	
	/**
	 * viewHolder
	 * @author liqian
	 *
	 */
	static class CommentViewHolder {
		public View commentItemView;
		public ImageView avatarView;
		public TextView commentUsernameView;
		public TextView commentContentView;
		public TextView commentTimeView;
	}

}

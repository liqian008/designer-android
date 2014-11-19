package com.bruce.designer.view.holder;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.activity.Activity_AlbumInfo;
import com.bruce.designer.activity.Activity_UserHome;
import com.bruce.designer.adapter.AlbumSlidesAdapter;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.AlbumAuthorInfo;
import com.bruce.designer.model.AlbumSlide;
import com.bruce.designer.model.share.GenericSharedInfo;
import com.bruce.designer.util.StringUtils;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.UniversalImageUtil;
import com.bruce.designer.view.SharePanelView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AlbumViewHolder {
	
	// 整体item
	public View albumItemView;
	// 设计师item
	public View designerView;
	// 设计师头像
	public ImageView avatarView;
	// 设计师姓名
	public TextView usernameView;
	// 发布时间
	public TextView pubtimeView;
	// 封面大图view
	public ImageView coverView;
	//列表图片
	public GridView gridView;
	// 专辑title
	public TextView titleView;
	// 专辑描述
	public TextView contentView;
	// 专辑统计
	public Button btnBrowse;
	public Button btnLike;
	public Button btnComment;
	public Button btnFavorite;
	public Button btnShare;
	// 评论数量
	public TextView commentView;
	/*个人主页按钮*/
	public Button btnUserHome;
	
	
	/**
	 * 填充数据
	 * @param context
	 * @param album
	 */
	public void fillDisplayData(final Context context, final Album album){
		if(album!=null){
			//构造显示数据
			pubtimeView.setText(TimeUtil.displayTime(album.getCreateTime()));
			
			final AlbumAuthorInfo authorInfo = album.getAuthorInfo();
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
			
			if(authorInfo!=null){
				//显示头像
				ImageLoader.getInstance().displayImage(authorInfo.getDesignerAvatar(), avatarView, UniversalImageUtil.DEFAULT_AVATAR_DISPLAY_OPTION);
				//显示昵称
				usernameView.setText(authorInfo.getDesignerNickname());
			}
			
			//专辑title
			titleView.setText(album.getTitle());
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
			contentView.setText(remark +"......查看详情");
			
			//专辑统计
			btnBrowse.setText("浏览("+String.valueOf(album.getBrowseCount())+")");
			btnLike.setText("喜欢("+String.valueOf(album.getLikeCount())+")");
			btnComment.setText("评论("+String.valueOf(album.getCommentCount())+")");
			btnFavorite.setText("收藏("+String.valueOf(album.getFavoriteCount())+")");
			
			if(album.isFavorite()){
				btnFavorite.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_favorited), null,null,null);
			}else{
				btnFavorite.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_unfavorited), null,null,null);
			}
			if(album.isLike()){
				btnLike.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_liked), null,null,null);
			}else{
				btnLike.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.icon_unliked), null,null,null);
			}
			
			
//			//评论数量
//			if(commentView!=null){
//				if(album.getCommentCount()>0){
//					commentView.setText("查看全部"+album.getCommentCount()+"条评论");
//				}else{
//					commentView.setVisibility(View.GONE); 
//				}
//			}
			
			if(coverView!=null){
				//单独处理cover图片
				ImageLoader.getInstance().displayImage(album.getCoverMediumImg(), coverView, UniversalImageUtil.DEFAULT_DISPLAY_OPTION);
			}
			
			if(gridView!=null){
				//单独处理缩略图列表
				List<AlbumSlide> slideList = album.getSlideList();
				AlbumSlidesAdapter slideAdapter = new AlbumSlidesAdapter(context, slideList);
				gridView.setAdapter(slideAdapter);
			}
			
			albumItemView.setOnClickListener(new OnSingleClickListener() {
				@Override
				public void onSingleClick(View view) {
					Activity_AlbumInfo.show(context, album, authorInfo);
				}
			});
		}
	}
	
	public synchronized long addLike(Album album, int step){
		long likeCount = album.getLikeCount();
		long newLikeCount = likeCount + step;
		btnLike.setText("喜欢("+String.valueOf(newLikeCount)+")");
		return newLikeCount;
	}
	
	public synchronized long addFavorite(Album album, int step){
		long favoriteCount = album.getFavoriteCount();
		long newFavoriteCount = favoriteCount + step;
		btnFavorite.setText("收藏("+String.valueOf(newFavoriteCount)+")");
		return newFavoriteCount;
	}
	
	
}
package com.bruce.designer.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.activity.Activity_AlbumInfo;
import com.bruce.designer.activity.Activity_UserHome;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.AlbumAuthorInfo;
import com.bruce.designer.model.AlbumSlide;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.UniversalImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 用于展示设计师的专辑列表的Listadapter
 * @author liqian
 *
 */
public class DesignerAlbumsAdapter extends BaseAdapter {
		private List<Album> albumList;
		private Context context;
		
		public DesignerAlbumsAdapter(Context context, List<Album> albumList) {
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
				List<AlbumSlide> slideList = album.getSlideList();
				AlbumSlidesAdapter slideAdapter = new AlbumSlidesAdapter(context, slideList);
				gridView.setAdapter(slideAdapter);
				
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

				TextView pubtimeView = (TextView) albumItemView.findViewById(R.id.txtTime);
				pubtimeView.setText(TimeUtil.displayTime(album.getCreateTime()));
				
				TextView albumTitleView = (TextView) albumItemView.findViewById(R.id.txtSticker);
				albumTitleView.setText(album.getTitle());
				
				TextView albumContentView = (TextView) albumItemView.findViewById(R.id.txtContent);
				albumContentView.setText(album.getRemark());
				
				//浏览，评论等交互数
				Button btnBrowse = (Button) albumItemView.findViewById(R.id.btnBrowse);
				Button btnLike = (Button) albumItemView.findViewById(R.id.btnLike);
				Button btnComment = (Button) albumItemView.findViewById(R.id.btnComment);
				Button btnFavorite = (Button) albumItemView.findViewById(R.id.btnFavorite);
				
				btnBrowse.setText("浏览("+String.valueOf(album.getBrowseCount())+")");
				btnLike.setText("喜欢("+String.valueOf(album.getLikeCount())+")");
				btnComment.setText("评论("+String.valueOf(album.getCommentCount())+")");
				btnFavorite.setText("收藏("+String.valueOf(album.getFavoriteCount())+")");
				
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
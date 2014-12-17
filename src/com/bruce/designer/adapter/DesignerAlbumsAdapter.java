package com.bruce.designer.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.bruce.designer.R;
import com.bruce.designer.constants.ConstantsStatEvent;
import com.bruce.designer.listener.IOnAlbumListener;
import com.bruce.designer.listener.OnSingleClickListener;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.share.GenericSharedInfo;
import com.bruce.designer.view.holder.AlbumViewHolder;

/**
 * 用于展示设计师的专辑列表的Listadapter
 * @author liqian
 *
 */
public class DesignerAlbumsAdapter extends BaseAdapter {
	
		private List<Album> albumList;
		private Context context;
		private IOnAlbumListener onAlbumListener;
		
//		public DesignerAlbumsAdapter(Context context, List<Album> albumList) {
//			this.context = context;
//			this.albumList = albumList;
//		}
		
		
		public DesignerAlbumsAdapter(Context context, List<Album> albumList, IOnAlbumListener onShareListener) {
			this.context = context;
			this.albumList = albumList;
			this.onAlbumListener = onShareListener;
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
			AlbumViewHolder viewHolder = null;  
			final Album album = getItem(position);
			if(convertView==null){
				viewHolder=new AlbumViewHolder();
				//使用convertView
				if(album!=null){
//					View albumItemView = null;
					convertView = LayoutInflater.from(context).inflate(R.layout.item_designer_album_view, null);
					viewHolder.albumItemView = convertView;
					
					/*构造slide数据*/
					viewHolder.gridView = (GridView) convertView.findViewById(R.id.albumSlideImages);
					
					//发布时间
					viewHolder.pubtimeView = (TextView) convertView.findViewById(R.id.txtTime);
					viewHolder.designerView = (View) convertView.findViewById(R.id.designerContainer); 
					//设计师头像
					viewHolder.avatarView = (ImageView) convertView.findViewById(R.id.avatar);
					//设计师姓名
					viewHolder.usernameView = (TextView) convertView.findViewById(R.id.txtUsername);
					//专辑title
					viewHolder.titleView = (TextView) convertView.findViewById(R.id.txtSticker);
					viewHolder.priceView = (TextView) convertView.findViewById(R.id.txtPrice);
					viewHolder.contentView = (TextView) convertView.findViewById(R.id.txtContent);
					viewHolder.btnBrowse = (Button) convertView.findViewById(R.id.btnBrowse);
					viewHolder.btnLike = (Button) convertView.findViewById(R.id.btnLike);
					viewHolder.btnComment = (Button) convertView.findViewById(R.id.btnComment);
					viewHolder.btnFavorite = (Button) convertView.findViewById(R.id.btnFavorite);
					viewHolder.btnShare = (Button) convertView.findViewById(R.id.btnShare);
					//评论数量
					viewHolder.commentView = (TextView) convertView.findViewById(R.id.txtComment);
					
					viewHolder.btnUserHome = (Button) convertView.findViewById(R.id.btnUserHome);
					
					convertView.setTag(viewHolder);
				}
			}else{
				viewHolder = (AlbumViewHolder) convertView.getTag();
			}
			//构造显示数据
			viewHolder.fillDisplayData(context, album);
			
			if(onAlbumListener!=null){//将用户的点击操作事件传出，交由外层处理逻辑
				//构造分享对象
				final GenericSharedInfo generalSharedInfo = album.getGenericSharedInfo();
				viewHolder.btnShare.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View v) {
						StatService.onEvent(context, ConstantsStatEvent.EVENT_SHARE, "专辑列表中点击分享");
						
						onAlbumListener.onShare(generalSharedInfo);
					}
				});
				
				//评论事件
				viewHolder.btnComment.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View v) {
						StatService.onEvent(context, ConstantsStatEvent.EVENT_COMMENT, "专辑列表中点击评论");
						
						onAlbumListener.onComment(album);
					}
				});
				
				//收藏&取消收藏事件
				viewHolder.btnFavorite.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View v) {
						boolean favorited = album.isFavorite();
						if(favorited){
							StatService.onEvent(context, ConstantsStatEvent.EVENT_UNFOLLOW, "专辑列表中取消收藏");
						}else{
							StatService.onEvent(context, ConstantsStatEvent.EVENT_FOLLOW, "专辑列表中点击收藏");
						}
						
						int mode =  favorited?0:1;
						album.setFavorite(!favorited);
						onAlbumListener.onFavorite(album.getId(), album.getUserId(), mode);
					}
				});
				
				//赞&取消收藏事件
				viewHolder.btnLike.setOnClickListener(new OnSingleClickListener() {
					@Override
					public void onSingleClick(View v) {
						boolean liked = album.isLike();
						if(liked){
							StatService.onEvent(context, ConstantsStatEvent.EVENT_UNLIKE, "专辑列表中取消赞");
						}else{
							StatService.onEvent(context, ConstantsStatEvent.EVENT_LIKE, "专辑列表中点击赞");
						}
						
						int mode =  liked?0:1;
						album.setLike(!liked);
						onAlbumListener.onLike(album.getId(), album.getUserId(), mode);
					}
				});
			}
			
			return convertView;
		}
	}
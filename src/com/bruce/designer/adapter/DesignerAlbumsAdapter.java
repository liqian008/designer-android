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

import com.bruce.designer.R;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.AlbumSlide;
import com.bruce.designer.view.holder.AlbumViewHolder;

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
			AlbumViewHolder viewHolder = null;  
			final Album album = getItem(position);
			if(convertView==null){
				viewHolder=new AlbumViewHolder();
				//TODO 使用convertView
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
					viewHolder.contentView = (TextView) convertView.findViewById(R.id.txtContent);
					viewHolder.btnBrowse = (Button) convertView.findViewById(R.id.btnBrowse);
					viewHolder.btnLike = (Button) convertView.findViewById(R.id.btnLike);
					viewHolder.btnComment = (Button) convertView.findViewById(R.id.btnComment);
					viewHolder.btnFavorite = (Button) convertView.findViewById(R.id.btnFavorite);
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
			return convertView;
		}
	}
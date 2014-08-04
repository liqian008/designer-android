package com.bruce.designer.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.AlbumSlide;
import com.bruce.designer.util.TimeUtil;

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

				TextView usernameView = (TextView) albumItemView.findViewById(R.id.txtUsername);
				usernameView.setText(album.getTitle());
				
				TextView pubtimeView = (TextView) albumItemView.findViewById(R.id.txtTime);
				pubtimeView.setText(TimeUtil.displayTime(album.getCreateTime()));
				
				TextView albumTitleView = (TextView) albumItemView.findViewById(R.id.txtSticker);
				albumTitleView.setText(album.getTitle());
				
				TextView albumContentView = (TextView) albumItemView.findViewById(R.id.txtContent);
				albumContentView.setText(album.getRemark());
				
				return albumItemView;
			}
			return null;
		}
	}
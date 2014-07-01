package com.bruce.designer.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bruce.designer.model.Album;

public class AlbumDB {
	
//	public static int deleteAll() {
//		SQLiteDatabase db = DBHelper.getDB();
//		int result = db.delete("notice_info", null, null);
//		return result;
//	}
	
	public static final String TB_ALBUM_NAME = "tb_album";

	public static List<Album> queryAll(Context context) {
		// 读取SQLite里面的数据
		SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();

		// 这里是把SQLite里面的数据进行排序，依据ID由大到小排序，这样可以保证ListView展示在最上面的一条 数据是最新的一条
		Cursor cursor = db.query(TB_ALBUM_NAME, null, null, null, null, null, "id DESC");

		List<Album> albumList = new ArrayList<Album>();
		while (cursor.moveToNext()) {
			Album album = new Album();
			//System.out.println("<<<<<<<<<" + album.getTitle());
			album.setId(cursor.getInt(cursor.getColumnIndex("id")));
			album.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			album.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			album.setLink(cursor.getString(cursor.getColumnIndex("link")));
			album.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
			album.setCoverLargeImg(cursor.getString(cursor.getColumnIndex("cover_large_img")));
			album.setCoverMediumImg(cursor.getString(cursor.getColumnIndex("cover_medium_img")));
			album.setCoverSmallImg(cursor.getString(cursor.getColumnIndex("cover_small_img")));
			album.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
			album.setUpdateTime(cursor.getLong(cursor.getColumnIndex("update_time")));
			albumList.add(album);
		}
		return albumList;
	}
	
	
	public static int save(Context context, List<Album> albumList) {
		if(albumList!=null&&albumList.size()>0){
			SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
			
			for(Album album: albumList){
				ContentValues values = new ContentValues();  
				//数据准备
				values.put("id", album.getId());  
		        values.put("title", album.getTitle());  
		        values.put("remark", album.getRemark());
		        values.put("link", album.getLink());
		        values.put("user_id", album.getUserId());
		        values.put("status", album.getStatus());
		        values.put("cover_large_img", album.getCoverLargeImg());
		        values.put("cover_medium_img", album.getCoverMediumImg());
		        values.put("cover_small_img", album.getCoverSmallImg());
		        values.put("create_time", album.getCreateTime());
		        values.put("update_time", album.getUpdateTime());
			
		        db.insert(TB_ALBUM_NAME,  null, values);
//			db.execSQL("insert into tb_album (title, remark, link, user_id, status, cover_large_img, cover_medium_img, cover_small_img, create_time, update_time) values (?,?,?,?,?,?,?,?,?,?)", bindArgs);
			}
			return albumList.size();
		}
		return 0;
	}
}
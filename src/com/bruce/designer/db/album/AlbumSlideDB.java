package com.bruce.designer.db.album;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bruce.designer.db.DBHelper;
import com.bruce.designer.model.AlbumSlide;

public class AlbumSlideDB {
	
	/*专辑slide*/
	public static final String TB_ALBUM_SLIDE = "tb_album_slide";

	public static List<AlbumSlide> queryByAlbumId(Context context, Integer albumId) {
		List<AlbumSlide> albumSlideList = new ArrayList<AlbumSlide>();
		if(albumId!=null){
			// 读取SQLite里面的数据
			SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
	
			// 这里是把SQLite里面的数据进行排序，依据ID由大到小排序，这样可以保证ListView展示在最上面的一条 数据是最新的一条
			Cursor cursor = db.query(TB_ALBUM_SLIDE, null, "album_id=?", new String[]{String.valueOf(albumId)}, null, null, "is_cover desc, id asc");
	
			while (cursor.moveToNext()) {
				AlbumSlide albumSlide = new AlbumSlide();
				albumSlide.setId(cursor.getInt(cursor.getColumnIndex("id")));
				albumSlide.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				albumSlide.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
				albumSlide.setAlbumId(cursor.getInt(cursor.getColumnIndex("album_id")));
				albumSlide.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
				albumSlide.setIsCover(cursor.getShort(cursor.getColumnIndex("is_cover")));
				albumSlide.setSlideLargeImg(cursor.getString(cursor.getColumnIndex("slide_large_img")));
				albumSlide.setSlideMediumImg(cursor.getString(cursor.getColumnIndex("slide_medium_img")));
				albumSlide.setSlideSmallImg(cursor.getString(cursor.getColumnIndex("slide_small_img")));
				albumSlide.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
				albumSlide.setUpdateTime(cursor.getLong(cursor.getColumnIndex("update_time")));
				albumSlideList.add(albumSlide);
			}
		}
		return albumSlideList;
	}
	
	
	public static int save(Context context, List<AlbumSlide> albumSlideList) {
		if(albumSlideList!=null&&albumSlideList.size()>0){
			SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
			
			for(AlbumSlide albumSlide: albumSlideList){
				ContentValues values = new ContentValues();  
				//数据准备
				values.put("id", albumSlide.getId());  
		        values.put("title", albumSlide.getTitle());  
		        values.put("remark", albumSlide.getRemark());
		        values.put("album_id", albumSlide.getAlbumId());
		        values.put("user_id", albumSlide.getUserId());
		        values.put("is_cover", albumSlide.getIsCover());
		        values.put("slide_large_img", albumSlide.getSlideLargeImg());
		        values.put("slide_medium_img", albumSlide.getSlideMediumImg());
		        values.put("slide_small_img", albumSlide.getSlideSmallImg());
		        values.put("create_time", albumSlide.getCreateTime());
		        values.put("update_time", albumSlide.getUpdateTime());
			
		        db.insert(TB_ALBUM_SLIDE,  null, values);
			}
			return albumSlideList.size();
		}
		return 0;
	}
	
	/**
	 * 根据albumId删除
	 * @param context
	 * @param albumId
	 * @return
	 */
	public static int deleteByAlbumId(Context context, Integer albumId) {
		if(albumId!=null){
			SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
			int result = db.delete(TB_ALBUM_SLIDE, "album_id=?", new String[]{String.valueOf(albumId)});
			return result;
		}
		return 0;
	}
}
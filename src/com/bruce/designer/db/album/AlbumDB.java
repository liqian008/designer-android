package com.bruce.designer.db.album;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bruce.designer.db.DBHelper;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.AlbumAuthorInfo;

public class AlbumDB {
	
//	public static int deleteAll() {
//		SQLiteDatabase db = DBHelper.getDB();
//		int result = db.delete("notice_info", null, null);
//		return result;
//	}
	
	/*最新专辑*/
	public static final String TB_ALBUM_LATEST = "tb_album_latest";
	/*系统推荐专辑*/
	public static final String TB_ALBUM_RECOMMEND = "tb_album_recommend";
	/*我关注的的专辑*/
	public static final String TB_ALBUM_FOLLOW = "tb_album_follow";
//	/*我的专辑*/
//	public static final String TB_ALBUM = "tb_album";

	public static List<Album> queryAllLatest(Context context) {
		return queryAll(context, TB_ALBUM_LATEST);
	}
	
	public static List<Album> queryAllRecommend(Context context) {
		return queryAll(context, TB_ALBUM_RECOMMEND);
	}
	
	public static List<Album> queryAllFollow(Context context) {
		return queryAll(context, TB_ALBUM_FOLLOW);
	}
	
	private static List<Album> queryAll(Context context, String tableName) {
		// 读取SQLite里面的数据
		SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();

		// 这里是把SQLite里面的数据进行排序，依据ID由大到小排序，这样可以保证ListView展示在最上面的一条 数据是最新的一条
		Cursor cursor = db.query(tableName, null, null, null, null, null, "id DESC");

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
			
			album.setBrowseCount(cursor.getInt(cursor.getColumnIndex("browse_count")));
			album.setCommentCount(cursor.getInt(cursor.getColumnIndex("comment_count")));
			album.setLikeCount(cursor.getInt(cursor.getColumnIndex("like_count")));
			album.setFavoriteCount(cursor.getInt(cursor.getColumnIndex("favorite_count")));
			
			
			String designerAvatar = cursor.getString(cursor.getColumnIndex("designer_avatar"));
			String designerNickname = cursor.getString(cursor.getColumnIndex("designer_nickname"));
			
			boolean isFollowed = (cursor.getInt(cursor.getColumnIndex("designer_follow_status"))==1)?true:false;
			AlbumAuthorInfo authorInfo = new AlbumAuthorInfo(designerAvatar, designerNickname, isFollowed);
			album.setAuthorInfo(authorInfo);
			
			album.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
			album.setUpdateTime(cursor.getLong(cursor.getColumnIndex("update_time")));
			albumList.add(album);
		}
		return albumList;
	}
	
	
	public static int saveAlbumsByTab(Context context, List<Album> albumList, int tabIndex) {
		switch(tabIndex){
			case 0: return save(context, TB_ALBUM_LATEST, albumList);
			case 1:	return save(context, TB_ALBUM_RECOMMEND, albumList);
			case 2:	return save(context, TB_ALBUM_FOLLOW, albumList);
			default: return 0;
		}
	}
	
	public static int save(Context context, String tableName, List<Album> albumList) {
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
		        
		        values.put("browse_count", album.getBrowseCount());
		        values.put("comment_count", album.getCommentCount());
		        values.put("like_count", album.getLikeCount());
		        values.put("favorite_count", album.getFavoriteCount());
		        
		        AlbumAuthorInfo authorInfo = album.getAuthorInfo();
		        if(authorInfo!=null){
		        	values.put("designer_avatar", album.getAuthorInfo().getDesignerAvatar());
		        	values.put("designer_nickname", album.getAuthorInfo().getDesignerNickname());
		        	//关注状态
		        	int followStatus = album.getAuthorInfo().isFollowed()?1:0;
		        	values.put("designer_follow_status", followStatus);
		        }
		        
		        values.put("create_time", album.getCreateTime());
		        values.put("update_time", album.getUpdateTime());
			
		        db.insert(tableName,  null, values);
//			db.execSQL("insert into tb_album (title, remark, link, user_id, status, cover_large_img, cover_medium_img, cover_small_img, create_time, update_time) values (?,?,?,?,?,?,?,?,?,?)", bindArgs);
			}
			return albumList.size();
		}
		return 0;
	}
}
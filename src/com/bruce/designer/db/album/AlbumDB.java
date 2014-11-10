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
import com.bruce.designer.model.AlbumSlide;

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
	/*本周热门专辑*/
	public static final String TB_HOT_ALBUM_WEEKLY = "tb_hot_album_weekly";
	/*本月热门专辑*/
	public static final String TB_HOT_ALBUM_MONTHLY = "tb_hot_album_monthly";
	/*本年热门专辑*/
	public static final String TB_HOT_ALBUM_YEARLY = "tb_hot_album_yearly";
	
	public static List<Album> queryAllLatest(Context context) {
		return queryAll(context, TB_ALBUM_LATEST, true);
	}
	
	public static List<Album> queryAllRecommend(Context context) {
		return queryAll(context, TB_ALBUM_RECOMMEND, true);
	}
	
	public static List<Album> queryAllFollow(Context context) {
		return queryAll(context, TB_ALBUM_FOLLOW, true);
	}
	
	public static List<Album> queryHotWeekly(Context context) {
		return queryAll(context, TB_HOT_ALBUM_WEEKLY, true);
	}
	
	public static List<Album> queryHotMonthly(Context context) {
		return queryAll(context, TB_HOT_ALBUM_MONTHLY, true);
	}
	
	public static List<Album> queryHotYearly(Context context) {
		return queryAll(context, TB_HOT_ALBUM_YEARLY, true);
	}
	
	private static List<Album> queryAll(Context context, String tableName) {
		return queryAll(context, tableName, false);
	}
		
		
	private static List<Album> queryAll(Context context, String tableName, boolean initSlides) {
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
			
			//需要加载作品列表
			if(initSlides){
				List<AlbumSlide> albumSlideList = AlbumSlideDB.queryByAlbumId(context, album.getId());
				album.setSlideList(albumSlideList);
			}
			albumList.add(album);
		}
		return albumList;
	}
	
	
	public static int saveAlbumsByTab(Context context, List<Album> albumList, int tabIndex) {
		switch(tabIndex){
			case 0:	return save(context, TB_ALBUM_RECOMMEND, albumList);
			case 1: return save(context, TB_ALBUM_LATEST, albumList);
			case 2:	return save(context, TB_ALBUM_FOLLOW, albumList);
			default: return 0;
		}
	}
	
	public static int saveHotAlbumsByTab(Context context, List<Album> albumList, int tabIndex) {
		switch(tabIndex){
			case 0:	return save(context, TB_HOT_ALBUM_WEEKLY, albumList);
			case 1: return save(context, TB_HOT_ALBUM_MONTHLY, albumList);
			case 2:	return save(context, TB_HOT_ALBUM_YEARLY, albumList);
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
			
		        db.replace(tableName,  null, values);
		        
		        //保存slide信息
		        List<AlbumSlide> albumSlideList = album.getSlideList();
		        if(albumSlideList!=null&&albumSlideList.size()>0){
		        	AlbumSlideDB.save(context, albumSlideList);
		        }
		        
			}
			return albumList.size();
		}
		return 0;
	}
	
	
	public static int deleteByTab(Context context, int tabIndex){
		switch(tabIndex){
		case 0:	return delete(context, TB_ALBUM_RECOMMEND);
		case 1: return delete(context, TB_ALBUM_LATEST);
		case 2:	return delete(context, TB_ALBUM_FOLLOW);
		default: return 0;
		}
	}
	
	public static int deleteHotByTab(Context context, int tabIndex){
		switch(tabIndex){
		case 0:	return delete(context, TB_HOT_ALBUM_WEEKLY);
		case 1: return delete(context, TB_HOT_ALBUM_MONTHLY);
		case 2:	return delete(context, TB_HOT_ALBUM_YEARLY);
		default: return 0;
		}
	}
	
	
	public static int delete(Context context, String tableName) {
		if(tableName!=null){
			SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
			int result = db.delete(tableName, null, null);
			return result;
		}
		return 0;
	}
}
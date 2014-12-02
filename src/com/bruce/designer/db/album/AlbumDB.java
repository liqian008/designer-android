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
import com.bruce.designer.model.share.GenericSharedInfo;
import com.bruce.designer.util.StringUtils;

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
		Cursor cursor = db.query(tableName, null, null, null, null, null, "sort DESC");

		List<Album> albumList = new ArrayList<Album>();
		while (cursor.moveToNext()) {
			Album album = new Album();
			//System.out.println("<<<<<<<<<" + album.getTitle());
			album.setId(cursor.getInt(cursor.getColumnIndex("id")));
			album.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			album.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			album.setPrice(cursor.getLong(cursor.getColumnIndex("price")));
			
			album.setLink(cursor.getString(cursor.getColumnIndex("link")));
			album.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
			album.setCoverLargeImg(cursor.getString(cursor.getColumnIndex("cover_large_img")));
			album.setCoverMediumImg(cursor.getString(cursor.getColumnIndex("cover_medium_img")));
			album.setCoverSmallImg(cursor.getString(cursor.getColumnIndex("cover_small_img")));
			
			album.setBrowseCount(cursor.getInt(cursor.getColumnIndex("browse_count")));
			album.setCommentCount(cursor.getInt(cursor.getColumnIndex("comment_count")));
			album.setLikeCount(cursor.getInt(cursor.getColumnIndex("like_count")));
			album.setFavoriteCount(cursor.getInt(cursor.getColumnIndex("favorite_count")));
			
			int isLike = cursor.getInt(cursor.getColumnIndex("is_like")); 
			album.setLike(isLike==1);
			int isFavorite = cursor.getInt(cursor.getColumnIndex("is_favorite")); 
			album.setFavorite(isFavorite==1);
			
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
			
			//加载微信分享内容设定
			String wxShareTitle = (cursor.getString(cursor.getColumnIndex("wx_share_title")));
			String wxShareContent =(cursor.getString(cursor.getColumnIndex("wx_share_content")));
			String wxShareLink =(cursor.getString(cursor.getColumnIndex("wx_share_link")));
			String wxShareIconUrl =(cursor.getString(cursor.getColumnIndex("wx_share_icon_url")));
			
			if(!StringUtils.isBlank(wxShareTitle)&&!StringUtils.isBlank(wxShareContent)&&!StringUtils.isBlank(wxShareLink)&&!StringUtils.isBlank(wxShareIconUrl)){
				GenericSharedInfo genericSharedInfo = new GenericSharedInfo();
				GenericSharedInfo.WxSharedInfo wxSharedInfo = new GenericSharedInfo.WxSharedInfo(wxShareTitle, wxShareContent, wxShareIconUrl, wxShareLink);
				genericSharedInfo.setWxSharedInfo(wxSharedInfo);
				album.setGenericSharedInfo(genericSharedInfo);
			}
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
			
			long sort = System.currentTimeMillis();
			
			for(Album album: albumList){
				ContentValues values = new ContentValues();  
				//数据准备
				values.put("id", album.getId());  
		        values.put("title", album.getTitle());  
		        values.put("remark", album.getRemark());
		        values.put("price", album.getPrice());
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
		        
		        values.put("is_like", album.isLike()?1:0);
		        values.put("is_favorite", album.isFavorite()?1:0);
		        
		        //根据专辑的顺序保存排序值，从大到小（供查询时从大到小desc排序）
		        values.put("sort", sort);
		        sort--;
		        
		        AlbumAuthorInfo authorInfo = album.getAuthorInfo();
		        if(authorInfo!=null){
		        	values.put("designer_avatar", album.getAuthorInfo().getDesignerAvatar());
		        	values.put("designer_nickname", album.getAuthorInfo().getDesignerNickname());
		        	//关注状态
		        	int followStatus = album.getAuthorInfo().isFollowed()?1:0;
		        	values.put("designer_follow_status", followStatus);
		        }
		        
		        //保存分享信息
		        //微信分享
				if(album.getGenericSharedInfo()!=null&&album.getGenericSharedInfo().getWxSharedInfo()!=null){
					 values.put("wx_share_title", album.getGenericSharedInfo().getWxSharedInfo().getTitle());
					 values.put("wx_share_content", album.getGenericSharedInfo().getWxSharedInfo().getContent());
					 values.put("wx_share_icon_url", album.getGenericSharedInfo().getWxSharedInfo().getIconUrl());
					 values.put("wx_share_link", album.getGenericSharedInfo().getWxSharedInfo().getLink());
				}
				//微薄分享
				if(album.getGenericSharedInfo()!=null&&album.getGenericSharedInfo().getWeiboSharedInfo()!=null){
					 values.put("weibo_share_content", album.getGenericSharedInfo().getWeiboSharedInfo().getContent());
					 values.put("weibo_share_icon_url", album.getGenericSharedInfo().getWeiboSharedInfo().getIconUrl());
					 values.put("weibo_share_link", album.getGenericSharedInfo().getWeiboSharedInfo().getLink());
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
	
	
	public static int updateLikeStatus(Context context, int albumId, int likeStatus, int step) {
		updateLikeStatus(context, TB_ALBUM_LATEST, albumId, likeStatus, step);
		updateLikeStatus(context, TB_ALBUM_RECOMMEND, albumId, likeStatus, step);
		updateLikeStatus(context, TB_ALBUM_FOLLOW, albumId, likeStatus, step);
		updateLikeStatus(context, TB_HOT_ALBUM_WEEKLY, albumId, likeStatus, step);
		updateLikeStatus(context, TB_HOT_ALBUM_MONTHLY, albumId, likeStatus, step);
		updateLikeStatus(context, TB_HOT_ALBUM_YEARLY, albumId, likeStatus, step);
		return 1;
	}
	
	/**
	 * 更新是否赞的状态
	 * @param context
	 * @param tableName
	 * @param albumId
	 * @param likeStatus
	 * @param step 修改数量
	 * @return
	 */
	public static int updateLikeStatus(Context context, String tableName, int albumId, int likeStatus, int step) {
		SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
		ContentValues values = new ContentValues();  
		//数据准备
		values.put("is_like", likeStatus);  
		if(step>0){
			return db.update(tableName, values, "id=? and like_count=like_count+?", new String[]{String.valueOf(albumId), String.valueOf(step)});
		}else{
			return db.update(tableName, values, "id=? and like_count=like_count-?", new String[]{String.valueOf(albumId), String.valueOf(0-step)});
		}
	}
	
	
	public static int updateFavoriteStatus(Context context, int albumId, int favoriteStatus, int step) {
		updateFavoriteStatus(context, TB_ALBUM_LATEST, albumId, favoriteStatus, step);
		updateFavoriteStatus(context, TB_ALBUM_RECOMMEND, albumId, favoriteStatus, step);
		updateFavoriteStatus(context, TB_ALBUM_FOLLOW, albumId, favoriteStatus, step);
		updateFavoriteStatus(context, TB_HOT_ALBUM_WEEKLY, albumId, favoriteStatus, step);
		updateFavoriteStatus(context, TB_HOT_ALBUM_MONTHLY, albumId, favoriteStatus, step);
		updateFavoriteStatus(context, TB_HOT_ALBUM_YEARLY, albumId, favoriteStatus, step);
		return 1;
	}
	
	/**
	 * 更新是否收藏的状态
	 * @param context
	 * @param tableName
	 * @param albumId
	 * @param favoriteStatus
	 * @return
	 */
	public static int updateFavoriteStatus(Context context, String tableName, int albumId, int favoriteStatus, int step) {
		SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
		ContentValues values = new ContentValues();  
		//数据准备
		values.put("is_favorite", favoriteStatus);
//		return db.update(tableName, values, "id=?", new String[]{String.valueOf(albumId)});
		if(step>0){
			return db.update(tableName, values, "id=? and favorite_count=favorite_count+?", new String[]{String.valueOf(albumId), String.valueOf(step)});
		}else{
			return db.update(tableName, values, "id=? and favorite_count=like_count-?", new String[]{String.valueOf(albumId), String.valueOf(0-step)});
		}
	}
	
	
	public static int deleteByTab(Context context, int tabIndex){
		switch(tabIndex){
		case 0: return delete(context, TB_ALBUM_LATEST);
		case 1:	return delete(context, TB_ALBUM_RECOMMEND);
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
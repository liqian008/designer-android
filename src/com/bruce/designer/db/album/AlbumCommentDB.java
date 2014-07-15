package com.bruce.designer.db.album;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bruce.designer.db.DBHelper;
import com.bruce.designer.model.Comment;

public class AlbumCommentDB {
	
	/*专辑slide*/
	public static final String TB_COMMENT = "tb_comment";

	public static List<Comment> queryByAlbumId(Context context, Integer albumId) {
		List<Comment> commentList = new ArrayList<Comment>();
		if(albumId!=null){
			// 读取SQLite里面的数据
			SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
			
			// 这里是把SQLite里面的数据进行排序，依据ID由大到小排序，这样可以保证ListView展示在最上面的一条 数据是最新的一条
			Cursor cursor = db.query(TB_COMMENT, null, "album_id=?", new String[]{String.valueOf(albumId)}, null, null, "id asc");
	
			while (cursor.moveToNext()) {
				Comment comment = new Comment();
				comment.setId(cursor.getLong(cursor.getColumnIndex("id")));
				comment.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				comment.setComment(cursor.getString(cursor.getColumnIndex("comment")));
				comment.setAlbumId(cursor.getInt(cursor.getColumnIndex("album_id")));
				comment.setAlbumSlideId(cursor.getInt(cursor.getColumnIndex("album_slide_id")));
				comment.setFromId(cursor.getInt(cursor.getColumnIndex("from_id")));
				comment.setToId(cursor.getInt(cursor.getColumnIndex("to_id")));
				comment.setDesignerId(cursor.getInt(cursor.getColumnIndex("designer_id")));
				comment.setUserHeadImg(cursor.getString(cursor.getColumnIndex("user_head_img")));
				comment.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
				comment.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
				comment.setUpdateTime(cursor.getLong(cursor.getColumnIndex("update_time")));
				commentList.add(comment);
			}
		}
		return commentList;
	}
	
	
	public static int save(Context context, List<Comment> commentList) {
		if(commentList!=null&&commentList.size()>0){
			SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
			
			for(Comment comment: commentList){
				ContentValues values = new ContentValues();  
				//数据准备
				values.put("id", comment.getId());  
		        values.put("title", comment.getTitle());
		        values.put("comment", comment.getComment());
		        values.put("album_id", comment.getAlbumId());
		        values.put("album_slide_id", comment.getAlbumSlideId());
		        values.put("to_id", comment.getToId());
		        values.put("from_id", comment.getFromId());
		        values.put("designer_id", comment.getDesignerId());
		        values.put("nickname", comment.getNickname());
		        values.put("user_head_img", comment.getUserHeadImg());
		        values.put("create_time", comment.getCreateTime());
		        values.put("update_time", comment.getUpdateTime());
			
		        db.insert(TB_COMMENT,  null, values);
			}
			return commentList.size();
		}
		return 0;
	}
	
	public static int deleteByAlbumId(Context context, Integer albumId) {
		if(albumId!=null){
			SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
			int result = db.delete(TB_COMMENT, "album_id=?", new String[]{String.valueOf(albumId)});
			return result;
		}
		return 0;
	}
}
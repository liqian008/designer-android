package com.bruce.designer.listener;

import com.bruce.designer.model.Album;
import com.bruce.designer.model.share.GenericSharedInfo;

public interface IOnAlbumListener {
	
	public static final int HANDLER_FLAG_LIKE_POST_RESULT = 21;
	public static final int HANDLER_FLAG_UNLIKE_POST_RESULT = 22;
	public static final int HANDLER_FLAG_FAVORITE_POST_RESULT = 31;
	public static final int HANDLER_FLAG_UNFAVORITE_POST_RESULT = 32;
	
	public void onLike(int albumId, int designerId, int mode);
	
	public void onFavorite(int albumId, int designerId, int mode);
	
	public void onComment(Album album);
	
	public void onShare(GenericSharedInfo sharedInfo);
}	

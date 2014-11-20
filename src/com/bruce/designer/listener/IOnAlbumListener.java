package com.bruce.designer.listener;

import com.bruce.designer.model.Album;
import com.bruce.designer.model.share.GenericSharedInfo;

public interface IOnAlbumListener {
	
	public static final int HANDLER_FLAG_LIKE_POST = 21;
	public static final int HANDLER_FLAG_UNLIKE_POST = 22;
	public static final int HANDLER_FLAG_FAVORITE_POST = 31;
	public static final int HANDLER_FLAG_UNFAVORITE_POST = 32;
	
	public void onLike(int albumId, int designerId, int mode);
	
	public void onFavorite(int albumId, int designerId, int mode);
	
	public void onComment(Album album);
	
	public void onShare(GenericSharedInfo sharedInfo);
}	

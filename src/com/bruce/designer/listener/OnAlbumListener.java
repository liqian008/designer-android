package com.bruce.designer.listener;

import com.bruce.designer.model.share.GenericSharedInfo;

public interface OnAlbumListener {
	
	
	
	public void onShare(GenericSharedInfo sharedInfo);
	
	public void onLike(int albumId, int designerId, int mode);
	
	public void onFavorite(int albumId, int designerId, int mode);
}	

package com.bruce.designer.listener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.bruce.designer.AppApplication;
import com.bruce.designer.activity.Activity_AlbumInfo;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.album.PostFavoriteApi;
import com.bruce.designer.api.album.PostLikeApi;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.model.share.GenericSharedInfo;
import com.bruce.designer.util.UiUtil;
import com.bruce.designer.view.SharePanelView;

public class OnAlbumListener implements IOnAlbumListener{
	
	private Context context;
	private Handler handler;
	private View mainView;

	public OnAlbumListener(Context context, Handler handler, View mainView){
		this.context = context;
		this.handler = handler;
		this.mainView = mainView;
	}
	
	@Override
	public void onShare(GenericSharedInfo sharedInfo) {
		SharePanelView sharePanel = new SharePanelView(context, sharedInfo);
		sharePanel.show(mainView);
	}

	@Override
	public void onComment(Album album) {
		if (album != null && album.getAuthorInfo() != null) {
			Activity_AlbumInfo.show(context, album, album.getAuthorInfo(), true);
		}
	}

	@Override
	public void onLike(int albumId, int designerId, int mode) {
		if (!AppApplication.isGuest()) {
			postLike(context, handler, albumId, designerId, mode);
		} else {
			UiUtil.showShortToast(context, Config.GUEST_ACCESS_DENIED_TEXT);// 游客无法操作
		}
	}

	@Override
	public void onFavorite(int albumId, int designerId, int mode) {
		if (!AppApplication.isGuest()) {
			postFavorite(context, handler, albumId, designerId, mode);
		} else {
			UiUtil.showShortToast(context, Config.GUEST_ACCESS_DENIED_TEXT);// 游客无法操作
		}
	}
	
	/**
	 * 发起赞
	 */
	public static void postLike(final Context context, final Handler handler, final int albumId, final int designerId, final int mode) {
		//启动线程post数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				PostLikeApi api = new PostLikeApi(albumId, designerId, mode);
				ApiResult apiResult = ApiManager.invoke(context, api);
				if(mode==1){
					message = handler.obtainMessage(HANDLER_FLAG_LIKE_POST_RESULT);
				}else{
					message = handler.obtainMessage(HANDLER_FLAG_UNLIKE_POST_RESULT);
				}
				//api的返回参数中没有albumId，为了更新db所需的album，对apiResult做了特殊加工
				if(apiResult!=null) apiResult.setData(albumId);
				message.obj = apiResult;
//				message.obj = albumId;
				message.sendToTarget();
				
				
//				if(apiResult!=null&&apiResult.getResult()==1){
//					if(mode==1){
//						message = handler.obtainMessage(HANDLER_FLAG_LIKE_POST_RESULT);
//					}else{
//						message = handler.obtainMessage(HANDLER_FLAG_UNLIKE_POST_RESULT);
//					}
//					//api的返回参数中没有albumId，为了更新db，对apiResult做了特殊加工
//					apiResult.setData(albumId);
//					message.obj = apiResult;
////					message.obj = albumId;
//					message.sendToTarget();
//				}
			}
		});
		thread.start();
	}
	
	/**
	 * 发起收藏
	 */
	public static void postFavorite(final Context context, final Handler handler, final int albumId, final int designerId, final int mode) {
		//启动线程post数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				PostFavoriteApi api = new PostFavoriteApi(albumId, designerId, mode);
				ApiResult apiResult = ApiManager.invoke(context, api);
				if(mode==1){
					message = handler.obtainMessage(HANDLER_FLAG_FAVORITE_POST_RESULT);
				}else{
					message = handler.obtainMessage(HANDLER_FLAG_UNFAVORITE_POST_RESULT);
				}
				//api的返回参数中没有albumId，为了更新db所需的album，对apiResult做了特殊加工
				if(apiResult!=null) apiResult.setData(albumId);
				message.obj = apiResult;
//				message.obj = albumId;
				message.sendToTarget();
				
				
				
//				if(apiResult!=null&&apiResult.getResult()==1){
//					if(mode==1){
//						message = handler.obtainMessage(HANDLER_FLAG_FAVORITE_POST_RESULT);
//					}else{
//						message = handler.obtainMessage(HANDLER_FLAG_UNFAVORITE_POST_RESULT);
//					}
//					//api的返回参数中没有albumId，为了更新db，对apiResult做了特殊加工
//					apiResult.setData(albumId);
//					message.obj = apiResult;
////					message.obj = albumId;
//					message.sendToTarget();
//				}
			}
		});
		thread.start();
	}
	
}

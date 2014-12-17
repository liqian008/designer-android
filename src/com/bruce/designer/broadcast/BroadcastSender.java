package com.bruce.designer.broadcast;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;

import com.bruce.designer.constants.ConstantsKey;

public class BroadcastSender {
	
//	/**
//	 * 网络不可用广播
//	 * @param context
//	 */
//	public static void networkUnavailable(Context context) {
//		broadcast(context, ConstantsKey.BROADCAST_ACTION, ConstantsKey.BROADCAST_NETWORK_INVALID);
//	}
//	
//	/**
//	 * 禁止游客进行交互操作(Toast提示，建议用户进行登录)
//	 * @param context
//	 */
//	public static void guestDenied(Context context) {
//		broadcast(context, ConstantsKey.BROADCAST_ACTION, ConstantsKey.BROADCAST_GUEST_DENIED);
//	}
	
	/**
	 * 回退至登录界面
	 * @param context
	 */
	public static void back2Login(Context context) {
		broadcast(context, ConstantsKey.BROADCAST_ACTION, ConstantsKey.BROADCAST_BACK_TO_LOGIN);
	}

	public static void broadcast(Context context, String action, int key) {
		Intent intent = new Intent();
		intent.setAction(action);
		intent.putExtra(ConstantsKey.BUNDLE_BROADCAST_KEY, key);
		//发送广播
		context.sendBroadcast(intent);
	}
	
	
	/**
	 * 广播
	 * @param context
	 * @param action
	 * @param paramMap
	 */
	public static void broadcast(Context context, String action, Map<String, String> paramMap) {
		Intent intent = new Intent();
		intent.setAction(action);
		if(paramMap!=null&&paramMap.size()>0){
			for(Entry<String, String> entry: paramMap.entrySet()){
				intent.putExtra(entry.getKey(), entry.getValue());
			}
		}
		//发送广播
		context.sendBroadcast(intent);
	}
	
	
}

package com.bruce.designer.broadcast;

import com.bruce.designer.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * 通知栏
 * @author liqian
 */
public class NotificationBuilder {
	
	/**
	 * 生成一个notification并立刻消失，通常用于用户操作结果的展示
	 * 
	 * @param context 上下文
	 * @param content 内容
	 */
	@SuppressWarnings("deprecation")
	public static void createNotification(Context context, String content) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, content, System.currentTimeMillis());
		// 定义下拉通知栏时要展现的内容信息
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent notificationIntent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, null, null, contentIntent);
		notificationManager.notify(1, notification);
		notificationManager.cancel(1);
	}
}

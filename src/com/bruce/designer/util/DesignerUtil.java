package com.bruce.designer.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.bruce.designer.AppManager;
import com.bruce.designer.activity.Activity_Login;

public class DesignerUtil {

	/**
	 * 指导用户进行登录
	 * @param context
	 * @param title
	 * @param message
	 * @return
	 */
	public static void guideGuestLogin(final Context context, String title, String message) {
		DialogInterface.OnClickListener onclickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==-2){
					//跳转到登录界面
					AppManager.getInstance().finishAllActivity();
					Intent loginIntent = new Intent(context, Activity_Login.class);
					context.startActivity(loginIntent);
				}else if(which==-1){
					dialog.dismiss();
				}
			}
		};
		UiUtil.showAlertDialog(context, true, null, title, message, "立刻登录", onclickListener, "我知道了", onclickListener).show();
	}
	

}

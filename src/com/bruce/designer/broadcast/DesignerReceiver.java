package com.bruce.designer.broadcast;

import com.bruce.designer.AppManager;
import com.bruce.designer.activity.Activity_Login;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.util.UiUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DesignerReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConstantsKey.BROADCAST_ACTION)) {
			
			int key = intent.getIntExtra(ConstantsKey.BUNDLE_BROADCAST_KEY, 0);
			switch (key) {
			case ConstantsKey.BROADCAST_NETWORK_INVALID://无可用网络
				UiUtil.showShortToast(context, "当前网络不可用");
				break;
			case ConstantsKey.BROADCAST_GUEST_DENIED://游客操作受限
				UiUtil.showShortToast(context, "游客无法进行该操作，请先进行登录");
				break;
			case ConstantsKey.BROADCAST_BACK_TO_LOGIN://需返回至登录界面
				AppManager.getInstance().finishAllActivity();
				Activity_Login.show(context);
				break;
			default:
				break;
			}
		}
	}
}

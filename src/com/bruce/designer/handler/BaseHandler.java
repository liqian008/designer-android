package com.bruce.designer.handler;

import com.bruce.designer.util.UiUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

@Deprecated
public abstract class BaseHandler extends Handler {
	
	private Context context;
	
	protected BaseHandler(Context context) {
		this.context = context;
	}
	
	@Override
	public final void handleMessage(Message msg) {
		if(msg!=null&&msg.what==-1){
			UiUtil.showShortToast(context, "当前网络不可用");
		}else{
			processHandlerMessage(msg);
		}
	}

	protected abstract void processHandlerMessage(Message msg); 
	
}

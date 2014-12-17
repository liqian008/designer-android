package com.bruce.designer.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.bruce.designer.broadcast.BroadcastSender;
import com.bruce.designer.constants.Config;
import com.bruce.designer.exception.ErrorCode;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.UiUtil;

public abstract class DesignerHandler extends Handler {
	
	private Context context;
	
	protected DesignerHandler(Context context) {
		this.context = context;
	}
	
	@Override
	public final void handleMessage(Message msg) {
		ApiResult apiResult = (ApiResult) msg.obj;
		if(apiResult==null){
			UiUtil.showShortToast(context, "服务器正在开小差，请稍后再试");
		}else{
			if(apiResult.getResult()!=1){
				int errorCode = apiResult.getErrorcode();
				if(errorCode==ErrorCode.E_SYS_INVALID_TICKET || errorCode==ErrorCode.E_SYS_INVALID_SIG){//对于通用错误的特殊处理
					BroadcastSender.back2Login(context);
					return;
				}
				if(errorCode==ErrorCode.CLIENT_NETWORK_UNAVAILABLE){//对于通用错误的特殊处理
					UiUtil.showShortToast(context, Config.NETWORK_UNAVAILABLE_TEXT);
					return;
				}
				if(errorCode==ErrorCode.CLIENT_GUEST_ACCESS_DENIED){//对于通用错误的特殊处理
					UiUtil.showShortToast(context, Config.GUEST_ACCESS_DENIED_TEXT);
					return;
				}
			}
			//剩余工作交给子handler处理
			processHandlerMessage(msg);
		}
	}

	protected abstract void processHandlerMessage(Message msg); 
	
}

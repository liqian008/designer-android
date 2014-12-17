package com.bruce.designer.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.bruce.designer.broadcast.BroadcastSender;
import com.bruce.designer.constants.Config;
import com.bruce.designer.exception.ErrorCode;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.DesignerUtil;
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
				if(errorCode==ErrorCode.CLIENT_NETWORK_UNAVAILABLE){//对于无网情况的特殊处理
					UiUtil.showShortToast(context, Config.NETWORK_UNAVAILABLE_TEXT);
					return;
				}
				if(errorCode==ErrorCode.CLIENT_GUEST_ACCESS_DENIED){//对于游客访问权限的特殊处理
					DesignerUtil.guideGuestLogin(context, "提示", "游客无法进行该操作，请先登录");
					return;
				}
				if(errorCode==ErrorCode.E_SYS_ANTISPAM){//敏感词
					UiUtil.showShortToast(context, apiResult.getMessage());
					return;
				}
			}
			//剩余工作交给子handler处理
			processHandlerMessage(msg);
		}
	}

	protected abstract void processHandlerMessage(Message msg); 
	
}

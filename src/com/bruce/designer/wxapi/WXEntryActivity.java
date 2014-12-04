package com.bruce.designer.wxapi;


import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bruce.designer.AppApplication;
import com.bruce.designer.R;
import com.bruce.designer.activity.BaseActivity;
import com.bruce.designer.broadcast.BroadcastSender;
import com.bruce.designer.constants.ConstantsKey;
import com.bruce.designer.util.UiUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler{
	
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api = AppApplication.getWxApi();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //IWXAPI的实例
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		//UiUtil.showShortToast(this, "收到微信请求消息");
		switch (req.getType()) {
//		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//			goToGetMsg();
//			break;
//		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//			goToShowMsg((ShowMessageFromWX.Req) req);
//			break;
		default:
			break;
		}
	}
	

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		//UiUtil.showShortToast(this, "WXEntryActivity收到消息");
		int result = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success; 
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		
		//发起广播，通知activity_login进行微信登录操作
		Map<String, String> broadcastMap = new HashMap<String, String>();
		broadcastMap.put("weixin_oauth_code", "123");
		broadcastMap.put("weixin_oauth_state", "2345");
		BroadcastSender.broadcast(context, ConstantsKey.BROADCAST_ACTION_WEIXIN_LOGIN, broadcastMap);
		
		if(resp instanceof SendMessageToWX.Resp){//如果是分享消息的response
			UiUtil.showShortToast(context, getResources().getString(result));
			finish();
		}
		
		if(resp instanceof SendAuth.Resp){//如果是oauth的response
			UiUtil.showShortToast(this, "WXEntryActivity收到登录消息");
			UiUtil.showShortToast(this, "auth code: "+ ((SendAuth.Resp) resp).code);
			UiUtil.showShortToast(this, "auth state: " + ((SendAuth.Resp) resp).state);
			if(resp.errCode==BaseResp.ErrCode.ERR_OK){
				String code = ((SendAuth.Resp)resp).code;
				String state = ((SendAuth.Resp)resp).state;
				//向服务器提交accessToken
				if(code!=null){
//					//发起广播，通知activity_login进行微信登录操作
//					Map<String, String> broadcastMap = new HashMap<String, String>();
//					broadcastMap.put("weixin_oauth_code", code);
//					broadcastMap.put("weixin_oauth_state", state);
//					BroadcastSender.broadcast(context, ConstantsKey.BROADCAST_ACTION_WEIXIN_LOGIN, broadcastMap);
				}else{
					finish();
				}
			}else{
				UiUtil.showShortToast(context, getResources().getString(result));
				finish();
			}
		}
	}
	
	
	
	private WeixinLoginHandler loginHandler = new WeixinLoginHandler();
	
	class WeixinLoginHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
//			UiUtil.showShortToast(context, "收到登录handler消息", Toast.LENGTH_SHORT).show();
			switch (msg.what) {
			case 1:
				UiUtil.showShortToast(context, "微信登录成功");
//				//UiUtil.showShortToast(context, "微信登录成功", Toast.LENGTH_SHORT).show();
//				//发起广播，以刷新页面
//				Intent intent = new Intent("cn.hlmy.wxgame.broadcast");
//				intent.putExtra("wxgameBroadcast", 100);
//				context.sendBroadcast(intent);
				finish();
				break;
			default:
//				UiUtil.showShortToast(context, "微信登录失败", Toast.LENGTH_SHORT).show();
				UiUtil.showShortToast(context, "微信登录失败");
				finish();
	        	break;
			}
		}
	}
}
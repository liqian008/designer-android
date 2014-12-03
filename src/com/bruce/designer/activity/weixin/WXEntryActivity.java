package com.bruce.designer.activity.weixin;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bruce.designer.AppApplication;
import com.bruce.designer.R;
import com.bruce.designer.activity.BaseActivity;
import com.bruce.designer.api.ApiManager;
import com.bruce.designer.api.account.WeixinLoginApi;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.UiUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler{
	
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = WXEntryActivity.this;
        
        //IWXAPI的实例
        api = AppApplication.getWxApi();
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
		
		if(resp instanceof SendMessageToWX.Resp){//如果是分享消息的response
			UiUtil.showShortToast(context, "分享消息："+getResources().getString(result));
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
					//启线程进行登录
					weixinLogin(code, state);
				}else{
					finish();
				}
			}else{
				UiUtil.showShortToast(context, getResources().getString(result));
				finish();
			}
		}
	}
	
	
	/**
     * 微博登录，根据用户的accessToken换取用户资料
     * @param accessToken
     * @param expiresTime 
     * @param refreshToken 
     * @param thirdpartyType
     */
    private void weixinLogin(final String code, final String state) {
		//启动线程获取用户数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				WeixinLoginApi api = new WeixinLoginApi(code, state);
				ApiResult apiResult = ApiManager.invoke(context, api);
				if(apiResult!=null&&apiResult.getResult()==1){
					Message message = loginHandler.obtainMessage(1);
					message.obj = apiResult.getData();
					message.sendToTarget();
				}else{//数据异常
//					UiUtil.showShortToast(context, "登录失败，请重试");
				}
			}
		}).start();
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
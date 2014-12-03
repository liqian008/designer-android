package com.bruce.designer.util;

import com.bruce.designer.AppApplication;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;

public class WeixinUtil {

	/**
	 * 发起微信登录请求
	 * @param activity
	 */
	public static void weixinLogin() {
		SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "";
		
		IWXAPI api = AppApplication.getWxApi();
		api.sendReq(req);
	}


}

package com.bruce.designer.api;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;

import com.bruce.designer.constants.Config;
import com.bruce.designer.model.UserPassport;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.HttpClientUtil;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.MD5;
import com.bruce.designer.util.MobileUtils;
import com.bruce.designer.util.SharedPreferenceUtil;

public abstract class ApiWrapper {

	/* 加密 5000字 上限 */
	private static final int MD5_STRING_LIMIT = 5000;
	
	/**
	 * 
	 * @param httpMethod
	 * @param requestUri
	 * @return
	 * @throws Exception
	 */
	public static JsonResultBean invoke(Context context, AbstractApi api){
		//检查网络状态
		if(!MobileUtils.isNetworkConnected(context)){//未联网
			//TODO 异常或错误码
//			UiUtil.showShortToast(context, "无可用网络");
		}
		String requestUri = api.getRequestUri();
		Map<String, String> apiParamMap = api.getParamMap();
		RequestMethodEnum requestMethod = api.getRequestMethod();
		
		//构造完整请求参数
		Map<String, String> fullParamMap = buildFullParam(apiParamMap);
		String response = null;
		try {
			if(RequestMethodEnum.GET.equals(requestMethod)){
				response = HttpClientUtil.httpGet(requestUri, fullParamMap);
			}else{
				response = HttpClientUtil.httpPost(requestUri, fullParamMap);
			}
			//对返回结果做通用性检查（主要检查sig参数及session失效），两种情况下都认为失败
			if(response!=null){
				//子类处理逻辑
				return api.processResponse(response);
			}
		} catch (Exception e) {
			//请求系统异常
			e.printStackTrace();
			//TODO 异常或错误码
		}
		return null;
	}

	/**
	 * 构造mcs请求的基本参数
	 * 
	 * @param param
	 */
	private static TreeMap<String, String> buildFullParam(Map<String, String> param) {
		TreeMap<String, String> fullParamMap = new TreeMap<String, String>();
		if (param!= null&&param.size()>0) {
			fullParamMap.putAll(param);
		}
		String appVersion = Config.APP_VERSION;
		String appId = Config.APP_ID;
		String secretKey = Config.APP_SECRET_KEY;
		
		fullParamMap.put("app_id", appId);
		fullParamMap.put("v", appVersion);
		fullParamMap.put("call_id", Long.toString(System.currentTimeMillis()));
		//构造用户级的请求参数
		UserPassport userPassport = SharedPreferenceUtil.readObjectFromSp(
				UserPassport.class, Config.SP_CONFIG_ACCOUNT,
				Config.SP_KEY_USERPASSPORT);
		if(userPassport!=null&&userPassport.getTicket()!=null){
			String ticket= null;
			ticket= userPassport.getTicket();
			fullParamMap.put("t", ticket);
			//TODO 使用用户的密钥进行加密，此处暂时屏蔽，使用统一的secretKey进行加密
			//secretKey = userPassport.getSecretKey();
		}
		
		final StringBuilder sb = new StringBuilder("");
		for (Map.Entry<String, String> entry : fullParamMap.entrySet()) {
			sb.append(entry.getKey()).append('=').append(entry.getValue());
		}

		String value = limitedString(sb.toString(), MD5_STRING_LIMIT) + secretKey;
		final String sig = MD5.toMD5(value);
		LogUtil.d("======value: " + value);
		LogUtil.d("======secretKey: " + secretKey);
		LogUtil.d("======sig: " + sig);
		fullParamMap.put("sig", sig);
		
		return fullParamMap;
	}

	/**
	 * 限制加密字符串长度
	 * 
	 * @param input
	 * @param maxLength
	 * @return
	 */
	private static String limitedString(String input, int maxLength) {
		if (input == null) {
			return "";
		}
		if (input.length() > maxLength) {
			return input.substring(0, maxLength);
		}
		return input;
	}
}

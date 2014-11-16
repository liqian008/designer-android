package com.bruce.designer.api;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;

import com.bruce.designer.AppApplication;
import com.bruce.designer.broadcast.BroadcastSender;
import com.bruce.designer.constants.Config;
import com.bruce.designer.exception.DesignerException;
import com.bruce.designer.exception.ErrorCode;
import com.bruce.designer.model.UserPassport;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.HttpClientUtil;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.MD5;
import com.bruce.designer.util.MobileUtils;

public class ApiManager {

	/* 加密 5000字 上限 */
	private static final int MD5_STRING_LIMIT = 5000;
	/* 错误的响应 */
	private static ApiResult errorResult = new ApiResult(0, null, 0, "操作失败");
	
	/**
	 * 
	 * @param httpMethod
	 * @param requestUri
	 * @return
	 * @throws Exception
	 */
	public static ApiResult invoke(Context context, AbstractApi api){
		//检查网络状态
		if(!MobileUtils.isNetworkConnected(context)){//未联网
			//无网情况下的处理
			BroadcastSender.networkUnavailable(context);
			return errorResult;
		}
		
		if(api.needAuth()&&AppApplication.isGuest()){
			//游客的操作检查
			BroadcastSender.guestDenied(context);
			return errorResult;
		}
		
		
		try {
			String requestUri = api.getRequestUri();
			String response = null;
			Map<String, String> apiParamMap = api.getParamMap();
			RequestMethodEnum requestMethod = api.getRequestMethod();
			//构造完整请求参数
			Map<String, String> fullParamMap = buildFullParam(apiParamMap);

			//判断是否是multipart请求
			if(!api.isMultipart()){
				
				if(RequestMethodEnum.GET.equals(requestMethod)){
					response = HttpClientUtil.httpGet(requestUri, fullParamMap);
				}else{
					response = HttpClientUtil.httpPost(requestUri, fullParamMap);
				}
			}else{
				String imagePath = api.getFilePath();
				response = HttpClientUtil.httpPostImage(requestUri, fullParamMap, imagePath, null);
			}
			//对返回结果做通用性检查（主要检查sig参数及session失效），两种情况下都认为失败
			if(response!=null){
				//子类处理逻辑
				return api.processResponse(response);
			}
		} catch (DesignerException e) {
			if(e.getErrorCode()==ErrorCode.SYSTEM_MISSING_PARAM){//签名错误，需要重新登录
				BroadcastSender.back2Login(context);
			}
		} catch (Exception e) {
			//请求系统异常
			e.printStackTrace();
			//TODO 异常或错误码
		}
		//默认返回错误响应
		return errorResult;
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
		String appVersionName = Config.APP_VERSION_NAME;
		int appVersionCode = Config.APP_VERSION_CODE;
		String appId = Config.APP_ID;
		String secretKey = Config.APP_SECRET_KEY;
		
		fullParamMap.put("app_id", appId);
		fullParamMap.put("v_name", appVersionName);
		fullParamMap.put("v_code", String.valueOf(appVersionCode));
		fullParamMap.put("call_id", Long.toString(System.currentTimeMillis()));
		//构造用户级的请求参数
//		UserPassport userPassport = SharedPreferenceUtil.readObjectFromSp(
//				UserPassport.class, Config.SP_CONFIG_ACCOUNT,
//				Config.SP_KEY_USERPASSPORT);
		UserPassport userPassport = AppApplication.getUserPassport();
		
		if(userPassport!=null&&userPassport.getTicket()!=null){
			String ticket= null;
			ticket= userPassport.getTicket();
			fullParamMap.put("t", ticket);
			//TODO 使用返回UserPassport中secret进行加密，使用统一的secretKey进行加密
//			secretKey = userPassport.getUserSecretKey();
		}
		
		final StringBuilder sb = new StringBuilder("");
		for (Map.Entry<String, String> entry : fullParamMap.entrySet()) {
			sb.append(entry.getKey()).append('=').append(entry.getValue());
		}

		String value = limitedString(sb.toString(), MD5_STRING_LIMIT) + secretKey;
		LogUtil.d("======value: " + value);
		final String sig = MD5.toMD5(value);
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

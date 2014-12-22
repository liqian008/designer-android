package com.bruce.designer.api.system;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.User;
import com.bruce.designer.model.VersionCheckResult;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.model.share.GenericSharedInfo;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;
import com.bruce.designer.util.StringUtils;

/**
 * 系统检查API，在唯一入口splash页面进行调用，主要做两件事情
 * 1、检查客户端版本更新并返回
 * 2、返回用户信息（如果客户端需要强制更新，则不返回本段内容）
 * @author liqian
 */
public class SystemCheckApi extends AbstractApi {
	
	private Map<String, String> paramMap = null;
	
	public SystemCheckApi(){
		super();
		paramMap = new HashMap<String, String>();
//		apiManager中自动构建，子api无需构造以下参数
//		paramMap.put("clientType", String.valueOf(Config.CLIENT_TYPE));
//		paramMap.put("versionCode", String.valueOf(AppApplication.getVersionCode()));
//		paramMap.put("channel", String.valueOf(AppApplication.getChannel()));
	}
	
	@Override
	protected ApiResult processApiResult(int result, int errorcode, String message, String dataStr) {
		if(result==1){
		JSONObject jsonData;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			jsonData = new JSONObject(dataStr);
			//版本检查的node
			String verionResultStr = jsonData.optString("versionCheckResult");
			VersionCheckResult versionCheckResult = JsonUtil.gson.fromJson(verionResultStr, VersionCheckResult.class);
			dataMap.put("versionCheckResult", versionCheckResult);
			
			//用户资料的node
			boolean needLogin = jsonData.optBoolean("needLogin", true);
			dataMap.put("needLogin", needLogin);
			//微信公众帐号的二维码
			String wxmpQrcodeUrl = jsonData.optString("wxmpQrcodeUrl", "");
			dataMap.put("wxmpQrcodeUrl", wxmpQrcodeUrl);
			
			//分享对象
			String appSharedInfoStr = jsonData.optString("appSharedInfo", "");
			try{
				GenericSharedInfo appSharedInfo = JsonUtil.gson.fromJson(appSharedInfoStr, GenericSharedInfo.class);
				dataMap.put("appSharedInfo", appSharedInfo);
			}catch(Exception e){
			}
			
			boolean showPrice= jsonData.optBoolean("showPrice", false);
			dataMap.put("showPrice", showPrice);
			
			//登录用户的个人资料
			String hostUserStr = jsonData.optString("hostUser");
//			LogUtil.d("hostUserStr: "+hostUserStr);
			if(!StringUtils.isBlank(hostUserStr)){
				User hostUser = null;
				try{
					hostUser = JsonUtil.gson.fromJson(hostUserStr, User.class);
				}catch(Exception e){
				}
				if(hostUser!=null){
					dataMap.put("hostUser", hostUser);
				}
			}
			return ResponseBuilderUtil.buildSuccessResult(dataMap);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}

	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
		if(paramMap!=null){
			dataMap.putAll(paramMap);
		}
	}

	@Override
	protected String getApiMethodName() {
		return "systemCheck.cmd";
	}

	/**
	 * 此api是否需要登录用户才能操作
	 * @return
	 */
	protected boolean needAuth(){
		return false;
	}
}

package com.bruce.designer.api.system;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.AppApplication;
import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.VersionCheckResult;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.ApplicationUtil;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;

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
		paramMap.put("clientType", String.valueOf(Config.CLIENT_TYPE));
		paramMap.put("versionCode", String.valueOf(AppApplication.getVersionCode()));
		paramMap.put("channel", String.valueOf(AppApplication.getChannel()));
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

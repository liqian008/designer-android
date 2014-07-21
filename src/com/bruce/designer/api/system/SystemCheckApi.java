package com.bruce.designer.api.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.UserFan;
import com.bruce.designer.model.VersionCheckResult;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;
import com.google.gson.reflect.TypeToken;

/**
 * 系统检查API，在唯一入口splash页面进行调用，主要做两件事情
 * 1、检查客户端版本更新并返回
 * 2、返回用户信息（如果客户端需要强制更新，则不返回本段内容）
 * @author liqian
 */
public class SystemCheckApi extends AbstractApi {
	
	public SystemCheckApi(){
		super();
	}
	
	@Override
	protected ApiResult processResultData(String dataStr) {
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
		return ResponseBuilderUtil.buildErrorResult(0);
	}

	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
		//do nothing
	}

	@Override
	protected String getApiMethodName() {
		return "systemCheck.cmd";
	}

}

package com.bruce.designer.api.account;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.RequestMethodEnum;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.UserPassport;
import com.bruce.designer.model.VersionCheckResult;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;

/**
 * weibo登录
 * @author liqian
 *
 */
public class TestLoginApi extends AbstractApi{
	
	
	public TestLoginApi(){
		super();
	}
	
	@Override
	protected String getApiMethodName() {
		return "testLogin.cmd";
	}
	
	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
	}

	@Override
	protected ApiResult processResultData(String dataStr) {
		JSONObject jsonData;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			jsonData = new JSONObject(dataStr);
			String userPassportStr = jsonData.optString("userPassport");
			UserPassport userPassport = JsonUtil.gson.fromJson(userPassportStr, UserPassport.class);
			dataMap.put("userPassport", userPassport);
			return ResponseBuilderUtil.buildSuccessResult(dataMap);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}
	

}

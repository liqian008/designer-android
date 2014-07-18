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
import com.bruce.designer.util.JsonUtil;

/**
 * weibo登录
 * @author liqian
 *
 */
public class TestLoginApi extends AbstractApi{
	
	private String REQUESTS_URI= Config.JINWAN_API_PREFIX;
	
	
	private Map<String, String> paramMap = null;
	
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
	protected Map<String, Object> processResultData(String dataStr) {
		JSONObject jsonData;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			jsonData = new JSONObject(dataStr);
			String userPassportStr = jsonData.optString("userPassport");
			UserPassport userPassport = JsonUtil.gson.fromJson(userPassportStr, UserPassport.class);
			dataMap.put("userPassport", userPassport);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataMap;
	}

	

}

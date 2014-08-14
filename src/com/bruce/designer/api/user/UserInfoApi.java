package com.bruce.designer.api.user;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.User;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;

public class UserInfoApi extends AbstractApi{
	
	private Map<String, String> paramMap = null;
	
	public UserInfoApi(int userId){
		paramMap = new TreeMap<String, String>();
		paramMap.put("userId", String.valueOf(userId));
	}
	
	@Override
	protected ApiResult processResultData(String dataStr) {
		JSONObject jsonData;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			jsonData = new JSONObject(dataStr);
			int followsCount = jsonData.optInt("followsCount");
			int fansCount = jsonData.optInt("fansCount");
			boolean hasFollowed = jsonData.optBoolean("hasFollowed");
			String userinfoStr = jsonData.getString("userinfo");
			
			User userinfo = JsonUtil.gson.fromJson(userinfoStr, User.class);
			if(userinfo!=null){
				dataMap.put("userinfo", userinfo);
				dataMap.put("followsCount", followsCount);
				dataMap.put("fansCount", fansCount);
				dataMap.put("hasFollowed", hasFollowed);
				return ResponseBuilderUtil.buildSuccessResult(dataMap);
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
		return "userInfo.cmd";
	}
}

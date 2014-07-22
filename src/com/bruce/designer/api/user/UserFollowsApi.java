package com.bruce.designer.api.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.UserFollow;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;
import com.google.gson.reflect.TypeToken;

public class UserFollowsApi extends AbstractApi {

private Map<String, String> paramMap = null;
	
	public UserFollowsApi(int userId){
		paramMap = new TreeMap<String, String>();
		paramMap.put("userId", String.valueOf(userId));
	}
	
	@Override
	protected ApiResult processResultData(String dataStr) {
		JSONObject jsonData;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			jsonData = new JSONObject(dataStr);
			String followListStr = jsonData.getString("followList");
			List<UserFollow> followList = JsonUtil.gson.fromJson(followListStr, new TypeToken<List<UserFollow>>() {}.getType());
			if (followList != null) {
				dataMap.put("followList", followList);
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
		return "userFollows.cmd";
	}
}

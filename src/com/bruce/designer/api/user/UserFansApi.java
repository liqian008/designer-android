package com.bruce.designer.api.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.UserFan;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;
import com.google.gson.reflect.TypeToken;

public class UserFansApi extends AbstractApi {

private Map<String, String> paramMap = null;
	
	public UserFansApi(int userId){
		paramMap = new TreeMap<String, String>();
		paramMap.put("userId", String.valueOf(userId));
	}

	@Override
	protected ApiResult processResultData(String dataStr) {
		JSONObject jsonData;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			jsonData = new JSONObject(dataStr);
			String fanListStr = jsonData.getString("fanList");
			if(fanListStr!=null){
				List<UserFan> fanList = JsonUtil.gson.fromJson(fanListStr, new TypeToken<List<UserFan>>() {}.getType());
				if (fanList != null) {
					dataMap.put("fanList", fanList);
					return ResponseBuilderUtil.buildSuccessResult(dataMap);
				}
			}
		}catch (JSONException e) {
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
		return "userFans.cmd";
	}

}

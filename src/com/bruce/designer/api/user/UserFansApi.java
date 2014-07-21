package com.bruce.designer.api.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.RequestMethodEnum;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.UserFan;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;
import com.google.gson.reflect.TypeToken;

public class UserFansApi extends AbstractApi {

	private String REQUESTS_URI = null;


	private Map<String, String> paramMap = null;

	public UserFansApi(int userId) {
		REQUESTS_URI = Config.JINWAN_API_PREFIX + "/" + userId + "/fans.json";
	}

//	@Override
//	public Map<String, String> getParamMap() {
//		return paramMap;
//	}


	@Override
	public String getRequestUri() {
		return REQUESTS_URI;
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
	}

	@Override
	protected String getApiMethodName() {
		// TODO Auto-generated method stub
		return null;
	}

}

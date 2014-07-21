package com.bruce.designer.api.account;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.RequestMethodEnum;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.User;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;

/**
 * 注册或绑定
 * @author liqian
 *
 */
public class RegisterLoginApi extends AbstractApi{

	private String REQUESTS_URI= Config.JINWAN_API_PREFIX+"/register.json";
	
	private Map<String, String> paramMap = null;
	
	public RegisterLoginApi(String uid, String accessToken, int thirdpartyType){
		paramMap = new TreeMap<String, String>();
		paramMap.put("uid", String.valueOf(uid));
		paramMap.put("accessToken", accessToken);
		paramMap.put("thirdpartyType", String.valueOf(thirdpartyType));
	}
	
	@Override
	public void fillDataMap(Map<String, String> dataMap){
	}


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
			int followsCount = jsonData.getInt("followsCount");
			int fansCount = jsonData.getInt("fansCount");
			
			String userinfoStr = jsonData.getString("userinfo");
			User userinfo = JsonUtil.gson.fromJson(userinfoStr, User.class);
			if(userinfo!=null){
				dataMap.put("userinfo", userinfo);
				dataMap.put("followsCount", followsCount);
				dataMap.put("fansCount", fansCount);
				return ResponseBuilderUtil.buildSuccessResult(dataMap);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}

	@Override
	protected String getApiMethodName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

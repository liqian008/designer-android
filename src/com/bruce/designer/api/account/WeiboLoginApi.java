package com.bruce.designer.api.account;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.RequestMethodEnum;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.ResponseBuilderUtil;

/**
 * weibo登录
 * @author liqian
 *
 */
public class WeiboLoginApi extends AbstractApi{
	
	private String REQUESTS_URI= Config.JINWAN_API_PREFIX;
	
	
	private Map<String, String> paramMap = null;
	
	public WeiboLoginApi(String wbUid, String accessToken){
		paramMap = new TreeMap<String, String>();
		paramMap.put("wbUid", String.valueOf(wbUid));
		paramMap.put("wbAccessToken", accessToken);
	}
	
	@Override
	protected String getApiMethodName() {
		return "wbLogin.cmd";
	}
	
	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
		if(paramMap!=null){
			dataMap.putAll(paramMap);
		}
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
//			int followsCount = jsonData.getInt("followsCount");
//			int fansCount = jsonData.getInt("fansCount");
//			
//			String userinfoStr = jsonData.getString("userinfo");
//			User userinfo = JsonUtil.gson.fromJson(userinfoStr, User.class);
//			if(userinfo!=null){
//				dataMap.put("userinfo", userinfo);
//				dataMap.put("followsCount", followsCount);
//				dataMap.put("fansCount", fansCount);
//			}
			return ResponseBuilderUtil.buildSuccessResult(dataMap);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}

	

}

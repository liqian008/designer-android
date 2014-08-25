package com.bruce.designer.api.account;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.UserPassport;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;

/**
 * 游客登录
 * 
 * @author liqian
 * 
 */
public class GuestLoginApi extends AbstractApi {

	public GuestLoginApi() {
		super();
	}

	@Override
	protected String getApiMethodName() {
		return "guestLogin.cmd";
	}
	
	/**
	 * 此api是否需要登录用户才能操作
	 * @return
	 */
	protected boolean needAuth(){
		return false;
	}

	@Override
	protected void fillDataMap(Map<String, String> dataMap) {
	}

	@Override
	protected ApiResult processApiResult(int result, int errorcode,
			String message, String dataStr) {
		if (result == 1) {
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
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}

}

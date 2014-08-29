package com.bruce.designer.api.account;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.model.User;
import com.bruce.designer.model.UserPassport;
import com.bruce.designer.model.result.ApiResult;
import com.bruce.designer.util.JsonUtil;
import com.bruce.designer.util.ResponseBuilderUtil;

/**
 * weibo登录
 * 
 * @author liqian
 * 
 */
public class WeiboLoginApi extends AbstractApi {

	private Map<String, String> paramMap = null;

	public WeiboLoginApi(String wbUid, String accessToken, String refreshToken,
			long expiresTime) {
		paramMap = new TreeMap<String, String>();
		paramMap.put("weiboUid", String.valueOf(wbUid));
		paramMap.put("weiboAccessToken", accessToken);
		paramMap.put("weiboRefreshToken", refreshToken);
		paramMap.put("weiboExpireIn", String.valueOf(expiresTime));
	}

	@Override
	protected String getApiMethodName() {
		return "weiboLogin.cmd";
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
		if (paramMap != null) {
			dataMap.putAll(paramMap);
		}
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
				UserPassport userPassport = JsonUtil.gson.fromJson(
						userPassportStr, UserPassport.class);

				String hostUserStr = jsonData.optString("hostUser");
				User hostUser = JsonUtil.gson.fromJson(hostUserStr, User.class);
				if (userPassport != null && hostUser != null) {
					dataMap.put("userPassport", userPassport);
					dataMap.put("hostUser", hostUser);
					return ResponseBuilderUtil.buildSuccessResult(dataMap);
				}else{//没有用户登录信息
					boolean needBind = jsonData.optBoolean("needBind");
					if (needBind) {
						dataMap.put("needBind", needBind);
						dataMap.put("thirdpartyUname",  jsonData.optString("thirdpartyUname"));
						dataMap.put("thirdpartyAvatar",  jsonData.optString("thirdpartyAvatar"));
						return ResponseBuilderUtil.buildSuccessResult(dataMap);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return ResponseBuilderUtil.buildErrorResult(0);
	}

}
